package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.UserGroup;
import com.aaseya.AIS.dao.UserGroupDAO;
import com.aaseya.AIS.dto.AllUserGroupDTO;
import com.aaseya.AIS.dto.UserGroupDTO;
import com.aaseya.AIS.dto.UsersGroupDTO;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserGroupService {

    @Autowired 
    private UserGroupDAO userGroupDAO;

    public String processUserGroup(UserGroupDTO userGroupDTO) {
        try {
            userGroupDAO.addOrUpdateUserGroup(
                    userGroupDTO.getAction(),
                    userGroupDTO.getGroupId(),
                    userGroupDTO.getGroupName(),
                    userGroupDTO.getDescription(),
                    userGroupDTO.getUserIds()
            );

            return "add".equalsIgnoreCase(userGroupDTO.getAction()) ?
                    "User Group added successfully" :
                    "User Group updated successfully";

        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public List<UserGroupDTO> getUsersByGroupId(Long groupId) {
        List<Object[]> users = userGroupDAO.getUsersByGroupId(groupId);
        
        return users.stream()
            .map(obj -> new UserGroupDTO((Long) obj[0], (String) obj[1], (String) obj[2]))
            .collect(Collectors.toList());
 }
    
 //Get all the UserGroups with UserGroupID, UserGroupName, isActive and get the count of Users linked to that UserGroup
    
    public List<UserGroupDTO> getUserGroupSummary() {
        return userGroupDAO.getUserGroupSummary();
    }
  
    @Transactional
      public Page<AllUserGroupDTO> getAllUserGroups(Pageable pageable) {
          Page<UserGroup> userGroups = userGroupDAO.getAllUserGroups(pageable);
          return userGroups.map(AllUserGroupDTO::new);
      }
}


