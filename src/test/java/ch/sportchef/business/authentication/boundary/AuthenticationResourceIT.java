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
import ch.sportchef.test.TestData;
import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.json.Json;
import javax.json.JsonObject;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static com.airhacks.rulz.jaxrsclient.JAXRSClientProvider.buildWithURI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@Category(IntegrationTests.class)
public class AuthenticationResourceIT {

    @ClassRule
    public static final JAXRSClientProvider PROVIDER = buildWithURI("http://localhost:8080/sportchef/api/users");

    private static final String TEST_USER_EMAIL = "auth.test@sportchef.ch";

    private static String testUserLocation;

    @BeforeClass
    public static void createTestUser() {
        testUserLocation = TestData.createTestUser("AuthTest", "AuthTest", "AuthTest",  TEST_USER_EMAIL, false);
    }

    @AfterClass
    public static void deleteTestUser() {
        TestData.deleteTestUser(testUserLocation);
    }

    @Rule
    public final JAXRSClientProvider provider = buildWithURI("http://localhost:8080/sportchef/api/authentication");

    @Test
    public void requestChallengeWithBadRequest() {
        // arrange
        final String email = "";

        // act
        final Response response = provider.target()
                .queryParam("email", email)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        //assert
        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void requestChallengeWithNotFound() {
        // arrange
        final String email = "foobar@sportchef.ch";

        // act
        final Response response = provider.target()
                .queryParam("email", email)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void testChallengeAndResponse() throws InterruptedException, IOException, MessagingException {
        requestChallengeWithSuccess();
        Thread.sleep(1000); // wait 1 second for mail to be delivered
        final Optional<String> challenge = checkEmailForChallenge();
        if (challenge.isPresent()) {
            checkChallengeWithWrongEmail(challenge.get());
            checkChallengeWithCorrectEmail(challenge.get());
            // authenticateWithTokenSuccessful(token);
        } else {
            fail("No challenge mail received!");
        }
    }

    private void requestChallengeWithSuccess() {
        // arrange
        final String email = TEST_USER_EMAIL;

        // act
        final Response response = provider.target()
                .queryParam("email", email)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    private Optional<String> checkEmailForChallenge() throws InterruptedException, IOException, MessagingException {
        String challenge = null;
        boolean emailFound = false;
        int tries = 0;

        final Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        final Session session = Session.getInstance(props, null);
        final Store store = session.getStore();
        store.connect("mail.your-server.de", "auth.test@sportchef.ch", "E4f23bOQwX82iYOh");
        final Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        while (!emailFound && tries <= 10) {
            tries++;

            if (inbox.getMessageCount() > 0) {
                final Message[] messages = inbox.getMessages();
                for (final Message message : messages) {
                    final String content = (String) message.getContent();
                    if (content.startsWith("Challenge = ")) {
                        challenge = content.split("=")[1].trim();
                        if (challenge != null && !challenge.isEmpty()) {
                            emailFound = true;
                        }
                    }

                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }

            Thread.sleep(3000); // 3 seconds
        }

        inbox.close(true);

        return Optional.ofNullable(challenge);
    }

    private void checkChallengeWithWrongEmail(@NotNull final String challenge) {
        // arrange
        final JsonObject credential = Json.createObjectBuilder()
                .add("userId", "foo@bar.ch")
                .add("password", challenge)
                .build();

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(credential));

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }

    private void checkChallengeWithCorrectEmail(@NotNull final String challenge) {
        // arrange
        final JsonObject credential = Json.createObjectBuilder()
                .add("userId", TEST_USER_EMAIL)
                .add("password", challenge)
                .build();

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(credential));

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    private void authenticateWithTokenSuccessful(@NotNull final String token) {
        // arrange

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.text(token));

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void authenticateWithChallengeForbidden() {
        // arrange
        final JsonObject credential = Json.createObjectBuilder()
                .add("userId", "foo@bar.ch")
                .add("password", "abcd1234")
                .build();

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(credential));

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    public void authenticateWithTokenUnauthorized() {
        // arrange
        final String token = "invalid_token";

        // act
        final Response response = provider.target()
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.text(token));

        //assert
        assertThat(response.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
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
