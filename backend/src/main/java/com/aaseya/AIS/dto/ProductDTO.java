package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.Date;

public class ProductDTO {
    private String productName;
    private String productCode;
    private String productCategory;
    private LocalDate productExpiryDate;
    private String serialNumber;
    private String manufacturer;
    private String notes;
    private String productAddress;


//    // Constructors
//    public ProductDTO() {}
//
//    public ProductDTO(String productName, String productCode, String productCategory, Date productExpiryDate, 
//                      String serialNumber, String manufacturer, String notes) {
//        this.productName = productName;
//        this.productCode = productCode;
//        this.productCategory = productCategory;
//        this.productExpiryDate = productExpiryDate;
//        this.serialNumber = serialNumber;
//        this.manufacturer = manufacturer;
//        this.notes = notes;
//    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductAddress() {
		return productAddress;
	}

	public void setProductAddress(String productAddress) {
		this.productAddress = productAddress;
	}

	public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public LocalDate getProductExpiryDate() {
        return productExpiryDate;
    }

    public void setProductExpiryDate(LocalDate productExpiryDate) {
        this.productExpiryDate = productExpiryDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
	public String toString() {
		return "ProductDTO [productName=" + productName + ", productCode=" + productCode + ", productCategory="
				+ productCategory + ", productExpiryDate=" + productExpiryDate + ", serialNumber=" + serialNumber
				+ ", manufacturer=" + manufacturer + ", notes=" + notes + ", productAddress=" + productAddress + "]";
	}
}