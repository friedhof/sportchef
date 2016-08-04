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
package ch.sportchef.business.imprint.boundary;

import ch.sportchef.business.imprint.control.ImprintService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("imprint")
@Produces(MediaType.TEXT_PLAIN)
public class ImprintResource {

    private final ImprintService imprintService;

    @Inject
    public ImprintResource(@NotNull final ImprintService imprintService) {
        this.imprintService = imprintService;
    }

    @GET
    public Response getImprint() throws IOException {
        final String markdown = imprintService.getImprint();
        return Response.ok(markdown).build();
    }

}
