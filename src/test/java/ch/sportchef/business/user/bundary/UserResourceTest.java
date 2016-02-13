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
import ch.sportchef.business.user.boundary.UserService;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.test.UnitTests;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
public class UserResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    private UserResource userResource;

    @Inject
    private EasyMockProvider mockProvider;

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

    @Test
    public void findWithSuccess() {
        // arrange
        final User testUser = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        expect(userServiceMock.findByUserId(anyObject())).andStubReturn(Optional.of(testUser));
        mockProvider.replayAll();

        // act
        final User user = userResource.find();

        // assert
        assertThat(user, is(testUser));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void findWithNotFound() {
        // arrange
        expect(userServiceMock.findByUserId(anyObject())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        final User user = userResource.find();
    }

    @Test
    public void updateWithSuccess() throws URISyntaxException {
        // arrange
        final User testUser = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");
        final String location = "http://localhost:8080/sportchef/api/users/1";
        final URI uri = new URI(location);

        expect(userServiceMock.findByUserId(testUser.getUserId())).andStubReturn(Optional.of(testUser));
        expect(userServiceMock.update(anyObject())).andStubReturn(testUser);
        expect(uriInfoMock.getAbsolutePathBuilder()).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.build()).andStubReturn(uri);
        mockProvider.replayAll();

        // act
        final Response response = userResource.update(testUser, uriInfoMock);
        final User user = (User) response.getEntity();

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertThat(user, is(testUser));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void updateWithNotFound() {
        // arrange
        final User testUser = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");

        expect(userServiceMock.findByUserId(testUser.getUserId())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        final Response response = userResource.update(testUser, uriInfoMock);
    }

    @Test
    public void deleteWithSuccess() {
        // arrange
        final User testUser = new User(1L, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch");

        expect(userServiceMock.findByUserId(testUser.getUserId())).andStubReturn(Optional.of(testUser));
        mockProvider.replayAll();

        // act
        final Response response = userResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void deleteWithNotFound() {
        // arrange
        expect(userServiceMock.findByUserId(anyObject())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        final Response response = userResource.delete();
    }

}
