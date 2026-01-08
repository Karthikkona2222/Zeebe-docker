package com.aaseya.AIS.dto;

import java.util.List;

public class UsersGroupDTO {
    private String groupName;
    private String description;
    private List<UsersSkillGroupDTO> users;
      
     public UsersGroupDTO() {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public List<UsersSkillGroupDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UsersSkillGroupDTO> users) {
		this.users = users;
	}
	
	
}

