package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

public class UserResource {

    private long userId;
    private UserManager manager;

    public UserResource(final long userId, final UserManager manager) {
        this.userId = userId;
        this.manager = manager;
    }

    @GET
    public Response find() {
        final User user = manager.findByUserId(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

}
