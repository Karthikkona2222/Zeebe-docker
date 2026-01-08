package com.aaseya.AIS.dto;



/* DB Create script to use for table creation.

CREATE TABLE document_upload (
        id SERIAL PRIMARY KEY,
        document_name VARCHAR(255) NOT NULL,
response_data JSONB,
document_id CHAR(30) NOT NULL,
store_id VARCHAR(20),
content_type VARCHAR(100),
content_hash VARCHAR(255),
policy_id VARCHAR(255),
document_purpose VARCHAR(255)
);
*/

public record DocumentUploadDTO(
        Long id,
        String documentName,
        String responseData,
        String documentId,
        String storeId,
        String contentType,
        String contentHash,

        String policyId,
        String documentPurpose
) {}
