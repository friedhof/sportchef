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
import ch.sportchef.business.event.entity.EventBuilder;

import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

class EventRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Event> events = new ConcurrentHashMap<>();

    private final AtomicLong eventSeq = new AtomicLong(0);


    Event create(@NotNull final Event event) {
        final Long eventId = eventSeq.incrementAndGet();
        final Event eventToCreate = EventBuilder.fromEvent(event).withEventId(eventId).build();
        events.put(eventId, eventToCreate);
        return eventToCreate;
    }

    Event update(@NotNull final Event event) {
        final Event previousEvent = events.getOrDefault(event.getEventId(), event);
        if (!previousEvent.getVersion().equals(event.getVersion())) {
            throw new OptimisticLockException("You tried to update an event that was modified concurrently!");
        } else {
            events.put(event.getEventId(), EventBuilder.fromEvent(event).build());
            return event;
        }
    }

    Optional<Event> findByEventId(final long eventId) {
        return Optional.ofNullable(events.get(eventId));
    }

    List<Event> findAll() {
       return events.values().stream()
               .sorted((event1, event2) -> {
                   final LocalDateTime event1DateTime = LocalDateTime.of(event1.getDate(), event1.getTime());
                   final LocalDateTime event2DateTime = LocalDateTime.of(event2.getDate(), event2.getTime());
                   return event1DateTime.compareTo(event2DateTime);
               })
               .collect(toList());
    }

    void delete(final long eventId) {
        events.remove(eventId);
    }
}
