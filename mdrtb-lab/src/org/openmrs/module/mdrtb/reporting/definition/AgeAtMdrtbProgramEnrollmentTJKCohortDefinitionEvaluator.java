package org.openmrs.module.mdrtb.reporting.definition;

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
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.evaluator.ProgramEnrollmentCohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={AgetAtMdrtbProgramEnrollmentTJKCohortDefinition.class},order=20)
public class AgeAtMdrtbProgramEnrollmentTJKCohortDefinitionEvaluator extends ProgramEnrollmentCohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public AgeAtMdrtbProgramEnrollmentTJKCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	// first evaluate MdrtbTreatmentStartedCohort
    	Cohort baseCohort = super.evaluate(cohortDefinition, context); 
 
    	AgetAtMdrtbProgramEnrollmentTJKCohortDefinition cd = (AgetAtMdrtbProgramEnrollmentTJKCohortDefinition) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	
    	// get all the program during the specified period
    	Map<Integer,List<MdrtbPatientProgram>> tbPatientProgramsMap = ReportUtil.getMdrtbPatientProgramsEnrolledInDateRangeMap(cd.getEnrolledOnOrAfter(), cd.getEnrolledOnOrBefore());
    	
    	for (int id : baseCohort.getMemberIds()) {
    		// first we need to find out what program(s) the patient was on during a given time period
    		List<MdrtbPatientProgram> programs = tbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			MdrtbPatientProgram program = programs.get(programs.size() - 1);
    			
    			Patient patient = Context.getPatientService().getPatient(id);
    			Date programStarted = program.getDateEnrolled();
    			
    			if(programStarted!=null) {
    			Integer age = patient.getAge(programStarted);
    			System.out.println(id + " " + age + " " + cd.getMinAge() + " " + cd.getMaxAge());
    			if(age !=null && (age >=cd.getMinAge() && age < cd.getMaxAge()))
    				resultCohort.addMember(id);
    			}
    		}
    	}
		return resultCohort;
	}
}
