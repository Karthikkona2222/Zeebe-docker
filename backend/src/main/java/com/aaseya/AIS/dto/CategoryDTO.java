package com.aaseya.AIS.dto;

public class CategoryDTO {
    private String categoryName;
    private long categoryId;
   
    private Boolean status;

    // Default constructor
    public CategoryDTO() {
    }

    // Parameterized constructor (if needed for other purposes)
    public CategoryDTO(String categoryName, long categoryId, Boolean status) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        
        this.status = status;
    }

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

  
   }