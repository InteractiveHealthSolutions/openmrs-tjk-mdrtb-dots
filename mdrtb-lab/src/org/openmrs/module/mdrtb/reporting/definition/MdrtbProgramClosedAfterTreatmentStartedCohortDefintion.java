package org.openmrs.module.mdrtb.reporting.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("mdrtb.reporting.MdrtbProgramClosedAfterTreatmentStartedCohortDefintion")
public class MdrtbProgramClosedAfterTreatmentStartedCohortDefintion extends MdrtbTreatmentStartedCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// if defined, the program must have closed within x months from treatment start
	@ConfigurationProperty
	private Integer monthsFromTreatmentStart;
		
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public MdrtbProgramClosedAfterTreatmentStartedCohortDefintion() {
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

	public void setMonthsFromTreatmentStart(Integer monthsFromTreatmentStart) {
	    this.monthsFromTreatmentStart = monthsFromTreatmentStart;
    }

	public Integer getMonthsFromTreatmentStart() {
	    return monthsFromTreatmentStart;
    }
	
}
