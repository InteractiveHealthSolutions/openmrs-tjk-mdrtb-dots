package org.openmrs.module.mdrtb.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VisitsController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/module/mdrtb/visits/visits.form")
	public ModelAndView showVisits(@RequestParam(required = false, value="patientProgramId") Integer patientProgramId,
	                               @RequestParam(required = false, value="patientId") Integer patientId, ModelMap map) {
		
		if (patientProgramId != null && patientProgramId != -1) {
			MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			map.put("visits", new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(program));
			map.put("patientId", program.getPatient().getId());
			map.put("patientProgramId", patientProgramId);
		}
		else if (patientId != null) {			
			Patient patient = Context.getPatientService().getPatient(patientId);
			map.put("patientId", patientId);
			map.put("patientProgramId", -1);
			map.put("visits", new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(patient));

		}
		else {
			throw new MdrtbAPIException("Patient program Id or patient Id must be specified.");
		}
		
		return new ModelAndView("/module/mdrtb/visits/visits", map);
	}
	
	@RequestMapping("/module/mdrtb/visits/delete.form")
	public ModelAndView deleteVisit(@RequestParam(required = false, value="patientProgramId") Integer patientProgramId,
	                                @RequestParam(required = false, value="patientId") Integer patientId, 
									@RequestParam(required = true, value="visitId") Integer visitId, ModelMap map) {
		
		// handle the deletion
		Encounter encounter = Context.getEncounterService().getEncounter(visitId);
		if (encounter == null) {
			throw new MdrtbAPIException("Invalid encounter Id " + visitId + " - Cannot void visit.");
		}
		else {
			Context.getEncounterService().voidEncounter(encounter, "user deleted visit via MDR-TB module UI");
		}
		
		// forward on to the main showVisits method to display the visits page
		return showVisits(patientProgramId, patientId, map); 
	}
										
								
}
