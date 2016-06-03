package org.openmrs.module.mdrtb.form;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;


public interface SimpleForm {

	public Integer getId();
	
	public void setEncounter(Encounter encounter);

	public Encounter getEncounter();
	
	public Person getProvider();
	
	public void setProvider(Person provider);
	
	public Patient getPatient();
	
	public void setPatient(Patient patient);
	
	public Date getEncounterDatetime();

	public void setEncounterDatetime(Date date);
	
	public Location getLocation();
	
	public void setLocation(Location location);
	
	public String getWeight();
	
	public void setWeight(String weight);
	
	public String getPulse();
	
	public void setPulse(String pulse);
	
	public String getTemperature();
	
	public void setTemperature(String temperature);
	
	public String getSystolicBloodPressure();
	
	public void setSystolicBloodPressure(String pressure);
	
	public String getRespiratoryRate();
	
	public void setRespiratoryRate(String rate);
	
	public String getClinicianNotes();
	
	public void setClinicianNotes(String comments);
}
