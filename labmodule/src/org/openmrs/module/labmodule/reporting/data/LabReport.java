package org.openmrs.module.labmodule.reporting.data;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.exception.MdrtbAPIException;
import org.openmrs.module.labmodule.reporting.ReportSpecification;
import org.openmrs.module.labmodule.reporting.ReportUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LAB Report
 */
public class LabReport implements ReportSpecification {
	
	/**
	 * @see ReportSpecification#getName()
	 */
	public String getName() {
		return Context.getMessageSourceService().getMessage("labmodule.labReports");
	}
	
	/**
	 * @see ReportSpecification#getDescription()
	 */
	public String getDescription() {
		return Context.getMessageSourceService().getMessage("labmodule.report.description");
	}
	
	@ModelAttribute("labResults")
	public Collection<Oblast> getLabResults() {
		
		Location location = null;
		return Context.getService(TbService.class).getOblasts();
		
	}
	
	/**
	 * @see ReportSpecification#getParameters()
	 */
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("location", Context.getMessageSourceService().getMessage("dotsreports.facility"), Location.class));
		l.add(new Parameter("year", Context.getMessageSourceService().getMessage("dotsreports.year"), Integer.class));
		return l;
	}
	
	/**
	 * @see ReportSpecification#getRenderingModes()
	 */
	public List<RenderingMode> getRenderingModes() {
		List<RenderingMode> l = new ArrayList<RenderingMode>();
		l.add(ReportUtil.renderingModeFromResource("HTML", "org/openmrs/module/labmodule/reporting/data/output/DOTS-TB07-OMAR" + 
			(StringUtils.isNotBlank(Context.getLocale().getLanguage()) ? "_" + Context.getLocale().getLanguage() : "") + ".html"));
		return l;
	}
	
	

	/**
	 * @see ReportSpecification#validateAndCreateContext(Map)
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters) {
		
		EvaluationContext context = ReportUtil.constructContext(parameters);
		Integer year = (Integer)parameters.get("year");
		
		if (year == null) {
			throw new IllegalArgumentException(Context.getMessageSourceService().getMessage("dotsreports.error.pleasEnterAYear"));
		}
		context.getParameterValues().putAll(ReportUtil.getPeriodDates(year, null, null));
		
		Map<String,Object>pMap = context.getParameterValues();
		Set<String> keySet = pMap.keySet();
		
		return context;
	}
	
	/**
	 * ReportSpecification#evaluateReport(EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public ReportData evaluateReport(EvaluationContext context) {
		
		Integer year = (Integer) context.getParameterValue("year");
		Location location = (Location) context.getParameterValue("location");
		String oblast = (String) context.getParameterValue("oblast");
		Date endDate = (Date)context.getParameterValue("endDate");
		Date startDate = (Date)context.getParameterValue("startDate");
		
		Oblast o = null;
		if(!oblast.equals("") && location == null)
			o =  Context.getService(TbService.class).getOblast(Integer.parseInt(oblast));
		
		List<Location> locList = new ArrayList<Location>();
		if(o != null && location == null)
			locList = Context.getService(TbService.class).getLocationsFromOblastName(o);
		else if (location != null)
			locList.add(location);
		
		if(location != null)
			context.addParameterValue("location", location.getName()); 
		else if(o != null)
			context.addParameterValue("location", o.getName()); 
		else
			context.addParameterValue("location", " - "); 
		
		ReportDefinition report = new ReportDefinition(); 
		
		String totalTest = Context.getService(TbService.class).getAllLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalTest", totalTest); 
		String totalMicroscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalMicroscopy", totalMicroscopy); 
		String totalDiagnosticTest = Context.getService(TbService.class).getDiagnosticLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalDiagnosticTest", totalDiagnosticTest); 
		String totalDiagnosticMicroscopyTest = Context.getService(TbService.class).getDisgnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalDiagnosticMicroscopyTest", totalDiagnosticMicroscopyTest); 
		String totalFollowupTest = Context.getService(TbService.class).getFollowupLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalFollowupTest", totalFollowupTest); 
		String totalFollowupMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("totalFollowupMicroscopyTest", totalFollowupMicroscopyTest); 
		String allPositiveTest = Context.getService(TbService.class).getAllPositiveResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveTest", allPositiveTest); 
		if(allPositiveTest.equals("0") || totalTest.equals("0"))
			context.addParameterValue("allPercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(allPositiveTest)/Float.parseFloat(totalTest))*100;
			context.addParameterValue("allPercentageTest", String.format("%.2f", per) + "%"); 
		}
		String allPositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveDiagnosticTest", allPositiveDiagnosticTest); 
		if(allPositiveDiagnosticTest.equals("0") || totalDiagnosticTest.equals("0"))
			context.addParameterValue("allDiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(allPositiveDiagnosticTest)/Float.parseFloat(totalDiagnosticTest))*100;
			context.addParameterValue("allDiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String allPositiveFollowupTest = Context.getService(TbService.class).getPositiveFollowupResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveFollowupTest", allPositiveFollowupTest);
		if(allPositiveFollowupTest.equals("0") || totalFollowupTest.equals("0"))
			context.addParameterValue("allFollowupPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(allPositiveFollowupTest)/Float.parseFloat(totalFollowupTest))*100;
			context.addParameterValue("allFollowupPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String allPositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveMicroscopyTest", allPositiveMicroscopyTest); 
		if(allPositiveMicroscopyTest.equals("0") || totalMicroscopy.equals("0"))
			context.addParameterValue("allPositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(allPositiveMicroscopyTest)/Float.parseFloat(totalMicroscopy))*100;
			context.addParameterValue("allPositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String allPositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveMicroscopyDiagnosticTest", allPositiveMicroscopyDiagnosticTest);
		if(allPositiveMicroscopyDiagnosticTest.equals("0") || totalDiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("allPositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(allPositiveMicroscopyDiagnosticTest)/Float.parseFloat(totalDiagnosticMicroscopyTest))*100;
			context.addParameterValue("allPositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String allPositiveMicroscopyFollowupTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositiveMicroscopyFollowupTest", allPositiveMicroscopyFollowupTest);
		if(allPositiveMicroscopyFollowupTest.equals("0") || totalFollowupMicroscopyTest.equals("0"))
			context.addParameterValue("allPositiveMicroscopyPercentageFollowupTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(allPositiveMicroscopyFollowupTest)/Float.parseFloat(totalFollowupMicroscopyTest))*100;
			context.addParameterValue("allPositiveMicroscopyPercentageFollowupTest", String.format("%.2f", per5) + "%"); 
		}
		String allPHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPHCTest", allPHCTest);
		String allPositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("allPositivePHCTest", allPositivePHCTest);
		String allDiagnosticRatio = Context.getService(TbService.class).getRateDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allDiagnosticRatio", allDiagnosticRatio);
		String allFollowupRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allFollowupRatio", allFollowupRatio);
		String allDiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("allDiagnosticSaliva", allDiagnosticSaliva);
		if(allDiagnosticSaliva.equals("0") || totalDiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("allMicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(allDiagnosticSaliva)/Float.parseFloat(totalDiagnosticMicroscopyTest))*100;
			context.addParameterValue("allMicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String totalWeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate, endDate, locList);
		context.addParameterValue("totalWeeklyLoad", totalWeeklyLoad);
		
		Map<String,Date>dateMap = ReportUtil.getPeriodDates(year, "1", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q1Test = Context.getService(TbService.class).getAllLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1Test", q1Test); 
		String q1Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1Microscopy", q1Microscopy); 
		String q1DiagnosticTest = Context.getService(TbService.class).getDiagnosticLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1DiagnosticTest", q1DiagnosticTest); 
		String q1DiagnosticMicroscopyTest = Context.getService(TbService.class).getDisgnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1DiagnosticMicroscopyTest", q1DiagnosticMicroscopyTest); 
		String q1FollowupTest = Context.getService(TbService.class).getFollowupLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1FollowupTest", q1FollowupTest); 
		String q1FollowupMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1FollowupMicroscopyTest", q1FollowupMicroscopyTest); 
		String q1PositiveTest = Context.getService(TbService.class).getAllPositiveResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveTest", q1PositiveTest); 
		if(q1PositiveTest.equals("0") || q1Test.equals("0"))
			context.addParameterValue("q1PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q1PositiveTest)/Float.parseFloat(q1Test))*100;
			context.addParameterValue("q1PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q1PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveDiagnosticTest", q1PositiveDiagnosticTest); 
		if(q1PositiveDiagnosticTest.equals("0") || q1DiagnosticTest.equals("0"))
			context.addParameterValue("q1DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q1PositiveDiagnosticTest)/Float.parseFloat(q1DiagnosticTest))*100;
			context.addParameterValue("q1DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q1PositiveFollowupTest = Context.getService(TbService.class).getPositiveFollowupResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveFollowupTest", q1PositiveFollowupTest);
		if(q1PositiveFollowupTest.equals("0") || q1FollowupTest.equals("0"))
			context.addParameterValue("q1FollowupPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q1PositiveFollowupTest)/Float.parseFloat(q1FollowupTest))*100;
			context.addParameterValue("q1FollowupPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q1PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveMicroscopyTest", q1PositiveMicroscopyTest); 
		if(q1PositiveMicroscopyTest.equals("0") || q1Microscopy.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q1PositiveMicroscopyTest)/Float.parseFloat(q1Microscopy))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q1PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveMicroscopyDiagnosticTest", q1PositiveMicroscopyDiagnosticTest);
		if(q1PositiveMicroscopyDiagnosticTest.equals("0") || q1DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q1PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q1DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q1PositiveMicroscopyFollowupTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositiveMicroscopyFollowupTest", q1PositiveMicroscopyFollowupTest);
		if(q1PositiveMicroscopyFollowupTest.equals("0") || q1FollowupMicroscopyTest.equals("0"))
			context.addParameterValue("q1PositiveMicroscopyPercentageFollowupTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q1PositiveMicroscopyFollowupTest)/Float.parseFloat(q1FollowupMicroscopyTest))*100;
			context.addParameterValue("q1PositiveMicroscopyPercentageFollowupTest", String.format("%.2f", per5) + "%"); 
		}
		String q1PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PHCTest", q1PHCTest);
		String q1PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1PositivePHCTest", q1PositivePHCTest);
		String q1DiagnosticRatio = Context.getService(TbService.class).getRateDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1DiagnosticRatio", q1DiagnosticRatio);
		String q1FollowupRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1FollowupRatio", q1FollowupRatio);
		String q1DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q1DiagnosticSaliva", q1DiagnosticSaliva);
		if(q1DiagnosticSaliva.equals("0") || q1DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q1MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q1DiagnosticSaliva)/Float.parseFloat(q1DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q1MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q1WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate, endDate, locList);
		context.addParameterValue("q1WeeklyLoad", q1WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "2", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q2Test = Context.getService(TbService.class).getAllLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2Test", q2Test); 
		String q2Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2Microscopy", q2Microscopy); 
		String q2DiagnosticTest = Context.getService(TbService.class).getDiagnosticLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2DiagnosticTest", q2DiagnosticTest); 
		String q2DiagnosticMicroscopyTest = Context.getService(TbService.class).getDisgnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2DiagnosticMicroscopyTest", q2DiagnosticMicroscopyTest); 
		String q2FollowupTest = Context.getService(TbService.class).getFollowupLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2FollowupTest", q2FollowupTest); 
		String q2FollowupMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2FollowupMicroscopyTest", q2FollowupMicroscopyTest); 
		String q2PositiveTest = Context.getService(TbService.class).getAllPositiveResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveTest", q2PositiveTest); 
		if(q2PositiveTest.equals("0") || q2Test.equals("0"))
			context.addParameterValue("q2PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q2PositiveTest)/Float.parseFloat(q2Test))*100;
			context.addParameterValue("q2PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q2PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveDiagnosticTest", q2PositiveDiagnosticTest); 
		if(q2PositiveDiagnosticTest.equals("0") || q2DiagnosticTest.equals("0"))
			context.addParameterValue("q2DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q2PositiveDiagnosticTest)/Float.parseFloat(q2DiagnosticTest))*100;
			context.addParameterValue("q2DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q2PositiveFollowupTest = Context.getService(TbService.class).getPositiveFollowupResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveFollowupTest", q2PositiveFollowupTest);
		if(q2PositiveFollowupTest.equals("0") || q2FollowupTest.equals("0"))
			context.addParameterValue("q2FollowupPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q2PositiveFollowupTest)/Float.parseFloat(q2FollowupTest))*100;
			context.addParameterValue("q2FollowupPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q2PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveMicroscopyTest", q2PositiveMicroscopyTest); 
		if(q2PositiveMicroscopyTest.equals("0") || q2Microscopy.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q2PositiveMicroscopyTest)/Float.parseFloat(q2Microscopy))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q2PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveMicroscopyDiagnosticTest", q2PositiveMicroscopyDiagnosticTest);
		if(q2PositiveMicroscopyDiagnosticTest.equals("0") || q2DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q2PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q2DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q2PositiveMicroscopyFollowupTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositiveMicroscopyFollowupTest", q2PositiveMicroscopyFollowupTest);
		if(q2PositiveMicroscopyFollowupTest.equals("0") || q2FollowupMicroscopyTest.equals("0"))
			context.addParameterValue("q2PositiveMicroscopyPercentageFollowupTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q2PositiveMicroscopyFollowupTest)/Float.parseFloat(q2FollowupMicroscopyTest))*100;
			context.addParameterValue("q2PositiveMicroscopyPercentageFollowupTest", String.format("%.2f", per5) + "%"); 
		}
		String q2PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PHCTest", q2PHCTest);
		String q2PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2PositivePHCTest", q2PositivePHCTest);
		String q2DiagnosticRatio = Context.getService(TbService.class).getRateDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2DiagnosticRatio", q2DiagnosticRatio);
		String q2FollowupRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2FollowupRatio", q2FollowupRatio);
		String q2DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q2DiagnosticSaliva", q2DiagnosticSaliva);
		if(q2DiagnosticSaliva.equals("0") || q2DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q2MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q2DiagnosticSaliva)/Float.parseFloat(q2DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q2MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q2WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate, endDate, locList);
		context.addParameterValue("q2WeeklyLoad", q2WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "3", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q3Test = Context.getService(TbService.class).getAllLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3Test", q3Test); 
		String q3Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3Microscopy", q3Microscopy); 
		String q3DiagnosticTest = Context.getService(TbService.class).getDiagnosticLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3DiagnosticTest", q3DiagnosticTest); 
		String q3DiagnosticMicroscopyTest = Context.getService(TbService.class).getDisgnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3DiagnosticMicroscopyTest", q3DiagnosticMicroscopyTest); 
		String q3FollowupTest = Context.getService(TbService.class).getFollowupLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3FollowupTest", q3FollowupTest); 
		String q3FollowupMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3FollowupMicroscopyTest", q3FollowupMicroscopyTest); 
		String q3PositiveTest = Context.getService(TbService.class).getAllPositiveResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveTest", q3PositiveTest); 
		if(q3PositiveTest.equals("0") || q3Test.equals("0"))
			context.addParameterValue("q3PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q3PositiveTest)/Float.parseFloat(q3Test))*100;
			context.addParameterValue("q3PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q3PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveDiagnosticTest", q3PositiveDiagnosticTest); 
		if(q3PositiveDiagnosticTest.equals("0") || q3DiagnosticTest.equals("0"))
			context.addParameterValue("q3DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q3PositiveDiagnosticTest)/Float.parseFloat(q3DiagnosticTest))*100;
			context.addParameterValue("q3DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q3PositiveFollowupTest = Context.getService(TbService.class).getPositiveFollowupResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveFollowupTest", q3PositiveFollowupTest);
		if(q3PositiveFollowupTest.equals("0") || q3FollowupTest.equals("0"))
			context.addParameterValue("q3FollowupPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q3PositiveFollowupTest)/Float.parseFloat(q3FollowupTest))*100;
			context.addParameterValue("q3FollowupPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q3PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveMicroscopyTest", q3PositiveMicroscopyTest); 
		if(q3PositiveMicroscopyTest.equals("0") || q3Microscopy.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q3PositiveMicroscopyTest)/Float.parseFloat(q3Microscopy))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q3PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveMicroscopyDiagnosticTest", q3PositiveMicroscopyDiagnosticTest);
		if(q3PositiveMicroscopyDiagnosticTest.equals("0") || q3DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q3PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q3DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q3PositiveMicroscopyFollowupTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositiveMicroscopyFollowupTest", q3PositiveMicroscopyFollowupTest);
		if(q3PositiveMicroscopyFollowupTest.equals("0") || q3FollowupMicroscopyTest.equals("0"))
			context.addParameterValue("q3PositiveMicroscopyPercentageFollowupTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q3PositiveMicroscopyFollowupTest)/Float.parseFloat(q3FollowupMicroscopyTest))*100;
			context.addParameterValue("q3PositiveMicroscopyPercentageFollowupTest", String.format("%.2f", per5) + "%"); 
		}
		String q3PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PHCTest", q3PHCTest);
		String q3PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3PositivePHCTest", q3PositivePHCTest);
		String q3DiagnosticRatio = Context.getService(TbService.class).getRateDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3DiagnosticRatio", q3DiagnosticRatio);
		String q3FollowupRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3FollowupRatio", q3FollowupRatio);
		String q3DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q3DiagnosticSaliva", q3DiagnosticSaliva);
		if(q3DiagnosticSaliva.equals("0") || q3DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q3MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q3DiagnosticSaliva)/Float.parseFloat(q3DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q3MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q3WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate, endDate, locList);
		context.addParameterValue("q3WeeklyLoad", q3WeeklyLoad);
		
		dateMap = ReportUtil.getPeriodDates(year, "4", null);
		startDate = dateMap.get("startDate");
		endDate = dateMap.get("endDate");
		
		String q4Test = Context.getService(TbService.class).getAllLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4Test", q4Test); 
		String q4Microscopy = Context.getService(TbService.class).getAllMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4Microscopy", q4Microscopy); 
		String q4DiagnosticTest = Context.getService(TbService.class).getDiagnosticLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4DiagnosticTest", q4DiagnosticTest); 
		String q4DiagnosticMicroscopyTest = Context.getService(TbService.class).getDisgnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4DiagnosticMicroscopyTest", q4DiagnosticMicroscopyTest); 
		String q4FollowupTest = Context.getService(TbService.class).getFollowupLabResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4FollowupTest", q4FollowupTest); 
		String q4FollowupMicroscopyTest = Context.getService(TbService.class).getFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4FollowupMicroscopyTest", q4FollowupMicroscopyTest); 
		String q4PositiveTest = Context.getService(TbService.class).getAllPositiveResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveTest", q4PositiveTest); 
		if(q4PositiveTest.equals("0") || q4Test.equals("0"))
			context.addParameterValue("q4PercentageTest", "-"); 
		else{
			Float per = (Float.parseFloat(q4PositiveTest)/Float.parseFloat(q4Test))*100;
			context.addParameterValue("q4PercentageTest", String.format("%.2f", per) + "%"); 
		}
		String q4PositiveDiagnosticTest = Context.getService(TbService.class).getPositiveDiagnosticResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveDiagnosticTest", q4PositiveDiagnosticTest); 
		if(q4PositiveDiagnosticTest.equals("0") || q4DiagnosticTest.equals("0"))
			context.addParameterValue("q4DiagnosticPercentageTest", "-"); 
		else{
			Float per1 = (Float.parseFloat(q4PositiveDiagnosticTest)/Float.parseFloat(q4DiagnosticTest))*100;
			context.addParameterValue("q4DiagnosticPercentageTest", String.format("%.2f", per1) + "%"); 
		}
		String q4PositiveFollowupTest = Context.getService(TbService.class).getPositiveFollowupResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveFollowupTest", q4PositiveFollowupTest);
		if(q4PositiveFollowupTest.equals("0") || q4FollowupTest.equals("0"))
			context.addParameterValue("q4FollowupPercentageTest", "-"); 
		else{
			Float per2 = (Float.parseFloat(q4PositiveFollowupTest)/Float.parseFloat(q4FollowupTest))*100;
			context.addParameterValue("q4FollowupPercentageTest", String.format("%.2f", per2) + "%"); 
		}
		String q4PositiveMicroscopyTest = Context.getService(TbService.class).getAllPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveMicroscopyTest", q4PositiveMicroscopyTest); 
		if(q4PositiveMicroscopyTest.equals("0") || q4Microscopy.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageTest",  "-"); 
		else{
			Float per3 = (Float.parseFloat(q4PositiveMicroscopyTest)/Float.parseFloat(q4Microscopy))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageTest", String.format("%.2f", per3) + "%"); 
		}
		String q4PositiveMicroscopyDiagnosticTest = Context.getService(TbService.class).getDiagnosticPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveMicroscopyDiagnosticTest", q4PositiveMicroscopyDiagnosticTest);
		if(q4PositiveMicroscopyDiagnosticTest.equals("0") || q4DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageDiagnosticTest",  "-"); 
		else{
			Float per4 = (Float.parseFloat(q4PositiveMicroscopyDiagnosticTest)/Float.parseFloat(q4DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageDiagnosticTest", String.format("%.2f", per4) + "%"); 
		}
		String q4PositiveMicroscopyFollowupTest = Context.getService(TbService.class).getFollowupPositiveMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositiveMicroscopyFollowupTest", q4PositiveMicroscopyFollowupTest);
		if(q4PositiveMicroscopyFollowupTest.equals("0") || q4FollowupMicroscopyTest.equals("0"))
			context.addParameterValue("q4PositiveMicroscopyPercentageFollowupTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q4PositiveMicroscopyFollowupTest)/Float.parseFloat(q4FollowupMicroscopyTest))*100;
			context.addParameterValue("q4PositiveMicroscopyPercentageFollowupTest", String.format("%.2f", per5) + "%"); 
		}
		String q4PHCTest = Context.getService(TbService.class).getAllPHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PHCTest", q4PHCTest);
		String q4PositivePHCTest = Context.getService(TbService.class).getPositivePHCResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4PositivePHCTest", q4PositivePHCTest);
		String q4DiagnosticRatio = Context.getService(TbService.class).getRateDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4DiagnosticRatio", q4DiagnosticRatio);
		String q4FollowupRatio = Context.getService(TbService.class).getRateFollowupMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4FollowupRatio", q4FollowupRatio);
		String q4DiagnosticSaliva = Context.getService(TbService.class).getSalivaDiagnosticMicroscopyResultDuring(startDate, endDate, locList);
		context.addParameterValue("q4DiagnosticSaliva", q4DiagnosticSaliva);
		if(q4DiagnosticSaliva.equals("0") || q4DiagnosticMicroscopyTest.equals("0"))
			context.addParameterValue("q4MicroscopySalivaRateDiagnostoicTest",   "-"); 
		else{
			Float per5 = (Float.parseFloat(q4DiagnosticSaliva)/Float.parseFloat(q4DiagnosticMicroscopyTest))*100;
			context.addParameterValue("q4MicroscopySalivaRateDiagnostoicTest", String.format("%.2f", per5) + "%"); 
		}
		String q4WeeklyLoad = Context.getService(TbService.class).getAverageWeeklyLoadPerLabTechnician(startDate, endDate, locList);
		context.addParameterValue("q4WeeklyLoad", q4WeeklyLoad);
		
		if (!locList.isEmpty()){
			List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();
			for(Location loc : locList)
				cohortDefinitions.add(Cohorts.getLocationFilter(loc, startDate, endDate));
				
			if(!cohortDefinitions.isEmpty()){
				report.setBaseCohortDefinition(ReportUtil.getCompositionCohort("OR", cohortDefinitions), null);
			}
		}
		
	 CohortDefinition allLabResults = Cohorts.getAllLabResultDuring(startDate,endDate);
		
		CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition(); 
		d.addParameter(ReportingConstants.END_DATE_PARAMETER); 
		d.addColumn("total", allLabResults, null); 
		report.addDataSetDefinition("all", d, null); 
		   
		ReportData results = null;
		  
		try {
			results = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return results;
	}

}