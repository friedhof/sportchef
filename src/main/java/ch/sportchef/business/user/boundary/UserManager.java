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
package ch.sportchef.business.user.boundary;

import ch.sportchef.business.exception.ExpectationFailedException;
import ch.sportchef.business.user.entity.User;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

@Stateless
public class UserManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    private final AtomicLong userSeq = new AtomicLong(0);

    public User create(@NotNull final User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new ExpectationFailedException("Email address has to be unique");
        }
        final Long userId = userSeq.incrementAndGet();
        final User userToCreate = new User(userId, user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail());
        this.users.put(userId, userToCreate);
        return userToCreate;
    }

    public User update(@NotNull final User user) {
        this.users.put(user.getUserId(), user);
        return user;
    }

    public Optional<User> findByUserId(@NotNull final Long userId) {
        return Optional.ofNullable(this.users.get(userId));
    }

    public Optional<User> findByEmail(@NotNull final String email) {
        return this.users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findAny();
    }

    public List<User> findAll() {
        return this.users.values().stream()
                .sorted(comparingLong(User::getUserId))
                .collect(toList());
    }

    public void delete(final Long userId) {
        this.users.remove(userId);
    }
}
