package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.DocumentUpload;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dao.DocumentUploadRepository;
import com.aaseya.AIS.dao.PoolDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.ClaimCaseDTO;
import com.aaseya.AIS.dto.DashboardCountsDTO;
import com.aaseya.AIS.dto.DocumentUploadDTO;
import com.aaseya.AIS.dto.PolicyDetailsDTO;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DocumentUploadService {

    @Autowired
    private DocumentUploadRepository repository;

    public DocumentUploadDTO saveDocument(DocumentUploadDTO dto) {
        DocumentUpload entity = new DocumentUpload(
                null,
                dto.documentName(),
                dto.responseData(),
                dto.documentId(),
                dto.storeId(),
                dto.contentType(),
                dto.contentHash(),
                dto.policyId(),
                dto.documentPurpose()
        );

        DocumentUpload saved = repository.save(entity);

        // Upload the document in GCP.




        return new DocumentUploadDTO(
                saved.getId(),
                saved.getDocumentName(),
                saved.getResponseData(),
                saved.getDocumentId(),
                saved.getStoreId(),
                saved.getContentType(),
                saved.getContentHash(),
                saved.getPolicyId(),
                saved.getDocumentPurpose()
        );
    }

    @Transactional(readOnly = true)
    public DocumentUploadDTO getDocumentById(String documentId) {
        DocumentUpload found = repository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));

        return new DocumentUploadDTO(
                found.getId(),
                found.getDocumentName(),
                found.getResponseData(),
                found.getDocumentId(),
                found.getStoreId(),
                found.getContentType(),
                found.getContentHash(),
                found.getPolicyId(),
                found.getDocumentPurpose()
        );
    }
}
