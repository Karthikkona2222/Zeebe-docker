package com.aaseya.AIS.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.stream.Collectors;

import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.UserSkill;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dao.UserSkillDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.GetAllSkillDTO;
import com.aaseya.AIS.dto.UserSkillDTO;

@Service
public class UserSkillService {

	@Autowired
	private UserSkillDAO userSkillDAO;

	@Autowired
	private UsersDAO usersDAO;

	public List<UserSkillDTO> getUserSkillsByUserId(Long userId) {
		try {
			// Fetch UserSkill entities from DAO
			List<UserSkill> userSkills = userSkillDAO.getUserSkillsByUserId(userId);

			// Map UserSkill entities to UserSkillDTOs
			return userSkills.stream().map(userSkill -> {
				UserSkillDTO dto = new UserSkillDTO();
				dto.setSkillId(userSkill.getId().getSkillId());
				dto.setSkill(userSkill.getSkill().getSkill()); // Assuming Skill has getId()
				dto.setStartDate(userSkill.getStartDate());
				dto.setExpiryDate(userSkill.getExpiryDate());
				return dto;
			}).toList();
		} catch (Exception e) {
			throw new RuntimeException("Error fetching UserSkills by userId: " + userId, e);
		}
	}

	public Page<GetAllSkillDTO> getAllUsersWithSkills(Pageable pageable, String role) {
		Page<Users> userPage;

		// If the role is not Inspector, fetch all users
		userPage = usersDAO.getAllUsersWithSkills(role, pageable);

		// Map Users entities to GetAllSkillDTO
		return userPage.map(user -> {
			List<String> skills = user.getSkill().stream().map(Skill::getSkill).collect(Collectors.toList());
			return new GetAllSkillDTO(user.getUserID(), user.getUserName(), user.getRole(), user.getRegistrationID(), skills);
		});
	}

}
