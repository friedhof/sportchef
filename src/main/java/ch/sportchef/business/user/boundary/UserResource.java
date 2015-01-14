package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;

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
    public User update(@Valid final User user) {
        find(); // only update existing users
        user.setUserId(this.userId);
        return this.manager.save(user);
    }

}
