package ch.sportchef.business.user.bundary;

import ch.sportchef.business.user.boundary.UserService;
import ch.sportchef.business.user.boundary.UsersResource;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.test.UnitTests;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
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
    public void saveUserWithSuccess() throws URISyntaxException {
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

}
