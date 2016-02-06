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
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

}
