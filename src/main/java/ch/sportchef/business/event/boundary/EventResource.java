package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;

public class EventResource {

    private long eventId;
    private EventManager manager;

    public EventResource(final long eventId, final EventManager manager) {
        this.eventId = eventId;
        this.manager = manager;
    }

    @GET
    public Event find() {
        final Event event = this.manager.findByEventId(this.eventId);
        if (event == null) {
            throw new NotFoundException(String.format("event with id '%d' not found", eventId));
        }
        return event;
    }

}
