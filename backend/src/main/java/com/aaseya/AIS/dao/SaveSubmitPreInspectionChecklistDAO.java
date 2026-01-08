package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SaveSubmitPreInspectionChecklistDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SaveSubmitPreInspectionChecklist saveOrUpdate(SaveSubmitPreInspectionChecklist checklist) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(checklist); 
            transaction.commit();
            return checklist;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
    public List<SaveSubmitPreInspectionChecklist> getByInspectionId(long inspectionid) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM SaveSubmitPreInspectionChecklist WHERE id.inspectionid = :inspectionid", SaveSubmitPreInspectionChecklist.class)
                          .setParameter("inspectionid", inspectionid)
                          .getResultList();
        } finally {
            session.close();
        }
	
    }
    
}
