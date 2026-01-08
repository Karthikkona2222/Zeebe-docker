package com.aaseya.AIS.dao;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.InspectionCase;
@Repository
public class StatusDAO {
    @Autowired
    private SessionFactory sessionFactory;
    public void saveInspectionCase(InspectionCase inspectionCase) {
    	System.out.println("print");
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
       System.out.println("print");
        try {
            session.persist(inspectionCase);
            System.out.println(inspectionCase);
            transaction.commit();
        } catch (Exception e) {
        	e.printStackTrace();
 
        } finally {
            session.close();
        }
    }
    public List<InspectionCase> findInspectionCasesByStatus(String createdBy) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<InspectionCase> inspectionCases = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM InspectionCase ic WHERE ic.createdBy = :createdBy and status IN ('in_progress','pending','new')";
            Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
            query.setParameter("createdBy", createdBy);
            inspectionCases = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return inspectionCases;
    }
    }