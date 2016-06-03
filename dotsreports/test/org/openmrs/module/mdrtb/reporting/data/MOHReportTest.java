package org.openmrs.module.mdrtb.reporting.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.reporting.DotsPreviewReportRenderer;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.data.MOHReport;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MOHReportTest extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
    
    @Test
	public void render() throws Exception {
    	
		ReportSpecification report = new MOHReport();

		Map<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("location", Context.getLocationService().getLocation(2));
		parameters.put("year", 2009);
		
		EvaluationContext context = report.validateAndCreateContext(parameters);
		ReportData data = report.evaluateReport(context);
		RenderingMode mode = new RenderingMode(new DotsPreviewReportRenderer(), "Preview", null, null);
		mode.getRenderer().render(data, mode.getArgument(), System.out);
    }
}
