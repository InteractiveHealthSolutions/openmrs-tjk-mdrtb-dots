package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.Xpert;

/**
 * Interface that defines how to interaction with a Lab Result
 * 
 * An implementation of this interface will help us encapsulate the 
 * the messiness of storing the specimen data in nested obsgroups
 */
public interface LabResult extends Comparable<LabResult>{

	/**
	 * Data points this interface provides access to
	 * 
	 * id: the id code of the Lab Result (primary key in the database)
	 * labNumber: an idenifier that has been assigned to the sample for trackin purposes
	 * dateRecieve: date of receiving of request for laboratory investigation 
	 * patient: the patient associated with the specimen
	 * provider: who collected the specimen
	 * location: lab Name
	 * comments: any comments about the specimen itself
	 * 
	 * requestingLabNumber: Requesting medical facility
	 * investigationPurpose: Purpose of investigation
	 * biologicalSpecimen: Biological Specimen
	 * 
	 * peripheralLabNo: Peripheral laboratory investigation number
	 * bacterioscopyResult: Bacterioscopy Result:
	 * dateResult: Date of result
	 * 
	 * dateSampleSentToBacteriologicalLab: Date of sending of biological sample to bacteriological laboratory: 
	 * resultOfInvestigationOfBacteriologicalLab: Result of investigation in bacteriological laboratory
	 * 
	 * This interface also defines a "getLabResult" method, intended to be used
	 * by a service to retrieve the underlying object that needs to be saved
	 * to persist this specimen, and a "getLabResultID", used to retrieve whatever ID
	 * is used to reference the specimen (in the standard implementation, it's the 
	 * id of the underlying encounter)
	 */

	public Object getLabResult();
	public String getId();   
	
	public String getLabNumber();
	public void setLabNumber(String labNumber);
	
	public Date getInvestigationDate();
	public void setInvestigationDate(Date investigationDate);
	
	public Concept getRequestingLabName();
	public void setRequestingLabName(Concept requestingLabName);
	
	public Concept getInvestigationPurpose();
	public void setInvestigationPurpose(Concept investigationPurpose);
	
	public Concept getBiologicalSpecimen();
	public void setBiologicalSpecimen(Concept biologicalSpecimen);
	
	public String getPeripheralLabNumber();
	public void setPeripheralLabNumber(String peripheralLabNumber);
	
	public Concept getMicroscopyResult();
	public void setMicroscopyResult(Concept microscopyResult);
	
	public Date getDateResult();
	public void setDateResult(Date dateResult);
	
	public Date getDateSampleSentToBacteriologicalLab();
	public void setDateSampleSentToBacteriologicalLab(Date dateSampleSentToBacteriologicalLab);
	
	public String getResultOfInvestigationOfBacteriologicalLab();
	public void setResultOfInvestigationOfBacteriologicalLab(String resultOfInvestigationOfBacteriologicalLab);
	
	public String getComments();
	public void setComments(String comments);
	
	public Patient getPatient();
	public void setPatient(Patient patient);
	
	public Location getLocation();
	public void setLocation(Location location);
	
	public Person getProvider();
	public void setProvider(Person provider);
	
	public Date getDateCollected();
	public void setDateCollected(Date dateCollected);

	public Microscopy addMicroscopy();
	public Xpert addXpert();
	public Smear addSmear();
	public HAIN addHAIN();
	public HAIN2 addHAIN2();
	public Culture addCulture();
	
	public List<Culture> getCultures();
	public List<HAIN2> getHAINS2();
	public List<HAIN> getHAINS();
	public List<Xpert> getXperts();
	public List<Microscopy> getMicroscopies();
	
	public void voidPeripheralLabNo();
	public void voidMicroscopyResult();
	public void voidDateResult();
	
	public void setStatus(String status);
	public String getStatus();
}
