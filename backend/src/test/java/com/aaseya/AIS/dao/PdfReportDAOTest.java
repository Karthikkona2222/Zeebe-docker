package com.aaseya.AIS.dao;
import com.aaseya.AIS.Model.PdfReport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.util.Collections;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class PdfReportDAOTest {
 
    @Mock
    private EntityManager entityManager;
 
    @Mock
    private CriteriaBuilder criteriaBuilder;
 
    @Mock
    private CriteriaQuery<PdfReport> criteriaQuery;
 
    @Mock
    private Root<PdfReport> root;
 
    @Mock
    private TypedQuery<PdfReport> typedQuery;
 
    @Mock
    private Path<Object> inspectionCasePath;
 
    @Mock
    private Path<Object> inspectionIdPath;
 
    @Mock
    private Predicate predicate;
 
    @InjectMocks
    private PdfReportDAO pdfReportDAO;
 
    // ✅ save()
    @Test
    void save_shouldCallEntityManagerPersist() {
        PdfReport pdfReport = new PdfReport();
 
        pdfReportDAO.save(pdfReport);
 
        verify(entityManager, times(1)).persist(pdfReport);
    }
 
    // ✅ getByInspectionId() – record exists
    @Test
    void getByInspectionId_whenRecordExists_shouldReturnPdfReport() {
        Long inspectionId = 1L;
        PdfReport report = new PdfReport();
 
        // mock Criteria API chain
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(PdfReport.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(PdfReport.class)).thenReturn(root);
 
        // root.get("inspectionCase").get("inspectionID")
        when(root.get("inspectionCase")).thenReturn(inspectionCasePath);
        when(inspectionCasePath.get("inspectionID")).thenReturn(inspectionIdPath);
 
        // cb.equal(...)
        when(criteriaBuilder.equal(inspectionIdPath, inspectionId)).thenReturn(predicate);
 
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
 
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(report));
 
        PdfReport result = pdfReportDAO.getByInspectionId(inspectionId);
 
        assertNotNull(result);
        assertEquals(report, result);
    }
 
    // ✅ getByInspectionId() – no record
    @Test
    void getByInspectionId_whenNoRecord_shouldReturnNull() {
        Long inspectionId = 2L;
 
        // mock Criteria API chain
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(PdfReport.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(PdfReport.class)).thenReturn(root);
 
        when(root.get("inspectionCase")).thenReturn(inspectionCasePath);
        when(inspectionCasePath.get("inspectionID")).thenReturn(inspectionIdPath);
 
        when(criteriaBuilder.equal(inspectionIdPath, inspectionId)).thenReturn(predicate);
 
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);
 
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
 
        PdfReport result = pdfReportDAO.getByInspectionId(inspectionId);
 
        assertNull(result);
    }
}
 
 
