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
import ch.sportchef.business.authentication.entity.AuthenticationData;
import ch.sportchef.business.user.entity.User;
import org.apache.commons.mail.EmailException;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
        final String email = "foo@bar.ch";
        final String challenge = "12345-abcde";
        final AuthenticationData authenticationData = new AuthenticationData(email, challenge);
        when(authenticationServiceMock.validateChallenge(email, challenge))
                .thenReturn(Optional.empty());

        // act
        final Response response = authenticationResource.authenticate(authenticationData);

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
        verify(authenticationServiceMock, times(1)).validateChallenge(email, challenge);
    }

    @Test
    public void authenticateWithCorrectEmail() {
        // arrange
        final String email = TEST_USER_EMAIL;
        final String challenge = "12345-abcde";
        final AuthenticationData authenticationData = new AuthenticationData(email, challenge);
        when(authenticationServiceMock.validateChallenge(email, challenge))
                .thenReturn(Optional.of(TEST_TOKEN));

        // act
        final Response response = authenticationResource.authenticate(authenticationData);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(((Entity) response.getEntity()).getEntity(), is(TEST_TOKEN));
        verify(authenticationServiceMock, times(1)).validateChallenge(email, challenge);
    }

}
