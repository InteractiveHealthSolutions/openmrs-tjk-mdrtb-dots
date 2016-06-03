package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.reporting.definition.DotsTreatmentStartedCohortDefinition;
import org.openmrs.module.dotsreports.reporting.definition.DotsTreatmentStartedCohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class MdrtbTreatmentStartedCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
	
	/**
	 * @see {@link DotsTreatmentStartedCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients whose first tb regimen was during the passed period", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsWhoseFirstTbRegimenWasDuringThePassedPeriod() throws Exception {
		DotsTreatmentStartedCohortDefinition cd = new DotsTreatmentStartedCohortDefinition();
		cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		cd.addParameter(new Parameter("toDate", "To Date", Date.class));
		
		int runningTotal = 0;
		for (int i=1998; i<=2010; i++) {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("fromDate", DateUtil.getDateTime(i, 1, 1));
			context.addParameterValue("toDate", DateUtil.getDateTime(i, 12, 31));
			EvaluatedCohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
			runningTotal += cohort.size();
			System.out.println("Num who started treatment in " + i + ": " + cohort.size() + "[" + cohort.getCommaSeparatedPatientIds() + "]");
		}
		System.out.println("Total all years: " + runningTotal);
	}
}