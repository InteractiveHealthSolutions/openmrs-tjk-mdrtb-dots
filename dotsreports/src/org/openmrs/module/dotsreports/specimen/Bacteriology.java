package org.openmrs.module.dotsreports.specimen;

import org.openmrs.Concept;


public interface Bacteriology extends Test {

	/**
	 * Data points this interface provides access to:
	 * 
	 * result: the Concept that represents the result of the smear
	 */
	
	public Concept getResult();
	public void setResult(Concept result);
}
