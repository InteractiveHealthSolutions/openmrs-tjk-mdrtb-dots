package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.openmrs.module.mdrtb.reporting.data.Cohorts;
import org.openmrs.module.mdrtb.reporting.definition.MdrtbBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={MdrtbPreviousProgramOutcomeCohortDefinition.class},order=20)
public class MdrtbPreviousProgramOutcomeCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public MdrtbPreviousProgramOutcomeCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	
    	
    	MdrtbPreviousProgramOutcomeCohortDefinition cd = (MdrtbPreviousProgramOutcomeCohortDefinition) cohortDefinition;
    	
    	Cohort baseCohort = MdrtbUtil.getMdrPatients(null, null, null, null, null);
    	
    	Cohort resultCohort = new Cohort();
    	
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.set(1900,0, 1, 0, 0,1);
    	
    	GregorianCalendar gc2 = new GregorianCalendar();
    	gc2.setTime(cd.getFromDate());
    	gc.add(gc.DATE, -1);
    	
    	// get all the program during the specified period
    	Map<Integer,List<MdrtbPatientProgram>> mdrtbPatientProgramsMap = ReportUtil.getMdrtbPatientProgramsInDateRangeMap(gc.getTime(), gc2.getTime());
    	
    	for (int id : baseCohort.getMemberIds()) {
    		// first we need to find out what program(s) the patient was on during a given time period
    		List<MdrtbPatientProgram> programs = mdrtbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			MdrtbPatientProgram program = programs.get(programs.size() - 1);
    			
    			try {
    				if(program.getOutcome()!=null && (program.getOutcome().getConcept()==Context.getService(MdrtbService.class).getConcept(cd.getOutcome()))) {
    			
    				resultCohort.addMember(id);
    			}
    			
    			}
    			catch(Exception e) {
    				if(program.getOutcome()==null) {
    					System.out.println("NULL PROGRAM OUTCOME");
    				}
    				
    				else if( cd.getOutcome()==null) {
    					System.out.println("NULL CD OUTCOME");
    				}
    				e.printStackTrace();
    			}
				
			
				
				
    		}
    	}
		return resultCohort;
	}
}
