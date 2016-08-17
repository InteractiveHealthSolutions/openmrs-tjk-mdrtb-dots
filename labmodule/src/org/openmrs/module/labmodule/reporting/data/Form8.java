package org.openmrs.module.labmodule.reporting.data;

/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.exception.MdrtbAPIException;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * WHO Form 05 Report
 */
public class Form8 implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form8");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form8.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
		
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/Form8" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	

	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		/*Integer quarter = (Integer)parameters.get("quarter");*/
		
		String quarter = (String)parameters.get("quarter");
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, null));
		Map<String,Object>pMap = context.getParameterValues();
		Set<String> keySet = pMap.keySet();
		System.out.println("PARAMS");
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
	        System.out.println(iterator.next());
	 }
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	 @SuppressWarnings("unchecked")
    public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		// Base Cohort is confirmed mdr patients, in program, who started treatment during the quarter, optionally at location
				Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
				
				
				if (location != null) {
					CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);//Cohorts.getLocationFilterTJK(location.getCountyDistrict(), startDate, endDate);
					if (locationFilter != null) {
						//baseCohortDefs.put("location", new Mapped(locationFilter, null));
						report.setBaseCohortDefinition(locationFilter, null);
					}	
				}
				/*CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
				report.setBaseCohortDefinition(baseCohort, null);*/
		
				CohortDefinition dots = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
				CohortDefinition mdr = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
				CohortDefinition hiv = Cohorts.getHivPositiveDuring(startDate, endDate);
				
				CohortDefinition allTB = ReportUtil.getCompositionCohort("OR", dots,mdr);
				
				CohortDefinition age04=ReportUtil.getCompositionCohort("OR", Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 0, 4),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 0, 4));
				CohortDefinition age0514=ReportUtil.getCompositionCohort("OR", Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 5, 14),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 5, 14));
				CohortDefinition age1517=ReportUtil.getCompositionCohort("OR", Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 15, 17),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 5, 17));
				CohortDefinition age1819=ReportUtil.getCompositionCohort("OR", Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 18, 19),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 18,19));
				CohortDefinition age2024=ReportUtil.getCompositionCohort("OR", Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 20, 24),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 20, 24));
				CohortDefinition age2534 = ReportUtil.getCompositionCohort("OR",Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 25, 34),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 25, 34));
				CohortDefinition age3544 = ReportUtil.getCompositionCohort("OR",Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 35, 44),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 35, 44));
				CohortDefinition age4554 = ReportUtil.getCompositionCohort("OR",Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 45, 54),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 45, 54));
				CohortDefinition age5564 = ReportUtil.getCompositionCohort("OR",Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 55, 64),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 55, 64));
				CohortDefinition age65 = ReportUtil.getCompositionCohort("OR",Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 65, 999),Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 65, 999));
	
				
				CohortDefinition men = Cohorts.getMalePatients();
				CohortDefinition women = Cohorts.getFemalePatients();
				
				CohortDefinition pulmonary = Cohorts.getAllPulmonaryDuring(startDate,endDate);
				CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryDuring(startDate,endDate);
				CohortDefinition smearPositive = Cohorts.getAnySmearPositiveDuring(startDate, endDate);
				CohortDefinition pulSmearPositive = ReportUtil.getCompositionCohort("AND", pulmonary,smearPositive);
				
				CohortDefinition haveDiagnosticType = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, null);
				CohortDefinition haveClinicalDiagnosis = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, TbConcepts.TB_CLINICAL_DIAGNOSIS[0]);
				CohortDefinition haveLabDiagnosis = ReportUtil.minus(haveDiagnosticType, haveClinicalDiagnosis);
				
				//New Age filters added by Omar
				
				
				Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
				
				CohortDefinition allNewCases = groups.get("New");
				CohortDefinition allFailures = groups.get("AfterFailure");
				CohortDefinition allDefault = groups.get("AfterDefault");
				CohortDefinition allOthers = groups.get("Other");
				CohortDefinition allTransferred = groups.get("TransferredIn");
				CohortDefinition allRelapses = groups.get("Relapse");
				
				CohortDefinition newOrRelapse=ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses);
				CohortDefinition notNew = ReportUtil.minus(allTB,allNewCases);
				CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",groups.get("AfterDefault"),groups.get("AfterFailure"),groups.get("Other"),groups.get("TransferredIn") ); 
				//CohortDefinition allExceptNew = ReportUtil.getCompositionCohort("OR",allRelapses,allTransferred,allOthers );
			
				Map<String, CohortDefinition> outcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
				
				CohortDefinition transferOut = outcomes.get("TransferredOut");
				CohortDefinition died = outcomes.get("Died");
				CohortDefinition canceled = outcomes.get("Canceled");
				CohortDefinition hospitalizedEver =Cohorts.getEverHospitalizedDuringPeriod(startDate, endDate);
				
				CohortDefinition respiratoryTb = ReportUtil.getCompositionCohort("OR",pulmonary,pulmonary);
				//////////////////////TABLE1:ALL NEW AND RETREATMENT CASES /////////////////////
				CohortCrossTabDataSetDefinition table1 = new CohortCrossTabDataSetDefinition();
		
				
				///ROWS END HERE
				//table1.addRow("allTBCases",ReportUtil.getCompositionCohort("AND", allTB,newOrRelapse),null);
				table1.addRow("allTBCasesMale",ReportUtil.getCompositionCohort("AND",allTB, men,newOrRelapse),null);
				table1.addRow("allTBCasesFemale",ReportUtil.getCompositionCohort("AND",allTB, women,newOrRelapse),null);
				table1.addRow("RespTBCasesMale",ReportUtil.getCompositionCohort("AND",respiratoryTb, men,newOrRelapse),null);
				table1.addRow("RespTBCasesFemale",ReportUtil.getCompositionCohort("AND",respiratoryTb, women,newOrRelapse),null);
				table1.addRow("PulTBCasesMale",ReportUtil.getCompositionCohort("AND",pulmonary, men,newOrRelapse),null);
				table1.addRow("PulTBCasesFemale",ReportUtil.getCompositionCohort("AND",pulmonary, women,newOrRelapse),null);
				/*table1.addRow("bxTBCasesMale",ReportUtil.getCompositionCohort("AND",bx, men),null);
				table1.addRow("bxTBCasesFemale",ReportUtil.getCompositionCohort("AND",bx, women),null);
				table1.addRow("fcTBCasesMale",ReportUtil.getCompositionCohort("AND",fc, men),null);
				table1.addRow("fcTBCasesFemale",ReportUtil.getCompositionCohort("AND",fc, women),null);
				table1.addRow("bxTBCasesMale",ReportUtil.getCompositionCohort("AND",bx, men),null);
				table1.addRow("bxTBCasesFemale",ReportUtil.getCompositionCohort("AND",bx, women),null);
				table1.addRow("nsTBCasesMale",ReportUtil.getCompositionCohort("AND",ns, men),null);
				table1.addRow("nsTBCasesFemale",ReportUtil.getCompositionCohort("AND",ns, women),null);
				table1.addRow("ooTBCasesMale",ReportUtil.minus(ReportUtil.getCompositionCohort("AND", extrapulmonary,men),ReportUtil.getCompositionCohort("AND",ns, men)),null);
				table1.addRow("ooTBCasesMale",ReportUtil.minus(ReportUtil.getCompositionCohort("AND", extrapulmonary,women),ReportUtil.getCompositionCohort("AND",ns, women)),null);
				table1.addRow("bjTBCasesMale",ReportUtil.getCompositionCohort("AND",bj, men),null);
				table1.addRow("bjTBCasesFemale",ReportUtil.getCompositionCohort("AND",bj, women),null);
				table1.addRow("guTBCasesMale",ReportUtil.getCompositionCohort("AND",gu, men),null);
				table1.addRow("guTBCasesFemale",ReportUtil.getCompositionCohort("AND",gu, women),null);
				table1.addRow("plTBCasesMale",ReportUtil.getCompositionCohort("AND",pl, men),null);
				table1.addRow("plTBCasesFemale",ReportUtil.getCompositionCohort("AND",pl, women),null);
				table1.addRow("varTBCasesMale",ReportUtil.getCompositionCohort("AND",var, men),null);
				table1.addRow("varTBCasesFemale",ReportUtil.getCompositionCohort("AND",var, women),null);
				table1.addRow("eyeTBCasesMale",ReportUtil.getCompositionCohort("AND",eye, men),null);
				table1.addRow("eyeTBCasesFemale",ReportUtil.getCompositionCohort("AND",eye, women),null);
				table1.addRow("miliaryTBCasesMale",ReportUtil.getCompositionCohort("AND",miliary, men),null);
				table1.addRow("miliaryTBCasesFemale",ReportUtil.getCompositionCohort("AND",miliary, women),null);*/
				table1.addRow("mdrTBCasesMale",ReportUtil.getCompositionCohort("AND",mdr, men,newOrRelapse),null);
				table1.addRow("mdrTBCasesFemale",ReportUtil.getCompositionCohort("AND",mdr, women,newOrRelapse),null);
				table1.addRow("hivTBCasesMale",ReportUtil.getCompositionCohort("AND",hiv, men,newOrRelapse),null);
				table1.addRow("hivTBCasesFemale",ReportUtil.getCompositionCohort("AND",hiv, women,newOrRelapse),null);
				/*table1.addRow("ruralTBCasesMale",ReportUtil.getCompositionCohort("AND",rural, men),null);
				table1.addRow("ruralTBCasesFemale",ReportUtil.getCompositionCohort("AND",rural, women),null);*/
				/////////////////////////////////////////////////////
				////      			COLUMNS           			////
				///////////////////////////////////////////////////
				table1.addColumn("t1total",ReportUtil.getCompositionCohort("AND", allTB,newOrRelapse),null);
				table1.addColumn("age04",  age04, null);
				
				table1.addColumn("age14",  age0514, null);
				table1.addColumn("age17",  age1517, null);
				
				table1.addColumn("age19",  age1819, null);
				
				table1.addColumn("age24",  age2024, null);
				
				table1.addColumn("age34",  age2534, null);
				
				table1.addColumn("age44",  age3544, null);
				
				table1.addColumn("age54",  age4554, null);
				
				table1.addColumn("age64",  age5564, null);
				
				table1.addColumn("age65",  age65, null);
				
			
				
				report.addDataSetDefinition("table1", table1, null);
				
				//////////////////////TABLE2:PATIENT POPULATION /////////////////////
				
				////////////////////TABLE3: Patient Movement /////////////////////
				CohortCrossTabDataSetDefinition table3 = new CohortCrossTabDataSetDefinition();
				
				//ROWS
				//table3.addRow(group 1 to 2);
				//table3.addRow(group 2 to 1);
				
				table3.addRow("relapses", ReportUtil.getCompositionCohort("AND",allTB,allRelapses),null);
				table3.addRow("transferIn", ReportUtil.getCompositionCohort("AND", allTB,allTransferred),null);
				table3.addRow("transferOut", ReportUtil.getCompositionCohort("AND", allTB,transferOut),null);
				table3.addRow("canceled", ReportUtil.getCompositionCohort("AND", allTB,canceled),null);
				table3.addRow("died", ReportUtil.getCompositionCohort("AND", allTB,died),null);
				table3.addRow("diedNew", ReportUtil.getCompositionCohort("AND", allTB,died,allNewCases),null);
				table3.addRow("diedNotNew", ReportUtil.getCompositionCohort("AND", allTB,died,notNew),null);
				table3.addRow("diedHIV", ReportUtil.getCompositionCohort("AND", allTB,died,hiv),null);
				table3.addRow("diedHospital", ReportUtil.getCompositionCohort("AND", allTB,died,hospitalizedEver),null);
				report.addDataSetDefinition("table3", table3, null);
				
				////////////////////TABLE4: Hospital and sanatorium care to patients  /////////////////////
				CohortCrossTabDataSetDefinition table4 = new CohortCrossTabDataSetDefinition();
				
				table4.addRow("hospitalized",ReportUtil.getCompositionCohort("AND", allTB,hospitalizedEver),null);
				table4.addRow("inHospital",ReportUtil.getCompositionCohort("AND", allTB,hospitalizedEver),null);
				//table4.addRow("inSanitorium",ReportUtil.getCompositionCohort("AND", allTB,hospitalizedEver),null);
				table4.addRow("newOrRelapse",ReportUtil.getCompositionCohort("AND", allTB,newOrRelapse),null);
				table4.addRow("newOrRelapseLd",ReportUtil.getCompositionCohort("AND", allTB,newOrRelapse,haveLabDiagnosis),null);
				table4.addRow("newOrRelapseOther",ReportUtil.getCompositionCohort("AND", allTB,newOrRelapse,haveClinicalDiagnosis),null);
				
				
				table4.addColumn("total",allTB,null);
				table4.addColumn("age014",ReportUtil.getCompositionCohort("OR",age04,age0514),null);
				table4.addColumn("age1517",age1517,null);
				table4.addColumn("age1819",age1819,null);
				
				
				report.addDataSetDefinition("table4", table4, null);
				
////////////////////TABLE5A: Bac-exc  /////////////////////
				
//////////////////TABLE5B: Contacts  /////////////////////
				
//////////////////TABLE6: TB08  /////////////////////
				//TOTAL REGISTERED
				
				//CohortDefinition cured =  Cohorts.getCuredDuringFilter(startDate, endDate);
				CohortDefinition cured =  outcomes.get("Cured");
				CohortDefinition txCompleted = outcomes.get("TreatmentCompleted");
				
				CohortDefinition failed = outcomes.get("Failed");
				CohortDefinition defaulted = outcomes.get("Defaulted");
				CohortDefinition cancelled = outcomes.get("Canceled");
			
				
				
				
				
				
				CohortDefinition haveTxOutcome = Cohorts.getHaveTreatmentOutcome(null, null, null);
				CohortDefinition haveNoTxOutcome = ReportUtil.minus(allTB,haveTxOutcome);
				//sCohortDefinition onSLDWaitingList
				
				
				/*CohortDefinition culturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
				CohortDefinition smearPositive = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
				CohortDefinition bacteriology = ReportUtil.getCompositionCohort("OR", smearPositive, culturePositive);*/
				
				//CohortDefinition diedDuringTreatment =  Cohorts.getDiedDuringFilter(startDate, endDate);
				
				//DIED:TB
				//CohortDefinition tbDied = ReportUtil.getCompositionCohort("AND", allTB,diedDuringTreatment,bacteriology);
				
				//DIED: NON-TB
				//CohortDefinition nonTbDeath = ReportUtil.minus(diedDuringTreatment, tbDied);
				
				//FAILURE
				//CohortDefinition failure = Cohorts.getFailedDuringFilter(startDate, endDate);
				
				//DEFAULTED
				//CohortDefinition defaulted = Cohorts.getDefaultedDuringFilter(startDate, endDate);
				
				//FLD NOT STARTED
				
				
				/*if (location!=null)
				{
					CohortDefinition treatmentStarted = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate,endDate);
					treatmentNotStarted = ReportUtil.minus(allTB, treatmentStarted);
				}*/
				
				CohortDefinition fldTreatmentStarted = Cohorts.getFLDTreatmentStartedFilter(startDate, endDate);
				CohortDefinition sldTreatmentStarted = Cohorts.getSLDTreatmentStartedFilter(startDate, endDate);
				CohortDefinition treatmentNotStarted = ReportUtil.minus(allTB, ReportUtil.getCompositionCohort("OR", fldTreatmentStarted, sldTreatmentStarted));
				//WAITING FOR SLD
				//TODO: NO INFORMATION YET
				
				//CANCELLED
				//TODO: NO INFORMATION YET
				
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(1900, 0, 1, 0, 0, 1);
				//ENROLLED FOR SLD
				//CohortDefinition mdr = Cohorts.getInMdrProgramEverDuring(gc.getTime(), endDate);
				
				
				//New Age filters added by Omar
			
				//Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
				
				
				
				CohortDefinition allDefaults = groups.get("AfterDefault");
			
				
//				CohortDefinition allRelapses= ReportUtil.getCompositionCohort("OR",groups.get("Relapse"),groups.get("AfterDefault"),groups.get("AfterFailure") ); 
//				CohortDefinition allExceptNew = ReportUtil.getCompositionCohort("OR",allRelapses,allTransferred,allOthers );
				//CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",groups.get("AfterDefault"),groups.get("AfterFailure"),groups.get("Other"),groups.get("TransferredIn") );
				//CohortDefinition allRelapses = groups.get("Relapse");
				
				CohortDefinition pulmonaryLabDiagnosis = ReportUtil.getCompositionCohort("AND", pulmonary, haveLabDiagnosis);
				CohortDefinition pulmonaryClinicalDiagnosis = ReportUtil.getCompositionCohort("AND", pulmonary, haveClinicalDiagnosis);
				//////////////////////			TABLE1			/////////////////////
				CohortCrossTabDataSetDefinition table6 = new CohortCrossTabDataSetDefinition();
				
				
				
				//////////////////////////////////////////////
				/////				ROWS				  ///
				////////////////////////////////////////////
				
				//NEW CASES
				table6.addRow("newptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveLabDiagnosis), null);
				table6.addRow("newptbld04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveLabDiagnosis,age04), null);
				table6.addRow("newptbld0514", ReportUtil.getCompositionCohort("AND", haveLabDiagnosis, pulmonary,allNewCases,allTB, age0514),null);
				table6.addRow("newptbld1517", ReportUtil.getCompositionCohort("AND", haveLabDiagnosis, pulmonary,allNewCases, allTB,age1517),null);

				
				table6.addRow("newptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveClinicalDiagnosis), null);
				table6.addRow("newptbsd04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveClinicalDiagnosis,age04), null);
				table6.addRow("newptbsd0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allNewCases,allTB, age0514),null);
				table6.addRow("newptbsd1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allNewCases, allTB,age1517),null);

				table6.addRow("neweptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allNewCases,haveClinicalDiagnosis), null);
				table6.addRow("neweptb04", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allNewCases,haveClinicalDiagnosis,age04), null);
				table6.addRow("neweptb0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allNewCases,allTB, age0514),null);
				table6.addRow("neweptb1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allNewCases, allTB,age1517),null);
				
				table6.addRow("newTotal", ReportUtil.getCompositionCohort("AND", allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("newTotal04", ReportUtil.getCompositionCohort("AND",age04, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("newTotal0514", ReportUtil.getCompositionCohort("AND",age0514, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("newTotal1517", ReportUtil.getCompositionCohort("AND",age1517, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				
				//RELAPSES
				table6.addRow("relapseptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis), null);
				table6.addRow("relapseptbld04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age04), null);
				table6.addRow("relapseptbld0514", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age0514),null);
				table6.addRow("relapseptbld1517", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age1517),null);
				
				table6.addRow("relapseptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveClinicalDiagnosis), null);
				table6.addRow("relapseptbsd04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveClinicalDiagnosis,age04), null);
				table6.addRow("relapseptbsd0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allRelapses,allTB, age0514),null);
				table6.addRow("relapseptbsd1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allRelapses, allTB,age1517),null);

				table6.addRow("relapseeptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allRelapses,haveClinicalDiagnosis), null);
				table6.addRow("relapseeptb04", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allRelapses,haveClinicalDiagnosis,age04), null);
				table6.addRow("relapseeptb0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allRelapses,allTB, age0514),null);
				table6.addRow("relapseeptb1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allRelapses, allTB,age1517),null);
				
				table6.addRow("relapseTotal", ReportUtil.getCompositionCohort("AND", allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("relapseTotal04", ReportUtil.getCompositionCohort("AND",age04, allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("relapseTotal0514", ReportUtil.getCompositionCohort("AND",age0514, allRelapses, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				table6.addRow("relapseTotal1517", ReportUtil.getCompositionCohort("AND",age1517, allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				
				//FAILURE
				table6.addRow("failureptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allFailures,haveLabDiagnosis), null);
				table6.addRow("failureptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allFailures,haveClinicalDiagnosis), null);
				table6.addRow("failureeptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allFailures,haveClinicalDiagnosis), null);
				//table6.addRow("failureTotal", ReportUtil.getCompositionCohort("AND", allTB, allFailures,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				
				//DEFAULT
				table6.addRow("defaultptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allDefaults,haveLabDiagnosis), null);
				table6.addRow("defaultptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allDefaults,haveClinicalDiagnosis), null);
				table6.addRow("defaulteptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allDefaults,haveClinicalDiagnosis), null);
				//table6.addRow("defaultTotal", ReportUtil.getCompositionCohort("AND", allTB, allDefaults,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				
				
				//OTHER
				table6.addRow("otherptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allOthers,haveLabDiagnosis), null);
				table6.addRow("otherptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allOthers,haveClinicalDiagnosis), null);
				table6.addRow("othereptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allOthers,haveClinicalDiagnosis), null);
				//table6.addRow("otherTotal", ReportUtil.getCompositionCohort("AND", allTB, allOthers,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
				
				//TOTAL
				table6.addRow("totalptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveLabDiagnosis),null);
				table6.addRow("totalptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveClinicalDiagnosis),null);
				table6.addRow("totaleptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB),null);
				
				
				/////////////////////////////////////////////////////
				////      			COLUMNS           			////
				///////////////////////////////////////////////////
				table6.addColumn("reg", allTB,null);
				table6.addColumn("eval", ReportUtil.minus(haveTxOutcome, cancelled), null);
				table6.addColumn("cured", cured, null);
				table6.addColumn("tx", txCompleted, null);
				table6.addColumn("tbdeath", died, null);
				table6.addColumn("notbdeath", died, null);
				table6.addColumn("fail", failed, null);
				table6.addColumn("def", defaulted, null);
				table6.addColumn("nofld", ReportUtil.getCompositionCohort("AND",allTB,treatmentNotStarted), null);
				table6.addColumn("nosld", ReportUtil.getCompositionCohort("AND",allTB,ReportUtil.getCompositionCohort("OR", mdr,treatmentNotStarted)), null);
				
				table6.addColumn("coltotal",ReportUtil.getCompositionCohort("OR",allTB,ReportUtil.minus(haveTxOutcome, cancelled),cured,txCompleted,died,failed,defaulted,ReportUtil.getCompositionCohort("AND",allTB,treatmentNotStarted),ReportUtil.getCompositionCohort("AND",ReportUtil.getCompositionCohort("OR", mdr,treatmentNotStarted))),null);
				table6.addColumn("canceled", cancelled,null);
				table6.addColumn("sld", ReportUtil.getCompositionCohort("AND",allTB,sldTreatmentStarted), null);
				
				report.addDataSetDefinition("tbl", table6, null);
		ReportData data;
		try {
			data = Context.getService(ReportDefinitionService.class).evaluate(
					report, context);
		} catch (EvaluationException e) {
			throw new MdrtbAPIException("Unable to evaluate WHO Form 7 report",
					e);
		}
		return data;
	}
}