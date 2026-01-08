package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.DocumentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentUploadRepository extends JpaRepository<DocumentUpload, Long> {
    Optional<DocumentUpload> findByDocumentId(String documentId);
    Optional<DocumentUpload> findByDocumentIdAndStoreId(String documentId, String storeId);

}
