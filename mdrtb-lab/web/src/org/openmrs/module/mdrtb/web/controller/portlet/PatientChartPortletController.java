package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.patientchart.PatientChart;
import org.openmrs.module.mdrtb.patientchart.PatientChartFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.controller.PortletController;

/**
 * The Patient Chart Portlet Controller
 */
public class PatientChartPortletController extends PortletController {
	
	/**
	 * @see PortletController#populateModel(HttpServletRequest, Map)
	 */
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(Integer.valueOf((String) model.get("patientProgramId")));
		PatientChart chart = new PatientChartFactory().createPatientChart(program);
		model.put("chart", chart);
	}
}
