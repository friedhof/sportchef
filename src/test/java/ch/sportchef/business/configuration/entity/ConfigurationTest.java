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

import ch.sportchef.test.UnitTests;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@Category(UnitTests.class)
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
    public void getContactCompany() {
        assertThat(configuration.getContactCompany(), is("My Company"));
    }

    @Test
    public void getContactName() {
        assertThat(configuration.getContactName(), is("My Name"));
    }

    @Test
    public void getContactStreet() {
        assertThat(configuration.getContactStreet(), is("My Street"));
    }

    @Test
    public void getContactCity() {
        assertThat(configuration.getContactCity(), is("My City"));
    }

    @Test
    public void getContactPhone() {
        assertThat(configuration.getContactPhone(), is("My Phone"));
    }

    @Test
    public void getContactEmail() {
        assertThat(configuration.getContactEmail(), is("My Email"));
    }

    @Test
    public void getContactWeb() {
        assertThat(configuration.getContactWeb(), is("My Web"));
    }

}
