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
import java.util.LinkedHashMap;
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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * Outcome Report which reports on patient outcome by registration group
 */
public class OutcomeReportTJK implements org.openmrs.module.dotsreports.reporting.ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.outcomeReport");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.outcomeReport.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location",  Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.yearOfTreatmentStart"), Integer.class));
		/*l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarterOptional"), Integer.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("mdrtb.monthOptional"), Integer.class));*/
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarterOptional"), String.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("dotsreports.monthOptional"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/OutcomeReport" + 
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
			//CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
			CohortDefinition locationFilter = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate, endDate);
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
		
		// create the rows in the chart
		Map<String, CohortDefinition> rows = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
		for (String key : rows.keySet()) {
			dsd.addRow(key, rows.get(key), null);
		}
		
		dsd.addRow("Total", ReportUtil.getCompositionCohort(dsd.getRows(), "OR"), null);
		
		// create the columns in the chart
		Map<String, CohortDefinition> columns = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
		for (String key : columns.keySet()) {
			dsd.addColumn(key, columns.get(key), null);
		}
		dsd.addColumn("Total", ReportUtil.getCompositionCohort(dsd.getColumns(), "OR"), null);	
		
		report.addDataSetDefinition("Treatment results", dsd, null);
		
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