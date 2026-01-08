package com.aaseya.AIS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.dao.UpdateIsActiveDAO;
import com.aaseya.AIS.dto.UpdateIsActiveDTO;

import jakarta.transaction.Transactional;
@Service
public class UpdateIsActiveService {
	
	 @Autowired
	    private UpdateIsActiveDAO updateIsActiveDAO;
	 @Transactional
	    public void updateStatus(UpdateIsActiveDTO request) throws Exception {
	        
	        updateIsActiveDAO.updateIsActive(request.getInputType(), request.getAction(),request.getIds());
	    }

}
