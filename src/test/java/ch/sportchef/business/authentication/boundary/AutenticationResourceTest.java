package ch.sportchef.business.authentication.boundary;

import ch.sportchef.business.user.entity.User;
import ch.sportchef.test.UnitTests;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.apache.commons.mail.EmailException;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
public class AutenticationResourceTest {

    private static final String TEST_USER_EMAIL = "auth.test@sportchef.ch";

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest
    private AuthenticationResource authenticationResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private AuthenticationService authenticationServiceMock;

    @Test
    public void requestChallengeWithBadRequest() throws EmailException {
        // arrange
        final String email = "";

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void requestChallengeWithNotFound() throws EmailException {
        // arrange
        final String email = "foobar@sportchef.ch";

        // act
        final Response response = authenticationResource.requestChallenge(email);

        //assert
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void requestChallengeWithSuccess() throws EmailException {
        // arrange
        final User testUser = new User(0L, "AuthTest", "AuthTest", "AuthTest", TEST_USER_EMAIL);
        EasyMock.expect(authenticationServiceMock.requestChallenge(TEST_USER_EMAIL)).andReturn(true);
        mockProvider.replayAll();

        // act
        final Response response = authenticationResource.requestChallenge(TEST_USER_EMAIL);

        //assert
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test
    public void authenticateWithWrongEmail() {
        // arrange
        final DefaultLoginCredentials credential = new DefaultLoginCredentials();
        credential.setUserId("foo@bar.ch");
        credential.setPassword("12345-abcde");
        EasyMock.expect(authenticationServiceMock.validateChallenge(anyObject(), eq(credential)))
                .andReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        final Response response = authenticationResource.authenticate(credential);

        //assert
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
        mockProvider.verifyAll();
    }

}
