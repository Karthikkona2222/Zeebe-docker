package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.IDPAISummaryDAO;
import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IDPAIServiceTest {

    @Mock
    private IDPAISummaryDAO dao;

    @InjectMocks
    private IDPAIService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchByInspectionID() {
        when(dao.findByInspectionID(10L)).thenReturn(List.of());

        service.search(null, null, 10L, null);
        verify(dao, times(1)).findByInspectionID(10L);
    }

    @Test
    void testSearchByEntityInspectionSource() {
        when(dao.findByEntityInspectionSource("ABC", "Electrical", "ADP"))
                .thenReturn(List.of());

        service.search("ABC", "Electrical", null, "ADP");
        verify(dao, times(1)).findByEntityInspectionSource("ABC", "Electrical", "ADP");
    }

    @Test
    void testSearchWithoutFilters() {
        when(dao.findByEntityInspectionSource(null, null, "ADP"))
                .thenReturn(List.of());

        service.search(null, null, null, null);

        verify(dao, times(1)).findByEntityInspectionSource(null, null, "ADP");
    }
}
