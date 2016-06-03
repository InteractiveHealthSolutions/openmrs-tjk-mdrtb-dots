package org.openmrs.module.dotsreports.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a smear
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface Smear extends Bacteriology {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * bacilli: the bacilli count
	 * method: the Concept that represents the method used when performing the test
	 * 
	 */
	
	public Integer getBacilli();
	public void setBacilli(Integer bacilli);
	
	public Concept getMethod();
	public void setMethod(Concept method);
}
