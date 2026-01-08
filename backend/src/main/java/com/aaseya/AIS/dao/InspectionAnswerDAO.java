package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.InspectionChecklistandAnswers;
import com.aaseya.AIS.dto.SaveInspectionDTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;



@Repository
public class InspectionAnswerDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public void saveInspectionAnswers(SaveInspectionDTO saveInspectionDTO) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			List<InspectionChecklistandAnswers> inspectionChecklistandAnswers = saveInspectionDTO.getInspectionChecklistandAnswers();
			inspectionChecklistandAnswers.forEach(e -> session.merge(e));
			transaction.commit();
			session.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<InspectionChecklistandAnswers> getInspectionChecklistAnswers(final long inspectionId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<InspectionChecklistandAnswers> query = cb.createQuery(InspectionChecklistandAnswers.class);
		Root<InspectionChecklistandAnswers> root = query.from(InspectionChecklistandAnswers.class);
		
		query.where(cb.equal(root.get("id").get("inspectionID"), inspectionId));
		List<InspectionChecklistandAnswers> answerList = session.createQuery(query).getResultList();
		transaction.commit();
		session.close();
		return answerList;
	}

}
