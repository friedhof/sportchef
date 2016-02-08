package ch.sportchef.test;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestData {

    public static final JAXRSClientProvider PROVIDER = buildWithURI("http://localhost:8080/sportchef/api/users");

    public static String createTestUser(@NotNull final String firstName,
                                            @NotNull final String lastName,
                                            @NotNull final String phone,
                                            @NotNull final String email,
                                            final boolean replace) {
        if (replace) {
            cleanTestUsers(email);
        } else {
            final Optional<JsonObject> user = searchTestUser(email);
            if (user.isPresent()) {
                final String userId = Long.toString(user.get().getJsonNumber("userId").longValue());
                return PROVIDER.target().path(userId).getUri().toString();
            }
        }

        final JsonObject userToCreate = Json.createObjectBuilder()
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("phone", phone)
                .add("email", email)
                .build();
        final Response response = PROVIDER.target().request(MediaType.APPLICATION_JSON).post(Entity.json(userToCreate));
        final String location = response.getHeaderString("Location");

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        assertThat(location, notNullValue());

        return location;
    }

    private static Optional<JsonObject> searchTestUser(@NotNull final String email) {
        final Response response = PROVIDER.target().request(MediaType.APPLICATION_JSON).get();
        final JsonArray jsonArray = response.readEntity(JsonArray.class);
        return jsonArray.stream()
                .map(v -> (JsonObject) v)
                .filter(o -> email.equals(o.getString("email")))
                .findFirst();
    }

    private static void cleanTestUsers(@NotNull final String email) {
        final Response response1 = PROVIDER.target().request(MediaType.APPLICATION_JSON).get();
        final JsonArray jsonArray = response1.readEntity(JsonArray.class);
        jsonArray.forEach((value) -> {
            final JsonObject user = (JsonObject) value;
            if (email.equals(user.getString("email"))) {
                final String userId = Long.toString(user.getJsonNumber("userId").longValue());
                final Response response2 = PROVIDER.target().path(userId).request().delete();
                assertThat(response2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
            }
        });
    }

    public static void deleteTestUser(@NotNull final String location) {
        final Response response = PROVIDER.target(location).request().delete();
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }
}
