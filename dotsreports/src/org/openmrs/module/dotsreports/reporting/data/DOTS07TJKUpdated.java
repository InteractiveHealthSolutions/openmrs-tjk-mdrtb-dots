package org.openmrs.module.dotsreports.reporting.data;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
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
public class DOTS07TJKUpdated implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form07");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form07.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTS-TB07-OMAR" + 
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
		String quarter = (String) parameters.get("quarter");
		
		if (quarter == null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleaseEnterAQuarter"));
		}
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
					//
					CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate, true);
					if (locationFilter != null) {
						//baseCohortDefs.put("location", new Mapped(locationFilter, null));
						report.setBaseCohortDefinition(locationFilter, null);
					}	
				}
				/*CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
				report.setBaseCohortDefinition(baseCohort, null);*/
				
				CohortDefinition dstb = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
				CohortDefinition drtb = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
				
				CohortDefinition allTB = ReportUtil.getCompositionCohort("OR", dstb,drtb);//Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
				
				CohortDefinition tbHIV = Cohorts.getHivPositiveDuring(startDate, endDate);
				//CohortDefinition onlyTB = ReportUtil.minus(allTB,tbHIV);
				
				CohortDefinition men = Cohorts.getMalePatients();
				CohortDefinition women = Cohorts.getFemalePatients();
				
				CohortDefinition pulmonary = Cohorts.getAllPulmonaryDuring(startDate,endDate);
				CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryDuring(startDate,endDate);
				//CohortDefinition culturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
				CohortDefinition smearPositive = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
				//CohortDefinition rapidTestPositive = Cohorts.getAnyXpertOrHAINPositiveDuring(startDate, endDate);
				CohortDefinition rapidTestPositive = Cohorts.getFirstEverMTBResult(null, null, Boolean.TRUE);
				//Context.getObsService().getO
				
				//CohortDefinition bacteriology = ReportUtil.getCompositionCohort("OR", smearPositive, culturePositive);
				
				/*CohortDefinition haveDiagnosticType = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, null);
				CohortDefinition haveClinicalDiagnosis = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, TbConcepts.TB_CLINICAL_DIAGNOSIS[0]);
				CohortDefinition haveLabDiagnosis = ReportUtil.minus(haveDiagnosticType, haveClinicalDiagnosis);*/
				
				CohortDefinition haveLabDiagnosis = ReportUtil.getCompositionCohort("OR", smearPositive,  rapidTestPositive);
				CohortDefinition haveClinicalDiagnosis = ReportUtil.minus(allTB,haveLabDiagnosis);
				
				CohortDefinition dotsage04=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 0, 4);
				CohortDefinition dotsage0514=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 5, 14);
				CohortDefinition dotsage1517=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 15, 17);
				CohortDefinition dotsage1819=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 18, 19);
				CohortDefinition dotsage2024=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 20, 24);
				CohortDefinition dotsage2534 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 25, 34);
				CohortDefinition dotsage3544 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 35, 44);
				CohortDefinition dotsage4554 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 45, 54);
				CohortDefinition dotsage5564 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 55, 64);
				CohortDefinition dotsage65 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 65, null);
				
				CohortDefinition mdrage04=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 0, 4);
				CohortDefinition mdrage0514=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 5, 14);
				CohortDefinition mdrage1517=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 15, 17);
				CohortDefinition mdrage1819=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 18, 19);
				CohortDefinition mdrage2024=Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 20, 24);
				//
				CohortDefinition mdrage2534 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 25, 34);
				CohortDefinition mdrage3544 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 35, 44);
				CohortDefinition mdrage4554 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 45, 54);
				CohortDefinition mdrage5564 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 55, 64);
				CohortDefinition mdrage65 = Cohorts.getAgeAtDOTSRegistration(startDate, endDate, 65, null);

				/*CohortDefinition dotsage04=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 0, 5);
				CohortDefinition dotsage0514=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 5, 15);
				CohortDefinition dotsage1517=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 15, 18);
				CohortDefinition dotsage1819=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 18, 20);
				CohortDefinition dotsage2024=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 20, 25);
				CohortDefinition dotsage2534 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 25, 35);
				CohortDefinition dotsage3544 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 35, 45);
				CohortDefinition dotsage4554 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 45, 55);
				CohortDefinition dotsage5564 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 55, 65);
				CohortDefinition dotsage65 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 65, 999);*/

				/*CohortDefinition mdrage04=Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 0, 5);
				CohortDefinition mdrage0514=Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 5, 15);
				CohortDefinition mdrage1517=Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 15, 18);
				CohortDefinition mdrage1819=Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 18, 20);
				CohortDefinition mdrage2024=Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 20, 25);
				//
				CohortDefinition mdrage2534 = Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 25, 35);
				CohortDefinition mdrage3544 = Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 35, 45);
				CohortDefinition mdrage4554 = Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 45, 55);
				CohortDefinition mdrage5564 = Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 55, 65);
				CohortDefinition mdrage65 = Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 65, 999);
*/
				//
				
				CohortDefinition age04=ReportUtil.getCompositionCohort("OR", dotsage04, mdrage04);
				CohortDefinition age0514=ReportUtil.getCompositionCohort("OR", dotsage0514, mdrage0514);
				CohortDefinition age1517=ReportUtil.getCompositionCohort("OR", dotsage1517, mdrage1517);
				CohortDefinition age1819=ReportUtil.getCompositionCohort("OR", dotsage1819, mdrage1819);
				CohortDefinition age2024=ReportUtil.getCompositionCohort("OR", dotsage2024, mdrage2024);
				//
				CohortDefinition age2534 = ReportUtil.getCompositionCohort("OR", dotsage2534, mdrage2534);
				CohortDefinition age3544 = ReportUtil.getCompositionCohort("OR", dotsage3544, mdrage3544);
				CohortDefinition age4554 = ReportUtil.getCompositionCohort("OR", dotsage4554, mdrage4554);
				CohortDefinition age5564 = ReportUtil.getCompositionCohort("OR", dotsage5564, mdrage5564);
				CohortDefinition age65 = ReportUtil.getCompositionCohort("OR", dotsage65, mdrage65);
				
				//New Age filters added by Omar
				/*CohortDefinition age04=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 0, 5);
				CohortDefinition age0514=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 5, 15);
				CohortDefinition age1517=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 15, 18);
				CohortDefinition age1819=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 18, 20);
				CohortDefinition age2024=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 20, 25);
				//
				CohortDefinition age2534 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 25, 35);
				CohortDefinition age3544 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 35, 45);
				CohortDefinition age4554 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 45, 55);
				CohortDefinition age5564 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 55, 65);
				CohortDefinition age65 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 65, 999);
*/				
				Map<String, CohortDefinition> dotsgroups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
				Map<String, CohortDefinition> mdrgroups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
				
				CohortDefinition dotsallNewCases = dotsgroups.get("New");
				CohortDefinition dotsallFailures = dotsgroups.get("AfterFailure");
				CohortDefinition dotsallDefault = dotsgroups.get("AfterDefault");
				CohortDefinition dotsallOthers = dotsgroups.get("Other");
				CohortDefinition dotsallTransferred = dotsgroups.get("TransferredIn");
				CohortDefinition dotsallRelapses = dotsgroups.get("Relapse");
				
				CohortDefinition mdrallNewCases = mdrgroups.get("New");
				CohortDefinition mdrallFailures = mdrgroups.get("AfterFailure");
				CohortDefinition mdrallDefault = mdrgroups.get("AfterDefault");
				CohortDefinition mdrallOthers = mdrgroups.get("Other");
				CohortDefinition mdrallTransferred = mdrgroups.get("TransferredIn");
				CohortDefinition mdrallRelapses = mdrgroups.get("Relapse");
				
				CohortDefinition allNewCases = ReportUtil.getCompositionCohort("OR", dotsallNewCases,mdrallNewCases);
				CohortDefinition allFailures = ReportUtil.getCompositionCohort("OR", dotsallFailures,mdrallFailures);
				CohortDefinition allDefault = ReportUtil.getCompositionCohort("OR", dotsallDefault,mdrallDefault);
				CohortDefinition allOthers = ReportUtil.getCompositionCohort("OR", dotsallOthers,mdrallOthers);
				CohortDefinition allTransferred = ReportUtil.getCompositionCohort("OR", dotsallTransferred,mdrallTransferred);
				CohortDefinition allRelapses = ReportUtil.getCompositionCohort("OR", dotsallRelapses,mdrallRelapses);
				
				CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",allDefault,allFailures,allOthers,allTransferred ); 
				//CohortDefinition allExceptNew = ReportUtil.getCompositionCohort("OR",allRelapses,allTransferred,allOthers );
			
				
				//////////////////////TABLE1:ALL NEW AND RETREATMENT CASES /////////////////////
				CohortCrossTabDataSetDefinition table1 = new CohortCrossTabDataSetDefinition();
				
				//////////////////////////////////////////////
				/////				ROWS				  ///
				////////////////////////////////////////////
				
				//Notes from meeting with Ali.H 27-11-14:
				// 1.LD means bacteriologically positive (Confirm with Ali)
				// 2.  Relapse = Relapse + After Default + After Failiure (done)
				
				//NEW CASES
				table1.addRow("NewCassesPTbLd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveLabDiagnosis), null);
				table1.addRow("NewCassesTbHivPTbLd", ReportUtil.getCompositionCohort("AND",pulmonary,haveLabDiagnosis, tbHIV),null);
				
				table1.addRow("NewCassesPTbSd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveClinicalDiagnosis), null);
				table1.addRow("NewCassesTbHivPTbSd", ReportUtil.getCompositionCohort("AND",pulmonary,haveClinicalDiagnosis, tbHIV), null);
				
				table1.addRow("NewCassesEpTb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allNewCases), null);
				table1.addRow("NewCassesTbHivEpTb", ReportUtil.getCompositionCohort("AND", extrapulmonary,tbHIV,allNewCases), null);
			
				table1.addRow("NewCassesTotal", ReportUtil.getCompositionCohort("AND",allNewCases,allTB),null);
				table1.addRow("NewCassesTbHivTotal", ReportUtil.getCompositionCohort("AND",allNewCases,tbHIV),null);
				
				//RELAPSES
				table1.addRow("RelapsesPTbLd", ReportUtil.getCompositionCohort("AND", pulmonary,allRelapses,haveLabDiagnosis,allTB), null);
				table1.addRow("RelapsesTbHivPTbLd", ReportUtil.getCompositionCohort("AND", allRelapses, pulmonary,haveLabDiagnosis,tbHIV),null);
				
				table1.addRow("RelapsesPTbSd", ReportUtil.getCompositionCohort("AND", pulmonary,allRelapses,haveClinicalDiagnosis,allTB), null);
				table1.addRow("RelapsesTbHivPTbSd", ReportUtil.getCompositionCohort("AND", pulmonary,allRelapses,haveClinicalDiagnosis,tbHIV), null);
				
				table1.addRow("RelapsesEpTb",ReportUtil.getCompositionCohort("AND",allRelapses,extrapulmonary,allTB),null);
				table1.addRow("RelapsesTbHivEpTb", ReportUtil.getCompositionCohort("AND",allRelapses,extrapulmonary,tbHIV),null);
				
				table1.addRow("RelapsesTotal", ReportUtil.getCompositionCohort("AND",allRelapses,allTB),null);
				table1.addRow("RelapsesTbHivTotal", ReportUtil.getCompositionCohort("AND",allRelapses,tbHIV),null);
				
				//TOTALS
				table1.addRow("TotalPTbLd", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),pulmonary,haveLabDiagnosis,allTB), null);
				table1.addRow("TotalTbHivPTbLd", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),pulmonary,haveLabDiagnosis,tbHIV), null);
				
				table1.addRow("TotalPTbSd", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),pulmonary,haveClinicalDiagnosis,allTB), null);
				table1.addRow("TotalTbHivPTbSd", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),pulmonary,haveClinicalDiagnosis,tbHIV), null);
				
				table1.addRow("TotalEpTb", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),extrapulmonary,allTB), null);
				table1.addRow("TotalTbHivEpTb", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),extrapulmonary,tbHIV), null);
				
				table1.addRow("TotalTotal", ReportUtil.getCompositionCohort("AND",ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),allTB),null);
				table1.addRow("TotalTbHivTotal", ReportUtil.getCompositionCohort("AND",ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses),tbHIV),null);
				
				///ROWS END HERE
				
				/////////////////////////////////////////////////////
				////      			COLUMNS           			////
				///////////////////////////////////////////////////
				table1.addColumn("04Female", ReportUtil.getCompositionCohort("AND", age04,women), null);
				table1.addColumn("04Male", ReportUtil.getCompositionCohort("AND", age04,men), null);
				table1.addColumn("14Female", ReportUtil.getCompositionCohort("AND", age0514,women), null);
				table1.addColumn("14Male", ReportUtil.getCompositionCohort("AND", age0514,men), null);
				table1.addColumn("17Female", ReportUtil.getCompositionCohort("AND", age1517,women), null);
				table1.addColumn("17Male", ReportUtil.getCompositionCohort("AND", age1517,men), null);
				table1.addColumn("19Female", ReportUtil.getCompositionCohort("AND", age1819,women), null);
				table1.addColumn("19Male", ReportUtil.getCompositionCohort("AND", age1819,men), null);
				table1.addColumn("24Female", ReportUtil.getCompositionCohort("AND", age2024,women), null);
				table1.addColumn("24Male", ReportUtil.getCompositionCohort("AND", age2024,men), null);
				table1.addColumn("34Female", ReportUtil.getCompositionCohort("AND", age2534,women), null);
				table1.addColumn("34Male", ReportUtil.getCompositionCohort("AND", age2534,men), null);
				table1.addColumn("44Female", ReportUtil.getCompositionCohort("AND", age3544,women), null);
				table1.addColumn("44Male", ReportUtil.getCompositionCohort("AND", age3544,men), null);
				table1.addColumn("54Female", ReportUtil.getCompositionCohort("AND", age4554,women), null);
				table1.addColumn("54Male", ReportUtil.getCompositionCohort("AND", age4554,men), null);
				table1.addColumn("64Female", ReportUtil.getCompositionCohort("AND", age5564,women), null);
				table1.addColumn("64Male", ReportUtil.getCompositionCohort("AND", age5564,men), null);
				table1.addColumn("65Female", ReportUtil.getCompositionCohort("AND", age65,women), null);
				table1.addColumn("65Male", ReportUtil.getCompositionCohort("AND", age65,men), null);
				table1.addColumn("TotalFemale", women, null);
				table1.addColumn("TotalMale", men, null);
				table1.addColumn("GenTotal", ReportUtil.getCompositionCohort("OR", men, women), null);
				//report.addDataSetDefinition("all", table1, null);
				
				//////////////////////TABLE2:ALL RETREATMENT CASES /////////////////////
				//CohortCrossTabDataSetDefinition table2 = new CohortCrossTabDataSetDefinition();
			
				
				///////////////////////			ROWS		////////////////////////////
				
				table1.addRow(	"ptbldretreatment", 			ReportUtil.getCompositionCohort("AND",allTB,pulmonary,haveLabDiagnosis),null);
				table1.addRow(	"tbhivptbldretreatment",	 	ReportUtil.getCompositionCohort("AND",tbHIV,pulmonary,haveLabDiagnosis),null);
				table1.addRow(	"ptbbsdretreatment", 			ReportUtil.getCompositionCohort("AND",allTB,pulmonary,haveClinicalDiagnosis),null);
				table1.addRow(	"tbhivptbsdretreatment", 		ReportUtil.getCompositionCohort("AND",tbHIV,pulmonary,haveLabDiagnosis),null);
				table1.addRow(	"eptbretreatment", 				ReportUtil.getCompositionCohort("AND",allTB,extrapulmonary),null);
				table1.addRow(	"tbhiveptbretreatment", 		ReportUtil.getCompositionCohort("AND",tbHIV,extrapulmonary),null);
				table1.addRow(	"totalretreatment", 			ReportUtil.getCompositionCohort("AND",allTB),null);
				table1.addRow(	"totalhivretreatment", 			ReportUtil.getCompositionCohort("AND",tbHIV),null);

				///////////////////////		   COLUMNS		////////////////////////////
				//FAILURES
				table1.addColumn("failuremale"	, 		ReportUtil.getCompositionCohort("AND", men, allFailures), null);
				table1.addColumn("failurefemale",		ReportUtil.getCompositionCohort("AND", women, allFailures), null);
				table1.addColumn("failuretotal"	, 		allFailures,null);
				
				//DEFAULT
				table1.addColumn("defaultmale",		ReportUtil.getCompositionCohort("AND", men, allDefault), null);
				table1.addColumn("defaultfemale" ,		ReportUtil.getCompositionCohort("AND", women,allDefault), null);
				table1.addColumn("defaulttotal"	 ,		allDefault, null);
				
				//OTHER
				table1.addColumn("othermale"	,		ReportUtil.getCompositionCohort("AND", men, allOthers), null);
				table1.addColumn("otherfemale" 	,		ReportUtil.getCompositionCohort("AND", women, allOthers), null);
				table1.addColumn("othertotal"	,		allOthers, null);
				
				//TOTAL
				table1.addColumn("totalmale"	,		ReportUtil.getCompositionCohort("AND", men, allRetreatment), null);
				table1.addColumn("totalfemale" 	,		ReportUtil.getCompositionCohort("AND", women, allRetreatment), null);
				table1.addColumn("totaltotal"	,		allRetreatment, null);
				
				//TOTAL (COLUMN 1 + 2)
				table1.addColumn("totalcolumnmale"	,	ReportUtil.getCompositionCohort("AND", men, ReportUtil.getCompositionCohort("OR",allNewCases,allRelapses,allRetreatment)), null);
				table1.addColumn("totalcolumnfemale" ,	ReportUtil.getCompositionCohort("AND", women,ReportUtil.getCompositionCohort("OR",allNewCases,allRelapses,allRetreatment)), null);
				table1.addColumn("totalcolumntotal"	,	ReportUtil.getCompositionCohort("OR", allNewCases,allRelapses,allRetreatment), null);
				
				//report.addDataSetDefinition("retreatment", table2, null);
				report.addDataSetDefinition("all", table1, null);
				
		ReportData data;
		
		try {
			data = Context.getService(ReportDefinitionService.class).evaluate(
					report, context);
		} catch (EvaluationException e) {
			throw new MdrtbAPIException("Unable to evaluate WHO Form 7 report",
					e);
		}
		
		CohortDefinition newMen = ReportUtil.getCompositionCohort("AND", men, allNewCases, report.getBaseCohortDefinition().getParameterizable());
		Cohort test = null;
		
		try {
			test = Context.getService(CohortDefinitionService.class).evaluate(newMen, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("********NEW**************");
		Set<Integer> ids = test.getMemberIds();
		for(Integer testid:ids) {
			System.out.println(testid);
		}
		
		
		CohortDefinition relapseMen = ReportUtil.getCompositionCohort("AND", men, allRelapses, allTB,report.getBaseCohortDefinition().getParameterizable());
	    test = null;
		
		try {
			test = Context.getService(CohortDefinitionService.class).evaluate(relapseMen, new EvaluationContext());
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("**********RELAPSES**************");
		ids = test.getMemberIds();
		for(Integer testid:ids) {
			System.out.println(testid);
		}
		
		
		
		
		return data;
	}
}