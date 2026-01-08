package com.aaseya.AIS.Model;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Segment")
public class Segment {

	
//	@GeneratedValue(generator = SegmentIDGenerator.GENERATOR_NAME)
//	@GenericGenerator(name = SegmentIDGenerator.GENERATOR_NAME, strategy = "com.aaseya.AIS.Model.SegmentIDGenerator")
//	private String segment_id;

	@Id	
	@Column(name = "segment_name")
	private String segment_name;

	@Column(name = "IsActive")
	private String IsActive;

	@Column
	private String description;

	@OneToMany(cascade = CascadeType.MERGE)
	@JoinColumn(name = "segment_name")
	private List<SubSegment> subSegment;	

	public String getSegment_name() {
		return segment_name;
	}

	public void setSegment_name(String segment_name) {
		this.segment_name = segment_name;
	}

	public String getIsActive() {
		return IsActive;
	}

	public void setIsActive(String isActive) {
		IsActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SubSegment> getSubSegment() {
		return subSegment;
	}

	public void setSubSegment(List<SubSegment> subSegment) {
		this.subSegment = subSegment;
	}

	@Override
	public String toString() {
		return "Segment [name=" + segment_name + ", IsActive=" + IsActive + ", description=" + description + "]";
	}	

}
