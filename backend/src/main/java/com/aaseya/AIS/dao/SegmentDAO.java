package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.Model.SubSegment;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;



@Repository
public class SegmentDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void saveSegment(Segment segment) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		
		session.persist(segment);
		transaction.commit();
		session.close();
	}
	
	public void saveSubSegment(SubSegment subSegment) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		
		session.persist(subSegment);
		transaction.commit();
		session.close();
	}
	
	public void updateSubSegment(SubSegment subSegment) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		
		session.merge(subSegment);
		transaction.commit();
		session.close();
	}
	
	public List<SubSegment> getSubSegmentByName(List<String> subSegmentNames) {
		Session session = sessionFactory.openSession();
//		Transaction transaction = session.getTransaction();
//		transaction.begin();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<SubSegment> cr = cb.createQuery(SubSegment.class);
		Root<SubSegment> root = cr.from(SubSegment.class);
		In<String> inClause = cb.in(root.get("name"));
		for (String subSegmentName : subSegmentNames) {
		    inClause.value(subSegmentName);
		}
		
		cr.select(root).where(root.get("name").in(subSegmentNames));
//		cr.select(root).where(inClause);
		
		List<SubSegment> subSegments = session.createQuery(cr).getResultList();		
				
		session.close();
		return subSegments;
	}

}
