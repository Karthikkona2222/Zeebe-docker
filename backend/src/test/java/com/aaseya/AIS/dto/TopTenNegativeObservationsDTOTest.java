package com.aaseya.AIS.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TopTenNegativeObservationsDTOTest {

    @Test
    void testTopTenNegativeObservationsDTO_GettersAndSetters() {
        // ✅ Arrange
        TopTenNegativeObservationsDTO dto = new TopTenNegativeObservationsDTO();

        Long sno = 1L;
        String categoryName = "Safety";
        String checklistItemName = "Fire Extinguisher";
        Long negativeCount = 5L;

        // ✅ Act
        dto.setSno(sno);
        dto.setCategoryName(categoryName);
        dto.setChecklistItemName(checklistItemName);
        dto.setNegativeCount(negativeCount);

        // ✅ Assert
        assertNotNull(dto);
        assertEquals(sno, dto.getSno());
        assertEquals(categoryName, dto.getCategoryName());
        assertEquals(checklistItemName, dto.getChecklistItemName());
        assertEquals(negativeCount, dto.getNegativeCount());
    }
}
