package com.aaseya.AIS.dto;

import java.util.List;

public class UserGroupDTO {
    private String action;
    private Long groupId;
    private String groupName;
    private String description;
    private Long userID;
    private String emailID;
    private String userName;
    private List<Long> userIds;
    private boolean isActive;
    private Long usersCount;
    
    
    public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

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

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
    
  

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Long getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(Long usersCount) {
		this.usersCount = usersCount;
	}
    
    public UserGroupDTO(Long userID, String emailID, String userName) {
        this.userID = userID;
        this.emailID = emailID;
        this.userName = userName;
    }
    
    // Constructor matching the DAO query projection
    public UserGroupDTO(Long groupId, String groupName, boolean isActive, Long usersCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.isActive = isActive;
        this.usersCount = usersCount;
    }
    public UserGroupDTO() {
        // Default constructor required for JSON serialization/deserialization
    }

}
