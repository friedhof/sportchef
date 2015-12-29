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
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;
import pl.setblack.airomem.core.SimpleController;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserResource {

    private long userId;
    private SimpleController<UserManager> manager;

    public UserResource(final long userId, final SimpleController<UserManager> manager) {
        this.userId = userId;
        this.manager = manager;
    }

    @GET
    public User find() {
        final User user = this.manager.readOnly().findByUserId(this.userId);
        if (user == null) {
            throw new NotFoundException(String.format("user with id '%d' not found", userId));
        }
        return user;
    }

    @PUT
    public Response update(@Valid final User user, @Context final UriInfo info) {
        find(); // only update existing users
        final User userToUpdate = new User(this.userId, user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail());
        final User updatedUser = this.manager.executeAndQuery(mgr -> mgr.update(userToUpdate));
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedUser).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete() {
        final User user = find(); // only delete existing users
        this.manager.execute(mgr -> mgr.delete(user.getUserId()));
        return Response.noContent().build();
    }

}
