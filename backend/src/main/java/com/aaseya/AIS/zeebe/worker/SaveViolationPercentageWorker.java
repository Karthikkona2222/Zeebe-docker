package com.aaseya.AIS.zeebe.worker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.AIS.Model.NewEntity;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
@Component
public class SaveViolationPercentageWorker {
	
	@Autowired
    private EntityManager entityManager;
@Transactional
    @JobWorker(type = "save-violation-percentage", autoComplete = true)
    public void handleSaveViolationPercentage(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        String entityId = (String) variables.get("entityId"); // Ensure you pass this from the BPMN
        Double violationPercentage = ((Number) variables.get("violationPercentage")).doubleValue();

        NewEntity entity = entityManager.find(NewEntity.class, entityId);
        if (entity != null) {
            entity.setViolation_Percentage(violationPercentage);
            entityManager.merge(entity);
        } else {
            throw new RuntimeException("Entity not found for ID: " + entityId);
        }
    }

}
