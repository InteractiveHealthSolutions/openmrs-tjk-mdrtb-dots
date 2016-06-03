package org.openmrs.module.dotsreports.reporting.data;

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
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.dotsreports.reporting.definition.DotsBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CompositionCohortDefinitionEvaluator;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;


public class DOTS08TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form08");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form08.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		/*l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), Integer.class));*/
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTS-TB08" + 
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
    @SuppressWarnings("unchecked")
    public ReportData evaluateReport(EvaluationContext context) {

    	ReportDefinition report = new ReportDefinition();
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		// Base Cohort is confirmed mdr patients, in program, who started treatment during the quarter, optionally at location
		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		//baseCohortDefs.put("enrolled", new Mapped(Cohorts.getInDOTSProgramAndStartedTreatmentFilter(startDate, endDate), null));
		//CohortDefinition cdd = Cohorts.getInDOTSProgramEverDuring(startDate, endDate);
		//CohortDefinition cdd = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
		CohortDefinition cdd = Cohorts.getCompletedDOTSProgramsEnrolledDuring(startDate, endDate);
		if(cdd==null) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXxxxx");
		}
		else {
		baseCohortDefs.put("enrolled", new Mapped(cdd, null));
		}//baseCohortDefs.put("startedTreatment", new Mapped(Cohorts.getStartedTreatmentFilter(startDate, endDate), null));
		if (location != null) {
			CohortDefinition locationFilter = Cohorts.getLocationFilterTJK(location.getCountyDistrict(), startDate, endDate);
			if (locationFilter != null) {
				baseCohortDefs.put("location", new Mapped(locationFilter, null));
			}	
		}
		CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
		CompositionCohortDefinitionEvaluator ccde = new CompositionCohortDefinitionEvaluator();
		Cohort c = null;
		
		try {
			c = ccde.evaluate(baseCohort,context);
		} catch (EvaluationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(c!=null) {
			Set<Integer> ids = c.getMemberIds();
			
			 for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
	    	        System.out.println(iterator.next());
			 }
		}
		
		report.setBaseCohortDefinition(baseCohort, null);
		
		CohortCrossTabDataSetDefinition dsd = new CohortCrossTabDataSetDefinition();
		
		// first a column that is just the count of the base cohort
		//dsd.addColumn("StartedTreatment", null);
		
		// now get all the outcome state cohort filters, which we are going to use to create the other filters
		Map<String, CohortDefinition> outcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
		
		// now get all the group state cohort filters, which we are going to use to create the other filters
		Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
		
		// get all the patients who had an outcome 
		//CohortDefinition programClosedAfterTreatmentStart = Cohorts.getProgramClosedAfterTreatmentStartedFilter(startDate, endDate,null);
		//CohortDefinition programClosedAfterTreatmentStart = Cohorts.getDOTSProgramCompletedDuring(startDate, null);
		
		// get the patients that have positive, negative, and unknown bacteriology results
		/*CohortDefinition positiveResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, null, null, Result.POSITIVE);
		CohortDefinition negativeResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, null, null, Result.NEGATIVE);
		CohortDefinition unknownResult = Cohorts.getDotsBacResultAfterTreatmentStart(startDate, endDate, null, null, Result.UNKNOWN);*/
		
		CohortDefinition positiveResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
		CohortDefinition negativeResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.NEGATIVE);
		CohortDefinition unknownResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.UNKNOWN);
		
		//get cohort definitions for males and for females
		CohortDefinition men = Cohorts.getMalePatients();
		CohortDefinition women = Cohorts.getFemalePatients();
		
		CohortDefinition pulmonary = Cohorts.getAllPulmonaryEver();
		CohortDefinition extraPulmonary = Cohorts.getAllExtraPulmonaryEver();
		
		System.out.println(outcomes.size());
		System.out.println(groups.size());
		//System.out.println(programClosedAfterTreatmentStart.toString());
		
		System.out.println("\n\n\n\n\n\n\n\n\n COHORTS DEFS DONE \n\n\n\n\n\n\n\n");
		
		
		//ReportUtil.getCompositionCohort("AND", groups.get("New"), programClosedAfterTreatmentStart,pulmonary,positiveResult)
		//start making rows
		CohortDefinition newPulSmearPos = ReportUtil.getCompositionCohort("AND", groups.get("New"),pulmonary,positiveResult);
		 ccde = new CompositionCohortDefinitionEvaluator();
			c = null;
			
			try {
				c = ccde.evaluate(newPulSmearPos,context);
			} catch (EvaluationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(c!=null) {
				Set<Integer> ids = c.getMemberIds();
				System.out.println("NEW PULMONARY POSITIVE");
				 for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
		    	        System.out.println(iterator.next());
				 }
			}
		CohortDefinition newPulSmearNeg = ReportUtil.getCompositionCohort("AND", groups.get("New"),pulmonary,negativeResult);
		CohortDefinition newExtra = ReportUtil.getCompositionCohort("AND", groups.get("New"),extraPulmonary);
		dsd.addRow("NewPulmonarySmearPositive", newPulSmearPos , null);
		dsd.addRow("NewPulmonarySmearNegative", newPulSmearNeg, null);
		dsd.addRow("NewExtrapulmonary", newExtra, null);
		//dsd.addRow("NewTotal", ReportUtil.getCompositionCohort("AND", groups.get("New"), programClosedAfterTreatmentStart), null);
		dsd.addRow("NewTotal", ReportUtil.getCompositionCohort("OR", newPulSmearPos,newPulSmearNeg,newExtra),null);
		
		
		CohortDefinition relapse = ReportUtil.getCompositionCohort("AND", groups.get("Relapse"));
		CohortDefinition posTxAfterFailure = ReportUtil.getCompositionCohort("AND", groups.get("AfterFailure"),positiveResult);
		CohortDefinition posAfterDefault = ReportUtil.getCompositionCohort("AND", groups.get("AfterDefault"), positiveResult);
		CohortDefinition otherPositive = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other")), positiveResult);
		 ccde = new CompositionCohortDefinitionEvaluator();
			c = null;
			
			try {
				c = ccde.evaluate(otherPositive,context);
			} catch (EvaluationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(c!=null) {
				Set<Integer> ids = c.getMemberIds();
				System.out.println("OTHER POSITIVE");
				 for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
		    	        System.out.println(iterator.next());
				 }
			}
		CohortDefinition negTxAfterFailure = ReportUtil.getCompositionCohort("AND", groups.get("AfterFailure"), negativeResult);
		CohortDefinition otherNegative = ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"),groups.get("AfterDefault")), negativeResult);
		dsd.addRow("Relapse", relapse , null);
		dsd.addRow("PositiveTxAfterFailure", posTxAfterFailure, null);
		dsd.addRow("PositiveAfterDefault", posAfterDefault, null);
		dsd.addRow("OtherPositive", otherPositive, null);
		dsd.addRow("NegativeTxAfterFailure", negTxAfterFailure , null);
		dsd.addRow("OtherNegative", otherNegative, null);
		//dsd.addRow("RetreatmentTotal", ReportUtil.getCompositionCohort("AND", ReportUtil.getCompositionCohort("OR",groups.get("Relapse"),groups.get("TransferredIn"),groups.get("Other"),groups.get("AfterDefault"),groups.get("AfterFailure")),programClosedAfterTreatmentStart), null);
		dsd.addRow("RetreatmentTotal", ReportUtil.getCompositionCohort("OR", relapse,posTxAfterFailure, posAfterDefault,otherPositive,negTxAfterFailure,otherNegative ), null);
		
		dsd.addColumn("Male", ReportUtil.getCompositionCohort("AND", men), null);
		dsd.addColumn("Female", ReportUtil.getCompositionCohort("AND", women), null);
		dsd.addColumn("Total", ReportUtil.getCompositionCohort("OR", men, women), null);
		dsd.addColumn("Cured", ReportUtil.getCompositionCohort("AND", outcomes.get("Cured")), null);
		dsd.addColumn("TreatmentCompleted", ReportUtil.getCompositionCohort("AND", outcomes.get("TreatmentCompleted")), null);
		dsd.addColumn("Died", ReportUtil.getCompositionCohort("AND", outcomes.get("Died")), null);
		dsd.addColumn("Failed", ReportUtil.getCompositionCohort("AND", outcomes.get("Failed")), null);
		dsd.addColumn("Defaulted", ReportUtil.getCompositionCohort("AND", outcomes.get("Defaulted")), null);
		dsd.addColumn("TransferredOut", ReportUtil.getCompositionCohort("AND", outcomes.get("TransferredOut")), null);
		dsd.addColumn("TotalEval", ReportUtil.getCompositionCohort("OR", outcomes.get("Cured"),outcomes.get("TreatmentCompleted"), outcomes.get("Died"), outcomes.get("Failed"), outcomes.get("Defaulted"),outcomes.get("TransferredOut")), null);
		dsd.addColumn("Canceled", ReportUtil.getCompositionCohort("AND", outcomes.get("Canceled")),null);
		
		
		report.addDataSetDefinition("Treatment results", dsd, null);
		
		ReportData data;
        try {
	        data = Context.getService(ReportDefinitionService.class).evaluate(report, context);
        }
        catch (Throwable e) {
        	e.printStackTrace();
        	throw new MdrtbAPIException("Unable to evaluate WHO Form 8" +
        			" report", e);
        }
		return data;
    }
    	
 }
