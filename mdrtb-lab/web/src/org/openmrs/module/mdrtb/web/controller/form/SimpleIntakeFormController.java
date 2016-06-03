package org.openmrs.module.mdrtb.web.controller.form;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.form.SimpleFormValidator;
import org.openmrs.module.mdrtb.form.SimpleIntakeForm;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
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
@RequestMapping("/module/mdrtb/form/intake.form")
public class SimpleIntakeFormController extends AbstractFormController {
	
	@ModelAttribute("intake")
	public SimpleIntakeForm getIntakeForm(@RequestParam(required = true, value = "encounterId") Integer encounterId,
	                                      @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		// if no form is specified, create a new one
		if (encounterId == -1) {
			MdrtbPatientProgram program = Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
			
			SimpleIntakeForm form = new SimpleIntakeForm(program.getPatient());
			
			// prepopulate the intake form with any program information
			form.setEncounterDatetime(program.getDateEnrolled());
			form.setLocation(program.getLocation());
				
			return form;
		}
		else {
			return new SimpleIntakeForm(Context.getEncounterService().getEncounter(encounterId));
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showIntakeForm() {
		ModelMap map = new ModelMap();
		return new ModelAndView("/module/mdrtb/form/intake", map);	
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView processIntakeForm (@ModelAttribute("intake") SimpleIntakeForm intake, BindingResult errors, 
	                                       @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                       @RequestParam(required = false, value = "returnUrl") String returnUrl,
	                                       SessionStatus status, HttpServletRequest request, ModelMap map) {

		// perform validation and check for errors
		if (intake != null) {
    		new SimpleFormValidator().validate(intake, errors);
    	}
		
		if (errors.hasErrors()) {
			map.put("errors", errors);
			return new ModelAndView("/module/mdrtb/form/intake", map);
		}
		
		// save the actual update
		Context.getEncounterService().saveEncounter(intake.getEncounter());

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
