package org.openmrs.module.dotsreports.reporting.definition;

import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.DotsBacBaselineTJKResultCohortDefinition")
public class CompletedDotsProgramEnrolledDuringTJKCohortDefinition extends ProgramEnrollmentCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
		//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public CompletedDotsProgramEnrolledDuringTJKCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}

	
}