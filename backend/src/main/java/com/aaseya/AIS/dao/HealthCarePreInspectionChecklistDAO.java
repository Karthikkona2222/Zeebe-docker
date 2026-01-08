package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.HealthCarePreInspectionChecklist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HealthCarePreInspectionChecklistDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public List<HealthCarePreInspectionChecklist> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthCarePreInspectionChecklist> cq = cb.createQuery(HealthCarePreInspectionChecklist.class);
        Root<HealthCarePreInspectionChecklist> root = cq.from(HealthCarePreInspectionChecklist.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

    public HealthCarePreInspectionChecklist findById(Long id) {
        return entityManager.find(HealthCarePreInspectionChecklist.class, id);
    }
}
