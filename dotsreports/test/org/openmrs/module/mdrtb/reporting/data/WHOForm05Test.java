package org.openmrs.module.mdrtb.reporting.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.reporting.DotsPreviewReportRenderer;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.data.DOTS10TJK;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class WHOForm05Test extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
	
	@Override
	public String getWebappName() {
		return "openmrs_haiti_mdrtb";
	}
    
    @Test
	public void render() throws Exception {
    	
		ReportSpecification report = new DOTS10TJK();

		Map<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("location", Context.getLocationService().getLocation(2));
		parameters.put("year", 2009);
		parameters.put("quarter", 2);
		
		EvaluationContext context = report.validateAndCreateContext(parameters);
		ReportData data = report.evaluateReport(context);
		RenderingMode mode = new RenderingMode(new DotsPreviewReportRenderer(), "Preview", null, null);
		mode.getRenderer().render(data, mode.getArgument(), System.out);
		
		/*
		DataSetRow row = data.getDataSets().values().iterator().next().iterator().next();
		for (Map.Entry<DataSetColumn, Object> e : row.getColumnValues().entrySet()) {
			System.out.println(e.getKey().getLabel() + ": " + ((Cohort)e.getValue()).getCommaSeparatedPatientIds());
		}
		*/
    }
}
