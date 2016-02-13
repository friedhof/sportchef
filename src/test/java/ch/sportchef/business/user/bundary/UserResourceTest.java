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
import java.util.Optional;

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

        // assert
        mockProvider.verifyAll();
    }

}
