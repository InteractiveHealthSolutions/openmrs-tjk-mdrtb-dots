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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.reporting.data.Cohorts;
import org.openmrs.module.mdrtb.Oblast;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;


public class TB07TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb07");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("mdrtb.tb07.title");
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("mdrtb.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("mdrtb.year"), Integer.class));
		//l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarter"), Integer.class));
		l.add(new Parameter("quarter", Context.getMessageSourceService().getMessage("mdrtb.quarterOptional"), String.class));
		l.add(new Parameter("month", Context.getMessageSourceService().getMessage("mdrtb.monthOptional"), String.class));
		
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
		String oblast = (String) parameters.get("oblast");
		String month = (String) parameters.get("month");
		if (quarter == null && month==null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("mdrtb.error.pleaseEnterAQuarterOrMonth"));
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, quarter, month));
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
    public ReportData evaluateReport(EvaluationContext context) {
		
		ReportDefinition report = new ReportDefinition();
		
		//OBLAST
				String oblast = (String) context.getParameterValue("oblast");
				//\\OBLAST
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		
		//OBLAST
				Oblast o = null;
				if(!oblast.equals("") && location == null)
					o =  Context.getService(MdrtbService.class).getOblast(Integer.parseInt(oblast));
				
				List<Location> locList = new ArrayList<Location>();
				if(o != null && location == null)
					locList = Context.getService(MdrtbService.class).getLocationsFromOblastName(o);
				else if (location != null)
					locList.add(location);
				
				if(location != null)
					context.addParameterValue("location", location.getName()); 
				else if(o != null)
					context.addParameterValue("location", o.getName()); 
				else
					context.addParameterValue("location", "All"); 
				//\\OBLAST
		
		// Set base cohort to patients assigned to passed location, if applicable
		//CohortDefinition locationFilter = Cohorts.getLocationFilter(location, startDate, endDate);
				//OBLAST
				if (!locList.isEmpty()){
					List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();
					for(Location loc : locList)
						cohortDefinitions.add(Cohorts.getLocationFilter(loc, startDate, endDate));
						
					if(!cohortDefinitions.isEmpty()){
						report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("OR", cohortDefinitions), null);
						//report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("AND", Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate), report.getBaseCohortDefinition().getParameterizable()));
						//report.getBaseCohortDefinition().
					}
				}
				
//		CohortDefinition locationFilter;
//		
//		if(location!=null)
//			//locationFilter = Cohorts.getTreatmentStartAndAddressFilterTJK(location.getCountyDistrict(), startDate, endDate);
//			locationFilter = ReportUtil.getCompositionCohort("AND", Cohorts.getLocationFilter(location, startDate, endDate),Cohorts.getStartedTreatmentFilter(startDate, endDate));
//		
//		else
//			locationFilter= Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);
//		
//		if (locationFilter != null) {
//			
//			report.setBaseCohortDefinition(locationFilter, null);
//		}
//		
		CohortDefinition drtb = Cohorts.getEnrolledInMDRProgramDuring(startDate, endDate);

		// Add the data set definitions
		CohortCrossTabDataSetDefinition labResultDsd = new CohortCrossTabDataSetDefinition();
		//CohortDefinition polydr = Cohorts.getPolydrDetectionFilter(startDate, endDate);
		/*CohortDefinition mdr = Cohorts.getMdrDetectionFilter(startDate, endDate);
		CohortDefinition xdr = Cohorts.getXdrDetectionFilter(startDate, endDate);*/
		CohortDefinition totalDetected = drtb;
		CohortDefinition mdrOnly = Cohorts.getResistanceTypeFilter(startDate, endDate,TbClassification.MDR_TB);
		CohortDefinition xdr = Cohorts.getResistanceTypeFilter(startDate, endDate,TbClassification.XDR_TB);
		CohortDefinition polydr = Cohorts.getResistanceTypeFilter(startDate, endDate,TbClassification.POLY_RESISTANT_TB);
		CohortDefinition rr = Cohorts.getResistanceTypeFilter(startDate, endDate,TbClassification.RIF_RESISTANT_TB);
		CohortDefinition prexdr = Cohorts.getResistanceTypeFilter(startDate, endDate,TbClassification.PRE_XDR_TB);
		
		CohortDefinition mdr = ReportUtil.getCompositionCohort("OR", mdrOnly,rr);
		
		 mdr = ReportUtil.getCompositionCohort("AND", mdr, drtb);
		 polydr = ReportUtil.getCompositionCohort("AND", polydr, drtb);
		 prexdr = ReportUtil.getCompositionCohort("AND", prexdr, drtb);
		 xdr = ReportUtil.getCompositionCohort("AND", xdr, drtb);
		 
		// note that for the purpose of this report, the polydr, mdr, and xdr cohorts should be mutually exclusive
		// that is, by the standard definition, any patients that are mdr or xdr are also polydr; but we 
		// do not want to include the mdr and xdr patients in our poly count, hence why we use the "minus" method here
		//labResultDsd.addColumn("polydr", ReportUtil.minus(polydr, mdr, xdr), null);
		//labResultDsd.addColumn("mdr", ReportUtil.minus(mdr, xdr), null);
		labResultDsd.addColumn("total", totalDetected, null);
		labResultDsd.addColumn("mdr", mdr, null);
		labResultDsd.addColumn("xdr", xdr, null);
		labResultDsd.addColumn("prexdr", prexdr, null);
		labResultDsd.addColumn("pdr", polydr, null);
		report.addDataSetDefinition("labDetections", labResultDsd, null);
		
		CohortCrossTabDataSetDefinition treatmentDsd = new CohortCrossTabDataSetDefinition();
		
		CohortDefinition startedTreatment = Cohorts.getStartedTreatmentFilter(startDate, endDate);
		startedTreatment = ReportUtil.getCompositionCohort("AND", startedTreatment, drtb);
		
		Map<String, CohortDefinition> groups = ReportUtil.getMdrtbPreviousTreatmentFilterSet(startDate, endDate);
		
		
		
		CohortDefinition newPatients = groups.get("New");
		CohortDefinition relapse1 = groups.get("Relapse1");
		CohortDefinition relapse2 = groups.get("Relapse2");
		CohortDefinition default1 = groups.get("AfterDefault1");
		CohortDefinition default2 = groups.get("AfterDefault2");
		CohortDefinition failure1 = groups.get("AfterFailure1");
		CohortDefinition failure2 = groups.get("AfterFailure2");
		//CohortDefinition transferIn = groups.get("TransferredIn");
		CohortDefinition other = groups.get("Other");
		
		CohortDefinition pat04 = Cohorts.getAgeAtRegistration(startDate, endDate, 0, 4);
		CohortDefinition pat0514 = Cohorts.getAgeAtRegistration(startDate, endDate, 5, 14);
		CohortDefinition pat1517 = Cohorts.getAgeAtRegistration(startDate, endDate, 5, 17);
		CohortDefinition tbhiv = Cohorts.getHivPositiveDuring(startDate, endDate);
		/*
		 * 
		 * map.put("Relapse1", relapse1);
			map.put("Relapse2", relapse2);
			map.put("AfterDefault1", afterDefault1);
			map.put("AfterDefault2", afterDefault2);
			//map.put("AfterFailure", treatmentAfterFailure);
			map.put("AfterFailure1", treatmentAfterFailure1);
			map.put("AfterFailure2", treatmentAfterFailure2);
			//map.put("AfterFailureCategoryII", failureCatII);
			map.put("TransferredIn", transferred);
			map.put("Other", other);
		 */
		
		CohortDefinition shortRegimen = Cohorts.getSLDRegimenFilter(startDate, endDate, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_2_SHORT));
		CohortDefinition standardRegimen = Cohorts.getSLDRegimenFilter(startDate, endDate, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_2_STANDARD));
		CohortDefinition individualizedRegimen = Cohorts.getSLDRegimenFilter(startDate, endDate, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.REGIMEN_2_INDIVIDUALIZED));
		
		mdr = ReportUtil.getCompositionCohort("AND", mdr, startedTreatment);
		polydr = ReportUtil.getCompositionCohort("AND", polydr, startedTreatment);
		prexdr = ReportUtil.getCompositionCohort("AND", prexdr, startedTreatment);
		xdr = ReportUtil.getCompositionCohort("AND", xdr, startedTreatment);
		
		CohortDefinition shortMdr = ReportUtil.getCompositionCohort("AND", mdr, shortRegimen);
		CohortDefinition standardMdr = ReportUtil.getCompositionCohort("AND", mdr, standardRegimen);
		//CohortDefinition indivMdr = ReportUtil.getCompositionCohort("AND", mdr, individualizedRegimen);
		
		
		
		CohortDefinition xdrprexdr = ReportUtil.getCompositionCohort("OR", prexdr,xdr);
		CohortDefinition standardXdrprexdr = ReportUtil.getCompositionCohort("AND", xdrprexdr, standardRegimen);
		CohortDefinition indivXdrprexdr = ReportUtil.getCompositionCohort("AND", xdrprexdr, individualizedRegimen);
		
		
		
		//ROWS
		treatmentDsd.addRow("mdr", mdr, null);
		treatmentDsd.addRow("mdr04", ReportUtil.getCompositionCohort("AND", mdr,pat04), null);
		treatmentDsd.addRow("mdr0514", ReportUtil.getCompositionCohort("AND", mdr,pat0514), null);
		treatmentDsd.addRow("mdr1517", ReportUtil.getCompositionCohort("AND", mdr,pat1517), null);
		treatmentDsd.addRow("mdrhiv", ReportUtil.getCompositionCohort("AND", mdr,tbhiv), null);
		
		treatmentDsd.addRow("pdr", polydr, null);
		treatmentDsd.addRow("pdr04", ReportUtil.getCompositionCohort("AND", polydr,pat04), null);
		treatmentDsd.addRow("pdr0514", ReportUtil.getCompositionCohort("AND", polydr,pat0514), null);
		treatmentDsd.addRow("pdr1517", ReportUtil.getCompositionCohort("AND", polydr,pat1517), null);
		treatmentDsd.addRow("pdrhiv", ReportUtil.getCompositionCohort("AND", polydr,tbhiv), null);
		
		treatmentDsd.addRow("prexdr", prexdr, null);
		treatmentDsd.addRow("prexdr04", ReportUtil.getCompositionCohort("AND", prexdr,pat04), null);
		treatmentDsd.addRow("prexdr0514", ReportUtil.getCompositionCohort("AND", prexdr,pat0514), null);
		treatmentDsd.addRow("prexdr1517", ReportUtil.getCompositionCohort("AND", prexdr,pat1517), null);
		treatmentDsd.addRow("prexdrhiv", ReportUtil.getCompositionCohort("AND", prexdr,tbhiv), null);
		
		treatmentDsd.addRow("xdr", xdr, null);
		treatmentDsd.addRow("xdr04", ReportUtil.getCompositionCohort("AND", xdr,pat04), null);
		treatmentDsd.addRow("xdr0514", ReportUtil.getCompositionCohort("AND", xdr,pat0514), null);
		treatmentDsd.addRow("xdr1517", ReportUtil.getCompositionCohort("AND", xdr,pat1517), null);
		treatmentDsd.addRow("xdrhiv", ReportUtil.getCompositionCohort("AND", xdr,tbhiv), null);
		
		treatmentDsd.addRow("total", startedTreatment, null);
		treatmentDsd.addRow("total04", ReportUtil.getCompositionCohort("AND", startedTreatment,pat04), null);
		treatmentDsd.addRow("total0514", ReportUtil.getCompositionCohort("AND", startedTreatment,pat0514), null);
		treatmentDsd.addRow("total1517", ReportUtil.getCompositionCohort("AND", startedTreatment,pat1517), null);
		treatmentDsd.addRow("totalhiv", ReportUtil.getCompositionCohort("AND", startedTreatment,tbhiv), null);
		
		treatmentDsd.addRow("mdrshr", shortMdr, null);
		treatmentDsd.addRow("mdrshr04", ReportUtil.getCompositionCohort("AND", shortMdr,pat04), null);
		treatmentDsd.addRow("mdrshr0514", ReportUtil.getCompositionCohort("AND", shortMdr,pat0514), null);
		treatmentDsd.addRow("mdrshr1517", ReportUtil.getCompositionCohort("AND", shortMdr,pat1517), null);
		treatmentDsd.addRow("mdrshrhiv", ReportUtil.getCompositionCohort("AND", shortMdr,tbhiv), null);
		
		treatmentDsd.addRow("mdrstr", standardMdr, null);
		treatmentDsd.addRow("mdrstr04", ReportUtil.getCompositionCohort("AND", standardMdr,pat04), null);
		treatmentDsd.addRow("mdrstr0514", ReportUtil.getCompositionCohort("AND", standardMdr,pat0514), null);
		treatmentDsd.addRow("mdrstr1517", ReportUtil.getCompositionCohort("AND", standardMdr,pat1517), null);
		treatmentDsd.addRow("mdrstrhiv", ReportUtil.getCompositionCohort("AND", standardMdr,tbhiv), null);
		
		treatmentDsd.addRow("mdrtotal", mdr, null);
		treatmentDsd.addRow("mdrtotal04", ReportUtil.getCompositionCohort("AND", mdr,pat04), null);
		treatmentDsd.addRow("mdrtotal0514", ReportUtil.getCompositionCohort("AND", mdr,pat0514), null);
		treatmentDsd.addRow("mdrtotal1517", ReportUtil.getCompositionCohort("AND", mdr,pat1517), null);
		treatmentDsd.addRow("mdrtotalhiv", ReportUtil.getCompositionCohort("AND", mdr,tbhiv), null);
		
		treatmentDsd.addRow("xdrind", indivXdrprexdr, null);
		treatmentDsd.addRow("xdrind04", ReportUtil.getCompositionCohort("AND", indivXdrprexdr,pat04), null);
		treatmentDsd.addRow("xdrind0514", ReportUtil.getCompositionCohort("AND", indivXdrprexdr,pat0514), null);
		treatmentDsd.addRow("xdrind1517", ReportUtil.getCompositionCohort("AND", indivXdrprexdr,pat1517), null);
		treatmentDsd.addRow("xdrindhiv", ReportUtil.getCompositionCohort("AND", indivXdrprexdr,tbhiv), null);
		
		
		treatmentDsd.addRow("xdrstr", standardXdrprexdr, null);
		treatmentDsd.addRow("xdrstr04", ReportUtil.getCompositionCohort("AND", standardXdrprexdr,pat04), null);
		treatmentDsd.addRow("xdrstr0514", ReportUtil.getCompositionCohort("AND", standardXdrprexdr,pat0514), null);
		treatmentDsd.addRow("xdrstr1517", ReportUtil.getCompositionCohort("AND", standardXdrprexdr,pat1517), null);
		treatmentDsd.addRow("xdrstrhiv", ReportUtil.getCompositionCohort("AND", standardXdrprexdr,tbhiv), null);
		
		treatmentDsd.addRow("xdrtotal", xdrprexdr, null);
		treatmentDsd.addRow("xdrtotal04", ReportUtil.getCompositionCohort("AND", xdrprexdr,pat04), null);
		treatmentDsd.addRow("xdrtotal0514", ReportUtil.getCompositionCohort("AND", xdrprexdr,pat0514), null);
		treatmentDsd.addRow("xdrtotal1517", ReportUtil.getCompositionCohort("AND", xdrprexdr,pat1517), null);
		treatmentDsd.addRow("xdrtotalhiv", ReportUtil.getCompositionCohort("AND", xdrprexdr,tbhiv), null);
		
		//COLUMNS
		treatmentDsd.addColumn("New", newPatients, null);
		treatmentDsd.addColumn("Relapse1", relapse1, null);
		treatmentDsd.addColumn("Relapse2", relapse2, null);
		treatmentDsd.addColumn("AfterDefault1",default1, null);
		treatmentDsd.addColumn("AfterDefault2",default2, null);
		treatmentDsd.addColumn("AfterFailureCategoryI", failure1, null);
		
		treatmentDsd.addColumn("AfterFailureCategoryII", failure2, null);
		treatmentDsd.addColumn("Other", other, null);
		treatmentDsd.addColumn("newTotal", startedTreatment, null);
		
		//check
		/*treatmentDsd.addRow("mdr", Cohorts.getConfirmedMdrOnlyInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		treatmentDsd.addRow("xdr", Cohorts.getConfirmedXdrInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		treatmentDsd.addRow("tbhiv", Cohorts.getHivPositiveDuring(startDate, endDate), null);
		treatmentDsd.addRow("children", Cohorts.getAgeAtEnrollmentInMdrtbProgram(startDate, endDate, 0, 14), null);*/
		
		
		//treatmentDsd.addRow("suspected", Cohorts.getSuspectedMdrInProgramAndStartedTreatmentFilter(startDate, endDate), null);
		
		//CohortCrossTabDataSetDefinition siteDsd = new CohortCrossTabDataSetDefinition();
		//treatmentDsd.addRow("pulmonary", Cohorts.getAllPulmonaryEver(),null);
		//treatmentDsd.addRow("extrapulmonary", Cohorts.getAllPulmonaryEver(),null);
		//report.addDataSetDefinition("site", siteDsd,null);
		/*
		CohortDefinition pulmonary = Cohorts.getAllPulmonaryEver();
		CohortDefinition extrapulmonary = Cohorts.getAllExtraPulmonaryEver();*/
		
		//Map<String, CohortDefinition> columns = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate);
		
		/*for (String key : columns.keySet()) {
			treatmentDsd.addColumn(key, columns.get(key), null);
		}*/
		
		/*CohortDefinition previousSecondLine = ReportUtil.getMdrtbPreviousDrugUseFilterSet(startDate, endDate).get("PreviousSecondLine");
		
		CohortDefinition curedMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.CURED[0]);
		CohortDefinition txCompletedMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.TREATMENT_COMPLETE[0]);
		CohortDefinition defaultMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.LOST_TO_FOLLOWUP[0]);
		CohortDefinition failureMDR = Cohorts.getPreviousMdrtbProgramOutcome(startDate, endDate, MdrtbConcepts.FAILED[0]);
		CohortDefinition relapseMDR = ReportUtil.getCompositionCohort("OR", curedMDR,txCompletedMDR);
		
		GregorianCalendar gc = new GregorianCalendar();
    	gc.set(1900,0, 1, 0, 0,1);
    	
    	GregorianCalendar gc2 = new GregorianCalendar();
    	gc2.setTime(startDate);
    	gc.add(gc.DATE, -1);*/
		
		/*CohortDefinition prevXDR = Cohorts.getXdrDetectionFilter(gc.getTime(), gc2.getTime());
		CohortDefinition newOnly = ReportUtil.minus(ReportUtil.getCompositionCohort("AND", groups.get("New"), pulmonary),previousSecondLine);
		CohortDefinition relapseOnly = ReportUtil.minus(ReportUtil.getCompositionCohort("AND", groups.get("Relapse"), pulmonary),previousSecondLine);
		CohortDefinition defaultOnly = ReportUtil.minus(ReportUtil.getCompositionCohort("AND", groups.get("AfterDefault"), pulmonary),previousSecondLine);
		CohortDefinition failureCatIOnly = ReportUtil.minus(ReportUtil.getCompositionCohort("AND", groups.get("AfterFailureCategoryI"), pulmonary),previousSecondLine);
		CohortDefinition failureCatIIOnly = ReportUtil.minus(ReportUtil.getCompositionCohort("AND", groups.get("AfterFailureCategoryII"),pulmonary),previousSecondLine);
		CohortDefinition unknownOnly = ReportUtil.minus(groups.get("Other"),previousSecondLine);
		CohortDefinition newTotal = ReportUtil.getCompositionCohort("OR", newOnly,relapseOnly,defaultOnly,failureCatIOnly,failureCatIIOnly,unknownOnly );
		CohortDefinition prevTotal =  ReportUtil.getCompositionCohort("OR",ReportUtil.getCompositionCohort("AND", previousSecondLine, relapseMDR),ReportUtil.getCompositionCohort("AND", previousSecondLine, defaultMDR),ReportUtil.getCompositionCohort("AND", previousSecondLine, failureMDR),ReportUtil.getCompositionCohort("AND", previousSecondLine, prevXDR));*/
		
		/*treatmentDsd.addColumn("New", newOnly, null);
		treatmentDsd.addColumn("Relapse", relapseOnly, null);
		treatmentDsd.addColumn("AfterDefault",defaultOnly, null);
		treatmentDsd.addColumn("AfterFailureCategoryI", failureCatIOnly, null);
		treatmentDsd.addColumn("AfterFailureCategoryII", failureCatIIOnly, null);
		treatmentDsd.addColumn("Other", unknownOnly, null);
		treatmentDsd.addColumn("newTotal", newTotal, null);*/
		
		/*treatmentDsd.addColumn("relapseMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, relapseMDR), null);
		treatmentDsd.addColumn("defaultMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, defaultMDR), null);
		treatmentDsd.addColumn("failureMDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, failureMDR), null);
		treatmentDsd.addColumn("prevXDR", ReportUtil.getCompositionCohort("AND", previousSecondLine, prevXDR),null);
		treatmentDsd.addColumn("prevTotal",prevTotal,null);
		treatmentDsd.addColumn("finalTotal", ReportUtil.getCompositionCohort("OR", prevTotal,newTotal),null);*/
		
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