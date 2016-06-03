package org.openmrs.module.mdrtb.form;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;


public class SimpleIntakeForm extends AbstractSimpleForm {

	public SimpleIntakeForm() {
		super();
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")));		
		
	}
	
	public SimpleIntakeForm(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.intake_encounter_type")));		
	}
	
	public SimpleIntakeForm(Encounter encounter) {
		super(encounter);
	}
	
	public Concept getAnatomicalSite() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}
	
	public void setAnatomicalSite(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(site != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		} 
	}
}
