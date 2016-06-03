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
package org.openmrs.module.dotsreports.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * WHO Form 05 Report
 */
public class DOTS10TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form10");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form10.title");
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
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTS-TB10" + 
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
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
    public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		// Set base cohort to patients assigned to passed location, if applicable
		CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
		if (locationFilter != null) {
			report.setBaseCohortDefinition(locationFilter, null);
		}

		// Add the data set definitions
		CohortCrossTabDataSetDefinition labResultDsd = new CohortCrossTabDataSetDefinition();
		
		// note that for the purpose of this report, the polydr, mdr, and xdr cohorts should be mutually exclusive
		// that is, by the standard definition, any patients that are mdr or xdr are also polydr; but we 
		// do not want to include the mdr and xdr patients in our poly count, hence why we use the "minus" method here
		/*labResultDsd.addColumn("polydr", ReportUtil.minus(polydr, mdr, xdr), null);
		labResultDsd.addColumn("mdr", ReportUtil.minus(mdr, xdr), null);
		labResultDsd.addColumn("xdr", xdr, null);*/
		
		
		/*CohortCrossTabDataSetDefinition treatmentDsd = new CohortCrossTabDataSetDefinition();
		treatmentDsd.addRow("confirmed", Cohorts.getInDOTSProgramAndStartedTreatmentFilter(startDate, endDate), null);
		//treatmentDsd.addRow("suspected", Cohorts.getSuspectedMdrInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		*/
		Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
		Map<String, CohortDefinition> outcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
		/*for (String key : columns.keySet()) {
			treatmentDsd.addColumn(key, columns.get(key), null);
		}*/
		CohortDefinition positiveResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
		CohortDefinition negativeResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.NEGATIVE);
		CohortDefinition unknownResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.UNKNOWN);
		//report.addDataSetDefinition("startedTreatment", treatmentDsd, null);
		
		
		CohortDefinition negativeAtTwo = Cohorts.getConvertedInMonthEnrolledDuring(startDate, endDate, 2);
		CohortDefinition negativeAtThree = Cohorts.getConvertedInMonthEnrolledDuring(startDate, endDate, 3);
		CohortDefinition negativeAtFour = Cohorts.getConvertedInMonthEnrolledDuring(startDate, endDate, 4);
		
		CohortDefinition convertedAtTwo = ReportUtil.getCompositionCohort("AND",negativeAtTwo,positiveResult);
		CohortDefinition convertedAtThree = ReportUtil.getCompositionCohort("AND",positiveResult,ReportUtil.minus(negativeAtThree, convertedAtTwo));
		CohortDefinition convertedAtFour = ReportUtil.getCompositionCohort("AND",positiveResult,ReportUtil.minus(negativeAtFour, convertedAtThree));
		CohortDefinition noFollowupSmear = Cohorts.getNoFollowupSmears(startDate, endDate);
		CohortDefinition notConverted = ReportUtil.getCompositionCohort("AND",ReportUtil.minus(positiveResult, convertedAtFour,convertedAtThree,convertedAtTwo,noFollowupSmear));
		
		
		labResultDsd.addRow("New", groups.get("New"),null);
		labResultDsd.addRow("Relapse", groups.get("Relapse"),null);
		labResultDsd.addRow("AfterFailure", groups.get("AfterFailure"),null);
		labResultDsd.addRow("AfterDefault", groups.get("AfterDefault"),null);
		labResultDsd.addRow("Other", groups.get("Other"),null);
		
		labResultDsd.addColumn("Positive", positiveResult, null);
		labResultDsd.addColumn("ConvertedAtTwo", convertedAtTwo, null);
		labResultDsd.addColumn("ConvertedAtThree", convertedAtThree, null);
		labResultDsd.addColumn("ConvertedAtFour", convertedAtFour, null);
		labResultDsd.addColumn("NotConverted", notConverted, null);
		labResultDsd.addColumn("NoFollowup", ReportUtil.getCompositionCohort("AND", positiveResult,noFollowupSmear), null);
		
		/*labResultDsd.addColumn("New", ReportUtil.getCompositionCohort("AND", groups.get("New"),positiveResult),null);
		labResultDsd.addColumn("Relapse", ReportUtil.getCompositionCohort("AND", groups.get("Relapse"),positiveResult),null);
		labResultDsd.addColumn("AfterFailure", ReportUtil.getCompositionCohort("AND", groups.get("AfterFailure"),positiveResult),null);
		labResultDsd.addColumn("AfterDefault", ReportUtil.getCompositionCohort("AND", groups.get("AfterDefault"),positiveResult),null);
		labResultDsd.addColumn("Other", ReportUtil.getCompositionCohort("AND", groups.get("Other"),positiveResult),null);*/
		
		//labResultDsd
		
		report.addDataSetDefinition("labDetections", labResultDsd, null);
		//report.addDataSetDefinition("chacha", labResultDsd, null);
		
		CohortCrossTabDataSetDefinition lowerSet = new CohortCrossTabDataSetDefinition();
		lowerSet.addColumn("NoFollowupDead", ReportUtil.getCompositionCohort("AND",positiveResult,noFollowupSmear,outcomes.get("Died")),null);
		lowerSet.addColumn("NoFollowupTransferOut", ReportUtil.getCompositionCohort("AND",positiveResult,noFollowupSmear,outcomes.get("TransferredOut")),null);
		lowerSet.addColumn("NoFollowupDefaulted", ReportUtil.getCompositionCohort("AND",positiveResult,noFollowupSmear,outcomes.get("Defaulted")),null);
		
		//lowerSet.addColumn("NoFollowup", ReportUtil.getCompositionCohort("AND",positiveResult,noFollowupSmear,outcomes.get("Defaulted")),null);
		lowerSet.addColumn("NoFollowupCanceled", ReportUtil.getCompositionCohort("AND",positiveResult,noFollowupSmear,outcomes.get("Canceled")),null);
		report.addDataSetDefinition("lowerSet", lowerSet, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
        	throw new MdrtbAPIException("Unable to evaluate WHO Form 5 report", e);
        }
		return data;
	}
}