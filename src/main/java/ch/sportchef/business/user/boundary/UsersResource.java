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

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    private final UserService userService;

    @Inject
    public UsersResource(@NotNull final UserService userService) {
        this.userService = userService;
    }

    @POST
    public Response save(@Valid final User user, @Context final UriInfo info) {
        final User saved = userService.create(user);
        final Long userId = saved.getUserId();
        final URI uri = info.getAbsolutePathBuilder().path("/" + userId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response findAll() {
        final List<User> users = userService.findAll();
        return Response.ok(users).build();
    }

    @Path("{userId}")
    public UserResource find(@PathParam("userId") final long userId) {
        return new UserResource(userId, userService);
    }

}
