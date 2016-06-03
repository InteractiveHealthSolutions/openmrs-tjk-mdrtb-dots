package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Date;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("mdrtb.reporting.MdrtbPreviousProgramOutcomeCohortDefinition")
public class MdrtbPreviousProgramOutcomeCohortDefinition  extends MdrtbTreatmentStartedCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
	@ConfigurationProperty
	private Date fromDate;
	
	@ConfigurationProperty
	private Date toDate;
	
	@ConfigurationProperty
	private String outcome;
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public MdrtbPreviousProgramOutcomeCohortDefinition() {
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
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	
	
	
	

	
}
