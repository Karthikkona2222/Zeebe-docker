package com.aaseya.AIS.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OperateServiceTest {

    @Spy
    @InjectMocks
    private OperateService operateService;

    // 1) riskScore present, plain number
    @Test
    void getRiskScore_whenPlainNumber_shouldReturnInteger() {
        long processInstanceKey = 123L;

        doReturn("85")
            .when(operateService)
            .getVariableByName(processInstanceKey, "riskScore");

        Integer result = operateService.getRiskScore(processInstanceKey);

        assertNotNull(result);
        assertEquals(85, result);
    }

    // 2) riskScore present, with quotes -> still works because you strip them
    @Test
    void getRiskScore_whenNumberWithQuotes_shouldReturnInteger() {
        long processInstanceKey = 124L;

        doReturn("\"90\"")
            .when(operateService)
            .getVariableByName(processInstanceKey, "riskScore");

        Integer result = operateService.getRiskScore(processInstanceKey);

        assertNotNull(result);
        assertEquals(90, result);
    }

    // 3) riskScore variable missing -> null
    @Test
    void getRiskScore_whenVariableMissing_shouldReturnNull() {
        long processInstanceKey = 456L;

        doReturn(null)
            .when(operateService)
            .getVariableByName(processInstanceKey, "riskScore");

        Integer result = operateService.getRiskScore(processInstanceKey);

        assertNull(result);
    }

    // 4) riskScore not a number -> null
    @Test
    void getRiskScore_whenValueIsNotNumber_shouldReturnNull() {
        long processInstanceKey = 789L;

        doReturn("NOT_A_NUMBER")
            .when(operateService)
            .getVariableByName(processInstanceKey, "riskScore");

        Integer result = operateService.getRiskScore(processInstanceKey);

        assertNull(result);
    }
}

