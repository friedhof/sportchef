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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.event.entity.Event;
import pl.setblack.airomem.core.SimpleController;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;

@Named
@Singleton
public class EventService {

    private final SimpleController<EventManager> manager =
            PersistenceManager.createSimpleController(Event.class, EventManager::new);

    public Event create(@NotNull final Event event) {
        return manager.executeAndQuery((mgr) -> mgr.create(event));
    }

    public Event update(@NotNull final Event event) {
        return manager.executeAndQuery((mgr) -> mgr.update(event));
    }

    public Event findByEventId(final long eventId) {
        return manager.readOnly().findByEventId(eventId);
    }

    public List<Event> findAll() {
        return manager.readOnly().findAll();
    }

    public void delete(final long eventId) {
        manager.execute((mgr) -> mgr.delete(eventId));
    }
}
