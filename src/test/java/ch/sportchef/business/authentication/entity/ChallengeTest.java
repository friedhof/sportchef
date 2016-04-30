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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChallengeTest {

    private static final String TEST_CHALLENGE = "test-challenge";

    private Challenge challenge;

    @Before
    public void before() {
        challenge = new Challenge(TEST_CHALLENGE);
    }

    @Test
    public void getChallenge() {
        // arrange

        // act
        final String challengeValue = challenge.getChallenge();

        // assert
        assertThat(challengeValue, is(TEST_CHALLENGE));
    }

    @Test
    public void increaseAndGetTries() {
        // arrange
        challenge.increaseTries();
        challenge.increaseTries();
        challenge.increaseTries();

        // act
        final int tries = challenge.getTries();

        // assert
        assertThat(tries, is(3));
    }

    @Test
    public void toStringTest() {
        // arrange

        // act
        final String toString = challenge.toString();

        // assert
        assertThat(toString, is("Challenge(challenge=test-challenge, tries=0)"));
    }

}