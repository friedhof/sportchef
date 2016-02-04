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

import ch.sportchef.business.configuration.boundary.ConfigurationManager;
import ch.sportchef.business.user.boundary.UserManager;
import ch.sportchef.business.user.entity.User;
import ch.sportchef.business.auth.service.LoginService;
import pl.setblack.airomem.core.SimpleController;

import javax.inject.Inject;
import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.mail.EmailException;

public class LoginResource {

    @Inject
    private LoginService loginService;
    @Inject
    private ConfigurationManager configurationManager;

    private String email;
    private String token;
    private HashMap<String,String> cookie;

    private SimpleController<UserManager> manager;

    public LoginResource(){
    }

    public LoginResource(final String email, final String token, final HashMap<String,String> cookie, final SimpleController<UserManager> manager) {
        this.email = email;
        this.token = token;
        this.cookie = cookie;
        this.manager = manager;
    }

    public User findLogin() {
        final Optional<User> user = this.manager.readOnly().findByEmail(this.email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new NotFoundException(String.format("user with email '%s' not found", email));
    }

    @POST
    public Response login() throws EmailException, InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final User user = findLogin();
        this.loginService = new LoginService();
        String cookieToken = this.loginService.loginRequest(user);
        JsonObject jsonObject = Json.createObjectBuilder()
            .add("token", cookieToken)
            .add("email",user.getEmail())
            .build();
        return Response.ok(jsonObject).build();
    }

    @GET
    public Response authenticate() throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
        this.loginService = new LoginService();
        User user = this.loginService.checkLoginToken(this.token,this.cookie);
        String cookieToken = loginService.generateToken();
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("token", cookieToken)
                .add("email",user.getEmail())
                .build();
        return Response.ok(jsonObject).build();
    }

}
