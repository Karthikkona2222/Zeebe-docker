package com.aaseya.AIS.dao;
 
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
@Repository
public class InspectionPeriodicityDAO {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void save(Inspection_Type_Schedule periodicity) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.merge(periodicity);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
 
    public Inspection_Type_Schedule findByInspectionTypeId(Long inspectionTypeId) {
        try (Session session = sessionFactory.openSession()) {

            // Get CriteriaBuilder
            var cb = session.getCriteriaBuilder();

            // Create query for Inspection_Type_Schedule
            var cq = cb.createQuery(Inspection_Type_Schedule.class);

            // Define root entity
            var root = cq.from(Inspection_Type_Schedule.class);

            // Add where clause: inspectionType.id = :inspectionTypeId
            cq.select(root).where(
                cb.equal(root.get("inspectionType").get("id"), inspectionTypeId)
            );

            // Execute query
            return session.createQuery(cq).uniqueResult();
        }
    }
}
 
 