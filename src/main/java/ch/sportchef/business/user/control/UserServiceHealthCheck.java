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
package ch.sportchef.business.user.control;

import ch.sportchef.business.user.entity.User;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;
import java.util.List;

class UserServiceHealthCheck extends HealthCheck {

    private final UserService userService;

    UserServiceHealthCheck(@NotNull final UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    protected Result check() {
        try {
            final List<User> users = userService.findAll();
            return users != null ? Result.healthy() : Result.unhealthy("Can't access users!");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
