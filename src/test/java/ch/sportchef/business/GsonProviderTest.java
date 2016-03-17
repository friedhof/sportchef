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
package ch.sportchef.business;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GsonProviderTest {

    @Test
    public void isReadable() {
        // arrange
        final GsonProvider gsonProvider = new GsonProvider();

        // act
        final boolean readable = gsonProvider.isReadable(null, null, null, null);

        // assert
        assertThat(readable, is(true));
    }

    @Test
    public void readFrom() throws IOException {
        // arrange
        final GsonProvider gsonProvider = new GsonProvider();
        final Class<Object> type = Object.class;
        final Type genericType = TestObject.class;
        final ByteArrayInputStream entityStream =
                new ByteArrayInputStream("{\"id\":1,\"test\":\"This is a Test!\"}".getBytes());

        // act
        final Object object = gsonProvider.readFrom(
                type,
                genericType,
                null,
                null,
                null,
                entityStream);

        // assert
        final TestObject testObject = (TestObject) object;
        assertThat(testObject.id, is(1L));
        assertThat(testObject.test, is("This is a Test!"));
    }

    @Test
    public void isWriteable() {
        // arrange
        final GsonProvider gsonProvider = new GsonProvider();

        // act
        final boolean writeable = gsonProvider.isWriteable(null, null, null, null);

        // assert
        assertThat(writeable, is(true));
    }

    @Test
    public void getSize() {
        // arrange
        final GsonProvider gsonProvider = new GsonProvider();

        // act
        final long size = gsonProvider.getSize(null, null, null, null, null);

        // assert
        assertThat(size, is(-1L));
    }

    @Test
    public void writeTo() throws IOException {
        // arrange
        final GsonProvider gsonProvider = new GsonProvider();
        final Object object = new TestObject();
        final Class<?> type = TestObject.class;
        final Type genericType = TestObject.class;
        final ByteArrayOutputStream entityStream = new ByteArrayOutputStream();

        // act
        gsonProvider.writeTo(
                object,
                type,
                genericType,
                null,
                null,
                null,
                entityStream);

        // assert
        assertThat(entityStream.toString(), is("{\"id\":1,\"test\":\"This is a Test!\"}"));
    }

    private class TestObject {
        private Long id = 1L;
        private String test = "This is a Test!";
    }
}