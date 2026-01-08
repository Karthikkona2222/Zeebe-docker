package com.aaseya.AIS.Model;

import jakarta.persistence.*;

import java.util.Arrays;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CSRDocuments")
public class CSRDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CSR_document_id")
    private Long csrDocumentId;

    @Column(name = "document_type")
    private String documentType;  // Example: "PDF", "JPEG", "DOCX"

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "document", columnDefinition = "BYTEA")
    private byte[] document; // Stores any file type in bytea format (PostgreSQL)

    // Many documents belong to one policy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    @JsonIgnore
    private PolicyDetails policyDetails;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    @JsonIgnore
    private ClaimCase claimCase;

   


    // 🏗 Default constructor
    public CSRDocuments() {}

    // ⚙️ Getters & Setters
    

    public String getDocumentType() {
        return documentType;
    }

    public Long getCsrDocumentId() {
		return csrDocumentId;
	}
    
    public ClaimCase getClaimCase() {
        return claimCase;
    }

    public void setClaimCase(ClaimCase claimCase) {
        this.claimCase = claimCase;
    }

	public void setCsrDocumentId(Long csrDocumentId) {
		this.csrDocumentId = csrDocumentId;
	}

	public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public PolicyDetails getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(PolicyDetails policyDetails) {
        this.policyDetails = policyDetails;
    }

    @Override
	public String toString() {
		return "CSRDocuments [csrDocumentId=" + csrDocumentId + ", documentType=" + documentType + ", document="
				+ Arrays.toString(document) + ", policyDetails=" + policyDetails + "]";
	}
}
