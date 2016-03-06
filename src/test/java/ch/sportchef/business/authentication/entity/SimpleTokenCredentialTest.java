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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.entity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimpleTokenCredentialTest {

    private static final String TEST_TOKEN = "test-token";

    @Test
    public void testInvalidate() {
        // arrange
        final SimpleTokenCredential stc = new SimpleTokenCredential(TEST_TOKEN);

        // act
        stc.invalidate();

        // assert
        assertThat(stc.getToken(), is(nullValue()));
    }

    @Test
    public void testGetToken() {
        // arrange
        final SimpleTokenCredential stc = new SimpleTokenCredential(TEST_TOKEN);

        // act
        final String token = stc.getToken();

        // assert
        assertThat(token, is(TEST_TOKEN));
    }

    @Test
    public void testToString() {
        // arrange
        final SimpleTokenCredential stc = new SimpleTokenCredential(TEST_TOKEN);

        // act
        final String toString = stc.toString();

        // assert
        assertThat(toString, is("SimpleTokenCredential{token='test-token'}"));
    }
}