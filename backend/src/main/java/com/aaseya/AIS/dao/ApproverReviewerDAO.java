package com.aaseya.AIS.dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.aaseya.AIS.Model.Approver_Reviewer_Comment;

@Repository
public class ApproverReviewerDAO {

///////////Save  the inspection for approver////////////	

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public List<Approver_Reviewer_Comment> findByInspectionId(final long inspectionID) {
		return getCurrentSession().createQuery("from Approver_Reviewer_Comment where id.inspectionID = :inspectionID")
				.setParameter("inspectionID", inspectionID).list();
	}

	public void save(Approver_Reviewer_Comment approverReviewerComments) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.merge(approverReviewerComments);			
		} finally {
			transaction.commit();
			session.close();
		}
	}
}
