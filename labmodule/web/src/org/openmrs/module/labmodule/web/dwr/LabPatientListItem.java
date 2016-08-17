package org.openmrs.module.labmodule.web.dwr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.web.dwr.PatientListItem;
import org.openmrs.web.dwr.PersonListItem;

public class LabPatientListItem extends PatientListItem {
protected final Log log = LogFactory.getLog(getClass());
	
	private Integer patientId;
	
	private String identifier = "";
	
	private Boolean identifierCheckDigit = false;
	
	private String otherIdentifiers = "";
	
	private String labNumber = "";
	
	private Date recievedDate;
	
	private int  microscopyTests;
	private int  xpertTests;
	private int  hainTests;
	private int  hain2Tests;
	private int  cultureTest;
	
	private String patientStatus = "";
	
	private Integer encounterId;
	
	
	public LabPatientListItem() {
	}
	
	public LabPatientListItem(Patient patient) {
		
		super(patient);
		
		if (patient != null) {
			
			patientId = patient.getPatientId();
			
			// get patient's identifiers
			boolean first = true;
			for (PatientIdentifier pi : patient.getIdentifiers()) {
				if (first) {
					identifier = pi.getIdentifier();
					identifierCheckDigit = pi.getIdentifierType().hasCheckDigit();
					first = false;
				} else {
					if (otherIdentifiers != "")
						otherIdentifiers += ",";
					otherIdentifiers += " " + pi.getIdentifier();
				}
			}
			
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof PatientListItem) {
			PatientListItem pi = (PatientListItem) obj;
			if (pi.getPatientId() == null || patientId == null)
				return false;
			return pi.getPatientId().equals(patientId);
		}
		return false;
	}
	
	public int hashCode() {
		if (patientId == null)
			return super.hashCode();
		return patientId.hashCode();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getOtherIdentifiers() {
		return otherIdentifiers;
	}
	
	public void setOtherIdentifiers(String otherIdentifiers) {
		this.otherIdentifiers = otherIdentifiers;
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public String getLabNumber() {
		return labNumber;
	}
	
	public void setLabNumber(String labNumber) {
		this.labNumber = labNumber;
	}
	
	public Date getRecievedDate() {
		return recievedDate;
	}
	
	public void setRecievedDate(Date recievedDate) {
		this.recievedDate = recievedDate;
	}
	
	public int getMicroscopyTests() {
		return microscopyTests;
	}
	
	public void setMicroscopyTests(int number) {
		this.microscopyTests = number;
	}
	
	public int getXpertTests() {
		return xpertTests;
	}
	
	public void setXpertTests(int number) {
		this.xpertTests = number;
	}
	
	public int getHainTests() {
		return hainTests;
	}
	
	public void setHainTests(int number) {
		this.hainTests = number;
	}
	
	public int getHain2Tests() {
		return hain2Tests;
	}
	
	public void setHain2Tests(int number) {
		this.hain2Tests = number;
	}
	
	public int getCultureTests() {
		return cultureTest;
	}
	
	public void setCultureTests(int number) {
		this.cultureTest = number;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	/**
	 * @return Returns the identifierIdentifierCheckdigit.
	 */
	public Boolean getIdentifierCheckDigit() {
		return identifierCheckDigit;
	}
	
	/**
	 * @param identifierIdentifierCheckdigit The identifierIdentifierCheckdigit to set.
	 */
	public void setIdentifierCheckDigit(Boolean identifierCheckDigit) {
		this.identifierCheckDigit = identifierCheckDigit;
	}
	
	public void setPatientStatus(String status) {
		this.patientStatus = status;
	}
	
	public String getPatientStatus() {
		return patientStatus;
	}
}
