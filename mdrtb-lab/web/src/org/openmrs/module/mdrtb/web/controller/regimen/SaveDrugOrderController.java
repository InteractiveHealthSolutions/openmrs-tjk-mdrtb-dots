package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller processes a submission of changes to a Drug Order
 */
@Controller
public class SaveDrugOrderController {
	
	protected static final Log log = LogFactory.getLog(SaveDrugOrderController.class);
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
    	binder.registerCustomEditor(Concept.class, new ConceptEditor());
    	binder.registerCustomEditor(Drug.class, new DrugEditor());
	}
    
    @RequestMapping("/module/mdrtb/regimen/saveDrugOrder")
    public String saveRegimen(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=false, value="type") String type,
    		@RequestParam(required=false, value="orderId") Integer orderId,
    		@RequestParam(required=true, value="generic") Concept generic,
    		@RequestParam(required=false, value="drug") Drug drug,
    		@RequestParam(required=false, value="dose") Double dose,
    		@RequestParam(required=false, value="units") String units,
    		@RequestParam(required=false, value="frequency") String frequency,
    		@RequestParam(required=false, value="perDay") String perDay,
    		@RequestParam(required=false, value="perWeek") String perWeek,
    		@RequestParam(required=false, value="instructions") String instructions,
    		@RequestParam(required=true, value="startDate") Date changeDate,
    		@RequestParam(required=false, value="autoExpireDate") Date autoExpireDate,
    		@RequestParam(required=false, value="discontinuedDate") Date discontinuedDate,
    		@RequestParam(required=false, value="discontinuedReason") Concept discontinuedReason,
    		HttpServletRequest request, ModelMap model) throws Exception {
    	
    	User user = Context.getAuthenticatedUser();
    	
    	// TODO: Validate here
    	
		DrugOrder drugOrder = null;
		if (ObjectUtil.isNull(orderId)) {
			drugOrder = new DrugOrder();
			drugOrder.setPatient(Context.getPatientService().getPatient(patientId));
			drugOrder.setOrderType(Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
		}
		else {
			drugOrder = Context.getOrderService().getOrder(orderId, DrugOrder.class);
		}
		drugOrder.setConcept(generic);
		drugOrder.setDrug(drug);
		drugOrder.setDose(dose);
		drugOrder.setUnits(units);
		if (ObjectUtil.isNull(frequency)) {
			frequency = "";
			String separator = "";
			if (ObjectUtil.notNull(perDay)) {
				//frequency += perDay + "/day";
				frequency += perDay + Context.getMessageSourceService().getMessage("mdrtb.perday");
				//separator = " x ";
				separator = " ";
			}
			if (ObjectUtil.notNull(perWeek)) {
				//frequency += separator + perWeek + " days/week";
				frequency += separator + perWeek + " " + Context.getMessageSourceService().getMessage("mdrtb.daysperweek");
			}
		}
		drugOrder.setFrequency(frequency);
		drugOrder.setInstructions(instructions);
		drugOrder.setStartDate(changeDate);
		drugOrder.setAutoExpireDate(autoExpireDate);
		
		if (drugOrder.getDiscontinued() == Boolean.TRUE) { // If originally was discontinued, but no longer, null out fields
			if (discontinuedDate == null) {
				drugOrder.setDiscontinued(false);
				drugOrder.setDiscontinuedDate(null);
				drugOrder.setDiscontinuedBy(null);
				drugOrder.setDiscontinuedReason(null);
			}
			else {
				drugOrder.setDiscontinuedDate(discontinuedDate);
				drugOrder.setDiscontinuedReason(discontinuedReason);
			}
		}
		else {
			if (discontinuedDate != null) {
				drugOrder.setDiscontinued(true);
				drugOrder.setDiscontinuedDate(discontinuedDate);
				drugOrder.setDiscontinuedBy(user);
				drugOrder.setDiscontinuedReason(discontinuedReason);
			}
		}

		Context.getOrderService().saveOrder(drugOrder);
    	return "redirect:/module/mdrtb/regimen/manageDrugOrders.form?patientId="+patientId + "&patientProgramId="+patientProgramId;
    }    
}
