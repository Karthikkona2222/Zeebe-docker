package com.aaseya.AIS.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.InspectionCase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class getGroupCasesForLeadDAO {
	
	@PersistenceContext
    private EntityManager entityManager;

	public List<InspectionCase> getGroupCaseForLeadInspector(long leadId) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<InspectionCase> query = cb.createQuery(InspectionCase.class);
	    Root<InspectionCase> root = query.from(InspectionCase.class);

	    // Create predicates for leadId and caseCreationType values
	    Predicate leadIdPredicate = cb.equal(root.get("leadId"), leadId);

	    // Ensure caseCreationType is of type String before applying "in" filter
	    Predicate caseCreationTypePredicate;
	    if (root.get("caseCreationType").getJavaType().equals(String.class)) {
	        caseCreationTypePredicate = root.get("caseCreationType").in("plan", "group");
	    } else {
	        throw new IllegalArgumentException("caseCreationType should be a String, but found a different type.");
	    }

	    // Apply filters
	    query.select(root).where(cb.and(leadIdPredicate, caseCreationTypePredicate));

	    return entityManager.createQuery(query).getResultList();
	}

    }


