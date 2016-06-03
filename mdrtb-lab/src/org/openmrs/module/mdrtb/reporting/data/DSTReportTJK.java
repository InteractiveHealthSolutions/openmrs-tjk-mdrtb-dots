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
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
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
 * Outcome Report which reports on patient outcome by registration group
 */
public class DSTReportTJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.dstreport");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.dstreport.title");
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
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/DSTForm" + 
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
		CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
		report.setBaseCohortDefinition(baseCohort, null);
		
		CohortCrossTabDataSetDefinition dsd = new CohortCrossTabDataSetDefinition();
		
		// columns
		Map<String, CohortDefinition> groups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
		CohortDefinition pulmonary = Cohorts.getAllPulmonaryEver();
		CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryEver();
		CohortDefinition smearNegative = Cohorts.getAllSmearNegativeDuring(startDate, endDate);
		CohortDefinition smearPositive = Cohorts.getAnySmearPositiveDuring(startDate, endDate);
		
		CohortDefinition failure = ReportUtil.getCompositionCohort("OR", groups.get("AfterFailureCategoryI"),groups.get("AfterFailureCategoryII"));
		CohortDefinition total = ReportUtil.getCompositionCohort("OR",groups.get("New"),groups.get("Relapse"),groups.get("AfterDefault"),failure,groups.get("Other"));
		dsd.addColumn("NewSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,groups.get("New")),null);
		dsd.addColumn("NewSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,groups.get("New")),null);
		dsd.addColumn("NewExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,groups.get("New")),null);
		
		dsd.addColumn("RelapseSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,groups.get("Relapse")),null);
		dsd.addColumn("RelapseSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,groups.get("Relapse")),null);
		dsd.addColumn("RelapseExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,groups.get("Relapse")),null);
		
		dsd.addColumn("DefaultSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,groups.get("AfterDefault")),null);
		dsd.addColumn("DefaultSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,groups.get("AfterDefault")),null);
		dsd.addColumn("DefaultExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,groups.get("AfterDefault")),null);
		
		dsd.addColumn("FailureSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,failure),null);
		dsd.addColumn("FailureSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,failure),null);
		dsd.addColumn("FailureExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,failure),null);
		
		dsd.addColumn("OtherSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,groups.get("Other")),null);
		dsd.addColumn("OtherSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,groups.get("Other")),null);
		dsd.addColumn("OtherExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,groups.get("Other")),null);
		
		dsd.addColumn("TotalSSPos", ReportUtil.getCompositionCohort("AND",pulmonary,smearPositive,total),null);
		dsd.addColumn("TotalSSNeg", ReportUtil.getCompositionCohort("AND",pulmonary,smearNegative,total),null);
		dsd.addColumn("TotalExtra", ReportUtil.getCompositionCohort("AND",extrapulmonary,total),null);
		
		//rows
		CohortDefinition culturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
		CohortDefinition confirmedMdrtb = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition dstResultExists = ReportUtil.getDstResultCohort(endDate);
		CohortDefinition referredXpert = Cohorts.getPatientsReferredToTestFilter(null, endDate, MdrtbConcepts.GENEXPERT[0]);
		CohortDefinition referredHAIN = Cohorts.getPatientsReferredToTestFilter(null, endDate, MdrtbConcepts.HAIN_TEST[0]);
		CohortDefinition referredCulture = Cohorts.getPatientsReferredToTestFilter(null, endDate, MdrtbConcepts.CULTURE_RESULT[0]);
		
		
		CohortDefinition referredCultureOnly = ReportUtil.minus(referredCulture, culturePositive);
		CohortDefinition referredXpertOnly = ReportUtil.minus(referredXpert, dstResultExists);
		CohortDefinition referredHAINOnly = ReportUtil.minus(referredHAIN, dstResultExists);
		CohortDefinition becameMdrtbAfterTreatmentStart  = Cohorts.getMdrtbAfterTreatmentStart(startDate, null, 0, null);
	//	Map<String, CohortDefinition> outcomes = ReportUtil.getMdrtbOutcomesFilterSet(startDate, endDate);
		
		dsd.addRow("referredXpert", referredXpertOnly, null);
		dsd.addRow("referredHAIN", referredHAINOnly, null);
		dsd.addRow("referredCulture", referredCultureOnly, null);
		dsd.addRow("culturePositive", culturePositive, null);
		dsd.addRow("dstResultExists", dstResultExists, null);
		dsd.addRow("mdr", confirmedMdrtb,  null);
		dsd.addRow("mdrafter", becameMdrtbAfterTreatmentStart, null);
		
		
		
		
		
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