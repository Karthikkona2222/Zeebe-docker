package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.Inspection_SLA;
import com.aaseya.AIS.Model.Inspection_Type;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InspectionSLADAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Check if an Inspection_SLA exists for the given Inspection_Type and entity size.
     */
    public boolean existsByInspectionTypeAndEntitySize(Inspection_Type inspectionType, String entitySize) {
        try (Session session = sessionFactory.openSession()) {
            // Use Hibernate's CriteriaBuilder (HibernateCriteriaBuilder)
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();

            // Create a query to count rows
            var query = cb.createQuery(Long.class);

            // Define the root table (Inspection_SLA)
            var root = query.from(Inspection_SLA.class);

            // Create the query with a count and WHERE conditions
            query.select(cb.count(root))
                 .where(
                     cb.equal(root.get("inspectionType"), inspectionType),
                     cb.equal(root.get("entitySize"), entitySize)
                 );

            // Execute the query and return the result
            Long count = session.createQuery(query).uniqueResult();
            return count != null && count > 0;
        }
    }

    /**
     * Retrieve an Inspection_SLA for the given Inspection_Type and entity size.
     */
    public Inspection_SLA findByInspectionTypeAndEntitySize(Inspection_Type inspectionType, String entitySize) {
        try (Session session = sessionFactory.openSession()) {
            // Use Hibernate's CriteriaBuilder
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();

            // Create a query for Inspection_SLA
            var query = cb.createQuery(Inspection_SLA.class);

            // Define the root table (Inspection_SLA)
            var root = query.from(Inspection_SLA.class);

            // Create the query with WHERE conditions
            query.select(root)
                 .where(
                     cb.equal(root.get("inspectionType"), inspectionType),
                     cb.equal(root.get("entitySize"), entitySize)
                 );

            // Execute the query and return the result
            return session.createQuery(query).uniqueResult();
        }
    }

    /**
     * Save or update an Inspection_SLA in the database.
     */
    public void saveInspectionSLA(Inspection_SLA inspectionSLA) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Save or update the entity
            session.merge(inspectionSLA);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
