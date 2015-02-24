package ch.sportchef.business.user.bundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EventResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/events");

    @Test
    public void crud() {
        // create
        final String location = createEventWithSuccess();
        final String notFoundLocation = location.substring(0, location.lastIndexOf("/") + 1) + Long.MAX_VALUE;
        createEventWithBadRequest();

        // read
        readOneEventWithSuccess(location);
        readOneEventWithNotFound(notFoundLocation);
        readAllEvents(location);

        // update
        final JsonObject eventToConflict = updateEventWithSuccess(location);
        updateEventWithConflict(location, eventToConflict);
        updateEventWithNotFound(notFoundLocation);
    }

    private long getEventId(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private String createEventWithSuccess() {
        // arrange
        final JsonObject eventToCreate = Json.createObjectBuilder()
                .add("title", "Christmas Party")
                .add("location", "Town Hall")
                .add("date", "2015-12-24")
                .add("time", "18:00")
                .build();

        // act
        final Response response = this.provider.target().request(MediaType.APPLICATION_JSON).post(Entity.json(eventToCreate));

        //assert
        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        final String location = response.getHeaderString("Location");
        assertThat(location, notNullValue());
        final long eventId = getEventId(location);
        assertTrue(eventId > 0);

        return location;
    }

    private void createEventWithBadRequest() {
        // arrange
        final JsonObject eventToCreate = Json.createObjectBuilder()
                .add("title", "")
                .add("location", "")
                .add("date", "")
                .add("time", "")
                .build();

        // act
        final Response response = this.provider.target().request(MediaType.APPLICATION_JSON).post(Entity.json(eventToCreate));

        //assert
        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
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

    private void readAllEvents(final String location) {
        // arrange

        // act
        final Response response = this.provider.target()
                .request(MediaType.APPLICATION_JSON).get();
        final JsonArray jsonArray = response.readEntity(JsonArray.class);
        final JsonObject jsonObject = jsonArray.size() > 0 ? jsonArray.getJsonObject(jsonArray.size() - 1) : null;

        // assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertFalse(jsonArray.isEmpty());
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("eventId").longValue(), is(getEventId(location)));
        assertThat(jsonObject.getString("title"), is("Christmas Party"));
        assertThat(jsonObject.getString("location"), is("Town Hall"));
        assertThat(jsonObject.getString("date"), is("2015-12-24"));
        assertThat(jsonObject.getString("time"), is("18:00"));
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

}
