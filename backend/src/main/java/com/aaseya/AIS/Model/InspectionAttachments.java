package com.aaseya.AIS.Model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Base64;

@Entity
@Table(name = "inspection_attachments") // Ensure table name is correct
public class InspectionAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id") // Ensure column names match your DB schema
    private long attachmentID;


    @Column(name = "attachment",columnDefinition = "BYTEA")
    private byte[] attachment;


    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "inspectionID", referencedColumnName = "inspectionID"),
        @JoinColumn(name = "categoryID", referencedColumnName = "categoryID"),
        @JoinColumn(name = "checklistID", referencedColumnName = "checklistID")
    })
   
    private InspectionChecklistandAnswers inspectionChecklistandAnswers;

    // ✅ Getters and Setters
    public long getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(long attachmentID) {
        this.attachmentID = attachmentID;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public void setAttachmentFromBase64(String base64Attachment) {
        if (base64Attachment != null && !base64Attachment.isEmpty()) {
            this.attachment = Base64.getDecoder().decode(base64Attachment);
        }
    }
    public String getAttachmentAsBase64() {
        return (this.attachment != null) ? Base64.getEncoder().encodeToString(this.attachment) : null;
    }


    public InspectionChecklistandAnswers getInspectionChecklistandAnswers() {
        return inspectionChecklistandAnswers;
    }

   
    
    public void setInspectionChecklistandAnswers(InspectionChecklistandAnswers inspectionChecklistandAnswers) {
        this.inspectionChecklistandAnswers = inspectionChecklistandAnswers;
    }

	@Override
	public String toString() {
		return "InspectionAttachments [attachmentID=" + attachmentID + ", attachment=" + Arrays.toString(attachment)
				+ ", inspectionChecklistandAnswers=" + inspectionChecklistandAnswers + "]";
	}

}