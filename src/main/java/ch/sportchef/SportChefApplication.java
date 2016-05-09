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

import ch.sportchef.business.AutoInstall;
import ch.sportchef.business.RuntimeExceptionMapper;
import ch.sportchef.business.WebApplicationExceptionMapper;
import ch.sportchef.business.admin.boundary.AdminResource;
import ch.sportchef.business.authentication.boundary.AuthenticationResource;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.user.boundary.UsersResource;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.SneakyThrows;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;

import static org.eclipse.jetty.servlets.CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_METHODS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_ORIGINS_PARAM;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
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
        registerServletFilters(environment.servlets());
        install(injector);
    }

    @Override
    public void initialize(@NotNull final Bootstrap<SportChefConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/webapp/dist", "/", "index.html"));
    }

    private static void registerModules(@NotNull final ObjectMapper objectMapper) {
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new GuavaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(Include.NON_ABSENT);
    }

    private static Injector createInjector(@NotNull final SportChefConfiguration configuration,
                                           @NotNull final Environment environment) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SportChefConfiguration.class).toInstance(configuration);
                bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
                bind(LifecycleEnvironment.class).toInstance(environment.lifecycle());
                bind(MetricRegistry.class).toInstance(environment.metrics());
            }
        });
    }

    private static void registerResources(@NotNull final Environment environment,
                                          @NotNull final Injector injector) {
        final JerseyEnvironment jersey = environment.jersey();
        jersey.register(injector.getInstance(AdminResource.class));
        jersey.register(injector.getInstance(AuthenticationResource.class));
        jersey.register(injector.getInstance(EventsResource.class));
        jersey.register(injector.getInstance(UsersResource.class));
    }

    private static void registerExceptionMapper(@NotNull final Environment environment) {
        final JerseyEnvironment jersey = environment.jersey();
        jersey.register(new WebApplicationExceptionMapper());
        jersey.register(new RuntimeExceptionMapper());
    }

    private static void registerServletFilters(@NotNull final ServletEnvironment servletEnvironment) {
        final Dynamic filter = servletEnvironment.addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }

    private static void install(@NotNull final Injector injector) {
        final AutoInstall autoInstall = injector.getInstance(AutoInstall.class);
        autoInstall.install();
    }

}
