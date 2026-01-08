package com.aaseya.AIS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersRoleDTO {
    private String userName;
    private long userID;

    @JsonProperty("userid") // This renames 'emailID' in the JSON response
    private String emailID;

   

    public UsersRoleDTO(String userName, long userID, String emailID) {
		super();
		this.userName = userName;
		this.userID = userID;
		this.emailID = emailID;
	}

	public String getUserName() {
        return userName;
    }

    public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
