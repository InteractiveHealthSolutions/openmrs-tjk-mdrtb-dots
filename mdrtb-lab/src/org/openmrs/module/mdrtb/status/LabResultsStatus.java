package org.openmrs.module.mdrtb.status;

import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;


public class LabResultsStatus extends Status {

	public LabResultsStatus(MdrtbPatientProgram program) {
	    super(program);
    }

	public StatusItem getSmearConversion() {
		return getItem("smearConversion");
	}
	
	public StatusItem getCultureConversion() {
		return getItem("cultureConversion");
	}
	
	public StatusItem getDiagnosticSmear() {
		return getItem("diagnosticSmear");
	}
	
	public StatusItem getDiagnosticCulture() {
		return getItem("diagnosticCulture");
	}
	
	public StatusItem getDiagnosticXpert() {
		return getItem("diagnosticXpert");
	}
	
	public StatusItem getDiagnosticHAIN() {
		return getItem("diagnosticHAIN");
	}
	
	public StatusItem getMostRecentSmear() {
		return getItem("mostRecentSmear");
	}
	
	public StatusItem getMostRecentCulture() {
		return getItem("mostRecentCulture");
	}
	
	public StatusItem getMostRecentXpert() {
		return getItem("mostRecentXpert");
	}
	
	public StatusItem getMostRecentHAIN() {
		return getItem("mostRecentHAIN");
	}
	
	public StatusItem getDrugResistanceProfile() {
		return getItem("drugResistanceProfile");
	}
	
	public StatusItem getPendingLabResults() {
		return getItem("pendingLabResults");
	}
	
	public StatusItem getTbClassification() {
		return getItem("tbClassification");
	}

	public StatusItem getAnatomicalSite() {
		return getItem("anatomicalSite");
	}
}
