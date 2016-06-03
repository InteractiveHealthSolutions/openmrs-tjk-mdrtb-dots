package org.openmrs.module.dotsreports.reporting.definition;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.dotsreports.program.TbPatientProgram;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={DotsProgramClosedAfterTreatmentStartedCohortDefintion.class},order=20)
public class DotsProgramClosedAfterTreatmentStartedCohortDefinitionEvaluator extends DotsTreatmentStartedCohortDefinitionEvaluator {
	
	/**
	 * Default Constructor
	 */
	public DotsProgramClosedAfterTreatmentStartedCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients whose first TB regimen was during the passed period
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	// first evaluate MdrtbTreatmentStartedCohort
    	Cohort baseCohort = super.evaluate(cohortDefinition, context); 
 
    	DotsProgramClosedAfterTreatmentStartedCohortDefintion cd = (DotsProgramClosedAfterTreatmentStartedCohortDefintion) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	
    	// get all the program during the specified period
    	Map<Integer,List<TbPatientProgram>> mdrtbPatientProgramsMap = ReportUtil.getTbPatientProgramsInDateRangeMap(cd.getFromDate(), cd.getToDate());
    	
    	for (int id : baseCohort.getMemberIds()) {
    		// first we need to find out what program(s) the patient was on during a given time period
    		List<TbPatientProgram> programs = mdrtbPatientProgramsMap.get(id);

    		// only continue if the patient was in a program during this time period
    		if (programs != null && programs.size() != 0) {
    			
    			// by convention, operate on the most recent program during the time period (there really should only ever be one program in a single period)
    			TbPatientProgram program = programs.get(programs.size() - 1);
    	
    			// test to see if the program is closed
    			if (program.getDateCompleted() != null) {
    				
    				// if months from treatment start is null, we just are testing if the program has been closed
    				if (cd.getMonthsFromTreatmentStart() == null) {
    					resultCohort.addMember(id);
    				}
    				// otherwise, see if the completion date is within the range
    				else {
    					Calendar treatmentStartDate = Calendar.getInstance();
    					treatmentStartDate.setTime(program.getTreatmentStartDateDuringProgram());
    					// increment by the specified number of months
    					treatmentStartDate.add(Calendar.MONTH, cd.getMonthsFromTreatmentStart());
    					
    					// test to see if the completed date the treatment start date + x months
    					if (program.getDateCompleted().before(treatmentStartDate.getTime())) {
    						resultCohort.addMember(id);
    					}
    				}
    			}
    		}
    	}
    	System.out.println("---->" + resultCohort.getSize());
    	Set<Integer>ids = resultCohort.getMemberIds();
    	 for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
    	        System.out.println(iterator.next());

    	    }
		return resultCohort;
	}
}
