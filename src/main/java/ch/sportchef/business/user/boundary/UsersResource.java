package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

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
@Path("users")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UsersResource {

    @Inject
    private UserManager manager;

    @POST
    public Response save(@Valid final User user, @Context final UriInfo info) {
        final User saved = this.manager.save(user);
        final long userId = saved.getUserId();
        final URI uri = info.getAbsolutePathBuilder().path("/" + userId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response findAll() {
        final List<User> allUsers = this.manager.findAll();
        return Response.ok(allUsers).build();
    }

    @Path("{userId}")
    public UserResource find(@PathParam("userId") final long userId) {
        return new UserResource(userId, this.manager);
    }

}
