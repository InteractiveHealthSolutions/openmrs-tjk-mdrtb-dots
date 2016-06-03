package org.openmrs.module.dotsreports.specimen;

import java.util.List;
import java.util.Map;

import org.openmrs.Concept;

/**
 * Interface that defines how to interaction with a dst
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the dst data in obsgroups
 */
public interface Dst extends Test {
	/**
	 * Data points this interface provides access to:
	 * 
	 * method: the Concept that represents the method used when performing the test
	 * direct: whether this was a direct or indirect test
	 * organism: the type of organism present
	 * coloniesInControl: the colonies in the control
	 * 
	 * results: returns a set of all the dst results for this test	
	 * resultsMap: returns a set of all the dst results, mapped by concept_id + "|" + concentration
	 * 
	 */
	
	public Concept getMethod();
	public void setMethod(Concept method);
	
	public Boolean getDirect();
	public void setDirect(Boolean direct);
	
	public Concept getOrganismType();
	public void setOrganismType(Concept organismType);
	
	public String getOrganismTypeNonCoded();
	public void setOrganismTypeNonCoded(String organismType);
	
	public Integer getColoniesInControl();
	public void setColoniesInControl(Integer coloniesInControl);

	public List<DstResult> getResults();
	public Map<Integer,List<DstResult>> getResultsMap();
	public DstResult addResult();
	public void removeResult(DstResult result);
}

