package app.io;

import app.util.ImagePathList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

public class ImageHandler implements AutoCloseable, Closeable {
    private int mImageSize = -1;
    private final ImagePathList mFilesToDelete = new ImagePathList(),
            mFilesToKeep = new ImagePathList();
    private Iterator<Path> mFilePathsIterator;
    private Path mRootPath, mLoadedImagePath;
    private volatile Icon mLoadedImage;
    private volatile boolean mFinishedLoading = true, mClosed = false;

    public record ImageData(Icon image, Path path) {
    }

    private void preloadImage() {
        if(!mFinishedLoading)
            return;
        mFinishedLoading = false;
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            IO.println("[DEBUG] Loading and scaling started at "+startTime);

            BufferedImage image = getImage();
            mLoadedImage = scaleImage(image);

            long endTime = System.currentTimeMillis();
            long delta = endTime - startTime;
            IO.println("[DEBUG] Image is loaded and scaled. (took "+delta+" millis)");
            mFinishedLoading = true;
        }).start();
    }

    private BufferedImage getImage() {
        if(mFilePathsIterator ==null||!mFilePathsIterator.hasNext()) {
            IO.println("[DEBUG] No more images.");
            return null;
        }
        BufferedImage out;
        try {
            do {
                mLoadedImagePath = mFilePathsIterator.next();
                IO.println("[DEBUG] Loading file " + mLoadedImagePath.toAbsolutePath());
                out = ImageIO.read(mLoadedImagePath.toUri().toURL());
            } while(out == null);
            return out;
        } catch (Exception e) {
            System.err.println("\u001B[31m[ERROR] Something went wrong while trying to load image. "+e.getMessage()+"\u001B[0m");
            return null;
        }
    }

    private Icon scaleImage(BufferedImage image) {
        if(image==null)
            return null;
        if(mImageSize != -1) {
            int width = image.getWidth(), height = image.getHeight();
            Image scaledImage = width > height ?
                    scaleImageByWidth (image, mImageSize) :
                    scaleImageByHeight(image, mImageSize) ;
            return new ImageIcon(scaledImage);
        } else {
            return new ImageIcon(image);
        }
    }

    private Image scaleImageByHeight(BufferedImage image, int height) {
        int width = (height * image.getWidth()) / image.getHeight();
        return image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
    }

    private Image scaleImageByWidth(BufferedImage image, int width) {
        int height = (width * image.getHeight()) / image.getWidth();
        return image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
    }

    public ImageData nextImageData() {
        if(mClosed)
            throw new IllegalStateException("ImageHandler is closed.");
        if(mFilePathsIterator ==null)
            throw new IllegalStateException("No paths are loaded.");
        try {
            IO.println("[DEBUG] Returning pre-loaded image.");
            long startTime = System.currentTimeMillis();
            final long MAX_LOAD_TIME = 5_000;
            while(true) {
                if(System.currentTimeMillis()-startTime > MAX_LOAD_TIME) {
                    return null;
                }
                if(mFinishedLoading)
                    return new ImageData(mLoadedImage, mLoadedImagePath);
            }
        } catch (Exception e) {
            IO.println("[ERROR] Error while loading image "+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            IO.println("[DEBUG] Loading next image...");
            preloadImage();
        }
    }

    public void keep(Path path) {
        if(mClosed)
            throw new IllegalStateException("ImageHandler is closed.");
        if(mLoadedImagePath==null)
            throw new IllegalStateException("No image is selected");
        mFilesToKeep.add(path);
    }

    public void delete(Path path) {
        if(mClosed)
            throw new IllegalStateException("ImageHandler is closed.");
        if(mLoadedImagePath==null)
            throw new IllegalStateException("No image is selected");
        mFilesToDelete.add(path);
    }

    @Override
    public void close() throws IOException {
        if(mRootPath==null)
            throw new IllegalStateException("No root path set");
        if(mClosed)
            throw new IllegalStateException("ImageHandler is already closed.");
        Path trashFolder = Paths.get(mRootPath.toAbsolutePath().toString(), "__trash__");
        if(!Files.exists(trashFolder)) {
            Files.createDirectory(trashFolder);
        }
        if(!Files.isDirectory(trashFolder)) {
            Files.delete(trashFolder);
            Files.createDirectory(trashFolder);
        }
        for (Path path: mFilesToDelete) {
            IO.println("Moving: "+ path.toAbsolutePath() +" -> "+ trashFolder.toAbsolutePath());
            Files.move(path, Paths.get(trashFolder.toAbsolutePath().toString(),
                    path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        }

        Path keepFolder = Paths.get(mRootPath.toAbsolutePath().toString(), "__keep__");
        if(!Files.exists(keepFolder))
            Files.createDirectory(keepFolder);
        if(!Files.isDirectory(keepFolder)) {
            Files.delete(keepFolder);
            Files.createDirectory(keepFolder);
        }
        for (Path path : mFilesToKeep) {
            IO.println("Moving: "+path.toAbsolutePath()+" -> "+keepFolder.toAbsolutePath());
            Files.move(path, Paths.get(keepFolder.toAbsolutePath().toString(),
                    path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        }

        mClosed = true;
    }

    public ImageHandler setImagesWidth(int width) {
        mImageSize = width;
        return this;
    }

    public ImageHandler setRootPath(Path root) {
        mRootPath = root;
        return this;
    }

    public ImageHandler setIterator(Iterator<Path> iterator) {
        mFilePathsIterator = iterator;
        preloadImage();
        return this;
    }
}
