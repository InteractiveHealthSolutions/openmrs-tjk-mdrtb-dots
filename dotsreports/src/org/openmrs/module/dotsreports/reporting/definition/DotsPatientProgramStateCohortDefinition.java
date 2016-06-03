package org.openmrs.module.dotsreports.reporting.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("dotsreports.reporting.DotsPatientProgramStateCohortDefinition")
public class DotsPatientProgramStateCohortDefinition extends BaseCohortDefinition {
	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(group="workflow")
	private List<Concept> stateConcepts = new ArrayList<Concept>();

	@ConfigurationProperty(group="workflow")
	private Concept workflowConcept;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date startDate;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date endDate;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public DotsPatientProgramStateCohortDefinition() {
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

	/**
	 * @return the fromDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	

	public void setStateConcepts(List<Concept> stateConcepts) {
	    this.stateConcepts = stateConcepts;
    }

	public List<Concept> getStateConcepts() {
	    return stateConcepts;
    }

	public void setWorkflowConcept(Concept workflowConcept) {
	    this.workflowConcept = workflowConcept;
    }

	public Concept getWorkflowConcept() {
	    return workflowConcept;
    }

	public void addStateConcept(Concept stateConcept) {
		this.stateConcepts.add(stateConcept);
	}
}
