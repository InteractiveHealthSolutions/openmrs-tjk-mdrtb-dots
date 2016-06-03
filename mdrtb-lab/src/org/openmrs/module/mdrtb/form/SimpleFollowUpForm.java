package org.openmrs.module.mdrtb.form;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;


public class SimpleFollowUpForm extends AbstractSimpleForm {

	public SimpleFollowUpForm() {
		super();
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")));		
		
	}
	
	public SimpleFollowUpForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.follow_up_encounter_type")));		
	}
	
	public SimpleFollowUpForm(Encounter encounter) {
		super(encounter);
	}
}
