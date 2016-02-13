/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.boundary;

import org.apache.commons.mail.EmailException;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;

@Path("authentication")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AuthenticationResource {

    @Inject
    private Identity identity;

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private AuthenticationService authenticationService;

    @GET
    @Consumes({MediaType.WILDCARD})
    public Response requestChallenge(@QueryParam("email") final String email) throws EmailException {
        if (email == null || email.trim().isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        return authenticationService.requestChallenge(email) ?
                Response.ok().build() :
                Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response authenticate(final DefaultLoginCredentials credential) {
        final Optional<String> token = authenticationService.validateChallenge(identity, credential);

        return token.isPresent() ?
                Response.ok(Entity.text(token.get())).build() :
                Response.status(Status.FORBIDDEN).build();
    }

    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    public Response authenticate(final String token) {
        return authenticationService.authentication(identity, credentials, token).isPresent() ?
                Response.ok(Entity.text(token)).build() :
                Response.status(Status.UNAUTHORIZED).build();
    }

    @POST
    @Consumes({MediaType.WILDCARD})
    public Response unsupportedCredentialType(@Context HttpServletRequest request) {
        final String ct = request.getHeader("Content-Type");
        return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
    }

    @DELETE
    public Response logout() {
        authenticationService.logout(identity);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
