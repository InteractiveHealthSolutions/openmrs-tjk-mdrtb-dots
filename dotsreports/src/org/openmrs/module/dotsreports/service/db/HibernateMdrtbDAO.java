package org.openmrs.module.dotsreports.service.db;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Location;
import org.openmrs.api.db.DAOException;

public class HibernateMdrtbDAO implements MdrtbDAO {

    protected static final Log log = LogFactory.getLog(HibernateMdrtbDAO.class);
    
    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;
    
    
    public void setSessionFactory(SessionFactory sessionFactory) { 
        this.sessionFactory = sessionFactory;
    }
         
    /**
	 * @see MdrtbDAO#getLocationsWithAnyProgramEnrollments()
	 */
    @SuppressWarnings("unchecked")
	public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException {
		String query = "select distinct location from PatientProgram where voided = false";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}
    
    /**
	 * @see MdrtbDAO#getAllRayonsTJK()
	 */
    @SuppressWarnings("unchecked")
	public List<String> getAllRayonsTJK() throws DAOException {
		String query = "select distinct name from address_hierarchy_entry where level_id=3";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}
    
}
