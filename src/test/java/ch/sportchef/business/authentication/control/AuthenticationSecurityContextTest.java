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

import ch.sportchef.business.authentication.entity.Role;
import ch.sportchef.business.user.entity.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationSecurityContextTest {

    private static final String TEST_EMAIL = "foo@bar";
    private static final Role TEST_ROLE_USER = Role.USER;
    private static final Role TEST_ROLE_ADMIN = Role.ADMIN;
    private static final String TEST_AUTHENTICATION_SCHEME = "test_authentication_scheme";

    private AuthenticationSecurityContext authenticationSecurityContext;

    @Before
    public void setUp() {
        final User user = User.builder().email(TEST_EMAIL).build();

        final SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.isSecure()).thenReturn(true);
        when(securityContextMock.getAuthenticationScheme()).thenReturn(TEST_AUTHENTICATION_SCHEME);

        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.isUserInRole(user, TEST_ROLE_USER)).thenReturn(true);
        when(authenticationServiceMock.isUserInRole(user, TEST_ROLE_ADMIN)).thenReturn(false);

        authenticationSecurityContext = new AuthenticationSecurityContext(
                authenticationServiceMock, securityContextMock, user);
    }

    @Test
    public void getUserPrincipal() {
        // arrange

        // act
        final Principal userPrincipal = authenticationSecurityContext.getUserPrincipal();

        // assert
        assertThat(userPrincipal.getName(), is(TEST_EMAIL));
    }

    @Test
    public void isUserInRoleUser() {
        // arrange

        // act
        final boolean userInRole = authenticationSecurityContext.isUserInRole(TEST_ROLE_USER.toString());

        // assert
        assertThat(userInRole, is(true));
    }

    @Test
    public void isUserInRoleAdmin() {
        // arrange

        // act
        final boolean userInRole = authenticationSecurityContext.isUserInRole(TEST_ROLE_ADMIN.toString());

        // assert
        assertThat(userInRole, is(false));
    }

    @Test
    public void isSecure() {
        // arrange

        // act
        final boolean secure = authenticationSecurityContext.isSecure();

        // assert
        assertThat(secure, is(true));
    }

    @Test
    public void getAuthenticationScheme() {
        // arrange

        // act
        final String authenticationScheme = authenticationSecurityContext.getAuthenticationScheme();

        // assert
        assertThat(authenticationScheme, is(TEST_AUTHENTICATION_SCHEME));
    }
}