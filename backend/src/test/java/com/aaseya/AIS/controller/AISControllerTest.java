package com.aaseya.AIS.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dto.EntityInspectionCasesReportResponseDTO;
import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
import com.aaseya.AIS.dto.InspectionFilters;
import com.aaseya.AIS.dto.TopTenNegativeObservationsDTO;
import com.aaseya.AIS.service.CategoriesSummaryReportService;
import com.aaseya.AIS.service.EntityInspectionReportService;
import com.aaseya.AIS.service.IDPAIService;
import com.aaseya.AIS.service.InspectionCaseService;
import com.aaseya.AIS.service.OperateService;
import com.aaseya.AIS.service.PdfReportService;
import com.aaseya.AIS.service.TaskListService;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.SetVariablesCommandStep1;
import io.camunda.zeebe.client.api.response.SetVariablesResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AISControllerTest {

    // ====== Shared mocks ======
    @Mock private OperateService operateService;
    @Mock private TaskListService tasklistservice;
    @Mock private InspectionCaseService inspectionCaseService;
    @Mock private InspectionCaseDAO inspectionCaseDAO;
    @Mock private CategoriesSummaryReportService categoriesSummaryReportService;
    @Mock private EntityInspectionReportService entityInspectionReportService;
    @Mock private ZeebeClient zeebeClient;
    @Mock private SetVariablesCommandStep1 setVariablesStep1;
    @Mock private PdfReportService pdfReportService;
    @Mock private EntityManager entityManager;

    @Mock private TypedQuery<byte[]> query;

   
    @Mock private ZeebeFuture<SetVariablesResponse> zeebeFuture;
    
   
    @Mock private SetVariablesCommandStep1.SetVariablesCommandStep2 setVariablesStep2;
    
    @Mock
    private IDPAIService service;

  
   
    
    private void mockZeebeChain() {
        // zeebeClient.newSetVariablesCommand(inspectionId)
        doReturn(setVariablesStep1)
                .when(zeebeClient)
                .newSetVariablesCommand(anyLong());

        // .variables(...)
        doReturn(setVariablesStep2)
                .when(setVariablesStep1)
                .variables(anyMap());

        // .send()
        doReturn(zeebeFuture)
                .when(setVariablesStep2)
                .send();

        // .join()
        doReturn(null)
                .when(zeebeFuture)
                .join();
    }


    @InjectMocks
    private AISController controller;  // real controller under test

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Build MockMvc WITHOUT Spring context
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ======================================================
    // A) Tests for GET /api/ai/recommendations
    // ======================================================

    @Test
    void whenRiskScoreGreaterOrEqual80_thenScheduleFollowUpInspection() {
        Long inspectionId = 123L;
        when(operateService.getRiskScore(inspectionId)).thenReturn(85);

        ResponseEntity<Map<String, Object>> resp = controller.getAiRecommendations(inspectionId);

        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals("Schedule Follow up Inspection", body.get("recommendation"));
    }

    @Test
    void whenRiskScoreBetween70And79_thenAssignToComplianceOfficer() {
        Long inspectionId = 456L;
        when(operateService.getRiskScore(inspectionId)).thenReturn(75);

        ResponseEntity<Map<String, Object>> resp = controller.getAiRecommendations(inspectionId);

        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals("Assign case to compliance officer", body.get("recommendation"));
    }

    @Test
    void whenRiskScoreLessThan70_thenNoAiRecommendation() {
        Long inspectionId = 789L;
        when(operateService.getRiskScore(inspectionId)).thenReturn(60);

        ResponseEntity<Map<String, Object>> resp = controller.getAiRecommendations(inspectionId);

        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals("No AI recommendation", body.get("recommendation"));
    }

    @Test
    void whenRiskScoreIsNull_thenReturnMessageRiskScoreNotFound() {
        Long inspectionId = 999L;
        when(operateService.getRiskScore(inspectionId)).thenReturn(null);

        ResponseEntity<Map<String, Object>> resp = controller.getAiRecommendations(inspectionId);

        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertEquals("riskScore variable not found or invalid", body.get("message"));
    }

    // ======================================================
    // B) Top 10 Negative Observations
    // ======================================================

    @Test
    void getTopTenNegativeObservations_pageOutOfRange_returnsEmpty() {

        Long insTypeId = 1L;
        String processType = "ADP";
        int pageNumber = 5;   // way beyond available data
        int pageSize = 2;

        List<TopTenNegativeObservationsDTO> fullList = new ArrayList<>();
        fullList.add(new TopTenNegativeObservationsDTO());

        when(categoriesSummaryReportService.getTop10NegativeObservations(insTypeId, processType))
                .thenReturn(fullList);

        ResponseEntity<Page<TopTenNegativeObservationsDTO>> response =
                controller.getTopTenNegativeObservations(insTypeId, processType, pageNumber, pageSize);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Page<TopTenNegativeObservationsDTO> page = response.getBody();
        assertEquals(0, page.getContent().size());   // empty page
        assertEquals(1, page.getTotalElements());    // but total elements = 1
    }

    // ======================================================
    // C) /getEntityInspectionCasesReport
    // ======================================================

    @Test
    void getEntityInspectionCasesReport_ADP_Pagination_ok() throws Exception {

        EntityInspectionCasesReportResponseDTO case1 = new EntityInspectionCasesReportResponseDTO();
        case1.setCaseId(1L);
        case1.setInspectionType("ADP");
        case1.setStatus("Completed");
        case1.setInspectionDate("2024-12-01");

        when(entityInspectionReportService.getInspectionCases(any()))
                .thenReturn(List.of(case1));

        mockMvc.perform(
                    post("/getEntityInspectionCasesReport?pageNumber=0&pageSize=10&processType=ADP")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"entityId\":\"E101\",\"startdate\":\"2024-01-01\",\"enddate\":\"2024-12-01\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].caseId").value(1))
                .andExpect(jsonPath("$.content[0].inspectionType").value("ADP"));
    }

    @Test
    void getEntityInspectionCasesReport_MANUAL_Pagination_ok() throws Exception {

        EntityInspectionCasesReportResponseDTO case1 = new EntityInspectionCasesReportResponseDTO();
        case1.setCaseId(2L);
        case1.setInspectionType("MANUAL");
        case1.setStatus("Completed");
        case1.setInspectionDate("2024-10-10");

        when(entityInspectionReportService.getInspectionCases(any()))
                .thenReturn(List.of(case1));

        mockMvc.perform(
                    post("/getEntityInspectionCasesReport?pageNumber=0&pageSize=10&processType=MANUAL")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"entityId\":\"E102\",\"startdate\":\"2024-01-01\",\"enddate\":\"2024-12-31\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].caseId").value(2))
                .andExpect(jsonPath("$.content[0].inspectionType").value("MANUAL"));
    }

    // ======================================================
    // D) /getEntityInspectionReport
    // ======================================================

    @Test
    void getEntityInspectionReport_WithProcessType_statusOk() throws Exception {

        // No stubbing required – controller should still return 200 OK.
        mockMvc.perform(
                    post("/getEntityInspectionReport")
                            .param("pageNumber", "0")
                            .param("pageSize", "10")
                            .param("processType", "MANUAL")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"entityId\":\"E202\",\"startdate\":\"2024-01-01\",\"enddate\":\"2024-12-31\"}")
                )
                .andExpect(status().isOk());
    }

    // ======================================================
    // E) getCaseStats – pagination on List<Map<..>>
    // ======================================================

    @Test
    void getCaseStats_pagination_ok() {

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate   = LocalDate.of(2025, 12, 31);
        String processType  = "ADP";
        int pageNumber = 0;
        int pageSize   = 2;

        List<Map<String, Object>> fullList = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("month", "January 2025");
        fullList.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put("month", "February 2025");
        fullList.add(m2);

        Map<String, Object> m3 = new HashMap<>();
        m3.put("month", "March 2025");
        fullList.add(m3);

        when(inspectionCaseService.getCaseStatsByMonth(startDate, endDate, processType))
                .thenReturn(fullList);

        ResponseEntity<Page<Map<String, Object>>> response =
                controller.getCaseStats(startDate, endDate, processType, pageNumber, pageSize);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(3, response.getBody().getTotalElements());

        verify(inspectionCaseService)
                .getCaseStatsByMonth(startDate, endDate, processType);
    }

    // ======================================================
    // F) getInspectionHistoryCases
    // ======================================================

    private List<InspectionCase_EntityDTO> dummyHistoryList(int size) {
        List<InspectionCase_EntityDTO> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
            dto.setInspectionID(1000L + i);
            dto.setEntityid("Entity " + i);
            dto.setInspection_type("TYPE");
            dto.setDateOfInspection(LocalDate.now().toString());
            dto.setInspector_source("SRC");
            dto.setStatus("COMPLETED");
            list.add(dto);
        }
        return list;
    }

    // processType = ADP – first page, processType propagated
    @Test
    void getInspectionHistoryCases_processTypeSet_firstPage_ok() {

        String processType = "ADP";
        InspectionFilters filters = new InspectionFilters();
        int pageNumber = 0;
        int pageSize = 2;

        List<InspectionCase_EntityDTO> fullList = dummyHistoryList(5);

        when(inspectionCaseService.getInspectionHistoryCases(any(InspectionFilters.class)))
                .thenReturn(fullList);

        ResponseEntity<Page<InspectionCase_EntityDTO>> response =
                controller.getInspectionHistoryCases(processType, filters, pageNumber, pageSize);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Page<InspectionCase_EntityDTO> page = response.getBody();
        assertNotNull(page);
        assertEquals(2, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(0, page.getNumber());

        ArgumentCaptor<InspectionFilters> captor = ArgumentCaptor.forClass(InspectionFilters.class);
        verify(inspectionCaseService, times(1)).getInspectionHistoryCases(captor.capture());

        assertEquals("ADP", captor.getValue().getProcessType());
    }

    // processType = null – should not set in filters
    @Test
    void getInspectionHistoryCases_processTypeNull_ok() {

        String processType = null;
        InspectionFilters filters = new InspectionFilters();
        int pageNumber = 1;
        int pageSize = 3;

        List<InspectionCase_EntityDTO> fullList = dummyHistoryList(7);

        when(inspectionCaseService.getInspectionHistoryCases(any()))
                .thenReturn(fullList);

        ResponseEntity<Page<InspectionCase_EntityDTO>> response =
                controller.getInspectionHistoryCases(processType, filters, pageNumber, pageSize);

        Page<InspectionCase_EntityDTO> page = response.getBody();
        assertNotNull(page);

        assertEquals(3, page.getContent().size());
        assertEquals(7, page.getTotalElements());

        ArgumentCaptor<InspectionFilters> captor = ArgumentCaptor.forClass(InspectionFilters.class);
        verify(inspectionCaseService).getInspectionHistoryCases(captor.capture());

        assertNull(captor.getValue().getProcessType());
    }

    // page beyond size – empty page
    @Test
    void getInspectionHistoryCases_pageBeyondSize_returnsEmpty() {

        String processType = "RISK";
        InspectionFilters filters = new InspectionFilters();
        int pageNumber = 5;
        int pageSize = 10;

        List<InspectionCase_EntityDTO> fullList = dummyHistoryList(12);

        when(inspectionCaseService.getInspectionHistoryCases(any()))
                .thenReturn(fullList);

        ResponseEntity<Page<InspectionCase_EntityDTO>> response =
                controller.getInspectionHistoryCases(processType, filters, pageNumber, pageSize);

        Page<InspectionCase_EntityDTO> page = response.getBody();

        assertNotNull(page);
        assertEquals(0, page.getContent().size());
        assertEquals(12, page.getTotalElements());
    }

    // ===============================
    // SUCCESS CASE
    // ===============================
    @Test
    void getPdfByInspectionId_success() {

        Long inspectionId = 100L;
        byte[] pdfData = "pdf-content".getBytes();

        when(entityManager.createQuery(anyString(), eq(byte[].class)))
                .thenReturn(query);
        when(query.setParameter(eq("id"), eq(inspectionId)))
                .thenReturn(query);
        when(query.getSingleResult())
                .thenReturn(pdfData);

        ResponseEntity<byte[]> response =
                controller.getPdfByInspectionId(inspectionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals(pdfData.length, response.getHeaders().getContentLength());
        assertEquals(
                "inline; filename=inspection-100.pdf",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
        );
        assertArrayEquals(pdfData, response.getBody());
    }

    // ===============================
    // NOT FOUND / EXCEPTION CASE
    // ===============================
    @Test
    void getPdfByInspectionId_notFound() {

        when(entityManager.createQuery(anyString(), eq(byte[].class)))
                .thenThrow(new RuntimeException("No result"));

        ResponseEntity<byte[]> response =
                controller.getPdfByInspectionId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


@Test
void updateAiDecision_continue_highRisk_shouldScheduleFollowUpInspection() {
  Long inspectionId = 1L;
  int riskScore = 85;

  mockZeebeChain();
  when(operateService.getRiskScore(inspectionId)).thenReturn(riskScore);

  InspectionCase inspectionCase = new InspectionCase();
  when(inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue()))
          .thenReturn(inspectionCase);

  
  

  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "continue");

  assertEquals(HttpStatus.OK, resp.getStatusCode());
  Map<String, Object> body = (Map<String, Object>) resp.getBody();

  assertEquals(inspectionId, body.get("inspectionId"));
  assertEquals(true, body.get("AIrecommendation"));
  assertEquals(riskScore, body.get("riskScore"));
  assertEquals("Schedule Follow up Inspection", body.get("savedRecommendation"));
  assertEquals("Schedule Follow up Inspection", inspectionCase.getRecommendation());

  verify(inspectionCaseDAO).updateInspectionCase(inspectionCase);
  verify(zeebeClient).newSetVariablesCommand(inspectionId);
  ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
  verify(setVariablesStep1).variables(varsCaptor.capture());

  Map<String, Object> vars = varsCaptor.getValue();
  assertEquals(Boolean.TRUE, vars.get("AIrecommendation"));
  assertEquals("Schedule Follow up Inspection", vars.get("recommendation"));
}

@Test
void updateAiDecision_continue_mediumRisk_shouldAssignToComplianceOfficer() {
  Long inspectionId = 2L;
  int riskScore = 75;

  mockZeebeChain();
  when(operateService.getRiskScore(inspectionId)).thenReturn(riskScore);

  InspectionCase inspectionCase = new InspectionCase();
  when(inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue()))
  .thenReturn(inspectionCase);
  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "continue");

  assertEquals(HttpStatus.OK, resp.getStatusCode());
  Map<String, Object> body = (Map<String, Object>) resp.getBody();

  assertEquals("Assign case to compliance officer", body.get("savedRecommendation"));
  assertEquals("Assign case to compliance officer", inspectionCase.getRecommendation());

  verify(inspectionCaseDAO).updateInspectionCase(inspectionCase);
  ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
  verify(setVariablesStep1).variables(varsCaptor.capture());

  Map<String, Object> vars = varsCaptor.getValue();
  assertEquals(Boolean.TRUE, vars.get("AIrecommendation"));
  assertEquals("Assign case to compliance officer", vars.get("recommendation"));
}
@Test
void updateAiDecision_continue_lowRisk_shouldNoAiRecommendation() {
  Long inspectionId = 3L;

  mockZeebeChain();
  when(operateService.getRiskScore(inspectionId)).thenReturn(60); // < 70

  InspectionCase inspectionCase = new InspectionCase();
  when(inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue()))
  .thenReturn(inspectionCase);
  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "continue");

  assertEquals(HttpStatus.OK, resp.getStatusCode());
  Map<String, Object> body = (Map<String, Object>) resp.getBody();

  assertEquals("No AI recommendation", body.get("savedRecommendation"));
  assertEquals("No AI recommendation", inspectionCase.getRecommendation());

  verify(inspectionCaseDAO).updateInspectionCase(inspectionCase);
  ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
  verify(setVariablesStep1).variables(varsCaptor.capture());

  Map<String, Object> vars = varsCaptor.getValue();
  assertEquals(Boolean.TRUE, vars.get("AIrecommendation"));
  assertEquals("No AI recommendation", vars.get("recommendation"));

}

@Test
void updateAiDecision_continue_nullRisk_shouldNoAiRecommendation() {
  Long inspectionId = 4L;

  mockZeebeChain();
  when(operateService.getRiskScore(inspectionId)).thenReturn(null);

  InspectionCase inspectionCase = new InspectionCase();
  when(inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue()))
  .thenReturn(inspectionCase);
  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "continue");

  assertEquals(HttpStatus.OK, resp.getStatusCode());
  Map<String, Object> body = (Map<String, Object>) resp.getBody();

  assertEquals("No AI recommendation", body.get("savedRecommendation"));
  assertEquals("No AI recommendation", inspectionCase.getRecommendation());

  verify(inspectionCaseDAO).updateInspectionCase(inspectionCase);
}

@Test
void updateAiDecision_skip_shouldSetDidNotFollowRecommendation() {
  Long inspectionId = 5L;
  int riskScore = 90; // ignored for skip

  mockZeebeChain();
  when(operateService.getRiskScore(inspectionId)).thenReturn(riskScore);

  InspectionCase inspectionCase = new InspectionCase();
  when(inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue()))
  .thenReturn(inspectionCase);
  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "skip");

  assertEquals(HttpStatus.OK, resp.getStatusCode());
  Map<String, Object> body = (Map<String, Object>) resp.getBody();

  assertEquals(false, body.get("AIrecommendation"));
  assertEquals("Did not followed the recommendations", body.get("savedRecommendation"));
  assertEquals("Did not followed the recommendations", inspectionCase.getRecommendation());

  verify(inspectionCaseDAO).updateInspectionCase(inspectionCase);
  ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
  verify(setVariablesStep1).variables(varsCaptor.capture());

  Map<String, Object> vars = varsCaptor.getValue();
  assertEquals(Boolean.FALSE, vars.get("AIrecommendation"));
  assertEquals("Did not followed the recommendations", vars.get("recommendation"));

}

@Test
void updateAiDecision_whenException_shouldReturn500() {
  Long inspectionId = 6L;

  // Don't mock Zeebe here, it's never reached when an exception is thrown
  when(operateService.getRiskScore(inspectionId))
          .thenThrow(new RuntimeException("Something went wrong"));

  ResponseEntity<?> resp = controller.updateAiDecision(inspectionId, "continue");

  assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());

  @SuppressWarnings("unchecked")
  Map<String, Object> body = (Map<String, Object>) resp.getBody();
  assertNotNull(body);
  assertTrue(String.valueOf(body.get("error")).contains("Something went wrong"));

  // Optional: ensure we never touched Zeebe or DAO in this error flow
  verifyNoInteractions(zeebeClient, inspectionCaseDAO);
}

@Test
void testControllerDelegatesToService() {
    MockitoAnnotations.openMocks(this);

    List<IDPSummaryResponseDTO> expected = List.of(mock(IDPSummaryResponseDTO.class));

    when(service.search("ABC", "Electrical", 100L, "ADP")).thenReturn(expected);

    List<IDPSummaryResponseDTO> result =
            controller.getSummary("ABC", "Electrical", 100L, "ADP");

    assertEquals(expected, result);
    verify(service, times(1)).search("ABC", "Electrical", 100L, "ADP");
}
@Test
void completeComplianceOfficerTask_success() throws Exception {

    long inspectionId = 101L;
    String taskId = "TASK_123";
    String taskResult = "Task Completed";

    when(tasklistservice.getActiveTaskID(String.valueOf(inspectionId)))
            .thenReturn(taskId);

    when(tasklistservice.CompleteTaskByID(eq(taskId), any()))
            .thenReturn(taskResult);

    mockMvc.perform(
            post("/CompleteComplianceOfficer/{inspectionId}/complete", inspectionId)
                    .contentType(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inspectionId").value(inspectionId))
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.message")
                    .value("Compliance Officer task completed successfully"))
            .andExpect(jsonPath("$.taskResponse").value(taskResult));
}

// ❌ NO ACTIVE TASK FOUND
@Test
void completeComplianceOfficerTask_noActiveTask() throws Exception {

    long inspectionId = 102L;

    when(tasklistservice.getActiveTaskID(String.valueOf(inspectionId)))
            .thenReturn(null);

    mockMvc.perform(
            post("/CompleteComplianceOfficer/{inspectionId}/complete", inspectionId)
                    .contentType(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error")
                    .value("No active task found for inspectionId: " + inspectionId));
}

// ❌ SERVICE EXCEPTION
@Test
void completeComplianceOfficerTask_exceptionThrown() throws Exception {

    long inspectionId = 103L;

    when(tasklistservice.getActiveTaskID(String.valueOf(inspectionId)))
            .thenThrow(new RuntimeException("Tasklist service down"));

    mockMvc.perform(
            post("/CompleteComplianceOfficer/{inspectionId}/complete", inspectionId)
                    .contentType(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Tasklist service down"));
}
}





