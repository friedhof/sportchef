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

import ch.sportchef.business.admin.boundary.AdminResource;
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
        final Injector injector = createInjector(configuration);
        registerResources(environment, injector);
    }

    private Injector createInjector(@NotNull final SportChefConfiguration configuration) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SportChefConfiguration.class).toInstance(configuration);
            }
        });
    }

    private void registerResources(@NotNull final Environment environment,
                                   @NotNull final Injector injector) {
        final JerseyEnvironment jersey = environment.jersey();
        jersey.register(injector.getInstance(AdminResource.class));
    }

}
