package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.status.StatusItem;
import org.openmrs.module.mdrtb.status.VisitStatus;
import org.openmrs.module.mdrtb.status.VisitStatusRenderer;


public class DashboardVisitStatusRenderer implements VisitStatusRenderer {
	
    public void renderVisit(StatusItem visit, VisitStatus status) {
    	
    	DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
    	
    	Encounter encounter = (Encounter) visit.getValue();
    	
    	String[] params = { df.format(encounter.getEncounterDatetime()), encounter.getLocation().getDisplayString()};
    	
    	visit.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.visitStatus.visit", params,
		    "{0} at {1}", Context.getLocale()));
    	
    	// now determine where to link to
    	// if there is a form linked to this encounter, assume it is an HTML Form Entry form
    	// (note, however, that we exclude specimen collection encounters--they can't have forms linked to them)
    	if(encounter.getForm() != null 
    			&& !encounter.getEncounterType().equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
    		visit.setLink("/module/htmlformentry/htmlFormEntry.form?personId=" + encounter.getPatientId() 
    			+ "&formId=" + encounter.getForm().getId() + "&encounterId=" + encounter.getId() + 
    			"&mode=VIEW");
    	}
    	// otherwise, create the link based on the encounter type of the visit
    	else {
    	
    		EncounterType type = encounter.getEncounterType();
    	
    		if (type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")))) {
    			visit.setLink("/module/mdrtb/form/intake.form?patientId="
        			+ status.getPatientProgram().getPatient().getPatientId()
        			+ "&patientProgramId=" + status.getPatientProgram().getId() 
        			+ "&encounterId=" + encounter.getId());
    		}
    		else if (type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")))) {
    			visit.setLink("/module/mdrtb/form/followup.form?patientId="
        			+ status.getPatientProgram().getPatient().getPatientId()
        			+ "&patientProgramId=" + status.getPatientProgram().getId() 
        			+ "&encounterId=" + encounter.getId());
    		}
    		else if(type.equals(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")))) {
    			visit.setLink("/module/mdrtb/specimen/specimen.form?specimenId=" + encounter.getId()
    							+ "&patientProgramId=" + status.getPatientProgram().getId());
    		}
    		else {
    			throw new MdrtbAPIException("Invalid encounter type passed to Dashboard visit status renderer.");
    		}
    	}
    }

    public void renderNewIntakeVisit(StatusItem newIntakeVisit, VisitStatus status) {
    
    	// we've changed this so that we link to the select form page instead of determining what form to use here
     	newIntakeVisit.setLink("/module/mdrtb/form/select.form?formType=intake&patientId=" 
     		+ status.getPatientProgram().getPatient().getPatientId() 
     		+ "&patientProgramId=" + status.getPatientProgram().getId());
    	
    	/**
    	EncounterType [] intake = {Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"))};
    
    	// see if there are any forms associated with the intake encounter type
    	List<Form> intakeForm = Context.getFormService().getForms(null, true, Arrays.asList(intake), false, null, null, null);
    	
    	// if there is no custom intake form, link to the default one
    	if (intakeForm == null || intakeForm.isEmpty()) {
    		newIntakeVisit.setLink("/module/mdrtb/form/intake.form?patientId="
    			+ status.getPatientProgram().getPatient().getPatientId()
    			+ "&patientProgramId=" + status.getPatientProgram().getId() 
    			+ "&encounterId=-1");
    	}
    	// if there is a custom HTML form, use it
    	else if (intakeForm.size() == 1) {
    		// if there is exactly one form, assume it is an html form and create a link to it
    		newIntakeVisit.setLink("/module/htmlformentry/htmlFormEntry.form?personId=" 
    			+ status.getPatientProgram().getPatient().getPatientId() 
    			+ "&formId=" + intakeForm.get(0).getFormId() + 
    			"&mode=NEW");
    	}
    	else {
    		throw new MdrtbAPIException("More than one form associated with MDR-TB intake encounter.");
    	}	
    	*/
    }

    public void renderNewFollowUpVisit(StatusItem newFollowUpVisit, VisitStatus status) {
    
    	// we've changed this so that we link to the select form page instead of determining what form to use here
    	newFollowUpVisit.setLink("/module/mdrtb/form/select.form?formType=followUp&patientId=" 
     		+ status.getPatientProgram().getPatient().getPatientId() 
     		+ "&patientProgramId=" + status.getPatientProgram().getId());
    	
    	/**
    	EncounterType [] followUp = {Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"))};
        
    	// see if there are any forms associated with the intake encounter type
    	List<Form> followUpForm = Context.getFormService().getForms(null, true, Arrays.asList(followUp), false, null, null, null);
    	
    	if (followUpForm == null || followUpForm.isEmpty()) {
    		newfollowUpVisit.setLink("/module/mdrtb/form/followup.form?patientId="
    			+ status.getPatientProgram().getPatient().getPatientId()
    			+ "&patientProgramId=" + status.getPatientProgram().getId() 
    			+ "&encounterId=-1");
    	}
    	else if (followUpForm.size() == 1) {
    		// if there is exactly one form, assume it is an html form and create a link to it
    		newfollowUpVisit.setLink("/module/htmlformentry/htmlFormEntry.form?personId=" 
    			+ status.getPatientProgram().getPatient().getPatientId() 
    			+ "&formId=" + followUpForm.get(0).getFormId() + "&mode=NEW");
    	}
    	else {
    		throw new MdrtbAPIException("More than one form associated with MDR-TB follow-up encounter.");
    	}
    	
    	*/
    } 
}
