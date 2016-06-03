package org.openmrs.module.mdrtb.web.controller.regimen;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This Controller manages the Edit Regimen Page
 */
@Controller
public class EditRegimenController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
    	binder.registerCustomEditor(Concept.class, new ConceptEditor());
    	binder.registerCustomEditor(Drug.class, new DrugEditor());
	}
	
    @RequestMapping("/module/mdrtb/regimen/editRegimen")
    public void editRegimen(
    		@RequestParam(required=true, value="patientId") Integer patientId,
    		@RequestParam(required=true, value="patientProgramId") Integer patientProgramId,
    		@RequestParam(required=true, value="type") String type,
    		@RequestParam(required=true, value="changeDate") Date changeDate,
    		ModelMap model) {
    	
    	model.addAttribute("patientId", patientId);
    	model.addAttribute("patientProgramId", (patientProgramId != null ? patientProgramId : -1) );
    	model.addAttribute("type", type);
    	model.addAttribute("changeDate", changeDate);
    	
    	Patient p = Context.getPatientService().getPatient(patientId);
    	model.addAttribute("patient", p);
    }
}
