package org.openmrs.module.mdrtb.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/mdrtb/chart/chart.form")
public class ChartController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
	public ModelAndView showPatientSummary(@RequestParam(required = true, value="patientId") Integer patientId, 
	                                       @RequestParam(required = true, value="patientProgramId") Integer patientProgramId, ModelMap map) {
		
		// redirect to the dashboard if no patient program has been specified
		if (patientProgramId == null || patientProgramId == -1) {
			return new ModelAndView("redirect:/module/mdrtb/dashboard/dashboard.form?patientId=" + patientId);
		}
		
		map.put("patientId",patientId);
		map.put("patientProgramId", patientProgramId);
		
		return new ModelAndView("/module/mdrtb/chart/chart", map);
	}
}
