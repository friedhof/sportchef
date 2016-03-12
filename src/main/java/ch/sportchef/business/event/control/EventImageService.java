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

import ch.sportchef.business.AverageColorCalculator;
import ch.sportchef.business.ImageResizer;
import ch.sportchef.business.event.entity.Event;
import org.apache.commons.io.IOUtils;
import pl.setblack.badass.Politician;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Random;

@Singleton
public class EventImageService {

    private static final String FILE_EXTENSION = ".png"; //NON-NLS
    private static final String FILE_TYPE = "PNG"; //NON-NLS
    private static final File IMAGE_UPLOAD_PATH;
    private static final int IMAGE_HEIGHT = 200;
    private static final int IMAGE_WIDTH = 350;

    static {
        // build path to image upload folder
        final String imageUploadFolder = String.format("%s%s.sportchef%simages%sevents", //NON-NLS
                System.getProperty("user.home"), File.separator, File.separator, File.separator);

        // create the image upload folder if it does not exist
        IMAGE_UPLOAD_PATH = new File(imageUploadFolder);
        if (!IMAGE_UPLOAD_PATH.exists()) {
            IMAGE_UPLOAD_PATH.mkdirs();
        }
    }

    @Inject
    private EventService eventService;

    public byte[] getImage(@NotNull final Long eventId) throws IOException {
        final File file = new File(IMAGE_UPLOAD_PATH, String.format("%d%s", eventId, FILE_EXTENSION)); //NON-NLS
        if (file.exists()) {
            return Files.readAllBytes(file.toPath());
        }
        throw new NotFoundException(String.format("event with id '%d' has no image", eventId)); //NON-NLS
    }

    public void uploadImage(@NotNull final Long eventId, @NotNull final byte[] image) throws IOException {
        final File file = new File(IMAGE_UPLOAD_PATH, String.format("%d%s", eventId, FILE_EXTENSION)); //NON-NLS
        file.createNewFile();
        try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file), 8192)) {
            outputStream.write(image);
        }

        final String averageColor;
        final BufferedImage inputImage = ImageIO.read(file);
        final BufferedImage outputImage = ImageResizer.resizeAndCrop(inputImage, IMAGE_WIDTH, IMAGE_HEIGHT);
        ImageIO.write(outputImage, FILE_TYPE, file);
        averageColor = AverageColorCalculator.getAverageColorAsHex(outputImage);
        inputImage.flush();
        outputImage.flush();

        final Event event = eventService.findByEventId(eventId).get();
        final Event eventToUpdate = event.toBuilder()
                .cssBackgroundColor(averageColor)
                .build();
        eventService.update(eventToUpdate);
    }

    public void chooseRandomDefaultImage(@NotNull final Long eventId) {
        final int index = new Random().nextInt(14);
        final String filename = String.format("default-event-image-%03d.png", index);
        Politician.beatAroundTheBush(() -> {
            try (final InputStream inputStream =
                         Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
                final byte[] image = IOUtils.toByteArray(inputStream);
                uploadImage(eventId, image);
            }
        });
    }

    public void deleteImage(@NotNull final Long eventId) {
        final File file = new File(IMAGE_UPLOAD_PATH, eventId + FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        }
        throw new NotFoundException(String.format("event with id '%d' has no image", eventId)); //NON-NLS
    }

}
