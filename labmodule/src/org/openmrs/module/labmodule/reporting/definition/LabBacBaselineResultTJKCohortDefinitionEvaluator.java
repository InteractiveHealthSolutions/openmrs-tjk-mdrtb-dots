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
@Handler(supports={LabBacBaselineResultTJKCohortDefinition.class},order=20)
public class LabBacBaselineResultTJKCohortDefinitionEvaluator extends ProgramEnrollmentCohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public LabBacBaselineResultTJKCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	// first evaluate MdrtbTreatmentStartedCohort
    	Cohort baseCohort = super.evaluate(cohortDefinition, context); 
 
    	LabBacBaselineResultTJKCohortDefinition cd = (LabBacBaselineResultTJKCohortDefinition) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	
    	// get all the program during the specified period
    	//Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = ReportUtil.getTbPatientProgramsInDateRangeMap(cd.getEnrolledOnOrAfter(), cd.getEnrolledOnOrBefore());
    	
    	for (int id : baseCohort.getMemberIds()) {
    		// first we need to find out what program(s) the patient was on during a given time period
    	//	List<TbPatientProgram> programs = tbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		//if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			//TbPatientProgram program = programs.get(programs.size() - 1);
    			
    			Patient patient = Context.getPatientService().getPatient(id); 
    			
    			
    			// create the range we want to search for specimens for
    		/*	Calendar treatmentStartDate = Calendar.getInstance();
				treatmentStartDate.setTime(program.getTreatmentStartDateDuringProgram());
				//treatmentStartDate.setTime(program.);
			
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
				}*/
					
				Boolean positiveSmear = null;
				//Boolean positiveCulture = null;
				Set<Concept> positiveResults = TbUtil.getPositiveResultConcepts();
				Concept negative = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE);
			  	
				// get all the specimens for the given patient during the given range and test the results of all smears and cultures
				for (Specimen specimen : Context.getService(TbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
    				if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==0) {
					for (Smear smear : specimen.getSmears()) {
    					if (smear.getResult() != null) {
    						if (positiveResults.contains(smear.getResult())) {
    							positiveSmear = true;
    						}
    						else if (smear.getResult().equals(negative)) {
    							positiveSmear = false;
    						}
    					}
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
    				if ((positiveSmear != null && positiveSmear)) {// || (positiveCulture != null && positiveCulture)) {
    					break;
    				}
				}
    			}
    			
				LabBacBaselineResultTJKCohortDefinition.Result result;
    			 
    			// consider the patient positive if they have either a positive smear or culture
    			if ((positiveSmear != null && positiveSmear)) {// || (positiveCulture != null && positiveCulture)) {
    				result = LabBacBaselineResultTJKCohortDefinition.Result.POSITIVE;
    			}
    			// consider the patient negative if they have both a negative smear AND culture
	    		else if (positiveSmear !=null && !positiveSmear) {// && positiveCulture != null && !positiveCulture) {
	    			result = LabBacBaselineResultTJKCohortDefinition.Result.NEGATIVE;
	    		}
    			// otherwise, consider the status unknown
	    		else {
	    			result = LabBacBaselineResultTJKCohortDefinition.Result.UNKNOWN;
	    		}
    			
    			// determine whether or not to include this patient in the cohort depending on the overall result parameter
    			if (cd.getOverallResult() == result) {
    				resultCohort.addMember(id);
    			}
    		}
    	//}
		return resultCohort;
	}
}
