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

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
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

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest
    private AuthenticationRequestFilter authenticationRequestFilter;

    @Inject
    private AuthenticationService authenticationService;

    @Test
    public void filterAuthenticationSuccessful() throws IOException, InvalidJwtException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("Bearer valid_token");
        when(requestContextMock.getSecurityContext()).thenReturn(mock(SecurityContext.class));
        when(authenticationService.validate("valid_token")).thenReturn(Optional.of("foo@bar"));

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(1)).setSecurityContext(any(AuthenticationSecurityContext.class));
        verify(requestContextMock, times(0)).abortWith(any());
    }

    @Test
    public void filterInvalidTokenData() throws IOException, InvalidJwtException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("Bearer invalid_token_data");
        when(requestContextMock.getSecurityContext()).thenReturn(mock(SecurityContext.class));
        when(authenticationService.validate("invalid_token_data")).thenReturn(Optional.empty());

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(0)).setSecurityContext(any());
        verify(requestContextMock, times(1)).abortWith(any());
    }

    @Test
    public void filterInvalidAuthenticationHeader() throws IOException, InvalidJwtException {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("invalid_authentication_header");

        // act
        authenticationRequestFilter.filter(requestContextMock);

        // assert
        verify(requestContextMock, times(0)).setSecurityContext(any());
        verify(requestContextMock, times(1)).abortWith(any());
    }

}