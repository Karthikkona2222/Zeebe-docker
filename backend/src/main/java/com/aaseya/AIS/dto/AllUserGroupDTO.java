package com.aaseya.AIS.dto;
 
import com.aaseya.AIS.Model.UserGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
 
public class AllUserGroupDTO {
 
    private Long groupId;
    private String groupName;
    private String description;
    private boolean isActive;
 
    // ✅ Constructor to initialize from UserGroup entity
 // ✅ Constructor to initialize from UserGroup entity
    public AllUserGroupDTO(UserGroup userGroup) {
        this.groupId = userGroup.getGroupId();
        this.groupName = userGroup.getGroupName();
        this.description = userGroup.getDescription();
        this.isActive = userGroup.isActive();
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
 
    @JsonProperty("is_active") 
    public boolean getIsActive() {
        return isActive;
    }
 
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
 
    @Override
    public String toString() {
        return "UserGroupDTO [groupId=" + groupId + ", groupName=" + groupName + ", description=" + description
                + ", isActive=" + isActive + "]";
    }
}