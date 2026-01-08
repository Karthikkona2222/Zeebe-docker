package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "ClaimRequestDocuments")
public class ClaimRequestDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

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

    // 🏗 Default constructor
    public ClaimRequestDocuments() {}

    // ⚙️ Getters & Setters
    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
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
        return "ClaimRequestDocuments [documentId=" + documentId + ", documentType=" + documentType + "]";
    }
}
