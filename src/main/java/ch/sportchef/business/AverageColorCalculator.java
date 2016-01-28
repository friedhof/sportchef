package ch.sportchef.business;

import java.awt.Color;
import java.awt.image.BufferedImage;

public enum AverageColorCalculator {
    ;

    public static String getAverageColorAsHex(final BufferedImage image) {
        int red = 0, green = 0, blue = 0, pixelCount = 0, columnCount, rowCount;

        for (columnCount = 0; columnCount < image.getWidth(); columnCount++) {
            for (rowCount = 0; rowCount < image.getHeight(); pixelCount++) {
                Color color = new Color(image.getRGB(columnCount, rowCount++));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
        }

        final Color averageColor = new Color(red / pixelCount, green / pixelCount, blue / pixelCount);
        final int averageColorRGB = averageColor.getRGB();

        return String.format("#%06X", 0xFFFFFF & averageColorRGB); // print rgb as hex value
    }
}
