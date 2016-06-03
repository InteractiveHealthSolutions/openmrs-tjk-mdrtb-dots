package org.openmrs.module.mdrtb.patientchart;

import java.util.Date;
import java.util.List;

import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.util.OpenmrsUtil;


public class SpecimenRecordComponent implements RecordComponent {

	private Specimen specimen;
	
	private List<Regimen> regimens;
	
	private Date date;
	
	public SpecimenRecordComponent() {}
	
	public SpecimenRecordComponent(Specimen specimen) {
		this.specimen = specimen;
		if(specimen != null) {
			this.date = specimen.getDateCollected(); // if there is a specimen, date is set to date collected of specimen
		}
	}
	
	public SpecimenRecordComponent(Specimen specimen, List<Regimen> regimens) {
		this.specimen = specimen;
		this.regimens = regimens;
		if(specimen != null) {
			this.date = specimen.getDateCollected(); // if there is a specimen, date is set to date collected of specimen
		}
	}
	
	public SpecimenRecordComponent(Date date, List<Regimen> regimens) {
		this.date = date;  // if there is no specimen, date must be specified
		this.regimens = regimens;
	}

	/**
	 * Record Component interface methods
	 */
	public String getType() {
		return "specimenRecordComponent";
	}
	
	public Date getDate() {
		return date;
    }

    public int compareTo(RecordComponent component) {
    	return OpenmrsUtil.compareWithNullAsEarliest(this.getDate(), component.getDate());
    }
	
	/**
	 * Other public methods
	 */
	
	public void setSpecimen(Specimen specimen) {
	    this.specimen = specimen;
    }

	public Specimen getSpecimen() {
	    return specimen;
    }

	public void setRegimens(List<Regimen> regimen) {
	    this.regimens = regimen;
    }

	public List<Regimen> getRegimens() {
	    return regimens;
    }
}
