package com.aaseya.AIS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aaseya.AIS.dto.AISResponseDTO;
import com.aaseya.AIS.dto.EntityDetailsDTO;
import com.aaseya.AIS.service.NewEntityService;

@CrossOrigin("*")
@RestController
public class EntityController {
	
	@Autowired
	private NewEntityService entityService;

	@PostMapping("/addUpdateEntity")
	public AISResponseDTO addUpdateEntity(@RequestBody EntityDetailsDTO entityDetailsDTO) {
		AISResponseDTO responseDTO = new AISResponseDTO();
		try {
			boolean res = entityService.addUpdateEntity(entityDetailsDTO);
			if(res) {
				responseDTO.setStatus("Success");
				responseDTO.setMessage(entityDetailsDTO.getName() + " has been successfully added.");
			} else {
				responseDTO.setStatus("Failure");
				responseDTO.setMessage("Failed to add/update entity.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setMessage("Failed to add/update entity : " + e.getMessage());
		}
		return responseDTO;
	}
}
