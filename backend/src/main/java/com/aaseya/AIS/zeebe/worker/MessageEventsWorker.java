package com.aaseya.AIS.zeebe.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class MessageEventsWorker {
	 private static final Logger logger = LoggerFactory.getLogger(MessageEventsWorker.class);
	@JobWorker(type = "handle-message-events", autoComplete = true)
	public void handleMessageEvents(final JobClient client, final ActivatedJob job) {

	    Map<String, Object> variables = job.getVariablesAsMap();
	    String eventType = (String) variables.get("eventType"); // You must pass this in the process
	    String inspectionId = (String) variables.get("inspectionId");

	    if (eventType == null) {
	        client.newFailCommand(job.getKey())
	              .retries(0)
	              .errorMessage("Missing eventType variable to handle message logic.")
	              .send().join();
	        return;
	    }

	    switch (eventType) {
	        case "sendToReinspection":
	            handleSendToReinspectionLogic(inspectionId);
	            break;

	        case "priorityReceived":
	            String priority = (String) variables.getOrDefault("priority", "Normal");
	            handlePriorityReceivedLogic(inspectionId, priority);
	            break;

	        default:
	            client.newFailCommand(job.getKey())
	                  .retries(0)
	                  .errorMessage("Unsupported eventType: " + eventType)
	                  .send().join();
	            return;
	    }

	    // Optionally update process variables
	    client.newCompleteCommand(job.getKey())
	          .variables(Map.of("messageHandled", true))
	          .send().join();
	}

	private void handleSendToReinspectionLogic(String inspectionId) {
	    logger.info("Handled send to reinspection for inspection ID: {}", inspectionId);
	    // Your logic to update inspection status or notify
	}

	private void handlePriorityReceivedLogic(String inspectionId, String priority) {
	    logger.info("Handled priority received for inspection ID: {}, Priority: {}", inspectionId, priority);
	    // Your logic to update priority or escalate
	}


}
