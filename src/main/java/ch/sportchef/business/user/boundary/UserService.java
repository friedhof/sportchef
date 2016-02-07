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
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.user.entity.User;
import pl.setblack.airomem.core.SimpleController;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Named
public class UserService {

    private SimpleController<UserManager> manager =
            PersistenceManager.createSimpleController(User.class, UserManager::new);

    public User create(@NotNull final User user) {
        return manager.executeAndQuery((mgr) -> mgr.create(user));
    }

    public User update(@NotNull final User user) {
        return manager.executeAndQuery((mgr) -> mgr.update(user));
    }

    public Optional<User> findByUserId(@NotNull final Long userId) {
        return manager.readOnly().findByUserId(userId);
    }

    public Optional<User> findByEmail(@NotNull final String email) {
        return manager.readOnly().findByEmail(email);
    }

    public List<User> findAll() {
        return manager.readOnly().findAll();
    }

    public void delete(final Long userId) {
        manager.execute((mgr) -> mgr.delete(userId));
    }

}
