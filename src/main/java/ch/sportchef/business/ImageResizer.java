package ch.sportchef.business;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResizer {

    public static BufferedImage resizeAndCrop(final BufferedImage inputImage,
                                              final int outputWidth, final int outputHeight) {

        final double outputAspectRatio = (outputWidth * 1.0) / (outputHeight * 1.0);

        final int inputWidth = inputImage.getWidth();
        final int inputHeight = inputImage.getHeight();

        if (inputWidth == outputWidth && inputHeight == outputHeight) {
            return inputImage;
        }

        BufferedImage resizedImage = null;
        if (inputWidth != outputWidth && inputHeight != outputHeight) {
            final double inputAspectRatio = (inputWidth * 1.0) / (inputHeight * 1.0);

            int scaleWidth = 0;
            int scaleHeight = 0;
            if (outputAspectRatio < inputAspectRatio) {
                scaleWidth = outputWidth;
                scaleHeight = inputHeight * outputWidth / inputWidth;
            } else {
                scaleWidth = inputWidth * outputHeight / inputHeight;
                scaleHeight = outputHeight;
            }

            resizedImage = resize(inputImage, scaleWidth, scaleHeight);
        } else {
            resizedImage = inputImage;
        }

        final BufferedImage croppedImage = crop(resizedImage, outputWidth, outputHeight);

        return croppedImage;
    }

    public static BufferedImage resize(final BufferedImage inputImage,
                                       final int outputWidth, final int outputHeight) {
        final BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, inputImage.getType());

        final Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, outputWidth, outputHeight, null);
        g2d.dispose();

        return outputImage;
    }

    public static BufferedImage crop(final BufferedImage inputImage,
                                     final int outputWidth, final int outputHeight) {
        final int inputWidth = inputImage.getWidth();
        final int inputHeight = inputImage.getHeight();

        final int startX = inputWidth > outputWidth ? (inputWidth - outputWidth) / 2 : 0;
        final int startY = inputHeight > outputHeight ? (inputHeight - outputHeight) / 2 : 0;

        return crop(inputImage, startX, startY, outputWidth, outputHeight);
    }

    public static BufferedImage crop(final BufferedImage inputImage,
                                     final int startX, final int startY,
                                     final int outputWidth, final int outputHeight) {
        return inputImage.getSubimage(startX, startY, outputWidth, outputHeight);
    }

}
