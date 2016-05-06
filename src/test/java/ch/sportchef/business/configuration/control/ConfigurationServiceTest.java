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
package ch.sportchef.business.configuration.control;

import ch.sportchef.business.configuration.entity.Configuration;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationServiceTest {

    @Test
    public void getConfiguration() {
        // arrange
        final Configuration configurationMock = mock(Configuration.class);
        final ConfigurationRepository configurationRepositoryMock = mock(ConfigurationRepository.class);
        when(configurationRepositoryMock.getConfiguration()).thenReturn(configurationMock);
        final ConfigurationService configurationService = new ConfigurationService(configurationRepositoryMock);

        // act
        final Configuration configuration = configurationService.getConfiguration();

        // assert
        assertThat(configuration, is(configurationMock));
    }

    @Test
    public void toStringTest() {
        // arrange
        final ConfigurationRepository configurationRepositoryMock = mock(ConfigurationRepository.class);
        final ConfigurationService configurationService = new ConfigurationService(configurationRepositoryMock);

        // act
        final String toString = configurationService.toString();

        // assert
        assertThat(toString, startsWith("ConfigurationService(configurationRepository=Mock for ConfigurationRepository, hashCode: "));
    }
}