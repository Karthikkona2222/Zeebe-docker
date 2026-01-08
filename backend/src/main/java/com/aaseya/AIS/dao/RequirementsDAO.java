package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.Requirements;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RequirementsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Requirements findRequirementByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Requirements> query = cb.createQuery(Requirements.class);
        Root<Requirements> root = query.from(Requirements.class);
        query.select(root).where(cb.equal(root.get("name"), name));
        return entityManager.createQuery(query).getResultStream().findFirst().orElse(null);
    }
    
    public List<Requirements> findRequirementsByIds(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Requirements> query = cb.createQuery(Requirements.class);
        Root<Requirements> root = query.from(Requirements.class);
        query.select(root).where(root.get("id").in(ids));
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public void saveRequirement(Requirements requirement) {
        entityManager.persist(requirement);
    }
}
