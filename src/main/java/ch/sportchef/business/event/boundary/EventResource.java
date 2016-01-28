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
import pl.setblack.airomem.core.SimpleController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class EventResource {

    private Long eventId;

    private SimpleController<EventManager> manager;

    public EventResource(@NotNull final Long eventId, @NotNull final SimpleController<EventManager> manager) {
        this.eventId = eventId;
        this.manager = manager;
    }

    @GET
    public Event find() {
        final Event event = this.manager.readOnly().findByEventId(this.eventId);
        if (event == null) {
            throw new NotFoundException(String.format("event with id '%d' not found", eventId));
        }
        return event;
    }

    @PUT
    public Response update(@Valid final Event event, @Context final UriInfo info) {
        find(); // only update existing events
        final Event eventToUpdate = new Event(this.eventId, event.getTitle(), event.getLocation(), event.getDate(), event.getTime());
        final Event updatedEvent = this.manager.executeAndQuery(mgr -> mgr.update(eventToUpdate));
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedEvent).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete() {
        final Event event = find(); // only delete existing events
        try {
            image().deleteImage();
        } catch (final NotFoundException e) {
            // ignore, this event has no images
        }
        this.manager.execute(mgr -> mgr.delete(event.getEventId()));
        return Response.noContent().build();
    }

    @Path("image")
    public EventImageResource image() {
        find(); // only existing events can have images
        return new EventImageResource(manager, this.eventId);
    }
}
