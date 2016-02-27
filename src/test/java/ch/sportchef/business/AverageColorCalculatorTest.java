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

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AverageColorCalculatorTest {

    private static final String[][] TEST_IMAGES = {
            { "test-grey-dark.png",  "#5E5E5E" }, //NON-NLS
            { "test-grey-light.png", "#A0A0A0" }  //NON-NLS
    };

    @Test
    @SuppressWarnings("ObjectAllocationInLoop")
    public void getAverageColorAsHex() throws URISyntaxException, IOException {
        for (final String[] testImage : TEST_IMAGES) {
            // arrange
            final String testImageName = testImage[0];
            final String expectedColorCode = testImage[1];
            final Thread currentThread = Thread.currentThread();
            final ClassLoader classLoader = currentThread.getContextClassLoader();
            final URL url = classLoader.getResource(testImageName);
            assert url != null;
            final URI uri = url.toURI();
            final File file = new File(uri);
            final BufferedImage inputImage = ImageIO.read(file);

            // act
            final String averageColorAsHex = AverageColorCalculator.getAverageColorAsHex(inputImage);

            // assert
            assertThat(averageColorAsHex, is(expectedColorCode));
        }
    }

    @Test(expected=NullPointerException.class)
    public final void testImageColorWithoutImage() {
        // arrange

        // act
        AverageColorCalculator.getAverageColorAsHex(null);

        // assert
    }
}