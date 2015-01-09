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

public class UserResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/users");

    @Test
    public void crud() {
        // create a new user
        final String location = createUserWithSuccess();
        createUserWithBadRequest();

        // read one user
        readOneUserWithSuccess(location);
        readOneUserWithNotFound(location.substring(0, location.lastIndexOf("/") + 1) + Long.MAX_VALUE);

        // read all users
        readAllUsers(location); // location of created user to do asserts
    }

    private long getUserId(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private String createUserWithSuccess() {
        // arrange
        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Doe")
                .add("phone", "+41 79 555 00 01")
                .add("email", "john.doe@sportchef.ch")
                .build();

        // act
        final Response response = this.provider.target().request().post(Entity.json(userToCreate));

        //assert
        assertThat(response.getStatus(), is(201));
        final String location = response.getHeaderString("Location");
        assertThat(location, notNullValue());
        final long id = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
        assertTrue(id > 0);

        return location;
    }

    private void createUserWithBadRequest() {
        // arrange
        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", "")
                .add("lastName", "")
                .add("phone", "")
                .add("email", "")
                .build();

        // act
        final Response response = this.provider.target().request().post(Entity.json(userToCreate));

        //assert
        assertThat(response.getStatus(), is(400));
    }

    private void readOneUserWithSuccess(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(200));
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("John"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("john.doe@sportchef.ch"));
    }

    private void readOneUserWithNotFound(final String location) {
        // arrange

        // act
        final Response response = this.provider.target(location)
                .request(MediaType.APPLICATION_JSON).get();
        final JsonObject jsonObject = response.readEntity(JsonObject.class);

        // assert
        assertThat(response.getStatus(), is(404));
        assertNull(jsonObject);
    }

    private void readAllUsers(final String location) {
        // arrange

        // act
        final Response response = this.provider.target()
                .request(MediaType.APPLICATION_JSON).get();
        final JsonArray jsonArray = response.readEntity(JsonArray.class);
        final JsonObject jsonObject = jsonArray.size() > 0 ? jsonArray.getJsonObject(jsonArray.size() - 1) : null;

        // assert
        assertThat(response.getStatus(), is(200));
        assertFalse(jsonArray.isEmpty());
        assertNotNull(jsonObject);
        assertThat(jsonObject.getJsonNumber("userId").longValue(), is(getUserId(location)));
        assertThat(jsonObject.getString("firstName"), is("John"));
        assertThat(jsonObject.getString("lastName"), is("Doe"));
        assertThat(jsonObject.getString("phone"), is("+41 79 555 00 01"));
        assertThat(jsonObject.getString("email"), is("john.doe@sportchef.ch"));
    }

}
