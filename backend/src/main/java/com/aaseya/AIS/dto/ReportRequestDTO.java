package com.aaseya.AIS.dto;
public class ReportRequestDTO {
    private String overview;
    private String observations;
    private String riskScore;
    private String recommendations;
    private String summary;
    private String measurements;  
    private String keyRisks;       
	public final String getOverview() {
		return overview;
	}
	public final void setOverview(String overview) {
		this.overview = overview;
	}
	public final String getMeasurements() {
		return measurements;
	}
	public final void setMeasurements(String measurements) {
		this.measurements = measurements;
	}
	public final String getKeyRisks() {
		return keyRisks;
	}
	public final void setKeyRisks(String keyRisks) {
		this.keyRisks = keyRisks;
	}
	public final String getObservations() {
		return observations;
	}
	public final void setObservations(String observations) {
		this.observations = observations;
	}
	public final String getRiskScore() {
		return riskScore;
	}
	public final void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}
	public final String getRecommendations() {
		return recommendations;
	}
	public final void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}
	public final String getSummary() {
		return summary;
	}
	public final void setSummary(String summary) {
		this.summary = summary;
	}
 
   
}
 
 
 
