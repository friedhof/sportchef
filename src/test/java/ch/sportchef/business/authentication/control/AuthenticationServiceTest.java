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
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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

    private Configuration createConfigurationMock() {
        final Configuration configurationMock = mock(Configuration.class);
        expect(configurationMock.getSMTPServer()).andReturn("localhost");
        expect(configurationMock.getSMTPPort()).andReturn(4444);
        expect(configurationMock.getSMTPUser()).andReturn("test");
        expect(configurationMock.getSMTPPassword()).andReturn("test");
        expect(configurationMock.getSMTPSSL()).andReturn(FALSE);
        expect(configurationMock.getSMTPFrom()).andReturn("noreply@sportchef.ch");
        replay(configurationMock);
        return configurationMock;
    }

    private SmtpServer createSmtpServerMock() {
        final ServerOptions smtpServerOptions = new ServerOptions();
        smtpServerOptions.port = 4444;
        final SmtpServer smtpServer = SmtpServerFactory.startServer(smtpServerOptions);
        return smtpServer;
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
    public void requestAndValidateChallenge() {
        // arrange

        // act
        final String challenge = requestChallengeOk();
        final String token = validateChallenge(challenge);

        // assert
        System.out.println(token);
    }

    private String requestChallengeOk() {
        // arrange
        final Optional<User> userOptional = Optional.of(
                new User(TEST_USER_ID, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_PHONE, TEST_USER_EMAIL));
        expect(userServiceMock.findByEmail(TEST_USER_EMAIL)).andReturn(userOptional);
        expectLastCall().times(2);
        final Configuration configurationMock = createConfigurationMock();
        expect(configurationServiceMock.getConfiguration()).andReturn(configurationMock);
        mockProvider.replayAll();
        final SmtpServer smtpServer = createSmtpServerMock();

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        smtpServer.stop();
        assertThat(smtpServer.getEmailCount(), is(1));
        assertThat(smtpServer.getMessage(0).getFirstHeaderValue("To"), is(TEST_USER_EMAIL));
        assertThat(ok, is(true));

        final String body = smtpServer.getMessage(0).getBody();
        final String challenge = body.substring(body.indexOf("=") + 2);
        return challenge;
    }

    private String validateChallenge(@NotNull final String challenge) {
        // arrange
        final Identity identityMock = mock(Identity.class);
        expect(identityMock.isLoggedIn()).andReturn(false);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        expect(credentialMock.getUserId()).andReturn(TEST_USER_EMAIL);
        expect(credentialMock.getPassword()).andReturn(challenge);
        replay(identityMock, credentialMock);

        // act
        final Optional<String> token = authenticationService.validateChallenge(identityMock, credentialMock);

        // assert
        assertThat(token, notNullValue());
        assertThat(token.isPresent(), is(true));
        mockProvider.verifyAll();

        return token.get();
    }

    @Test
    public void authenticationIsLoggedInOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        expect(identityMock.isLoggedIn()).andReturn(true);
        expect(identityMock.getAccount()).andReturn(mock(Account.class));
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        replay(identityMock, credentialMock);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(true));
        assertThat(tokenOptional.get(), is(token));
    }

    @Test
    public void authenticationIsLoggedInNotOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        expect(identityMock.isLoggedIn()).andReturn(true);
        expect(identityMock.getAccount()).andReturn(null);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        replay(identityMock, credentialMock);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(false));
    }

    @Test
    public void authenticationIsNotLoggedInOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        expect(identityMock.isLoggedIn()).andReturn(false);
        expect(identityMock.getAccount()).andReturn(mock(Account.class));
        expect(identityMock.login()).andReturn(Identity.AuthenticationResult.SUCCESS);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        credentialMock.setCredential(anyObject());
        replay(identityMock, credentialMock);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(true));
        assertThat(tokenOptional.get(), is(token));
    }

    @Test
    public void authenticationIsNotLoggedInNotOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        expect(identityMock.isLoggedIn()).andReturn(false);
        expect(identityMock.getAccount()).andReturn(null);
        expect(identityMock.login()).andReturn(Identity.AuthenticationResult.FAILED);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        credentialMock.setCredential(anyObject());
        replay(identityMock, credentialMock);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(false));
    }

    @Test
    public void logout() {
        // arrange

        // act

        // assert
    }

}