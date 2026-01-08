package com.aaseya.AIS.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Template;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class TempCheckInspDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // Fetch template by name
    public Template getTemplateByName(String templateName) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Template> query = builder.createQuery(Template.class);
            Root<Template> root = query.from(Template.class);

            Predicate predicate = builder.equal(root.get("template_name"), templateName);
            query.select(root).where(predicate);

            return session.createQuery(query).uniqueResult();
        }
    }

    // Fetch template by ID using CriteriaBuilder
    public Template getTemplateById(Long templateId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Template> query = builder.createQuery(Template.class);
            Root<Template> root = query.from(Template.class);

            Predicate predicate = builder.equal(root.get("template_id"), templateId);
            query.select(root).where(predicate);

            return session.createQuery(query).uniqueResult();
        }
    }

    public Checklist_Category getChecklistById(Long checklistId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Checklist_Category> query = builder.createQuery(Checklist_Category.class);
            Root<Checklist_Category> root = query.from(Checklist_Category.class);

            // Replace 'id' with the actual field name in Checklist_Category entity
            Predicate predicate = builder.equal(root.get("id"), checklistId);
            query.select(root).where(predicate);

            return session.createQuery(query).uniqueResult();
        }
    }


    // Fetch inspection type by ID using CriteriaBuilder
    public Inspection_Type getInspectionTypeById(Long inspectionTypeId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Inspection_Type> query = builder.createQuery(Inspection_Type.class);
            Root<Inspection_Type> root = query.from(Inspection_Type.class);

            Predicate predicate = builder.equal(root.get("ins_type_id"), inspectionTypeId); // Replace with the correct field name
            query.select(root).where(predicate);

            return session.createQuery(query).uniqueResult();
        }
    }

    // Save or update template
    public void saveTemplate(Template template) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(template);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save or update the template: " + e.getMessage(), e);
        }
    }
}
