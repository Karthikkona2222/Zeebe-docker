package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.aaseya.AIS.dto.AccessToken;

import com.aaseya.AIS.dto.TaskVariables;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskListService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

//  @Value("camunda.client.id")
//  private String clientId;
//  
//  @Value("camunda.client.id")
//  private String clientSecret;
//  
//  @Value("camunda.grant.type")
//  private String grantType;
//  
//  @Value("camunda.keycloak.id")
//  private String keycloakId;
//  
//  @Value("camunda.keycloak.url")
//  private String keycloakUrl;

	public String getTasklistToken() {
		String token = "";

		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			// Creating the form data to send in the POST request
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("client_secret", OMSConstant.CLIENT_SECRET);
			map.add("client_id", OMSConstant.CLIENT_ID);
			map.add("grant_type", OMSConstant.GRANT_TYPE);
			map.add("audience", OMSConstant.AUDIENCE); // Added audience here

			// Creating the HTTP entity with headers and form data
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

			// Sending the request to get the access token
			ResponseEntity<AccessToken> response = restTemplate.exchange(OMSConstant.TOKEN_URL, HttpMethod.POST,
					request, AccessToken.class);

			// Extract the access token from the response
			token = response.getBody().getAccess_token();
			System.out.println("token: " + token);
		} catch (Exception eek) {
			logger.info("** Exception: " + eek.getMessage());
		}

		return token;
	}

	public String getActiveTaskID(String processID) {
		String taskID = "";
		RestTemplate restTemplate = new RestTemplate();

		try {
			// Set request factory with a custom timeout
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0); // Set your preferred timeout
			restTemplate.setRequestFactory(requestFactory);

			// Set up headers including authorization
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String authToken = "Bearer " + getTasklistToken(); // Retrieve task list token
			headers.add("Authorization", authToken);

			// Prepare JSON request body
			String requestJson = "{\"taskState\":\"CREATED\",\"processInstanceKey\":\"" + processID + "\"}";
			HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

			// Send the POST request
			ResponseEntity<TaskVariables[]> resEntity = restTemplate.exchange(OMSConstant.TASKS_SEARCH_URL,
					HttpMethod.POST, entity, TaskVariables[].class);

			// Extract response body
			TaskVariables[] tasks = resEntity.getBody();

			// Return the taskID of the first matching task
			if (tasks != null && tasks.length > 0) {
				taskID = tasks[0].getId(); // Get taskID of the first task
			}

		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage());
		}

		return taskID;
	}

	public String CompleteTaskByID(String id, Map<String, Object> variables) {
	    String uri = OMSConstant.BASE_TASK_URL + id + OMSConstant.COMPLETE_URL;
	    ResponseEntity<String> response = null;
	    logger.info("Complete Variables: " + variables);

	    RestTemplate restTemplate = new RestTemplate();
	    try {
	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	        requestFactory.setConnectTimeout(0);
	        restTemplate.setRequestFactory(requestFactory);
	        
	        logger.info("Complete task URI::: " + uri);
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        String authToken = "Bearer " + getTasklistToken();
	        headers.add("Authorization", authToken);

	        String requestJson;
	        if (variables.containsKey("reviewStatus") && variables.containsKey("reinspection")) {
	            requestJson = "{\"variables\": [{\"name\": \"reviewStatus\",\"value\": false},{\"name\": \"reinspection\",\"value\": true}]}";
	        } else if (variables.containsKey("reviewStatus")) {
	            requestJson = "{\"variables\": [{\"name\": \"reviewStatus\",\"value\": true}]}";
	        } else if (variables.containsKey("approved") && variables.containsKey("reinspection")) {
	            requestJson = "{\"variables\": [{\"name\": \"approved\",\"value\": false},{\"name\": \"reinspection\",\"value\": true}]}";
	        } else if (variables.containsKey("approved")) {
	            requestJson = "{\"variables\": [{\"name\": \"approved\",\"value\": true}]}";
	        } else {
	            requestJson = "{\"variables\": [{\"name\": \"checklistFilled\",\"value\": true}]}";
	        }

	        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
	        logger.info("Request Body: " + entity.getBody());

	        response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, String.class);
	        logger.info("Request completed successfully");

	    } catch (Exception e) {
	        logger.error("Exception occurred while completing task: " + e.getMessage(), e);
	        return "Error: " + e.getMessage();
	    }

	    return response != null ? response.getBody() : "Error: No response received from server";
	}

	public ArrayList<TaskVariables> GetOpenOrders(String process) {

		if (process.equals("")) {
			process = "Order";
		}

		ResponseEntity<TaskVariables[]> Resentity = null;
		ArrayList<TaskVariables> Task1 = new ArrayList<TaskVariables>();

		RestTemplate restTemplate = new RestTemplate();
		try {

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0);
			// requestFactory.setReadTimeout(0);
			restTemplate.setRequestFactory(requestFactory);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String AuthToken = "Bearer " + getTasklistToken();
			headers.add("Authorization", AuthToken);

			String requestJson = "{\"taskState\":\"CREATED\",\"includeVariables\": [\r\n" + "    {\r\n"
					+ "      \"name\": \"firstName\"\r\n" + "      },\r\n" + "       {\r\n"
					+ "      \"name\": \"orderID\"\r\n" + "      },\r\n" + "      {\r\n"
					+ "        \"name\":\"status\"\r\n" + "      }]}";

			HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
			Resentity = restTemplate.exchange(OMSConstant.TASKS_SEARCH_URL, HttpMethod.POST, entity,
					TaskVariables[].class);
			TaskVariables[] task = Resentity.getBody();
			logger.info("Fetch all orders" + Resentity.getBody());
			for (int i = 0; i < task.length; i++) {

				// logger.info("result " + i + " " + task[i]); String taskid =
				task[i].getId();
				String processID = task[i].getProcessInstanceKey();

				if (task[i].getProcessName().contains(process)
						&& (task[i].getTaskState() == null || task[i].getTaskState().equalsIgnoreCase("CREATED"))) {

					Task1.add(task[i]);
					logger.info("Process ID" + processID + "State " + task[i].getState() + "  Task State"
							+ task[i].getTaskState());
				}
			}

		} catch (Exception e) {
			logger.info("exception in get tasks " + e.getMessage());
		}
		return Task1;
	}

	public String getKeyCloakToken() {
		String token = "";
		String uri = "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
		logger.info(" inside Patch ::" + uri);
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

			map.add("client_secret", OMSConstant.KEYCLOAK_CLIENT_SECRET);
			map.add("client_id", OMSConstant.KEYCLOAK_CLIENT_ID);
			map.add("grant_type", OMSConstant.GRANT_TYPE);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<AccessToken> response = restTemplate.exchange(uri, HttpMethod.POST, request,
					AccessToken.class);
			token = response.getBody().getAccess_token();

			logger.info(" accss toekn ::: " + response.getBody());
			logger.info("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
		} catch (Exception eek) {
			logger.info("** Exception: " + eek.getMessage());
		}

		return token;
	}

	public TaskVariables[] GetOpenOrders(String process, String userGroup) {

		if (process.equals("")) {
			process = "Order";
		}
		TaskVariables[] task = null;
		ResponseEntity<TaskVariables[]> Resentity = null;
		ArrayList<TaskVariables> Task1 = new ArrayList<TaskVariables>();

		RestTemplate restTemplate = new RestTemplate();

		try {

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			requestFactory.setConnectTimeout(0);
			// requestFactory.setReadTimeout(0);
			restTemplate.setRequestFactory(requestFactory);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String AuthToken = "Bearer " + getTasklistToken();
			headers.add("Authorization", AuthToken);

			String requestJson = "{\"state\":\"CREATED\",\"candidateGroup\":\"" + userGroup + "\"}";

			logger.info("----requestJson-->" + requestJson);

			HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

			Resentity = restTemplate.exchange(OMSConstant.TASKS_SEARCH_URL, HttpMethod.POST, entity,
					TaskVariables[].class);

			task = Resentity.getBody();

		} catch (Exception e) {
			logger.info("exception in get tasks " + e.getMessage());
		}
		return task;
	}

	public void assignTask(String ProcessID, String taskID, String userID) {

		RestTemplate restTemplate = new RestTemplate();
		try {

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0);
			// requestFactory.setReadTimeout(0);
			restTemplate.setRequestFactory(requestFactory);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String AuthToken = "Bearer " + getTasklistToken();
			headers.add("Authorization", AuthToken);

			String requestJson = "{\"assignee\":\"" + userID + "\" , \"allowOverrideAssignment\":true}";
			HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

			// ResponseEntity<TaskVariables> resEntity =
			restTemplate.exchange(OMSConstant.TASKS_ASSIGN_URL + taskID + "/assign", HttpMethod.PATCH, entity,
					TaskVariables.class);
			// System.out.println("assign task response: "+ resEntity.getBody());
//	      TaskVariables[] task = resEntity.getBody();
//	      for (int i = 0; i < task.length; i++) {
//	        if (task[i].getProcessInstanceKey().equals(ProcessID)
//	            && task[i].getTaskState().equals("CREATED")) {
//	          logger.info(" Match found at " + i + task[i].getName());
//	          TaskID += task[i].getId();
////	        		  + ",";
//	        }
//	      }

		} catch (Exception eek) {
			logger.info("** Exception: " + eek.getMessage());
		}

	}

	public TaskVariables getTaskById(String taskId) {
		RestTemplate restTemplate = new RestTemplate();
		TaskVariables taskVariables = null;

		try {
			// Set request factory with a custom timeout
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0); // Set your preferred timeout
			restTemplate.setRequestFactory(requestFactory);

			// Set up headers including authorization
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String authToken = "Bearer " + getTasklistToken(); // Retrieve task list token
			headers.add("Authorization", authToken);

			// Send the GET request
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<TaskVariables> resEntity = restTemplate.exchange(OMSConstant.GET_TASK_URL + taskId,
					HttpMethod.GET, entity, TaskVariables.class);

			// Extract response body
			taskVariables = resEntity.getBody();

		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage());
		}

		return taskVariables;
	}

//	public FormVariables getFormById(String formId, String processDefinitionKey, Integer version) {
//	    RestTemplate restTemplate = new RestTemplate();
//	    FormVariables formVariables = null;
//
//	    try {
//	        // Set request factory with a custom timeout
//	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//	        requestFactory.setConnectTimeout(0); // Set your preferred timeout
//	        restTemplate.setRequestFactory(requestFactory);
//
//	        // Set up headers including authorization
//	        HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(MediaType.APPLICATION_JSON);
//	        String authToken = "Bearer " + getTasklistToken(); // Retrieve task list token
//	        headers.add("Authorization", authToken);
//
//	        // Send the GET request
//	        HttpEntity<String> entity = new HttpEntity<>(headers);
//	        String url = OMSConstant.GET_FORM_URL + formId + "?processDefinitionKey=" + processDefinitionKey;
//	        if (version != null) {
//	            url += "&version=" + version;
//	        }
//	        ResponseEntity<FormVariables> resEntity = restTemplate.exchange(url, HttpMethod.GET, entity, FormVariables.class);
//
//	        // Extract response body
//	        formVariables = resEntity.getBody();
//
//	    } catch (Exception e) {
//	        logger.info("Exception: " + e.getMessage());
//	    }
//
//	    return formVariables;
//	}
	
	public String completeTaskByProcessInstanceKey(String processInstanceKey, List<Map<String, Object>> variablesList) {
	    RestTemplate restTemplate = new RestTemplate();
	    try {
	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	        requestFactory.setConnectTimeout(0);
	        restTemplate.setRequestFactory(requestFactory);

	        String searchUri = OMSConstant.TASKS_SEARCH_URL;
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        String authToken = "Bearer " + getTasklistToken();
	        headers.add("Authorization", authToken);

	        String searchRequestJson = String.format("{\"processInstanceKey\":\"%s\", \"taskState\":\"CREATED\"}", processInstanceKey);
	        HttpEntity<String> searchEntity = new HttpEntity<>(searchRequestJson, headers);

	        ResponseEntity<TaskVariables[]> searchResponse = restTemplate.exchange(searchUri, HttpMethod.POST, searchEntity, TaskVariables[].class);
	        TaskVariables[] tasks = searchResponse.getBody();

	        if (tasks == null || tasks.length == 0) {
	            logger.info("No active task found for processInstanceKey: " + processInstanceKey);
	            return "No active task found for processInstanceKey: " + processInstanceKey;
	        }

	        String taskId = tasks[0].getId();
	        logger.info("Found taskId: " + taskId + " for processInstanceKey: " + processInstanceKey);

	        ObjectMapper mapper = new ObjectMapper();
	        String completeRequestJson;

	        if (variablesList == null || variablesList.isEmpty()) {
	            // Send empty variables array if none provided
	            completeRequestJson = "{\"variables\":[]}";
	        } else {
	            Map<String, Object> payload = Map.of("variables", variablesList);
	            completeRequestJson = mapper.writeValueAsString(payload);
	        }

	        String completeUri = OMSConstant.BASE_TASK_URL + taskId + OMSConstant.COMPLETE_URL;
	        HttpEntity<String> completeEntity = new HttpEntity<>(completeRequestJson, headers);

	        ResponseEntity<String> completeResponse = restTemplate.exchange(completeUri, HttpMethod.PATCH, completeEntity, String.class);

	        logger.info("Task completed successfully for taskId: " + taskId);
	        return completeResponse.getBody();

	    } catch (Exception e) {
	        logger.error("Exception while completing task by processInstanceKey: " + e.getMessage(), e);
	        return "Error: " + e.getMessage();
	    }
	}

}
