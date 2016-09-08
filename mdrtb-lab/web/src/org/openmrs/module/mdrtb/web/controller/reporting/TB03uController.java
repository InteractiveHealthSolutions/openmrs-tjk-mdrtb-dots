package org.openmrs.module.mdrtb.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConceptMap;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.Oblast;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.reporting.TB03uData;
import org.openmrs.module.mdrtb.reporting.TB03uUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.HAIN;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Xpert;
/*import org.openmrs.module.mdrtbdrugforecast.DrugCount;
import org.openmrs.module.mdrtbdrugforecast.MdrtbDrugStock;
import org.openmrs.module.mdrtbdrugforecast.MdrtbUtil;
import org.openmrs.module.mdrtbdrugforecast.MdrtbConcepts;
import org.openmrs.module.mdrtbdrugforecast.drugneeds.DrugForecastUtil;
import org.openmrs.module.mdrtbdrugforecast.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtbdrugforecast.regimen.Regimen;
import org.openmrs.module.mdrtbdrugforecast.regimen.RegimenUtils;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.reporting.definition.MdrtbDrugForecastTreatmentStartedOnDrugCohortDefinition;
import org.openmrs.module.mdrtbdrugforecast.service.MdrtbDrugForecastService;
import org.openmrs.module.mdrtbdrugforecast.status.TreatmentStatusCalculator;
import org.openmrs.module.mdrtbdrugforecast.web.controller.status.DashboardTreatmentStatusRenderer;*/
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller

public class TB03uController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
        binder.registerCustomEditor(Location.class, new LocationEditor());
    }
        
    
    @RequestMapping(method=RequestMethod.GET, value="/module/dotsreports/reporting/tb03")
    public void showRegimenOptions(ModelMap model) {
    	
    	
    
       
        List<Location> locations = Context.getLocationService().getAllLocations();//ms = (MdrtbDrugForecastService) Context.getService(MdrtbDrugForecastService.class);
        List<Oblast> oblasts = Context.getService(MdrtbService.class).getOblasts();
        //drugSets =  ms.getMdrtbDrugs();
        
       

        model.addAttribute("locations", locations);
        model.addAttribute("oblasts", oblasts);
      
    	
    }
    
  
    
    
    @RequestMapping(method=RequestMethod.POST, value="/module/dotsreports/reporting/tb03")
    public String doTB03(
    		@RequestParam("location") Location location,
    		@RequestParam("oblast") String oblast,
            @RequestParam(value="year", required=true) Integer year,
            @RequestParam(value="quarter", required=false) String quarter,
            @RequestParam(value="month", required=false) String month,
            ModelMap model) throws EvaluationException {
    	
    	Cohort patients = MdrtbUtil.getMdrPatientsTJK(null, null, location, oblast, null, null, null, null,year,quarter,month);
    	Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, month);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
		
		Form tb03Form = Context.getFormService().getForm(MdrtbConstants.TB03_FORM_ID);
		ArrayList<Form> formList = new ArrayList<Form>();
		formList.add(tb03Form);
    	
    	
    	Set<Integer> idSet = patients.getMemberIds();
    	ArrayList<TB03uData> patientSet  = new ArrayList<TB03uData>();
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	
    	ArrayList<Person> patientList = new ArrayList<Person>();
    	ArrayList<Concept> conceptQuestionList = new ArrayList<Concept>();
    	ArrayList<Concept> conceptAnswerList = new ArrayList<Concept>();
    	Integer regimenConceptId = null;
    	Integer codId = null;
    	List<Obs> obsList = null;
    	
    	Concept reg1New = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_NEW);
    	Concept reg1Rtx = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_1_RETREATMENT);
    	
    	sdf.applyPattern("dd.MM.yyyy");
    	for (Integer i : idSet) {
    		patientList.clear();
         	conceptQuestionList.clear();
         	conceptAnswerList.clear();
    		
    		TB03uData tb03Data = new TB03uData();
    		tb03Data.setReg1New(Boolean.FALSE);
    		tb03Data.setReg1Rtx(Boolean.FALSE);
    	    Patient patient = Context.getPatientService().getPatient(i);
    	    if(patient==null) {
    	    	continue;
    	    	
    	    }
    	    	
    	    
    	    
    	    patientList.add(patient);
    	    
    	    
    	    tb03Data.setPatient(patient);
    	    
    	    //PATIENT IDENTIFIER
    	    tb03Data.setIdentifier(patient.getActiveIdentifiers().get(0).toString());
    	    
    	    //DATE OF TB03 REGISTRATION
    	    List<Encounter> tb03EncList = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, formList, null, null, false);
    	    Date encDate = null;
    	    if(tb03EncList.size() > 0 && tb03EncList.get(0)!=null) {
    	    	encDate = tb03EncList.get(0).getEncounterDatetime();
    	    	tb03Data.setTb03RegistrationDate(sdf.format(encDate));
    	    	
    	    }
    	   
    	    
    	    //FORMATTED DATE OF BIRTH
    	    tb03Data.setDateOfBirth(sdf.format(patient.getBirthdate()));
    	    
    	    //AGE AT TB03 Registration
    	    Concept q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AGE_AT_DOTS_REGISTRATION);
    	    
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setAgeAtTB03Registration(obsList.get(0).getValueNumeric().intValue());
    	    
    	    //TX CENTER FOR IP
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_IP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setIntensivePhaseFacility(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //TX CENTER FOR CP
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_CENTER_FOR_CP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setContinuationPhaseFacility(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //DOTS TREATMENT REGIMEN
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setTreatmentRegimen(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //DATE OF TB03 TREATMENT START
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TREATMENT_START_DATE);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setTb03TreatmentStartDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //SITE OF DISEASE (P/EP)
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setSiteOfDisease(obsList.get(0).getValueCoded().getName().getShortName());
    	    
    	  //HIV TEST RESULT
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setHivTestResult(obsList.get(0).getValueCoded().getName().getName());

    	    //DATE OF HIV TEST
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_HIV_TEST);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setHivTestDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //DATE OF ART START
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_ART_TREATMENT_START);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setArtStartDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //DATE OF CP START
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_PCT_TREATMENT_START);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setCpStartDate(sdf.format(obsList.get(0).getValueDatetime()));  
    	    
    	    
    	    
    	    //REGISTRATION GROUP
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_GROUP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null) {
    	    	tb03Data.setRegGroup(obsList.get(0).getValueCoded().getConceptId());
    	    }
    	    
    	    
    	    //DIAGNOSTIC RESULTS
    	    
    	    //DIAGNOSTIC SMEAR
    	    
    	    Smear diagnosticSmear = TB03uUtil.getDiagnosticSmear(patient);
    	    if(diagnosticSmear!=null) {
    	    		if(diagnosticSmear.getResult()!=null) 
    	    			tb03Data.setDiagnosticSmearResult(diagnosticSmear.getResult().getName().getShortName());
    	    		if(diagnosticSmear.getResultDate()!=null)
    	    			tb03Data.setDiagnosticSmearDate(sdf.format(diagnosticSmear.getResultDate()));
    	    		
    	    		tb03Data.setDiagnosticSmearTestNumber(diagnosticSmear.getSpecimenId());
    	    }
    	    
    	    
    	    //DIAGNOSTIC XPERT
    	    Xpert firstXpert = TB03uUtil.getFirstXpert(patient);
    	    if(firstXpert!=null) {
    	    	if(firstXpert.getResult()!=null)
    	    		tb03Data.setXpertMTBResult(firstXpert.getResult().getName().getShortName());
    	    	if(firstXpert.getRifResistance()!=null)
    	    		tb03Data.setXpertRIFResult("; R: " + firstXpert.getRifResistance().getName().getShortName());
    	    	if(firstXpert.getResultDate()!=null)
    	    		tb03Data.setXpertTestDate(sdf.format(firstXpert.getResultDate()));
    	    	
    	    	tb03Data.setXpertTestNumber(firstXpert.getSpecimenId());
    	    }
    	    
    	    
    	    
    	    //DIAGNOSTIC HAIN
    	    HAIN firstHAIN = TB03uUtil.getFirstHAIN(patient);
    	    if(firstHAIN!=null) {
    	    	if(firstHAIN.getResult()!=null)
    	    		tb03Data.setHainMTBResult(firstHAIN.getResult().getName().getShortName());
    	    	if(firstHAIN.getRifResistance()!=null)
    	    		tb03Data.setHainRIFResult(firstHAIN.getRifResistance().getName().getShortName());
    	    	if(firstHAIN.getInhResistance()!=null)
    	    		tb03Data.setHainINHResult(firstHAIN.getInhResistance().getName().getShortName());
    	    	if(firstHAIN.getResultDate()!=null)
    	    		tb03Data.setHainTestDate(sdf.format(firstHAIN.getResultDate()));
    	    	
    	    	tb03Data.setHainTestNumber(firstHAIN.getSpecimenId());
    	    }
    	    
    	    //DIAGNOSTIC CULTURE
    	    Culture diagnosticCulture  = TB03uUtil.getDiagnosticCulture(patient);
    	    if(diagnosticCulture!=null) {
    	    	if(diagnosticCulture.getResult()!=null)
    	    		tb03Data.setCultureResult(diagnosticCulture.getResult().getName().getShortName());
    	    	if(diagnosticCulture.getResultDate()!=null)
    	    		tb03Data.setCultureTestDate(sdf.format(diagnosticCulture.getResultDate()));
    	    	tb03Data.setCultureTestNumber(diagnosticCulture.getSpecimenId());
    	    }
    	    
    	    
    	   
    	    
    	    //DST
    	    Dst firstDst = TB03uUtil.getDiagnosticDST(patient);
    	    
    	    if(firstDst!=null) {
    	    	if(firstDst.getDateCollected()!=null)
    	    		tb03Data.setDstCollectionDate(sdf.format(firstDst.getDateCollected()));
    	    	if(firstDst.getResultDate()!=null)
    	    		tb03Data.setDstResultDate(sdf.format(firstDst.getResultDate()));
    	    	List<DstResult> resList = firstDst.getResults();
    	    	String drugName = null;
    	    	String result = null;
    	    	for(DstResult res : resList)
    	    	{
    	    		if(res.getDrug()!=null) {
    	    			drugName = res.getDrug().getShortestName(Context.getLocale(), false).toString();
    	    			result = res.getResult().getName().getShortName();
    	    			tb03Data.getDstResults().put(drugName,result);
    	    			//System.out.println(drugName + "-" + result + " | " + res.getResult());
    	    			
    	    		}
    	    	}
    	    	System.out.println("-------");
    	    }
    	    
    	    
    	    
    	    //DRUG RESISTANCE
    	    
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANCE_TYPE);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setDrugResistance(obsList.get(0).getValueCoded().getName().getShortName());
    	    
    	
    	    
    	    //FOLLOW-UP SMEARS
    	    
    	    //first check patient regimen
    	    Smear followupSmear = null;
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOSIS_PATIENT_CATEGORY);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	regimenConceptId = obsList.get(0).getValueCoded().getConceptId();
    	    
    	    //accordingly look for smears
    	    if(regimenConceptId!=null) {
    	    	if(regimenConceptId.equals(reg1New.getConceptId())) {
    	    		
    	    		tb03Data.setReg1New(Boolean.TRUE);
    	    		
    	    		followupSmear = TB03uUtil.getFollowupSmear(patient, 2);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth2SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth2SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth2TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 3);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth3SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 5);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth5SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 6);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth6SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth6SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth6TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	}
    	    	
    	    	else if(regimenConceptId.equals(reg1Rtx.getConceptId())) {
    	    		tb03Data.setReg1Rtx(Boolean.TRUE);
    	    		 followupSmear = TB03uUtil.getFollowupSmear(patient, 3);
     	    	    if(followupSmear!=null) {
     	    	    		if(followupSmear.getResult()!=null) 
     	    	    			tb03Data.setMonth3SmearResult(followupSmear.getResult().getName().getShortName());
     	    	    		if(followupSmear.getResultDate()!=null)
     	    	    			tb03Data.setMonth3SmearDate(sdf.format(followupSmear.getResultDate()));
     	    	    		
     	    	    		tb03Data.setMonth3TestNumber(followupSmear.getSpecimenId());
     	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 4);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth4SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth4SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth4TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 5);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth5SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth5SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth5TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	    
    	    	    followupSmear = TB03uUtil.getFollowupSmear(patient, 8);
    	    	    if(followupSmear!=null) {
    	    	    		if(followupSmear.getResult()!=null) 
    	    	    			tb03Data.setMonth8SmearResult(followupSmear.getResult().getName().getShortName());
    	    	    		if(followupSmear.getResultDate()!=null)
    	    	    			tb03Data.setMonth8SmearDate(sdf.format(followupSmear.getResultDate()));
    	    	    		
    	    	    		tb03Data.setMonth8TestNumber(followupSmear.getSpecimenId());
    	    	    }
    	    	}
    	    }
    	    
    	    //TX OUTCOME
    	    //CHECK CAUSE OF DEATH
    	   
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    {	
    	    	codId = obsList.get(0).getValueCoded().getConceptId();
    	    	if(codId.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId()))
    	    		tb03Data.setDiedOfTB(true);
    	    	else
    	    		tb03Data.setDiedOfTB(false);
    	    }
    	    
    	    else
	    		tb03Data.setDiedOfTB(false);
    	    
    	    
    	    	
    	    
    	    
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null) {
    	    	tb03Data.setTb03TreatmentOutcome(obsList.get(0).getValueCoded().getConceptId());
    	    }
    	    
    	    //NOTES
    	    
    	    q = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setNotes(obsList.get(0).getValueText());
    	    
    	    patientSet.add(tb03Data);
    	    
    	    regimenConceptId = null;
        	codId = null;
        	obsList = null;
    	   
    	}
    	
    	Integer num = patients.getSize();
    	model.addAttribute("num", num);
    	model.addAttribute("patientSet", patientSet);
        return "/module/dotsreports/reporting/tb03Results";
    }
    
    
  
    
}
