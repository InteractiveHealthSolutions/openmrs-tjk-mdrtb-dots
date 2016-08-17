package org.openmrs.module.labmodule.reporting.definition;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("dotsreports.reporting.DotsProgramLocationCohortDefinition")
public class LabProgramLocationCohortTJKDefinition extends BaseCohortDefinition {
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="district")
	private String location;

	@ConfigurationProperty(group="startDateGroup")
	private Date startDate;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date endDate;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public LabProgramLocationCohortTJKDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the fromDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
	    return location;
    }
	
	/**
	 * @return location the location to set
	 */
	public void setLocation(String location) {
	    this.location = location;
    }
}
