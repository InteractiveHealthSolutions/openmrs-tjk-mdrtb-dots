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
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.dotsreports.reporting.definition.DotsBacResultAfterTreatmentStartedCohortDefinition.Result;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CompositionCohortDefinitionEvaluator;
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


public class DOTS08TJKUpdated implements ReportSpecification /**
 * @see ReportSpecification#getName()
 */
{
	
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
	//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
	l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("dotsreports.quarter"), String.class));
	return l;
}

/**
 * @see ReportSpecification#getRenderingModes()
 */
public List<RenderingMode> getRenderingModes() {
	List<RenderingMode> l = new ArrayList<RenderingMode>();
	l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTS-TB08-OMAR" + 
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
	
	Map<String,Object>pMap = context.getParameterValues();
	Set<String> keySet = pMap.keySet();
	System.out.println("PARAMS");
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
				CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate, true);//Cohorts.getLocationFilterTJK(location.getCountyDistrict(), startDate, endDate);
				if (locationFilter != null) {
					//baseCohortDefs.put("location", new Mapped(locationFilter, null));
					report.setBaseCohortDefinition(locationFilter, null);
				}	
			}
			/*CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
			report.setBaseCohortDefinition(baseCohort, null);*/
			
			//TOTAL REGISTERED
			//CohortDefinition allTB = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
			CohortDefinition dstb = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
			CohortDefinition drtb = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
			
			CohortDefinition allTB = ReportUtil.getCompositionCohort("OR", dstb,drtb);
			
			Map<String, CohortDefinition> dotsoutcomes = ReportUtil.getDotsOutcomesFilterSet(startDate, endDate);
			Map<String, CohortDefinition> mdroutcomes = ReportUtil.getMdrtbOutcomesFilterSet(startDate, endDate);
			//CURED
			//CohortDefinition cured =  Cohorts.getCuredDuringFilter(startDate, endDate);
			CohortDefinition dotscured =  dotsoutcomes.get("Cured");
			CohortDefinition dotstxCompleted = dotsoutcomes.get("TreatmentCompleted");
			CohortDefinition dotsdied = dotsoutcomes.get("Died");
			CohortDefinition dotsfailed = dotsoutcomes.get("Failed");
			CohortDefinition dotsdefaulted = dotsoutcomes.get("Defaulted");
			CohortDefinition dotstout = dotsoutcomes.get("TransferredOut");
			CohortDefinition dotscancelled = dotsoutcomes.get("Canceled");
			CohortDefinition dotsstartedSLD =dotsoutcomes.get("StartedSLD");
			//TREATMENT COMPLETED
			//CohortDefinition treatmentCompleted = Cohorts.getTreatmentCompletedDuringFilter(startDate, endDate);
			
			CohortDefinition mdrcured =  mdroutcomes.get("Cured");
			CohortDefinition mdrtxCompleted = mdroutcomes.get("TreatmentCompleted");
			CohortDefinition mdrdied = mdroutcomes.get("Died");
			CohortDefinition mdrfailed = mdroutcomes.get("Failed");
			CohortDefinition mdrdefaulted = mdroutcomes.get("Defaulted");
			CohortDefinition mdrtout = mdroutcomes.get("TransferredOut");
			//CohortDefinition mdrcancelled = mdroutcomes.get("Canceled");
			//CohortDefinition mdrstartedSLD = mdroutcomes.get("StartedSLD");
			
			CohortDefinition cured =  ReportUtil.getCompositionCohort("OR", dotscured,mdrcured);
			CohortDefinition txCompleted = ReportUtil.getCompositionCohort("OR", dotstxCompleted,mdrtxCompleted);
			CohortDefinition died = ReportUtil.getCompositionCohort("OR", dotsdied,mdrdied);
			CohortDefinition failed = ReportUtil.getCompositionCohort("OR", dotsfailed,mdrfailed);
			CohortDefinition defaulted = ReportUtil.getCompositionCohort("OR", dotsdefaulted,mdrdefaulted);
			CohortDefinition tout = ReportUtil.getCompositionCohort("OR", dotstout,mdrtout);
			CohortDefinition cancelled = dotscancelled;
			CohortDefinition startedSLD =dotsstartedSLD;
			
			CohortDefinition pulmonary = Cohorts.getAllPulmonaryDuring(startDate,endDate);
			CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryDuring(startDate,endDate);
			
			
			CohortDefinition smearPositive = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
			CohortDefinition rapidTestPositive = Cohorts.getAnyXpertOrHAINPositiveDuring(startDate, endDate);
			
			/*CohortDefinition haveDiagnosticType = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, null);
			CohortDefinition haveClinicalDiagnosis = Cohorts.getHaveDiagnosticTypeDuring(startDate, endDate, TbConcepts.TB_CLINICAL_DIAGNOSIS[0]);
			CohortDefinition haveLabDiagnosis = ReportUtil.minus(haveDiagnosticType, haveClinicalDiagnosis);*/
			
			CohortDefinition haveLabDiagnosis = ReportUtil.getCompositionCohort("OR", smearPositive,  rapidTestPositive);
			CohortDefinition haveClinicalDiagnosis = ReportUtil.minus(allTB,haveLabDiagnosis);
			
			CohortDefinition haveTxOutcome = Cohorts.getHaveTreatmentOutcome(null, null, null);
			CohortDefinition haveNoTxOutcome = ReportUtil.minus(allTB,haveTxOutcome);
			//sCohortDefinition onSLDWaitingList
			
			
			/*CohortDefinition culturePositive = Cohorts.getAnyCulturePositiveDuring(startDate, endDate);
			CohortDefinition smearPositive = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
			CohortDefinition bacteriology = ReportUtil.getCompositionCohort("OR", smearPositive, culturePositive);*/
			
			//CohortDefinition diedDuringTreatment =  Cohorts.getDiedDuringFilter(startDate, endDate);
			
			//DIED:TB
			CohortDefinition tbDied = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.CAUSE_OF_DEATH).getId(),
					null, null, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.DEATH_BY_TB).getId());
			
			
			
			//DIED: NON-TB
			//CohortDefinition nonTbDeath = ReportUtil.minus(diedDuringTreatment, tbDied);
			CohortDefinition nonTbDeath = ReportUtil.getCodedObsCohort(TimeModifier.ANY, Context.getService(TbService.class).getConcept(TbConcepts.CAUSE_OF_DEATH).getId(),
					null, null, SetComparator.IN, Context.getService(TbService.class).getConcept(TbConcepts.DEATH_BY_OTHER_DISEASES).getId());
			
			//FAILURE
			//CohortDefinition failure = Cohorts.getFailedDuringFilter(startDate, endDate);
			
			//DEFAULTED
			//CohortDefinition defaulted = Cohorts.getDefaultedDuringFilter(startDate, endDate);
			
			//FLD NOT STARTED
			
			
			/*if (location!=null)
			{
				CohortDefinition treatmentStarted = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate,endDate);
				treatmentNotStarted = ReportUtil.minus(allTB, treatmentStarted);
			}*/
			
			//CohortDefinition fldTreatmentStarted = Cohorts.getFLDTreatmentStartedFilter(startDate, endDate);
			
			//CohortDefinition fldTreatmentStarted = Cohorts.getHaveDOTSTxStartDate(startDate, endDate);
			CohortDefinition sldTreatmentStarted = ReportUtil.getCompositionCohort("OR", startedSLD, Cohorts.getSLDTreatmentStartedFilter(startDate, endDate));
			//CohortDefinition treatmentNotStarted = ReportUtil.minus(allTB, ReportUtil.getCompositionCohort("OR", fldTreatmentStarted, sldTreatmentStarted));
			//WAITING FOR SLD
			//TODO: NO INFORMATION YET
			
			//CANCELLED
			//TODO: NO INFORMATION YET
			
			GregorianCalendar gc = new GregorianCalendar();
			gc.set(1900, 0, 1, 0, 0, 1);
			//ENROLLED FOR SLD
			CohortDefinition mdr = Cohorts.getInMdrProgramEverDuring(gc.getTime(), endDate);
			
			
			//New Age filters added by Omar
			CohortDefinition age04=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 0, 5);
			CohortDefinition age0514=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 5, 15);
			CohortDefinition age1517=Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 15, 18);
				
			Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
			
			
			/*CohortDefinition allNewCases = groups.get("New");
			CohortDefinition allFailures = groups.get("AfterFailure");
			CohortDefinition allDefaults = groups.get("AfterDefault");
			CohortDefinition allOthers = groups.get("Other");
			CohortDefinition allTransferred = groups.get("TransferredIn");
			
//			CohortDefinition allRelapses= ReportUtil.getCompositionCohort("OR",groups.get("Relapse"),groups.get("AfterDefault"),groups.get("AfterFailure") ); 
//			CohortDefinition allExceptNew = ReportUtil.getCompositionCohort("OR",allRelapses,allTransferred,allOthers );
			CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",groups.get("AfterDefault"),groups.get("AfterFailure"),groups.get("Other"),groups.get("TransferredIn") );
			CohortDefinition allRelapses = groups.get("Relapse");*/
			
			/*Map<String, CohortDefinition> dotsgroups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
			Map<String, CohortDefinition> mdrgroups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
			
			CohortDefinition dotsallNewCases = dotsgroups.get("New");
			CohortDefinition dotsallFailures = dotsgroups.get("AfterFailure");
			CohortDefinition dotsallDefault = dotsgroups.get("AfterDefault");
			CohortDefinition dotsallOthers = dotsgroups.get("Other");
			CohortDefinition dotsallTransferred = dotsgroups.get("TransferredIn");
			CohortDefinition dotsallRelapses = dotsgroups.get("Relapse");
			
			CohortDefinition mdrallNewCases = mdrgroups.get("New");
			CohortDefinition mdrallFailures = mdrgroups.get("AfterFailure");
			CohortDefinition mdrallDefault = mdrgroups.get("AfterDefault");
			CohortDefinition mdrallOthers = mdrgroups.get("Other");
			CohortDefinition mdrallTransferred = mdrgroups.get("TransferredIn");
			CohortDefinition mdrallRelapses = mdrgroups.get("Relapse");
			
			CohortDefinition allNewCases = ReportUtil.getCompositionCohort("OR", dotsallNewCases,mdrallNewCases);
			CohortDefinition allFailures = ReportUtil.getCompositionCohort("OR", dotsallFailures,mdrallFailures);
			CohortDefinition allDefault = ReportUtil.getCompositionCohort("OR", dotsallDefault,mdrallDefault);
			CohortDefinition allOthers = ReportUtil.getCompositionCohort("OR", dotsallOthers,mdrallOthers);
			CohortDefinition allTransferred = ReportUtil.getCompositionCohort("OR", dotsallTransferred,mdrallTransferred);
			CohortDefinition allRelapses = ReportUtil.getCompositionCohort("OR", dotsallRelapses,mdrallRelapses);
			
			CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",allDefault,allFailures,allOthers,allTransferred );*/
			
			Map<String, CohortDefinition> dotsgroups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
			Map<String, CohortDefinition> mdrgroups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
			
			CohortDefinition dotsallNewCases = dotsgroups.get("New");
			CohortDefinition dotsallFailures = ReportUtil.getCompositionCohort("OR", dotsgroups.get("AfterFailure1"),dotsgroups.get("AfterFailure2"));
			CohortDefinition dotsallDefault = ReportUtil.getCompositionCohort("OR", dotsgroups.get("AfterDefault1"),dotsgroups.get("AfterDefault2"));
			CohortDefinition dotsallOthers = dotsgroups.get("Other");
			CohortDefinition dotsallTransferred = dotsgroups.get("TransferredIn");
			CohortDefinition dotsallRelapses = ReportUtil.getCompositionCohort("OR", dotsgroups.get("Relapse1"),dotsgroups.get("Relapse2"));
			
			CohortDefinition mdrallNewCases = mdrgroups.get("New");
			CohortDefinition mdrallFailures = ReportUtil.getCompositionCohort("OR", mdrgroups.get("AfterFailure1"),mdrgroups.get("AfterFailure2"));
			CohortDefinition mdrallDefault =ReportUtil.getCompositionCohort("OR", mdrgroups.get("AfterDefault1"),mdrgroups.get("AfterDefault2"));
			CohortDefinition mdrallOthers = mdrgroups.get("Other");
			CohortDefinition mdrallTransferred = mdrgroups.get("TransferredIn");
			CohortDefinition mdrallRelapses = ReportUtil.getCompositionCohort("OR", mdrgroups.get("Relapse1"),mdrgroups.get("Relapse2"));
			
			CohortDefinition allNewCases = ReportUtil.getCompositionCohort("OR", dotsallNewCases,mdrallNewCases);
			CohortDefinition allFailures = ReportUtil.getCompositionCohort("OR", dotsallFailures,mdrallFailures);
			CohortDefinition allDefault = ReportUtil.getCompositionCohort("OR", dotsallDefault,mdrallDefault);
			CohortDefinition allOthers = ReportUtil.getCompositionCohort("OR", dotsallOthers,mdrallOthers);
			CohortDefinition allTransferred = ReportUtil.getCompositionCohort("OR", dotsallTransferred,mdrallTransferred);
			CohortDefinition allRelapses = ReportUtil.getCompositionCohort("OR", dotsallRelapses,mdrallRelapses);
			
			CohortDefinition allRetreatment= ReportUtil.getCompositionCohort("OR",allDefault,allFailures,allOthers,allTransferred ); 
			
			CohortDefinition pulmonaryLabDiagnosis = ReportUtil.getCompositionCohort("AND", pulmonary, haveLabDiagnosis);
			CohortDefinition pulmonaryClinicalDiagnosis = ReportUtil.getCompositionCohort("AND", pulmonary, haveClinicalDiagnosis);
			//////////////////////			TABLE1			/////////////////////
			CohortCrossTabDataSetDefinition table1 = new CohortCrossTabDataSetDefinition();
			
			
			
			//////////////////////////////////////////////
			/////				ROWS				  ///
			////////////////////////////////////////////
			
			//NEW CASES
			table1.addRow("newptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveLabDiagnosis), null);
			table1.addRow("newptbld04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveLabDiagnosis,age04), null);
			table1.addRow("newptbld0514", ReportUtil.getCompositionCohort("AND", haveLabDiagnosis, pulmonary,allNewCases,allTB, age0514),null);
			table1.addRow("newptbld1517", ReportUtil.getCompositionCohort("AND", haveLabDiagnosis, pulmonary,allNewCases, allTB,age1517),null);

			
			table1.addRow("newptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveClinicalDiagnosis), null);
			table1.addRow("newptbsd04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allNewCases,haveClinicalDiagnosis,age04), null);
			table1.addRow("newptbsd0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allNewCases,allTB, age0514),null);
			table1.addRow("newptbsd1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allNewCases, allTB,age1517),null);

			table1.addRow("neweptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allNewCases,haveClinicalDiagnosis), null);
			table1.addRow("neweptb04", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allNewCases,haveClinicalDiagnosis,age04), null);
			table1.addRow("neweptb0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allNewCases,allTB, age0514),null);
			table1.addRow("neweptb1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allNewCases, allTB,age1517),null);
			
			table1.addRow("newTotal", ReportUtil.getCompositionCohort("AND", allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("newTotal04", ReportUtil.getCompositionCohort("AND",age04, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("newTotal0514", ReportUtil.getCompositionCohort("AND",age0514, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("newTotal1517", ReportUtil.getCompositionCohort("AND",age1517, allTB, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			
			//RELAPSES
			table1.addRow("relapseptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis), null);
			table1.addRow("relapseptbld04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age04), null);
			table1.addRow("relapseptbld0514", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age0514),null);
			table1.addRow("relapseptbld1517", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveLabDiagnosis,age1517),null);
			
			table1.addRow("relapseptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveClinicalDiagnosis), null);
			table1.addRow("relapseptbsd04", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allRelapses,haveClinicalDiagnosis,age04), null);
			table1.addRow("relapseptbsd0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allRelapses,allTB, age0514),null);
			table1.addRow("relapseptbsd1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, pulmonary,allRelapses, allTB,age1517),null);

			table1.addRow("relapseeptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allRelapses,haveClinicalDiagnosis), null);
			table1.addRow("relapseeptb04", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allRelapses,haveClinicalDiagnosis,age04), null);
			table1.addRow("relapseeptb0514", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allRelapses,allTB, age0514),null);
			table1.addRow("relapseeptb1517", ReportUtil.getCompositionCohort("AND", haveClinicalDiagnosis, extrapulmonary,allRelapses, allTB,age1517),null);
			
			table1.addRow("relapseTotal", ReportUtil.getCompositionCohort("AND", allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("relapseTotal04", ReportUtil.getCompositionCohort("AND",age04, allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("relapseTotal0514", ReportUtil.getCompositionCohort("AND",age0514, allRelapses, allNewCases,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			table1.addRow("relapseTotal1517", ReportUtil.getCompositionCohort("AND",age1517, allTB, allRelapses,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			
			//FAILURE
			table1.addRow("failureptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allFailures,haveLabDiagnosis), null);
			table1.addRow("failureptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allFailures,haveClinicalDiagnosis), null);
			table1.addRow("failureeptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allFailures,haveClinicalDiagnosis), null);
			//table1.addRow("failureTotal", ReportUtil.getCompositionCohort("AND", allTB, allFailures,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			
			//DEFAULT
			table1.addRow("defaultptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allDefault,haveLabDiagnosis), null);
			table1.addRow("defaultptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allDefault,haveClinicalDiagnosis), null);
			table1.addRow("defaulteptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allDefault,haveClinicalDiagnosis), null);
			table1.addRow("defaultTotal", ReportUtil.getCompositionCohort("AND", allTB, allDefault,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			
			
			//OTHER
			table1.addRow("otherptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allOthers,haveLabDiagnosis), null);
			table1.addRow("otherptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,allOthers,haveClinicalDiagnosis), null);
			table1.addRow("othereptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB,allOthers,haveClinicalDiagnosis), null);
			table1.addRow("otherTotal", ReportUtil.getCompositionCohort("AND", allTB, allOthers,ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary) ) ,null);
			
			//TOTAL
			table1.addRow("totalptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveLabDiagnosis),null);
			table1.addRow("totalptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveClinicalDiagnosis),null);
			table1.addRow("totaleptb", ReportUtil.getCompositionCohort("AND", extrapulmonary,allTB),null);
//			table1.addRow("totalptbld", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveLabDiagnosis),null);
//			table1.addRow("totalptbsd", ReportUtil.getCompositionCohort("AND", pulmonary,allTB,haveClinicalDiagnosis),null);
			table1.addRow("totaltotal", ReportUtil.getCompositionCohort("AND", allTB, ReportUtil.getCompositionCohort("OR",pulmonaryLabDiagnosis,pulmonaryClinicalDiagnosis,extrapulmonary)) ,null);
			
			/////////////////////////////////////////////////////
			////      			COLUMNS           			////
			///////////////////////////////////////////////////
			table1.addColumn("reg", allTB,null);
			////table1.addColumn("eval", ReportUtil.minus(haveTxOutcome, cancelled), null);
			table1.addColumn("eval",  ReportUtil.getCompositionCohort("OR",cured,txCompleted,died,failed,defaulted,tout),null);
			//table1.addColumn("eval", ReportUtil.getCompositionCohort("OR",ReportUtil.minus(haveTxOutcome, cancelled),cured,txCompleted,died,failed,defaulted,ReportUtil.getCompositionCohort("AND",allTB,treatmentNotStarted),ReportUtil.getCompositionCohort("AND",ReportUtil.getCompositionCohort("AND", mdr,treatmentNotStarted))),null);
			table1.addColumn("cured", cured, null);
			table1.addColumn("tx", txCompleted, null);
			
			
			table1.addColumn("tbdeath", tbDied, null);
			table1.addColumn("notbdeath", nonTbDeath, null);
			table1.addColumn("fail", failed, null);
			table1.addColumn("def", defaulted, null);
			table1.addColumn("tout", tout, null);
		//	table1.addColumn("nofld", ReportUtil.getCompositionCohort("AND",haveNoTxOutcome,treatmentNotStarted), null);
		//	table1.addColumn("nosld", ReportUtil.getCompositionCohort("AND",haveNoTxOutcome,ReportUtil.getCompositionCohort("AND", mdr,treatmentNotStarted)), null);
			
			table1.addColumn("coltotal",ReportUtil.getCompositionCohort("OR",cured,txCompleted,died,failed,defaulted,tout),null);
			table1.addColumn("canceled", cancelled,null);
			//table1.addColumn("sld", ReportUtil.getCompositionCohort("AND",allTB,sldTreatmentStarted), null);
			table1.addColumn("sld", ReportUtil.getCompositionCohort("AND",allTB,startedSLD), null);
			
			report.addDataSetDefinition("tbl", table1, null);
		
			
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
