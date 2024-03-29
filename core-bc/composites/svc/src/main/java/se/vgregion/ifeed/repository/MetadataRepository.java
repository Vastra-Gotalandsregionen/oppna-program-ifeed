package se.vgregion.ifeed.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.types.CachedUser;
import se.vgregion.ifeed.types.Metadata;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class MetadataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Metadata getByName(String name) {
        Query query = entityManager.createQuery("select u from Metadata u where u.name = :name");
        query.setParameter("name", name);
        try {
            return (Metadata) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Metadata> getActiveItemsParent(String withName) {
        Query query = entityManager.createQuery("select c from Metadata u join Metadata c on u.children where u.name = :name and c.active");
        query.setParameter("name", withName);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public CachedUser merge(CachedUser cachedUser) {
        return entityManager.merge(cachedUser);
    }
}
