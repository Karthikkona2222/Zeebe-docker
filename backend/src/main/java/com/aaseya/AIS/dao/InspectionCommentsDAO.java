package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Approver_Reviewer_Comment;
import com.aaseya.AIS.dto.SaveInspectionDTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class InspectionCommentsDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public void saveInspectionComment(SaveInspectionDTO saveInspectionDTO) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			saveInspectionDTO.getApprover_Reviewer_Comment().forEach(e -> session.merge(e));
			transaction.commit();
			session.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Approver_Reviewer_Comment> getApproverReviewerComments(final long inspectionId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Approver_Reviewer_Comment> query = cb.createQuery(Approver_Reviewer_Comment.class);
		Root<Approver_Reviewer_Comment> root = query.from(Approver_Reviewer_Comment.class);
		
		query.where(cb.equal(root.get("id").get("inspectionID"), inspectionId));
		List<Approver_Reviewer_Comment> answerList = session.createQuery(query).getResultList();
		transaction.commit();
		session.close();
		return answerList;
	}
}
