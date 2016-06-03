package org.openmrs.module.mdrtb.status;



public class HivStatus extends Status {

	public HivStatus() {
	}

	public StatusItem getHivStatus() {
		return getItem("hivStatus");
	}
	
	public StatusItem getRegimens() {
		return getItem("regimens");
	}
	
	public StatusItem getCurrentRegimen() {
		return getItem("currentRegimen");
	}
	
	public StatusItem getMostRecentCd4Count() {
		return getItem("mostRecentCd4Count");
	}
	
	public StatusItem getMostRecentTestResult() {
		return getItem("mostRecentTestResult");
	}
	
	public StatusItem getArtTreatment() {
		return getItem("artTreatment");
	}
}
