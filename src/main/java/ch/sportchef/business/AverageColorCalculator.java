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

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.awt.Color;
import java.awt.image.BufferedImage;

@UtilityClass
public class AverageColorCalculator {

    private static final int THRESHOLD_COLOR = 600;

    public static String getAverageColorAsHex(@NotNull final BufferedImage image) {
        final Color averageColor = checkColorThreshold(getAverageColor(image));
        final int averageColorRGB = averageColor.getRGB();
        
        return String.format("#%06X", 0xFFFFFF & averageColorRGB); // print rgb as hex value
    }

    private static Color getAverageColor(@NotNull final BufferedImage image) {
        long red = 0;
        long green = 0;
        long blue = 0;
        long pixelCount = 0;

        int columnCount;
        int rowCount;

        for (columnCount = 0; columnCount < image.getWidth(); columnCount++) {
            for (rowCount = 0; rowCount < image.getHeight(); pixelCount++) {
                @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // color is immutable
                final Color color = new Color(image.getRGB(columnCount, rowCount++));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
        }

        final int averageRed = (int) (red / pixelCount);
        final int averageGreen = (int) (green / pixelCount);
        final int averageBlue = (int) (blue / pixelCount);

        return new Color(averageRed, averageGreen, averageBlue);
    }

    private static Color checkColorThreshold(@NotNull final Color color) {
        int averageRed = color.getRed();
        int averageGreen = color.getGreen();
        int averageBlue = color.getBlue();
        long averageColor = averageRed + averageGreen + averageBlue;

        if (averageColor > THRESHOLD_COLOR) {
            while (averageColor > THRESHOLD_COLOR) {
                if (averageRed > 0) averageRed--;
                if (averageGreen > 0) averageGreen--;
                if (averageBlue > 0) averageBlue--;
                averageColor = averageRed + averageGreen + averageBlue;
            }
        }

        return new Color(averageRed, averageGreen, averageBlue);
    }
}
