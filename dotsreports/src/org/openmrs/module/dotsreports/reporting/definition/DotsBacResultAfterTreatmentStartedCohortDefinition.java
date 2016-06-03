package org.openmrs.module.dotsreports.reporting.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.DotsBacResultAfterTreatmentStartedCohortDefinition")
public class DotsBacResultAfterTreatmentStartedCohortDefinition extends DotsTreatmentStartedCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
	@ConfigurationProperty
	private Integer fromTreatmentMonth;
	
	@ConfigurationProperty
	private Integer toTreatmentMonth;
	
	@ConfigurationProperty
	private Result overallResult;
	
	public enum Result {
    	POSITIVE, NEGATIVE, UNKNOWN;
    }
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public DotsBacResultAfterTreatmentStartedCohortDefinition() {
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
	
	
    public void setOverallResult(Result overallResult) {
	    this.overallResult = overallResult;
    }

	public Result getOverallResult() {
	    return overallResult;
    }

	public void setFromTreatmentMonth(Integer fromTreatmentMonth) {
	    this.fromTreatmentMonth = fromTreatmentMonth;
    }

	public Integer getFromTreatmentMonth() {
	    return fromTreatmentMonth;
    }

	public void setToTreatmentMonth(Integer toTreatmentMonth) {
	    this.toTreatmentMonth = toTreatmentMonth;
    }

	public Integer getToTreatmentMonth() {
	    return toTreatmentMonth;
    }
}
