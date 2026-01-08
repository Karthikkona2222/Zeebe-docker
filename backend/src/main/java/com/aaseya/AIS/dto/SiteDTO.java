package com.aaseya.AIS.dto;

public class SiteDTO {
    private String siteName;
    private String siteManager;
    private String siteAddress;
    private String comments;
    private String siteType;

//    // Constructors
//    public SiteDTO() {}
//
//    public SiteDTO(String siteName, String siteManager, String siteAddress, String comments) {
//        this.siteName = siteName;
//        this.siteManager = siteManager;
//        this.siteAddress = siteAddress;
//        this.comments = comments;
//    }

    // Getters and Setters
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteManager() {
        return siteManager;
    }

    public void setSiteManager(String siteManager) {
        this.siteManager = siteManager;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	@Override
	public String toString() {
		return "SiteDTO [siteName=" + siteName + ", siteManager=" + siteManager + ", siteAddress=" + siteAddress
				+ ", comments=" + comments + ", siteType=" + siteType + "]";
	}
}