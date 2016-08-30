package org.openmrs.module.labmodule.web.controller.specimen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Bacteriology;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst;
import org.openmrs.module.labmodule.specimen.DstResult;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.ScannedLabReport;
import org.openmrs.module.labmodule.specimen.Smear;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.SpecimenValidator;
import org.openmrs.module.labmodule.specimen.TestValidator;
import org.openmrs.module.labmodule.specimen.Xpert;
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
@RequestMapping("/module/labmodule/lab/labEntry.form")
public class LabSpecimenController extends AbstractSpecimenController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Returns the default list of drugs to display in the DST add test results box
	 */
	@ModelAttribute("defaultDstDrugs")
	public List<List<Object>> getDefaultDstDrugs() {
		return TbUtil.getDefaultDstDrugs();
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
				smear = Context.getService(TbService.class).getSmear(smearId);
			}
			
			// create the new smear if needed
			if (smear == null) {
				LabResult labresult = Context.getService(TbService.class).getLabResult(specimenId);
				smear = Context.getService(TbService.class).createSmear(labresult);
			}
		}
				
		// it's okay if we return null here, as this attribute is only used on a post
		return smear;
	}
	
	
	/**
	 * Returns the microscopy that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("microscopy")
	public Microscopy getMicroscopy(@RequestParam(required = false, value="microscopyId") Integer microscopyId, @RequestParam(required = false, value="labResultId") Integer labResultId) {
		Microscopy microscopy = null;
	
		if(microscopyId != null){
		
			// create the new microscopy if needed
			if (labResultId != null) {
				LabResult labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				if(microscopyId == 0)
					microscopy = Context.getService(TbService.class).createMicroscopy(labResult);
				else{
					List<Microscopy> microscopyList = labResult.getMicroscopies();
					for(Microscopy m : microscopyList){
						if(Integer.parseInt(m.getId()) == microscopyId)
					    	Context.getService(TbService.class).deleteTest(Integer.parseInt(m.getId()));
					}
					microscopy = Context.getService(TbService.class).createMicroscopy(labResult);
				}
			}
			
		}
		
		// it's okay if we return null here, as this attribute is only used on a post
		return microscopy;
	}
	
	/**
	 * Returns the hain2 that should be used to bind a form posting to
	 * 
	 * @param smearId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("hain2")
	public HAIN2 getHAIN2(@RequestParam(required = false, value="hain2Id") Integer hain2Id, @RequestParam(required = false, value="labResultId") Integer labResultId) {
		HAIN2 hain2 = null;
	
		if(hain2Id != null){
			
			// create the new hain2 if needed
			if (labResultId != null) {
				LabResult labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				if(hain2Id == 0)
					hain2 = Context.getService(TbService.class).createHAIN2(labResult);
				else{
					List<HAIN2> hainList = labResult.getHAINS2();
					for(HAIN2 h : hainList){
						
						if(Integer.parseInt(h.getId()) == hain2Id)
					    	Context.getService(TbService.class).deleteTest(Integer.parseInt(h.getId()));
						
					}
					hain2 = Context.getService(TbService.class).createHAIN2(labResult);

				}
			}
			
		}	
				
		// it's okay if we return null here, as this attribute is only used on a post
		return hain2;
	}
	
	
	/**
	 * Returns the culture that should be used to bind a form posting to
	 * 
	 * @param cultureId
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("culture")
	public Culture getCulture(@RequestParam(required = false, value="cultureId") Integer cultureId, @RequestParam(required = false, value="labResultId") Integer labResultId) {
		Culture culture = null;
		
		if(cultureId != null){
			
			// create the new smear if needed
			if (labResultId != null) {
				LabResult labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				if(cultureId == 0)
					culture = Context.getService(TbService.class).createCulture(labResult);
				else{
					List<Culture> culturesList = labResult.getCultures();
					for(Culture c : culturesList){
						
						if(Integer.parseInt(c.getId()) == cultureId)
					    	Context.getService(TbService.class).deleteTest(Integer.parseInt(c.getId()));
						
					}
					culture = Context.getService(TbService.class).createCulture(labResult);

				}
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
				dst = Context.getService(TbService.class).getDst(dstId);
			}
			
			// create the new dst if needed
			if (dst == null) {
				Specimen specimen = Context.getService(TbService.class).getSpecimen(specimenId);
				dst = Context.getService(TbService.class).createDst(specimen);
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
	public Xpert getXpert(@RequestParam(required = false, value="xpertId") Integer xpertId, @RequestParam(required = false, value="labResultId") Integer labResultId) {
		Xpert xpert = null;
	
		if(xpertId != null){
		
			// create the new smear if needed
			if (labResultId != null) {
				LabResult labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				if(xpertId == 0)
					xpert = Context.getService(TbService.class).createXpert(labResult);
				else{
					List<Xpert> xpertList = labResult.getXperts();
					for(Xpert x : xpertList){
						if(Integer.parseInt(x.getId()) == xpertId)
					    	Context.getService(TbService.class).deleteTest(Integer.parseInt(x.getId()));
					}
					xpert = Context.getService(TbService.class).createXpert(labResult);
				}
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
	public HAIN getHAIN(@RequestParam(required = false, value="hainId") Integer hainId, @RequestParam(required = false, value="labResultId") Integer labResultId) {
		HAIN hain = null;
		
		if(hainId != null){
			
			// create the new smear if needed
			if (labResultId != null) {
				LabResult labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				if(hainId == 0)
					hain = Context.getService(TbService.class).createHAIN(labResult);
				else{
					List<HAIN> hainList = labResult.getHAINS();
					for(HAIN h : hainList){
						if(Integer.parseInt(h.getId()) == hainId)
					    	Context.getService(TbService.class).deleteTest(Integer.parseInt(h.getId()));
					}
					hain = Context.getService(TbService.class).createHAIN(labResult);
				}
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
			return Context.getService(TbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
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
		
		return new ModelAndView("/module/labmodule/lab/labEntry", map);
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=specimen")
	public ModelAndView processSubmit(@RequestParam(required = true, value = "patientId") Integer patientId,
			                          @RequestParam(required = false, value = "lab") Location lab,
			                          @RequestParam(required = false, value = "provider") Person provider,
			                          @RequestParam(required = false, value = "lab_e") Location labE,
			                          @RequestParam(required = false, value = "provider_e") Person providerE,
			                          @RequestParam(required = false, value = "labResultId") Integer labResultId,
			                              HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {
			
			String labNumber;
			String dateRecieve;
			String dateInvestigation;
			String requestingLabName;
			String investigationPurpose;
			String biologicalSpecimen;
			String peripheral[];
			String peripheralLabNo;
			String microscopyResult;
			String dateResult;
			String dateSampleSentToBacteriologicalLab;
			String resultOfInvestigationOfBacteriologicalLab;
			String comments;
			Location location;
			Person person;
			
			LabResult labResult = null;
			
			if(labResultId != null){
				
				labResult = Context.getService(TbService.class).getLabResult(labResultId);
				
				// Get all form Data..
				labNumber = request.getParameter("labNumber_e");
				dateRecieve = request.getParameter("dateRecieve_e");
				dateInvestigation = request.getParameter("dateInvestigation_e");
				requestingLabName = request.getParameter("requestingLabName_e");
				investigationPurpose = request.getParameter("investigationPurpose_e");
				biologicalSpecimen = request.getParameter("biologicalSpecimen_e");
				peripheral = request.getParameterValues("peripheral_e");
				peripheralLabNo = request.getParameter("peripheralLabNo_e");
				microscopyResult = request.getParameter("microscopyResult_e");
				dateResult = request.getParameter("dateResult_e");
				dateSampleSentToBacteriologicalLab = request.getParameter("dateSampleSentToBacteriologicalLab_e");
				resultOfInvestigationOfBacteriologicalLab = request.getParameter("resultOfInvestigationOfBacteriologicalLab_e");
				comments = request.getParameter("comments_e");
				
				location = labE;
				person = providerE;
			}
			
			else{
				
				labResult = Context.getService(TbService.class).createLabResult(Context.getPatientService().getPatient(patientId));
				
				// Get all form Data..
				labNumber = request.getParameter("labNumber");
				dateRecieve = request.getParameter("dateRecieve");
				dateInvestigation = request.getParameter("dateInvestigation");
				requestingLabName = request.getParameter("requestingLabName");
				investigationPurpose = request.getParameter("investigationPurpose");
				biologicalSpecimen = request.getParameter("biologicalSpecimen");
				peripheral = request.getParameterValues("peripheral");
				peripheralLabNo = request.getParameter("peripheralLabNo");
				microscopyResult = request.getParameter("microscopyResult");
				dateResult = request.getParameter("dateResult");
				dateSampleSentToBacteriologicalLab = request.getParameter("dateSampleSentToBacteriologicalLab");
				resultOfInvestigationOfBacteriologicalLab = request.getParameter("resultOfInvestigationOfBacteriologicalLab");
				comments = request.getParameter("comments");
				
				location = lab;
				person = provider;

			}

			SimpleDateFormat dateFormat = Context.getDateFormat();
	    	dateFormat.setLenient(false);
			
			// add the lab result-specific parameters
	    	if (StringUtils.isNotBlank(labNumber)) {
	    		labResult.setLabNumber(labNumber);		
			}
			if (StringUtils.isNotBlank(dateInvestigation)) {
				labResult.setInvestigationDate(dateFormat.parse(dateInvestigation));
			}
			if (StringUtils.isNotBlank(requestingLabName)) {
				labResult.setRequestingLabName(Context.getConceptService().getConcept(Integer.valueOf(requestingLabName)));
			}
			if (StringUtils.isNotBlank(investigationPurpose)) {
				labResult.setInvestigationPurpose(Context.getConceptService().getConcept(Integer.valueOf(investigationPurpose)));
			}
			if (StringUtils.isNotBlank(biologicalSpecimen)) {
				labResult.setBiologicalSpecimen(Context.getConceptService().getConcept(Integer.valueOf(biologicalSpecimen)));
			}
			if(peripheral !=  null){
				
					if (StringUtils.isNotBlank(peripheralLabNo)) {
						labResult.setPeripheralLabNumber(peripheralLabNo);
					}
					else{
						labResult.setPeripheralLabNumber(null);
					}
					if (StringUtils.isNotBlank(microscopyResult)) {
						labResult.setMicroscopyResult(Context.getConceptService().getConcept(Integer.valueOf(microscopyResult)));
					}
					else{
						labResult.setMicroscopyResult(null);
					}
					if (StringUtils.isNotBlank(dateResult)) {
						labResult.setDateResult(dateFormat.parse(dateResult));
					}
					else{
						labResult.setDateResult(null);
					}
				
			}
			if (StringUtils.isNotBlank(dateSampleSentToBacteriologicalLab)) {
				labResult.setDateSampleSentToBacteriologicalLab(dateFormat.parse(dateSampleSentToBacteriologicalLab));
			}
			if (StringUtils.isNotBlank(resultOfInvestigationOfBacteriologicalLab)) {
				labResult.setResultOfInvestigationOfBacteriologicalLab(resultOfInvestigationOfBacteriologicalLab);
			}
			if (StringUtils.isNotBlank(comments)) {
				labResult.setComments(comments);
			}
			
			// add the specimen-specific parameters
			labResult.setLocation(location);
			labResult.setProvider(person);
			labResult.setDateCollected(dateFormat.parse(dateRecieve));
	    	
	    	int id = Context.getService(TbService.class).saveLabResult(labResult);
	    		    	
	    	// clears the command object from the session
			status.setComplete();
			map.clear();
			
			// redirect to the overview page
			return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
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
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=xpert")
	public ModelAndView processSubmit(@ModelAttribute("xpert") Xpert xpert, BindingResult errors,
									  @RequestParam(required = true, value = "patientId") Integer patientId,
            						  @RequestParam(required = true, value = "lab") Location lab,
            						  @RequestParam(required = true, value = "provider") Person provider,
            						  HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
    	String xpertTestDate = "";
    	String mtbResult = "";
    	String rifResult = "";
    	String xpertError = "";
    	
    	if(request.getParameter("xpertTestDate") != null){
	    	xpertTestDate = request.getParameter("xpertTestDate");
	    	mtbResult = request.getParameter("mtbResult");
	    	rifResult = request.getParameter("rifResult");
	    	xpertError = request.getParameter("xpertError");
    	}
    	else{
    		String count = request.getParameter("count");
    		xpertTestDate = request.getParameter("xpertTestDate_"+count);
	    	mtbResult = request.getParameter("mtbResult_"+count);
	    	rifResult = request.getParameter("rifResult_"+count);
	    	xpertError = request.getParameter("xpertError_"+count);
    	}
    	
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	    	
    	if (StringUtils.isNotBlank(xpertTestDate)) {
    		xpert.setResultDate(dateFormat.parse(xpertTestDate));
		}
    	if (StringUtils.isNotBlank(mtbResult)) {
    		xpert.setMtbBurden(Context.getConceptService().getConcept(Integer.valueOf(mtbResult)));
		}
    	if (StringUtils.isNotBlank(rifResult)) {
    		xpert.setRifResistance(Context.getConceptService().getConcept(Integer.valueOf(rifResult)));
		}
    	if (StringUtils.isNotBlank(xpertError)) {
    		xpert.setErrorCode(xpertError);
		}
    	
		// save the actual update
		int id = Context.getService(TbService.class).saveXpert(xpert);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
		
	}
	
    
    /**
     * Handles the submission of a Microscopy form
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
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=microscopy")
	public ModelAndView processSubmit(@ModelAttribute("microscopy") Microscopy microscopy, BindingResult errors,
									  @RequestParam(required = true, value = "patientId") Integer patientId,
            						  @RequestParam(required = true, value = "lab") Location lab,
            						  @RequestParam(required = true, value = "provider") Person provider,
            						  HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
    	
    	int id = 0;
    	String sample1Date = "";
    	String sample1Appearance = "";
    	String sample1Result = "";
    	
    	if(request.getParameter("sampleDate") != null){
    	
	    	sample1Date = request.getParameter("sampleDate");
	    	sample1Appearance = request.getParameter("sampleAppearance");
	    	sample1Result = request.getParameter("sampleResult");
	   
    	}
    	else{
   
    		String count = request.getParameter("count");
    		sample1Date = request.getParameter("sampleDate_"+count);
	    	sample1Appearance = request.getParameter("sampleAppearance_"+count);
	    	sample1Result = request.getParameter("sampleResult_"+count);
	  	
    	}
    	
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	
    	if (StringUtils.isNotBlank(sample1Date)) {
    		microscopy.setResultDate(dateFormat.parse(sample1Date));
		}
    	if (StringUtils.isNotBlank(sample1Appearance)) {
    		microscopy.setSampleApperence(Context.getConceptService().getConcept(Integer.valueOf(sample1Appearance)));
		}
    	if (StringUtils.isNotBlank(sample1Result)) {
    		microscopy.setSampleResult(Context.getConceptService().getConcept(Integer.valueOf(sample1Result)));
		}
    	
    	// save the actual update
    	id = Context.getService(TbService.class).saveMicroscopy(microscopy);
    	
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
		
	}
    
    
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=deleteTest")
	public ModelAndView processSubmit(@RequestParam(required = true, value = "patientId") Integer patientId,
            						  HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
        	
    	
    	String id = request.getParameter("id");
    	
		Context.getService(TbService.class).deleteTest(Integer.parseInt(id));
		
		String testId = request.getParameter("labResultId");
		Integer labResultId = Integer.parseInt(testId);
    	
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + labResultId);
		
	}
    
   
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=delete")
	public ModelAndView processSubmit(@RequestParam(required = true, value = "patientId") Integer patientId,
									  @RequestParam(required = true, value = "labResultId") Integer labResultId,
									  @RequestParam(required = true, value = "testType") String testType,
            						  HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
        	
    	
    	String id = request.getParameter("id");
    	
    	
    	if(testType == null){
    		Context.getService(TbService.class).deleteTest(Integer.parseInt(id));
    		String testId = request.getParameter("labResultId");
    		labResultId = Integer.parseInt(testId);
    	}	
    	else if(testType.equals("labResult")){
    		Context.getService(TbService.class).deleteSpecimen(labResultId);
    		
    		status.setComplete();
    		map.clear();
    		
    		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId);
    		
    		
    	}
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + labResultId);
		
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
										@RequestParam(required = true, value = "patientId") Integer patientId,
										@RequestParam(required = true, value = "lab") Location lab,
										@RequestParam(required = true, value = "provider") Person provider,
										HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {
	     
    	String cultureDate = "";
    	String cultureResult = "";
    	
    	if(request.getParameter("cultureTestDate") != null){
	        cultureDate = request.getParameter("cultureTestDate");
	    	cultureResult = request.getParameter("cultureResult");
    	}
    	else{
    		String count = request.getParameter("count");
    		cultureDate = request.getParameter("cultureTestDate_"+count);
	    	cultureResult = request.getParameter("cultureResult_"+count);
    	}
    	
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	
    	if (StringUtils.isNotBlank(cultureDate)) {
    		culture.setResultDate(dateFormat.parse(cultureDate));
		}
    	if (StringUtils.isNotBlank(cultureResult)) {
    		culture.setResult(Context.getConceptService().getConcept(Integer.valueOf(cultureResult)));
		}
    	
		// save the actual update
		int id = Context.getService(TbService.class).saveCulture(culture);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
    	
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
			
			return new ModelAndView("/module/labmodule/lab/labEntry", map);
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
		Context.getService(TbService.class).saveDst(dst);
		
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?specimenId=" + specimenId + "&testId=" + dst.getId() + "&patientProgramId=" + patientProgramId, map);	
	}
    
    /**
     * Handles the submission of a hain form
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
			  							@RequestParam(required = true, value = "patientId") Integer patientId,
									    @RequestParam(required = true, value = "lab") Location lab,
									    @RequestParam(required = true, value = "provider") Person provider,
									    HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
    	
    	String hainTestDate = "";
    	String mtbResult = ""; 
    	String rifResult = "";
    	String inhResult = "";
    	
    	if(request.getParameter("hainTestDate") != null){
	    	hainTestDate = request.getParameter("hainTestDate");
	    	mtbResult = request.getParameter("mtbResult");
	    	rifResult = request.getParameter("rifResult");
	    	inhResult = request.getParameter("inhResult");
    	}
    	else{
    		String count = request.getParameter("count");
    		hainTestDate = request.getParameter("hainTestDate_"+count);
	    	mtbResult = request.getParameter("mtbResult_"+count);
	    	rifResult = request.getParameter("rifResult_"+count);
	    	inhResult = request.getParameter("inhResult_"+count);
    	}
    	
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	    	
    	if (StringUtils.isNotBlank(hainTestDate)) {
    		hain.setResultDate(dateFormat.parse(hainTestDate));
		}
    	if (StringUtils.isNotBlank(mtbResult)) {
    		hain.setMtbBurden(Context.getConceptService().getConcept(Integer.valueOf(mtbResult)));
		}
    	if (StringUtils.isNotBlank(rifResult)) {
    		hain.setRifResistance(Context.getConceptService().getConcept(Integer.valueOf(rifResult)));
		}
    	if (StringUtils.isNotBlank(inhResult)) {
    		hain.setInhResistance(Context.getConceptService().getConcept(Integer.valueOf(inhResult)));
		}
    	
		// save the actual update
		int id = Context.getService(TbService.class).saveHAIN(hain);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
		
	}
    
    
    /**
     * Handles the submission of a hain form
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
    @RequestMapping(method = RequestMethod.POST, params = "submissionType=hain2")
	public ModelAndView processSubmit(@ModelAttribute("hain2") HAIN2 hain, BindingResult errors,
			  							@RequestParam(required = true, value = "patientId") Integer patientId,
									    @RequestParam(required = true, value = "lab") Location lab,
									    @RequestParam(required = true, value = "provider") Person provider,
									    HttpServletRequest request, ModelMap map, SessionStatus status) throws ParseException {	
    	
    	String hainTestDate = "";
    	String mtbResult = "";
    	String moxResult = "";
    	String cmResult = "";
    	String erResult  = "";
    	
    	if(request.getParameter("hain2TestDate") != null){
	    	hainTestDate = request.getParameter("hain2TestDate");
	    	mtbResult = request.getParameter("mtbResult");
	    	moxResult = request.getParameter("moxResult");
	    	cmResult = request.getParameter("cmResult");
	    	erResult  = request.getParameter("erResult");
    	}
    	else{
    		String count = request.getParameter("count");
    		hainTestDate = request.getParameter("hain2TestDate_"+count);
	    	mtbResult = request.getParameter("mtbResult_"+count);
	    	moxResult = request.getParameter("moxResult_"+count);
	    	cmResult = request.getParameter("cmResult_"+count);
	    	erResult  = request.getParameter("erResult_"+count);
    	}
    	
    	SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	    	
    	if (StringUtils.isNotBlank(hainTestDate)) {
    		hain.setResultDate(dateFormat.parse(hainTestDate));
		}
    	if (StringUtils.isNotBlank(mtbResult)) {
    		hain.setMtbBurden(Context.getConceptService().getConcept(Integer.valueOf(mtbResult)));
		}
    	if (StringUtils.isNotBlank(moxResult)) {
    		hain.setMoxResistance(Context.getConceptService().getConcept(Integer.valueOf(moxResult)));
		}
    	if (StringUtils.isNotBlank(cmResult)) {
    		hain.setCmResistance(Context.getConceptService().getConcept(Integer.valueOf(cmResult)));
		}
    	if (StringUtils.isNotBlank(erResult)) {
    		hain.setErResistance(Context.getConceptService().getConcept(Integer.valueOf(erResult)));
		}
    	
		// save the actual update
		int id = Context.getService(TbService.class).saveHAIN2(hain);
			
		// clears the command object from the session
		status.setComplete();
		map.clear();
		
		return new ModelAndView("redirect:labEntry.form?patientId=" + patientId + "&labResultId=" + id);
		
	}
}
