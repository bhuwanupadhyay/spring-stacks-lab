package org.acme.quickstart;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class AwesomeService {

    @PersistenceContext
    EntityManager entityManager;

    public Awesome getAwesome(Long id) {
        return entityManager.find(Awesome.class, id);
    }

    public Awesome createAwesome(Awesome awesome) {
        entityManager.persist(awesome);
        return awesome;
    }

    public Awesome updateAwesome(Long id, Awesome awesome) {
        Awesome awesomeToUpdate = entityManager.find(Awesome.class, id);
        awesomeToUpdate.setName(awesome.getName());
        return awesomeToUpdate;
    }

    public void deleteAwesome(Long id) {
        Awesome awesomeToDelete = entityManager.find(Awesome.class, id);
        entityManager.remove(awesomeToDelete);
    }

    public List<Awesome> listAll() {
        return entityManager.createQuery("FROM Awesome", Awesome.class)
                            .getResultList();
    }
}
