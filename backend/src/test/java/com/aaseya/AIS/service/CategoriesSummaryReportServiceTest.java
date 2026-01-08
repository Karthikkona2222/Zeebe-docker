package com.aaseya.AIS.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aaseya.AIS.dao.CategoriesSummaryReportDAO;
import com.aaseya.AIS.dto.TopTenNegativeObservationsDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CategoriesSummaryReportServiceTest {

    @Mock
    private CategoriesSummaryReportDAO categoriesSummaryReportDAO;

    @InjectMocks
    private CategoriesSummaryReportService categoriesSummaryReportService;

    // ---------- 1. processType = null / empty → DAO called with null ----------
    @Test
    void testGetTopNegativeCategories_whenProcessTypeNull_callsDaoWithNull() {
        Long insTypeId = 1L;

        // ✅ Explicit List<Object[]>
        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{"Safety", 5L});
        daoResult.add(new Object[]{"Electrical", 3L});

        when(categoriesSummaryReportDAO
                .getTop5CategoriesWithNegativeObservations(insTypeId, null))
                .thenReturn(daoResult);

        Map<String, Long> result =
                categoriesSummaryReportService.getTopNegativeCategories(insTypeId, null);

        assertEquals(2, result.size());
        assertEquals(5L, result.get("Safety"));
        assertEquals(3L, result.get("Electrical"));

        verify(categoriesSummaryReportDAO)
                .getTop5CategoriesWithNegativeObservations(insTypeId, null);
    }

    // ---------- 2. processType = ADP → DAO called with "ADP" ----------
    @Test
    void testGetTopNegativeCategories_whenProcessTypeADP_callsDaoWithAdp() {
        Long insTypeId = 2L;

        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{"Radiation", 4L});

        when(categoriesSummaryReportDAO
                .getTop5CategoriesWithNegativeObservations(insTypeId, "ADP"))
                .thenReturn(daoResult);

        Map<String, Long> result =
                categoriesSummaryReportService.getTopNegativeCategories(insTypeId, "ADP");

        assertEquals(1, result.size());
        assertEquals(4L, result.get("Radiation"));

        verify(categoriesSummaryReportDAO)
                .getTop5CategoriesWithNegativeObservations(insTypeId, "ADP");
    }

    // ---------- 3. processType = anything else → DAO called with "NON_ADP" ----------
    @Test
    void testGetTopNegativeCategories_whenProcessTypeOther_callsDaoWithNonAdp() {
        Long insTypeId = 3L;

        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{"Mechanical", 2L});

        when(categoriesSummaryReportDAO
                .getTop5CategoriesWithNegativeObservations(insTypeId, "NON_ADP"))
                .thenReturn(daoResult);

        Map<String, Long> result =
                categoriesSummaryReportService.getTopNegativeCategories(insTypeId, "Manual");

        assertEquals(1, result.size());
        assertEquals(2L, result.get("Mechanical"));

        verify(categoriesSummaryReportDAO)
                .getTop5CategoriesWithNegativeObservations(insTypeId, "NON_ADP");
    }
    
    @Test
    void testGetTop10NegativeObservations_withInsTypeId_notNull() {
        Long insTypeId = 1L;
        String processType = "ADP";

        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{"Cat1", "Item1", 5L});
        daoResult.add(new Object[]{"Cat2", "Item2", 3L});

        when(categoriesSummaryReportDAO
                .getTop10CategoriesWithNegativeObservations(insTypeId, processType))
                .thenReturn(daoResult);

        List<TopTenNegativeObservationsDTO> result =
                categoriesSummaryReportService.getTop10NegativeObservations(insTypeId, processType);

        // verify size
        assertEquals(2, result.size());

        // verify mapping & SNO sequence
        TopTenNegativeObservationsDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getSno());
        assertEquals("Cat1", dto1.getCategoryName());
        assertEquals("Item1", dto1.getChecklistItemName());
        assertEquals(5L, dto1.getNegativeCount());

        TopTenNegativeObservationsDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getSno());
        assertEquals("Cat2", dto2.getCategoryName());
        assertEquals("Item2", dto2.getChecklistItemName());
        assertEquals(3L, dto2.getNegativeCount());

        // verify DAO call
        verify(categoriesSummaryReportDAO)
                .getTop10CategoriesWithNegativeObservations(insTypeId, processType);
    }

    // ---------- 2. ins_Type_Id == null ----------
    @Test
    void testGetTop10NegativeObservations_withInsTypeId_null() {
        Long insTypeId = null;
        String processType = "Manual";

        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{"CatX", "ItemX", 7L});

        when(categoriesSummaryReportDAO
                .getTop10CategoriesWithNegativeObservations(null, processType))
                .thenReturn(daoResult);

        List<TopTenNegativeObservationsDTO> result =
                categoriesSummaryReportService.getTop10NegativeObservations(insTypeId, processType);

        assertEquals(1, result.size());

        TopTenNegativeObservationsDTO dto = result.get(0);
        assertEquals(1L, dto.getSno());  // starts again from 1
        assertEquals("CatX", dto.getCategoryName());
        assertEquals("ItemX", dto.getChecklistItemName());
        assertEquals(7L, dto.getNegativeCount());

        verify(categoriesSummaryReportDAO)
                .getTop10CategoriesWithNegativeObservations(null, processType);
    }
}

