package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;


public interface Microscopy extends Bacteriology {


	/**
	 * Data points this interface provides access to:
	 * 
	 * colonies: the # of colonies present
	 * method: the Concept that represents the method used when performing the test
	 * organism: the type of organism present
	 * daysToPosiivity: the number of days for a culture to show a positive result
	 * 
	 */
	
	public Concept getSampleApperence();
	public void setSampleApperence(Concept Apperence);
	
	public Concept getSampleResult();
	public void setSampleResult(Concept Result);
	
	public void setLocation(Location location);
	public void setDateCollected(Date dateCollected);
	
}
