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
package ch.sportchef.business.authentication.entity;

import org.jetbrains.annotations.Nullable;
import org.picketlink.idm.credential.AbstractBaseCredentials;

import javax.validation.constraints.NotNull;

/**
 * <p>A simple credential that uses a token as a credential.</p>
 */
public class SimpleTokenCredential extends AbstractBaseCredentials {

    @Nullable
    private String token;

    @SuppressWarnings("NullableProblems")
    public SimpleTokenCredential(@NotNull final String token) {
        this.token = token;
    }

    @Override
    public void invalidate() {
        token = null;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return String.format("SimpleTokenCredential{token='%s'}", token); //NON-NLS
    }
}
