package ch.sportchef.business.user.bundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class UserResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/users");

    @Test
    public void crud() {
        // create
        final long newUserId = createWithSuccess();
        createWithError();
    }

    public long createWithSuccess() {
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

        return id;
    }

    public void createWithError() {
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

}
