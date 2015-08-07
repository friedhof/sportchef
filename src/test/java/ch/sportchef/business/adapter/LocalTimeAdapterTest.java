/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.adapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LocalTimeAdapterTest {

    private static final String TIME_TEST_STRING = "13:02";
    private static final LocalTime TIME_TEST_OBJECT = LocalTime.of(13, 2);

    private static LocalTimeAdapter adapter;

    @BeforeClass
    public static void beforeClass() {
        adapter = new LocalTimeAdapter();
    }

    @AfterClass
    public static void afterClass() {
        adapter = null;
    }

    @Test
    public void marshal() throws Exception {
        assertThat(adapter.marshal(TIME_TEST_OBJECT), is(TIME_TEST_STRING));
    }

    @Test
    public void unmarshal() throws Exception {
        assertThat(adapter.unmarshal(TIME_TEST_STRING), is(TIME_TEST_OBJECT));
    }
}
