package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.NewEntity;
import com.itextpdf.layout.element.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class NewEntityDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // Method to find NewEntity by ID
    public NewEntity findById(String entityId) {
        return entityManager.find(NewEntity.class, entityId);
    }

    // Method to save a new NewEntity
    public void save(NewEntity newEntity) {
        entityManager.persist(newEntity);
    }

    // Method to update an existing NewEntity
    public void update(NewEntity newEntity) {
        entityManager.merge(newEntity);
    }

    // Method to delete a NewEntity by ID
    public void deleteById(String entityId) {
        NewEntity entity = findById(entityId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    public boolean existsById(String entityId) {
        String jpql = "SELECT COUNT(e) FROM NewEntity e WHERE e.entityid = :entityId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("entityId", entityId); // Pass the String entityId
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }
    
//    NewEntity entity = entityManager.find(NewEntity.class, entityId);
//    if (entity != null) {
//        entity.setViolation_Percentage(violationPercentage);
//        entityManager.merge(entity);
//    }
//    // Custom query example: Find all entities with a specific condition
//    public List<NewEntity> findAllByCondition(String someCondition) {
//        String jpql = "SELECT e FROM NewEntity e WHERE e.someField = :condition";
//        TypedQuery<NewEntity> query = entityManager.createQuery(jpql, NewEntity.class);
//        query.setParameter("condition", someCondition);
//        return query.getResultList();
//    }
}