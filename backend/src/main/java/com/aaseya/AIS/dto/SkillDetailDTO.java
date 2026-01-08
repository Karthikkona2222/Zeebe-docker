package com.aaseya.AIS.dto;

import java.util.List;

public class SkillDetailDTO {

	private String name;
	private List<InspectionTypeDTO> inspectionTypeDTO;
	private List<UsersDTO> usersDTO;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<InspectionTypeDTO> getInspectionTypeDTO() {
		return inspectionTypeDTO;
	}
	public void setInspectionTypeDTO(List<InspectionTypeDTO> inspectionTypeDTO) {
		this.inspectionTypeDTO = inspectionTypeDTO;
	}
	public List<UsersDTO> getUsersDTO() {
		return usersDTO;
	}
	public void setUsersDTO(List<UsersDTO> usersDTO) {
		this.usersDTO = usersDTO;
	}
	
}
