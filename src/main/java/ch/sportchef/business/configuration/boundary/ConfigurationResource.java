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
package ch.sportchef.business.configuration.boundary;

import ch.sportchef.business.configuration.entity.Configuration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("configuration")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ConfigurationResource {

    @Inject
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private ConfigurationManager configurationManager;

    @GET
    @Path("contact")
    public final Response getContact() {
        
        @SuppressWarnings("LocalVariableOfConcreteClass")
        final Configuration configuration = configurationManager.getConfiguration();

        JsonObject contact = Json.createObjectBuilder()
                .add("company", configuration.getContactCompany())
                .add("name", configuration.getContactName())
                .add("street", configuration.getContactStreet())
                .add("city", configuration.getContactCity())
                .add("phone", configuration.getContactPhone())
                .add("web", configuration.getContactWeb())
                .add("email", configuration.getContactEmail())
                .build();

        return Response.ok(contact).build();
    }

    @Override
    public String toString() {
        return "ConfigurationResource{" +
                "configurationManager=" + configurationManager +
                '}';
    }
}
