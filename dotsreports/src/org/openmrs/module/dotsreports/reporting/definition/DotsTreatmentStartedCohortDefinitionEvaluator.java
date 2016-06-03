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

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.reporting.MdrtbQueryService;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DotsTreatmentStartedCohortDefinition and produces a Cohort
 */
@Handler(supports={DotsTreatmentStartedCohortDefinition.class})
public class DotsTreatmentStartedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public DotsTreatmentStartedCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients whose first TB regimen was during the passed period
     */
    public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {	
    	DotsTreatmentStartedCohortDefinition cd = (DotsTreatmentStartedCohortDefinition) cohortDefinition;
    	Concept tbDrugSet = Context.getService(TbService.class).getConcept(TbConcepts.TUBERCULOSIS_DRUGS);
    	return MdrtbQueryService.getPatientsFirstStartingDrugs(context, cd.getFromDate(), cd.getToDate(), tbDrugSet);
    	
	}
}