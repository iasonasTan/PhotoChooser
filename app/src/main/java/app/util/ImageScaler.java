package app.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageScaler {
    private int mMaxImageSize;

    public ImageScaler() {
        this(-1);
    }

    public ImageScaler(int maxSize) {
        mMaxImageSize = maxSize;
    }

    public Icon scaleImage(BufferedImage image) {
        if (image == null) return null;
        if (mMaxImageSize != -1) {
            int width = image.getWidth(), height = image.getHeight();
            Image scaledImage = width > height ?
                    scaleImageByWidth(image, mMaxImageSize) :
                    scaleImageByHeight(image, mMaxImageSize);
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

    public void setImageSize(int size) {
        mMaxImageSize = size;
    }
}
