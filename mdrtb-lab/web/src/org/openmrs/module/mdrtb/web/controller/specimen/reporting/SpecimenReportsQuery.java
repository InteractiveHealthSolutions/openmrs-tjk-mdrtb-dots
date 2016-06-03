package org.openmrs.module.mdrtb.web.controller.specimen.reporting;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.api.context.Context;

/**
 * The backing object for the specimen reports query
 */
public class SpecimenReportsQuery {
	
	private Date startDateCollected;
		
	private Date endDateCollected;
		
	private Integer daysSinceSmear;
	
	private Integer daysSinceCulture;
		
	private Location lab;
		
	public SpecimenReportsQuery() {
	}

	public void setStartDateCollected(Date startDateCollected) {
		this.startDateCollected = startDateCollected;
	}

	public Date getStartDateCollected() {
		return startDateCollected;
	}
	
	public String getStartDateCollectedAsString() {
		return Context.getDateFormat().format(startDateCollected);
	}

	public void setEndDateCollected(Date endDateCollected) {
		this.endDateCollected = endDateCollected;
	}

	public Date getEndDateCollected() {
		return endDateCollected;
	}
	
	public String getEndDateCollectedAsString() {
		return Context.getDateFormat().format(endDateCollected);
	}

	public void setDaysSinceSmear(Integer daysSinceSmear) {
	    this.daysSinceSmear = daysSinceSmear;
    }

	public Integer getDaysSinceSmear() {
	    return daysSinceSmear;
    }

	public void setDaysSinceCulture(Integer daysSinceCulture) {
		this.daysSinceCulture = daysSinceCulture;
	}

	public Integer getDaysSinceCulture() {
		return daysSinceCulture;
    }

	public void setLab(Location lab) {
		this.lab = lab;
	}

	public Location getLab() {
		return lab;
	}	
}
