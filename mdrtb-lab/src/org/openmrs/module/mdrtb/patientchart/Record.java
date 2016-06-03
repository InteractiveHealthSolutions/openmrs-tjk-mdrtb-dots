package org.openmrs.module.mdrtb.patientchart;

import java.util.LinkedList;
import java.util.List;


public class Record {
	
	private List<RecordComponent> components;
	
	private String label;
	
	public Record() {
		this.components = new LinkedList<RecordComponent>();
	}
	
	public Record(List<RecordComponent> specimens) {
		this.components = specimens;
	}

	public void setComponents(List<RecordComponent> specimens) {
	    this.components = specimens;
    }

	public List<RecordComponent> getComponents() {
	    return components;
    }

	public void setLabel(String label) {
	    this.label = label;
    }

	public String getLabel() {
	    return label;
    }
	
}
