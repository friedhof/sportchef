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
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ConcurrentModificationException;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    private User createTestUser() {
        return User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .phone("+41 79 555 00 01")
                .email("john.doe@sportchef.ch")
                .build();
    }

    @Test
    public void findWithSuccess() {
        // arrange
        final User testUser = createTestUser();
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(1L)).thenReturn(Optional.of(testUser));
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act
        final User user = userResource.find();

        // assert
        assertThat(user, is(testUser));
        verify(userServiceMock, times(1)).findByUserId(1L);
    }

    @Test
    public void findWithNotFound() {
        // arrange
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(1L)).thenReturn(Optional.empty());
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act & assert
        assertThrows(NotFoundException.class,
                () -> userResource.find());
    }

    @Test
    public void updateWithSuccess() throws URISyntaxException {
        // arrange
        final User testUser = createTestUser();
        final UriInfo uriInfoMock = mock(UriInfo.class);
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(userServiceMock.update(anyObject())).thenReturn(testUser);
        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        final String location = "http://localhost:8080/sportchef/api/users/1";
        final URI uri = new URI(location);
        when(uriBuilderMock.build()).thenReturn(uri);
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act
        final Response response = userResource.update(testUser, uriInfoMock);
        final User user = (User) response.getEntity();

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertThat(user, is(testUser));
        verify(userServiceMock, times(1)).findByUserId(testUser.getUserId());
        verify(userServiceMock, times(1)).update(anyObject());
        verify(uriInfoMock, times(1)).getAbsolutePathBuilder();
        verify(uriBuilderMock, times(1)).build();
    }

    @Test
    public void updateWithNotFound() {
        // arrange
        final User testUser = createTestUser();
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(1L)).thenReturn(Optional.empty());
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act & assert
        assertThrows(NotFoundException.class,
                () -> userResource.update(testUser, null));
    }

    @Test
    public void updateWithConflict() {
        // arrange
        final User testUser = createTestUser();
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(1L)).thenReturn(Optional.of(testUser));
        when(userServiceMock.update(anyObject())).thenThrow(new ConcurrentModificationException());
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act & assert
        assertThrows(ConcurrentModificationException.class,
                () -> userResource.update(testUser, null));
    }

    @Test
    public void deleteWithSuccess() {
        // arrange
        final User testUser = createTestUser();
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act
        final Response response = userResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        verify(userServiceMock, times(1)).findByUserId(testUser.getUserId());
    }

    @Test
    public void deleteWithNotFound() {
        // arrange
        final UserService userServiceMock = mock(UserService.class);
        when(userServiceMock.findByUserId(anyObject())).thenReturn(Optional.empty());
        final UserResource userResource =  new UserResource(1L, userServiceMock);

        // act & assert
        assertThrows(NotFoundException.class,
                () -> userResource.delete());
    }

}
