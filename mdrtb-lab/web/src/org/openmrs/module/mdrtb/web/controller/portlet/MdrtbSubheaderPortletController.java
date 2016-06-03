package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.web.controller.PortletController;


public class MdrtbSubheaderPortletController extends PortletController {
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		Patient patient = Context.getPatientService().getPatient((Integer) model.get("patientId"));
			
		List<MdrtbPatientProgram> programs = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient);
		// we want to display the list in reverse chronological order
		Collections.reverse(programs);
		model.put("patientPrograms", programs);
		
		// get the program we are currently viewing	
		if (StringUtils.isNotBlank((String) model.get("patientProgramId")) && Integer.valueOf((String) model.get("patientProgramId")) != -1) {
			MdrtbPatientProgram patientProgram = Context.getService(MdrtbService.class).getMdrtbPatientProgram(Integer.valueOf((String) model.get("patientProgramId")));
			model.put("patientProgram", patientProgram);
		}
		else {
			model.put("patientProgramId", -1);
		}
	
		User mdrUser = Context.getAuthenticatedUser();
		
		
		if(! mdrUser.isSuperUser()&& mdrUser.hasRole("Lab Tech")) {
			model.put("labtech", true);
			
		}
		
		else
		{
			model.put("labtech", false);
			
		}
	}	
}
