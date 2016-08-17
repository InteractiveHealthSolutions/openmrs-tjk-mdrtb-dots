package org.openmrs.module.labmodule.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a smear
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface HAIN2 extends Test {
	
	/**
	 * Data points this interface provides access to:
	 * 
	 * bacilli: the bacilli count
	 * method: the Concept that represents the method used when performing the test
	 * 
	 */
	
	public Concept getResult();
	public void setResult(Concept mtbResult);
	
	public Concept getMoxResistance();
	public void setMoxResistance(Concept moxResistance);
	
	
	public Concept getCmResistance();
	public void setCmResistance(Concept cmResistance);
	
	public Concept getErResistance();
	public void setErResistance(Concept erResistance);
	

	public Concept getMtbBurden();
	public void setMtbBurden(Concept mtbBurden);
	
	
}
