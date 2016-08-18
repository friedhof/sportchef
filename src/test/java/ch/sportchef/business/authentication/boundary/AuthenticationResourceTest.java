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
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticationResourceTest {

    @Test
    public void requestChallengeWithBadRequest() {
        // arrange
        final String email = "";
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        final AuthenticationResource authenticationResource = new AuthenticationResource(authenticationServiceMock);

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
        verify(authenticationServiceMock, times(0)).requestChallenge(email);
    }

    @Test
    public void requestChallengeWithNotFound() {
        // arrange
        final String email = "foobar@sportchef.ch";
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.requestChallenge(email)).thenReturn(false);
        final AuthenticationResource authenticationResource = new AuthenticationResource(authenticationServiceMock);

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
        verify(authenticationServiceMock, times(1)).requestChallenge(email);
    }

    @Test
    public void requestChallengeWithSuccess() {
        // arrange
        final String email = "foobar@sportchef.ch";
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.requestChallenge(email)).thenReturn(true);
        final AuthenticationResource authenticationResource = new AuthenticationResource(authenticationServiceMock);

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        verify(authenticationServiceMock, times(1)).requestChallenge(email);
    }

    @Test
    public void authenticateWithWrongEmail() {
        // arrange
        final String email = "foobar@sportchef.ch";
        final String challenge = "12345-abcde";
        final AuthenticationData authenticationData = new AuthenticationData(email, challenge);
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateChallenge(email, challenge)).thenReturn(Optional.empty());
        final AuthenticationResource authenticationResource = new AuthenticationResource(authenticationServiceMock);

        // act
        final Response response = authenticationResource.authenticate(authenticationData);

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
        verify(authenticationServiceMock, times(1)).validateChallenge(email, challenge);
    }

    @Test
    public void authenticateWithCorrectEmail() {
        // arrange
        final String email = "foobar@sportchef.ch";
        final String challenge = "12345-abcde";
        final String token = "valid_token";
        final AuthenticationData authenticationData = new AuthenticationData(email, challenge);
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateChallenge(email, challenge)).thenReturn(Optional.of(token));
        final AuthenticationResource authenticationResource = new AuthenticationResource(authenticationServiceMock);

        // act
        final Response response = authenticationResource.authenticate(authenticationData);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(((Entity) response.getEntity()).getEntity(), is(token));
        verify(authenticationServiceMock, times(1)).validateChallenge(email, challenge);
    }

}
