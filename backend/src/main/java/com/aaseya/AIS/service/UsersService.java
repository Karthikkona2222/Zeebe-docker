package com.aaseya.AIS.service;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.UserSkill;
import com.aaseya.AIS.Model.UserSkillId;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.SkillDAO;
import com.aaseya.AIS.dao.UserSkillDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dao.ZoneDAO;
import com.aaseya.AIS.dto.UserSkillDTO;
import com.aaseya.AIS.dto.UsersDTO;
import com.aaseya.AIS.dto.UsersRoleDTO;
import com.aaseya.AIS.utility.AESDecryptionUtil;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class UsersService {
    @Autowired
    private UsersDAO usersDAO;
 
    @Autowired
    private ZoneDAO zoneDAO;
 
    @Autowired
    private SkillDAO skillDAO;
 
    @Autowired
    private UserSkillDAO userSkillDAO;

 
    @Transactional
    public void addUsers(UsersDTO usersDTO) {
        Users user = new Users();
        user.setUserName(usersDTO.getUserName());
        user.setPhoneNumber(usersDTO.getPhoneNumber());
        user.setPassword(usersDTO.getPassword());
        user.setEmailID(usersDTO.getEmailID());
        user.setRegistrationID(usersDTO.getUserId());
        user.setCountryName(usersDTO.getCountryName());
        user.setRole(usersDTO.getRole());
        user.setStatus(usersDTO.getStatus());
        if (usersDTO.getPhoto() != null && !usersDTO.getPhoto().isEmpty()) {
            byte[] decodedPhoto = Base64.getDecoder().decode(usersDTO.getPhoto());
            user.setPhoto(decodedPhoto);
        }
 
        
        Set<Zone> zones = zoneDAO.findByIds(usersDTO.getZoneIds()); // This assumes the DAO method takes List<String>
 
        user.setZones(zones);
 
        // Set bi-directional relationship between users and zones
        for (Zone zone : zones) {
            if (zone.getUsers() == null) {
                zone.setUsers(new ArrayList<>());
            }
            zone.getUsers().add(user);
        }
 
 
        
 
        // Save the user, which will also save the associations in the user_skill table
        usersDAO.saveUsers(user);

 
        List<UserSkillDTO> skillDTOs = usersDTO.getSkill();
        if (skillDTOs != null && !skillDTOs.isEmpty()) {
            for (UserSkillDTO skillDTO : skillDTOs) {
                Skill skill = skillDAO.findById(skillDTO.getSkillId());
                if (skill == null) {
                    throw new RuntimeException("Skill not found with ID: " + skillDTO.getSkillId());
                }
 
                UserSkillId userSkillid = new UserSkillId();
                userSkillid.setSkillId(skillDTO.getSkillId());
                userSkillid.setUserId(user.getUserID());
                UserSkill userSkill = new UserSkill();
                userSkill.setId(userSkillid);
                userSkill.setUser(user);
                userSkill.setSkill(skill);
                userSkill.setStartDate(skillDTO.getStartDate());
                userSkill.setExpiryDate(skillDTO.getExpiryDate());
 
                userSkillDAO.save(userSkill);
            }
        }
    }

	public java.util.List<Users> getAllUser() {
		// TODO Auto-generated method stub
		return null;
	}
 
	@Transactional
    public List<UsersDTO> getAllUsers() {
        List<Users> users = usersDAO.getAllUsers();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
	private UsersDTO convertToDTO(Users user) {
	    UsersDTO dto = new UsersDTO();
	    //dto.setUserID(user.getUserID());
	    dto.setUserName(user.getUserName());
	    dto.setPhoneNumber(user.getPhoneNumber());
	    dto.setPassword(user.getPassword());
	    dto.setEmailID(user.getEmailID());
	    dto.setCountryName(user.getCountryName());
	    dto.setRole(user.getRole());
	    dto.setStatus(user.getStatus());
	   // dto.setZones(user.getZones().stream().map(Zone::getName).collect(Collectors.toList()));
	    dto.setSkills(user.getSkill().stream().map(Skill::getSkill).collect(Collectors.toList()));  
	    return dto;
	}
 
 

    public List<UsersDTO> getUsersByRole(String role) {
        List<Users> usersList = usersDAO.getUsersByRole(role);
        List<UsersDTO> userDTOList = new ArrayList<>();
        for (Users user : usersList) {
            UsersDTO userDTO = new UsersDTO();
            //userDTO.setUserID(user.getUserID());
            userDTO.setUserName(user.getUserName());
            userDTO.setEmailID(user.getEmailID());
            userDTO.setRole(user.getRole());
            List<String> skillNames = new ArrayList<>();
            for (Skill skill : user.getSkill()) {
                skillNames.add(skill.getSkill());
            }
            userDTO.setSkills(skillNames);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }
    public Page<UsersDTO> getSelectedUserDetails(int page, int size) {
        Page<Users> users = usersDAO.getAllUsers1(page,size);
        
        return users.map(user -> new UsersDTO(user.getUserID(), user.getRegistrationID(), user.getUserName(), user.getEmailID(),
				user.getPhoneNumber(), user.getRole(), user.getIsActive()));
    }

////getuserdetails basesd on emailid///
 
	public Map<String, Object> getUserDetails(String emailId) {
	    Users user = usersDAO.getUserDetailsByEmail(emailId);
 
	    if (user == null) {
	        throw new IllegalArgumentException("User not found for email: " + emailId);
	    }
 
	    // Initialize userDetails map
	    Map<String, Object> userDetails = new HashMap<>();
 
	    // User Details
	    Map<String, Object> userDetailsMap = new HashMap<>();
	    userDetailsMap.put("userID", user.getUserID());
	    userDetailsMap.put("UserId", user.getRegistrationID());
	    userDetailsMap.put("Name", user.getUserName());
	    userDetailsMap.put("ContactNumber", user.getPhoneNumber());
	    userDetailsMap.put("EmailId", user.getEmailID());
	    userDetailsMap.put("Role", user.getRole());
	    byte[] photoBytes = user.getPhoto();
	    if (photoBytes != null) {
	        String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
	        userDetailsMap.put("Photo", photoBase64);
	    } else {
	        userDetailsMap.put("Photo", null);
	    }
 
	    // Add User Details to the main map
	    userDetails.put("UserDetails", userDetailsMap);
	    List<UserSkill> userSkills = userSkillDAO.getUserSkillsByUserId(user.getUserID());
	    // Skills mapping with startDate and expiryDate
	    List<Map<String, Object>> skills = userSkills.stream()
	            .map(skill -> {
	                Map<String, Object> skillMap = new HashMap<>();
	                skillMap.put("SkillId", skill.getSkill().getSkillId());
	                skillMap.put("StartDate", skill.getStartDate());
	                skillMap.put("ExpiryDate", skill.getExpiryDate());
	                return skillMap;
	            })
	            .collect(Collectors.toList());
	    userDetails.put("Skills", skills);
 
	    // Zones mapping
	    List<Map<String, String>> zones = user.getZones().stream()
	            .map(zone -> {
	                Map<String, String> zoneMap = new HashMap<>();
	                zoneMap.put("ZoneId", zone.getZoneId());
	                zoneMap.put("ZoneName", zone.getName());
	                return zoneMap;
	            })
	            .collect(Collectors.toList());
	    userDetails.put("Zones", zones);
 
	   
 
	    return userDetails; 
	}
	@Transactional
	public void updateUserDetails(UsersDTO usersDTO) {
	    if (usersDTO.getUserID() == 0) {
	        throw new IllegalArgumentException("User ID cannot be null or zero");
	    }
 
	    // Validate email format
	    if (usersDTO.getEmailID() == null || usersDTO.getEmailID().isEmpty()) {
	        throw new IllegalArgumentException("Email ID cannot be null or empty");
	    }
 
	    // Update user details using DAO
	    usersDAO.updateUserDetails(usersDTO);
	}
	public UsersDTO authenticateUser(String username, String encryptedPassword) {
	    // Decrypt the password received from frontend
	    String decryptedPassword = AESDecryptionUtil.decrypt(encryptedPassword);

	    // Query the database using username and plain-text password
	    UsersDTO userDTO = usersDAO.findByUsernameAndPassword(username, decryptedPassword);

	    if (userDTO == null) {
	        throw new NoResultException("Invalid username or password");
	    }

	    return userDTO;
	}

	 public List<UsersRoleDTO> getUserByRole(String role) {
	        return usersDAO.getUserByRole(role);
	    }

}
