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
import org.openmrs.module.dotsreports.reporting.definition.DotsBacResultAfterTreatmentStartedCohortDefinition.Result;
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


public class WHOForm06TJK implements org.openmrs.module.dotsreports.reporting.ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form06");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form06.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		/*l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));*/
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/WHOForm06" + 
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
		if (quarter == null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleaseEnterAQuarter"));
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, null));
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
		baseCohortDefs.put("enrolledInDOTS", new Mapped(Cohorts.getInDOTSProgramAndStartedTreatmentFilter(startDate, endDate), null));
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
		
		// first a column that is just the count of the base cohort
		dsd.addColumn("StartedTreatment", null);
		
		// now get all the outcome state cohort filters, which we are going to use to create the other filters
		Map<String, CohortDefinition> outcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);

		// get all the patients who had an outcome within 6 months from treatment start
		CohortDefinition programClosedAfterTreatmentStart = Cohorts.getProgramClosedAfterTreatmentStartedFilter(startDate, endDate, 9);
		
		// create the died, defaulted, and transferred out rows
		dsd.addColumn("Died", ReportUtil.getCompositionCohort("AND", outcomes.get("Died"), programClosedAfterTreatmentStart), null);
		dsd.addColumn("Defaulted", ReportUtil.getCompositionCohort("AND", outcomes.get("Defaulted"), programClosedAfterTreatmentStart), null);
		dsd.addColumn("TransferredOut", ReportUtil.getCompositionCohort("AND", outcomes.get("TransferredOut"), programClosedAfterTreatmentStart), null);
		
		// create an "other" row to capture any other outcomes that may have occurred (technically, this should always be 0, because a patient couldn't have failed or be cured within 9 mths)
		dsd.addColumn("Other", ReportUtil.getCompositionCohort("AND", outcomes.get("Failed"), outcomes.get("Cured"), 
			outcomes.get("TreatmentCompleted"), programClosedAfterTreatmentStart), null);
		
		// get the patients that have positive, negative, and unknown bacteriology results
		CohortDefinition positiveResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, 8, 9, Result.POSITIVE);
		CohortDefinition negativeResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, 8, 9, Result.NEGATIVE);
		CohortDefinition unknownResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, 8, 9, Result.UNKNOWN);
		
		// now create these rows by subtracting any that had an outcome within nine months of treatment start
		dsd.addColumn("Negative", ReportUtil.minus(negativeResult, programClosedAfterTreatmentStart), null);
		dsd.addColumn("Positive", ReportUtil.minus(positiveResult, programClosedAfterTreatmentStart), null);
		dsd.addColumn("Unknown", ReportUtil.minus(unknownResult, programClosedAfterTreatmentStart), null);
		
		report.addDataSetDefinition("sixMonthStatus", dsd, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (EvaluationException e) {
        	throw new MdrtbAPIException("Unable to evaluate WHO Form 10y report", e);
        }
		return data;
    }
    	
 }
