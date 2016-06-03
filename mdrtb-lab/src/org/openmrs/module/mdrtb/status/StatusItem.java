package org.openmrs.module.mdrtb.status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class StatusItem {

	private Object value;
	
	private Date date;
	
	private String displayString;
	
	private String link;
	
	private List<StatusFlag> flags = new LinkedList<StatusFlag>();

	public StatusItem() {
	}
	
	public StatusItem(Object value) {
		this.value = value;
	}
	
    public Object getValue() {
    	return value;
    }
	
    public void setValue(Object value) {
    	this.value = value;
    }

    public void setDate(Date date) {
	    this.date = date;
    }

	public Date getDate() {
	    return date;
    }

	public String getDisplayString() {
    	return displayString;
    }
	
    public void setDisplayString(String displayString) {
    	this.displayString = displayString;
    }

    public void setLink(String link) {
	    this.link = link;
    }

	public String getLink() {
	    return link;
    }

	public void setFlags(List<StatusFlag> flags) {
	    this.flags = flags;
    }


	public List<StatusFlag> getFlags() {
	    return flags;
    }

	public void addFlag(StatusFlag flag) {
		this.flags.add(flag);
    }

	public void removeFlag(StatusFlag flag) {
		this.flags.remove(flag);
	}
	
	public Boolean getFlagged() {
		return !this.flags.isEmpty();
	}
}


