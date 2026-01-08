package com.aaseya.AIS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.ZoneDAO;
import com.aaseya.AIS.dto.ZoneDTO;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;

@Service
public class ZoneService {

	@Autowired
	private ZoneDAO zoneDAO;

//	@Transactional
	public void addZone(Zone zone) {
		zoneDAO.saveZone(zone);
	}

	@Transactional
	public void saveZone(Zone zone) {
		zoneDAO.saveZoneWithCriteria(zone);
	}

////////Get all zones for user registration and add zone///
	public List<ZoneDTO> getAllZones() {
		return zoneDAO.getAllZones1();
	}
}
