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
            return configuration != null ? Result.healthy() : Result.unhealthy("can't access configuration");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
