package com.aaseya.AIS.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class PeriodicityDetailsByInspectionTypeDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * ✅ Fetch entity details from the NewEntity table by entityId.
     */
    public NewEntity getEntityById(String entityId) {
        return entityManager.find(NewEntity.class, entityId);
    }
 
    /**
     * ✅ Fetch inspection type details from Inspection_Type table by ins_type_id.
     */
    public Inspection_Type getInspectionTypeById(Long insTypeId) {
        return entityManager.find(Inspection_Type.class, insTypeId);
    }
 
    /**
     * ✅ Fetch all schedules from Inspection_Type_Schedule where entityId matches.
     * 
     * Uses Criteria API:
     *  - CriteriaBuilder creates a type-safe query
     *  - Root represents the entity (Inspection_Type_Schedule)
     *  - cb.equal() applies a WHERE filter for entityId
     */
    public List<Inspection_Type_Schedule> getSchedulesByEntityId(String entityId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type_Schedule> cq = cb.createQuery(Inspection_Type_Schedule.class);
        Root<Inspection_Type_Schedule> root = cq.from(Inspection_Type_Schedule.class);
 
        cq.select(root).where(cb.equal(root.get("entityId"), entityId));
 
        return entityManager.createQuery(cq).getResultList();
    }
}