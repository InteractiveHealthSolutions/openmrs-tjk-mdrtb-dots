package org.openmrs.module.dotsreports.web.controller.reporting;

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
import org.openmrs.module.dotsreports.MdrtbConceptMap;
import org.openmrs.module.dotsreports.MdrtbConstants;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.dotsreports.reporting.TB03Data;
import org.openmrs.module.dotsreports.reporting.TB03Util;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.dotsreports.specimen.Culture;
import org.openmrs.module.dotsreports.specimen.HAIN;
import org.openmrs.module.dotsreports.specimen.Smear;
import org.openmrs.module.dotsreports.specimen.Xpert;
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

public class TB03Controller {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(Concept.class, new ConceptEditor());
        binder.registerCustomEditor(Location.class, new LocationEditor());
    }
        
    
    @RequestMapping(method=RequestMethod.GET, value="/module/dotsreports/reporting/tb03")
    public void showRegimenOptions(ModelMap model) {
    	
    	
    
       
        List<Location> locations = Context.getLocationService().getAllLocations();//ms = (MdrtbDrugForecastService) Context.getService(MdrtbDrugForecastService.class);
       
        //drugSets =  ms.getMdrtbDrugs();
        
       

        model.addAttribute("locations", locations);
      
    	
    }
    
  
    
    
    @RequestMapping(method=RequestMethod.POST, value="/module/dotsreports/reporting/tb03")
    public String doTB03(
    		@RequestParam("location") Location location,
            @RequestParam(value="year", required=false) Integer year,
            @RequestParam(value="quarter", required=false) String quarter,
            ModelMap model) throws EvaluationException {
    	
    	Cohort patients = TbUtil.getDOTSPatientsTJK(null, null, location, null, null, null, null,year,quarter);
    	Map<String, Date> dateMap = ReportUtil.getPeriodDates(year, quarter, null);
		
		Date startDate = (Date)(dateMap.get("startDate"));
		Date endDate = (Date)(dateMap.get("endDate"));
		
		Form tb03Form = Context.getFormService().getForm(MdrtbConstants.TB03_FORM_ID);
		ArrayList<Form> formList = new ArrayList<Form>();
		formList.add(tb03Form);
    	
    	
    	Set<Integer> idSet = patients.getMemberIds();
    	ArrayList<TB03Data> patientSet  = new ArrayList<TB03Data>();
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	
    	ArrayList<Person> patientList = new ArrayList<Person>();
    	ArrayList<Concept> conceptQuestionList = new ArrayList<Concept>();
    	ArrayList<Concept> conceptAnswerList = new ArrayList<Concept>();
    	List<Obs> obsList = null;
    	
    	sdf.applyPattern("dd.MM.yyyy");
    	for (Integer i : idSet) {
    		patientList.clear();
         	conceptQuestionList.clear();
         	conceptAnswerList.clear();
    		
    		TB03Data tb03Data = new TB03Data();
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
    	    Concept q = Context.getService(TbService.class).getConcept(TbConcepts.AGE_AT_DOTS_REGISTRATION);
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setAgeAtTB03Registration(obsList.get(0).getValueNumeric().intValue());
    	    
    	    //TX CENTER FOR IP
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CENTER_FOR_IP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setIntensivePhaseFacility(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //TX CENTER FOR CP
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_CENTER_FOR_CP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setContinuationPhaseFacility(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //DOTS TREATMENT REGIMEN
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.TUBERCULOSIS_PATIENT_CATEGORY);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setTreatmentRegimen(obsList.get(0).getValueCoded().getName().getName());
    	    
    	    //DATE OF TB03 TREATMENT START
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.DOTS_TREATMENT_START_DATE);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setTb03TreatmentStartDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //SITE OF DISEASE (P/EP)
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setSiteOfDisease(obsList.get(0).getValueCoded().getName().getShortName());
    	    
    	  //HIV TEST RESULT
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.RESULT_OF_HIV_TEST);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setHivTestResult(obsList.get(0).getValueCoded().getName().getName());

    	    //DATE OF HIV TEST
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_HIV_TEST);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setHivTestDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //DATE OF ART START
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_ART_TREATMENT_START);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setArtStartDate(sdf.format(obsList.get(0).getValueDatetime()));
    	    
    	  //DATE OF CP START
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.DATE_OF_PCT_TREATMENT_START);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setCpStartDate(sdf.format(obsList.get(0).getValueDatetime()));  
    	    
    	    
    	    
    	    //REGISTRATION GROUP
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_GROUP);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null) {
    	    	tb03Data.setRegGroup(obsList.get(0).getValueCoded().getConceptId());
    	    }
    	    
    	    
    	    //DIAGNOSTIC RESULTS
    	    
    	    //DIAGNOSTIC SMEAR
    	    
    	    Smear diagnosticSmear = TB03Util.getDiagnosticSmear(patient);
    	    if(diagnosticSmear!=null) {
    	    		if(diagnosticSmear.getResult()!=null) 
    	    			tb03Data.setDiagnosticSmearResult(diagnosticSmear.getResult().getName().getShortName());
    	    		if(diagnosticSmear.getResultDate()!=null)
    	    			tb03Data.setDiagnosticSmearDate(sdf.format(diagnosticSmear.getResultDate()));
    	    		
    	    		tb03Data.setDiagnosticSmearTestNumber(diagnosticSmear.getSpecimenId());
    	    }
    	    
    	    
    	    //DIAGNOSTIC XPERT
    	    Xpert firstXpert = TB03Util.getFirstXpert(patient);
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
    	    HAIN firstHAIN = TB03Util.getFirstHAIN(patient);
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
    	    Culture diagnosticCulture  = TB03Util.getDiagnosticCulture(patient);
    	    if(diagnosticCulture!=null) {
    	    	if(diagnosticCulture.getResult()!=null)
    	    		tb03Data.setCultureResult(diagnosticCulture.getResult().getName().getShortName());
    	    	if(diagnosticCulture.getResultDate()!=null)
    	    		tb03Data.setCultureTestDate(sdf.format(diagnosticCulture.getResultDate()));
    	    	tb03Data.setCultureTestNumber(diagnosticCulture.getSpecimenId());
    	    }
    	    
    	    
    	    patientSet.add(tb03Data);
    	    
    	    //DST
    	    
    	    
    	    //DRUG RESISTANCE
    	    //DOTS TREATMENT REGIMEN
    	    q = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANCE_TYPE);
    	    conceptQuestionList.clear();
    	    conceptQuestionList.add(q);
    	    
    	    obsList = Context.getObsService().getObservations(patientList, null, conceptQuestionList, null, null, null, null, null, null, startDate, endDate, false);
    	    if(obsList.size()>0 && obsList.get(0)!=null)
    	    	tb03Data.setDrugResistance(obsList.get(0).getValueCoded().getName().getShortName());
    	    
    	
    	    
    	    //FOLLOW-UP SMEARS
    	    
    	    
    	    //TX OUTCOME
    	    
    	    
    	    
    	   
    	}
    	
    	Integer num = patients.getSize();
    	model.addAttribute("num", num);
    	model.addAttribute("patientSet", patientSet);
        return "/module/dotsreports/reporting/tb03Results";
    }
    
    
  
    
}
