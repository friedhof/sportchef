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

import ch.sportchef.test.IntegrationTests;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.experimental.categories.Category;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category(IntegrationTests.class)
public class EventResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/events");

    private long getEventId(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private void readOneEventWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("eventId").longValue(), is(getEventId(location)));
        assertThat(jsonObject.getString("title"), is("Christmas Party"));
        assertThat(jsonObject.getString("location"), is("Town Hall"));
        assertThat(jsonObject.getString("date"), is("2015-12-24"));
        assertThat(jsonObject.getString("time"), is("18:00"));
    }

    private void readOneEventWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
        assertNull(jsonObject);
    }

    private JsonObject updateEventWithSuccess(final String location) {
        // arrange
        final JsonObject eventToUpdate = Json.createObjectBuilder()
                .add("eventId", getEventId(location))
                .add("title", "New Year Party")
                .add("location", "Town Hall")
                .add("date", "2015-12-31")
                .add("time", "20:00")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(eventToUpdate));
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("eventId").longValue(), is(getEventId(location)));
        assertThat(jsonObject.getString("title"), is("New Year Party"));
        assertThat(jsonObject.getString("location"), is("Town Hall"));
        assertThat(jsonObject.getString("date"), is("2015-12-31"));
        assertThat(jsonObject.getString("time"), is("20:00"));

        return eventToUpdate;
    }

    private void updateEventWithConflict(final String location, final JsonObject eventToUpdate) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(eventToUpdate));

        //assert
        assertThat(response.getStatus(), is(Response.Status.CONFLICT.getStatusCode()));
    }

    private void updateEventWithNotFound(final String location) {
        // arrange
        final JsonObject eventToUpdate = Json.createObjectBuilder()
                .add("eventId", getEventId(location))
                .add("title", "New Year Party")
                .add("location", "Town Hall")
                .add("date", "2015-12-31")
                .add("time", "20:00")
                .build();

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).put(Entity.json(eventToUpdate));

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    private void deleteEventWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    private void deleteEventWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location).request(MediaType.APPLICATION_JSON).delete();

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

}
