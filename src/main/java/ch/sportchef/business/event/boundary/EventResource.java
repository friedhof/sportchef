package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

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

    @PUT
    public Response update(@Valid final Event event, @Context final UriInfo info) {
        find(); // only update existing events
        event.setEventId(this.eventId);
        final Event updatedEvent = this.manager.save(event);
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedEvent).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete() {
        final Event event = find(); // only delete existing events
        this.manager.delete(event.getEventId());
        return Response.noContent().build();
    }

}
