package org.openmrs.module.labmodule.reporting.definition;

import java.util.Arrays;
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
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.reporting.definition.LabBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Smear;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.evaluator.ProgramEnrollmentCohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={ LabNoFollowupSmearTJKCohortDefinition.class},order=20)
public class LabNoFollowupSmearTJKCohortDefinitionEvaluator extends ProgramEnrollmentCohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public LabNoFollowupSmearTJKCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	// first evaluate MdrtbTreatmentStartedCohort
    	Cohort baseCohort = super.evaluate(cohortDefinition, context); 
 
    	 LabNoFollowupSmearTJKCohortDefinition cd = ( LabNoFollowupSmearTJKCohortDefinition) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	
    	// get all the program during the specified period
    	//Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = ReportUtil.getTbPatientProgramsInDateRangeMap(cd.getEnrolledOnOrAfter(), cd.getEnrolledOnOrBefore());
    	Boolean foundFollowup = false;
    	for (int id : baseCohort.getMemberIds()) {
    		foundFollowup = false;
    		// first we need to find out what program(s) the patient was on during a given time period
    	//	List<TbPatientProgram> programs = tbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		//if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			//TbPatientProgram program = programs.get(programs.size() - 1);
    			
    			Patient patient = Context.getPatientService().getPatient(id); 
    			
    			
				// get all the specimens for the given patient during the given range and test the results of all smears and cultures
				for (Specimen specimen : Context.getService(TbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
    				if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()!=0) {
    					foundFollowup = true;
    					break;
    				}
    				
    				
				}
				
				if(foundFollowup==false) {
					resultCohort.addMember(id);
				}
    				
    				/*for (Culture culture : specimen.getCultures()) {
    					if (culture.getResult() != null) {
    						if (positiveResults.contains(culture.getResult())) {
    							positiveCulture = true;
    						}
    						else if (culture.getResult().equals(negative)) {
    							positiveCulture = false;
    						}
    					}
    				}*/
    					
    				// if we've found a positive smear or culture, no point in doing anything else
    			
    	//}
		
	}
    	return resultCohort;
    }
}

