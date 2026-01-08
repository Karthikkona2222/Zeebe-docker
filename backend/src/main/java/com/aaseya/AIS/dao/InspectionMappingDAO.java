package com.aaseya.AIS.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionMapping;
import com.aaseya.AIS.Model.Template;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InspectionMappingDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Save Inspection Mapping Entry
    public void saveInspectionMapping(InspectionMapping inspectionMapping) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(inspectionMapping);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Fetch Inspection Mapping by Inspection ID
    public List<InspectionMapping> getMappingsByInspectionId(Long inspectionId) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<InspectionMapping> cq = cb.createQuery(InspectionMapping.class);
            Root<InspectionMapping> root = cq.from(InspectionMapping.class);
            cq.select(root).where(cb.equal(root.get("inspectionID"), inspectionId));

            return session.createQuery(cq).getResultList();
        } finally {
            session.close();
        }
    }

    // Fetch Inspection Mapping by Inspector ID
    public List<InspectionMapping> getMappingsByInspectorId(String inspectorId) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<InspectionMapping> cq = cb.createQuery(InspectionMapping.class);
            Root<InspectionMapping> root = cq.from(InspectionMapping.class);
            cq.select(root).where(cb.equal(root.get("inspectorID"), inspectorId));

            return session.createQuery(cq).getResultList();
        } finally {
            session.close();
        }
    }
    
  //Save inpectionId, inspectorId, Category ids in inspectionMapping --Starting
    @PersistenceContext
    private EntityManager entityManager;

    // Fetch template_id based on inspectionId
    public Long getTemplateIdByInspectionId(Long inspectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<InspectionCase> root = query.from(InspectionCase.class);

        query.select(root.get("template_id")) // Ensure this matches your entity field
             .where(cb.equal(root.get("inspectionID"), inspectionId));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        List<Long> result = typedQuery.getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    // Fetch checklist_cat_ids based on template_id by joining with Template entity
    public List<Long> getChecklistCategoryIdsByTemplateId(Long templateId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Checklist_Category> checklistRoot = query.from(Checklist_Category.class);
        Join<Checklist_Category, Template> templateJoin = checklistRoot.join("templates"); // Ensure field name matches entity

        query.select(checklistRoot.get("checklist_cat_id")) // Ensure this matches your entity field
             .where(cb.equal(templateJoin.get("template_id"), templateId));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    // Save inspection mapping records
    @Transactional
    public void updateInspectionMapping(List<InspectionMapping> mappings) {
        for (InspectionMapping mapping : mappings) {
            entityManager.merge(mapping);
        }
    }
    
  //Save inpectionId, inspectorId, Category ids in inspectionMapping -- Ending
    
    public String getInspectorID(Long inspectionId, Long categoryId) {
        Session session = sessionFactory.openSession();
        String inspectorID = null;
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<InspectionMapping> query = cb.createQuery(InspectionMapping.class);
            Root<InspectionMapping> root = query.from(InspectionMapping.class);
            
            query.where(cb.equal(root.get("id").get("inspectionID"), inspectionId),
                        cb.equal(root.get("id").get("checklistCatID"), categoryId));
            
            List<InspectionMapping> results = session.createQuery(query).getResultList();
            if (!results.isEmpty()) {
                inspectorID = results.get(0).getInspectorID();
            }
        } finally {
            session.close();
        }
        return inspectorID;
    }
    
    public List<Long> getCategoryByInspectorId(String inspectorId , Long inspectionId) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<InspectionMapping> cq = cb.createQuery(InspectionMapping.class);
            Root<InspectionMapping> root = cq.from(InspectionMapping.class);
            cq.select(root).where(cb.equal(root.get("inspectorID"), inspectorId),cb.equal(root.get("id").get("inspectionID"), inspectionId));

            return session.createQuery(cq).getResultList().stream().map(mapping->mapping.getId().getChecklistCatID()).collect(Collectors.toList());
        } finally {
            session.close();
        }
    }
    
}

