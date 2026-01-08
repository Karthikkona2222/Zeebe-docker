package com.aaseya.AIS.utility;

import com.aaseya.AIS.service.TaskListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.aaseya.AIS.OMSConstant.CAMUNDA_DOCUMENT_UPLOAD;

public class CommonUtils {

    private final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public ArrayNode uploadFiletoGcp(File file) throws JsonProcessingException {

        String fileId = "";
        TaskListService taskListService = new TaskListService();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CAMUNDA_DOCUMENT_UPLOAD);

            // Add the Bearer Token to the Authorization header
            httpPost.setHeader("Authorization", "Bearer " + taskListService.getTasklistToken());

            // Create the MultipartEntityBuilder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            String boundaryNumber = UUID.randomUUID().toString();
            String contentType = "multipart/form-data; boundary="+boundaryNumber;
            // builder.addBinaryBody("file", file, ContentType.create("application/pdf"), file.getName());
            builder.addBinaryBody("file", file, ContentType.create(contentType), file.getName());

            // Build the HttpEntity from the builder
            HttpEntity multipartEntity = builder.build();
            httpPost.setEntity(multipartEntity);

            // Execute the request and get the response
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println("Response Status: " + response.getStatusLine());
                logger.info("Response Status of is {}, Response Entity is {}", response.getStatusLine(), response.getEntity().toString());
                fileId = EntityUtils.toString(response.getEntity());

            } catch (IOException e) {

                logger.info("IO Exception {}", e.fillInStackTrace());
                e.printStackTrace();
            }
        }catch (Exception ef){
            logger.info("Exception {}", ef.fillInStackTrace());
            ef.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(fileId);
        ArrayNode arrayNode = null;
        if (node.isArray()) {
            arrayNode = (ArrayNode) node;
        } else if (node.isObject()) {
            arrayNode = mapper.createArrayNode();
            arrayNode.add(node);
        } else {
            // primitive or text -> wrap as single-element array
            arrayNode = mapper.createArrayNode();
            arrayNode.add(node);
        }

        return arrayNode;
    }

    public ArrayNode uploadFiletoGcp(byte[] file, String fileName) throws JsonProcessingException {

        String fileId = "";
        TaskListService taskListService = new TaskListService();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CAMUNDA_DOCUMENT_UPLOAD);

            // Add the Bearer Token to the Authorization header
            httpPost.setHeader("Authorization", "Bearer " + taskListService.getTasklistToken());

            // Create the MultipartEntityBuilder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            String boundaryNumber = UUID.randomUUID().toString();
            String contentType = "multipart/form-data; boundary="+boundaryNumber;
            //builder.addBinaryBody("file", file, ContentType.create("application/pdf"), file.getName());
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM,fileName);


            // Build the HttpEntity from the builder
            HttpEntity multipartEntity = builder.build();
            httpPost.setEntity(multipartEntity);

            // Execute the request and get the response
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println("Response Status: " + response.getStatusLine());
                logger.info("Response Status of is {}, Response Entity is {}", response.getStatusLine(), response.getEntity().toString());
                fileId = EntityUtils.toString(response.getEntity());

            } catch (IOException e) {

                logger.info("IO Exception {}", e.fillInStackTrace());
                e.printStackTrace();
            }
        }catch (Exception ef){
            logger.info("Exception {}", ef.fillInStackTrace());
            ef.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(fileId);
        ArrayNode arrayNode = null;
        if (node.isArray()) {
            arrayNode = (ArrayNode) node;
        } else if (node.isObject()) {
            arrayNode = mapper.createArrayNode();
            arrayNode.add(node);
        } else {
            // primitive or text -> wrap as single-element array
            arrayNode = mapper.createArrayNode();
            arrayNode.add(node);
        }

        return arrayNode;
    }



}
