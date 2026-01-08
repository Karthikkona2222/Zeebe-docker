package com.aaseya.AIS.dao;

import jakarta.persistence.EntityManager;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import com.aaseya.AIS.Model.UserGroup;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dto.UserGroupDTO;
import com.aaseya.AIS.dto.UsersGroupDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserGroupDAO {

	@PersistenceContext
	private EntityManager entityManager;

    @Transactional
    public UserGroup addOrUpdateUserGroup(String action, Long groupId, String groupName, String description, List<Long> userIds) {
        UserGroup userGroup;

        if ("edit".equalsIgnoreCase(action)) {
            userGroup = entityManager.find(UserGroup.class, groupId);
            if (userGroup == null) {
                throw new RuntimeException("User Group not found with id: " + groupId);
            }

            if (isGroupNameExists(groupName, groupId)) {
                throw new RuntimeException("Group name already exists");
            }

        } else { 
            if (isGroupNameExists(groupName, null)) {
                throw new RuntimeException("Group name already exists");
            }
            userGroup = new UserGroup(); 
        }

       
        userGroup.setGroupName(groupName);
        userGroup.setDescription(description);
        userGroup.setActive(true);

       
        Set<Users> users = new HashSet<>();
        for (Long userId : userIds) {
            Users user = entityManager.find(Users.class, userId);
            if (user == null) {
                throw new RuntimeException("User not found with id: " + userId);
            }
            users.add(user);
        }

        userGroup.setUsers(users);

      
        if ("edit".equalsIgnoreCase(action)) {
            entityManager.merge(userGroup);  
        } else {
            entityManager.persist(userGroup); 
        }

        return userGroup;
    }

    private boolean isGroupNameExists(String groupName, Long excludeGroupId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<UserGroup> root = query.from(UserGroup.class);
        
        Predicate groupNamePredicate = criteriaBuilder.equal(root.get("groupName"), groupName);
        Predicate groupIdPredicate = excludeGroupId != null ?
                criteriaBuilder.notEqual(root.get("groupId"), excludeGroupId) : 
                criteriaBuilder.conjunction(); 
                
        query.select(criteriaBuilder.count(root)).where(criteriaBuilder.and(groupNamePredicate, groupIdPredicate));
        
        Long count = entityManager.createQuery(query).getSingleResult();
        
        return count > 0;
    }
  
    public List<Object[]> getUsersByGroupId(Long groupId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<UserGroup> userGroupRoot = query.from(UserGroup.class);
        Join<UserGroup, Users> userJoin = userGroupRoot.join("users", JoinType.LEFT);

        query.multiselect(
            userJoin.get("userID"),
            userJoin.get("emailID"),
            userJoin.get("userName")
        );

        query.where(cb.equal(userGroupRoot.get("groupId"), groupId));

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
    
  //Get all the UserGroups with UserGroupID, UserGroupName, isActive and get the count of Users linked to that UserGroup
    
    public List<UserGroupDTO> getUserGroupSummary() {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<UserGroupDTO> cq = cb.createQuery(UserGroupDTO.class);
      Root<UserGroup> root = cq.from(UserGroup.class);
      Join<UserGroup, ?> usersJoin = root.join("users", JoinType.LEFT); // Left join to include groups with no users

      // Select required fields and count users
      cq.multiselect(
              root.get("groupId"),
              root.get("groupName"),
              root.get("isActive"),
              cb.count(usersJoin.get("userID"))
      );

      cq.groupBy(root.get("groupId"), root.get("groupName"), root.get("isActive"));

      return entityManager.createQuery(cq).getResultList();
  }

  public Page<UserGroup> getAllUserGroups(Pageable pageable) {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<UserGroup> cq = cb.createQuery(UserGroup.class);
      Root<UserGroup> root = cq.from(UserGroup.class);
      cq.select(root);

      List<UserGroup> userGroups = entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
          .setMaxResults(pageable.getPageSize()).getResultList();

      long totalRecords = getTotalCount();

      return new PageImpl<>(userGroups, pageable, totalRecords);
    }

    private long getTotalCount() {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
      Root<UserGroup> root = countQuery.from(UserGroup.class);
      countQuery.select(cb.count(root));
      return entityManager.createQuery(countQuery).getSingleResult();
    }
    
    public UserGroup findById(Long groupId) {
        return entityManager.find(UserGroup.class, groupId);
    }
}
