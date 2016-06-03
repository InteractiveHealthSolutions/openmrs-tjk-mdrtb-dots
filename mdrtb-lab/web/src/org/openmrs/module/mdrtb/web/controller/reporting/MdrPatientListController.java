package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MdrPatientListController {
    
    @RequestMapping("/module/mdrtb/reporting/mdrPatientList")
    public void showPatientList(
            		@RequestParam("locationId") Integer locationId,
            		@RequestParam(value="enrollment", required=false) String enrollment,
            		@RequestParam(value="view", required=false) String view,
            		ModelMap model) {
        
    	Location l = Context.getLocationService().getLocation(locationId);
    	Cohort c = MdrtbUtil.getMdrPatients(null, null, enrollment, l, null);
    	view = (StringUtils.isEmpty(view) ? "patientSummary" : view);
		
    	model.addAttribute("view", view);
        model.addAttribute("patientIds", c.getCommaSeparatedPatientIds());
        model.addAttribute("location", l);
        model.addAttribute("runDate", new Date());
    }
}
