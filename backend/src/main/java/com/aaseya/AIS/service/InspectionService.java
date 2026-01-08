package com.aaseya.AIS.service;
 
 
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.ZoneDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.ZoneUserDTO;
import com.aaseya.AIS.dto.UsersDTO;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.Model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class InspectionService {
 
    @Autowired
    private InspectionCaseDAO inspectionCaseDAO;
 
    @Autowired
    private EntityDAO newEntityDAO;
 
    @Autowired
    private ZoneDAO zoneDAO;
 
    @Autowired
    private UsersDAO usersDAO;
 
    @Transactional
    public ZoneUserDTO getZoneUserDetailsByInspectionId(long inspectionId) {
        InspectionCase inspectionCase = inspectionCaseDAO.getInspectionCaseByIdforZone(inspectionId);
        if (inspectionCase == null) {
            return null;
        }

        NewEntity entity = inspectionCase.getEntity();
        if (entity == null) {
            return null;
        }

        Zone zone = entity.getZones().stream().findFirst().get();
        if (zone == null) {
            return null;
        }

        List<Users> inspectors = usersDAO.getInspectorsByZoneId(zone.getZoneId());

        List<UsersDTO> userDTOs = inspectors.stream()
            .map(user -> new UsersDTO(
	                user.getUserID(),
	                user.getUserName(),
	               
	                user.getEmailID(),
	               
	                user.getPhoneNumber(),
	                user.getRole()))
               
            .collect(Collectors.toList());


        return new ZoneUserDTO(zone.getZoneId(), zone.getName(), zone.getDescription(), zone.getLocation(), zone.getCoordinates(), zone.getIsDefaultZone(), userDTOs);
    }
}
 