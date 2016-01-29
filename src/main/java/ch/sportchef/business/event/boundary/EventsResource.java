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

import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.net.URI;
import java.util.List;

@Stateless
@Path("events")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class EventsResource {

    private final SimpleController<EventManager> manager =  SimpleController.loadOptional(Event.class.getName(), () -> new EventManager());

    @POST
    public Response save(@Valid final Event event, @Context final UriInfo info) {
        final Event saved = manager.executeAndQuery(mgr -> mgr.create(event));
        final long eventId = saved.getEventId();
        final URI uri = info.getAbsolutePathBuilder().path(File.separator + eventId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response findAll() {
        final List<Event> events = manager.readOnly().findAll();
        return Response.ok(events).build();
    }

    @Path("{eventId}")
    public EventResource find(@PathParam("eventId") final long eventId) {
        return new EventResource(eventId, manager);
    }

}
