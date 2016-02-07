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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.boundary;

import ch.sportchef.test.IntegrationTests;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(IntegrationTests.class)
public class AuthenticationResourceIT {

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/authentication");

    @Test
    public void authenticateWithUsernamePasswordNotSupported() {
        // arrange
        final JsonObject credential = Json.createObjectBuilder()
                .add("userId", "jane")
                .add("password", "abcd1234")
                .build();

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(credential));

        //assert
        assertThat(response.getStatus(), is(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()));
    }

    @Test
    public void authenticateWithTokenForbidden() {
        // arrange
        final String token = "invalid_token";

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.text(token));

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    public void authenticateWithTokenSuccessful() {
        // arrange
        final String token = "valid_token";

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.text(token));

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void logout() {
        // arrange

        // act
        final Response response = provider.target()
                .request()
                .delete();

        //assert
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void unsupportedMediaType() {
        // arrange
        final String html = "<p>Ups!</p>";

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(html, MediaType.TEXT_HTML));

        //assert
        assertThat(response.getStatus(), is(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()));
    }

}
