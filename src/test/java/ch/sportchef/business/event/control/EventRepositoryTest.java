package ch.sportchef.business.event.control;

import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.event.entity.EventBuilder;
import org.junit.Test;

import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
                .buildWithVersion();

        // act
        final Event updatedEvent =  eventRepository.update(eventToUpdate);

        // assert
        assertThat(updatedEvent.getTitle(), is(equalTo("changedTitle")));
    }

    @Test(expected = OptimisticLockException.class)
    public void updateWithConflict() {
        // arrange
        final EventRepository eventRepository = new EventRepository();
        final Event createdEvent = createEvent(eventRepository);
        final Event eventToUpdate1 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle1")
                .buildWithVersion();
        final Event eventToUpdate2 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle2")
                .buildWithVersion();

        // act
        eventRepository.update(eventToUpdate1);
        eventRepository.update(eventToUpdate2);

        // assert
    }

}