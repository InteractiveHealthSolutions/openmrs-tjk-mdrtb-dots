package org.openmrs.module.mdrtb.web.controller.form;

import java.util.Arrays;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/form/select.form")
public class SelectFormController {
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = true, value = "formType") String formType, 
	                             @RequestParam(required = true, value = "patientId") Integer patientId,
	                             @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                             @RequestParam(required = false, value = "returnUrl") String returnUrl,
	                             ModelMap map) {
		
		List<Form> forms;
		
		if (formType.equals("intake")) {
			EncounterType [] intake = {Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"))};
			forms = Context.getFormService().getForms(null, true, Arrays.asList(intake), false, null, null, null);
		}
		else if (formType.equals("followUp")) {
			EncounterType [] followUp = {Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"))};
	        forms = Context.getFormService().getForms(null, true, Arrays.asList(followUp), false, null, null, null);
		}
		else {
			throw new MdrtbAPIException("Invalid formType. Must be intake or follow-up.");
		}
		
		// if we don't have any custom forms here, then just redirect to the simple form
		if (forms.size() == 0) {
			if (formType.equals("intake")) {
				return new ModelAndView("redirect:/module/mdrtb/form/intake.form?patientId=" + patientId
    			+ "&patientProgramId=" + patientProgramId  
    			+ "&returnUrl=" + returnUrl
    			+ "&encounterId=-1");
			}
			else if (formType.equals("followUp")) {
				return new ModelAndView("redirect:/module/mdrtb/form/followup.form?patientId=" + patientId
	    			+ "&patientProgramId=" + patientProgramId  
	    			+ "&returnUrl=" + returnUrl
	    			+ "&encounterId=-1");
			}
			else {
				throw new MdrtbAPIException("Invalid formType. Must be intake or follow-up.");
			}
		}
		else {
			// show the list of available forms for this encounter type
			map.put("patientId", patientId);
			map.put("patientProgramId", patientProgramId);
			map.put("formType", formType);
			map.put("forms", forms);
			return new ModelAndView("/module/mdrtb/form/select", map);
		}
	}
}
