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

import ch.sportchef.business.authentication.entity.SimpleTokenCredential;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.basic.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * <p>A simple authenticator that supports two credential types: username/password or a simple token.</p>
 */
@RequestScoped
@PicketLink
public class CustomAuthenticator extends BaseAuthenticator {

    @Inject
    private DefaultLoginCredentials credentials;

    @Override
    public void authenticate() {
        if (credentials.getCredential() == null) {
            return;
        }

        final SimpleTokenCredential customCredential = (SimpleTokenCredential) credentials.getCredential();
        final String token = customCredential.getToken();

        if ("valid_token".equals(token)) {
            successfulAuthentication();
        }
    }

    private User getDefaultUser() {
        return new User("jane");
    }

    private void successfulAuthentication() {
        setStatus(AuthenticationStatus.SUCCESS);
        setAccount(getDefaultUser());
    }

}
