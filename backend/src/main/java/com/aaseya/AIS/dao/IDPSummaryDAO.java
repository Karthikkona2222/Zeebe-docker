package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.IDPSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IDPSummaryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // -----------------------------------------
    // SAVE METHOD
    // -----------------------------------------
    @Transactional
    public IDPSummary save(IDPSummary summary) {
        entityManager.persist(summary);
        return summary;
    }

    // -----------------------------------------
    // FIND BY ID USING CriteriaBuilder
    // -----------------------------------------
    public IDPSummary findById(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IDPSummary> cq = cb.createQuery(IDPSummary.class);

        Root<IDPSummary> root = cq.from(IDPSummary.class);
        cq.select(root).where(cb.equal(root.get("id"), id));

        return entityManager.createQuery(cq).getSingleResult();
    }
    // FIND BY processInstanceKey  (for idempotency)
    // -----------------------------------------
    public IDPSummary findByProcessInstanceKey(Long processInstanceKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IDPSummary> cq = cb.createQuery(IDPSummary.class);

        Root<IDPSummary> root = cq.from(IDPSummary.class);
        cq.select(root).where(cb.equal(root.get("processInstanceKey"), processInstanceKey));

        TypedQuery<IDPSummary> query = entityManager.createQuery(cq);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
   
}
