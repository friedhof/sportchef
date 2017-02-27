/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016, 2017 Marcus Fihlon
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
package ch.sportchef.business.user.control;

import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.user.entity.User;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheckRegistry;
import pl.setblack.airomem.core.PersistenceController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Singleton
@Timed(name = "Timed: EventService")
@Metered(name = "Metered: EventService")
public class UserService {

    private PersistenceController<UserRepository> controller;
    private HealthCheckRegistry healthCheckRegistry;

    @Inject
    public UserService(@NotNull final HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    @PostConstruct
    public void setupResources() {
        controller = PersistenceManager.createController(User.class, UserRepository::new);
        healthCheckRegistry.register("UserService", new UserServiceHealthCheck(this));
    }

    @PreDestroy
    public void cleanupResources() {
        controller.close();
    }

    public User create(@NotNull final User user) {
        return controller.executeAndQuery(mgr -> mgr.create(user));
    }

    public User update(@NotNull final User user) {
        return controller.executeAndQuery(mgr -> mgr.update(user));
    }

    public Optional<User> findByUserId(@NotNull final Long userId) {
        return controller.query(ctrl -> ctrl.findByUserId(userId));
    }

    public Optional<User> findByEmail(@NotNull final String email) {
        return controller.query(ctrl -> ctrl.findByEmail(email));
    }

    public List<User> findAll() {
        return controller.query(UserRepository::findAll);
    }

    public void delete(final Long userId) {
        controller.execute(mgr -> mgr.delete(userId));
    }

    public Optional<User> getAuthenticatedUser(@NotNull final SecurityContext securityContext) {
        final Principal principal = securityContext.getUserPrincipal();
        final String email = principal.getName();
        return findByEmail(email);
    }
}
