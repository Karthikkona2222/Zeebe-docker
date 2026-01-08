//package com.aaseya.AIS.service;
// 
//import java.util.List;
// 
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
// 
//import com.aaseya.AIS.Model.InspectionCase;
//import com.aaseya.AIS.Model.InspectionChecklistandAnswers;
//import com.aaseya.AIS.dao.InspectionCaseDAO;
// 
//@Service
//public class InspectionServiceClass {
// 
//    @Autowired
//    private InspectionCaseDAO inspectionCaseDAO;
// 
//    public void addInspectionCase(InspectionCase inspectionCase) {
//        inspectionCaseDAO.saveInspectionCase(inspectionCase);
//    }
// 
//    public List<InspectionCase> getAllInspectionCases() {
//        return inspectionCaseDAO.getAllInspectionCases();
//    }
//}