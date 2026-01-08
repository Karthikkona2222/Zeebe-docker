package com.aaseya.AIS.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.UserSkill;
import com.aaseya.AIS.Model.Users;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Repository
public class UserSkillDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
	private EntityManager entityManager;

    public void save(UserSkill userSkill) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession(); // Open a new session
            transaction = session.beginTransaction(); // Begin a transaction

            session.merge(userSkill); // Use persist to save the UserSkill entity

            transaction.commit(); // Commit the transaction
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback transaction in case of failure
            }
            throw new RuntimeException("Failed to save UserSkill", e);
        } finally {
            if (session != null) {
                session.close(); // Close the session
            }
        }
    }
    public List<UserSkill> getUserSkillsByUserId(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserSkill> query = builder.createQuery(UserSkill.class);
            Root<UserSkill> root = query.from(UserSkill.class);
            query.select(root).where(builder.equal(root.get("user").get("id"), userId));
            
            List<UserSkill> result = session.createQuery(query).getResultList();
            System.out.println("Fetched UserSkills: " + result); // Debug log
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching UserSkills by userId: " + userId, e);
        }
    }
    
 // Get UserSkill by userId and skillId
 	public UserSkill getUserSkillByUserIdAndSkillId(Long userId, Long skillId) {
 		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
 		CriteriaQuery<UserSkill> cq = cb.createQuery(UserSkill.class);
 		Root<UserSkill> userSkillRoot = cq.from(UserSkill.class);

 		Join<UserSkill, Users> userJoin = userSkillRoot.join("user", JoinType.INNER);
 		Join<UserSkill, Skill> skillJoin = userSkillRoot.join("skill", JoinType.INNER);

 		Predicate userPredicate = cb.equal(userJoin.get("userID"), userId);
 		Predicate skillPredicate = cb.equal(skillJoin.get("skillId"), skillId);

 		cq.select(userSkillRoot).where(cb.and(userPredicate, skillPredicate));

 		TypedQuery<UserSkill> query = entityManager.createQuery(cq);
 		return query.getResultStream().findFirst().orElse(null); // Return the first result or null
 	}

 	public void delete(UserSkill userSkill) {
 		try {
 			entityManager.remove(userSkill); // Using entityManager to remove the entity
 		} catch (Exception e) {
 			throw new RuntimeException("Failed to delete UserSkill", e);
 		}
 	}

 	// Find UserSkills by userId
 	public List<UserSkill> findByUserId(Long userId) {
 		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
 		CriteriaQuery<UserSkill> cq = cb.createQuery(UserSkill.class);
 		Root<UserSkill> userSkillRoot = cq.from(UserSkill.class);

 		// Add join on the 'user' entity
 		Join<UserSkill, Users> userJoin = userSkillRoot.join("user", JoinType.INNER);

 		// Add condition to filter by userId
 		Predicate userPredicate = cb.equal(userJoin.get("userID"), userId);

 		// Combine the predicates
 		cq.select(userSkillRoot).where(userPredicate);

 		// Execute the query
 		TypedQuery<UserSkill> query = entityManager.createQuery(cq);
 		return query.getResultList(); // Return the list (which may be empty)
 	}

 	public void deleteUserSkills(List<UserSkill> userSkills) {
 		Session session = null;
 		Transaction transaction = null;
 		try {
 			session = sessionFactory.openSession(); // Open a new session
 			transaction = session.beginTransaction(); // Begin a transaction
 			for (UserSkill userSkill : userSkills) {
 				userSkill = session.get(UserSkill.class, userSkill.getId());
 				session.remove(userSkill);
 			}

 			transaction.commit(); // Commit the transaction
 		} catch (Exception e) {
 			if (transaction != null) {
 				transaction.rollback(); // Rollback transaction in case of failure
 			}
 			throw new RuntimeException("Failed to save UserSkill", e);
 		} finally {
 			if (session != null) {
 				session.close(); // Close the session
 			}

 		}
 	}

}
