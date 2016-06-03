package org.openmrs.module.mdrtb.status;


public class StatusFlag {

	private String message;
	
	private String action;

	public void setMessage(String message) {
	    this.message = message;
    }

	public String getMessage() {
	    return message;
    }

	public void setAction(String action) {
	    this.action = action;
    }

	public String getAction() {
	    return action;
    }
	
}
