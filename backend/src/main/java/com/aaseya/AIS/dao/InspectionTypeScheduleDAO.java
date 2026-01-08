package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class InspectionTypeScheduleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void saveSchedule(Inspection_Type_Schedule schedule) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(schedule);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Inspection_Type_Schedule getScheduleById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Inspection_Type_Schedule.class, id);
        } finally {
            session.close();
        }
    }
}
