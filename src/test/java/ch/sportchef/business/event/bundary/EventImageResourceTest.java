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
package ch.sportchef.business.event.bundary;

import ch.sportchef.business.event.boundary.EventImageResource;
import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.junit.NeedleRule;
import org.needle4j.mock.EasyMockProvider;

import javax.inject.Inject;
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
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventImageResourceTest {

    private static final String TEST_IMAGE_NAME = "test-350x200.png";

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    private EventImageResource eventImageResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private EventService eventServiceMock;

    @Inject
    private EventImageService eventImageServiceMock;

    @Inject
    private HttpServletRequest httpServletRequest;

    @Before
    public void setup() {
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
        expect(eventImageServiceMock.getImage(1L)).andStubReturn(image);
        mockProvider.replayAll();

        // act
        final Response response = eventImageResource.getImage();

        // assert
        final StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        streamingOutput.write(output);
        final byte[] imageResponse = output.toByteArray();

        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(imageResponse, is(image));
        mockProvider.verifyAll();
    }

    @Test
    public void getImageTemporaryRedirect() throws IOException, URISyntaxException {
        // arrange
        expect(eventImageServiceMock.getImage(1L)).andStubThrow(new NotFoundException());
        mockProvider.replayAll();

        // act
        final Response response = eventImageResource.getImage();

        // assert
        assertThat(response.getStatus(), is(TEMPORARY_REDIRECT.getStatusCode()));
        assertThat(response.getLocation().toString(), is("http://placehold.it/350x200"));
        mockProvider.verifyAll();
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

        expect(httpServletRequest.getContentType()).andStubReturn(contentType);
        expect(httpServletRequest.getInputStream()).andStubReturn(inputStreamMock);

        eventImageServiceMock.uploadImage(anyLong(), anyObject());
        mockProvider.replayAll();

        // act
        final Response response = eventImageResource.uploadImage(httpServletRequest);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test
    public void uploadImageWithBadRequest() throws IOException, ServletException {
        // arrange
        final byte[] image = readTestImage();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        final ServletInputStreamMock inputStreamMock = new ServletInputStreamMock(inputStream);

        expect(httpServletRequest.getContentType()).andStubReturn(
                MediaType.MULTIPART_FORM_DATA);
        expect(httpServletRequest.getInputStream()).andStubReturn(inputStreamMock);
        mockProvider.replayAll();

        // act
        final Response response = eventImageResource.uploadImage(httpServletRequest);

        // assert
        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test
    public void delete() {
        // arrange

        // act
        final Response response = eventImageResource.deleteImage();

        // assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
    }

    private class ServletInputStreamMock extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public ServletInputStreamMock(@NotNull final ByteArrayInputStream inputStream) {
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
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return inputStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
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
        public synchronized void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }
    }

}
