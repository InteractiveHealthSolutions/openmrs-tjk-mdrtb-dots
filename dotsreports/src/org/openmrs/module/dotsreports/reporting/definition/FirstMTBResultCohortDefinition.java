package org.openmrs.module.dotsreports.reporting.definition;

import java.util.Date;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


@Localized("dotsreports.reporting.FirstMTBResultCohortDefinition")
public class FirstMTBResultCohortDefinition extends BaseCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	// note that, by convention, the first month of treatment is referred to as "treatment month 0"
	// so, for instance, if you want to test Bac results during the first 5 months of treatment,
	// then fromTreatmentMonth would be set to 0, and toTreatmentMonth set to 4, because you
	// want to examine all results during months 0, 1, 2, 3, 4, & 5
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private Boolean isPositive;

	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public FirstMTBResultCohortDefinition() {
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
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getIsPositive() {
		return isPositive;
	}

	public void setIsPositive(Boolean isPositive) {
		this.isPositive = isPositive;
	}

	
}
