package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.MdrtbConstants.TbClassification;
import org.openmrs.module.dotsreports.reporting.definition.DotsDstResultCohortDefinition;
import org.openmrs.module.dotsreports.reporting.definition.DotsDstResultCohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class DstResultCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

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
	 * @see {@link DotsDstResultCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return confirmed mdrtb patients for the period", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnConfirmedMdrtbPatientsForThePeriod() throws Exception {
		
		DotsDstResultCohortDefinition cd = new DotsDstResultCohortDefinition();
		cd.setTbClassification(TbClassification.MDR_TB);
		cd.addParameter(new Parameter("minResultDate", "From Date", Date.class));
		cd.addParameter(new Parameter("maxResultDate", "To Date", Date.class));
		
		int runningTotal = 0;
		for (int i=1998; i<=2010; i++) {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("minResultDate", DateUtil.getDateTime(i, 1, 1));
			context.addParameterValue("maxResultDate", DateUtil.getDateTime(i, 12, 31));
			EvaluatedCohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
			runningTotal += cohort.size();
			System.out.println("Num with MDR results " + i + ": " + cohort.size() + "[" + cohort.getCommaSeparatedPatientIds() + "]");
		}
		System.out.println("Total all years: " + runningTotal);
	}

	/**
	 * @see {@link DotsDstResultCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return confirmed xdrtb patients for the period", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnConfirmedXdrtbPatientsForThePeriod() throws Exception {
		EvaluationContext context = new EvaluationContext();
		DotsDstResultCohortDefinition cd = new DotsDstResultCohortDefinition();
		cd.setTbClassification(TbClassification.XDR_TB);
		EvaluatedCohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		System.out.println("Num XDR: " + cohort.size() + " - " + cohort.getCommaSeparatedPatientIds());
	}
}