/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public enum ImageResizer {
    ;

    public static BufferedImage resizeAndCrop(final BufferedImage inputImage,
                                              final int outputWidth, final int outputHeight) {

        final double outputAspectRatio = outputWidth * 1.0 / (outputHeight * 1.0);

        final int inputWidth = inputImage.getWidth();
        final int inputHeight = inputImage.getHeight();

        final BufferedImage outputImage;

        if (inputWidth == outputWidth && inputHeight == outputHeight) {
            outputImage = inputImage;
        } else {

            BufferedImage resizedImage = null;
            if (inputWidth != outputWidth && inputHeight != outputHeight) {
                final double inputAspectRatio = inputWidth * 1.0 / (inputHeight * 1.0);

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

            outputImage = crop(resizedImage, outputWidth, outputHeight);
        }

        return outputImage;
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
