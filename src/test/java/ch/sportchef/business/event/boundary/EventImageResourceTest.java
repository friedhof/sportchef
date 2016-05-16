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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventImageResourceTest {

    private static final String TEST_IMAGE_NAME = "test-350x200.png";

    private EventImageResource eventImageResource;
    private EventService eventServiceMock;
    private EventImageService eventImageServiceMock;

    @Before
    public void setup() {
        eventServiceMock = mock(EventService.class);
        eventImageServiceMock = mock(EventImageService.class);
        eventImageResource = new EventImageResource(1L, eventServiceMock, eventImageServiceMock);
    }

    private byte[] readTestImage() throws IOException {
        final File file = new File(getClass().getClassLoader().getResource(TEST_IMAGE_NAME).getFile());
        return Files.readAllBytes(file.toPath());
    }

    @Test
    public void getImageOK() throws IOException, URISyntaxException {
        // arrange
        final byte[] image = readTestImage();
        when(eventImageServiceMock.getImage(1L)).thenReturn(image);

        // act
        final Response response = eventImageResource.getImage();

        // assert
        final StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        streamingOutput.write(output);
        final byte[] imageResponse = output.toByteArray();
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(imageResponse, is(image));
        verify(eventImageServiceMock, times(1)).getImage(1L);
    }

    @Test
    public void getImageTemporaryRedirect() throws IOException, URISyntaxException {
        // arrange
        when(eventImageServiceMock.getImage(1L)).thenThrow(new NotFoundException());

        // act
        final Response response = eventImageResource.getImage();

        // assert
        assertThat(response.getStatus(), is(TEMPORARY_REDIRECT.getStatusCode()));
        assertThat(response.getLocation().toString(), is("http://placehold.it/350x200"));
        verify(eventImageServiceMock, times(1)).getImage(1L);
    }

    @Test
    public void uploadImageWithOK() throws IOException, ServletException {
        // arrange
        final byte[] fileContent = readTestImage();
        final Part[] parts = new Part[] {
                new FilePart(TEST_IMAGE_NAME, new ByteArrayPartSource(TEST_IMAGE_NAME, fileContent)) };
        final MultipartRequestEntity multipartRequestEntity =
                new MultipartRequestEntity(parts, new PostMethod().getParams());
        final ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
        multipartRequestEntity.writeRequest(requestContent);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(requestContent.toByteArray());
        final ServletInputStreamMock inputStreamMock = new ServletInputStreamMock(inputStream);
        final String contentType = multipartRequestEntity.getContentType();
        final HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getContentType()).thenReturn(contentType);
        when(httpServletRequestMock.getInputStream()).thenReturn(inputStreamMock);

        // act
        final Response response = eventImageResource.uploadImage(httpServletRequestMock);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        verify(httpServletRequestMock, times(1)).getContentType();
        verify(httpServletRequestMock, times(1)).getInputStream();
        verify(eventImageServiceMock, times(1)).uploadImage(anyLong(), anyObject());
    }

    @Test
    public void uploadImageWithBadRequest() throws IOException, ServletException {
        // arrange
        final byte[] image = readTestImage();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        final ServletInputStreamMock inputStreamMock = new ServletInputStreamMock(inputStream);
        final HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getContentType()).thenReturn(MediaType.MULTIPART_FORM_DATA);
        when(httpServletRequestMock.getInputStream()).thenReturn(inputStreamMock);

        // act
        final Response response = eventImageResource.uploadImage(httpServletRequestMock);

        // assert
        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        verify(httpServletRequestMock, times(1)).getContentType();
        verify(httpServletRequestMock, times(1)).getInputStream();
    }

    @Test
    public void delete() {
        // arrange

        // act
        final Response response = eventImageResource.deleteImage();

        // assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
    }

    private static class ServletInputStreamMock extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        ServletInputStreamMock(@NotNull final ByteArrayInputStream inputStream) {
            super();
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(@Nullable final ReadListener readListener) {
            throw new UnsupportedOperationException("This mock does not implement this method!");
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return inputStream.read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            return inputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public void mark(final int readlimit) {
            synchronized (inputStream) {
                inputStream.mark(readlimit);
            }
        }

        @Override
        public void reset() throws IOException {
            synchronized (inputStream) {
                inputStream.reset();
            }
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }
    }

}
