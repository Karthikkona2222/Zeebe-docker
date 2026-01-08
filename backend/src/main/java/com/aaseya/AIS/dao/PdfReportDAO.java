package com.aaseya.AIS.dao;
import com.aaseya.AIS.Model.PdfReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public class PdfReportDAO {
 
    @PersistenceContext
    private EntityManager entityManager;   // ✅
 
    // SAVE
    public void save(PdfReport pdfReport) {
        entityManager.persist(pdfReport);
    }
 
    // FETCH by inspectionId using CriteriaBuilder
    public PdfReport getByInspectionId(Long inspectionId) {
 
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PdfReport> cq = cb.createQuery(PdfReport.class);
        Root<PdfReport> root = cq.from(PdfReport.class);
 
        // pdfReport.inspectionCase.inspectionID = ?
        cq.select(root)
          .where(cb.equal(root.get("inspectionCase").get("inspectionID"), inspectionId));
 
        List<PdfReport> list = entityManager.createQuery(cq).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
 
 