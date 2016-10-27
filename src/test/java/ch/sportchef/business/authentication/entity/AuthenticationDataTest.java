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
package ch.sportchef.business.authentication.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuthenticationDataTest {

    private static final String TEST_EMAIL = "test-email";
    private static final String TEST_CHALLENGE = "test-challenge";

    private AuthenticationData authenticationData;

    @BeforeEach
    public void setUp() {
        authenticationData = AuthenticationData.builder().email(TEST_EMAIL).challenge(TEST_CHALLENGE).build();
    }

    @Test
    public void getEmail() {
        // arrange

        // act
        final String email = authenticationData.getEmail();

        // assert
        assertThat(email, is(TEST_EMAIL));
    }

    @Test
    public void getChallenge() {
        // arrange

        // act
        final String challenge = authenticationData.getChallenge();

        // assert
        assertThat(challenge, is(TEST_CHALLENGE));
    }

    @Test
    public void getStringTest() {
        // arrange

        // act
        final String toString = authenticationData.toString();

        // assert
        assertThat(toString, is("AuthenticationData(email=test-email, challenge=test-challenge)"));
    }
}