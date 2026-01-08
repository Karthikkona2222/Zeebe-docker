package com.aaseya.AIS.Model;

import java.util.Arrays;

import jakarta.persistence.*;

@Entity
@Table(name = "entity_attachments")

public class EntityAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;
    
    @Column(name = "attachment_type", nullable = false)
    private String attachmentType;
    
    @Column(name = "file_type", nullable = false)
    private String fileType;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "location", nullable = false)
    private String location;
    
    
    @ManyToOne()
    @JoinColumn(name = "entity_id", nullable = false)
    private NewEntity newEntity;

    public NewEntity getNewEntity() {
        return newEntity;
    }

    public void setNewEntity(NewEntity newEntity) {
        this.newEntity = newEntity;
    }

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

//	public byte[] getAttachment() {
//		return attachment;
//	}
//
//	public void setAttachment(byte[] attachment) {
//		this.attachment = attachment;
//	}

	@Override
	public String toString() {
		return "EntityAttachments [attachmentId=" + attachmentId + ", attachmentType=" + attachmentType + ", fileType="
				+ fileType + ", fileName=" + fileName + ", location=" + location + ", newEntity=" + newEntity + "]";
	}
    
    
}

