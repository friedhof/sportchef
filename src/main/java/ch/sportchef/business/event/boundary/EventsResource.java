package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

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
}
