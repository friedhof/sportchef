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
package ch.sportchef.business.event.control;

import ch.sportchef.business.event.entity.Event;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventImageServiceTest {

    private static final String TEST_IMAGE_NAME = "test-350x200.png";

    private static String realUserHome;
    private static String tempUserHome;
    private static File imageUploadPath;

    @BeforeClass
    public static void setUp() throws IOException {
        realUserHome = System.getProperty("user.home");
        tempUserHome = Files.createTempDirectory("sportchef-").toString();
        System.setProperty("user.home", tempUserHome);

        final String imageUploadFolder = String.format("%s%s.sportchef%simages%sevents", //NON-NLS
                tempUserHome, File.separator, File.separator, File.separator);
        imageUploadPath = new File(imageUploadFolder);
    }

    @AfterClass
    public static void tearDown() {
        System.setProperty("user.home", realUserHome);
    }

    private byte[] readTestImage() throws URISyntaxException, IOException {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader classLoader = currentThread.getContextClassLoader();
        final URL url = classLoader.getResource(TEST_IMAGE_NAME);
        final URI uri = url.toURI();

        return Files.readAllBytes(Paths.get(uri));
    }

    private byte[] prepareAndReturnTestImage(@NotNull final Long eventId) throws URISyntaxException, IOException {
        final byte[] bytes = readTestImage();
        if (!imageUploadPath.exists()) {
            imageUploadPath.mkdirs();
        }
        Files.write(Paths.get(imageUploadPath.getPath(), String.valueOf(eventId).concat(".png")), bytes);

        return bytes;
    }

    private BufferedImage readStoredImage(@NotNull final Long eventId) throws IOException {
        final File file = Paths.get(imageUploadPath.getPath(), String.valueOf(eventId).concat(".png")).toFile();
        return file.exists() ? ImageIO.read(file) : null;
    }

    @Test
    public void getImage() throws IOException, URISyntaxException {
        // arrange
        final Long eventId = 1L;
        final byte[] bytes = prepareAndReturnTestImage(eventId);
        final EventService eventServiceMock = mock(EventService.class);
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        final byte[] image = eventImageService.getImage(eventId);

        // assert
        assertThat(image, is(bytes));
    }

    @Test(expected = NotFoundException.class)
    public void getImageNotFound() throws IOException {
        // arrange
        final Long eventId = 2L;
        final EventService eventServiceMock = mock(EventService.class);
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        eventImageService.getImage(eventId);
    }

    @Test
    public void uploadImage() throws IOException, URISyntaxException {
        // arrange
        final Long eventId = 3L;
        final byte[] bytes = readTestImage();
        final Event event = Event.builder().eventId(eventId).build();
        final EventService eventServiceMock = mock(EventService.class);
        when(eventServiceMock.findByEventId(eventId)).thenReturn(Optional.of(event));
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        eventImageService.uploadImage(eventId, bytes);

        // assert
        final BufferedImage image = readStoredImage(eventId);
        assertThat(image, notNullValue());
    }

    @Test
    public void chooseRandomDefaultImage() throws IOException {
        // arrange
        final Long eventId = 4L;
        final Event event = Event.builder().eventId(eventId).build();
        final EventService eventServiceMock = mock(EventService.class);
        when(eventServiceMock.findByEventId(eventId)).thenReturn(Optional.of(event));
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        eventImageService.chooseRandomDefaultImage(eventId);

        // assert
        final BufferedImage image = readStoredImage(eventId);
        assertThat(image, notNullValue());
    }

    @Test
    public void deleteImage() throws IOException, URISyntaxException {
        // arrange
        final Long eventId = 5L;
        prepareAndReturnTestImage(eventId);
        final EventService eventServiceMock = mock(EventService.class);
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        eventImageService.deleteImage(eventId);

        // assert
        final BufferedImage image = readStoredImage(eventId);
        assertThat(image, nullValue());
    }

    @Test(expected = NotFoundException.class)
    public void deleteImageNotFound() {
        // arrange
        final Long eventId = 6L;
        final EventService eventServiceMock = mock(EventService.class);
        final EventImageService eventImageService = new EventImageService(eventServiceMock);

        // act
        eventImageService.deleteImage(eventId);
    }
}