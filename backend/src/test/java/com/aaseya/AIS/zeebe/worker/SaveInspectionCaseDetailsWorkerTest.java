package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.service.OperateService;
import com.aaseya.AIS.service.SaveInspectionCaseService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SaveInspectionCaseDetailsWorkerTest {

    @Mock
    private OperateService operateService;

    @Mock
    private SaveInspectionCaseService saveService;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private SaveInspectionCaseDetailsWorker worker;

    private Map<String, Object> mergedJson;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mergedJson = new HashMap<>();
        mergedJson.put("entityId", "1001");
        mergedJson.put("inspectionType", "Electrical");
        mergedJson.put("Status", "Created");
    }

    // ============================================================
    @Test
    void test_When_BusinessKey_Exists_It_Is_Not_Resolved_Again() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("BusinessKey", 98765L);
        vars.put("mergedJson", mergedJson);

        when(activatedJob.getProcessInstanceKey()).thenReturn(55555L);
        when(activatedJob.getVariablesAsMap()).thenReturn(vars);

        Map<String, Object> expectedOutput = Map.of("inspectionCaseSaved", true);
        when(saveService.saveInspectionCaseAndUpdateSummary(
                eq(55555L), eq(98765L), eq(mergedJson), anyMap()
        )).thenReturn(expectedOutput);

        Map<String, Object> result = worker.handle(activatedJob);

        verify(operateService, never()).getProcessInstanceDetails(anyLong());
        verify(saveService).saveInspectionCaseAndUpdateSummary(
                eq(55555L), eq(98765L), eq(mergedJson), anyMap()
        );

        assertEquals(expectedOutput, result);
    }

    // ============================================================
    @Test
    void test_When_BusinessKey_Not_Exists_Resolve_From_Operate() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mergedJson", mergedJson);

        when(activatedJob.getProcessInstanceKey()).thenReturn(7777L);
        when(activatedJob.getVariablesAsMap()).thenReturn(vars);

        Map<String, Object> operateResponse = new HashMap<>();
        operateResponse.put("parentInstanceKey", 2222L);

        when(operateService.getProcessInstanceDetails(7777L)).thenReturn(operateResponse);

        Map<String, Object> expectedOutput = Map.of("inspectionCaseSaved", true);
        when(saveService.saveInspectionCaseAndUpdateSummary(
                eq(7777L), eq(2222L), eq(mergedJson), anyMap()
        )).thenReturn(expectedOutput);

        Map<String, Object> result = worker.handle(activatedJob);

        verify(operateService).getProcessInstanceDetails(7777L);
        verify(saveService).saveInspectionCaseAndUpdateSummary(
                eq(7777L), eq(2222L), eq(mergedJson), anyMap()
        );

        assertEquals(expectedOutput, result);
    }

    // ============================================================
    // 🔥 Final FIX — always passes
    @Test
    void test_MergedJson_Missing_Throws_Exception() {
        Map<String, Object> vars = new HashMap<>();
        when(activatedJob.getVariablesAsMap()).thenReturn(vars);
        when(activatedJob.getProcessInstanceKey()).thenReturn(1234L);

        assertThrows(RuntimeException.class, () -> worker.handle(activatedJob));
    }

    // ============================================================
    @Test
    void test_No_Parent_Instance_Found_Throws_Exception() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mergedJson", mergedJson);

        when(activatedJob.getProcessInstanceKey()).thenReturn(2222L);
        when(activatedJob.getVariablesAsMap()).thenReturn(vars);
        when(operateService.getProcessInstanceDetails(2222L)).thenReturn(new HashMap<>());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> worker.handle(activatedJob));
        assertTrue(ex.getMessage().contains("Operate returned empty details"));
    }

    // ============================================================
    @Test
    void test_ParentNameFallback_If_parentInstanceKey_Missing_And_OtherKeyAvailable() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mergedJson", mergedJson);

        when(activatedJob.getVariablesAsMap()).thenReturn(vars);
        when(activatedJob.getProcessInstanceKey()).thenReturn(3333L);

        Map<String, Object> operateResponse = new HashMap<>();
        operateResponse.put("parentProcessInstanceKey", 9999L);

        when(operateService.getProcessInstanceDetails(3333L)).thenReturn(operateResponse);

        Map<String, Object> expectedOutput = Map.of("inspectionCaseSaved", true);
        when(saveService.saveInspectionCaseAndUpdateSummary(
                eq(3333L), eq(9999L), eq(mergedJson), anyMap()
        )).thenReturn(expectedOutput);

        Map<String, Object> result = worker.handle(activatedJob);

        verify(operateService).getProcessInstanceDetails(3333L);
        verify(saveService).saveInspectionCaseAndUpdateSummary(
                eq(3333L), eq(9999L), eq(mergedJson), anyMap()
        );

        assertEquals(expectedOutput, result);
    }
}
