package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

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
@Path("users")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UsersResource {

    @Inject
    private UserManager manager;

    @POST
    public Response save(@Valid User user, @Context UriInfo info) {
        final User saved = manager.save(user);
        final long id = saved.getId();
        final URI uri = info.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }

}
