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
            "test-350x200.png",
            "test-350x550.png",
            "test-525x300.png",
            "test-550x200.png" };

    private static final int IMAGE_WIDTH = 350;
    private static final int IMAGE_HEIGHT = 200;

    @Test
    public void testResizeAndCrop() throws IOException, URISyntaxException {
        for (int i = 0; i < IMAGE_NAMES.length; i++) {
            // arrange
            final File file = new File(Thread.currentThread()
                    .getContextClassLoader().getResource(IMAGE_NAMES[i]).toURI());
            final BufferedImage inputImage = ImageIO.read(file);

            // act
            final BufferedImage outputImage = ImageResizer.resizeAndCrop(inputImage, IMAGE_WIDTH, IMAGE_HEIGHT);

            // assert
            assertThat(outputImage.getWidth(), is(IMAGE_WIDTH));
            assertThat(outputImage.getHeight(), is(IMAGE_HEIGHT));
        }
    }
}
