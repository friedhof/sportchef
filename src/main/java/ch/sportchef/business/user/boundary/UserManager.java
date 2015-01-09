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
        return em.merge(user);
    }

    public User findByUserId(final long userId) {
        return em.find(User.class, userId);
    }

    public List<User> findAll() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> rootEntry = cq.from(User.class);
        final CriteriaQuery<User> all = cq.select(rootEntry);
        final TypedQuery<User> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }
}
