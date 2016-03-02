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

import ch.sportchef.business.exception.ExpectationFailedException;
import ch.sportchef.business.user.boundary.UserResource;
import ch.sportchef.business.user.boundary.UsersResource;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;
import org.needle4j.mock.EasyMockProvider;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsersResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest
    private UsersResource usersResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private UserService userServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Test
    public void saveWithSuccess() throws URISyntaxException {
        // arrange
        final User userToCreate = new User(0L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        final User savedUser = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        final String location = "http://localhost:8080/sportchef/api/users/1";
        final URI uri = new URI(location);

        expect(userServiceMock.create(userToCreate)).andStubReturn(savedUser);
        expect(uriInfoMock.getAbsolutePathBuilder()).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.path(anyString())).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.build()).andStubReturn(uri);
        mockProvider.replayAll();

        // act
        final Response response = usersResource.save(userToCreate, uriInfoMock);

        //assert
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        mockProvider.verifyAll();
    }

    @Test(expected=ExpectationFailedException.class)
    public void saveWithExpectationFailed() {
        // arrange
        final User userToCreate = new User(0L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        expect(userServiceMock.create(userToCreate))
                .andStubThrow(new ExpectationFailedException("Email address has to be unique"));
        mockProvider.replayAll();

        // act
        usersResource.save(userToCreate, null);
    }

    @Test
    public void findAll() {
        // arrange
        final User user1 = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        final User user2 = new User(2L, "Jane", "Doe", "+41 79 555 00 02", "jane.doe@sportchef.ch");
        final List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        expect(userServiceMock.findAll()).andStubReturn(users);
        mockProvider.replayAll();

        // act
        final Response response = usersResource.findAll();
        final List<User> list = (List<User>) response.getEntity();
        final User responseUser1 = list.get(0);
        final User responseUser2 = list.get(1);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(responseUser1, is(user1));
        assertThat(responseUser2, is(user2));
        mockProvider.verifyAll();
    }

    @Test
    public void find() {
        // arrange
        final long userId = 1L;

        // act
        final UserResource userResource = usersResource.find(userId);

        // assert
        assertThat(userResource, notNullValue());
    }
}
