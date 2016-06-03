package org.openmrs.module.mdrtb.patientchart;

import java.util.Date;

import org.openmrs.util.OpenmrsUtil;

public class StateChangeRecordComponent implements RecordComponent {

	private Date date;
	
	private String text;
	
	public StateChangeRecordComponent() {
	}
	
    public StateChangeRecordComponent(Date date, String text) {
	    this.date = date;
	    this.text = text;
    }

	public String getType() {
	    return "stateChangeRecordComponent";
    }

	public void setDate(Date date) {
	    this.date = date;
    }

	public Date getDate() {
	    return date;
    }

	public void setText(String text) {
	    this.text = text;
    }
	
	public int compareTo(RecordComponent component) {
    	return OpenmrsUtil.compareWithNullAsEarliest(this.getDate(), component.getDate());
    }

	public String getText() {
	    return text;
    }	
}
