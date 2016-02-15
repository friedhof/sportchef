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
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

public class UserResource {

    private long userId;

    private UserService userService;

    public UserResource(final long userId, final UserService userService) {
        this.userId = userId;
        this.userService = userService;
    }

    @GET
    public User find() {
        final Optional<User> user = userService.findByUserId(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new NotFoundException(String.format("user with id '%d' not found", userId));
    }

    @PUT
    public Response update(@Valid final User user, @Context final UriInfo info) {
        find(); // only update existing users
        final User userToUpdate = new User(this.userId, user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail());
        final User updatedUser = userService.update(userToUpdate);
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedUser).header("Location", uri.toString()).build();
    }

    @DELETE
    @Consumes({MediaType.WILDCARD})
    public Response delete() {
        final User user = find(); // only delete existing users
        userService.delete(user.getUserId());
        return Response.noContent().build();
    }

}
