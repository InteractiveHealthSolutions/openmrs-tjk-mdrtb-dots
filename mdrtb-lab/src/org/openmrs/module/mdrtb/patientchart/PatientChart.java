package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;


public class PatientChart {

	// Note: this map should be an ordered (Linked) map because we rely on the order in the implementation
	private List<Record> records;
	
	private List<Concept> drugTypes;
	
	
	public PatientChart() {
		setRecords(new LinkedList<Record>());
	}


	public void setRecords(List<Record> records) {
	    this.records = records;
	}
	
	public List<Record> getRecords() {
	    return records;
    }

	public void setDrugTypes(List<Concept> drugTypes) {
	    this.drugTypes = drugTypes;
    }

	public List<Concept> getDrugTypes() {
	    return drugTypes;
    }
}
