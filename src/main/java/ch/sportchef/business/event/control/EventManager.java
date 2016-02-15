/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2015, 2016 Marcus Fihlon
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

import ch.sportchef.business.event.entity.Event;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

class EventManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Event> events = new ConcurrentHashMap<>();

    private final AtomicLong eventSeq = new AtomicLong(0);


    Event create(@NotNull final Event event) {
        final Long eventId = eventSeq.incrementAndGet();
        final Event eventToCreate = new Event(eventId, event.getTitle(), event.getLocation(), event.getDate(), event.getTime());
        events.put(eventId, eventToCreate);
        return eventToCreate;
    }

    Event update(@NotNull final Event event) {
        events.put(event.getEventId(), event);
        return event;
    }

    Optional<Event> findByEventId(final long eventId) {
        return Optional.ofNullable(events.get(eventId));
    }

    List<Event> findAll() {
       return events.values().stream()
               .sorted(comparingLong(Event::getEventId))
               .collect(toList());
    }

    void delete(final long eventId) {
        events.remove(eventId);
    }
}
