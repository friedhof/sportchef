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
