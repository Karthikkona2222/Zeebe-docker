package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private long skillId;

    @Column(nullable = false)
    private String skill;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "expiry_date", nullable = true)
    private LocalDate expiryDate;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
    @JsonIgnore // Ignore in JSON serialization to prevent lazy-loading errors
    private Set<Inspection_Type> inspectionTypes;

    @ManyToMany(mappedBy = "skill", fetch = FetchType.EAGER)
    private List<Users> users = new ArrayList<>();

    // Default constructor
    public Skill() {
    }

    // Getters and setters
    public long getSkillId() {
        return skillId;
    }

    public void setSkillId(long skillId) {
        this.skillId = skillId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Set<Inspection_Type> getInspectionTypes() {
        return inspectionTypes;
    }

    public void setInspectionTypes(Set<Inspection_Type> inspectionTypes) {
        this.inspectionTypes = inspectionTypes;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Skill [skillId=" + skillId + ", skill=" + skill + ", isActive=" + isActive + ", startDate=" + startDate
                + ", expiryDate=" + expiryDate + "]";
    }
}
