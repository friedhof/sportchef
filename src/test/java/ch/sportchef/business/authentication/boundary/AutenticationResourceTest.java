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
package ch.sportchef.business.authentication.boundary;

import ch.sportchef.business.authentication.control.AuthenticationService;
import ch.sportchef.business.user.entity.User;
import org.apache.commons.mail.EmailException;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutenticationResourceTest {

    private static final String TEST_USER_EMAIL = "auth.test@sportchef.ch";
    private static final String TEST_TOKEN = "TEST_TOKEN";

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest
    private AuthenticationResource authenticationResource;

    @Inject
    private AuthenticationService authenticationServiceMock;

    @Test
    public void requestChallengeWithBadRequest() throws EmailException {
        // arrange
        final String email = "";

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void requestChallengeWithNotFound() throws EmailException {
        // arrange
        final String email = "foobar@sportchef.ch";

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void requestChallengeWithSuccess() throws EmailException {
        // arrange
        final User testUser = User.builder()
                .userId(0L)
                .firstName("AuthTest")
                .lastName("AuthTest")
                .phone("AuthTest")
                .email(TEST_USER_EMAIL)
                .build();
        when(authenticationServiceMock.requestChallenge(TEST_USER_EMAIL))
                .thenReturn(true);

        // act
        final Response response = authenticationResource.requestChallenge(TEST_USER_EMAIL);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        verify(authenticationServiceMock, times(1)).requestChallenge(TEST_USER_EMAIL);
    }

    @Test
    public void authenticateWithWrongEmail() {
        // arrange
        final DefaultLoginCredentials credential = new DefaultLoginCredentials();
        credential.setUserId("foo@bar.ch");
        credential.setPassword("12345-abcde");
        when(authenticationServiceMock.validateChallenge(anyObject(), eq(credential)))
                .thenReturn(Optional.empty());

        // act
        final Response response = authenticationResource.authenticate(credential);

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
        verify(authenticationServiceMock, times(1)).validateChallenge(anyObject(), eq(credential));
    }

    @Test
    public void authenticateWithCorrectEmail() {
        // arrange
        final DefaultLoginCredentials credential = new DefaultLoginCredentials();
        credential.setUserId(TEST_USER_EMAIL);
        credential.setPassword("12345-abcde");
        when(authenticationServiceMock.validateChallenge(anyObject(), eq(credential)))
                .thenReturn(Optional.of(TEST_TOKEN));

        // act
        final Response response = authenticationResource.authenticate(credential);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(((Entity) response.getEntity()).getEntity(), is(TEST_TOKEN));
        verify(authenticationServiceMock, times(1)).validateChallenge(anyObject(), eq(credential));
    }

    @Test
    public void authenticateWithTokenSuccessful() {
        // arrange
        when(authenticationServiceMock.authentication(anyObject(), anyObject(), eq(TEST_TOKEN)))
                .thenReturn(Optional.of(TEST_TOKEN));

        // act
        final Response response = authenticationResource.authenticate(TEST_TOKEN);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(((Entity) response.getEntity()).getEntity(), is(TEST_TOKEN));
        verify(authenticationServiceMock, times(1)).authentication(anyObject(), anyObject(), eq(TEST_TOKEN));
    }

    @Test
    public void authenticateWithTokenUnauthorized() {
        // arrange
        when(authenticationServiceMock.authentication(anyObject(), anyObject(), anyString()))
                .thenReturn(Optional.empty());

        // act
        final Response response = authenticationResource.authenticate("12345-abcde");

        //assert
        assertThat(response.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
        verify(authenticationServiceMock, times(1)).authentication(anyObject(), anyObject(), anyString());
    }

    @Test
    public void logout() {
        // arrange

        // act
        final Response response = authenticationResource.logout();

        // assert
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void unsupportedMediaType() {
        // arrange

        // act
        final Response response = authenticationResource.unsupportedCredentialType(null);

        //assert
        assertThat(response.getStatus(), is(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()));
    }

}
