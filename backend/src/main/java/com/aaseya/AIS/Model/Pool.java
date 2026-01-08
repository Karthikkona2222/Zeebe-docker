package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Pool")
public class Pool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pool_id")
    private Long id;

    @Column(name = "pool_name", nullable = false, unique = true)
    private String poolName;

    @Column(name = "active")
    private boolean active;

    // One Pool can have many users
    @OneToMany(mappedBy = "pool", fetch = FetchType.LAZY)
    private List<Users> users;

    // One Pool can have many claim cases assigned at a time (optional convenience)
    @OneToMany(mappedBy = "pool", fetch = FetchType.LAZY)
    private List<ClaimCase> claimCases;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPoolName() {
        return poolName;
    }
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public List<Users> getUsers() {
        return users;
    }
    public void setUsers(List<Users> users) {
        this.users = users;
    }
    public List<ClaimCase> getClaimCases() {
        return claimCases;
    }
    public void setClaimCases(List<ClaimCase> claimCases) {
        this.claimCases = claimCases;
    }
}
