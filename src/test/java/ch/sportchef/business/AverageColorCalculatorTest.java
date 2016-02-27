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
import org.junit.internal.runners.statements.ExpectException;

import javax.imageio.ImageIO;
import javax.ws.rs.NotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class AverageColorCalculatorTest {

    private static final String[] IMAGE_NAMES = {
            "test-grey-light.png", //NON-NLS
            "test-grey-dark.png" }; //NON-NLS

    @Test
    public final void testImageColorSuccess() throws URISyntaxException, IOException {

        final Thread currentThread = Thread.currentThread();
        final ClassLoader classLoader = currentThread.getContextClassLoader();
        final URL url = classLoader.getResource("test-grey-light.png");
        assert url != null;
        final URI uri = url.toURI();

        // act
        final String outputHex = testAverageColorAsHex(uri);

        // assert
        assertThat(outputHex, is("#A0A0A0"));
    }

    @Test
    public final void testImageColorFailed() throws URISyntaxException, IOException {

        final Thread currentThread = Thread.currentThread();
        final ClassLoader classLoader = currentThread.getContextClassLoader();
        final URL url = classLoader.getResource("test-grey-dark.png");
        assert url != null;
        final URI uri = url.toURI();

        // act
        final String outputHex = testAverageColorAsHex(uri);

        // assert
        assertThat(outputHex, not("#A0A0A0"));
    }

    @Test(expected=NullPointerException.class)
    public final void testImageColorWithoutImage() throws URISyntaxException, IOException {
/*
        final Thread currentThread = Thread.currentThread();
        final ClassLoader classLoader = currentThread.getContextClassLoader();
        final URL url = classLoader.getResource("test-grey-dark.png");
        assert url != null;
        final URI uri = url.toURI();
*/
        // act
        final String outputHex = testAverageColorAsHex(null);

        // assert
        assertThat(outputHex, not("#A0A0A0"));
    }

    private static String testAverageColorAsHex(final URI uri) throws IOException {
        final File file = new File(uri);
        final BufferedImage inputImage = ImageIO.read(file);
        return AverageColorCalculator.getAverageColorAsHex(inputImage);
    }

}