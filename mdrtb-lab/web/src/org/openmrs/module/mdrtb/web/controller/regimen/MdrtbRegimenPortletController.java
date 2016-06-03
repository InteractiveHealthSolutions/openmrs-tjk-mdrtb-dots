package org.openmrs.module.mdrtb.web.controller.regimen;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.PredictionModel;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This Controller renders a Portlet for managing a Regimen of a particular type
 */
@Controller
public class MdrtbRegimenPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping("/module/mdrtb/regimen/*.portlet")
	@SuppressWarnings("unchecked")
	public ModelAndView showPortlet(HttpServletRequest request, ModelMap map) throws Exception {
		
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());

		String portletPath = ObjectUtil.nvlStr(request.getAttribute("javax.servlet.include.servlet_path"), "");
		
		if (ObjectUtil.notNull(portletPath)) {

			// Allowable extensions are '' (no extension) and '.portlet'
			if (portletPath.endsWith("portlet")) {
				portletPath = portletPath.replace(".portlet", "");
			}
			else if (portletPath.endsWith("jsp")) {
				throw new ServletException("Illegal extension used for portlet: '.jsp'. Allowable extensions are '' (no extension) and '.portlet'");
			}
			log.debug("Loading portlet: " + portletPath);
			
			map.addAttribute("now", new Date());
			map.addAttribute("locale", Context.getLocale());
			map.addAttribute("portletUUID", UUID.randomUUID().toString().replace("-", ""));
			map.addAttribute("id", request.getAttribute("org.openmrs.portlet.id"));
			
			Integer patientId = (Integer)request.getAttribute("org.openmrs.portlet.patientId");
			String type = (String)request.getAttribute("org.openmrs.module.mdrtb.portlet.regimenType");
			RegimenHistory history = (RegimenHistory)request.getAttribute("org.openmrs.module.mdrtb.portlet.regimenHistory");
			Date changeDate = (Date)request.getAttribute("org.openmrs.module.mdrtb.portlet.changeDate");
			Map<String, String> parameters = (Map<String, String>)request.getAttribute("org.openmrs.portlet.parameters");
			
			Patient patient = null;
			Map<String, RegimenHistory> historyGroups = new LinkedHashMap<String, RegimenHistory>();
			if (history == null) {
				patient = Context.getPatientService().getPatient(patientId);
				historyGroups = RegimenUtils.getRegimenHistory(patient);
				
				if (ObjectUtil.notNull(type)) {
					history = historyGroups.get(type);
				}
			}
			else {
				type = history.getType().getName();
				historyGroups.put(type, history);
				patient = history.getPatient();
			}
			map.addAttribute("patient", patient);
			map.addAttribute("patientId", patient.getPatientId());
			map.addAttribute("history", history);
			map.addAttribute("type", type);
			map.addAttribute("regimenHistoryGroups", historyGroups);
			map.addAttribute("changeDate", changeDate);
			
			if (history != null && changeDate != null) {
				map.addAttribute("change", history.getRegimenChanges().get(changeDate));
				map.addAttribute("regimenAtStart", history.getRegimenOnDate(changeDate, false));
				map.addAttribute("regimenAtEnd", history.getRegimenOnDate(changeDate, true));
			}

			map.addAllAttributes(parameters);
			
			// Alerts
			Map<DrugOrder, String> drugAlerts = new HashMap<DrugOrder, String>();
			if ("true".equals(parameters.get("alerts"))) {
				
				Regimen activeTbRegimen = historyGroups.get("tb").getActiveRegimen();
				
				// Show warnings for any drug orders which are active and have resistances.  TODO: Limit by date?
				Concept resistantQuestion = getMdrtbService().getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG);
				List<Obs> dstResults = Context.getObsService().getObservationsByPersonAndConcept(patient, resistantQuestion);
				Set<Concept> resistances = new HashSet<Concept>();
				for (Obs o : dstResults) {
					resistances.add(o.getValueCoded());
				}
				for (DrugOrder order : activeTbRegimen.getDrugOrders()) {
					if (resistances.contains(order.getConcept())) {
						drugAlerts.put(order, MessageUtil.translate("mdrtb.drugContraIndicatedByDst"));
					}
				}
				
                // Resistance Probability: Experimental for Hamish
				if (dstResults.isEmpty()) { // Only show this warning if no DSTs have been done
	                if ("true".equals(Context.getAdministrationService().getGlobalProperty("mdrtb.enableResistanceProbabilityWarning"))) {
	                    Map<PredictionModel.RiskFactor, Boolean> riskFactors = PredictionModel.getRiskFactors(patient);
	                    map.addAttribute("resistanceRiskFactors", riskFactors);
	                    double probability = PredictionModel.calculateRiskProbability(riskFactors, 1);
	                    map.addAttribute("resistanceProbability", probability);
	                    
	                    Concept inh = getMdrtbService().getConcept(MdrtbConcepts.ISONIAZID);
	                    Concept rif = getMdrtbService().getConcept(MdrtbConcepts.RIFAMPICIN);
	                    for (DrugOrder order : activeTbRegimen.getDrugOrders()) {
	                    	if (order.getConcept().equals(inh) || order.getConcept().equals(rif)) {
	                    		drugAlerts.put(order, MessageUtil.translate("mdrtb.probabilityOfResistance") + ": " + probability + "%");
	                    	}
	                    }
	                }
				}
			}
			map.addAttribute("drugAlerts", drugAlerts);
		}
		
		return new ModelAndView(portletPath, map);
	}
	
	/**
	 * @return the MdrtbService
	 */
	private MdrtbService getMdrtbService() {
		return Context.getService(MdrtbService.class);
	}
}
