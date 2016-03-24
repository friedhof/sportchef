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
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static ch.sportchef.hamcrest.matcher.PatternMatcher.matchesPattern;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
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
        return User.builder()
                .userId(TEST_USER_ID)
                .firstName(TEST_USER_FIRSTNAME)
                .lastName(TEST_USER_LASTNAME)
                .phone(TEST_USER_PHONE)
                .email(TEST_USER_EMAIL)
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
        validateChallenge(requestChallenge());

        // assert
    }

    private String requestChallenge() {
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

    private void validateChallenge(@NotNull final String challenge) {
        // arrange

        // act
        final Optional<String> token = authenticationService.validateChallenge(TEST_USER_EMAIL, challenge);

        // assert
        assertThat(token, notNullValue());
        assertThat(token.isPresent(), is(true));
        assertThat(token.get(), matchesPattern(".{20}\\..{42}\\..{342}"));
    }

}