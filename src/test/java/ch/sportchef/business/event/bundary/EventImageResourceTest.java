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
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT;
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

}
