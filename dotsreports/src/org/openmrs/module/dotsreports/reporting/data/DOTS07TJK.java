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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.reporting.ReportSpecification;
import org.openmrs.module.dotsreports.reporting.ReportUtil;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
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
 * WHO Form 05 Report
 */
public class DOTS07TJK implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("dotsreports.form07");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("dotsreports.form07.title");
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
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/dotsreports/reporting/data/output/DOTS-TB07" + 
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
					CohortDefinition locationFilter = Cohorts.getLocationFilterTJK(location.getCountyDistrict(), startDate, endDate);
					if (locationFilter != null) {
						//baseCohortDefs.put("location", new Mapped(locationFilter, null));
						report.setBaseCohortDefinition(locationFilter, null);
					}	
				}
				/*CohortDefinition baseCohort = ReportUtil.getCompositionCohort(baseCohortDefs, "AND");
				report.setBaseCohortDefinition(baseCohort, null);*/
		
				CohortDefinition allTB = Cohorts.getEnrolledInDOTSProgramDuring(startDate, endDate);
				CohortDefinition tbHIV = Cohorts.getHivPositiveDuring(startDate, endDate);
				CohortDefinition onlyTB = ReportUtil.minus(allTB,tbHIV);
				
				CohortDefinition men = Cohorts.getMalePatients();
				CohortDefinition women = Cohorts.getFemalePatients();
				
				CohortDefinition pulmonary = Cohorts.getAllPulmonaryDuring(startDate,endDate);
				CohortDefinition extraPulmonary = Cohorts.getAllExtraPulmonaryDuring(startDate,endDate);
				
				CohortDefinition positiveResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.POSITIVE);
				CohortDefinition negativeResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.NEGATIVE);
				CohortDefinition unknownResult = Cohorts.getDotsBacBaselineTJKResult(startDate, endDate, null, null, org.openmrs.module.dotsreports.reporting.definition.DotsBacBaselineResultTJKCohortDefinition.Result.UNKNOWN);
				
				CohortDefinition newPulSmearPos = ReportUtil.getCompositionCohort("AND", pulmonary,positiveResult);
				CohortDefinition newPulSmearNeg = ReportUtil.getCompositionCohort("AND", pulmonary,negativeResult);
				CohortDefinition newExtra = ReportUtil.getCompositionCohort("AND",extraPulmonary);
				
				CohortDefinition age014 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 0, 15);
				CohortDefinition age1524 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 15, 25);
				CohortDefinition age2534 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 25, 35);
				CohortDefinition age3544 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 35, 45);
				CohortDefinition age4554 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 45, 55);
				CohortDefinition age5564 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 55, 65);
				CohortDefinition age65 = Cohorts.getAgeAtEnrollmentInDotsProgram(startDate, endDate, 65, 999);
				
				Map<String, CohortDefinition> groups = ReportUtil.getDotsRegistrationGroupsFilterSet(startDate, endDate);
				
				CohortCrossTabDataSetDefinition table1 = new CohortCrossTabDataSetDefinition();
				table1.addRow("tb", onlyTB , null);
				table1.addRow("tbhiv", tbHIV , null);

				table1.addColumn("NewPulmonarySmearPositiveMale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men), null);
				table1.addColumn("NewPulmonarySmearPositiveFemale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women), null);
				table1.addColumn("NewPulmonarySmearPositiveTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos),null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table1.addColumn("NewPulmonarySmearNegativeMale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,men), null);
				table1.addColumn("NewPulmonarySmearNegativeFemale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,women), null);
				table1.addColumn("NewPulmonarySmearNegativeTotal", ReportUtil.getCompositionCohort("AND", newPulSmearNeg),null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table1.addColumn("NewExtraPulmonaryMale", ReportUtil.getCompositionCohort("AND", newExtra,men), null);
				table1.addColumn("NewExtraPulmonaryFemale", ReportUtil.getCompositionCohort("AND", newExtra,women), null);
				table1.addColumn("NewExtraPulmonaryTotal", ReportUtil.getCompositionCohort("AND", newExtra),null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table1.addColumn("MaleTotal", ReportUtil.getCompositionCohort("AND", men), null);
				table1.addColumn("FemaleTotal", ReportUtil.getCompositionCohort("AND",women), null);
				table1.addColumn("Total", onlyTB,null);//,ReportUtil.getCompositionCohort("OR", men,women), null);

				
				report.addDataSetDefinition("allNew", table1, null);
				
				////////////////////////////////////////////////////////////////////
				
				CohortCrossTabDataSetDefinition table2 = new CohortCrossTabDataSetDefinition();
				table2.addRow("tb", onlyTB , null);
				table2.addRow("tbhiv", tbHIV , null);

				table2.addColumn("NewPulmonarySmearPositiveMale014", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age014), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale014", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age014), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal014", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age014), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale1524", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age1524), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale1524", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age1524), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal1524", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age1524), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale2534", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age2534), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale2534", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age2534), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal2534", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age2534), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale3544", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age3544), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale3544", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age3544), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal3544", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age3544), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale4554", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age4554), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale4554", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age4554), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal4554", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age4554), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale5564", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age5564), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale5564", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age5564), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal5564", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age5564), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("NewPulmonarySmearPositiveMale65", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("New"),age65), null);
				table2.addColumn("NewPulmonarySmearPositiveFemale65", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("New"),age65), null);
				//table2.addColumn("NewPulmonarySmearPositiveTotal65", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("New"),age65), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table2.addColumn("MaleTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men), null);
				table2.addColumn("FemaleTotal", ReportUtil.getCompositionCohort("AND",newPulSmearPos,women), null);
				table2.addColumn("Total", ReportUtil.getCompositionCohort("AND",newPulSmearPos,ReportUtil.getCompositionCohort("OR", women,men)), null);
				report.addDataSetDefinition("newByAge", table2, null);
				///////////////////////////////////////////////////////////////////////////////////
				
				CohortCrossTabDataSetDefinition table3 = new CohortCrossTabDataSetDefinition();
				table3.addRow("tb", onlyTB , null);
				table3.addRow("tbhiv", tbHIV , null);

				table3.addColumn("RelapsePulmonarySmearPositiveMale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("Relapse")), null);
				table3.addColumn("RelapsePulmonarySmearPositiveFemale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("Relapse")), null);
				table3.addColumn("RelapsePulmonarySmearPositiveTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("Relapse")), null);
				table3.addColumn("FailurePulmonarySmearPositiveMale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("AfterFailure")), null);
				table3.addColumn("FailurePulmonarySmearPositiveFemale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("AfterFailure")), null);
				table3.addColumn("FailurePulmonarySmearPositiveTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("AfterFailure")), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table3.addColumn("DefaultPulmonarySmearPositiveMale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,groups.get("AfterDefault")), null);
				table3.addColumn("DefaultPulmonarySmearPositiveFemale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,groups.get("AfterDefault")), null);
				table3.addColumn("DefaultPulmonarySmearPositiveTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos,groups.get("AfterDefault")), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				table3.addColumn("OtherPulmonarySmearPositiveMale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,men,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);
				table3.addColumn("OtherPulmonarySmearPositiveFemale", ReportUtil.getCompositionCohort("AND", newPulSmearPos,women,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);
				table3.addColumn("OtherPulmonarySmearPositiveTotal", ReportUtil.getCompositionCohort("AND", newPulSmearPos,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				
				
				report.addDataSetDefinition("RelapseByGender", table3, null);
				//////////////////////////////////////////////////////////////////////////////
				
				CohortCrossTabDataSetDefinition table4 = new CohortCrossTabDataSetDefinition();
				table4.addRow("tb", onlyTB , null);
				table4.addRow("tbhiv", tbHIV , null);

				table4.addColumn("DefaultPulmonarySmearNegativeMale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,men,groups.get("AfterDefault")), null);
				table4.addColumn("DefaultPulmonarySmearNegativeFemale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,women,groups.get("AfterDefault")), null);
				table4.addColumn("DefaultPulmonarySmearNegativeTotal", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,groups.get("AfterDefault")), null);
				table4.addColumn("OtherPulmonarySmearNegativeMale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,men,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);
				table4.addColumn("OtherPulmonarySmearNegativeFemale", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,women,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);
				table4.addColumn("OtherPulmonarySmearNegativeTotal", ReportUtil.getCompositionCohort("AND", newPulSmearNeg,ReportUtil.getCompositionCohort("OR",groups.get("TransferredIn"),groups.get("Other"))), null);//,ReportUtil.getCompositionCohort("OR", men,women), null);
				
				report.addDataSetDefinition("DefaultNegByGender", table4, null);
				//////////////////////////////////////////////////////////////////////////////
				
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