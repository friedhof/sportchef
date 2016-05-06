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
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import lombok.ToString;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

@Singleton
@ToString
@Timed(name = "Timed: ConfigurationService")
@Metered(name = "Metered: ConfigurationService")
public class ConfigurationService {

    private ConfigurationRepository configurationRepository;

    @Inject
    public ConfigurationService(@NotNull final ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Configuration getConfiguration() {
        return configurationRepository.getConfiguration();
    }
}
