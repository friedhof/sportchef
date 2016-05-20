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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;

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
import java.util.Optional;

public class EventResource {

    private final Long eventId;
    private final EventService eventService;
    private final EventImageService eventImageService;

    public EventResource(@NotNull final Long eventId,
                         @NotNull final EventService eventService,
                         @NotNull final EventImageService eventImageService) {
        this.eventId = eventId;
        this.eventService = eventService;
        this.eventImageService = eventImageService;
    }

    @GET
    public Event find() {
        final Optional<Event> event = eventService.findByEventId(eventId);
        if (event.isPresent()) {
            return event.get();
        }
        throw new NotFoundException(String.format("event with id '%d' not found", eventId));
    }

    @PUT
    public Response update(@Valid final Event event, @Context final UriInfo info) {
        find(); // only update existing events
        final Event eventToUpdate = event.toBuilder()
                .eventId(eventId)
                .build();
        final Event updatedEvent = eventService.update(eventToUpdate);
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedEvent).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete() {
        find(); // only delete existing events
        try {
            eventImageService.deleteImage(eventId);
        } catch (final NotFoundException e) {
            // ignore, event has no image
        }
        eventService.delete(eventId);
        return Response.noContent().build();
    }

    @Path("image")
    public EventImageResource image() {
        find(); // only existing events can have images
        return new EventImageResource(eventId, eventImageService);
    }
}
