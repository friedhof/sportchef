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

import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationResponseFilterTest {

    private final AuthenticationResponseFilter authenticationResponseFilter = new AuthenticationResponseFilter();

    @Test
    public void filterWithSuccess() {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getProperty("auth-failed")).thenReturn(null);
        when(requestContextMock.getHeaderString("Authorization")).thenReturn("Bearer valid_token");
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        final ContainerResponseContext responseContextMock = mock(ContainerResponseContext.class);
        when(responseContextMock.getHeaders()).thenReturn(headers);

        // act
        authenticationResponseFilter.filter(requestContextMock, responseContextMock);

        // assert
        assertThat(headers.size(), is(1));
        assertThat(headers.getFirst("Token"), is("valid_token"));
    }

    @Test
    public void filterWithFailed() {
        // arrange
        final ContainerRequestContext requestContextMock = mock(ContainerRequestContext.class);
        when(requestContextMock.getProperty("auth-failed")).thenReturn(TRUE);
        final ContainerResponseContext responseContextMock = mock(ContainerResponseContext.class);

        // act
        authenticationResponseFilter.filter(requestContextMock, responseContextMock);

        // assert
    }

}