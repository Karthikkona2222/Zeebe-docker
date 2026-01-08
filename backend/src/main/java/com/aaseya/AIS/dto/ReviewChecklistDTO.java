package com.aaseya.AIS.dto;

import java.util.List;

public class ReviewChecklistDTO {

	private String checklistName;
	private List<ReviewDTO> reviewList;

	public String getChecklistName() {
		return checklistName;
	}

	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}

	public List<ReviewDTO> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<ReviewDTO> reviewList) {
		this.reviewList = reviewList;
	}

}
