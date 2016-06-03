package org.openmrs.module.mdrtb.web.controller.specimen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.HAIN;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenValidator;
import org.openmrs.module.mdrtb.specimen.TestValidator;
import org.openmrs.module.mdrtb.specimen.Xpert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/specimen/specimen.form")
public class SpecimenController extends AbstractSpecimenController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Returns the default list of drugs to display in the DST add test results box
	 */
	@ModelAttribute("defaultDstDrugs")
	public List<List<Object>> getDefaultDstDrugs() {
		return MdrtbUtil.getDefaultDstDrugs();
	}
	
	/**
	 * Returns the smear that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("smear")
	public Smear getSmear(@RequestParam(required = false, value="smearId") Integer smearId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Smear smear = null;
		
		// only do something here if the smear id has been set
		if (smearId != null) {
			// smearId != -1 is means "this is a new smear"
			if (smearId != -1) {
				smear = Context.getService(MdrtbService.class).getSmear(smearId);
			}
			
			// create the new smear if needed
			if (smear == null) {
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen(specimenId);
				smear = Context.getService(MdrtbService.class).createSmear(specimen);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return smear;
	}
	
	/**
	 * Returns the culture that should be used to bind a form posting to
	 * 
	 * @param cultureId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("culture")
	public Culture getCulture(@RequestParam(required = false, value="cultureId") Integer cultureId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Culture culture = null;
		
		// only do something here if the culture id has been set
		if (cultureId != null) {
			// cultureId != -1 is means "this is a new culture"
			if (cultureId != -1) {
				culture = Context.getService(MdrtbService.class).getCulture(cultureId);
			}
			
			// create the new culture if needed
			if (culture == null) {
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen(specimenId);
				culture = Context.getService(MdrtbService.class).createCulture(specimen);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return culture;
	}
	
	/**
	 * Returns the dst that should be used to bind a form posting to
	 * 
	 * @param dstId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("dst")
	public Dst getDst(@RequestParam(required = false, value="dstId") Integer dstId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Dst dst = null;
		
		// only do something here if the dst id has been set
		if (dstId != null) {
			// dstId != -1 is means "this is a new dst"
			if (dstId != -1) {
				dst = Context.getService(MdrtbService.class).getDst(dstId);
			}
			
			// create the new dst if needed
			if (dst == null) {
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen(specimenId);
				dst = Context.getService(MdrtbService.class).createDst(specimen);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return dst;
	}
	
	/**
	 * Returns the smear that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("xpert")
	public Xpert getXpert(@RequestParam(required = false, value="xpertId") Integer xpertId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		Xpert xpert = null;
		
		// only do something here if the smear id has been set
		if (xpertId != null) {
			// smearId != -1 is means "this is a new smear"
			if (xpertId != -1) {
				xpert = Context.getService(MdrtbService.class).getXpert(xpertId);
			}
			
			// create the new xpert if needed
			if (xpert == null) {
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen(specimenId);
				xpert = Context.getService(MdrtbService.class).createXpert(specimen);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return xpert;
	}
	
	/**
	 * Returns the smear that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("hain")
	public HAIN getHAIN(@RequestParam(required = false, value="hainId") Integer hainId, @RequestParam(required = false, value="specimenId") Integer specimenId) {
		HAIN hain = null;
		
		// only do something here if the hain id has been set
		if (hainId != null) {
			// hainId != -1 is means "this is a new hain"
			if (hainId != -1) {
				hain = Context.getService(MdrtbService.class).getHAIN(hainId);
			}
			
			// create the new hain if needed
			if (hain == null) {
				Specimen specimen = Context.getService(MdrtbService.class).getSpecimen(specimenId);
				hain = Context.getService(MdrtbService.class).createHAIN(specimen);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return hain;
	}
	
	/**
	 * Returns the specimen that should be used to bind a form posting to
	 * 
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("specimen")
	public Specimen getSpecimen(@RequestParam(required = false, value="specimenId") Integer specimenId) {
		// for now, if no specimen is specified, default to the most recent specimen within the program
		System.out.println("->>>>>>" + specimenId);
		
		if (specimenId != null) {
			return Context.getService(MdrtbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
		}
		else {
			return null;
		}
	}
		
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(@RequestParam(required = false, value = "testId") String testId, ModelMap map) {
		
		// add the testId to the model map if there is one (this is used to make sure the proper detail page is shown after an edit)
		// TODO: unfortunately, this does not currently work since whenever an obs is saved it gets voided and recreated, so this
		// test id is no longer valid
		if (StringUtils.isEmpty(testId)) {
			testId = "-1";
		}
		map.put("testId", testId);
		
		return new ModelAndView("/module/mdrtb/specimen/specimen", map);
	}
	
	
	/** 
	 * Handles the submission of a specimen form
	 * 
	 * @param specimen
	 * @param specimenErrors
	 * @param status
	 * @param request
	 * @param map
	 * @param testId
	 * @param scannedLabReport
	 * @param removeScannedLabReports
	 * @return
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=specimen")
	public ModelAndView processSubmit(@ModelAttribute("specimen") Specimen specimen, BindingResult specimenErrors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = false, value = "testId") Integer testId, 
	                                  @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId,
	                                  @RequestParam(required = false, value = "addScannedLabReport") MultipartFile scannedLabReport,
	                                  @RequestParam(required = false, value = "addScannedLabReportLocation") Location scannedLabReportLocation,
	                                  @RequestParam(required = false, value = "removeScannedLabReport") String [] removeScannedLabReports) {
    	
    	
    	for (Object e : specimenErrors.getFieldErrors()) {
    		FieldError error = (FieldError) e;
    		
    		System.out.println("error = " + error.getCode() + " with message " + error.getDefaultMessage() + " and for field " + error.getField());
    	}
    	
    	
    	
		// validate
    	if(specimen != null) {
    		new SpecimenValidator().validate(specimen, specimenErrors);
    	}
    	
    	// validate that a lab has been supplied if there's a scanned lab report
    	if (scannedLabReport != null && !scannedLabReport.isEmpty() && scannedLabReportLocation == null) {
    		specimenErrors.reject("mdrtb.specimen.errors.noLabForScannedLabReport", "Please specify a lab for this scanned lab report.");
    	}
		
		if (specimenErrors.hasErrors()) {
			map.put("testId", testId);
			map.put("addScannedLabReport", scannedLabReport);
			map.put("addScannedLabReportLocation", scannedLabReportLocation);
			map.put("specimenErrors", specimenErrors);
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
		
		// remove scanned lab reports if necessary
		if (removeScannedLabReports != null) {
			Set<String> removeScannedLabReportSet = new HashSet<String>(Arrays.asList(removeScannedLabReports));
			
			for (ScannedLabReport report : specimen.getScannedLabReports()) {
				if (report.getId() != null && removeScannedLabReportSet.contains(report.getId())) {
					specimen.removeScannedLabReport(report);
					// TODO: we don't actually remove the underlying PDF file--is this a problem?
				}
			}
		}
		
		// save the actual update
		Context.getService(MdrtbService.class).saveSpecimen(specimen);
		
		// (somewhat) hacky way to manually handle the addition of new scanned lab report
		// (this has to happen after the specimen is saved for some reason)
		if (scannedLabReport != null && !scannedLabReport.isEmpty()) {
			// create the new result
			ScannedLabReport report = specimen.addScannedLabReport();
			
			report.setLab(scannedLabReportLocation);
			report.setFile(scannedLabReport);
		
			// need to save this explicitly for the obs handler to pick it up and handle it properly
			Context.getService(MdrtbService.class).saveScannedLabReport(report);
		}
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimen.getId() + "&testId=" + testId + "&patientProgramId=" + patientProgramId, map);
		
	}
    
    /**
     * Handles the submission of a smear form
     *
     * @param smear
     * @param smearErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=smear")
	public ModelAndView processSubmit(@ModelAttribute("smear") Smear smear, BindingResult errors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") Integer specimenId,
	                                  @RequestParam(required = true, value="patientProgramId") Integer patientProgramId) {	
    	
		// validate
    	if(smear != null) {
    		new TestValidator().validate(smear, errors);
    	}
		
    	// if validation fails
		if (errors.hasErrors()) {		
			map.put("testId", smear.getId());
			map.put("testType", smear.getTestType());
			map.put("test", smear);
			map.put("testErrors", errors);

			// override the testTypes parameter; we only want to create the add box for a smear in this case
			Collection<String> testTypes = new LinkedList<String>();
			testTypes.add("smear");
			map.put("testTypes", testTypes);			
			
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
				
		// save the actual update
		Context.getService(MdrtbService.class).saveSmear(smear);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + smear.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
    
    /**
     * Handles the submission of a culture form
     * 
     * @param culture
     * @param cultureErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=culture")
	public ModelAndView processSubmit(@ModelAttribute("culture") Culture culture, BindingResult errors,
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") Integer specimenId,
	                                  @RequestParam(required = true, value="patientProgramId") Integer patientProgramId) {
	                     
    	// validate
    	if(culture != null) {
    		new TestValidator().validate(culture, errors);
    	}
		
    	// if validation fails
		if (errors.hasErrors()) {
			map.put("testId", culture.getId());
			map.put("testType", culture.getTestType());
			map.put("test", culture);
			map.put("testErrors", errors);
			
			// override the testTypes parameter; we only want to create the add box for a culture in this case
			Collection<String> testTypes = new LinkedList<String>();
			testTypes.add("culture");
			map.put("testTypes", testTypes);
			
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
		
		// save the actual update
		Context.getService(MdrtbService.class).saveCulture(culture);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + culture.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
    
    /**
     * Handles the submission of a DST form
     * 
     * @param dst
     * @param dstErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @param removeDstResults
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=dst")
	public ModelAndView processSubmit(@ModelAttribute("dst") Dst dst, BindingResult errors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") Integer specimenId,
	                                  @RequestParam(required = false, value = "testId") Integer testId, 
	                                  @RequestParam(required = false, value = "patientProgramId") Integer patientProgramId, 
	                                  @RequestParam(required = false, value = "removeDstResult") String [] removeDstResults) {
	 
    	// validate
    	if(dst != null) {
    		new TestValidator().validate(dst, errors);
    	}
		
    	// if validation fails
		if (errors.hasErrors()) {
			map.put("testId", dst.getId());
			map.put("testType", dst.getTestType());
			map.put("test", dst);
			map.put("testErrors", errors);
			
			// override the testTypes parameter; we only want to create the add box for a dst in this case
			Collection<String> testTypes = new LinkedList<String>();
			testTypes.add("dst");
			map.put("testTypes", testTypes);					
			
			// hacky way to populate any add data, so that we save it and can redisplay it
			List<String> addDstResultResult = new ArrayList<String>();
			List<String> addDstResultConcentration = new ArrayList<String>();
			List<String> addDstResultColonies = new ArrayList<String>();
			List<String> addDstResultDrug = new ArrayList<String>();
			
			int i = 1;
			while (i<30) {
				addDstResultColonies.add(request.getParameter("addDstResult" + i + ".colonies"));
				addDstResultConcentration.add(request.getParameter("addDstResult" + i + ".concentration"));
				addDstResultResult.add(request.getParameter("addDstResult" + i + ".result"));
				addDstResultDrug.add(request.getParameter("addDstResult" + i + ".drug"));

				i++;
			}

			map.put("addDstResultColonies", addDstResultColonies);
			map.put("addDstResultConcentration", addDstResultConcentration);
			map.put("addDstResultResult", addDstResultResult);
			map.put("addDstResultDrug", addDstResultDrug);
			
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
    	
		// hacky way to manually handle the addition of new dsts
    	// note that we only add dsts that have a result and drug specified
		int i = 1;
		while(i<=30) {
			if(StringUtils.isNotEmpty(request.getParameter("addDstResult" + i + ".result")) 
				&& StringUtils.isNotEmpty(request.getParameter("addDstResult" + i + ".drug")) )  {
				// create the new result
				DstResult dstResult = dst.addResult();
			
				// pull the values from the request
				String colonies = request.getParameter("addDstResult" + i + ".colonies");
				String concentration = request.getParameter("addDstResult" + i + ".concentration");
				String resultType = request.getParameter("addDstResult" + i + ".result");
				String drug = request.getParameter("addDstResult" + i + ".drug");
				
				// assign them if they exist
				if (StringUtils.isNotBlank(colonies)) {
					dstResult.setColonies(Integer.valueOf(colonies));
				}
				if (StringUtils.isNotBlank(concentration)) {
					dstResult.setConcentration(Double.valueOf(concentration));
				}
				// although the DstResult obj should handle it, still a good idea to set the result before the drug because of the wonky way result/drugs are stored
				if (StringUtils.isNotBlank(resultType)) {
					dstResult.setResult(Context.getConceptService().getConcept(Integer.valueOf(resultType)));
				}
				if (StringUtils.isNotBlank(drug)) {
					dstResult.setDrug(Context.getConceptService().getConcept(Integer.valueOf(drug)));
				}
			}
			i++;
		} 
		 
		// remove dst results
		if(removeDstResults != null) {
			Set<String> removeDstResultSet = new HashSet<String>(Arrays.asList(removeDstResults));
			
			for(DstResult result : dst.getResults()) {
				if(result.getId() != null && removeDstResultSet.contains(result.getId())) {
					dst.removeResult(result);
				}
			}
		}
    	
		// save the actual update
		Context.getService(MdrtbService.class).saveDst(dst);
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + dst.getId() + "&patientProgramId=" + patientProgramId, map);	
	}
    
    /**
     * Handles the submission of a xpert form
     *
     * @param xpert
     * @param xpertErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=xpert")
	public ModelAndView processSubmit(@ModelAttribute("xpert") Xpert xpert, BindingResult errors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") Integer specimenId,
	                                  @RequestParam(required = true, value="patientProgramId") Integer patientProgramId) {	
    	
		// validate
    	if(xpert != null) {
    		new TestValidator().validate(xpert, errors);
    	}
		
    	// if validation fails
		if (errors.hasErrors()) {		
			map.put("testId", xpert.getId());
			map.put("testType", xpert.getTestType());
			map.put("test", xpert);
			map.put("testErrors", errors);

			// override the testTypes parameter; we only want to create the add box for a smear in this case
			Collection<String> testTypes = new LinkedList<String>();
			testTypes.add("xpert");
			map.put("testTypes", testTypes);			
			
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
				
		// save the actual update
		Context.getService(MdrtbService.class).saveXpert(xpert);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + xpert.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
    
    /**
     * Handles the submission of a xpert form
     *
     * @param hain
     * @param hainErrors
     * @param status
     * @param request
     * @param map
     * @param specimenId
     * @param testId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=hain")
	public ModelAndView processSubmit(@ModelAttribute("hain") HAIN hain, BindingResult errors, 
	                                  SessionStatus status, HttpServletRequest request, ModelMap map,
	                                  @RequestParam(required = true, value="specimenId") Integer specimenId,
	                                  @RequestParam(required = true, value="patientProgramId") Integer patientProgramId) {	
    	
		// validate
    	if(hain != null) {
    		new TestValidator().validate(hain, errors);
    	}
		
    	// if validation fails
		if (errors.hasErrors()) {		
			map.put("testId", hain.getId());
			map.put("testType", hain.getTestType());
			map.put("test", hain);
			map.put("testErrors", errors);

			// override the testTypes parameter; we only want to create the add box for a smear in this case
			Collection<String> testTypes = new LinkedList<String>();
			testTypes.add("hain");
			map.put("testTypes", testTypes);			
			
			return new ModelAndView("/module/mdrtb/specimen/specimen", map);
		}
				
		// save the actual update
		Context.getService(MdrtbService.class).saveHAIN(hain);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:specimen.form?specimenId=" + specimenId + "&testId=" + hain.getId() + "&patientProgramId=" + patientProgramId, map);
		
	}
}
