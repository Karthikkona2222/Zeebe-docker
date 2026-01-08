package com.aaseya.AIS.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_upload")
public class DocumentUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "response_data", columnDefinition = "jsonb")
    private String responseData;  // Store JSON as String, Postgres will parse it

    @Column(name = "document_id", length = 36, nullable = false)
    private String documentId;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "store_id", length = 20)
    private String storeId;

    @Column(name = "content_hash")
    private String contentHash;

    @Column(name = "policy_id")
    private String policyId;

    @Column(name = "document_purpose")
    private String documentPurpose;

    public DocumentUpload() {}

    public DocumentUpload(Long id, String documentName, String responseData, String documentId,
                          String storeId, String contentType, String contentHash,
                          String policyId, String documentPurpose) {
        this.id = id;
        this.documentName = documentName;
        this.responseData = responseData;
        this.documentId = documentId;
        this.storeId = storeId;
        this.contentType = contentType;
        this.contentHash = contentHash;
        this.policyId = policyId;
        this.documentPurpose = documentPurpose;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getDocumentPurpose() {
        return documentPurpose;
    }

    public void setDocumentPurpose(String documentPurpose) {
        this.documentPurpose = documentPurpose;
    }
}
