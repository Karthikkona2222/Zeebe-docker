package com.aaseya.AIS.zeebe.worker;

import java.time.LocalDate;

import java.util.HashMap;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;

import io.camunda.zeebe.client.api.worker.JobClient;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component

public class ScheduleFollowupWorker {

	@ZeebeWorker(type = "schedule-follow-up-SK")

	public void scheduleFollowUpInspection(final JobClient client, final ActivatedJob job) {

		Map<String, Object> vars = job.getVariablesAsMap();

		long processInstanceKey = job.getProcessInstanceKey();

		int riskScore = (int) vars.get("riskScore");

		// Simulate follow-up scheduling logic

		// String followUpId = "-" + processInstanceKey + "-" +
		// System.currentTimeMillis();

		// Save to DB (pseudo)

		// followUpService.createFollowUp(followUpId, inspectionId,
		// LocalDate.now().plusDays(7));

		Map<String, Object> result = new HashMap<>();

		result.put("followUpId", String.valueOf(processInstanceKey));

		result.put("status", "FOLLOW_UP_SCHEDULED");

		result.put("scheduleDate", LocalDate.now().plusDays(7).toString());

		client.newCompleteCommand(job.getKey())

				.variables(result)

				.send()

				.join();

	}

}
