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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
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
 * WHO Form 05 Report for Tajikistan
 */
public class WHOForm05TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.form05");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.form05.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("mdrtb.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("mdrtb.year"), Integer.class));
		//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/TB07" + 
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
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("mdrtb.error.pleaseEnterAQuarter"));
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
		//CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
		CohortDefinition locationFilter;
		
		if(location!=null)
			locationFilter = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate, endDate);
		else
			locationFilter= Cohorts.getStartedTreatmentFilter(startDate, endDate);
		
		if (locationFilter != null) {
			
			report.setBaseCohortDefinition(locationFilter, null);
		}
		
		

		// Add the data set definitions
		CohortCrossTabDataSetDefinition labResultDsd = new CohortCrossTabDataSetDefinition();
		//CohortDefinition polydr = Cohorts.getPolydrDetectionFilter(startDate, endDate);
		CohortDefinition mdr = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition xdr = Cohorts.getXdrDetectionFilter(startDate, endDate);
		
		// note that for the purpose of this report, the polydr, mdr, and xdr cohorts should be mutually exclusive
		// that is, by the standard definition, any patients that are mdr or xdr are also polydr; but we 
		// do not want to include the mdr and xdr patients in our poly count, hence why we use the "minus" method here
		//labResultDsd.addColumn("polydr", ReportUtil.minus(polydr, mdr, xdr), null);
		labResultDsd.addColumn("mdr", ReportUtil.minus(mdr, xdr), null);
		labResultDsd.addColumn("xdr", xdr, null);
		report.addDataSetDefinition("labDetections", labResultDsd, null);
		
		CohortCrossTabDataSetDefinition treatmentDsd = new CohortCrossTabDataSetDefinition();
		
		//check
		treatmentDsd.addRow("mdr", Cohorts.getConfirmedMdrOnlyInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		treatmentDsd.addRow("xdr", Cohorts.getConfirmedXdrInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		treatmentDsd.addRow("tbhiv", Cohorts.getHivPositiveDuring(startDate, endDate), null);
		treatmentDsd.addRow("children", Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 0, 14), null);
		
		
		//treatmentDsd.addRow("suspected", Cohorts.getSuspectedMdrInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		
		//CohortCrossTabDataSetDefinition siteDsd = new CohortCrossTabDataSetDefinition();
		//treatmentDsd.addRow("pulmonary", Cohorts.getAllPulmonaryEver(),null);
		//treatmentDsd.addRow("extrapulmonary", Cohorts.getAllPulmonaryEver(),null);
		//report.addDataSetDefinition("site", siteDsd,null);
		
		CohortDefinition pulmonary = Cohorts.getAllPulmonaryEver();
		CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryEver();
		
		//Map<String, CohortDefinition> columns = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate);
		Map<String, CohortDefinition> columns = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
		/*for (String key : columns.keySet()) {
			treatmentDsd.addColumn(key, columns.get(key), null);
		}*/
		
		treatmentDsd.addColumn("New", ReportUtil.getCompositionCohort("AND", columns.get("New"), pulmonary), null);
		treatmentDsd.addColumn("Relapse", ReportUtil.getCompositionCohort("AND", columns.get("Relapse"), pulmonary), null);
		treatmentDsd.addColumn("AfterDefault", ReportUtil.getCompositionCohort("AND", columns.get("AfterDefault"), pulmonary), null);
		treatmentDsd.addColumn("AfterFailureCategoryI", ReportUtil.getCompositionCohort("AND", columns.get("AfterFailureCategoryI"), pulmonary), null);
		treatmentDsd.addColumn("AfterFailureCategoryII", ReportUtil.getCompositionCohort("AND", columns.get("AfterFailureCategoryII"), pulmonary), null);
		treatmentDsd.addColumn("Other", extrapulmonary, null);
		treatmentDsd.addColumn("Total", ReportUtil.getCompositionCohort(treatmentDsd.getColumns(), "OR"), null);
		report.addDataSetDefinition("startedTreatment", treatmentDsd, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
        	throw new MdrtbAPIException("Unable to evaluate WHO Form 07y report", e);
        }
		return data;
	}
}