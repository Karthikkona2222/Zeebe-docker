package com.aaseya.AIS.zeebe.worker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.InspectionCaseDAO;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.SetVariablesCommandStep1;
import io.camunda.zeebe.client.api.command.SetVariablesCommandStep1.SetVariablesCommandStep2;
import io.camunda.zeebe.client.api.response.SetVariablesResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RouteInspectionJobWorkerTest {

    private static final long PROCESS_INSTANCE_KEY = 100L;
    private static final String COMPLIANCE_EMAIL = "ComplianceOfficer@gmail.com";

    @Mock
    private InspectionCaseDAO inspectionCaseDAO;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient zeebeClient;

    @Mock
    private ActivatedJob job;

    @Mock
    private JobClient jobClient;

    @Mock
    private SetVariablesCommandStep1 mockSetVarsStep1;

    @Mock
    private SetVariablesCommandStep2 mockSetVarsStep2;

    @Mock
    private ZeebeFuture<SetVariablesResponse> mockZeebeFuture;

    @InjectMocks
    private RouteInspectionJobWorker worker;

    @BeforeEach
    void setUp() {
        when(zeebeClient.newSetVariablesCommand(anyLong())).thenReturn(mockSetVarsStep1);
        when(mockSetVarsStep1.variables(anyMap())).thenReturn(mockSetVarsStep2);
        when(mockSetVarsStep2.send()).thenReturn(mockZeebeFuture);
        when(mockZeebeFuture.join()).thenReturn(null);

        when(job.getProcessInstanceKey()).thenReturn(PROCESS_INSTANCE_KEY);
    }

    // ✅ happy path: existing case is updated with compliance email and Zeebe vars set
    @Test
    void handleRouteInspection_shouldUpdateInspectionCase_whenExists() {
        String businessKey = "CASE-999";
        Long expectedId = 999L;   // 👈 use Long (wrapper)

        InspectionCase existing = new InspectionCase();
        existing.setInspectionID(expectedId);
        existing.setStatus("OldStatus");

        when(job.getVariablesAsMap()).thenReturn(Map.of("BusinessKey", businessKey));
        when(inspectionCaseDAO.getInspectionCaseById(expectedId))
                .thenReturn(Optional.of(existing));   // ✅ now matches method 2️⃣

        worker.handleRouteInspection(job, jobClient);

        verify(inspectionCaseDAO, times(1)).updateInspectionCase(existing);
        assertEquals("ComplianceOfficer@gmail.com", existing.getComplianceID());
    }

    // ❌ BusinessKey missing in variables → worker throws RuntimeException (wrapped)
    @Test
    void handleRouteInspection_shouldThrowRuntime_whenBusinessKeyMissing() {
        when(job.getVariablesAsMap()).thenReturn(Map.of()); // no BusinessKey

        RuntimeException outer = assertThrows(RuntimeException.class,
                () -> worker.handleRouteInspection(job, jobClient));

        assertNotNull(outer.getCause());
        assertTrue(outer.getCause() instanceof RuntimeException);
        assertTrue(outer.getCause().getMessage().contains("BusinessKey variable is missing"));
    }

    // ❌ BusinessKey has no digits → NumberFormatException wrapped in RuntimeException
    @Test
    void handleRouteInspection_shouldThrowRuntime_whenBusinessKeyHasNoDigits() {
        when(job.getVariablesAsMap()).thenReturn(Map.of("BusinessKey", "NO_DIGITS_HERE"));

        RuntimeException outer = assertThrows(RuntimeException.class,
                () -> worker.handleRouteInspection(job, jobClient));

        assertNotNull(outer.getCause());
        assertTrue(outer.getCause() instanceof NumberFormatException);
    }

    // ❌ Inspection case not found → inner RuntimeException wrapped in outer RuntimeException
    @Test
    void handleRouteInspection_shouldThrowRuntime_whenInspectionCaseNotFound() {
        String businessKey = "INS-123";
        Long expectedId = 123L;  // 👈 change to Long

        when(job.getVariablesAsMap()).thenReturn(Map.of("BusinessKey", businessKey));
        when(inspectionCaseDAO.getInspectionCaseById(expectedId))
                .thenReturn(Optional.empty());  // ✅ now matches Optional<InspectionCase> method

        RuntimeException outer = assertThrows(RuntimeException.class,
                () -> worker.handleRouteInspection(job, jobClient));

        assertNotNull(outer.getCause());
        assertTrue(outer.getCause().getMessage().contains("Inspection case not found for ID: " + expectedId));
    }

}
