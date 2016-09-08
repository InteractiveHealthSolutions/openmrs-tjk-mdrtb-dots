package org.openmrs.module.dotsreports.reporting.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a ProgramLocationCohortDefinition and produces a Cohort
 */
@Handler(supports={DOTSProgramLocationListCohortDefinition.class})
public class DOTSProgramLocationListCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public DOTSProgramLocationListCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients who were enrolled in a program at a specified location during a given time period
     * @should include patients with multiple enrollments during the given time period if one or more of those enrollments were at the specified location
     * @should define the boundary of a program from the end date of the previous program to the end date of the current program
     *	       i.e., that is if a person has the following enrollments:
     *						April 2010 to May 2010, Location A
     *						August 2010 to September 2010, Location B
     *          then evaluating
     *         		(location="Location B", startDate="June 2010", endDate="July 2010") will include this patient
     *              (location="Location B", startDate="April 2010", endDate="July 2010") will include this patient 
     *          
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
      	
      	DOTSProgramLocationListCohortDefinition cd = (DOTSProgramLocationListCohortDefinition) cohortDefinition;
      	if (cd.getLocation() == null) {
      		return null;
      	}
    	
    	// utility class
    	class SimpleProgram {
    		Date startDate;
    		Date endDate;
    		Integer locationId;
    		
    		SimpleProgram (Date startDate, Date endDate, Integer locationId) {
    			this.startDate = startDate;
    			this.endDate = endDate;
    			this.locationId = locationId;
    		}
    	}
    	
     	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"));
    	
     	Cohort resultCohort = new Cohort();
     	
     	// first get all the possible patient programs, sorted by date enrolled
    	StringBuffer q = new StringBuffer();
    	q.append("SELECT patient_id, date_enrolled, date_completed, location_id FROM patient_program");
    	q.append(" WHERE program_id = '" + mdrtbProgram.getId() + "' AND voided='0' ORDER BY date_enrolled");
    	
        List<List<Object>> results = Context.getAdministrationService().executeSQL(q.toString(), true);
  
    	// now convert the result set into a map from patient id a list of patient programs
        // (note that we are defining the "start date" of a specified patient program range to be the completion date of the program prior to that program)
    	Map<Integer, List<SimpleProgram>> programMap = new HashMap<Integer,List<SimpleProgram>>();
    	
    	for (List<Object> result : results) {
    		Integer patientId = (Integer) result.get(0);
    		if (programMap.get(patientId) == null) {
    			programMap.put(patientId, new ArrayList<SimpleProgram>());
    			programMap.get(patientId).add(new SimpleProgram(null, (Date) result.get(2), (Integer) result.get(3)));
    		}
    		else {
    			List<SimpleProgram> programs = programMap.get(patientId);
    			programs.add(new SimpleProgram(programs.get(programs.size()-1).endDate, (Date) result.get(2), (Integer) result.get(3)));
    		}
    	}
    	
    	// now loop through the patients and find matches
    	for (Integer patientId : programMap.keySet()) {	
    		for (SimpleProgram program : programMap.get(patientId)) {
    			if (program.locationId != null && program.locationId.equals(cd.getLocation().getId())) {
    				if ( (cd.getStartDate() == null || program.endDate == null || cd.getStartDate().before(program.endDate))
    					&& (cd.getEndDate() == null || program.startDate == null || cd.getEndDate().after(program.startDate))) {
    					resultCohort.addMember(patientId);
    					break;
    				}
    			}
    		}
    	}
    	return resultCohort;
    }
}
