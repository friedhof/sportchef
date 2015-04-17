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

import ch.sportchef.business.user.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.List;

@Stateless
public class UserManager {

    @PersistenceContext
    private EntityManager em;

    public User save(@NotNull final User user) {
        return this.em.merge(user);
    }

    public User findByUserId(final long userId) {
        return this.em.find(User.class, userId);
    }

    public List<User> findAll() {
        final CriteriaBuilder cb = this.em.getCriteriaBuilder();
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> rootEntry = cq.from(User.class);
        final CriteriaQuery<User> all = cq.select(rootEntry);
        final TypedQuery<User> allQuery = this.em.createQuery(all);
        return allQuery.getResultList();
    }

    public void delete(final long userId) {
        final User reference = em.getReference(User.class, userId);
        em.remove(reference);
    }
}
