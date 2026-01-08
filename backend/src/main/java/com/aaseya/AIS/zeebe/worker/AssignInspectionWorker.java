package com.aaseya.AIS.zeebe.worker;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dto.UsersDTO;
import com.aaseya.AIS.service.InspectionCaseService;
import com.aaseya.AIS.service.InspectionMappingService;
import com.aaseya.AIS.service.TaskListService;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class AssignInspectionWorker {

    private static final Logger logger = LoggerFactory.getLogger(AssignInspectionWorker.class);

    @Autowired
    private TaskListService tasklistservice;

    @Autowired
    private InspectionCaseService inspectionCaseService;

    @Autowired
    private InspectionCaseDAO inspectionCaseDAO;

    @Autowired
    private InspectionMappingService inspectionMappingService;

    @Autowired
    private ZeebeClient zeebeClient;

    @JobWorker(type = "assign-inspection", autoComplete = true)
    public void assignInspection(ActivatedJob job) {

        Map<String, Object> variables = job.getVariablesAsMap();

        //List<UsersDTO> usersDTO = inspectionCaseService.getZoneUserDetailsByInspectionId(inspectionId);
}
}
