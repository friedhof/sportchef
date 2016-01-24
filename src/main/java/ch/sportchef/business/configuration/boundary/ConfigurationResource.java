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

    private static final String CONTACT_JSON_COMPANY = "company"; //NON-NLS
    private static final String CONTACT_JSON_NAME = "name"; //NON-NLS
    private static final String CONTACT_JSON_STREET = "street"; //NON-NLS
    private static final String CONTACT_JSON_CITY = "city"; //NON-NLS
    private static final String CONTACT_JSON_PHONE = "phone"; //NON-NLS
    private static final String CONTACT_JSON_WEB = "web"; //NON-NLS
    private static final String CONTACT_JSON_EMAIL = "email"; //NON-NLS

    @Inject
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private ConfigurationManager configurationManager;

    @GET
    @Path("contact")
    @SuppressWarnings("FeatureEnvy")
    public final Response getContact() {

        @SuppressWarnings("LocalVariableOfConcreteClass")
        final Configuration configuration = configurationManager.getConfiguration();

        final JsonObject contact = Json.createObjectBuilder()
                .add(CONTACT_JSON_COMPANY, configuration.getContactCompany())
                .add(CONTACT_JSON_NAME, configuration.getContactName())
                .add(CONTACT_JSON_STREET, configuration.getContactStreet())
                .add(CONTACT_JSON_CITY, configuration.getContactCity())
                .add(CONTACT_JSON_PHONE, configuration.getContactPhone())
                .add(CONTACT_JSON_WEB, configuration.getContactWeb())
                .add(CONTACT_JSON_EMAIL, configuration.getContactEmail())
                .build();

        return Response.ok(contact).build();
    }

    @Override
    public final String toString() {
        return "ConfigurationResource{" + //NON-NLS
                "configurationManager=" + configurationManager +
                '}';
    }
}
