package org.openmrs.module.labmodule.reporting.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.DotsProgramClosedAfterTreatmentStartedCohortDefintion")
public class LabProgramClosedAfterTreatmentStartedCohortDefintion extends LabTreatmentStartedCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// if defined, the program must have closed within x months from treatment start
	@ConfigurationProperty
	private Integer monthsFromTreatmentStart;
		
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public LabProgramClosedAfterTreatmentStartedCohortDefintion() {
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
