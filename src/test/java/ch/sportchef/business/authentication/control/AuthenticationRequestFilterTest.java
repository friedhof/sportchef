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
package ch.sportchef.business.authentication.control;

import ch.sportchef.business.user.entity.User;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationRequestFilterTest {

    @Test
    public void filterAuthenticationSuccessful() throws IOException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("Bearer valid_token");
        when(requestContextMock.getSecurityContext()).thenReturn(mock(SecurityContext.class));
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validate("valid_token")).thenReturn(Optional.of(User.builder().build()));
        final AuthenticationRequestFilter authenticationRequestFilter = new AuthenticationRequestFilter(authenticationServiceMock);

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(1)).setSecurityContext(any(AuthenticationSecurityContext.class));
        verify(requestContextMock, times(0)).abortWith(any());
    }

    @Test
    public void filterInvalidTokenData() throws IOException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("Bearer invalid_token_data");
        when(requestContextMock.getSecurityContext()).thenReturn(mock(SecurityContext.class));
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validate("invalid_token_data")).thenReturn(Optional.empty());
        final AuthenticationRequestFilter authenticationRequestFilter = new AuthenticationRequestFilter(authenticationServiceMock);

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(0)).setSecurityContext(any());
        verify(requestContextMock, times(1)).abortWith(any());
    }

    @Test
    public void filterInvalidAuthenticationHeader() throws IOException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("invalid_authentication_header");
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        final AuthenticationRequestFilter authenticationRequestFilter = new AuthenticationRequestFilter(authenticationServiceMock);

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(0)).setSecurityContext(any());
        verify(requestContextMock, times(1)).abortWith(any());
    }

}