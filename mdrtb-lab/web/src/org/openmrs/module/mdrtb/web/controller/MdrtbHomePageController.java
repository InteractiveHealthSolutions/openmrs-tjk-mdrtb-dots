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
package org.openmrs.module.mdrtb.web.controller;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.data.DSTReportTJK;
import org.openmrs.module.mdrtb.reporting.data.MOHReport;
import org.openmrs.module.mdrtb.reporting.data.MOHReportTJK;
import org.openmrs.module.mdrtb.reporting.data.OutcomeReport;
import org.openmrs.module.mdrtb.reporting.data.OutcomeReportTJK;
import org.openmrs.module.mdrtb.reporting.data.TB07TJK;
import org.openmrs.module.mdrtb.reporting.data.TB08TJK;
import org.openmrs.module.mdrtb.reporting.data.WHOForm05;
import org.openmrs.module.mdrtb.reporting.data.WHOForm05TJK;
import org.openmrs.module.mdrtb.reporting.data.WHOForm06;
import org.openmrs.module.mdrtb.reporting.data.WHOForm06TJK;
import org.openmrs.module.mdrtb.reporting.data.WHOForm07;
import org.openmrs.module.mdrtb.reporting.data.WHOForm07TJK;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller backs the MDR-TB module home page
 */
@Controller
public class MdrtbHomePageController {
	
	protected final static Log log = LogFactory.getLog(MdrtbHomePageController.class);
	
    @SuppressWarnings("unchecked")
	@RequestMapping("/module/mdrtb/mdrtbIndex")
    public void viewHomePage(ModelMap model) {

        if (Context.isAuthenticated()){
        	
            
        	Map<String, String> reports = new LinkedHashMap<String, String>();
        	
        	// Load any configured BIRT reports
        	if (ModuleFactory.getStartedModulesMap().containsKey("birt")) {
	        	String str = Context.getAdministrationService().getGlobalProperty("mdrtb.birt_report_list");
	        	if (StringUtils.isNotEmpty(str)) {
	        		String birtPrefix = "module/birt/generateReport.form?reportId=";
		            try { 
		            	Class birtServiceClass = OpenmrsClassLoader.getInstance().loadClass("org.openmrs.module.birt.BirtReportService");
		            	Object reportService = Context.getService(birtServiceClass);
		            	Method getReportsMethod = birtServiceClass.getDeclaredMethod("getReports");	            	
		            	Class birtReportClass = OpenmrsClassLoader.getInstance().loadClass("org.openmrs.module.birt.BirtReport");
		            	Method getNameMethod = birtReportClass.getDeclaredMethod("getName");
		            	Method getIdMethod = birtReportClass.getDeclaredMethod("getReportId");
	
		            	List allReports = (List)getReportsMethod.invoke(reportService);
		                for (StringTokenizer st = new StringTokenizer(str, "|"); st.hasMoreTokens(); ) {
		                    String s = st.nextToken().trim();
		                    for (Object br : allReports) {
		                    	Object id = getIdMethod.invoke(br);
		                    	Object name = getNameMethod.invoke(br);
		                    	if (name.equals(s) || id.equals(s)) {
		                    		reports.put(birtPrefix + id, name.toString());
		                    	}
		                    }
		                }
		            }
		            catch (Exception ex){
		                log.error("Unable to setup birt reports in reference data in MdrtbFormController.", ex);
		            }
	        	}
        	}
        	
        	// Load default reporting framework reports
        	ReportSpecification[] rpts = {new TB07TJK(), new TB08TJK(), new DSTReportTJK()};
        	for (ReportSpecification spec : rpts) {
            	reports.put("module/mdrtb/reporting/reports.form?type=" + spec.getClass().getName(), spec.getName());
            }
        	
        	model.addAttribute("reports", reports);
        	
        	// add the specimen and resistance profiles
        	//reports.put("module/mdrtb/specimen/specimenReportsOverview.form", Context.getMessageSourceService().getMessage("mdrtb.specimenReports"));
        	//reports.put("module/mdrtb/specimen/labResultsReports.form", Context.getMessageSourceService().getMessage("mdrtb.resistanceProfiles"));
        	
        	// Load patient lists
        	List<Location> patientLocations = Context.getService(MdrtbService.class).getLocationsWithAnyProgramEnrollments();
        	model.addAttribute("patientLocations", patientLocations);
        	
           /* Map<String, String> patientLists = new LinkedHashMap<String, String>();
            patientLists.put("mdrtb.activePatients", "module/mdrtb/mdrtbListPatients.form?displayMode=mdrtbShortSummary&enrollment=current");
            model.addAttribute("patientLists", patientLists);
            
            // Link to Cohort Builder, if reporting compatibility is installed
            boolean rcStarted = ModuleFactory.getStartedModulesMap().containsKey("reportingcompatibility");
            model.addAttribute("showCohortBuilder", rcStarted);*/
        }
    }
}
