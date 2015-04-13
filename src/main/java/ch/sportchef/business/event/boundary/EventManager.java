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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

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
public class EventManager {

    @PersistenceContext
    private EntityManager em;

    public Event save(@NotNull final Event event) {
        return this.em.merge(event);
    }

    public Event findByEventId(final long eventId) {
        return this.em.find(Event.class, eventId);
    }

    public List<Event> findAll() {
        final CriteriaBuilder cb = this.em.getCriteriaBuilder();
        final CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        final Root<Event> rootEntry = cq.from(Event.class);
        final CriteriaQuery<Event> all = cq.select(rootEntry);
        final TypedQuery<Event> allQuery = this.em.createQuery(all);
        return allQuery.getResultList();
    }

    public void delete(final long eventId) {
        final Event reference = em.getReference(Event.class, eventId);
        em.remove(reference);
    }
}
