package com.aaseya.AIS.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.ColumnTransformer;

import com.aaseya.AIS.utility.JsonNodeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Entity")
public class NewEntity {

	@Id
	@Column(name = "entity_id")
	@GeneratedValue(generator = EntityIDGenerator.GENERATOR_NAME)
	@GenericGenerator(name = EntityIDGenerator.GENERATOR_NAME, strategy = "com.aaseya.AIS.Model.EntityIDGenerator", parameters = {
			@Parameter(name = EntityIDGenerator.PREFIX_PARAM, value = "EN") })
	private String entityid;

	@Column(unique = true, name = "entity_name")
	private String name;

	@Column(name = "address")
	private String address;

	@Column(name = "facility")
	private String facility;

	@Column(name = "entity_type")
	private String type;
	
	@Column(name = "violation_Percentage")
	private Double violation_Percentage;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "segment_name")
	private Segment segment;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sub_segment_name")
	private SubSegment subSegment;

	@OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<InspectionCase> inspectionCases;

	// Many-to-Many relationship with Inspection_Type
	@ManyToMany(cascade = CascadeType.MERGE, mappedBy = "newEntities", fetch = FetchType.EAGER)
	private Set<Inspection_Type> inspectionTypes; // Many-to-Many relationship with Inspection_Type

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "zone_entity_mapping", // Join table for the many-to-many relationship
			joinColumns = @JoinColumn(name = "entity_id"), inverseJoinColumns = @JoinColumn(name = "zone_id"))
	private Set<Zone> zones = new HashSet<>();	
	
	@OneToMany(mappedBy = "newEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityAttachments> entityAttachments = new ArrayList<>();

	public List<EntityAttachments> getEntityAttachments() {
	    return entityAttachments;
	}

	public void setEntityAttachments(List<EntityAttachments> entityAttachments) {
	    this.entityAttachments = entityAttachments;
	}

	@Column(name = "representative_name")
	private String representativeName;

	@Column(name = "representative_email")
	private String representativeEmail;

	@Column(name = "representative_id")
	private String representativeId;
	
	@Column(name = "building")
	private String building;

	@Column(name = "representative_phoneno")
	private String representativePhoneNo;

	@Column(name = "floor")
	private String floor;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "is_address")
	private String isAddress;

	@Column(name = "zone_name")
	private String zoneName;

	@Column(name = "country_name")
	private String countryName;

	@Column(name = "size")
	private String size;
	
	@Column(name = "entity_details", columnDefinition = "jsonb")
	@Convert(converter = JsonNodeConverter.class)
	@ColumnTransformer(write = "?::jsonb")
	private JsonNode entityDetails;

	// Getters and Setters

	public String getEntityid() {
		return entityid;
	}

	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Segment getSegment() {
		return segment;
	}
	

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public SubSegment getSubSegment() {
		return subSegment;
	}

	public void setSubSegment(SubSegment subSegment) {
		this.subSegment = subSegment;
	}

	public List<InspectionCase> getInspectionCases() {
		return inspectionCases;
	}

	public void setInspectionCases(List<InspectionCase> inspectionCases) {
		this.inspectionCases = inspectionCases;
	}

	public Set<Zone> getZones() {
		return zones;
	}

	public void setZones(Set<Zone> zones) {
		this.zones = zones;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getRepresentativeEmail() {
		return representativeEmail;
	}

	public void setRepresentativeEmail(String representativeEmail) {
		this.representativeEmail = representativeEmail;
	}

	public String getRepresentativeId() {
		return representativeId;
	}

	public void setRepresentativeId(String representativeId) {
		this.representativeId = representativeId;
	}

	public String getRepresentativePhoneNo() {
		return representativePhoneNo;
	}

	public void setRepresentativePhoneNo(String representativePhoneNo) {
		this.representativePhoneNo = representativePhoneNo;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getIsAddress() {
		return isAddress;
	}

	public void setIsAddress(String isAddress) {
		this.isAddress = isAddress;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Set<Inspection_Type> getInspectionTypes() {
		return inspectionTypes;
	}

	public void setInspectionTypes(Set<Inspection_Type> inspectionTypes) {
		this.inspectionTypes = inspectionTypes;
	}
	
	public JsonNode getEntityDetails() {
		return entityDetails;
	}

	public void setEntityDetails(JsonNode entityDetails) {
		this.entityDetails = entityDetails;
	}

	public Double getViolation_Percentage() {
		return violation_Percentage;
	}

	public void setViolation_Percentage(Double violation_Percentage) {
		this.violation_Percentage = violation_Percentage;
	}

	@Override
	public String toString() {
		return "NewEntity [entityid=" + entityid + ", name=" + name + ", violation_Percentage=" + violation_Percentage + ",address=" + address + ", facility=" + facility
				+ ", type=" + type + ", representativeName=" + representativeName + ", representativeEmail="
				+ representativeEmail + ", representativeId=" + representativeId + ", building=" + building
				+ ", representativePhoneNo=" + representativePhoneNo + ", floor=" + floor + ", entityDetails="
				+ entityDetails + "]";
	}

}
