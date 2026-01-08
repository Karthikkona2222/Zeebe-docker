package com.aaseya.AIS.zeebe.worker;
 
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
 
@Component
public class StartApproval {
 
	@Autowired
	private ZeebeClient zeebeClient;
 
	@JobWorker(type = "start-approval")
	public Map<String, Object> startReview(final ActivatedJob job) {
		Map<String, Object> map = job.getVariablesAsMap();
		String businessKey = map.get("AISBusinessKey").toString();
		zeebeClient.newPublishMessageCommand().messageName("StartApprovalMessage").correlationKey(businessKey).send()
				.join();
		return map;
	}
 
}

