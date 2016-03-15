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
package ch.sportchef.business.configuration.control;

import ch.sportchef.business.configuration.entity.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigurationRepositoryTest {

    private static ConfigurationRepository configurationRepository;

    @BeforeClass
    public static void setUp() throws IOException {
        final File dir = Paths.get(System.getProperty("user.home"), ".sportchef").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = Paths.get(System.getProperty("user.home"), ".sportchef", "cfg_custom.properties").toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        configurationRepository = new ConfigurationRepository();
    }

    @AfterClass
    public static void tearDown() {
        configurationRepository = null;
    }

    @Test
    public void getConfiguration() {
        // arrange

        // act
        final Configuration configuration = configurationRepository.getConfiguration();

        // assert
        assertThat(configuration, notNullValue());
    }

    @Test
    public void toStringTest() {
        // arrange

        // act
        final String toString = configurationRepository.toString();

        // assert
        assertThat(toString, is("ConfigurationRepository(configuration=Configuration(properties={smtp.from=, smtp.password=, admin.password=, smtp.useSSL=, smtp.user=, smtp.port=, smtp.server=}))"));
    }
}