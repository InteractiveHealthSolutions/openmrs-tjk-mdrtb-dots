package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.reporting.MdrtbQueryService;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={MdrtbAfterTreatmentStartedCohortDefinition.class},order=20)
public class MdrtbAfterTreatmentStartedCohortDefinitionEvaluator extends MdrtbTreatmentStartedCohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public MdrtbAfterTreatmentStartedCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	// Must be resistant to INH
		/**Concept inh = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID); 
		c = MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, inh);
    	
    	// Must be resistant to RIF
    	Concept rif = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN);
    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, rif));*/
    	
    	
    	// first evaluate MdrtbTreatmentStartedCohort
    	Cohort baseCohort = super.evaluate(cohortDefinition, context); 
 
    	MdrtbAfterTreatmentStartedCohortDefinition cd = (MdrtbAfterTreatmentStartedCohortDefinition) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	
    	// get all the program during the specified period
    	Map<Integer,List<MdrtbPatientProgram>> mdrtbPatientProgramsMap = ReportUtil.getMdrtbPatientProgramsInDateRangeMap(cd.getFromDate(), cd.getToDate());
    	
    	for (int id : baseCohort.getMemberIds()) {
    		// first we need to find out what program(s) the patient was on during a given time period
    		List<MdrtbPatientProgram> programs = mdrtbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			MdrtbPatientProgram program = programs.get(programs.size() - 1);
    			
    			Patient patient = Context.getPatientService().getPatient(id); 
    			
    			// create the range we want to search for specimens for
    			Calendar treatmentStartDate = Calendar.getInstance();
				treatmentStartDate.setTime(program.getTreatmentStartDateDuringProgram());
			
				// increment start date to the specified treatment month, if specified
				if(cd.getFromTreatmentMonth() != null) {
					treatmentStartDate.add(Calendar.MONTH, cd.getFromTreatmentMonth());
				}		
				Date startDateCollected = treatmentStartDate.getTime();
			
				// now set the end date to specified treatment month, if specified
				Date endDateCollected = null;
				if (cd.getToTreatmentMonth() != null) {			
					treatmentStartDate.add(Calendar.MONTH, cd.getToTreatmentMonth() - 
						(cd.getFromTreatmentMonth() != null ? cd.getFromTreatmentMonth() : 0) + 1);
					endDateCollected = treatmentStartDate.getTime();
				}
					
				
			  	
				Concept inh = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ISONIAZID); 
				Cohort c = MdrtbQueryService.getPatientsResistantToAnyDrugs(context, startDateCollected, endDateCollected, inh);
		    	
		    	// Must be resistant to RIF
		    	Concept rif = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN);
		    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, startDateCollected, endDateCollected, rif));
				
				
				// get all the specimens for the given patient during the given range and test the results of all smears and cultures
				
    		}
    	}
		return resultCohort;
	}
}
