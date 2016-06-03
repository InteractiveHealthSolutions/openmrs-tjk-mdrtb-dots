package org.openmrs.module.mdrtb.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.web.controller.PortletController;


public class MdrtbPatientHeaderPortletController extends PortletController {
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Patient patient = Context.getPatientService().getPatient((Integer) model.get("patientId"));
		PatientIdentifier identifier = patient.getPatientIdentifier(Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType"));
		
		model.put("primaryIdentifier", identifier);
	}
}
