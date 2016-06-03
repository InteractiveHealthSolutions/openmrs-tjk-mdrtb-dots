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
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.MdrtbQueryService;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.dotsreports.reporting.definition.DotsDstResultCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * MOH Report which reports on MDR TB Patients within broader TB report
 */
public class MOHReportTJK implements org.openmrs.module.dotsreports.reporting.ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return translate("name");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return translate("description");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", translate("location"), Location.class));
		l.add(new Parameter("year", translate("year"), Integer.class));
		/*l.add(new Parameter("quarter", translate("quarter"), Integer.class));
		l.add(new Parameter("month", translate("month"), Integer.class));*/
		l.add(new Parameter("quarter", translate("quarter"), String.class));
		l.add(new Parameter("month", translate("month"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		/*l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/mdrtb/reporting/data/output/MOHReport" + 
				(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));*/
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
	public ReportData evaluateReport(EvaluationContext context) {

		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");

		CohortDefinition inProgram = Cohorts.getInDOTSProgramEverDuring(startDate, endDate);
		//CohortDefinition atLocation = (location == null ? null : Cohorts.getLocationFilter(location, startDate, endDate));
		//CohortDefinition atLocation = (location == null ? null : Cohorts.getLocationFilter(location, startDate, endDate));
		CohortDefinition atLocation = (location==null || location.getCountyDistrict()==null ) ? null : Cohorts.getPatientsWithDistict(location);
		CohortDefinition newlyHospitalized = Cohorts.getNewlyHospitalizedDuringPeriod(startDate, endDate);
		CohortDefinition allHospitalized = Cohorts.getEverHospitalizedDuringPeriod(startDate, endDate);
		CohortDefinition startedTxDuring = Cohorts.getStartedTreatmentFilter(startDate, endDate);
		CohortDefinition startedTxByEnd = Cohorts.getStartedTreatmentFilter(null, endDate);
		CohortDefinition smearPositive = Cohorts.getMostRecentlySmearPositiveByDate(endDate);
		CohortDefinition smearNegative = Cohorts.getMostRecentlySmearNegativeByDate(endDate);
		CohortDefinition abandoned = Cohorts.getDefaultedDuringFilter(startDate, endDate);
		CohortDefinition died = Cohorts.getDiedDuringFilter(startDate, endDate);
		
		// TODO: figure out what obs to look for here--see ticket HATB-358
		CohortDefinition newlyHivPositive = Cohorts.getNewlyHivPositive(startDate, endDate);
		CohortDefinition everHivPositive = Cohorts.getHivPositiveDuring(null, endDate);
	
		CohortDefinition transferredIn = Cohorts.getTransferredInDuring(startDate, endDate);
		CohortDefinition controlSmearNegative = Cohorts.getAllSmearNegativeDuring(startDate, endDate);
		CohortDefinition controlSmearPositive = Cohorts.getAnySmearPositiveDuring(startDate, endDate);
		CohortDefinition firstCulturePositive = Cohorts.getFirstCulturePositiveDuring(startDate, endDate);
		CohortDefinition resistantTests = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition ambulatory = Cohorts.getMostRecentlyAmbulatoryByEnd(startDate, endDate);
		CohortDefinition pendingCultures = Cohorts.getPendingCulturesOnDate(endDate);
		CohortDefinition controlCultureNegative = Cohorts.getAllCultureNegativeDuring(startDate, endDate);
		CohortDefinition controlCulturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
		CohortDefinition completedTx = Cohorts.getTreatmentCompletedDuringFilter(startDate, endDate);
		CohortDefinition curedTx = Cohorts.getCuredDuringFilter(startDate, endDate);
		CohortDefinition failedTx = Cohorts.getFailedDuringFilter(startDate, endDate);
		CohortDefinition relapsedTx = Cohorts.getRelapsedDuringFilter(startDate, endDate);

		ReportDefinition report = new ReportDefinition();
		report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("AND", inProgram, atLocation), null);
		
		CohortCrossTabDataSetDefinition s3 = new CohortCrossTabDataSetDefinition();
		report.addDataSetDefinition("III. " + translate("section3"), s3, null);
		s3.addRow("1.a " + translate("1.a"), newlyHospitalized, null);
		s3.addRow("1.b " + translate("1.b"), allHospitalized, null);
		s3.addRow("2.a " + translate("2.a"), ReportUtil.getCompositionCohort("AND", startedTxDuring, smearPositive), null);
		s3.addRow("2.b " + translate("2.b"), ReportUtil.getCompositionCohort("AND", startedTxDuring, smearNegative), null);
		s3.addRow("2.c " + translate("2.c"), startedTxDuring, null);
		s3.addRow("3. " + translate("3"), abandoned, null);
		s3.addRow("4. " + translate("4"), died, null);
		s3.addRow("5. " + translate("5"), ReportUtil.getCompositionCohort("AND", startedTxByEnd, newlyHivPositive), null);
		s3.addRow("6. " + translate("6"), transferredIn, null);
		s3.addRow("7. " + translate("7"), controlSmearNegative, null);
		s3.addRow("8. " + translate("8"), controlSmearPositive, null);
		s3.addRow("9. " + translate("9"), firstCulturePositive, null);
		s3.addRow("10. " + translate("10"), resistantTests, null);
		s3.addRow("11. " + translate("11"), ambulatory, null);
		s3.addRow("12. " + translate("12"), pendingCultures, null);
		s3.addRow("13. " + translate("13"), everHivPositive, null);
		s3.addRow("14. " + translate("14"), controlCulturePositive, null);
		s3.addRow("15. " + translate("15"), controlCultureNegative, null);
		s3.addRow("16. " + translate("16"), ReportUtil.getCompositionCohort("OR", completedTx, curedTx), null);
		s3.addRow("17. " + translate("17"), failedTx, null);
		s3.addRow("18. " + translate("18"), relapsedTx, null);
		
		CohortCrossTabDataSetDefinition s4 = new CohortCrossTabDataSetDefinition();
		report.addDataSetDefinition("IV. " + translate("section4"), s4, null);
		Map<String, Cohort> profiles = MdrtbQueryService.getResistanceProfiles(context, endDate);
		for (Map.Entry<String, Cohort> e : profiles.entrySet()) {
			DotsDstResultCohortDefinition cd = new DotsDstResultCohortDefinition();
			cd.setMaxResultDate(endDate);
			cd.setSpecificProfile(e.getKey());
			s4.addRow(e.getKey(), cd, null);
		}
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
	       throw new MdrtbAPIException("Unable to evaluate MOH Report", e);
        }
		return data;
	}
	
	/**
	 * Utility method to translate the report indicators
	 */
	private static String translate(String key) {
		return MessageUtil.translate("dotsreports.indicatorReport."+key);
	}
}