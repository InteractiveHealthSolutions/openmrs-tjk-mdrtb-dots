package org.openmrs.module.mdrtb.patientchart;

import java.util.Date;


/**
 * A component of a record in a patient chart; represents one row in a patient record (which is in itself a row in the patient chart)
 */
public interface RecordComponent extends Comparable<RecordComponent>{

	/**
	 * Returns a string code representing type of this component
	 */
	public String getType();
	
	/**
	 * Gets the date associated with the component
	 */
	public Date getDate();
}
