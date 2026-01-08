package com.aaseya.AIS.dto;
 
import java.util.List;
import java.util.stream.Stream;

import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist;
 
public class SavePreInspectionDTO {
 
	private List<SaveSubmitPreInspectionChecklist> saveSubmitPreInspectionChecklist;

	private String action;
	private long inspectionID;

	public long getInspectionID() {
		return inspectionID;
	}

	public void setInspectionID(long inspectionID) {
		this.inspectionID = inspectionID;
	}

	public List<SaveSubmitPreInspectionChecklist> getSaveSubmitPreInspectionChecklist() {
		return saveSubmitPreInspectionChecklist;
	}

	public void setSaveSubmitPreInspectionChecklist(
			List<SaveSubmitPreInspectionChecklist> saveSubmitPreInspectionChecklist) {
		this.saveSubmitPreInspectionChecklist = saveSubmitPreInspectionChecklist;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Stream<SaveSubmitPreInspectionChecklist> stream() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

} 