package com.aaseya.AIS.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import jakarta.persistence.TypedQuery;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class ControlTypeDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
	private final CriteriaPaginationUtil criteriaPaginationUtil;

    
    @Autowired
	public ControlTypeDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.criteriaPaginationUtil = paginationUtil;
	}
	public Page<ControlType> getAllControlTypes(Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ControlType> cq = cb.createQuery(ControlType.class);
		//Root<ControlType> root = cq.from(ControlType.class);
		//cq.select(root);

		return criteriaPaginationUtil.getPaginatedData(cq, pageable, ControlType.class, Function.identity());
	}
    
    @Transactional
    public ControlType saveControlType(ControlType controlType, List<Long> inspectionTypeIds) {
        // Persist the ControlType entity
        entityManager.persist(controlType);

        // Fetch the Inspection_Type entities by their IDs using Criteria API
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type> criteriaQuery = criteriaBuilder.createQuery(Inspection_Type.class);
        Root<Inspection_Type> root = criteriaQuery.from(Inspection_Type.class);
        criteriaQuery.select(root).where(root.get("ins_type_id").in(inspectionTypeIds));

        List<Inspection_Type> inspectionTypes = entityManager.createQuery(criteriaQuery).getResultList();

        // Map the Inspection_Type entities to the ControlType entity
        controlType.setInspectionTypes(new HashSet<>(inspectionTypes));

        // Update the ControlType with the mapped relationships
        entityManager.merge(controlType);

        return controlType;
    }

    public ControlType findByControlTypeName(String controlTypeName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ControlType> criteriaQuery = criteriaBuilder.createQuery(ControlType.class);
        Root<ControlType> root = criteriaQuery.from(ControlType.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("controlTypeName"), controlTypeName));

        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            return null; // No ControlType found with that name
        }
    }
    
    public ControlType findById(Long controlTypeId) {
        return entityManager.find(ControlType.class, controlTypeId);
    }
    
    @Transactional
    public ControlType getControlTypeWithInspectionDetails(Long controlTypeId) {
        // Fetch the ControlType entity with its associated Inspection_Type entities
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ControlType> criteriaQuery = criteriaBuilder.createQuery(ControlType.class);
        Root<ControlType> root = criteriaQuery.from(ControlType.class);

        // Use fetch to load the Inspection_Type relationship eagerly
        root.fetch("inspectionTypes", jakarta.persistence.criteria.JoinType.LEFT);

        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("controlTypeId"), controlTypeId));

        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            return null; // No ControlType found with the given ID
        }
    }
  //Get All ControlTypes Without Pagination
  	public List<ControlType> getControlTypes() {
  	    Session session = null;
  	    Transaction transaction = null;
  	    List<ControlType> controlTypes = new ArrayList<>();

  	    try {
  	        session = sessionFactory.openSession();
  	        transaction = session.beginTransaction();

  	        CriteriaBuilder builder = session.getCriteriaBuilder();
  	        CriteriaQuery<ControlType> query = builder.createQuery(ControlType.class);
  	        Root<ControlType> root = query.from(ControlType.class);

  	        query.select(root); // Fetch all records

  	        controlTypes = session.createQuery(query).getResultList();
  	        transaction.commit();
  	    } catch (Exception e) {
  	        if (transaction != null) {
  	            transaction.rollback();
  	        }
  	        e.printStackTrace();
  	    } finally {
  	        if (session != null) {
  	            session.close();
  	        }
  	    }

  	    return controlTypes;
  	}
    public String getControlTypeByInspectionType(String inspectionTypeName) {
        try {
            String jpql = "SELECT c FROM ControlType c JOIN c.inspectionTypes i " +
                          "WHERE LOWER(i.name) = LOWER(:inspectionTypeName) AND c.isActive = true";
            TypedQuery<ControlType> query = entityManager.createQuery(jpql, ControlType.class);
            query.setParameter("inspectionTypeName", inspectionTypeName);
            List<ControlType> controlTypes = query.getResultList();

            if (controlTypes.isEmpty()) {
                return null;
            }
            // Return the first control type name found
            return controlTypes.get(0).getControlTypeName();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
  }

