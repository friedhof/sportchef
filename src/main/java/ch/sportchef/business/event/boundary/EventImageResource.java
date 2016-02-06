/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015, 2016 Marcus Fihlon
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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.AverageColorCalculator;
import ch.sportchef.business.ImageResizer;
import ch.sportchef.business.event.entity.Event;
import org.apache.commons.fileupload.MultipartStream;
import pl.setblack.airomem.core.SimpleController;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;


public class EventImageResource {

    private static final String IMAGE_PLACEHOLDER = "http://placehold.it/350x200";
    private static final String FILE_EXTENSION = ".png";
    private static final String FILE_TYPE = "PNG";
    private static final File IMAGE_UPLOAD_PATH;
    private static final int IMAGE_HEIGHT = 200;
    private static final int IMAGE_WIDTH = 350;

    static {
        // build path to image upload folder
        final String imageUploadFolder = System.getProperty("user.home") +
                File.separator + ".sportchef" +
                File.separator + "images" +
                File.separator + "events";

        // create the image upload folder if it does not exist
        IMAGE_UPLOAD_PATH = new File(imageUploadFolder);
        if (!IMAGE_UPLOAD_PATH.exists()) {
            IMAGE_UPLOAD_PATH.mkdirs();
        }
    }

    private final Long eventId;
    private final SimpleController<EventManager> manager;

    public EventImageResource(final SimpleController<EventManager> manager, @NotNull final Long eventId) {
        this.manager = manager;
        this.eventId = eventId;
    }

    @GET
    @Produces({"image/png"})
    public Response getImage() throws URISyntaxException, IOException {
        final File file = new File(IMAGE_UPLOAD_PATH, this.eventId + FILE_EXTENSION);
        if (file.exists()) {
            final byte[] image = Files.readAllBytes(file.toPath());
            return Response.ok().entity((StreamingOutput) stream -> {
                stream.write(image);
                stream.flush();
            }).build();
        }

        final URI location = new URI(IMAGE_PLACEHOLDER);
        return Response.temporaryRedirect(location).build();
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(@Context HttpServletRequest request) throws IOException, ServletException {
        final String contentType = request.getContentType();
        final byte[] boundary = contentType.substring(contentType.indexOf("boundary=") + 9).getBytes();

        File file = null;
        try (final BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream(), 8192)) {
            final MultipartStream multipartStream = new MultipartStream(inputStream, boundary, 8192, null);
            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                multipartStream.readHeaders(); // don't remove, strips headers off
                file = new File(IMAGE_UPLOAD_PATH, this.eventId + FILE_EXTENSION);
                file.createNewFile();
                try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file), 8192)) {
                    multipartStream.readBodyData(outputStream);
                    nextPart = multipartStream.readBoundary();
                }
            }
        }

        final String averageColor;
        if (file != null && file.exists()) {
            try {
                final BufferedImage inputImage = ImageIO.read(file);
                final BufferedImage outputImage = ImageResizer.resizeAndCrop(inputImage, IMAGE_WIDTH, IMAGE_HEIGHT);
                ImageIO.write(outputImage, FILE_TYPE, file);
                averageColor = AverageColorCalculator.getAverageColorAsHex(outputImage);
                inputImage.flush();
                outputImage.flush();
            } catch (final IOException e) {
                return Response.status(INTERNAL_SERVER_ERROR).header("Cause", e.getMessage()).build();
            }
        } else {
            // there was no image in the upload
            return Response.status(BAD_REQUEST).build();
        }

        final Event event = manager.readOnly().findByEventId(eventId);
        final Event eventToUpdate = new Event(event.getEventId(), event.getTitle(), event.getLocation(), event.getDate(), event.getTime(), averageColor);
        final Event updatedEvent = this.manager.executeAndQuery(mgr -> mgr.update(eventToUpdate));

        return Response.ok().build();
    }

    @DELETE
    public Response deleteImage() {
        final File file = new File(IMAGE_UPLOAD_PATH, this.eventId + FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
            return Response.noContent().build();
        }
        throw new NotFoundException(String.format("event with id '%d' has no image", eventId));
    }
}
