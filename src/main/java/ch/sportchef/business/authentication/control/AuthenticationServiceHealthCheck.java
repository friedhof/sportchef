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

import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;
import java.util.Optional;

class AuthenticationServiceHealthCheck extends HealthCheck {

    private final AuthenticationService authenticationService;

    AuthenticationServiceHealthCheck(@NotNull final AuthenticationService authenticationService) {
        super();
        this.authenticationService = authenticationService;
    }

    @Override
    protected Result check() {
        try {
            final Optional<String> token = authenticationService.validateChallenge("foo@bar", "foobar");
            return token.isPresent() ? Result.healthy() :
                    Result.unhealthy("Problems in AuthenticationService: Can't validate challenge!");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
