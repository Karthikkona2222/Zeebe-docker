package com.aaseya.AIS.dao;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionPlan;
import com.aaseya.AIS.dto.InspectionPlanDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;


@Repository
public class InspectionPlanDAO {
    
	@PersistenceContext
	private final EntityManager entityManager;
	
	@Autowired
    private final CriteriaPaginationUtil criteriaPaginationUtil;
    
	@Autowired
    public InspectionPlanDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
        this.entityManager = entityManager;
        this.criteriaPaginationUtil = paginationUtil;
    }
	
	

	public Page<InspectionPlan> getAllInspectionPlans(Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionPlan> cq = cb.createQuery(InspectionPlan.class);
        //Root<InspectionPlan> root = cq.from(InspectionPlan.class);
       //cq.select(root).orderBy(cb.asc(root.get("inspectionPlanId")));

        return criteriaPaginationUtil.getPaginatedData(cq, pageable, InspectionPlan.class, Function.identity());
    }
	
	
	  @Transactional
	    public InspectionPlan save(InspectionPlan inspectionPlan) {
	        entityManager.persist(inspectionPlan);
	        entityManager.flush(); // Ensure the ID is generated and available
	        return inspectionPlan; // Return the managed entity with ID
	    }
	  
	  @Transactional
	    public InspectionPlan findByIdWithCases(String inspectionPlanId) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<InspectionPlan> cq = cb.createQuery(InspectionPlan.class);
	        Root<InspectionPlan> inspectionPlanRoot = cq.from(InspectionPlan.class);

	        // Join with InspectionCase
	        Join<InspectionPlan, InspectionCase> inspectionCaseJoin = inspectionPlanRoot.join("inspectionCases");

	        // Set the selection and where clause
	        cq.select(inspectionPlanRoot)
	          .where(cb.equal(inspectionPlanRoot.get("inspectionPlanId"), inspectionPlanId));

	        // Execute the query
	        return entityManager.createQuery(cq).getSingleResult();
	    }
	  
	  public boolean existsByInspectionPlanName(String inspectionPlanName) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Long> query = cb.createQuery(Long.class);
	        Root<InspectionPlan> root = query.from(InspectionPlan.class);

	        query.select(cb.count(root));
	        query.where(cb.equal(root.get("inspectionPlanName"), inspectionPlanName));

	        Long count = entityManager.createQuery(query).getSingleResult();
	        return count > 0;
	    }
	  
	  @Transactional
		public InspectionPlan findById(String inspectionPlanId) {
			return entityManager.find(InspectionPlan.class, inspectionPlanId);
		}
}
  

    
   
