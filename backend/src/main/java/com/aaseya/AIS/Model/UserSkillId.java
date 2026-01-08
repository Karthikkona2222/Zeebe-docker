package com.aaseya.AIS.Model;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class UserSkillId {
	
	
    private Long userId;   //composite key
    private Long skillId; // 

    
    public UserSkillId() {}


    public UserSkillId(Long userId, Long skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    // Override equals and hashCode to ensure uniqueness of composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSkillId that = (UserSkillId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }
}
