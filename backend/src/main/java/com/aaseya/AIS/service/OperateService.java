package com.aaseya.AIS.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.aaseya.AIS.OMSConstant;
import com.aaseya.AIS.OperateConstant;
import com.aaseya.AIS.dto.AccessToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service // Add this annotation
public class OperateService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public String searchProcessInstances(String parentId, String string) {
		String uri = OperateConstant.SEARCH_PROCESS_INSTANCES;

		ResponseEntity<String> response = null;
		logger.info("Complete Variables: " + string);

		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0);
			restTemplate.setRequestFactory(requestFactory);
			logger.info("Complete task URI::: " + uri);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Authorization Token from OperateConstant
			String AuthToken = "Bearer " + getOperateToken(); // Uses the method to retrieve the token
			headers.add("Authorization", AuthToken);

			// Constructing the request body
			ObjectMapper mapper = new ObjectMapper();
			String requestJson = "";
			Map<String, Object> requestBody = new HashMap<>();

			// Populate the "filter" part of the body
			Map<String, Object> filter = new HashMap<>();
			filter.put("parentKey", parentId);

			requestBody.put("filter", filter);

			// Convert the request body to JSON
			try {
				requestJson = mapper.writeValueAsString(requestBody);
				logger.info("JSON request: " + requestJson);
			} catch (Exception e) {
				logger.error("Error while converting request to JSON", e);
			}

			HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
			logger.info("Request body: " + entity.getBody());

			// Sending the request
			response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			logger.info("Response received");

			// Parse the response to extract the subprocessInstanceKey
			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				String responseBody = response.getBody();
				if (responseBody != null && !responseBody.isEmpty()) {
					// Parse JSON response
					Map<String, Object> responseMap = mapper.readValue(responseBody,
							new TypeReference<Map<String, Object>>() {
							});
					List<Map<String, Object>> items = (List<Map<String, Object>>) responseMap.get("items");
					if (items != null && !items.isEmpty()) {
						Map<String, Object> firstItem = items.get(0);
						String subprocessInstanceKey = String.valueOf(firstItem.get("key"));
						logger.info("Subprocess instance key: " + subprocessInstanceKey);
						return subprocessInstanceKey;
					} else {
						logger.info("No subprocess instances found.");
					}
				}
			} else {
				logger.error("Failed to retrieve subprocess instances. Status code: " + response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while sending request: " + e.getMessage());
		}

		return null;
	}

	// This method would retrieve the token based on the constants from
	// OperateConstant
	private String getOperateToken() {
		// You need to implement token retrieval logic based on the constants from
		// OperateConstant
		String tokenUrl = OperateConstant.TOKEN_URL;
		String clientId = OperateConstant.CLIENT_ID;
		String clientSecret = OperateConstant.CLIENT_SECRET;
		String grantType = OperateConstant.GRANT_TYPE;
		String audience = OperateConstant.AUDIENCE;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("grant_type", grantType);
		body.add("audience", audience);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);
		Map<String, Object> responseBody = response.getBody();

		return responseBody != null ? (String) responseBody.get("access_token") : null;
	}

	public String getProcessDefinitionKeyByInstanceKey(long instanceKey) {
		String uri = OperateConstant.GET_PROCESS_INSTANCE_BY_KEY + "/" + instanceKey;

		ResponseEntity<String> response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Authorization Token from OperateConstant
			String authToken = "Bearer " + getOperateToken();
			headers.add("Authorization", authToken);

			HttpEntity<String> entity = new HttpEntity<>(headers);
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				String responseBody = response.getBody();
				if (responseBody != null && !responseBody.isEmpty()) {
					// Parse JSON response
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> responseMap = mapper.readValue(responseBody,
							new TypeReference<Map<String, Object>>() {
							});
					Object processDefinitionKey = responseMap.get("processDefinitionKey");
					return processDefinitionKey != null ? processDefinitionKey.toString() : null;
				}
			} else {
				logger.error("Failed to retrieve process instance. Status code: " + response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while sending request: " + e.getMessage());
		}

		return null;
	}

	// In OperateService
	public Map<String, Object> getDischargeSummary(long processInstanceKey) {
		String variableJson = getVariableByName(processInstanceKey, "DischargeSummary"); // value is stringified JSON in
																							// Operate [1]
		if (variableJson == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			// DischargeSummary is expected to be a JSON object in string form; parse into
			// Map
			return mapper.readValue(variableJson, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			logger.error("Failed to parse DischargeSummary JSON", e);
			return null;
		}
	}

	public Map<String, Object> getDischargeSummaryWithPolling(long processInstanceKey, Duration timeout,
			Duration interval) {
		long deadline = System.currentTimeMillis() + timeout.toMillis();
		Map<String, Object> summary;
		do {
			summary = getDischargeSummary(processInstanceKey); // calls Operate /v1/variables/search internally [15]
			if (summary != null)
				return summary;
			try {
				Thread.sleep(interval.toMillis());
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		} while (System.currentTimeMillis() < deadline);
		return null; // still not indexed in Operate within timeout [1]
	}

	public String getVariableByName(long processInstanceKey, String variableName) {
		String uri = OperateConstant.GET_VARIABLES_BY_INSTANCE_ID; // .../v1/variables/search [1]
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String authToken = "Bearer " + getOperateToken();
			headers.add("Authorization", authToken);

			// Request body per Operate API: filter by processInstanceKey and name
			// Note: in Operate SaaS, processInstanceKey is called "processInstanceKey" and
			// filter.name is the variable name; response items[] with fields
			// name,value,processInstanceKey [1][11]
			Map<String, Object> filter = new HashMap<>();
			filter.put("processInstanceKey", processInstanceKey);
			filter.put("name", variableName);

			Map<String, Object> req = new HashMap<>();
			req.put("filter", filter);
			req.put("size", 1);

			ObjectMapper mapper = new ObjectMapper();
			String requestJson = mapper.writeValueAsString(req);
			HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Map<String, Object> resp = mapper.readValue(response.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				List<Map<String, Object>> items = (List<Map<String, Object>>) resp.get("items");
				if (items != null && !items.isEmpty()) {
					Object value = items.get(0).get("value"); // Operate returns the variable value as a string [1]
					return value != null ? value.toString() : null;
				}
			} else {
				logger.error("Operate variables search failed: " + response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception fetching variable from Operate: " + e.getMessage(), e);
		}
		return null;
	}
	
	public Integer getRiskScore(long processInstanceKey) {
	    try {
	        String value = getVariableByName(processInstanceKey, "riskScore");

	        if (value == null) {
	            logger.warn("riskScore variable not found for instance {}", processInstanceKey);
	            return null;
	        }

	        // Remove quotes if any
	        value = value.replace("\"", "");

	        return Integer.parseInt(value);

	    } catch (Exception e) {
	        logger.error("riskScore is not a valid number", e);
	        return null;
	    }
	}

	
	public List<Map<String, Object>> getAllVariables(long processInstanceKey) {

	    String uri = OperateConstant.SEARCH_VARIABLES;
	    RestTemplate restTemplate = new RestTemplate();
	    ObjectMapper mapper = new ObjectMapper();

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        headers.add("Authorization", "Bearer " + getOperateToken());

	        Map<String, Object> req = new HashMap<>();
	        req.put("filter", Map.of("processInstanceKey", processInstanceKey));
	        req.put("size", 100);

	        String requestJson = mapper.writeValueAsString(req);

	        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

	        ResponseEntity<String> response =
	                restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

	        if (response.getStatusCode().is2xxSuccessful()) {

	            Map<String, Object> res =
	                    mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});

	            return (List<Map<String, Object>>) res.get("items");
	        }

	    } catch (Exception e) {
	        logger.error("Error while searching variables", e);
	    }

	    return null;
	}
	
	// for storing the inspection details in InspectionCase table
		public Map<String, Object> getProcessInstanceDetails(long instanceKey) {
		    String uri = OperateConstant.GET_PROCESS_INSTANCE_BY_KEY + "/" + instanceKey;

		    try {
		        RestTemplate restTemplate = new RestTemplate();

		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.APPLICATION_JSON);

		        String authToken = "Bearer " + getOperateToken();
		        headers.add("Authorization", authToken);

		        HttpEntity<String> entity = new HttpEntity<>(headers);
		        ResponseEntity<String> response =
		                restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

		        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
		            ObjectMapper mapper = new ObjectMapper();
		            return mapper.readValue(
		                    response.getBody(),
		                    new TypeReference<Map<String, Object>>() {});
		        }
		    } catch (Exception e) {
		        logger.error("Failed to fetch process instance details from Operate", e);
		    }

		    return Map.of(); // empty map on error
		}
		


}
