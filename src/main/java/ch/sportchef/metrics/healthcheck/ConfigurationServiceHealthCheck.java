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
package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.configuration.control.ConfigurationService;
import ch.sportchef.business.configuration.entity.Configuration;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;

public class ConfigurationServiceHealthCheck extends HealthCheck {

    private final ConfigurationService configurationService;

    public ConfigurationServiceHealthCheck(@NotNull final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    protected Result check() throws Exception {
        try {
            final Configuration configuration = configurationService.getConfiguration();
            return configuration != null ? Result.healthy() : Result.unhealthy("Can't access configuration!");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
