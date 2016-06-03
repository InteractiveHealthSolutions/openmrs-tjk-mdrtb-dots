package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.form.SimpleFollowUpForm;
import org.openmrs.module.mdrtb.form.SimpleFormValidator;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.web.util.MdrtbWebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/module/mdrtb/form/followup.form")
public class SimpleFollowUpFormController extends AbstractFormController {
	
	@ModelAttribute("followup")
	public SimpleFollowUpForm getIntakeForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	                                      @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			SimpleFollowUpForm form = new SimpleFollowUpForm(program.getPatient());
			
			// prepopulate the intake form with any program information
			form.setLocation(program.getLocation());
				
			return form;
		}
		else {
			return new SimpleFollowUpForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showFollowupForm() {	
		ModelMap map = new ModelMap();
		return new ModelAndView("/module/mdrtb/form/followup", map);	
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView processFollowupForm (@ModelAttribute("followup") SimpleFollowUpForm followup, BindingResult errors, 
	                                         @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                         @RequestParam(required = false, value = "returnUrl") String returnUrl,
	                                         SessionStatus status, HttpServletRequest request, ModelMap map) {

		// perform validation and check for errors
		if (followup != null) {
    		new SimpleFormValidator().validate(followup, errors);
    	}
		
		if (errors.hasErrors()) {
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/form/followup", map);
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(followup.getEncounter());

		// clears the command object from the session
		status.setComplete();
		map.clear();

		// if there is no return URL, default to the patient dashboard
		if (returnUrl == null || StringUtils.isEmpty(returnUrl)) {
			returnUrl = request.getContextPath() + "/module/mdrtb/dashboard/dashboard.form";
		}
		
		returnUrl = MdrtbWebUtil.appendParameters(returnUrl, null, patientProgramId);
		
		return new ModelAndView(new RedirectView(returnUrl));
	}
}
