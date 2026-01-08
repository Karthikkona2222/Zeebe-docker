package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.PolicyDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PolicyDetailsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public Page<PolicyDetails> findAll(Pageable pageable) {
		String query = "SELECT DISTINCT p FROM PolicyDetails p LEFT JOIN FETCH p.claimRequestDocuments";

		List<PolicyDetails> resultList = entityManager.createQuery(query, PolicyDetails.class)
				.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		Long count = entityManager.createQuery("SELECT COUNT(DISTINCT p) FROM PolicyDetails p", Long.class)
				.getSingleResult();

		return new PageImpl<>(resultList, pageable, count);
	}
	
	public PolicyDetails findById(String policyId) {
	    if (policyId == null || policyId.trim().isEmpty()) {
	        return null;
	    }

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<PolicyDetails> cq = cb.createQuery(PolicyDetails.class);
	    Root<PolicyDetails> root = cq.from(PolicyDetails.class);

	    cq.select(root).where(cb.equal(root.get("policyId"), policyId));

	    TypedQuery<PolicyDetails> query = entityManager.createQuery(cq);

	    List<PolicyDetails> results = query.getResultList();
	    return results.isEmpty() ? null : results.get(0);
	}

}