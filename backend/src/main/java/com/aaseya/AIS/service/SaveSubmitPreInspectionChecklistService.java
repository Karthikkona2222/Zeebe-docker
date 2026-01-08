package com.aaseya.AIS.service;
 
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.SaveSubmitPreInspectionChecklistDAO;
import com.aaseya.AIS.dto.SavePreInspectionDTO;
 
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
 
@Service
public class SaveSubmitPreInspectionChecklistService {
 
    @Autowired
    private SaveSubmitPreInspectionChecklistDAO saveSubmitPreInspectionChecklistDAO;
    
    @Autowired
    private SaveSubmitPreInspectionChecklistDAO checklistDAO;
    
    @Autowired
    private InspectionCaseDAO inspectionCaseDAO;
 
    @Transactional
    public SaveSubmitPreInspectionChecklist saveOrUpdateChecklist(SaveSubmitPreInspectionChecklist checklist) {
        return saveSubmitPreInspectionChecklistDAO.saveOrUpdate(checklist);
    }
 
    @Transactional
    public List<SaveSubmitPreInspectionChecklist> saveOrUpdateChecklists(SavePreInspectionDTO dto) {
        List<SaveSubmitPreInspectionChecklist> checklists = dto.getSaveSubmitPreInspectionChecklist();
 
        List<SaveSubmitPreInspectionChecklist> savedChecklists = checklists.stream()
                .map(checklist -> {
                    // Example of processing comment if needed
                    if (checklist.getComment() != null && !checklist.getComment().isEmpty()) {
                        // Handle any comment-specific logic here
                    }
                    return checklistDAO.saveOrUpdate(checklist);
                })
                .collect(Collectors.toList());
 
        if ("submit".equalsIgnoreCase(dto.getAction())) {
            long inspectionID = dto.getInspectionID();
            InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionID);
            if (inspectionCase != null) {
                inspectionCase.setIs_preinspection_submitted(true);
                inspectionCaseDAO.updateInspectionCase(inspectionCase);
            }
        }
 
        return savedChecklists;
    }
 
    public List<SaveSubmitPreInspectionChecklist> getChecklistsByInspectionId(long inspectionid) {
        return checklistDAO.getByInspectionId(inspectionid);
    }
}
 