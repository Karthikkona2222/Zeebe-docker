package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.ClaimRequestDocuments;
import com.aaseya.AIS.Model.PolicyDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ClaimRequestDocumentsDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public ClaimRequestDocuments saveClaimRequestDocument(ClaimRequestDocuments document) {
		entityManager.persist(document);
		return document;
	}

	public PolicyDetails findPolicyById(String policyId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PolicyDetails> query = cb.createQuery(PolicyDetails.class);
		Root<PolicyDetails> root = query.from(PolicyDetails.class);
		query.select(root).where(cb.equal(root.get("policyId"), policyId));

		List<PolicyDetails> result = entityManager.createQuery(query).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	public List<PolicyDetails> findByPolicyIdOrCustomerName(String policyId, String customerName) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PolicyDetails> query = cb.createQuery(PolicyDetails.class);
		Root<PolicyDetails> root = query.from(PolicyDetails.class);

		query.select(root).where(
				cb.or(cb.equal(root.get("policyId"), policyId), cb.equal(root.get("customerName"), customerName)));

		return entityManager.createQuery(query).getResultList();
	}
}