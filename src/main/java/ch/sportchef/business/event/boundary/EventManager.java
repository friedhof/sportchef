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
