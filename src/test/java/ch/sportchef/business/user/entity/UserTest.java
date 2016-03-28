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
package ch.sportchef.business.user.entity;

import ch.sportchef.business.authentication.entity.Role;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    private static final Long USER_ID = 1L;
    private static final String USER_FIRSTNAME = "John";
    private static final String USER_LASTNAME = "Doe";
    private static final String USER_PHONE = "+41 79 555 00 01";
    private static final String USER_EMAIL = "john.doe@sportchef.ch";
    private static final Role USER_ROLE = Role.USER;
    private static final Long USER_VERSION = 0L;

    private static User user;

    @BeforeClass
    public static void setUp() {
        user = User.builder()
                .userId(USER_ID)
                .firstName(USER_FIRSTNAME)
                .lastName(USER_LASTNAME)
                .phone(USER_PHONE)
                .email(USER_EMAIL)
                .role(USER_ROLE)
                .version(USER_VERSION)
                .build();
    }

    @AfterClass
    public static void tearDown() {
        user = null;
    }

    @Test
    public void getUserId() {
        assertThat(user.getUserId(), is(USER_ID));
    }

    @Test
    public void getFirstName() {
        assertThat(user.getFirstName(), is(USER_FIRSTNAME));
    }

    @Test
    public void getLastName() {
        assertThat(user.getLastName(), is(USER_LASTNAME));
    }

    @Test
    public void getPhone() {
        assertThat(user.getPhone(), is(USER_PHONE));
    }

    @Test
    public void getEmail() {
        assertThat(user.getEmail(), is(USER_EMAIL));
    }

    @Test
    public void getRole() {
        assertThat(user.getRole(), is(USER_ROLE));
    }

    @Test
    public void getVersion() {
        assertThat(user.getVersion(), is(USER_VERSION));
    }

    @Test
    public void toStringTest() {
        // arrange
        final String toStringExpect = String.format(
                "User(userId=%d, firstName=%s, lastName=%s, phone=%s, email=%s, role=%s, version=%d)",
                USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_PHONE, USER_EMAIL, USER_ROLE, user.getVersion());

        // act
        final String toStringIs = user.toString();

        // assert
        assertThat(toStringIs, is(toStringExpect));
    }
}