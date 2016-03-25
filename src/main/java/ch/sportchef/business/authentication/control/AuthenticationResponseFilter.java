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

import javax.validation.constraints.NotNull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(@NotNull final ContainerRequestContext requestContext,
                       @NotNull final ContainerResponseContext responseContext) {
        if (requestContext.getProperty("auth-failed") != null) {
            final Boolean failed = (Boolean) requestContext.getProperty("auth-failed");
            if (failed) {
                return;
            }
        }

        final String authHeaderVal = requestContext.getHeaderString("Authorization");
        if (authHeaderVal.startsWith("Bearer")) {
            final String token = authHeaderVal.split(" ")[1];
            final List<Object> tokenHeader = new ArrayList<>();
            tokenHeader.add(token);
            responseContext.getHeaders().put("Token", tokenHeader);
        }
    }

}
