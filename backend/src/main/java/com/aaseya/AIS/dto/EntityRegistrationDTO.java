package com.aaseya.AIS.dto;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.aaseya.AIS.Model.EntityAttachments;
import com.aaseya.AIS.Model.Inspection_Type;

public class EntityRegistrationDTO {

	private String entityType;
	private SiteDTO site;
	private ProductDTO product;
	private LicenseDTO license;
	private String entityId;
	private String action;
	private List<Long> insTypeId;
	private Set<Inspection_Type> inspectionTypes;
	private List<MultipartFile> attachments;
	private List<EntityAttachments> entityAttachments;
	private List<Long> deleteAttachmentIds;

	public List<EntityAttachments> getEntityAttachments() {
		return entityAttachments;
	}

	public void setEntityAttachments(List<EntityAttachments> entityAttachments) {
		this.entityAttachments = entityAttachments;
	}

	public List<MultipartFile> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<MultipartFile> attachments) {
		this.attachments = attachments;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public SiteDTO getSite() {
		return site;
	}

	public void setSite(SiteDTO site) {
		this.site = site;
	}

	public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}

	public LicenseDTO getLicense() {
		return license;
	}

	public void setLicense(LicenseDTO license) {
		this.license = license;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Long> getInsTypeId() {
		return insTypeId;
	}

	public void setInsTypeId(List<Long> insTypeId) {
		this.insTypeId = insTypeId;
	}

	public Set<Inspection_Type> getInspectionTypes() { 
	        return inspectionTypes;
	    }

	public void setInspectionTypes(Set<Inspection_Type> inspectionTypes) { 
	        this.inspectionTypes = inspectionTypes;
	    }

	public List<Long> getDeleteAttachmentIds() {
		return deleteAttachmentIds;
	}

	public void setDeleteAttachmentIds(List<Long> deleteAttachmentIds) {
		this.deleteAttachmentIds = deleteAttachmentIds;
	}

	@Override
    public String toString() {
        return "EntityRegistrationDTO [entityType=" + entityType + ", site=" + site + ", product=" + product
                + ", license=" + license + ", entityId=" + entityId + ", action=" + action + ", insTypeId=" + insTypeId
                + ", inspectionTypes=" + inspectionTypes + ", attachments=" + attachments + ", entityAttachments="
                + entityAttachments + ", deleteAttachmentIds=" + deleteAttachmentIds + "]";
    }

}