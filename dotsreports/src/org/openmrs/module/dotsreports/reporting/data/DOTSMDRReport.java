package org.openmrs.module.dotsreports.reporting.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.openmrs.module.dotsreports.MdrtbConstants.TreatmentState;
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


public class DOTSMDRReport implements ReportSpecification /**
 * @see ReportSpecification#getName()
 */
{
	
public String getName() {
	return Context.getMessageSourceService().getMessage("dotsreports.dotsmdrreport");
}

/**
 * @see ReportSpecification#getDescription()
 */
public String getDescription() {
	return Context.getMessageSourceService().getMessage("dotsreports.dotsmdrreport.title");
}

/**
 * @see ReportSpecification#getParameters()
 */
public List<Parameter> getParameters() {
	List<Parameter> l = new ArrayList<Parameter>();
	l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
	l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
	//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
	
	return l;
}

/**
 * @see ReportSpecification#getRenderingModes()
 */
public List<RenderingMode> getRenderingModes() {
	List<RenderingMode> l = new ArrayList<RenderingMode>();
	l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTSMDR" + 
		(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
	return l;
}



/**
 * @see ReportSpecification#validateAndCreateContext(Map)
 */
public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
	
	EvaluationContext context = ReportUtil.constructContext(parameters);
	Integer year = (Integer)parameters.get("year");
	String quarter = (String)parameters.get("quarter");
	
	context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, null));
	
	Map<String,Object>pMap = context.getParameterValues();
	Set<String> keySet = pMap.keySet();
	
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
				CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate, false);//Cohorts.getLocationFilterTJK(location.getCountyDistrict(), startDate, endDate);
				if (locationFilter != null) {
					//baseCohortDefs.put("location", new Mapped(locationFilter, null));
					report.setBaseCohortDefinition(locationFilter, null);
				}	
			}
			/*CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
			report.setBaseCohortDefinition(baseCohort, null);*/
			
			//TOTAL REGISTERED
			CohortDefinition dotsEnrolled = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
			CohortDefinition mdrEnrolled = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
			
			CohortDefinition mdrDiagnosed = Cohorts.getMdrDetectionFilter(startDate, endDate);
			CohortDefinition rrDiagnosed = Cohorts.getRRDetectionFilter(startDate, endDate);
			CohortDefinition xdrDiagnosed = Cohorts.getXdrDetectionFilter(startDate, endDate);
			CohortDefinition hivPositive = Cohorts.getHivPositiveDuring(startDate, endDate);
			CohortDefinition rrOrMdr = ReportUtil.getCompositionCohort("OR",rrDiagnosed,mdrDiagnosed);
			
			
			Map<String, CohortDefinition> outcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
			//CURED
			//CohortDefinition cured =  Cohorts.getCuredDuringFilter(startDate, endDate);
			CohortDefinition cured =  outcomes.get("Cured");
			CohortDefinition txCompleted = outcomes.get("TreatmentCompleted");
			CohortDefinition died = outcomes.get("Died");
			CohortDefinition failed = outcomes.get("Failed");
			CohortDefinition defaulted = outcomes.get("Defaulted");
			CohortDefinition cancelled = outcomes.get("Canceled");
			//TREATMENT COMPLETED
			//CohortDefinition treatmentCompleted = Cohorts.getTreatmentCompletedDuringFilter(startDate, endDate);
			
			
			
			CohortDefinition haveDiagnosticType = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, null);
			CohortDefinition haveClinicalDiagnosis = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, TbConcepts.TB_CLINICAL_DIAGNOSIS[0]);
			CohortDefinition haveLabDiagnosis = ReportUtil.minus(haveDiagnosticType, haveClinicalDiagnosis);
			CohortDefinition haveTxOutcome = Cohorts.getHaveTreatmentOutcome(null, null, null);
			CohortDefinition haveNoDotsOutcome = ReportUtil.minus(dotsEnrolled,haveTxOutcome);
			CohortDefinition haveNoMdrOutcome = ReportUtil.minus(mdrEnrolled,haveTxOutcome);
			
			
			CohortDefinition fldTreatmentStarted = Cohorts.getFLDTreatmentStartedFilter(startDate, endDate);
			CohortDefinition sldTreatmentStarted = Cohorts.getSLDTreatmentStartedFilter(startDate, endDate);
			//CohortDefinition treatmentNotStarted = ReportUtil.minus(allTB, ReportUtil.getCompositionCohort("OR", fldTreatmentStarted, sldTreatmentStarted));
			//WAITING FOR SLD
			//TODO: NO INFORMATION YET
			
				
			Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
			
			
			CohortDefinition allNewCases = groups.get("New");
			CohortDefinition allFailures = groups.get("AfterFailure");
			CohortDefinition allDefaults = groups.get("AfterDefault");
			CohortDefinition allOthers = groups.get("Other");
			CohortDefinition allTransferred = groups.get("TransferredIn");
			CohortDefinition allRelapses = groups.get("Relapse");
			CohortDefinition newOrRelapse = ReportUtil.getCompositionCohort("OR", allNewCases, allRelapses);
			
//			CohortDefinition allRelapses= ReportUtil.getCompositionCohort("OR",groups.get("Relapse"),groups.get("AfterDefault"),groups.get("AfterFailure") ); 
//			CohortDefinition allExceptNew = ReportUtil.getCompositionCohort("OR",allRelapses,allTransferred,allOthers );
			CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",groups.get("AfterDefault"),groups.get("AfterFailure"),groups.get("Other") );
			
			
			//////////////////////			TABLE1			/////////////////////
			CohortCrossTabDataSetDefinition table1 = new CohortCrossTabDataSetDefinition();
			table1.addRow("newDsLd",ReportUtil.getCompositionCohort("AND", dotsEnrolled,newOrRelapse,haveLabDiagnosis),null);
			table1.addRow("newDsSd",ReportUtil.getCompositionCohort("AND", dotsEnrolled,newOrRelapse,haveClinicalDiagnosis),null);
			table1.addRow("retreatmentDs",ReportUtil.getCompositionCohort("AND", dotsEnrolled,allRetreatment),null);
			table1.addRow("hivSd",ReportUtil.getCompositionCohort("AND", dotsEnrolled,hivPositive),null);
			
			table1.addColumn("Total",dotsEnrolled,null);
			table1.addColumn("Cured",cured,null);
			table1.addColumn("TreatmentCompleted",txCompleted,null);
			table1.addColumn("Failure",failed,null);
			table1.addColumn("Died", died,null);
			table1.addColumn("Default", defaulted,null);
			table1.addColumn("NotEval", haveNoDotsOutcome,null);
			
			report.addDataSetDefinition("table1", table1,null);
			
			
			CohortCrossTabDataSetDefinition table2 = new CohortCrossTabDataSetDefinition();
			
			table2.addRow("rrmdr", rrOrMdr, null);
			table2.addRow("rrmdrHiv", ReportUtil.getCompositionCohort("AND",rrOrMdr,hivPositive), null);
			table2.addRow("xdr",xdrDiagnosed,null);
			
			table2.addColumn("Total",Cohorts.getSLDTreatmentStartedFilter(startDate,endDate),null);
			table2.addColumn("Cured",cured,null);
			table2.addColumn("TreatmentCompleted",txCompleted,null);
			table2.addColumn("Failure",failed,null);
			table2.addColumn("Died", died,null);
			table2.addColumn("Default", defaulted,null);
			table2.addColumn("NotEval", haveNoDotsOutcome,null);
			
			report.addDataSetDefinition("table2", table2,null);
		
			
	ReportData data;
	try {
		data = Context.getService(ReportDefinitionService.class).evaluate(
				report, context);
	} catch (EvaluationException e) {
		throw new MdrtbAPIException("Unable to evaluate WHO Form 8 report",
				e);
	}
	return data;
}
 }
