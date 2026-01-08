package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
import com.aaseya.AIS.dto.InspectionFilters;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Combined test class for InspectionCaseService:
 *  - getInspectionHistoryCases + getCaseStatsByMonth
 *  - isDueDateSatisfied
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // allow some unused stubbings
class InspectionCaseServiceTest {

    @InjectMocks
    private InspectionCaseService inspectionCaseService;

    @Mock
    private InspectionCaseDAO inspectionCaseDAO;

    // used for risk-score queries inside service
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Integer> typedQuery;

    // ----------------- helpers -----------------

    private InspectionFilters buildFilters(String processType) {
        InspectionFilters filters = new InspectionFilters();
        filters.setProcessType(processType);
        return filters;
    }

    private InspectionCase buildInspectionCase(String processType) {
        // deep stub so we can call inspectionCase.getEntity().getName()
        InspectionCase inspectionCase = mock(InspectionCase.class, RETURNS_DEEP_STUBS);

        when(inspectionCase.getInspectionID()).thenReturn(1001L);
        when(inspectionCase.getInspectionType()).thenReturn("PERIODIC");

        // your DTO uses String date, so return String here
        when(inspectionCase.getDateOfInspection()).thenReturn("2024-01-10");

        when(inspectionCase.getInspector_source()).thenReturn("SRC1");
        when(inspectionCase.getStatus()).thenReturn("COMPLETED");
        when(inspectionCase.getProcessType()).thenReturn(processType);
        when(inspectionCase.getRecommendation()).thenReturn("Do maintenance");

        // entity name
        when(inspectionCase.getEntity().getName()).thenReturn("ABC Corp");

        return inspectionCase;
    }

    // ------------------------------------------------------------------
    // 1) req processType = null → includeRiskData = true
    //    case processType = ADP → riskScore + recommendation should be set
    // ------------------------------------------------------------------
    @Test
    void getInspectionHistoryCases_whenReqProcessTypeNull_andCaseADP_includesRiskData() {

        InspectionFilters filters = buildFilters(null);
        InspectionCase inspectionCase = buildInspectionCase("ADP");

        when(inspectionCaseDAO.getCasesCountByFilter(filters))
                .thenReturn(List.of(inspectionCase));

        when(entityManager.createQuery(anyString(), eq(Integer.class)))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("insId"), any()))
                .thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1))
                .thenReturn(typedQuery);
        when(typedQuery.getSingleResult())
                .thenReturn(85);

        List<InspectionCase_EntityDTO> result =
                inspectionCaseService.getInspectionHistoryCases(filters);

        assertNotNull(result);
        assertEquals(1, result.size());

        InspectionCase_EntityDTO dto = result.get(0);
        assertEquals(1001L,        dto.getInspectionID());
        assertEquals("ABC Corp",   dto.getEntityid());
        assertEquals("PERIODIC",   dto.getInspection_type());
        assertEquals("SRC1",       dto.getInspector_source());
        assertEquals("COMPLETED",  dto.getStatus());
        assertEquals("2024-01-10", dto.getDateOfInspection());

        // risk data must be present
        assertEquals("Do maintenance", dto.getRecommendation());
        assertEquals(85,               dto.getRiskScore());

        verify(entityManager, times(1))
                .createQuery(anyString(), eq(Integer.class));
        verify(typedQuery, times(1)).getSingleResult();
    }

    // ------------------------------------------------------------------
    // 2) req processType = MANUAL → includeRiskData = false
    //    even if case is ADP, riskScore + recommendation must NOT be set
    // ------------------------------------------------------------------
    @Test
    void getInspectionHistoryCases_whenReqProcessTypeManual_doesNotIncludeRiskData() {

        InspectionFilters filters = buildFilters("MANUAL"); // non-ADP
        InspectionCase inspectionCase = buildInspectionCase("ADP"); // case ADP

        when(inspectionCaseDAO.getCasesCountByFilter(filters))
                .thenReturn(List.of(inspectionCase));

        List<InspectionCase_EntityDTO> result =
                inspectionCaseService.getInspectionHistoryCases(filters);

        assertNotNull(result);
        assertEquals(1, result.size());

        InspectionCase_EntityDTO dto = result.get(0);
        assertEquals(1001L,      dto.getInspectionID());
        assertEquals("ABC Corp", dto.getEntityid());

        // risk data must be null
        assertNull(dto.getRiskScore());
        assertNull(dto.getRecommendation());

        verify(entityManager, never())
                .createQuery(anyString(), eq(Integer.class));
    }

    // ------------------------------------------------------------------
    // 3) risk query throws exception → riskScore should be null
    // ------------------------------------------------------------------
    @Test
    void getInspectionHistoryCases_whenRiskQueryFails_setsRiskScoreNull() {

        InspectionFilters filters = buildFilters("ADP");
        InspectionCase inspectionCase = buildInspectionCase("ADP");

        when(inspectionCaseDAO.getCasesCountByFilter(filters))
                .thenReturn(List.of(inspectionCase));

        when(entityManager.createQuery(anyString(), eq(Integer.class)))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("insId"), any()))
                .thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1))
                .thenReturn(typedQuery);
        when(typedQuery.getSingleResult())
                .thenThrow(new RuntimeException("DB error"));

        List<InspectionCase_EntityDTO> result =
                inspectionCaseService.getInspectionHistoryCases(filters);

        assertNotNull(result);
        assertEquals(1, result.size());

        InspectionCase_EntityDTO dto = result.get(0);

        // recommendation copied
        assertEquals("Do maintenance", dto.getRecommendation());
        // riskScore must be null on exception
        assertNull(dto.getRiskScore());
    }

    // ------------------------------------------------------------------
    // 4) getCaseStatsByMonth → delegates to DAO and returns result
    // ------------------------------------------------------------------
    @Test
    void testGetCaseStatsByMonth() {

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate   = LocalDate.of(2025, 12, 31);
        String processType  = "ADP";

        // Prepare mock return
        List<Map<String, Object>> daoResult = new ArrayList<>();

        Map<String, Object> row = new HashMap<>();
        row.put("month", "January 2025");
        daoResult.add(row);

        when(inspectionCaseDAO.getCaseStatsByMonth(startDate, endDate, processType))
                .thenReturn(daoResult);

        // Call service
        List<Map<String, Object>> result =
                inspectionCaseService.getCaseStatsByMonth(startDate, endDate, processType);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("January 2025", result.get(0).get("month"));

        // Verify call
        verify(inspectionCaseDAO).getCaseStatsByMonth(startDate, endDate, processType);
        verifyNoMoreInteractions(inspectionCaseDAO);
    }

    // ------------------------------------------------------------------
    // 5) isDueDateSatisfied tests
    // ------------------------------------------------------------------

    @Test
    void testIsDueDateSatisfied_WhenDueDateIsInFutureOrToday() {
        Long id = 1L;

        InspectionCase ic = new InspectionCase();
        ic.setDueDate(LocalDate.now().plusDays(1)); // future date

        when(inspectionCaseDAO.findById(id)).thenReturn(ic);

        boolean result = inspectionCaseService.isDueDateSatisfied(id);

        assertTrue(result);
    }

    @Test
    void testIsDueDateSatisfied_WhenDueDateIsPast() {
        Long id = 2L;

        InspectionCase ic = new InspectionCase();
        ic.setDueDate(LocalDate.now().minusDays(1)); // past date

        when(inspectionCaseDAO.findById(id)).thenReturn(ic);

        boolean result = inspectionCaseService.isDueDateSatisfied(id);

        assertFalse(result);
    }

    @Test
    void testIsDueDateSatisfied_WhenInspectionCaseNotFound() {
        Long id = 3L;

        when(inspectionCaseDAO.findById(id)).thenReturn(null);

        boolean result = inspectionCaseService.isDueDateSatisfied(id);

        assertFalse(result);
    }
}
