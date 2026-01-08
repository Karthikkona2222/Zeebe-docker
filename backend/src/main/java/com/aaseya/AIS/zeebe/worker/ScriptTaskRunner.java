package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.utility.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.aaseya.AIS.utility.Constants.CONST_BILL_INFO;
import static com.aaseya.AIS.utility.Constants.CONST_DIS_SUMM;


@Component
public class ScriptTaskRunner {


    private final Logger logger = LoggerFactory.getLogger(ScriptTaskRunner.class);


    @Autowired
    private ZeebeClient zeebeClient;
    @JobWorker(type = "script-runner-parsing-response")
    public void getScriptRunnerParsingResponse(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        Map<String, Object> map = job.getVariablesAsMap();
        logger.info("Read the job variables. {}",map.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode var = objectMapper.valueToTree(map.get("dischargeSummaryDetails"));
         JsonNode nodeArray = var.get("body").get("choices");
         if(nodeArray.size()>0){
             String content = nodeArray.get(0).get("message").get("content").toString();
             logger.info("Read the content for dischargeSummaryDetails. {}", content);
             client.newCompleteCommand(job.getKey())
                     .variables(Map.of("dischargeSummary", content))
                     .send()
                     .join();
             logger.info("Set the variable value for dischargeSummaryDetails.");
         }
    }

    @JobWorker(type = "script-runner-parsing-uploadedfiles")
    public void getScriptRunnerParsingUploadedFiles(final JobClient client, final ActivatedJob job) throws IOException {
        Map<String, Object> map = job.getVariablesAsMap();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode var = objectMapper.valueToTree(map.get("files"));
        String variableName = "";
        byte[] content = null;
        for (JsonNode file : var) {
            // verify whether the file has bill item or discharge summary or prescription.
            // documentDischargeSummary ,  documentDischargeBill , documentDoctorPrescription.
            // Read the File information content.
            content = null;
            if (file != null && !file.isNull()) {
                Path filePath = Paths.get(file.asText());
                String fileName = filePath.getFileName().toString();
                content = Files.readAllBytes(filePath);


                if(fileName.toLowerCase().contains("bill")){
                    variableName =  "documentDischargeBill";
                    continue;
                } else if (fileName.toLowerCase().contains("prescription")) {
                    variableName =  "documentDoctorPrescription";
                    continue;
                }else{
                    variableName =  "documentDischargeSummary";
                }

                Map<String, Object> document = new HashMap<>();
                document.put("name", fileName);
                document.put("content", Base64.getEncoder().encodeToString(content));
                document.put("mimeType", "application/pdf");
                logger.info("Filename of the variables values {} & Variable name is {}",fileName,variableName);
                map.put("document", document);
//                client.newCompleteCommand(job.getKey())
//                        .variables(Map.of(variableName, document))
//                        .send()
//                        .join();

            }
            }
        logger.info("Contant of the variables values {}",content.toString());
        zeebeClient.newSetVariablesCommand(job.getProcessInstanceKey())
                .variables(map).send().join();
        logger.info("map details",map.keySet().toString());
//        client.newCompleteCommand(job.getKey())
//                .variables(Map.of(variableName, map))
//                .send()
//                .join();

    }

    @JobWorker(type = "direct-upload-gcp")
    public void directS3Upload(final JobClient client, final ActivatedJob job) throws IOException {
        //{
        //  "informationForThePolicy": [
        //    {
        //      "billSummary": "C:/Users/jain.sanjay/Downloads/Discharge Summary Docs/Discharge Summary Docs/Type 1/heart_attack_hospital_bill.pdf",
        //      "dischargeSummary": "C:/Users/jain.sanjay/Downloads/Discharge Summary Docs/Discharge Summary Docs/Type 1/heart_attack_discharge_summary.pdf",
        //      "doctorPrescription": "C:/Users/jain.sanjay/Downloads/Discharge Summary Docs/Discharge Summary Docs/Type 1/heart_attack_prescription.pdf"
        //    }
        //  ]
        //}

        // Get the InformationForThePolicy node and Get all the files attached.

        Map<String, Object> variableMap = job.getVariablesAsMap();
        Map<String, Object> files = (Map<String, Object>) variableMap.get("informationForThePolicy");

        String binaryDataDoc = null;
        String documentName = null;
        ArrayNode billSummaryNode = null,dischargeSummaryNode=null,doctorPrescriptionNode=null;
        Map<String, Object> variableNodeMap = new HashMap<>();

        for (Map.Entry<String,Object> file : files.entrySet()) {
            // Read each file path
            String fileType = file.getKey();
            if(fileType.equalsIgnoreCase(CONST_BILL_INFO)){
                binaryDataDoc = ((Map<String,String>)(file.getValue())).get("document").toString();
                documentName =  ((Map<String,String>)(file.getValue())).get("documentType").toString();
                billSummaryNode = uploadFilesInformation(binaryDataDoc,documentName);
                variableNodeMap.put("billSummaryNode", billSummaryNode);

            }else if(fileType.equalsIgnoreCase(CONST_DIS_SUMM)){
                binaryDataDoc = ((Map<String,String>)(file.getValue())).get("document").toString();
                documentName =  ((Map<String,String>)(file.getValue())).get("documentType").toString();
                dischargeSummaryNode = uploadFilesInformation(binaryDataDoc,documentName);
                variableNodeMap.put("dischargeSummaryNode", dischargeSummaryNode);
            }else{
                binaryDataDoc = ((Map<String,String>)(file.getValue())).get("document").toString();
                documentName =  ((Map<String,String>)(file.getValue())).get("documentType").toString();
                doctorPrescriptionNode = uploadFilesInformation(binaryDataDoc,documentName);
                variableNodeMap.put("doctorPrescriptionNode", doctorPrescriptionNode);
            }

            // You can now set them directly in Zeebe variables
        }
        zeebeClient.newSetVariablesCommand(job.getProcessInstanceKey())
                .variables(variableNodeMap)
                .send()
                .join();

    }

    private ArrayNode uploadFilesInformation(String fileBase64,String fileName) throws JsonProcessingException {

        CommonUtils commonUtils = new CommonUtils();
        byte[] fileBase64code = Base64.getDecoder().decode(fileBase64);
        ArrayNode arrayNode = commonUtils.uploadFiletoGcp(fileBase64code,fileName);

        if(arrayNode.size()<1){
            logger.error("File couldn't be uploaded into the GCP {}", fileName);
        }else{
            logger.info("File uploaded into the GCP {}", fileName);
            logger.info("File Response received for the file path {} is {}",fileName,arrayNode.toPrettyString());
        }

        return arrayNode;
    }


    @JobWorker(type = "uploadDocumentsAndVerify")
    public void uploadDocumentAndVerify(final JobClient client, final ActivatedJob job) throws IOException {

        // Get the InformationForThePolicy node and Get all the files attached.
        Map<String, Object> variableMap = job.getVariablesAsMap();
        List<Map<String,Object>> files = (List<Map<String, Object>>) variableMap.get("informationForTheEntity");

        Map<String, Object> variableNodeMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode fileUploaded = mapper.createArrayNode();

            for (Map<String, Object> fileMap : files) {
                String binaryDataDoc = fileMap.get("document").toString();
                String documentName = fileMap.get("documentType").toString();

                ArrayNode uploadedResult = null;
                try {
                    uploadedResult = uploadFilesInformation(binaryDataDoc, documentName);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                fileUploaded.addAll(uploadedResult);
            }

            // set as documents array and send this.

        variableNodeMap.put("documents",fileUploaded);
        zeebeClient.newSetVariablesCommand(job.getProcessInstanceKey())
                .variables(variableNodeMap)
                .send()
                .join();

    }


}
