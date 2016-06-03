package org.openmrs.module.mdrtb.status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;


public class VisitStatusCalculator implements StatusCalculator {

	private VisitStatusRenderer renderer;
	
	public VisitStatusCalculator(VisitStatusRenderer renderer) {
		this.setRenderer(renderer);
	}
	
	// TODO: flags to add:
	// if there is more than one intake encounter?
	// if there is no scheduled follow up?
	
	
    public Status calculate(MdrtbPatientProgram mdrtbProgram, Patient patient) {
    	  	
    	// create the new status
    	VisitStatus status = null;
    	
    	if (mdrtbProgram != null) {
    		status = new VisitStatus(mdrtbProgram);
    	}
    	// hack to handle the situation if we don't have a patient program--create a "dummy" patient program with 
    	// just the patient information to pass on to the renderer
    	else {
    		MdrtbPatientProgram dummyProgram = new MdrtbPatientProgram();
    		dummyProgram.setPatient(patient);
    		status = new VisitStatus(dummyProgram);
    	}
    		
    	EncounterType intakeType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type"));
    	EncounterType followUpType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type"));
    	EncounterType specimenType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
    	
    	// where we will store the various visits
    	List<StatusItem> intakeVisits = new LinkedList<StatusItem>();
    	List<StatusItem> followUpVisits = new LinkedList<StatusItem>();
    	List<StatusItem> scheduledFollowUpVisits = new LinkedList<StatusItem>();
    	List<StatusItem> specimenCollectionVisits = new LinkedList<StatusItem>();
    	
    	List<Encounter> encounters = null;
    	
    	// get all the encounters during the program, or, if no program specified, get all MDR-TB encouters
    	if (mdrtbProgram != null) {
    		encounters = mdrtbProgram.getMdrtbEncountersDuringProgram();
    	}
    	else {
    		encounters = Context.getService(MdrtbService.class).getMdrtbEncounters(patient);
    	}
    	
    	if (encounters != null) {
    		for (Encounter encounter : encounters) {
    			// create a new status item for this encounter
    			StatusItem visit = new StatusItem();
    			visit.setValue(encounter);
    			visit.setDate(encounter.getEncounterDatetime());
    			renderer.renderVisit(visit, status);
    	
    			// now place the visit in the appropriate "bucket"
    			if (encounter.getEncounterType().equals(intakeType)) {
    				intakeVisits.add(visit);
    			}
    			else if (encounter.getEncounterType().equals(specimenType)) {
    				specimenCollectionVisits.add(visit);
    			}
    			else if (encounter.getEncounterType().equals(followUpType)) {
    				if (encounter.getEncounterDatetime().after(new Date())) {
    					scheduledFollowUpVisits.add(visit);
    				}
    				else {
    					followUpVisits.add(visit);
    				}
    			}
    		}
    	}
    	
    	// add all the lists to the main status 
    	status.addItem("intakeVisits", new StatusItem(intakeVisits));
    	status.addItem("specimenCollectionVisits", new StatusItem(specimenCollectionVisits));
    	status.addItem("scheduledFollowUpVisits", new StatusItem(scheduledFollowUpVisits));
    	status.addItem("followUpVisits", new StatusItem(followUpVisits));
    	
    	// now handle adding the links that we should use for the new intake and follow-up visits
    	// (the logic to determine these links is basically delegated to the renderer
    	StatusItem newIntakeVisit = new StatusItem();
    	renderer.renderNewIntakeVisit(newIntakeVisit, status);
    	status.addItem("newIntakeVisit", newIntakeVisit);
    	
     	StatusItem newFollowUpVisit = new StatusItem();
    	renderer.renderNewFollowUpVisit(newFollowUpVisit, status);
    	status.addItem("newFollowUpVisit", newFollowUpVisit);
    	
    	return status;
    }

    public Status calculate(MdrtbPatientProgram mdrtbProgram) {
    	return calculate(mdrtbProgram, mdrtbProgram.getPatient());
    }
    
    public Status calculate(Patient patient) {
    	return calculate(null, patient);
    }
    

	public void setRenderer(VisitStatusRenderer renderer) {
	    this.renderer = renderer;
    }


	public VisitStatusRenderer getRenderer() {
	    return renderer;
    }

}
