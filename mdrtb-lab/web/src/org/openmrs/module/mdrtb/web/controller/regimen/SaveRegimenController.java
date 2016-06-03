package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenChange;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller handles the submission of edits to a Regimen
 */
@Controller
public class SaveRegimenController {
	
	protected static final Log log = LogFactory.getLog(SaveRegimenController.class);
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
	}
    
    @RequestMapping("/module/mdrtb/regimen/saveRegimen")
    public String saveRegimen(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=true, value="type") String type,
    		@RequestParam(required=true, value="startingChangeDate") Date startingChangeDate,
    		@RequestParam(required=true, value="changeDate") Date changeDate,
    		@RequestParam(required=false, value="reasonForStarting") String reasonForStarting,
    		HttpServletRequest request, ModelMap model) throws Exception {
    	
    	User user = Context.getAuthenticatedUser();
    	OrderType drugOrderType = Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG);
    	Patient patient = Context.getPatientService().getPatient(patientId);
    	RegimenHistory history = RegimenUtils.getRegimenHistory(patient).get(type);
    	
    	RegimenChange change = history.getRegimenChanges().get(startingChangeDate);
    	Regimen regimenAtStart = history.getRegimenOnDate(startingChangeDate, false);
    	
    	// Get parameters organized
    	
    	Set<String> ordersToContinue = new HashSet<String>();
    	Set<String> ordersToStop = new HashSet<String>();

   		for (Object param : request.getParameterMap().keySet()) {
   			String[] split = param.toString().split(":", 2);
   			if (split.length == 2) {
   				String p = split[0];
   				String orderId = split[1];
   				String value = request.getParameter(param.toString());
   				if ("action".equals(p)) {
   					if ("stop".equals(value)) {
   						ordersToStop.add(orderId);
   					}
   					if ("continue".equals(value)) {
   						ordersToContinue.add(orderId);
   					}
   				}
   			}
   		}
   		
    	// Modify which orders which were ended on this date, if needed.  //TODO: Should we void and re-create?
   		Set<DrugOrder> existingOrdersEnded = (change == null ? new HashSet<DrugOrder>() : change.getOrdersEnded());

		for (DrugOrder order : regimenAtStart.getDrugOrders()) {
			String oId = order.getOrderId().toString();
			
			if (ordersToContinue.contains(oId) && existingOrdersEnded.contains(order)) { // Previously stopped, now continued
				order.setDiscontinued(false);
				order.setDiscontinuedDate(null);
				order.setDiscontinuedBy(null);
				order.setDiscontinuedReason(null);
				log.info("Order " + order.getOrderId() + " previously discontinued on " + changeDate + ".  Changed to continued.");
			}
			
			if (ordersToStop.contains(oId)) {
				
				String changeReason = request.getParameter("reason."+order.getOrderId());
    			if (ObjectUtil.isNull(changeReason)) {
    				// TODO: Validation error here.
    			}
				
				if (existingOrdersEnded.contains(order)) { // Still stopped, check if date or reason have changed
        			if (!order.getDiscontinuedDate().equals(changeDate)) {
        				order.setDiscontinuedDate(changeDate);
        				log.info("Order " + order.getOrderId() + " stop date changed from " + startingChangeDate + " to " + changeDate);
        			}
        			if (order.getDiscontinuedReason() == null || !order.getDiscontinuedReason().getConceptId().toString().equals(changeReason)) {
        				Concept c = Context.getConceptService().getConcept(changeReason);
        				order.setDiscontinuedReason(c);
        				log.info("Order " + order.getOrderId() + " stop reason changed to " + changeReason);
        			}
				}
				else { // New stoppage on this Date
					order.setDiscontinued(true);
					order.setDiscontinuedDate(changeDate);
					order.setDiscontinuedBy(user);
    				Concept c = Context.getConceptService().getConcept(changeReason);
    				order.setDiscontinuedReason(c);
    				log.info("Order " + order.getOrderId() + " newly discontinued on " + changeDate + " due to " + changeReason);
				}
			}
		}
		Context.getPatientService().savePatient(patient);
		
    	// Modify which orders which were started on this date, if needed.  //TODO: Should we void and re-create?
   		Set<DrugOrder> existingOrdersStarted = (change == null ? new HashSet<DrugOrder>() : change.getOrdersStarted());
   		Set<String> orderIdsToPreserve = new HashSet<String>();
   		
   		String[] newOrderKeys = request.getParameterValues("newOrderKey");
   		if (newOrderKeys != null) {
	   		for (String key : newOrderKeys) {
	   			String orderId = request.getParameter("orderId:"+key);
	   			String generic = request.getParameter("generic:"+key);
	   			String drug = request.getParameter("drug:"+key);
	   			String dose = request.getParameter("dose:"+key);
	   			String unit = request.getParameter("unit:"+key);
	   			String frequency = request.getParameter("frequency:"+key);
	   			if (ObjectUtil.isNull(frequency)) {
	   				frequency = "";
	   				String perDay = request.getParameter("perDay:"+key);
	   				String perWeek = request.getParameter("perWeek:"+key);
	   				String separator = "";
	   				if (ObjectUtil.notNull(perDay)) {
	   					/*frequency += perDay + "/day";
	   					separator = " x ";*/
	   					frequency += perDay + Context.getMessageSourceService().getMessage("mdrtb.perday");
	   					separator = " ";
	   				}
	   				if (ObjectUtil.notNull(perWeek)) {
	   					//frequency += separator + perWeek + " days/week";
	   					frequency += separator + perWeek + " " + Context.getMessageSourceService().getMessage("mdrtb.daysperweek");
	   				}
	   			}
	   			String autoExpireDate = request.getParameter("autoExpireDate:"+key);
	   			String instructions = request.getParameter("instructions:"+key);
	   			
	   			DrugOrder drugOrder = null;
	   			if (ObjectUtil.isNull(orderId)) {
	   				drugOrder = new DrugOrder();
	   				drugOrder.setPatient(patient);
	   				drugOrder.setOrderType(drugOrderType);
	   			}
	   			else {
	   				for (DrugOrder o : existingOrdersStarted) {
	   					if (o.getOrderId().toString().equals(orderId)) {
	   						drugOrder = o;
	   						orderIdsToPreserve.add(orderId);
	   					}
	   				}
	   			}
	   			drugOrder.setConcept(ObjectUtil.isNull(generic) ? null : Context.getConceptService().getConcept(generic));
	   			drugOrder.setDrug(ObjectUtil.isNull(drug) ? null : Context.getConceptService().getDrug(drug));
	   			drugOrder.setDose(ObjectUtil.isNull(dose) ? null : Double.parseDouble(dose));
	   			drugOrder.setUnits(unit);
	   			drugOrder.setFrequency(frequency);
	   			drugOrder.setStartDate(changeDate);
	   			drugOrder.setAutoExpireDate(ObjectUtil.isNull(autoExpireDate) ? null : Context.getDateFormat().parse(autoExpireDate));
	   			drugOrder.setInstructions(instructions);
	   			
	   			Context.getOrderService().saveOrder(drugOrder);
	   		}
   		}
   		
		// Void any orders that were removed
		for (DrugOrder existingOrder : existingOrdersStarted) {
			if (!orderIdsToPreserve.contains(existingOrder.getOrderId().toString())) {
				Context.getOrderService().voidOrder(existingOrder, "Voided from MDR-TB Drug Order Tab");
			}
		}
		
		// Update reason for starting obs
		if (change != null && change.getReasonForStarting() != null) {
			Obs reasonForStartingObs = change.getReasonForStarting();
			if (!reasonForStartingObs.getValueCoded().getConceptId().toString().equals(reasonForStarting) || changeDate.compareTo(startingChangeDate) != 0) {
				if (ObjectUtil.isNull(reasonForStarting)) {
					Context.getObsService().voidObs(reasonForStartingObs, "Reason for starting treatment voided on MDR-TB Drug Order Tab");
				}
				else {
					Concept answer = Context.getConceptService().getConcept(reasonForStarting);
					reasonForStartingObs.setValueCoded(answer);
					reasonForStartingObs.setObsDatetime(changeDate);
					Context.getObsService().saveObs(reasonForStartingObs, "Reason for starting treatment changed on MDR-TB Drug Order Tab");
				}
			}
		}
		else {
			if (ObjectUtil.notNull(reasonForStarting)) {
				Obs o = new Obs();
				o.setPerson(patient);
				o.setObsDatetime(changeDate);
				o.setConcept(Context.getService(MdrtbService.class).findMatchingConcept(history.getType().getReasonForStartingQuestion()));
				o.setValueCoded(Context.getConceptService().getConcept(reasonForStarting));
				o.setLocation(Context.getLocationService().getDefaultLocation());
				Context.getObsService().saveObs(o, "Reason for starting treatment saved on MDR-TB Drug Order Tab");
			}
		}
    	
    	return "redirect:/module/mdrtb/regimen/manageDrugOrders.form?patientId="+patientId + "&patientProgramId="+patientProgramId;
    }    
}
