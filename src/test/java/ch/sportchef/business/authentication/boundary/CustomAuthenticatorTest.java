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
package ch.sportchef.business.authentication.boundary;

import ch.sportchef.business.authentication.entity.SimpleTokenCredential;
import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.Account;

import javax.inject.Inject;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.picketlink.authentication.Authenticator.AuthenticationStatus.FAILURE;
import static org.picketlink.authentication.Authenticator.AuthenticationStatus.SUCCESS;

public class CustomAuthenticatorTest {

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    @ObjectUnderTest
    private CustomAuthenticator customAuthenticator;

    @Inject
    private DefaultLoginCredentials credentialsMock;

    @Inject
    private UserService userServiceMock;

    @Test
    public void authenticateWithEmptyCredential() {
        // arrange
        when(credentialsMock.getCredential()).thenReturn(null);

        // act
        customAuthenticator.authenticate();

        // assert
        assertThat(customAuthenticator.getStatus(), is(FAILURE));
    }

    @Test
    public void authenticateWithInvalidToken() {
        // arrange
        final String token = "invalid_token";
        final SimpleTokenCredential customCredential = mock(SimpleTokenCredential.class);
        when(customCredential.getToken()).thenReturn(token);
        when(credentialsMock.getCredential()).thenReturn(customCredential);

        // act
        customAuthenticator.authenticate();

        // assert
        assertThat(customAuthenticator.getStatus(), is(FAILURE));
    }

    @Test
    public void authenticateWithValidTokenWithoutEmail() {
        // arrange
        final String token = "valid_token";
        final SimpleTokenCredential customCredential = mock(SimpleTokenCredential.class);
        when(customCredential.getToken()).thenReturn(token);
        when(credentialsMock.getCredential()).thenReturn(customCredential);
        when(credentialsMock.getUserId()).thenReturn(null);

        // act
        customAuthenticator.authenticate();

        // assert
        assertThat(customAuthenticator.getStatus(), is(FAILURE));
    }

    @Test
    public void authenticateWithValidTokenWithEmptyEmail() {
        // arrange
        final String token = "valid_token";
        final SimpleTokenCredential customCredential = mock(SimpleTokenCredential.class);
        when(customCredential.getToken()).thenReturn(token);
        when(credentialsMock.getCredential()).thenReturn(customCredential);
        when(credentialsMock.getUserId()).thenReturn("  ");

        // act
        customAuthenticator.authenticate();

        // assert
        assertThat(customAuthenticator.getStatus(), is(FAILURE));
    }

    @Test
    public void authenticateWithValidTokenWithInvalidEmail() {
        // arrange
        final String token = "valid_token";
        final String email = "john.doe@sportchef.ch";
        final SimpleTokenCredential customCredential = mock(SimpleTokenCredential.class);
        when(customCredential.getToken()).thenReturn(token);
        when(credentialsMock.getCredential()).thenReturn(customCredential);
        when(credentialsMock.getUserId()).thenReturn(email);
        when(userServiceMock.findByEmail(email)).thenReturn(Optional.empty());

        // act
        customAuthenticator.authenticate();

        // assert
        assertThat(customAuthenticator.getStatus(), is(FAILURE));
    }

    @Test
    public void authenticateWithValidTokenWithValidEmail() {
        // arrange
        final String token = "valid_token";
        final Long userId = 1L;
        final String firstName = "John";
        final String lastName = "Doe";
        final String email = "john.doe@sportchef.ch";
        final SimpleTokenCredential customCredential = mock(SimpleTokenCredential.class);
        final User user = User.builder()
                .userId(1L)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        when(customCredential.getToken()).thenReturn(token);
        when(credentialsMock.getCredential()).thenReturn(customCredential);
        when(credentialsMock.getUserId()).thenReturn(email);
        when(userServiceMock.findByEmail(email)).thenReturn(Optional.of(user));

        // act
        customAuthenticator.authenticate();

        // assert
        final Account account = customAuthenticator.getAccount();
        final org.picketlink.idm.model.basic.User plUser = (org.picketlink.idm.model.basic.User) account;
        assertThat(plUser.getLoginName(), is(userId.toString()));
        assertThat(plUser.getFirstName(), is(firstName));
        assertThat(plUser.getLastName(), is(lastName));
        assertThat(plUser.getEmail(), is(email));
        assertThat(customAuthenticator.getStatus(), is(SUCCESS));
    }
}