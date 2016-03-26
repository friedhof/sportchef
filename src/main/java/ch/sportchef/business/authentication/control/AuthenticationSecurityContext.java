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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AuthenticationSecurityContext implements SecurityContext {

    private SecurityContext securityContext;
    private String email;

    @Override
    public Principal getUserPrincipal() {
        return () -> email;
    }

    @Override
    public boolean isUserInRole(final String role) {
        return securityContext.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return securityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return securityContext.getAuthenticationScheme();
    }

}
