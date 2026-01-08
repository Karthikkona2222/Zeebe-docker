package com.aaseya.AIS.dto;

public class CSRDocumentDTO {
	
	
    private String documentType;
    private byte[] document; // base64 encoded in JSON
    
    
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public byte[] getDocument() {
		return document;
	}
	public void setDocument(byte[] document) {
		this.document = document;
	}

    // getters & setters
}
