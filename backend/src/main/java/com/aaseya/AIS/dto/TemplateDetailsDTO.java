package com.aaseya.AIS.dto;

import java.util.List;

public class TemplateDetailsDTO {
    private Long templateId;
    private String templateName;
    private Long insTypeId;
    private String insTypeName;
    private List<CategoryDTO> categories;
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public Long getInsTypeId() {
		return insTypeId;
	}
	public void setInsTypeId(Long insTypeId) {
		this.insTypeId = insTypeId;
	}
	public String getInsTypeName() {
		return insTypeName;
	}
	public void setInsTypeName(String insTypeName) {
		this.insTypeName = insTypeName;
	}
	public List<CategoryDTO> getCategories() {
		return categories;
	}
	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}