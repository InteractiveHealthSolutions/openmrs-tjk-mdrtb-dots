/**
o  * The contents of this file are subject to the OpenMRS Public License
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
package org.openmrs.module.mdrtb.reporting.data;

/*
 * 
 * base cohort is all MDR-TB patients.
 * Number of patients started treatment before beginning of quarter - A
Number of patients started treatment during quarter - B

Table 2 = B - A
Table 3 - A
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;


import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
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
 * Outcome Report which reports on patient outcome by registration group
 */
public class TB08TJKoldv2 implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb08");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb08.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location",  Context.getMessageSourceService().getMessage("mdrtb.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("mdrtb.yearOfTreatmentStart"), Integer.class));
		/*l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarterOptional"), Integer.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("mdrtb.monthOptional"), Integer.class));*/
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarterOptional"), String.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("mdrtb.monthOptional"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/TB08" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer) parameters.get("year");
		/*Integer quarter = (Integer) parameters.get("quarter");
		Integer month = (Integer) parameters.get("month");*/
		String quarter = (String) parameters.get("quarter");
		String month = (String) parameters.get("month");
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, month));
		
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
		
		// Base Cohort is patients who started treatment during year, optionally at location
		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		/*baseCohortDefs.put("startedTreatment", new Mapped(Cohorts.getStartedTreatmentFilter(startDate, endDate), null));
		if (location != null) {
			CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
			if (locationFilter != null) {
				baseCohortDefs.put("location", new Mapped(locationFilter, null));
			}
		}*/
		//baseCohortDefs.put("startedTreatment", new Mapped(Cohorts.getStartedTreatmentFilter(startDate, endDate), null));
		if (location != null) {
			CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
			//CohortDefinition locationFilter = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate, endDate);
			if (locationFilter != null) {
				baseCohortDefs.put("location", new Mapped(locationFilter, null));
			}	
		}
		
		else {
			baseCohortDefs.put("startedTreatment", new Mapped(Cohorts.getStartedTreatmentFilter(startDate, endDate), null));
		}
		
		/*CohortDefinition mdr = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition pdr = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition pdrOnly = ReportUtil.minus(pdr, mdr);*/
		CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
		report.setBaseCohortDefinition(baseCohort, null);
		CohortDefinition xdr = Cohorts.getResistanceTypeFilter(startDate, endDate, TbClassification.XDR_TB);
		CohortDefinition drtb = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
		CohortDefinition pulmonary = Cohorts.getAllPulmonaryDuring(startDate,endDate);
		CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryDuring(startDate,endDate);
		
		pulmonary = ReportUtil.getCompositionCohort("AND", drtb,pulmonary);
		extrapulmonary = ReportUtil.getCompositionCohort("AND", drtb,extrapulmonary);
		CohortCrossTabDataSetDefinition dsd = new CohortCrossTabDataSetDefinition();
		
		// create the rows in the chart
		Map<String, CohortDefinition> groups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
		Map<String, CohortDefinition> outcomes = ReportUtil.getMdrtbOutcomesFilterSet(startDate, endDate);
		
		CohortDefinition cured = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("Cured"));
		CohortDefinition treatmentCompleted = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("TreatmentCompleted"));
		CohortDefinition successfulTreatment = ReportUtil.getCompositionCohort("AND", drtb, ReportUtil.getCompositionCohort("OR", cured, treatmentCompleted));
		CohortDefinition failed = ReportUtil.getCompositionCohort("AND", drtb,outcomes.get("Failed"));
		CohortDefinition ltfu = ReportUtil.getCompositionCohort("AND",drtb, outcomes.get("Defaulted"));
		CohortDefinition died = ReportUtil.getCompositionCohort("AND", drtb, outcomes.get("Died"));
		CohortDefinition stillEnrolled =  ReportUtil.getCompositionCohort("AND", drtb,ReportUtil.getCompositionCohort("OR", outcomes.get("StillEnrolled"),groups.get("TransferredOut")));
		
		CohortDefinition tbDied = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH).getId(),
				null, null, SetComparator.IN, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_TB).getId());
		
		//DIED: NON-TB
		//CohortDefinition nonTbDeath = ReportUtil.minus(diedDuringTreatment, tbDied);
		CohortDefinition nonTbDeath = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH).getId(),
				null, null, SetComparator.IN, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEATH_BY_OTHER_DISEASES).getId());

		tbDied = ReportUtil.getCompositionCohort("AND", died, tbDied);
		nonTbDeath = ReportUtil.getCompositionCohort("AND", died, nonTbDeath);
		
		
		//CohortDefinition previousSecondLine = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate).get("PreviousSecondLine");
		
	/*	CohortDefinition curedMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.CURED[0]);
		CohortDefinition txCompletedMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.TREATMENT_COMPLETE[0]);
		CohortDefinition defaultMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.LOST_TO_FOLLOWUP[0]);
		CohortDefinition failureMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.FAILED[0]);
		CohortDefinition relapseMDR = ReportUtil.getCompositionCohort("OR", curedMDR,txCompletedMDR);*/
		
		
		
		
		/*CohortDefinition newOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("New"),previousSecondLine));
		CohortDefinition relapseOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("Relapse"),previousSecondLine));
		CohortDefinition defaultOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("AfterDefault"),previousSecondLine));
		CohortDefinition failureCatIOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("AfterFailureCategoryI"),previousSecondLine));
		CohortDefinition failureCatIIOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("AfterFailureCategoryII"),previousSecondLine));
		CohortDefinition unknownOnly = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",mdr,pdrOnly),ReportUtil.minus(groups.get("Other"),previousSecondLine));*/
		
		/*CohortDefinition newOnly = groups.get("New");
		CohortDefinition relapseOnly = groups.get("Relapse");
		CohortDefinition defaultOnly = groups.get("AfterDefault");
		CohortDefinition failureCatIOnly = groups.get("AfterFailureCategoryI");
		CohortDefinition failureCatIIOnly = groups.get("AfterFailureCategoryII");
		CohortDefinition unknownOnly = groups.get("Other");*/
		
		dsd.addRow("New", groups.get("New"),null);
		dsd.addRow("Relapse1", groups.get("Relapse1"), null);
		dsd.addRow("Relapse2", groups.get("Relapse2"), null);
		dsd.addRow("AfterDefault1", groups.get("AfterDefault1") , null);
		dsd.addRow("AfterDefault2", groups.get("AfterDefault2") , null);
		dsd.addRow("AfterFailure1",groups.get("AfterFailure1") , null);
		dsd.addRow("AfterFailure2", groups.get("AfterFailure2"), null);
		dsd.addRow("Other", ReportUtil.getCompositionCohort("OR",groups.get("Other"),groups.get("TransferredIn")), null);
		dsd.addRow("Total", drtb,null);
		dsd.addRow("Xdr", xdr,null);
		/*dsd.addRow("RelapseMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, relapseMDR), null);
		dsd.addRow("DefaultMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, defaultMDR), null);
		dsd.addRow("FailureMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, failureMDR), null);
		dsd.addRow("PrevTotal", ReportUtil.getCompositionCohort("OR",relapseMDR,defaultMDR,failureMDR), null);
		dsd.addRow("Total",ReportUtil.getCompositionCohort("OR",newOnly,relapseOnly,defaultOnly,failureCatIOnly,failureCatIIOnly,unknownOnly,relapseMDR,defaultMDR,failureMDR),null);
*/		
		
		dsd.addColumn("pRegistered", pulmonary, null);
		dsd.addColumn("epRegistered", extrapulmonary, null);
		dsd.addColumn("Registered", drtb, null);
		dsd.addColumn("pCured", ReportUtil.getCompositionCohort("AND",pulmonary,cured), null);
		dsd.addColumn("epCured", ReportUtil.getCompositionCohort("AND",extrapulmonary,cured), null);
		dsd.addColumn("Cured", cured, null);
		dsd.addColumn("pTreatmentCompleted", ReportUtil.getCompositionCohort("AND",pulmonary,treatmentCompleted), null);
		dsd.addColumn("epTreatmentCompleted",ReportUtil.getCompositionCohort("AND",extrapulmonary,treatmentCompleted), null);
		dsd.addColumn("TreatmentCompleted", treatmentCompleted, null);
		dsd.addColumn("pSuccessfulTreatment", ReportUtil.getCompositionCohort("AND",pulmonary,successfulTreatment), null);
		dsd.addColumn("epSuccessfulTreatment",ReportUtil.getCompositionCohort("AND",extrapulmonary,successfulTreatment), null);
		dsd.addColumn("SuccessfulTreatment", successfulTreatment, null);
		dsd.addColumn("pFailed", ReportUtil.getCompositionCohort("AND",pulmonary,failed), null);
		dsd.addColumn("epFailed", ReportUtil.getCompositionCohort("AND",extrapulmonary,failed), null);
		dsd.addColumn("Failed", failed, null);
		dsd.addColumn("pDefaulted", ReportUtil.getCompositionCohort("AND",pulmonary,ltfu), null);
		dsd.addColumn("epDefaulted",ReportUtil.getCompositionCohort("AND",extrapulmonary,ltfu), null);
		dsd.addColumn("Defaulted", ltfu, null);
		dsd.addColumn("pDiedTB",ReportUtil.getCompositionCohort("AND",pulmonary,tbDied), null);
		dsd.addColumn("epDiedTB", ReportUtil.getCompositionCohort("AND",extrapulmonary,tbDied), null);
		dsd.addColumn("DiedTB", tbDied, null);
		dsd.addColumn("pDiedNonTB", ReportUtil.getCompositionCohort("AND",pulmonary,nonTbDeath), null);
		dsd.addColumn("epDiedNonTB", ReportUtil.getCompositionCohort("AND",extrapulmonary,nonTbDeath), null);
		dsd.addColumn("DiedNonTB", nonTbDeath, null);
		dsd.addColumn("pStillEnrolled", ReportUtil.getCompositionCohort("AND",pulmonary,stillEnrolled), null);
		dsd.addColumn("epStillEnrolled", ReportUtil.getCompositionCohort("AND",extrapulmonary,stillEnrolled), null);
		dsd.addColumn("StillEnrolled", stillEnrolled, null);
		dsd.addColumn("ColTotal",drtb, null);
		
		/*for (String key : rows.keySet()) {
			dsd.addRow(key, rows.get(key), null);
		}
		
		dsd.addRow("Total", ReportUtil.getCompositionCohort(dsd.getRows(), "OR"), null);*/
		
		// create the columns in the chart
		
		/*for (String key : columns.keySet()) {
			dsd.addColumn(key, columns.get(key), null);
		}
		dsd.addColumn("Total", ReportUtil.getCompositionCohort(dsd.getColumns(), "OR"), null);	
		*/
		report.addDataSetDefinition("results", dsd, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
        	throw new MdrtbAPIException("Unable to evaluate Outcomes report", e);
        }
		return data;
	}
}