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
package ch.sportchef.business.configuration.entity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static java.lang.Boolean.FALSE;

public class ConfigurationTest {

    private static Configuration configuration;

    @BeforeClass
    public static void prepareConfiguration() throws IOException {
        final Properties properties = new Properties();
        final InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("cfg_test.properties");
        properties.load(inputStream);
        configuration = new Configuration(properties);
    }

    @AfterClass
    public static void cleanupConfiguration() {
        configuration = null;
    }

    @Test
    public void getTokenSigningKey() {
        assertThat(configuration.getTokenSigningKey(), is("This is a test!"));
    }

    @Test
    public void getAdminPassword() {
        assertThat(configuration.getAdminPassword(), is("test.admin.password"));
    }

    @Test
    public void getAdminFirstname() {
        assertThat(configuration.getAdminFirstname(), is("test.admin.firstname"));
    }

    @Test
    public void getAdminLastname() {
        assertThat(configuration.getAdminLastname(), is("test.admin.lastname"));
    }

    @Test
    public void getAdminEmail() {
        assertThat(configuration.getAdminEmail(), is("test.admin.email"));
    }

    @Test
    public void getAdminPhone() {
        assertThat(configuration.getAdminPhone(), is("test.admin.phone"));
    }

    @Test
    public void getSMTPServer() {
        assertThat(configuration.getSMTPServer(), is("test.smtp.server"));
    }

    @Test
    public void getSMTPPort() {
        assertThat(configuration.getSMTPPort(), is(25));
    }

    @Test
    public void getSMTPUser() {
        assertThat(configuration.getSMTPUser(), is("test.smtp.user"));
    }

    @Test
    public void getSMTPPassword() {
        assertThat(configuration.getSMTPPassword(), is("test.smtp.password"));
    }

    @Test
    public void getSMTPSSL() {
        assertThat(configuration.getSMTPSSL(), is(FALSE));
    }

    @Test
    public void getSMTPFrom() {
        assertThat(configuration.getSMTPFrom(), is("test@smtp.from"));
    }

    @Test
    public void toStringTest() {
        assertThat(configuration.toString(), startsWith("Configuration(properties="));
    }
}
