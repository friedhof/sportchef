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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.SecurityContext;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationSecurityContextTest {

    private static final String TEST_EMAIL = "foo@bar";
    private static final String TEST_ROLE_TRUE = "test_role_true";
    private static final String TEST_ROLE_FALSE = "test_role_false";
    private static final String TEST_AUTHENTICATION_SCHEME = "test_authentication_scheme";

    private static AuthenticationSecurityContext authenticationSecurityContext;

    @BeforeClass
    public static void setUp() {
        final SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.isUserInRole(TEST_ROLE_TRUE)).thenReturn(true);
        when(securityContextMock.isUserInRole(TEST_ROLE_FALSE)).thenReturn(false);
        when(securityContextMock.isSecure()).thenReturn(true);
        when(securityContextMock.getAuthenticationScheme()).thenReturn(TEST_AUTHENTICATION_SCHEME);
        authenticationSecurityContext = new AuthenticationSecurityContext(securityContextMock, TEST_EMAIL);
    }

    @AfterClass
    public static void tearDown() {
        authenticationSecurityContext = null;
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
    public void isUserInRoleTrue() {
        // arrange

        // act
        final boolean userInRole = authenticationSecurityContext.isUserInRole(TEST_ROLE_TRUE);

        // assert
        assertThat(userInRole, is(true));
    }

    @Test
    public void isUserInRoleFalse() {
        // arrange

        // act
        final boolean userInRole = authenticationSecurityContext.isUserInRole(TEST_ROLE_FALSE);

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