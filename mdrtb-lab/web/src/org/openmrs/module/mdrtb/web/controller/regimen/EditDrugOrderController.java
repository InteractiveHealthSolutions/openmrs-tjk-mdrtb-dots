package org.openmrs.module.mdrtb.web.controller.regimen;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller manages the Drug Order Edit page
 */
@Controller
public class EditDrugOrderController {

    @RequestMapping("/module/mdrtb/regimen/editDrugOrder")
    public void editDrugOrder(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=true, value="type") String type,
    		@RequestParam(required=false, value="orderId") Integer orderId,
    		ModelMap model) {
    	
    	model.addAttribute("patientId", patientId);
    	model.addAttribute("patientProgramId", patientProgramId);
    	model.addAttribute("type", type);
    	model.addAttribute("orderId", orderId);
    	model.addAttribute("patient", Context.getPatientService().getPatient(patientId));
    	Order drugOrder = (ObjectUtil.isNull(orderId) ? new DrugOrder() : Context.getOrderService().getOrder(orderId));
    	model.addAttribute("drugOrder", drugOrder);
    	model.addAttribute("regimenType", RegimenUtils.getRegimenType(type));
    }
}
