package com.aaseya.AIS.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.Model.SubSegment;
import com.aaseya.AIS.dao.SegmentDAO;
import com.aaseya.AIS.dto.SegmentDTO;

import jakarta.transaction.Transactional;

@Service
public class SegmentService {

	@Autowired
	private SegmentDAO segmentDAO;
	
	@Transactional
	public void addSegment(SegmentDTO segmentDTO) {
		Segment segment = new Segment();
		segment.setSegment_name(segmentDTO.getName());
		segment.setDescription(segmentDTO.getDescription());
		segment.setIsActive("true");
		
		List<SubSegment> subSegment = segmentDAO.getSubSegmentByName(segmentDTO.getSubSegment());
//		subSegment = subSegment.stream().peek(subSeg -> {subSeg.setSegment(segment);
//		}).collect(Collectors.toList());//segmentDAO.updateSubSegment(subSeg);
//		System.out.println(subSegment.toString());		
		segment.setSubSegment(subSegment);
		System.out.println(segment);
		segmentDAO.saveSegment(segment);
	}

	public void addSubSegment(SubSegment subSegment) {
		segmentDAO.saveSubSegment(subSegment);
	}

}
