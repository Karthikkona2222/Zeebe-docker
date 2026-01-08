package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.HealthcareChecklistandAnswers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class HealthcareChecklistandAnswersDAO {
	
	@PersistenceContext
    private EntityManager entityManager;
	
    private final SessionFactory sessionFactory;
    
	
	@Transactional
    public void save(HealthcareChecklistandAnswers answer) {
        entityManager.persist(answer);
    }
	
	@Transactional
    public List<HealthcareChecklistandAnswers> findByClaimId(Long claimId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthcareChecklistandAnswers> cq = cb.createQuery(HealthcareChecklistandAnswers.class);
        Root<HealthcareChecklistandAnswers> root = cq.from(HealthcareChecklistandAnswers.class);
        cq.select(root).where(
            cb.equal(root.get("claimCase").get("claimId"), claimId)
        );
        return entityManager.createQuery(cq).getResultList();
    }
    
    // CriteriaBuilder with multiple criteria example:
	@Transactional
    public List<HealthcareChecklistandAnswers> findByChecklistIdAndCategoryId(Long checklistId, Long categoryId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthcareChecklistandAnswers> cq = cb.createQuery(HealthcareChecklistandAnswers.class);
        Root<HealthcareChecklistandAnswers> root = cq.from(HealthcareChecklistandAnswers.class);
        cq.select(root).where(
            cb.and(
                cb.equal(root.get("category").get("categoryId"), categoryId),
                cb.equal(root.get("checklistItem").get("checklistId"), checklistId)
            )
        );
        return entityManager.createQuery(cq).getResultList();
    }
    
	@Transactional
	public List<HealthcareChecklistandAnswers> findByChecklistIdAndCategoryIdAndClaimId(Long checklistId, Long categoryId, Long claimId) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<HealthcareChecklistandAnswers> cq = cb.createQuery(HealthcareChecklistandAnswers.class);
	    Root<HealthcareChecklistandAnswers> root = cq.from(HealthcareChecklistandAnswers.class);
	    cq.select(root).where(
	        cb.and(
	            cb.equal(root.get("checklistItem").get("checklistId"), checklistId),
	            cb.equal(root.get("category").get("categoryId"), categoryId),
	            cb.equal(root.get("claimCase").get("claimId"), claimId)
	        )
	    );
	    return entityManager.createQuery(cq).getResultList();
	}
	
	public HealthcareChecklistandAnswersDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
 
    /**
     * Fetch all checklist answers for a given claimId,
     * including category and checklistItem (to avoid LazyInitializationException).
     */
    public List<HealthcareChecklistandAnswers> getByClaimId(Long claimId) {
        Session session = sessionFactory.openSession();
 
        List<HealthcareChecklistandAnswers> list = session.createQuery(
                "select h from HealthcareChecklistandAnswers h " +
                "join fetch h.category " +
                "join fetch h.checklistItem " +
                "where h.claimCase.claimId = :claimId",
                HealthcareChecklistandAnswers.class)
                .setParameter("claimId", claimId)
                .list();
 
        session.close();
        return list;
    }

    
    public List<HealthcareChecklistandAnswers> getByClaimIdWithCategoryAndChecklistItem(Long claimId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HealthcareChecklistandAnswers> cq = cb.createQuery(HealthcareChecklistandAnswers.class);
        Root<HealthcareChecklistandAnswers> root = cq.from(HealthcareChecklistandAnswers.class);

        root.fetch("category", JoinType.LEFT);
        root.fetch("checklistItem", JoinType.LEFT);
        root.fetch("claimCase", JoinType.LEFT);

        cq.select(root).where(cb.equal(root.get("claimCase").get("claimId"), claimId));

        return entityManager.createQuery(cq).getResultList();
    }


}
