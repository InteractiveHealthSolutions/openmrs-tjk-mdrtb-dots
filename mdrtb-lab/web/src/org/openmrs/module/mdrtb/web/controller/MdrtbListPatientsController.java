package org.openmrs.module.mdrtb.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.ProgramWorkflowStateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MdrtbListPatientsController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
     	binder.registerCustomEditor(Location.class, new LocationEditor());
     	binder.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
    }  
    
    @RequestMapping("/module/mdrtb/mdrtbListPatients")
    public void listPatients(ModelMap model,
    				@RequestParam(required=false, value="displayMode") 		String displayMode,
	    			@RequestParam(required=false, value="identifier") 		String identifier,
	    			@RequestParam(required=false, value="name") 			String name,
	    			@RequestParam(required=false, value="enrollment")		String enrollment,
	    			@RequestParam(required=false, value="location") 		Location location,
	    			@RequestParam(required=false, value="states") 			List<ProgramWorkflowState> states,
	    			@RequestParam(required=false, value="minage")			Integer minage,
	    			@RequestParam(required=false, value="maxage")			Integer maxage,
	    			@RequestParam(required=false, value="gender")			String gender
	    			
	            ) {
    	
    	model.addAttribute("displayMode", displayMode);
    	model.addAttribute("identifier", identifier);
    	model.addAttribute("name", name);
    	model.addAttribute("enrollment", enrollment);
    	model.addAttribute("location", location);;
    	model.addAttribute("states", states);
    	model.addAttribute("minage", minage);
    	model.addAttribute("maxage", maxage);
    	model.addAttribute("gender", gender);
    	
    	
    	if (StringUtils.hasText(displayMode)) {
    		Cohort cohort = MdrtbUtil.getMdrPatientsTJK(identifier, name, enrollment, location, states, minage, maxage, gender);
        	model.addAttribute("patientIds", cohort.getCommaSeparatedPatientIds());
        	model.addAttribute("patients", Context.getPatientSetService().getPatients(cohort.getMemberIds()));
    	}
    	
    	MdrtbService svc = Context.getService(MdrtbService.class);
    	
    	Program mdrtbProgram = svc.getMdrtbProgram();
    	model.addAttribute("mdrProgram", mdrtbProgram);
    	
    	/*List<Location> patientLocations = svc.getLocationsWithAnyProgramEnrollments();
    	model.addAttribute("patientLocations", patientLocations);*/
    	List<Location> patientLocations = Context.getLocationService().getAllLocations();
    	model.addAttribute("patientLocations", patientLocations);
    }
}
