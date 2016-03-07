package ch.sportchef.business.authentication.control;

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.business.user.entity.UserBuilder;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.Account;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_FIRSTNAME = "John";
    private static final String TEST_USER_LASTNAME = "Doe";
    private static final String TEST_USER_PHONE = "+41 79 555 00 01";
    private static final String TEST_USER_EMAIL = "john.doe@sportchef.ch";

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest(postConstruct = true)
    private AuthenticationService authenticationService;

    @Inject
    private UserService userServiceMock;

    @Inject
    private ConfigurationService configurationServiceMock;

    private User createTestUser() {
        return UserBuilder.anUser()
                .withUserId(TEST_USER_ID)
                .withFirstName(TEST_USER_FIRSTNAME)
                .withLastName(TEST_USER_LASTNAME)
                .withPhone(TEST_USER_PHONE)
                .withEmail(TEST_USER_EMAIL)
                .build();
    }

    private Configuration createConfigurationMock() {
        final Configuration configurationMock = mock(Configuration.class);
        when(configurationMock.getSMTPServer())
                .thenReturn("localhost");
        when(configurationMock.getSMTPPort())
                .thenReturn(4444);
        when(configurationMock.getSMTPUser())
                .thenReturn("test");
        when(configurationMock.getSMTPPassword())
                .thenReturn("test");
        when(configurationMock.getSMTPSSL())
                .thenReturn(FALSE);
        when(configurationMock.getSMTPFrom())
                .thenReturn("noreply@sportchef.ch");
        return configurationMock;
    }

    private SmtpServer createSmtpServerMock() {
        final ServerOptions smtpServerOptions = new ServerOptions();
        smtpServerOptions.port = 4444;
        return SmtpServerFactory.startServer(smtpServerOptions);
    }

    @Test
    public void requestChallengeNotOk() {
        // arrange
        when(userServiceMock.findByEmail(TEST_USER_EMAIL))
                .thenReturn(Optional.empty());

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        assertThat(ok, is(false));
        verify(userServiceMock, times(1)).findByEmail(TEST_USER_EMAIL);
    }

    @Test
    public void requestAndValidateChallenge() {
        // arrange

        // act
        final String challenge = requestChallengeOk();
        final String token = validateChallenge(challenge);

        // assert
        assertThat(token.matches(".{20}\\..{38}\\..{86}"), is(true));
    }

    private String requestChallengeOk() {
        // arrange
        final Optional<User> userOptional = Optional.of(createTestUser());
        when(userServiceMock.findByEmail(TEST_USER_EMAIL))
                .thenReturn(userOptional);
        final Configuration configurationMock = createConfigurationMock();
        when(configurationServiceMock.getConfiguration())
                .thenReturn(configurationMock);
        final SmtpServer smtpServer = createSmtpServerMock();

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        smtpServer.stop();
        assertThat(smtpServer.getEmailCount(), is(1));
        assertThat(smtpServer.getMessage(0).getFirstHeaderValue("To"), is(TEST_USER_EMAIL));
        assertThat(ok, is(true));
        verify(userServiceMock, times(1)).findByEmail(TEST_USER_EMAIL);

        final String body = smtpServer.getMessage(0).getBody();
        final String challenge = body.substring(body.indexOf("=") + 2);
        return challenge;
    }

    private String validateChallenge(@NotNull final String challenge) {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(false);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        when(credentialMock.getUserId())
                .thenReturn(TEST_USER_EMAIL);
        when(credentialMock.getPassword())
                .thenReturn(challenge);

        // act
        final Optional<String> token = authenticationService.validateChallenge(identityMock, credentialMock);

        // assert
        assertThat(token, notNullValue());
        assertThat(token.isPresent(), is(true));
        verify(credentialMock, times(1)).getUserId();
        verify(credentialMock, times(1)).getPassword();

        return token.get();
    }

    @Test
    public void authenticationIsLoggedInOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(true);
        when(identityMock.getAccount())
                .thenReturn(mock(Account.class));
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(true));
        assertThat(tokenOptional.get(), is(token));
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, times(1)).getAccount();
    }

    @Test
    public void authenticationIsLoggedInNotOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(true);
        when(identityMock.getAccount())
                .thenReturn(null);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(false));
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, times(1)).getAccount();
    }

    @Test
    public void authenticationIsNotLoggedInOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(false);
        when(identityMock.getAccount())
                .thenReturn(mock(Account.class));
        when(identityMock.login())
                .thenReturn(Identity.AuthenticationResult.SUCCESS);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(true));
        assertThat(tokenOptional.get(), is(token));
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, times(1)).getAccount();
        verify(identityMock, times(1)).login();
        verify(credentialMock, times(1)).setCredential(anyObject());
    }

    @Test
    public void authenticationIsNotLoggedInNotOk() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(false);
        when(identityMock.getAccount())
                .thenReturn(null);
        when(identityMock.login())
                .thenReturn(Identity.AuthenticationResult.FAILED);
        final DefaultLoginCredentials credentialMock = mock(DefaultLoginCredentials.class);
        final String token = "1234567890";

        // act
        final Optional<String> tokenOptional = authenticationService.authentication(identityMock, credentialMock, token);

        // assert
        assertThat(tokenOptional.isPresent(), is(false));
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, times(1)).getAccount();
        verify(identityMock, times(1)).login();
        verify(credentialMock, times(1)).setCredential(anyObject());
    }

    @Test
    public void generateTokenOk() {
        // arrange
        final Optional<User> userOptional = Optional.of(createTestUser());
        when(userServiceMock.findByEmail(TEST_USER_EMAIL))
                .thenReturn(userOptional);

        // act
        final Optional<String> tokenOptional = authenticationService.generateToken(TEST_USER_EMAIL);

        // assert
        assertThat(tokenOptional.isPresent(), is(true));
        assertThat(tokenOptional.get(), notNullValue());
        verify(userServiceMock, times(1)).findByEmail(TEST_USER_EMAIL);
    }

    @Test
    public void generateTokenNotOk() {
        // arrange
        when(userServiceMock.findByEmail(TEST_USER_EMAIL))
                .thenReturn(Optional.empty());

        // act
        final Optional<String> tokenOptional = authenticationService.generateToken(TEST_USER_EMAIL);

        // assert
        assertThat(tokenOptional.isPresent(), is(false));
        verify(userServiceMock, times(1)).findByEmail(TEST_USER_EMAIL);
    }

    @Test
    public void logoutIsLoggedIn() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(true);

        // act
        authenticationService.logout(identityMock);

        // assert
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, times(1)).logout();
    }

    @Test
    public void logoutIsNotLoggedIn() {
        // arrange
        final Identity identityMock = mock(Identity.class);
        when(identityMock.isLoggedIn())
                .thenReturn(false);

        // act
        authenticationService.logout(identityMock);

        // assert
        verify(identityMock, times(1)).isLoggedIn();
        verify(identityMock, never()).logout();
    }

}