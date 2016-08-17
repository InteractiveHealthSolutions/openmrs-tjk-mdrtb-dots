package org.openmrs.module.labmodule.web.controller.specimen;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.SpecimenValidator;
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
@RequestMapping("/module/labmodule/specimen/add.form")
public class LabSpecimenAddController extends AbstractSpecimenController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
    	
	}

	@ModelAttribute("specimen")
	public Specimen getSpecimen(@RequestParam(required = true, value = "patientId") Integer patientId) {
		Specimen specimen = Context.getService(TbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
		
		// set the default type to "sputum"
		specimen.setType(Context.getService(TbService.class).getConcept(TbConcepts.SPUTUM));
		
		return specimen;
	}
		
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimenAdd(@RequestParam(required = true, value = "patientId") Integer patientId, ModelMap map) {
		map.put("patientId", patientId);
		return new ModelAndView("/module/mdrtb/specimen/specimenAdd", map);
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("specimen") Specimen specimen, BindingResult result, SessionStatus status, ModelMap map,
	                                  @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		
		// validate
		TbPatientProgram patientProgram = Context.getService(TbService.class).getMdrtbPatientProgram(patientProgramId);
		new SpecimenValidator().validate(specimen, result, patientProgram);
		
    	if (result.hasErrors()) {
			map.put("patientProgramId", patientProgramId);
			map.put("errors", result);
			return new ModelAndView("/module/mdrtb/specimen/specimenAdd", map);
		}
		
		// save the specimen
		Context.getService(TbService.class).saveSpecimen(specimen);
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// redirect to the new detail patient for this specimen
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
}
