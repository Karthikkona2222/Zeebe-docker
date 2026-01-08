package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.HealthCareChecklistCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HealthCareChecklistCategoryDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<HealthCareChecklistCategory> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthCareChecklistCategory> cq = cb.createQuery(HealthCareChecklistCategory.class);
        Root<HealthCareChecklistCategory> root = cq.from(HealthCareChecklistCategory.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

    public HealthCareChecklistCategory findById(Long id) {
        return entityManager.find(HealthCareChecklistCategory.class, id);
    }
}
