/**
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015 Marcus Fihlon
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EventManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Event> events = new ConcurrentHashMap<>();

    private final AtomicLong eventSeq = new AtomicLong(1);


    public Event createNew(@NotNull final Event event) {
        final long newId = eventSeq.incrementAndGet();
        event.setEventId(newId);
        this.events.put(newId, event);
        return event;
    }

    public Event update(@NotNull final Event event) {
        this.events.put(event.getEventId(), event);
        return event;
    }

    public Event findByEventId(final long eventId) { return this.events.get(eventId);}

    public List<Event> findAll() {
       return this.events.values().stream().collect(Collectors.toList());
    }

    public void delete(final long eventId) {
        this.events.remove(eventId);
    }
}
