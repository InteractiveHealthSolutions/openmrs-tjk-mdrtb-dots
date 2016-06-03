package org.openmrs.module.mdrtb.web.controller.regimen;

import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller manages the Drug Orders tab
 */
@Controller
public class ManageDrugOrdersController {

    @RequestMapping("/module/mdrtb/regimen/manageDrugOrders")
    public void manageRegimens(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		ModelMap model) {
    	
    	model.addAttribute("patientId", patientId);
    	model.addAttribute("patientProgramId", patientProgramId);
    	
    	Patient p = Context.getPatientService().getPatient(patientId);
    	model.addAttribute("patient", p);

    	Map<String, RegimenHistory> m = RegimenUtils.getRegimenHistory(p);
    	model.addAttribute("regimenHistoryGroups", m);
    }
}
