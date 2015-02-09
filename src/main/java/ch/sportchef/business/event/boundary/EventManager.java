package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.entity.Event;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

@Stateless
public class EventManager {

    @PersistenceContext
    private EntityManager em;

    public Event save(@NotNull final Event event) {
        return this.em.merge(event);
    }

}
