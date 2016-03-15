/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015, 2016 Marcus Fihlon
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
package ch.sportchef.business.configuration.control;

import ch.sportchef.business.configuration.entity.Configuration;
import lombok.ToString;
import pl.setblack.badass.Politician;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@ToString
class ConfigurationRepository {

    private  static final String DEFAULT_CONFIGURATION_FILE = "cfg_default.properties"; //NON-NLS
    private  static final String CUSTOM_CONFIGURATION_FILE = "cfg_custom.properties"; //NON-NLS

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final Configuration configuration;

    ConfigurationRepository() {
        final Properties properties = new Properties();

        loadDefaultConfiguration().forEach(properties::put);
        loadCustomConfiguration().forEach(properties::put);

        configuration = new Configuration(properties);
    }

    private static Map<Object, Object> loadDefaultConfiguration() {
        return Politician.beatAroundTheBush(() -> {
            final Properties properties = new Properties();
            try (final InputStream stream =
                         Thread.currentThread().getContextClassLoader()
                                 .getResourceAsStream(DEFAULT_CONFIGURATION_FILE)) {
                properties.load(stream);
            }

            return properties;
        });
    }

    private static Map<Object, Object> loadCustomConfiguration() {
        return Politician.beatAroundTheBush(() -> {
            final Properties properties = new Properties();
            final File file = Paths.get(System.getProperty("user.home"), ".sportchef", CUSTOM_CONFIGURATION_FILE).toFile();
            try (final InputStream stream = new FileInputStream(file)) {
                properties.load(stream);
            }

            return properties;
        });
    }

    @SuppressWarnings("MethodReturnOfConcreteClass")
    Configuration getConfiguration() {
        return configuration;
    }

}
