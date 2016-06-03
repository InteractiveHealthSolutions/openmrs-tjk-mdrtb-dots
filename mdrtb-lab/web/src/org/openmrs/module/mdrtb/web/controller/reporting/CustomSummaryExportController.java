package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Cohort;
import org.openmrs.module.mdrtb.reporting.PatientSummaryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Generates a custom summary for the passed patients and columns
 */
@Controller
public class CustomSummaryExportController {
	
    @RequestMapping("/module/mdrtb/reporting/customSummaryExport")
    public void showPatientList(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
            		@RequestParam("patientIds") String patientIds,
            		@RequestParam(value="columns") String[] columns) throws Exception {

    	Cohort c = new Cohort(patientIds);
    	List<String> columnList = (c == null ? null : Arrays.asList(columns));
    	PatientSummaryUtil.outputToExcel(response, c, columnList);
	}
}
