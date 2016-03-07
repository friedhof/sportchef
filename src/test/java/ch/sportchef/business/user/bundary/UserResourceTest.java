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
package ch.sportchef.business.user.bundary;

import ch.sportchef.business.user.boundary.UserResource;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.business.user.entity.UserBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserResourceTest {

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    private UserResource userResource;

    @Inject
    private UserService userServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Before
    public void setup() {
        userResource = new UserResource(1L, userServiceMock);
    }

    private User createTestUser() {
        return UserBuilder.anUser()
                .withUserId(1L)
                .withFirstName("John")
                .withLastName("Doe")
                .withPhone("+41 79 555 00 01")
                .withEmail("john.doe@sportchef.ch").build();
    }

    @Test
    public void findWithSuccess() {
        // arrange
        final User testUser = createTestUser();
        when(userServiceMock.findByUserId(1L))
                .thenReturn(Optional.of(testUser));

        // act
        final User user = userResource.find();

        // assert
        assertThat(user, is(testUser));
        verify(userServiceMock, times(1)).findByUserId(1L);
    }

    @Test(expected=NotFoundException.class)
    public void findWithNotFound() {
        // arrange
        when(userServiceMock.findByUserId(1L))
                .thenReturn(Optional.empty());

        // act
        userResource.find();
    }

    @Test
    public void updateWithSuccess() throws URISyntaxException {
        // arrange
        final User testUser = createTestUser();
        final String location = "http://localhost:8080/sportchef/api/users/1";
        final URI uri = new URI(location);

        when(userServiceMock.findByUserId(testUser.getUserId()))
                .thenReturn(Optional.of(testUser));
        when(userServiceMock.update(anyObject()))
                .thenReturn(testUser);
        when(uriInfoMock.getAbsolutePathBuilder())
                .thenReturn(uriBuilderMock);
        when(uriBuilderMock.build())
                .thenReturn(uri);

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

    @Test(expected=NotFoundException.class)
    public void updateWithNotFound() {
        // arrange
        final User testUser = createTestUser();

        when(userServiceMock.findByUserId(testUser.getUserId()))
                .thenReturn(Optional.empty());

        // act
        userResource.update(testUser, uriInfoMock);
    }

    @Test
    public void deleteWithSuccess() {
        // arrange
        final User testUser = createTestUser();

        when(userServiceMock.findByUserId(testUser.getUserId()))
                .thenReturn(Optional.of(testUser));

        // act
        final Response response = userResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        verify(userServiceMock, times(1)).findByUserId(testUser.getUserId());
    }

    @Test(expected=NotFoundException.class)
    public void deleteWithNotFound() {
        // arrange
        when(userServiceMock.findByUserId(anyObject()))
                .thenReturn(Optional.empty());

        // act
        userResource.delete();
    }

}
