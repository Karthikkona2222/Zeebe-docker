package com.aaseya.AIS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.zeebe.spring.client.annotation.Deployment;
@SpringBootApplication
//@Deployment(resources = "/camunda/*.bpmn")
public class AisApplication {

	public static void main(String [] args) {
		SpringApplication.run(AisApplication.class, args);
	}

}
