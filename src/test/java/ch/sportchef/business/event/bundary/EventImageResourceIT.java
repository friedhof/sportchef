/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.event.bundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventImageResourceIT {

    @ClassRule
    public static final JAXRSClientProvider PROVIDER = buildWithURI("http://localhost:8080/sportchef/api/events");

    private static String eventLocation;
    private static String imageLocation;

    @BeforeClass
    public static void createTestEvent() {
        final JsonObject eventToCreate = Json.createObjectBuilder()
                .add("title", "Christmas Party")
                .add("location", "Town Hall")
                .add("date", "2015-12-24")
                .add("time", "18:00")
                .build();
        final Response response = PROVIDER.target().request(MediaType.APPLICATION_JSON).post(Entity.json(eventToCreate));
        eventLocation = response.getHeaderString("Location");
        imageLocation = eventLocation + "/image";
    }

    @AfterClass
    public static void deleteTestEvent() {
        PROVIDER.target(eventLocation).request(MediaType.APPLICATION_JSON).delete();
    }

    @Test
    public void crud() throws IOException, URISyntaxException {
        readImageWithRedirect();
        uploadImage();
        readImageWithSuccess();
        deleteImageWithSuccess();
        readImageWithRedirect();
        deleteImageWithNotFound();
        readImageOfNonExistingEvent();
    }

    private void readImageWithRedirect() {
        // arrange

        // act
        final Response response = PROVIDER.target(this.imageLocation)
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .request("image/png").get();

        // assert
        assertThat(response.getStatus(), is(Response.Status.TEMPORARY_REDIRECT.getStatusCode()));
        final String imageLocation = response.getHeaderString("Location");
        assertThat(imageLocation, notNullValue());
        assertThat(imageLocation, is("http://placehold.it/350x200"));
    }

    private void uploadImage() throws IOException, URISyntaxException {
        // arrange
        final File file = new File(Thread.currentThread()
                .getContextClassLoader().getResource("test.png").toURI());
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPut httpPut = new HttpPut(this.imageLocation);
        final FileBody fileBody = new FileBody(file);
        final HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("file", fileBody).build();
        httpPut.setEntity(entity);

        // act
        final HttpResponse response = httpClient.execute(httpPut);

        // assert
        assertThat(response.getStatusLine().getStatusCode(), is(Response.Status.OK.getStatusCode()));
    }

    private void readImageWithSuccess() {
        // arrange

        // act
        final Response response = PROVIDER.target(this.imageLocation)
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .request("image/png").get();

        // assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        final String imageLocation = response.getHeaderString("Location");
        assertThat(imageLocation, nullValue());
    }

    private void deleteImageWithSuccess() {
        // arrange

        // act
        final Response response = PROVIDER.target(this.imageLocation)
                .request(MediaType.APPLICATION_OCTET_STREAM).delete();

        // assert
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    private void deleteImageWithNotFound() {
        // arrange

        // act
        final Response response = PROVIDER.target(this.imageLocation)
                .request(MediaType.APPLICATION_OCTET_STREAM).delete();

        // assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    private void readImageOfNonExistingEvent() {
        // arrange
        final String notFoundLocation = this.imageLocation.replaceFirst("\\/events\\/[0-9]*\\/", "/events/" + Long.MAX_VALUE + "/");

        // act
        final Response response = PROVIDER.target(notFoundLocation)
                .request("image/png").get();

        // assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }
}
