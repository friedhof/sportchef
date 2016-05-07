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
package ch.sportchef.business.authentication.control;

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.mail.EmailException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

import static ch.sportchef.business.authentication.entity.Role.ADMIN;
import static ch.sportchef.business.authentication.entity.Role.USER;
import static ch.sportchef.hamcrest.matcher.PatternMatcher.matchesPattern;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AuthenticationServiceTest {

    private static final String MALFORMED_TOKEN = "eyJhbGciOiJIUzUxMiJ9_eyJpYXQiOjE0NjE3ODc4NDEsImV4cCI6MTQ2MTc4ODQ0MSwiZW1haWwiOiJqb2huLmRvZUBzcG9ydGNoZWYuY2gifQ_8MJW7kRJYrc105JmorJgOA3Wn6z2Z5Ab0uteZsleKyJBLAibhDr35S0PH9trTwxYeocpIHVhQ-lCak_IVNdwSg";
    private static final String SIGNATURE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE0NjE3ODc4NDEsImV4cCI6MTQ2MTc4ODQ0MSwiZW1haWwiOiJqb2huLmRvZUBzcG9ydGNoZWYuY2gifQ.8MJW7kRJYrc105JmorJgOA3Wn6z2Z5Ab0uteZsleKyJBLAibhDr35S0PH9trTwxYeocpIHVhQ-lCak_IVNdwSg";

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_FIRSTNAME = "John";
    private static final String TEST_USER_LASTNAME = "Doe";
    private static final String TEST_USER_PHONE = "+41 79 555 00 01";
    private static final String TEST_USER_EMAIL = "john.doe@sportchef.ch";

    private UserService userServiceMock;
    private ConfigurationService configurationServiceMock;
    private SmtpServer smtpServer;

    @Before
    public void setup() {
        final User testUser = User.builder()
                .userId(TEST_USER_ID)
                .firstName(TEST_USER_FIRSTNAME)
                .lastName(TEST_USER_LASTNAME)
                .phone(TEST_USER_PHONE)
                .email(TEST_USER_EMAIL)
                .build();

        userServiceMock = mock(UserService.class);
        when(userServiceMock.findByEmail(anyString())).thenAnswer(x -> Optional.of(testUser));
        configurationServiceMock = mock(ConfigurationService.class);
        when(configurationServiceMock.getConfiguration()).thenAnswer(x -> createConfigurationMock());

        final ServerOptions smtpServerOptions = new ServerOptions();
        smtpServerOptions.port = 4444;
        smtpServer = SmtpServerFactory.startServer(smtpServerOptions);
    }

    @After
    public void tearDown() {
        smtpServer.stop();
    }

    private Configuration createConfigurationMock() {
        final Configuration configurationMock = mock(Configuration.class);
        when(configurationMock.getTokenSigningKey()).thenReturn("This is a Mock!");
        when(configurationMock.getSMTPServer()).thenReturn("localhost");
        when(configurationMock.getSMTPPort()).thenReturn(4444);
        when(configurationMock.getSMTPUser()).thenReturn("test");
        when(configurationMock.getSMTPPassword()).thenReturn("test");
        when(configurationMock.getSMTPSSL()).thenReturn(FALSE);
        when(configurationMock.getSMTPFrom()).thenReturn("noreply@sportchef.ch");
        return configurationMock;
    }

    @Test
    public void requestChallengeNotOk() {
        // arrange
        when(userServiceMock.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.empty());
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        assertThat(ok, is(false));
        verify(userServiceMock, times(1)).findByEmail(TEST_USER_EMAIL);
    }

    @Test(expected = MalformedJwtException.class)
    public void validateMalformedToken() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        authenticationService.validate(MALFORMED_TOKEN);

        // assert
    }

    @Test(expected = SignatureException.class)
    public void validateSignatureToken() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        authenticationService.validate(SIGNATURE_TOKEN);

        // assert
    }

    @Test(expected = ExpiredJwtException.class)
    public void validateExpiredToken() {
        // arrange
        final Date now = new Date();
        final Date exp = new Date(now.getTime() - 1000);
        final Claims claims = Jwts.claims();
        claims.setExpiration(exp);
        final String tokenSigningKey = configurationServiceMock.getConfiguration().getTokenSigningKey();
        final String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
                .compact();
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        authenticationService.validate(token);

        // assert
    }

    @Test
    public void requestAndValidateChallengeAndToken() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        validateToken(validateChallenge(authenticationService, requestChallenge(authenticationService)));

        // assert
        assertThat(smtpServer.getEmailCount(), is(1));
        verify(userServiceMock, times(2)).findByEmail(TEST_USER_EMAIL);
    }

    @Test
    public void typoWhileLoginDoesNotLogin() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);
        requestChallenge(authenticationService);

        // act
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, "wrongChallenge");

        // assert
        assertThat(token.isPresent(), is(false));
    }

    @Test
    public void loginWithoutChallengeRequestedDoesNotLogin() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, "anyChallenge");

        // assert
        assertThat(token.isPresent(), is(false));
    }

    @Test
    public void make1TypoWhileLoggingInStillWorks() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);
        final String correctChallenge = requestChallenge(authenticationService);

        // act
        authenticationService.validateChallenge(TEST_USER_EMAIL, "wrongChallenge");
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, correctChallenge);

        // assert
        assertThat(token.get(), matchesPattern(".{20}\\..{90}\\..{86}"));
    }

    @Test
    public void make10TyposWhileLoggingInDisablesTheChallenge() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);
        final String correctChallenge = requestChallenge(authenticationService);

        // act
        for (int i = 0; i < 10; i++) {
            authenticationService.validateChallenge(TEST_USER_EMAIL, "wrongChallenge");
        }
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, correctChallenge);

        // assert
        assertThat(token.isPresent(), is(false));
    }

    @Test(expected = EmailException.class)
    public void requestChallengeWithException() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        authenticationService.requestChallenge("@test");
    }

    private String requestChallenge(@NotNull final AuthenticationService authenticationService) {
        // arrange

        // act
        final boolean ok = authenticationService.requestChallenge(TEST_USER_EMAIL);

        // assert
        assertThat(ok, is(true));
        final MailMessage newestMessage = smtpServer.getMessage(smtpServer.getMessages().length - 1);
        assertThat(newestMessage.getFirstHeaderValue("To"), is(TEST_USER_EMAIL));

        final String body = newestMessage.getBody();
        return body.substring(body.indexOf("=") + 2);
    }

    private String validateChallenge(@NotNull final AuthenticationService authenticationService,
                                     @NotNull final String challenge) {
        // arrange

        // act
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, challenge);

        // assert
        assertThat(token.get(), matchesPattern(".{20}\\..{90}\\..{86}"));

        return token.get();
    }

    private void validateToken(@NotNull final String token) {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final Optional<User> userOptional = authenticationService.validate(token);

        // assert
        assertThat(userOptional, notNullValue());
        assertThat(userOptional.isPresent(), is(true));
        assertThat(userOptional.get().getEmail(), is(TEST_USER_EMAIL));
    }

    @Test
    public void isUserInRole() {
        // arrange
        final User user = User.builder().role(USER).build();
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final boolean isUserInRoleUser = authenticationService.isUserInRole(user, USER);
        final boolean isUserInRoleAdmin = authenticationService.isUserInRole(user, ADMIN);

        // assert
        assertThat(isUserInRoleUser, is(true));
        assertThat(isUserInRoleAdmin, is(false));
    }

    @Test
    public void isUserInAdmin() {
        // arrange
        final User admin = User.builder().role(ADMIN).build();
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final boolean isUserInRoleUser = authenticationService.isUserInRole(admin, USER);
        final boolean isUserInRoleAdmin = authenticationService.isUserInRole(admin, ADMIN);

        // assert
        assertThat(isUserInRoleUser, is(true));
        assertThat(isUserInRoleAdmin, is(true));
    }

    @Test
    public void shortChallengeIfNoActivityOngoing() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);

        // act
        final String challenge = requestChallenge(authenticationService);

        // assert
        assertThat(challenge.length(), equalTo(5)); //(26+26+10)^5=916132832
    }

    @Test
    public void longChallengeIfActivityOngoing() {
        // arrange
        final AuthenticationService authenticationService = new AuthenticationService(userServiceMock, configurationServiceMock);
        for (int i = 0; i < 100; i++) {
            authenticationService.requestChallenge(i + TEST_USER_EMAIL);
        }

        // act
        final String challenge = requestChallenge(authenticationService);

        // assert
        assertThat(challenge.length(), equalTo(10)); //(26+26+10)^10=8.39E17
    }

}