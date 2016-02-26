package ch.sportchef.business.event.control;

import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.event.entity.EventBuilder;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import javax.persistence.OptimisticLockException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EventRepositoryTest {
    @Test
    public void shouldUpdateEventWithoutProblem() {
        final EventRepository testee = new EventRepository();
        final Event createdEvent = prepareEvent(testee);
        final Event eventToUpdate = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle")
                .buildWithVersion();
        final Event updatedEvent =  testee.update(eventToUpdate);

        assertThat(updatedEvent.getTitle(), is(equalTo("changedTitle")));
    }

    @Test(expected = OptimisticLockException.class)
    public void shouldMakeOptimisticLockException() {
        final EventRepository testee = new EventRepository();
        final Event createdEvent = prepareEvent(testee);
        final Event eventToUpdate1 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle1")
                .buildWithVersion();
        final Event eventToUpdate2 = EventBuilder
                .fromEvent(createdEvent)
                .withTitle("changedTitle2")
                .buildWithVersion();
        final Event updatedEvent1 =  testee.update(eventToUpdate1);
        final Event updatedEvent2 =  testee.update(eventToUpdate2);


    }

    private Event prepareEvent(EventRepository testee) {
        final Event baseEvent = EventBuilder
                .anEvent()
                .withTitle("tetTitle")
                .withLocation("Basel Hackergarten")
                .withDate(LocalDate.of(2016,  4,1))
                .withTime(LocalTime.of(10,12))
                .build();

        return testee.create(baseEvent);
    }

}