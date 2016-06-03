/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dotsreports.reporting.definition;

import java.util.Iterator;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.reporting.MdrtbQueryService;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DotsTJKPatientDistrictCohortDefinition and produces a Cohort
 */
@Handler(supports={DotsTJKPatientDistrictCohortDefinition.class})
public class DotsTJKPatientDistrictCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public DotsTJKPatientDistrictCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients whose first TB regimen was during the passed period with the given Rayon in their address
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {	
    	Cohort fc = new Cohort();
    	DotsTJKPatientDistrictCohortDefinition cd = (DotsTJKPatientDistrictCohortDefinition) cohortDefinition;
    	Concept tbDrugSet = Context.getService(TbService.class).getConcept(TbConcepts.TUBERCULOSIS_DRUGS);
    	Cohort treatmentStartCohort = MdrtbQueryService.getPatientsFirstStartingDrugs(context, cd.getFromDate(), cd.getToDate(), tbDrugSet);
    
    	if(treatmentStartCohort.isEmpty())
    		return treatmentStartCohort;
    	
    	Set<Integer> tscIdSet = treatmentStartCohort.getMemberIds();
    	System.out.println("SET SIZE:" + tscIdSet.size());
    	Iterator<Integer> itr = tscIdSet.iterator();
    	Integer idCheck = null;
    	Patient patient = null;
    	PersonAddress addr = null;
    	PatientService ps = Context.getService(PatientService.class);
    	while(itr.hasNext()) {
    		idCheck = (Integer)itr.next();
    		patient = ps.getPatient(idCheck);
    		addr = patient.getPersonAddress();
    		if(cd.getDistrict()!=null) {
    		if(TbUtil.areRussianStringsEqual(addr.getCountyDistrict(), cd.getDistrict()))
    		//if(addr.getCountyDistrict()!= null && (addr.getCountyDistrict().equalsIgnoreCase(cd.getDistrict())));
    			fc.addMember(idCheck);
    		}
    		
    		else 
    			fc.addMember(idCheck);
    	}
    	
    	return fc;
    
    
    }
}