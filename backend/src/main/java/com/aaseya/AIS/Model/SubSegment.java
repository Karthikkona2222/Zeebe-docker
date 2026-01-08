package com.aaseya.AIS.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SubSegment")
public class SubSegment {

//	@Id
//	@Column(name = "subsegment_id")
//	@GeneratedValue(generator = SubSegmentIDGenerator.GENERATOR_NAME)
//	@GenericGenerator(name = SubSegmentIDGenerator.GENERATOR_NAME, strategy = "com.aaseya.AIS.Model.SubSegmentIDGenerator")
//	private String subsegment_id;

	@Column(name = "description")
	private String description;

	@Id	
	@Column(name = "sub_segment_name")
	private String name;

	@Column(name = "IsActive")
	private String IsActive;	

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsActive() {
		return IsActive;
	}

	public void setIsActive(String isActive) {
		IsActive = isActive;
	}
	
	@Override
	public String toString() {
		return "SubSegment [description=" + description + ", name=" + name + ", IsActive=" + IsActive + "]";
	}	

}
