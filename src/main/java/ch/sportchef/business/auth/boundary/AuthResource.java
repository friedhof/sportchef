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
package ch.sportchef.business.auth.boundary;

import ch.sportchef.business.user.boundary.UserManager;
import ch.sportchef.business.user.entity.User;
import pl.setblack.airomem.core.SimpleController;

import javax.ejb.Stateless;
import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;

@Stateless
@Path("authenticate")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AuthResource {

    private SimpleController<UserManager> manager =  SimpleController.loadOptional(User.class.getName(), UserManager::new);

    @Path("login/{email}")
    public LoginResource login(@PathParam("email") final String email) {
        return new LoginResource(email, null, null, this.manager);
    }

    @Path("{token}")
    public LoginResource authenticate(@PathParam("token") final String token, @CookieParam("login")String cookie) {

        String[] cookieData = cookie.split("\\|");
        HashMap<String,String> cookieMap = new HashMap<>();
        cookieMap.put("token",cookieData[0]);
        cookieMap.put("email",cookieData[1]);

        return new LoginResource(cookieMap.get("email"), token, cookieMap, this.manager);
    }

}