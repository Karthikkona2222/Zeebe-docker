package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.InspectionEscalation;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dto.InspectionEscalationDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class InspectionEscalationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional // Marking the method as transactional
    public void saveInspectionEscalation(InspectionEscalationDTO inspectionEscalationDTO) {
        Session session = null;
        try {
            session = sessionFactory.openSession(); // Open a new session
            session.beginTransaction(); // Begin the transaction

            // Create a new InspectionEscalation entity
            InspectionEscalation escalation = new InspectionEscalation();
            escalation.setEscalationMessage(inspectionEscalationDTO.getEscalationMessage());
            escalation.setInspectionId(inspectionEscalationDTO.getInspectionId()); // Assuming InspectionId corresponds to IncidentCaseId

            // Use CriteriaBuilder to fetch users based on IDs
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = cb.createQuery(Users.class);
            Root<Users> root = query.from(Users.class);

            // Fetch users whose IDs are in the list of userIds
            List<String> userIds = inspectionEscalationDTO.getUserIds();
            query.select(root).where(root.get("emailID").in(userIds));

            List<Users> users = session.createQuery(query).getResultList();
            escalation.setUsers(users);

            // Persist the escalation entity
            session.persist(escalation); // Persist the entity
            
            session.getTransaction().commit(); // Commit the transaction

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback if any exception occurs
            }
            e.printStackTrace(); // Log the exception
        } finally {
            if (session != null) {
                session.close(); // Close the session
            }
        }
    }

}