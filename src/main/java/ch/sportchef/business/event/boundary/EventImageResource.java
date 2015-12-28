package ch.sportchef.business.event.boundary;

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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class EventImageResource {

    private static final String IMAGE_PLACEHOLDER = "http://placehold.it/350x200";
    private static final String FILE_EXTENSION = ".png";
    private static final File IMAGE_UPLOAD_PATH;

    static {
        // build path to image upload folder
        final String imageUploadFolder = System.getProperty("jboss.server.data.dir") +
                File.separator + "sportchef" +
                File.separator + "events" +
                File.separator + "images";

        // create the image upload folder if it does not exist
        IMAGE_UPLOAD_PATH = new File(imageUploadFolder);
        if (!IMAGE_UPLOAD_PATH.exists()) {
            IMAGE_UPLOAD_PATH.mkdirs();
        }
    }

    private final Long eventId;

    public EventImageResource(@NotNull final Long eventId) {
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

        try (final BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream(), 8192)) {
            final MultipartStream multipartStream = new MultipartStream(inputStream, boundary, 8192, null);
            boolean nextPart = multipartStream.skipPreamble();
            while(nextPart) {
                multipartStream.readHeaders(); // don't remove, strips headers off
                final File file = new File(IMAGE_UPLOAD_PATH, this.eventId + FILE_EXTENSION);
                file.createNewFile();
                try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file), 8192)) {
                    multipartStream.readBodyData(outputStream);
                    nextPart = multipartStream.readBoundary();
                }
            }
        }

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
