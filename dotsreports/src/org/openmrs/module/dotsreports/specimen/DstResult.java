package org.openmrs.module.dotsreports.specimen;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a DST result
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the smear data in obsgroups
 */
public interface DstResult extends Comparable<DstResult>{

	/**
	 * Data points this interface provides get/set access to:
	 * 
	 * drug: the drug this test refers to
	 * concentration: the concentration of the drug
	 * colonies: the colonies present (after testing?)
	 * result: coded result of the test
	 * 
	 * Data points this interface provides get access to:
	 * test: the actual object that stores the data this interface is providing access to (i.e., in our current implementation, the dst result obsgroup)
	 * id: the id used to reference the test (i.e., in our current implementation, the obs_id of the dst result obsgroup for this test)
	 * 
	 */
	
	public Object getDstResult();
	public String getId(); 
	
	public Concept getDrug();
	public void setDrug(Concept drug);
	
	public Double getConcentration();
	public void setConcentration(Double concentration);
	
	public Concept getResult();
	public void setResult(Concept result);
	
	public Integer getColonies();
	public void setColonies(Integer colonies);
	
}
