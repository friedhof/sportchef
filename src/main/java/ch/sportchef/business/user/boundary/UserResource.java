package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserResource {

    private long userId;
    private UserManager manager;

    public UserResource(final long userId, final UserManager manager) {
        this.userId = userId;
        this.manager = manager;
    }

    @GET
    public User find() {
        final User user = this.manager.findByUserId(this.userId);
        if (user == null) {
            throw new NotFoundException(String.format("user with id '%d' not found", userId));
        }
        return user;
    }

    @PUT
    public Response update(@Valid final User user, @Context final UriInfo info) {
        find(); // only update existing users
        user.setUserId(this.userId);
        final User updatedUser = this.manager.save(user);
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok().header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete() {
        final User user = find(); // only delete existing users
        this.manager.delete(user.getUserId());
        return Response.noContent().build();
    }

}
