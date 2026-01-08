package com.aaseya.AIS.dto;

public class LicenseDTO {
    private String licenseType;
    private String licenseName;
    private String licenseNumber;
    private String issuingAuthority;
    private String associateLocationOrSite;
    private String comments;
    private String status;
//    // Constructors
//    public LicenseDTO() {}
//
//    public LicenseDTO(String licenseType, String licenseName, String licenseNumber, String issuingAuthority, 
//                      String site, String comments) {
//        this.licenseType = licenseType;
//        this.licenseName = licenseName;
//        this.licenseNumber = licenseNumber;
//        this.issuingAuthority = issuingAuthority;
//        this.site = site;
//        this.comments = comments;
//    }

    // Getters and Setters
    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    

	public String getAssociateLocationOrSite() {
		return associateLocationOrSite;
	}

	public void setAssociateLocationOrSite(String associateLocationOrSite) {
		this.associateLocationOrSite = associateLocationOrSite;
	}

	public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "LicenseDTO [licenseType=" + licenseType + ", licenseName=" + licenseName + ", licenseNumber="
				+ licenseNumber + ", issuingAuthority=" + issuingAuthority + ", associateLocationOrSite="
				+ associateLocationOrSite + ", comments=" + comments + ", status=" + status + "]";
	}
}
