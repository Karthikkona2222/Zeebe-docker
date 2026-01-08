package com.aaseya.AIS.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "code_range_category")
public class CodeRangeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_range", nullable = false)
    private String codeRange;

    @Column(name = "category", nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyDetails policyDetails;

    // Constructors
    public CodeRangeCategory() {}

    public CodeRangeCategory(String codeRange, String category, PolicyDetails policyDetails) {
        this.codeRange = codeRange;
        this.category = category;
        this.policyDetails = policyDetails;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeRange() { return codeRange; }
    public void setCodeRange(String codeRange) { this.codeRange = codeRange; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public PolicyDetails getPolicyDetails() { return policyDetails; }
    public void setPolicyDetails(PolicyDetails policyDetails) { this.policyDetails = policyDetails; }
}
