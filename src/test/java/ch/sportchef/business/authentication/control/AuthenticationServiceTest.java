package ch.sportchef.business.authentication.control;

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;
import org.needle4j.mock.EasyMockProvider;

import javax.inject.Inject;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuthenticationServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_FIRSTNAME = "John";
    private static final String TEST_USER_LASTNAME = "Doe";
    private static final String TEST_USER_PHONE = "+41 79 555 00 01";
    private static final String TEST_USER_EMAIL = "john.doe@sportchef.ch";

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest(postConstruct = true)
    private AuthenticationService authenticationService;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private UserService userServiceMock;

    @Inject
    private ConfigurationService configurationServiceMock;

    @Test
    public void requestChallengeOk() {
        // arrange
        final Optional<User> userOptional = Optional.of(
                new User(TEST_USER_ID, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_PHONE, TEST_USER_EMAIL));
        expect(userServiceMock.findByEmail(TEST_USER_EMAIL)).andReturn(userOptional);
        final Configuration configurationMock = mock(Configuration.class);
        expect(configurationMock.getSMTPServer()).andReturn("localhost");
        expect(configurationMock.getSMTPPort()).andReturn(4444);
        expect(configurationMock.getSMTPUser()).andReturn("test");
        expect(configurationMock.getSMTPPassword()).andReturn("test");
        expect(configurationMock.getSMTPSSL()).andReturn(FALSE);
        expect(configurationMock.getSMTPFrom()).andReturn("noreply@sportchef.ch");
        expect(configurationServiceMock.getConfiguration()).andReturn(configurationMock);
        replay(configurationMock);
        mockProvider.replayAll();
        final ServerOptions smtpServerOptions = new ServerOptions();
        smtpServerOptions.port = 4444;
        final SmtpServer smtpServer = SmtpServerFactory.startServer(smtpServerOptions);

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        smtpServer.stop();
        assertThat(smtpServer.getEmailCount(), is(1));
        assertThat(smtpServer.getMessage(0).getFirstHeaderValue("To"), is(TEST_USER_EMAIL));
        assertThat(ok, is(true));
        mockProvider.verifyAll();
    }

    @Test
    public void requestChallengeNotOk() {
        // arrange
        expect(userServiceMock.findByEmail(TEST_USER_EMAIL)).andReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        assertThat(ok, is(false));
        mockProvider.verifyAll();
    }

    @Test
    public void validateChallenge() {
        // arrange

        // act

        // assert
    }

    @Test
    public void authentication() {
        // arrange

        // act

        // assert
    }

    @Test
    public void generateToken() {
        // arrange

        // act

        // assert
    }

    @Test
    public void logout() {
        // arrange

        // act

        // assert
    }

}