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
package ch.sportchef;

import ch.sportchef.business.RuntimeExceptionMapper;
import ch.sportchef.business.WebApplicationExceptionMapper;
import ch.sportchef.business.admin.boundary.AdminResource;
import ch.sportchef.business.authentication.boundary.AuthenticationResource;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.user.boundary.UsersResource;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;

public class SportChefApplication extends Application<SportChefConfiguration> {

    @SneakyThrows
    public static void main(@NotNull final String... args) {
        new SportChefApplication().run(args);
    }

    @Override
    public void run(@NotNull final SportChefConfiguration configuration,
                    @NotNull final Environment environment) {
        registerModules(environment.getObjectMapper());
        final Injector injector = createInjector(configuration, environment);
        registerResources(environment, injector);
        registerExceptionMapper(environment);
    }

    private void registerModules(@NotNull final ObjectMapper objectMapper) {
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Injector createInjector(@NotNull final SportChefConfiguration configuration,
                                    @NotNull final Environment environment) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SportChefConfiguration.class).toInstance(configuration);
                bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
                bind(MetricRegistry.class).toInstance(environment.metrics());
            }
        });
    }

    private void registerResources(@NotNull final Environment environment,
                                   @NotNull final Injector injector) {
        final JerseyEnvironment jersey = environment.jersey();
        jersey.register(injector.getInstance(AdminResource.class));
        jersey.register(injector.getInstance(AuthenticationResource.class));
        jersey.register(injector.getInstance(EventsResource.class));
        jersey.register(injector.getInstance(UsersResource.class));
    }

    private void registerExceptionMapper(@NotNull final Environment environment) {
        final JerseyEnvironment jersey = environment.jersey();
        jersey.register(new WebApplicationExceptionMapper());
        jersey.register(new RuntimeExceptionMapper());
    }

}
