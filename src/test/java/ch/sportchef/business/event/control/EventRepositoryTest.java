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

import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.event.entity.EventBuilder;
import org.junit.Test;

import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventRepositoryTest {

    private Event createEvent(@NotNull final EventRepository eventRepository) {
        final Event baseEvent = EventBuilder
                .anEvent()
                .withTitle("tetTitle")
                .withLocation("Basel Hackergarten")
                .withDate(LocalDate.of(2016, 4, 1))
                .withTime(LocalTime.of(10, 12))
                .build();

        return eventRepository.create(baseEvent);
    }

    @Test
    public void updateOK() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event createdEvent = createEvent(eventRepository);
        final Event eventToUpdate = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle")
                .build();

        // act
        final Event updatedEvent =  eventRepository.update(eventToUpdate);

        // assert
        assertThat(updatedEvent.getTitle(), is(equalTo(eventToUpdate.getTitle())));
        assertThat(updatedEvent.getVersion(), is(not(equalTo(eventToUpdate.getVersion()))));
    }

    @Test(expected = OptimisticLockException.class)
    public void updateWithConflict() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event createdEvent = createEvent(eventRepository);
        final Event eventToUpdate1 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle1")
                .build();
        final Event eventToUpdate2 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle2")
                .build();

        // act
        eventRepository.update(eventToUpdate1);
        eventRepository.update(eventToUpdate2);

        // assert
    }

    @Test
    public void findByEventIdFound() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event event = createEvent(eventRepository);

        // act
        final Optional<Event> eventOptional = eventRepository.findByEventId(event.getEventId());

        // assert
        assertThat(eventOptional.isPresent(), is(true));
        assertThat(eventOptional.get(), is(event));
    }

    @Test
    public void findByEventIdNotFound() {
        // arrange
        final EventRepository eventRepository = new EventRepository();

        // act
        final Optional<Event> eventOptional = eventRepository.findByEventId(1L);

        // assert
        assertThat(eventOptional.isPresent(), is(false));
    }

    @Test
    public void findAllFound() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event event1 = createEvent(eventRepository);
        final Event event2 = createEvent(eventRepository);

        // act
        final List<Event> eventList = eventRepository.findAll();

        // assert
        assertThat(eventList, notNullValue());
        assertThat(eventList.size(), is(2));
        assertThat(eventList.get(0), is(event1));
        assertThat(eventList.get(1), is(event2));
    }

    @Test
    public void findAllNotFound() {
        // arrange
        final EventRepository eventRepository = new EventRepository();

        // act
        final List<Event> eventList = eventRepository.findAll();

        // assert
        assertThat(eventList, notNullValue());
        assertThat(eventList.size(), is(0));
    }

    @Test
    public void deleteExistingEvent() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event event = createEvent(eventRepository);

        // act
        eventRepository.delete(event.getEventId());

        // assert
        assertThat(eventRepository.findByEventId(event.getEventId()), is(Optional.empty()));
    }

    @Test
    public void deleteNonExistingEvent() {
        // arrange
        final EventRepository eventRepository = new EventRepository();

        // act
        eventRepository.delete(1L);

        // assert
        assertThat(eventRepository.findByEventId(1L), is(Optional.empty()));
    }

}