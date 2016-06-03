package org.openmrs.module.mdrtb.web.controller.specimen;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Bacteriology;
import org.openmrs.module.mdrtb.specimen.HAIN;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Xpert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/quickEntry.form")
public class SpecimenQuickEntryController extends AbstractSpecimenController {

	// used to specify if we are adding smears or cultures
	@ModelAttribute("testType")
	public String getTestType(@RequestParam(required = true, value = "testType") String testType) {
		return testType;
	}
	
	// used to specify the number of smear/cultures to add
	@ModelAttribute("numberOfTests")
	public Integer getNumberOfTests(@RequestParam(required = true, value="numberOfTests") Integer numberOfTests) {
		return numberOfTests;
	}
	
	// used to specify the default sample type (i.e, sputum)
	@ModelAttribute("defaultSampleType")
	public Concept getDefaultSampleType() {
		return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPUTUM);
	}
	
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showQuickEntry(@RequestParam(required = true, value = "patientId") Integer patientId, ModelMap map) {
		map.put("patientId", patientId);
		return new ModelAndView("/module/mdrtb/specimen/specimenQuickEntry", map);
	}
	
		
	// since there are multiple objects (ie specimens) here we can't use spring binding easily
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@RequestParam(required = true, value = "patientId") Integer patientId,
	                                  @RequestParam(required = true, value = "patientProgramId") Integer patientProgramId,
	                                  @RequestParam(required = true, value = "testType") String testType,
		                              @RequestParam(required = true, value = "numberOfTests") Integer numberOfTests,
		                              @RequestParam(required = true, value = "location") Location location,
		                              @RequestParam(required = true, value = "lab") Location lab,
		                              @RequestParam(required = true, value = "provider") Person provider,
		                              HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {
										
		// cycle through all tests
		Integer i = 1;
		while (i < numberOfTests + 1) {
			// we are only adding results where a date collected has been specified
			if (StringUtils.isNotBlank(request.getParameter("dateCollected" + i))) {
				
				String dateCollected = request.getParameter("dateCollected" + i);
				//String dateTested = request.getParameter("dateTested" + i);
				String dateCompleted = request.getParameter("dateCompleted" + i);
				String identifier = request.getParameter("identifier" + i);
				System.out.println(">>>>>" + identifier);
				String type = request.getParameter("type" + i);
				String appearance = request.getParameter("appearance" + i);
				String result = request.getParameter("result" + i);
				String rifResult = request.getParameter("rifResult" + i);
				/*String mtbBurden = request.getParameter("mtbBurden" + i);
				String errorCode = request.getParameter("errorCode" + i);*/
				String inhResult = request.getParameter("inhResult" + i);
					
				// create the new specimen
				Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(Context.getPatientService().getPatient(patientId));
				
				// add the specimen-specific parameters
				specimen.setLocation(location);
				specimen.setProvider(provider);
				
				SimpleDateFormat dateFormat = Context.getDateFormat();
		    	dateFormat.setLenient(false);
		    	specimen.setDateCollected(dateFormat.parse(dateCollected));
		    	
				
				if (StringUtils.isNotBlank(identifier)) {
					specimen.setIdentifier(identifier);		
				}
				if (StringUtils.isNotBlank(type)) {
					specimen.setType(Context.getConceptService().getConcept(Integer.valueOf(type)));
				}
				if (StringUtils.isNotBlank(appearance)) {
					specimen.setAppearance(Context.getConceptService().getConcept(Integer.valueOf(appearance)));
				}
				
				// now add the appropriate test
				Bacteriology bac = null;
				Xpert xpert = null;
				HAIN hain = null;
				if (testType.contains("smear")) {
					bac = specimen.addSmear();
				}
				else if (testType.contains("culture")) {
					bac = specimen.addCulture();
				}
				
				else if(testType.contains("xpert")) {
					xpert = specimen.addXpert();
				}
				
				else if(testType.contains("hain")) {
					hain = specimen.addHAIN();
				}
				//else {
				//	throw new MdrtbAPIException("Invalid test type passed to quick entry controller.");
				//}
				
				// now add the test-specific parameters
				if(bac!=null) {
				bac.setLab(lab);
				
				
				
				if (StringUtils.isNotBlank(result)) {
					bac.setResult(Context.getConceptService().getConcept(Integer.valueOf(result)));
				}
				
				if (StringUtils.isNotBlank(dateCompleted)) {
					bac.setResultDate(dateFormat.parse(dateCompleted));
				}
				
				}
				
				else if(xpert!=null) {
					xpert.setLab(lab);
					if (StringUtils.isNotBlank(dateCompleted)) {
						xpert.setResultDate(dateFormat.parse(dateCompleted));
					}
					/*if(StringUtils.isNotBlank(dateTested)) {
						dateFormat.setLenient(false);
				    	xpert.setStartDate(dateFormat.parse(dateTested));
						
					}*/
					
					if (StringUtils.isNotBlank(result)) {
						xpert.setResult(Context.getConceptService().getConcept(Integer.valueOf(result)));
					}
					if (StringUtils.isNotBlank(rifResult)) {
						xpert.setRifResistance(Context.getConceptService().getConcept(Integer.valueOf(rifResult)));
					}
					/*if (StringUtils.isNotBlank(mtbBurden)) {
						xpert.setMtbBurden(Context.getConceptService().getConcept(Integer.valueOf(mtbBurden)));
					}
					
					if(StringUtils.isNotBlank(errorCode)) {
						xpert.setErrorCode(errorCode);
					}*/
					
				}
				
				else if(hain!=null) {
					hain.setLab(lab);
					if (StringUtils.isNotBlank(dateCompleted)) {
						hain.setResultDate(dateFormat.parse(dateCompleted));
					}
					/*if(StringUtils.isNotBlank(dateTested)) {
						dateFormat.setLenient(false);
						hain.setStartDate(dateFormat.parse(dateTested));
						
					}*/
					
					if (StringUtils.isNotBlank(result)) {
						hain.setResult(Context.getConceptService().getConcept(Integer.valueOf(result)));
					}
					if (StringUtils.isNotBlank(rifResult)) {
						hain.setRifResistance(Context.getConceptService().getConcept(Integer.valueOf(rifResult)));
					}
					
					if (StringUtils.isNotBlank(inhResult)) {
						hain.setInhResistance(Context.getConceptService().getConcept(Integer.valueOf(inhResult)));
					}
					
					/*if(StringUtils.isNotBlank(errorCode)) {
						hain.setErrorCode(errorCode);
					}*/
					
					
					
				}
				
				// save the specimen
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
			
			i++;
		}
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		// redirect to the overview page
		return new ModelAndView("redirect:specimen.form?patientProgramId=" + patientProgramId + "&patientId=" + patientId);
	}
	
	
	
}
