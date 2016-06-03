package org.openmrs.module.dotsreports.service.db;

import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.db.DAOException;

public interface MdrtbDAO {

    /**
     * @return all Locations which have non-voided Patient Programs associated with them
     */
    public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException;
    public List<String> getAllRayonsTJK();
  
    
}
