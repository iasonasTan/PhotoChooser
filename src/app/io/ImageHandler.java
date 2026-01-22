package app.io;

import app.util.ImagePathList;
import app.util.ImageScaler;
import lib.gui.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ImageHandler implements AutoCloseable, Closeable {
    public static ImageHandler newInstance(Path rootPath, java.util.List<Path> paths) {
        return new ImageHandler(rootPath, paths, -1);
    }

    public static ImageHandler newInstance(Path rootPath, java.util.List<Path> paths, int imageWidth) {
        return new ImageHandler(rootPath, paths, imageWidth);
    }

    private final ImagePathList mFilesToDelete = new ImagePathList();
    private final ImagePathList mFilesToKeep   = new ImagePathList();
    private final ImageScaler mImageScaler     = new ImageScaler();
    private final Iterator<Path> mFilePathsIterator;

    private Path mLastReturnedImagePath;
    private final Path mRootPath;
    private boolean mClosed = false;
    private int mRemainingPictures;

    private volatile Path mLoadedImagePath;
    private volatile Icon mLoadedImage;
    private volatile boolean mFinishedLoading = true;

    private ImageHandler(Path root, List<Path> images, int imageWidth) {
        IO.println("Working with images at "+ root.toAbsolutePath());
        mImageScaler.setImageSize(imageWidth);
        mRootPath = root;
        mFilePathsIterator = images.iterator();
        mRemainingPictures = images.size();
        preloadImage();
    }

    public ImageData nextImageData() {
        try {
            IO.println("[DEBUG] Returning pre-loaded image.");
            long startTime = System.currentTimeMillis();
            final long MAX_LOAD_TIME = 5_000;
            while(true) {
                if(System.currentTimeMillis()-startTime > MAX_LOAD_TIME) {
                    return null;
                }
                if(mFinishedLoading) {
                    if(mLoadedImage == null)
                        return null;
                    mLastReturnedImagePath = mLoadedImagePath;
                    return new ImageData(mLoadedImage, mLoadedImagePath);
                }
            }
        } catch (Exception e) {
            IO.println("[ERROR] Error while loading image "+e.getMessage());
            throw new RuntimeException(e);
        } finally {
            IO.println("[DEBUG] Loading next image...");
            preloadImage();
        }
    }

    @Override
    public void close() throws IOException {
        if(mRootPath==null)
            throw new IllegalStateException("No root path set");
        if(mClosed)
            throw new IllegalStateException("ImageHandler is already closed.");
        mClosed = true;

        new FileMover(mRootPath, FileMover.MOVE_TRASH)
                .moveFiles(mFilesToDelete);
        new FileMover(mRootPath, FileMover.MOVE_KEEP)
                .moveFiles(mFilesToKeep);
    }

    private BufferedImage nextImage() {
        if(mFilePathsIterator ==null||!mFilePathsIterator.hasNext()) {
            IO.println("[DEBUG] No more images.");
            return null;
        }
        mRemainingPictures--;
//        if(mRemainingPictures < 1) {
//            try {
//                close();
//            } catch (IOException e) {
//                // ignore
//            }
//            AbstractScreen.dispose();
//            System.exit(1);
//        }
        try {
            BufferedImage out;
            do {
                mLoadedImagePath = mFilePathsIterator.next();
                IO.println("[DEBUG] Loading file " + mLoadedImagePath.toAbsolutePath());
                out = ImageIO.read(mLoadedImagePath.toUri().toURL());
            } while(out == null);
            return out;
        } catch (IOException | NoSuchElementException e) {
            System.err.println("\u001B[31m[ERROR] Something went wrong while trying to load image. "+e.getMessage()+"\u001B[0m");
            UI.showException(e);
            throw new RuntimeException(e);
        }
    }

    private void preloadImage() {
        if(!mFinishedLoading) return;
        mFinishedLoading = false;
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            IO.println("[DEBUG] Loading and scaling started at "+startTime);

            BufferedImage image = nextImage();
            mLoadedImage = mImageScaler.scaleImage(image);

            long endTime = System.currentTimeMillis();
            long delta = endTime - startTime;
            IO.println("[DEBUG] Image is loaded and scaled. (took "+delta+" millis)");
            mFinishedLoading = true;
        }).start();
    }

    private void check() {
        if(mClosed)
            throw new IllegalStateException("ImageHandler is closed.");
        if(mLoadedImagePath==null)
            throw new IllegalStateException("No image is selected");
    }

    public void keep() {
        check();
        mFilesToKeep.add(mLastReturnedImagePath);
    }

    public void delete() {
        check();
        mFilesToDelete.add(mLastReturnedImagePath);
    }

    public int getRemaining() {
        return mRemainingPictures;
    }

    public record ImageData(Icon image, Path path) {
    }
}
