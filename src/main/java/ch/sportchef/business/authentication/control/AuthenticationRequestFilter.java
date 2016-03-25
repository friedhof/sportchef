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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.control;

import org.jose4j.jwt.consumer.InvalidJwtException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationRequestFilter implements ContainerRequestFilter {

    @Inject
    private AuthenticationService authenticationService;

    @Override
    public void filter(@NotNull final ContainerRequestContext requestContext) throws IOException {
        final String authHeaderVal = requestContext.getHeaderString("Authorization");
        if (authHeaderVal.startsWith("Bearer")) {
            final String token = authHeaderVal.split(" ")[1];
            try {
                final Optional<String> subject = authenticationService.validate(token);
                final String email = subject.orElseThrow(() -> new InvalidJwtException("Invalid token data"));
                final SecurityContext securityContext = requestContext.getSecurityContext();
                requestContext.setSecurityContext(new AuthenticationSecurityContext(securityContext, email));
            } catch (final InvalidJwtException e) {
                requestContext.abortWith(
                        Response.status(UNAUTHORIZED)
                                .header("Cause", e.getMessage())
                                .build());
            }
        } else {
            requestContext.abortWith(
                    Response.status(UNAUTHORIZED)
                            .build());
        }
    }

}
