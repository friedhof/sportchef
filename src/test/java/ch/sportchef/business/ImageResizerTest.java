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
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ImageResizerTest {

    private static final String[] IMAGE_NAMES = {
            "test-350x200.png", //NON-NLS
            "test-350x550.png", //NON-NLS
            "test-525x300.png", //NON-NLS
            "test-550x200.png" }; //NON-NLS

    private static final int IMAGE_WIDTH = 350;
    private static final int IMAGE_HEIGHT = 200;

    @Test
    public final void testResizeAndCrop() throws IOException, URISyntaxException {
        for (final String imageName : IMAGE_NAMES) {
            // arrange
            final File file = new File(Thread.currentThread()
                    .getContextClassLoader().getResource(imageName).toURI());
            final BufferedImage inputImage = ImageIO.read(file);

            // act
            final BufferedImage outputImage = ImageResizer.resizeAndCrop(inputImage, IMAGE_WIDTH, IMAGE_HEIGHT);

            // assert
            assertThat(outputImage.getWidth(), is(IMAGE_WIDTH));
            assertThat(outputImage.getHeight(), is(IMAGE_HEIGHT));
        }
    }
}
