
package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.UserGroup;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.dao.UserGroupDAO;
import com.aaseya.AIS.dao.UserGroupForEditByIdDAO;
import com.aaseya.AIS.dto.UsersGroupDTO;
import com.aaseya.AIS.dto.UsersSkillGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupForEditByIdService {

	@Autowired
	private UserGroupForEditByIdDAO userGroupForEditByIdDAO;

	@Transactional
	public UsersGroupDTO getUserGroupDetails(Long groupId) {
		UserGroup userGroup = userGroupForEditByIdDAO.getUserGroupById(groupId);
		if (userGroup == null) {
			return null;
		}

		// Convert UserGroup to DTO
		UsersGroupDTO dto = new UsersGroupDTO();
		dto.setGroupName(userGroup.getGroupName());
		dto.setDescription(userGroup.getDescription());

		// Convert Users to DTO, including skills
		List<UsersSkillGroupDTO> usersDTO = userGroup.getUsers().stream().map(user -> {
			UsersSkillGroupDTO userDTO = new UsersSkillGroupDTO();
			userDTO.setUserID(user.getUserID());
			userDTO.setUserName(user.getUserName());

			// Map user's skills to skill names
			List<String> skillNames = user.getSkill().stream().map(Skill::getSkill) // Assuming Skill has a method
																					// getSkillName()
					.collect(Collectors.toList());

			userDTO.setSkills(skillNames); // Set skills in DTO

			return userDTO;
		}).collect(Collectors.toList());

		dto.setUsers(usersDTO); // Set user list in DTO
		return dto;
	}

}