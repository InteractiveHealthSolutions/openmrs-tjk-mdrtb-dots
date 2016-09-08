package org.openmrs.module.mdrtb.specimen;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;

/**
 * Interface that defines how to interaction with a specimen
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the specimen data in nested obsgroups
 */
public interface Specimen extends Comparable<Specimen>{

	/**
	 * Data points this interface provides access to
	 * 
	 * id: the id code of the specimen (primary key in the database)
	 * identifier: an idenifier that has been assigned to the sample for trackin purposes
	 * type: the concept that represents the type of the specimen (sputum, etc...)
	 * patient: the patient associated with the specimen
	 * location: the location where the specimen was collected
	 * provider: who collected the specimen
	 * dateCollected: the date the specimen was collected
	 * comments: any comments about the specimen itself
	 * 
	 * scannedLabReports: a list of the scanned lab reports associated with patient
	 * smears: a list of the smears associated with the specimen
	 * cultures: a list of the cultures associated with the specimen
	 * dsts: a list of the DSTS associated with the specimen
	 * 
	 * dstResultsMap: a combined map of all the dst results for *all* dst results associated with this specimen
	 * 
	 * This interface also defines a "getSpecimen" method, intended to be used
	 * by a service to retrieve the underlying object that needs to be saved
	 * to persist this specimen, and a "getSpecimenID", used to retrieve whatever ID
	 * is used to reference the specimen (in the standard implementation, it's the 
	 * id of the underlying encounter)
	 */

	public Object getSpecimen();
	public String getId();      
	
	public String getIdentifier();
	public void setIdentifier(String id);
	
	public Concept getType();
	public void setType(Concept type);
	
	public Patient getPatient();
	public void setPatient(Patient patient);
	
	public Location getLocation();
	public void setLocation(Location location);
	
	public Person getProvider();
	public void setProvider(Person provider);
	
	public Date getDateCollected();
	public void setDateCollected(Date dateCollected);
	
	public Concept getAppearance();
	public void setAppearance(Concept appearance);
	/*public void setAppearance(String appearance);*/
	
	public String getComments();
	public void setComments(String comments);
	
	public List<ScannedLabReport> getScannedLabReports();
	
	public List<Smear> getSmears();
	public Smear addSmear();
	
	public List<Culture> getCultures();
	public Culture addCulture();
	
	public List<Dst> getDsts();
	public Dst addDst();
	
	public List<Xpert> getXperts();
	public Xpert addXpert();
	
	public List<HAIN> getHAINs();
	public HAIN addHAIN();
	
	public List<Test> getTests();
	
	public Map<Integer,List<DstResult>> getDstResultsMap();
	
	public ScannedLabReport addScannedLabReport();
	public void removeScannedLabReport(ScannedLabReport report);
	
	public Double getMonthOfTreatment();
		
}
