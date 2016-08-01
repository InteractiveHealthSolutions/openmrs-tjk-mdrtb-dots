package org.openmrs.module.dotsreports.reporting.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.program.TbPatientProgram;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.dotsreports.reporting.definition.DotsBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.dotsreports.specimen.Culture;
import org.openmrs.module.dotsreports.specimen.Smear;
import org.openmrs.module.dotsreports.specimen.Specimen;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.evaluator.ProgramEnrollmentCohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an MdrtbProgramClosedAfterTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={FirstMTBResultCohortDefinition.class},order=20)
public class FirstMTBResultCohortDefinitionEvaluator implements CohortDefinitionEvaluator  {
	
	/**
	 * Default Constructor
	 */
	public FirstMTBResultCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
 
    	FirstMTBResultCohortDefinition cd = (FirstMTBResultCohortDefinition) cohortDefinition;
    	Cohort resultCohort = new Cohort();
    	Cohort rejectCohort = new Cohort();
    	Concept c = Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT);
    	
    	ArrayList<Concept> questions = new ArrayList<Concept>();
    	questions.add(c);
    	
    	Set <Concept> posSet = TbUtil.getPositiveResultConcepts();
    	
    	//SArrayList<Concept> answers = null;// = new ArrayList<Concept>();
    	
    	
    	List <Obs> obsList = Context.getObsService().getObservations(null, null, questions, null, null, null, null, null, null, cd.getStartDate(), cd.getEndDate(), false);
    	
    	// iterate via "iterator loop"
    			
    	for(Obs tempObs : obsList) {
    		if(resultCohort.contains(tempObs.getPersonId()) || rejectCohort.contains(tempObs.getPersonId()))
    			continue;
    		
    		if(posSet.contains(tempObs.getValueCoded())) {
    			resultCohort.addMember(tempObs.getPersonId());
    		}
    		
    		else {
    			rejectCohort.addMember(tempObs.getPersonId());
    		}
    			
    	}
    	
    	
    	return resultCohort;
	}
}
