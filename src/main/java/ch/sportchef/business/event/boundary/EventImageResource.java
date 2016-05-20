/*
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

import ch.sportchef.business.event.control.EventImageService;
import org.apache.commons.fileupload.MultipartStream;

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
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class EventImageResource {

    private static final String IMAGE_PLACEHOLDER = "http://placehold.it/350x200"; //NON-NLS

    private final Long eventId;
    private final EventImageService eventImageService;

    public EventImageResource(@NotNull final Long eventId,
                              @NotNull final EventImageService eventImageService) {
        this.eventId = eventId;
        this.eventImageService = eventImageService;
    }

    @GET
    @Produces({"image/png"})
    public Response getImage() throws URISyntaxException, IOException {
        Response response;

        try {
            final byte[] image = eventImageService.getImage(eventId);
            response = Response.ok().entity((StreamingOutput) stream -> {
                stream.write(image);
                stream.flush();
            }).build();
        } catch (final NotFoundException e) {
            // no image found, redirecting to placeholder image
            final URI location = new URI(IMAGE_PLACEHOLDER);
            response = Response.temporaryRedirect(location).build();
        }

        return response;
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(@Context final HttpServletRequest request) throws IOException, ServletException {
        Response response = Response.status(BAD_REQUEST).build();

        final String contentType = request.getContentType();
        final byte[] boundary = contentType.substring(contentType.indexOf("boundary=") + 9).getBytes(); //NON-NLS

        try (final BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream(), 8192)) {
            final MultipartStream multipartStream = new MultipartStream(inputStream, boundary, 8192, null);
            final boolean nextPart = multipartStream.skipPreamble();
            //noinspection LoopStatementThatDoesntLoop
            while (nextPart) {
                multipartStream.readHeaders(); // don't remove, strips headers off
                //noinspection NestedTryStatement
                try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192)) {
                    multipartStream.readBodyData(outputStream);
                    final byte[] image = outputStream.toByteArray();
                    eventImageService.uploadImage(eventId, image);
                    response = Response.ok().build();
                    break;
                }
            }
        }

        return response;
    }

    @DELETE
    public Response deleteImage() {
        eventImageService.deleteImage(eventId);
        return Response.noContent().build();
    }
}
