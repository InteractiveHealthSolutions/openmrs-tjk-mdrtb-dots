package org.openmrs.module.mdrtb.web.controller.program;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgramValidator;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.status.VisitStatus;
import org.openmrs.module.mdrtb.status.VisitStatusCalculator;
import org.openmrs.module.mdrtb.web.controller.status.DashboardVisitStatusRenderer;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProgramController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		
		// register binders for location and program workflow state
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("classificationsAccordingToPreviousDrugUse")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousDrugUse() {		
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse();
	}
	
	@ModelAttribute("classificationsAccordingToPreviousTreatment")
	public Collection<ProgramWorkflowState> getClassificationsAccordingToPreviousTreatment() {		
		return Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment();
	}
	
	@ModelAttribute("outcomes")
	Collection<ProgramWorkflowState> getOutcomes() {		
		return Context.getService(MdrtbService.class).getPossibleMdrtbProgramOutcomes();
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/program/showEnroll.form")
	public ModelAndView showEnrollInPrograms(@RequestParam(required = true, value = "patientId") Integer patientId,
	                                         ModelMap map) {
			
			Patient patient = Context.getPatientService().getPatient(patientId);
			if (patient == null) {
				throw new RuntimeException ("Show enroll called with invalid patient id " + patientId);
			}
		
			// we need to determine if this patient currently in active in an mdr-tb program to determine what fields to display
			MdrtbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(patient);
			map.put("hasActiveProgram", (mostRecentProgram != null && mostRecentProgram.getActive()) ? true : false);
			map.put("patientId", patientId);
			return new ModelAndView("/module/mdrtb/program/showEnroll", map);
	}

	@SuppressWarnings("unchecked")
    @RequestMapping(value = "/module/mdrtb/program/programEnroll.form", method = RequestMethod.POST)
	public ModelAndView processEnroll(@ModelAttribute("program") MdrtbPatientProgram program, BindingResult errors, 
	                                  @RequestParam(required = true, value = "patientId") Integer patientId,
	                                  SessionStatus status, HttpServletRequest request, ModelMap map) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		     
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		if (patient == null) {
			throw new RuntimeException ("Process enroll called with invalid patient id " + patientId);
		}
		
		// set the patient
		program.setPatient(patient);
		
		// perform validation (validation needs to happen after patient is set since patient is used to pull up patient's previous programs)
		if (program != null) {
    		new MdrtbPatientProgramValidator().validate(program, errors);
    	}
		
		if (errors.hasErrors()) {
			MdrtbPatientProgram mostRecentProgram = Context.getService(MdrtbService.class).getMostRecentMdrtbPatientProgram(patient);
			map.put("hasActiveProgram", mostRecentProgram != null && mostRecentProgram.getActive() ? true : false);
			map.put("patientId", patientId);
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/program/showEnroll", map);
		}
		
		// save the actual update
		Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());

		// clears the command object from the session
		status.setComplete();
		map.clear();
			
		// when we enroll in a program, we want to jump immediately to the intake for this patient
		// TODO: hacky to have to create a whole new visit status here just to determine the proper link?
		// TODO: modeling visit as a status probably wasn't the best way to go on my part
		VisitStatus visitStatus = (VisitStatus) new VisitStatusCalculator(new DashboardVisitStatusRenderer()).calculate(program);
		
		return new ModelAndView("redirect:" + visitStatus.getNewIntakeVisit().getLink() + "&returnUrl=" + request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=" + program.getId());		
	}
	
	@RequestMapping("/module/mdrtb/program/programDelete.form")
	public ModelAndView processDelete(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                  SessionStatus status, ModelMap map){

		MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
		
		// we need to save the patient id so that we know where to redirect to
		Integer patientId = program.getPatient().getId();
		
		// now void the program
		Context.getProgramWorkflowService().voidPatientProgram(program.getPatientProgram(), "voided by mdr-tb module");

		// clear the command object
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + patientId);
	}
}
