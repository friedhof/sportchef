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
package ch.sportchef.business.event.control;

import ch.sportchef.AbstractLifecycleListener;
import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.event.entity.Event;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.eclipse.jetty.util.component.LifeCycle;
import pl.setblack.airomem.core.SimpleController;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Singleton
@Timed(name = "Timed: EventService")
@Metered(name = "Metered: EventService")
public class EventService {

    private final SimpleController<EventRepository> controller;

    @Inject
    public EventService(@NotNull final LifecycleEnvironment lifecycleEnvironment,
                        @NotNull final HealthCheckRegistry healthCheckRegistry) {
        controller = PersistenceManager.createSimpleController(Event.class, EventRepository::new);
        lifecycleEnvironment.addLifeCycleListener(new AbstractLifecycleListener() {
            @Override
            public void lifeCycleStopping(@NotNull final LifeCycle event) {
                controller.close();
            }
        });
        healthCheckRegistry.register("EventService", new EventServiceHealthCheck(this));
    }

    public Event create(@NotNull final Event event) {
        return controller.executeAndQuery(mgr -> mgr.create(event));
    }

    public Event update(@NotNull final Event event) {
        return controller.executeAndQuery(mgr -> mgr.update(event));
    }

    public Optional<Event> findByEventId(@NotNull final Long eventId) {
        return controller.readOnly().findByEventId(eventId);
    }

    public List<Event> findAll() {
        return controller.readOnly().findAll();
    }

    public void delete(@NotNull final Long eventId) {
        controller.execute(mgr -> mgr.delete(eventId));
    }
}
