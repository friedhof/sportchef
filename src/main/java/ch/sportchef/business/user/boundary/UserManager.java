package ch.sportchef.business.user.boundary;

import ch.sportchef.business.user.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

@Stateless
public class UserManager {

    @PersistenceContext
    private EntityManager em;

    public User save(@NotNull final User user) {
        return em.merge(user);
    }

}
