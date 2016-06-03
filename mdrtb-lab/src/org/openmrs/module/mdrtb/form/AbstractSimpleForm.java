package org.openmrs.module.mdrtb.form;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;


public abstract class AbstractSimpleForm implements SimpleForm {
	protected Encounter encounter;
	
	public AbstractSimpleForm() {
		this.encounter = new Encounter();
	}
	
	public AbstractSimpleForm(Patient patient) {
		this.encounter = new Encounter();
		this.encounter.setPatient(patient);
	}
	
	public AbstractSimpleForm(Encounter encounter) {
		this.encounter = encounter;
	}
	
	
	public Integer getId() {
		return this.encounter.getId();
	}
	
	public void setEncounter(Encounter encounter) {
	    this.encounter = encounter;
    }

	public Encounter getEncounter() {
	    return encounter;
    }
	
	public Person getProvider() {
		return encounter.getProvider();
	}
	
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public Patient getPatient() {
		return encounter.getPatient();
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setPerson(patient);
		}
	}
	
	public Date getEncounterDatetime() {
		return encounter.getEncounterDatetime();
	}

	public void setEncounterDatetime(Date date) {
		encounter.setEncounterDatetime(date);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setObsDatetime(date);
		}
	}
	
	public Location getLocation() {
		return encounter.getLocation();
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// propogate to all the obs on the encounter
		for (Obs obs : encounter.getAllObs()) {
			obs.setLocation(location);
		}
	}
	
	public String getWeight() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WEIGHT), this.encounter);
	}
	
	public void setWeight(String weight) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WEIGHT), this.encounter, weight);
	}
	
	public String getPulse() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULSE), this.encounter);
	}
	
	public void setPulse(String pulse) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULSE), this.encounter, pulse);
	}
	
	public String getTemperature() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEMPERATURE), this.encounter);
	}
	
	public void setTemperature(String temperature) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEMPERATURE), this.encounter, temperature);
	}
	
	public String getSystolicBloodPressure() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SYSTOLIC_BLOOD_PRESSURE), this.encounter);
	}
	
	public void setSystolicBloodPressure(String pressure) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SYSTOLIC_BLOOD_PRESSURE), this.encounter, pressure);
	}
	
	public String getRespiratoryRate() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESPIRATORY_RATE), this.encounter);
	}
	
	public void setRespiratoryRate(String rate) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESPIRATORY_RATE), this.encounter, rate);
	}
	
	public String getClinicianNotes() {
		return fetchObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), this.encounter);
	}
	
	public void setClinicianNotes(String comments) {
		updateObs(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CLINICIAN_NOTES), this.encounter, comments);
	}
	
	/**
	 * Utility method that fetches the obs off the encounter that is associated with the specified concept
	 */
	private String fetchObs(Concept concept, Encounter encounter) {
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		if (obs == null) {
			return null;
		}
		else {
			// get the value based on the datatype
			if (concept.getDatatype().equals(Context.getConceptService().getConceptDatatypeByName("NUMERIC"))) {
				return obs.getValueNumeric().toString();
			} 
			else {
			    return obs.getValueText();
			}
		}
	}
	
	
	/**
	 * Utility method that fetches the obs on the encounter that is associated with the specified concept
	 * and updates it if needed
	 */
	private void updateObs(Concept concept, Encounter encounter, String value) {
		Obs obs = MdrtbUtil.getObsFromEncounter(concept, encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(value)) {
			return;
		}
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !StringUtils.equals(obs.getValueText(), value)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module");
			}
				
			// now create the new Obs and add it to the encounter
			if(StringUtils.isNotBlank(value)) {
				obs = new Obs (encounter.getPatient(), concept, encounter.getEncounterDatetime(), encounter.getLocation());
				
				// set value numeric if concept is type numeric, otherwise set value text
				if (concept.getDatatype().equals(Context.getConceptService().getConceptDatatypeByName("NUMERIC"))) {
					obs.setValueNumeric(Double.valueOf(value));
				}
				else {
					obs.setValueText(value);
				}
				
				encounter.addObs(obs);
			}
		}
	}
	
}
