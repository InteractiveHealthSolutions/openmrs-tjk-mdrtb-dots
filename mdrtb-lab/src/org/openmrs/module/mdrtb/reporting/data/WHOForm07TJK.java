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
package org.openmrs.module.mdrtb.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
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
public class WHOForm07TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.form07");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.form07.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("mdrtb.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("mdrtb.yearOfTreatmentStart"), Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/WHOForm07" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		try {
			Integer year = (Integer) parameters.get("year");
			context.addParameterValue("startDate", DateUtil.getDateTime(year-1, 12, 26));
			context.addParameterValue("endDate", DateUtil.getDateTime(year, 12, 25));
		}
		catch (Exception e) {
			throw new RuntimeException(Context.getMessageSourceService().getMessage("mdrtb.error.pleaseEnterAYear"));
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
		
		// Base Cohort is confirmed mdr patients, in program, who started treatment during year, optionally at location
		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("confirmedMdr", new Mapped(Cohorts.getConfirmedMdrInProgramAndStartedTreatmentFilter(startDate, endDate), null));
		/*if (location != null) {
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
		Map<String, CohortDefinition> rows = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate);
		for (String key : rows.keySet()) {
			dsd.addRow(key, rows.get(key), null);
		}
		
		dsd.addRow("Total", ReportUtil.getCompositionCohort(dsd.getRows(), "OR"), null);
		
		// create the columns in the chart
		Map<String, CohortDefinition> columns = ReportUtil.getMdrtbOutcomesFilterSet(startDate, endDate);
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
        	throw new MdrtbAPIException("Unable to evaluate WHO Form 7 report", e);
        }
		return data;
	}
}