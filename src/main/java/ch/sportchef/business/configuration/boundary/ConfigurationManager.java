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
package ch.sportchef.business.configuration.boundary;

import ch.sportchef.business.configuration.entity.Configuration;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

@Named
public class ConfigurationManager {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationManager.class.getName());

    private  static final String DEFAULT_CONFIGURATION_FILE = "cfg_default.properties"; //NON-NLS
    private  static final String CUSTOM_CONFIGURATION_FILE = "cfg_custom.properties"; //NON-NLS

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final Configuration configuration;

    public ConfigurationManager() {
        final Properties properties = new Properties();

        // load default configuration first
        loadProperties("default", DEFAULT_CONFIGURATION_FILE) //NON-NLS
                .forEach(properties::put);

        // load custom configuration
        loadProperties("custom", CUSTOM_CONFIGURATION_FILE) //NON-NLS
                .forEach(properties::put);

        configuration = new Configuration(properties);
    }

    private static Map<Object, Object> loadProperties(@NotNull final String type, @NotNull final String fileName) {
        final Properties properties = new Properties();
        try (final InputStream stream =
                     Thread.currentThread().getContextClassLoader()
                             .getResourceAsStream(fileName)) {
            properties.load(stream);
        } catch (final IOException e) {
            LOGGER.severe(String.format(
                    "Could not load %s configuration from file '%s'!", //NON-NLS
                    type, fileName));
        }
        return properties;
    }

    @SuppressWarnings("MethodReturnOfConcreteClass")
    public final Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public final String toString() {
        return String.format("ConfigurationManager{configuration=%s}", configuration); //NON-NLS
    }
}
