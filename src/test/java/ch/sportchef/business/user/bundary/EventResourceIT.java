package ch.sportchef.business.user.bundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/events");

    @Test
    public void crud() {
        // create
        final String location = createEventWithSuccess();
        createEventWithBadRequest();
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

}
