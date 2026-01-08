package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.HealthCareChecklistItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HealthCareChecklistItemDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<HealthCareChecklistItem> findByCategoryId(Long categoryId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthCareChecklistItem> cq = cb.createQuery(HealthCareChecklistItem.class);
        Root<HealthCareChecklistItem> root = cq.from(HealthCareChecklistItem.class);

        Join<?, ?> categoryJoin = root.join("category", JoinType.INNER);
        Predicate predicate = cb.equal(categoryJoin.get("categoryId"), categoryId);

        cq.select(root).where(predicate);

        return entityManager.createQuery(cq).getResultList();
    }

    public HealthCareChecklistItem findById(Long id) {
        return entityManager.find(HealthCareChecklistItem.class, id);
    }
    
    public List<HealthCareChecklistItem> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthCareChecklistItem> cq = cb.createQuery(HealthCareChecklistItem.class);
        Root<HealthCareChecklistItem> root = cq.from(HealthCareChecklistItem.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

}
