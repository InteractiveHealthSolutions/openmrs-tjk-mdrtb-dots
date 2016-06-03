package org.openmrs.module.mdrtb.web.controller.form;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


public class AbstractFormController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		
		// register binders 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		
	}
	
	@ModelAttribute("patientProgramId")
	public Integer getPatientProgramId(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return patientProgramId;
	}
	
	@ModelAttribute("program")
	public MdrtbPatientProgram getPatientProgram(@RequestParam(required = true, value = "patientProgramId") Integer patientProgramId) {
		return Context.getService(MdrtbService.class).getMdrtbPatientProgram(patientProgramId);
	}
	
	@ModelAttribute("returnUrl")
	public String getReturnUrl(@RequestParam(required = false, value = "returnUrl") String returnUrl) {
		return returnUrl;
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getProviders() {
		return Context.getService(MdrtbService.class).getProviders();
	}
	
	@ModelAttribute("locations")
	Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@ModelAttribute("sites")
	public Collection<ConceptAnswer> getAnatomicalSites() {
		return Context.getService(MdrtbService.class).getPossibleAnatomicalSites();
	}

}
