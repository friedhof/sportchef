package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
import java.net.URI;
import java.util.List;

@Stateless
@Path("events")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class EventsResource {

    @Inject
    private EventManager manager;

    @POST
    public Response save(@Valid final Event event, @Context final UriInfo info) {
        final Event saved = this.manager.save(event);
        final long eventId = saved.getEventId();
        final URI uri = info.getAbsolutePathBuilder().path("/" + eventId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response findAll() {
        final List<Event> allEvents = this.manager.findAll();
        return Response.ok(allEvents).build();
    }

    @Path("{eventId}")
    public EventResource find(@PathParam("eventId") final long eventId) {
        return new EventResource(eventId, this.manager);
    }

}
