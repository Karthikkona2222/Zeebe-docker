package com.aaseya.AIS.dto;

public class TopTenNegativeObservationsDTO {
	  private Long sno; // Serial Number
	    private String categoryName; // Category name
	    private String checklistItemName; // Checklist item name
	    private Long negativeCount; // Count of negative observations
		public Long getSno() {
			return sno;
		}
		public void setSno(Long sno) {
			this.sno = sno;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getChecklistItemName() {
			return checklistItemName;
		}
		public void setChecklistItemName(String checklistItemName) {
			this.checklistItemName = checklistItemName;
		}
		public Long getNegativeCount() {
			return negativeCount;
		}
		public void setNegativeCount(Long negativeCount) {
			this.negativeCount = negativeCount;
		}

	

}
