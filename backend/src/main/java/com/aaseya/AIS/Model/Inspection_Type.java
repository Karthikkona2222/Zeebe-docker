package com.aaseya.AIS.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Inspection_Type")
public class Inspection_Type {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ins_type_id")
	private long ins_type_id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String threshold;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(nullable = true)
	private LocalTime high;

	@Column(nullable = true)
	private LocalTime medium;

	@Column(nullable = true)
	private LocalTime low;
	
	 // ✅ New columns for template
    @Column(name = "selectedTemplateId")
    private Long selectedTemplateId;

    @Column(name = "selectedTemplateName")
    private String selectedTemplateName;

	@Column(name = "entity_size", nullable = false)
	private String entitySize;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "inspection_types")
	@JsonBackReference
	private Set<Template> templates;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "Inspection_Type_Entity", joinColumns = @JoinColumn(name = "ins_type_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))
	private Set<NewEntity> newEntities; // Many-to-Many relationship with NewEntity
	@JsonIgnore
	@OneToMany(mappedBy = "inspectionType",fetch = FetchType.EAGER)
	private List<Inspection_SLA> inspectionSLAs;
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "Inspection_Type_Skill", joinColumns = @JoinColumn(name = "ins_type_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
	private Set<Skill> skills;

//    // Many-to-Many relationship with PreInspectionChecklist
//    @ManyToMany(mappedBy = "inspectionTypes", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<PreInspectionChecklist> preInspectionChecklists;
	@JsonIgnore
	@OneToMany(mappedBy = "inspectionType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PreInspectionChecklist> preInspectionChecklist;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ControlType_InspectionType", // Same join table name
			joinColumns = @JoinColumn(name = "ins_type_id"), // Join column for Inspection_Type
			inverseJoinColumns = @JoinColumn(name = "controlTypeId") // Join column for ControlType
	)
	private Set<ControlType> controlTypes;
	
	@OneToMany(mappedBy = "inspectionType", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
	@JsonIgnore
	private List<Inspection_Type_Schedule> inspectionTypeSchedule;

	@OneToOne(mappedBy = "inspectionType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Inspection_Run_Log inspectionRunLog;

	// Default constructor

	public Set<ControlType> getControlTypes() {
		return controlTypes;
	}

	public void setControlTypes(Set<ControlType> controlTypes) {
		this.controlTypes = controlTypes;
	}


	// Default constructor
	public Inspection_Type() {
	}

	public long getIns_type_id() {
		return ins_type_id;
	}

	public void setIns_type_id(long ins_type_id) {
		this.ins_type_id = ins_type_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}


	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalTime getHigh() {
		return high;
	}

	public LocalTime getMedium() {
		return medium;
	}

	public LocalTime getLow() {
		return low;
	}

	public void setHigh(LocalTime high) {
		this.high = high;
	}

	public void setMedium(LocalTime medium) {
		this.medium = medium;
	}

	public void setLow(LocalTime low) {
		this.low = low;
	}

	public String getEntitySize() {
		return entitySize;
	}

	public void setEntitySize(String entitySize) {
		this.entitySize = entitySize;
	}

	public List<Inspection_SLA> getInspectionSLAs() {
		return inspectionSLAs;
	}

	public void setInspectionSLAs(List<Inspection_SLA> inspectionSLAs) {
		this.inspectionSLAs = inspectionSLAs;
	}

	public Set<Skill> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}

//    public Set<PreInspectionChecklist> getPreInspectionChecklists() {
//        return preInspectionChecklists;
//    }
//
//    public void setPreInspectionChecklists(Set<PreInspectionChecklist> preInspectionChecklists) {
//        this.preInspectionChecklists = preInspectionChecklists;
//    }

	public Set<NewEntity> getNewEntities() {
		return newEntities;
	}

	public void setNewEntities(Set<NewEntity> newEntities) {
		this.newEntities = newEntities;
	}

	public Set<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(Set<Template> templates) {
		this.templates = templates;
	}

	public List<PreInspectionChecklist> getPreInspectionChecklist() {
		return preInspectionChecklist;
	}

	public void setPreInspectionChecklist(List<PreInspectionChecklist> preInspectionChecklist) {
		this.preInspectionChecklist = preInspectionChecklist;
	}
	
	public List<Inspection_Type_Schedule> getInspectionTypeSchedule() {
		return inspectionTypeSchedule;
	}

	public void setInspectionTypeSchedule(List<Inspection_Type_Schedule> inspectionTypeSchedule) {
		this.inspectionTypeSchedule = inspectionTypeSchedule;
	}

	public Inspection_Run_Log getInspectionRunLog() {
		return inspectionRunLog;
	}

	public void setInspectionRunLog(Inspection_Run_Log inspectionRunLog) {
		this.inspectionRunLog = inspectionRunLog;
	}
	
	

	public Long getSelectedTemplateId() {
		return selectedTemplateId;
	}

	public void setSelectedTemplateId(Long selectedTemplateId) {
		this.selectedTemplateId = selectedTemplateId;
	}

	public String getSelectedTemplateName() {
		return selectedTemplateName;
	}

	public void setSelectedTemplateName(String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}

	@Override
	public String toString() {
		return "Inspection_Type [ins_type_id=" + ins_type_id + ", name=" + name + ", threshold=" + threshold
				+ ", isActive=" + isActive + ", high=" + high + ", medium=" + medium + ", low=" + low + ", entitySize="
				+ entitySize + "]";
	}

}
