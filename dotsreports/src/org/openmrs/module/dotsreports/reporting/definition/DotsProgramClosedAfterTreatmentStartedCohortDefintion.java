package org.openmrs.module.dotsreports.reporting.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.DotsProgramClosedAfterTreatmentStartedCohortDefintion")
public class DotsProgramClosedAfterTreatmentStartedCohortDefintion extends DotsTreatmentStartedCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// if defined, the program must have closed within x months from treatment start
	@ConfigurationProperty
	private Integer monthsFromTreatmentStart;
		
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public DotsProgramClosedAfterTreatmentStartedCohortDefintion() {
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
