package org.openmrs.module.mdrtb.status;

import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;


public class VisitStatus extends Status {

	public VisitStatus(MdrtbPatientProgram program) {
	    super(program);
    }

	public StatusItem getIntakeVisits() {
		return getItem("intakeVisits");
	}
	
	public StatusItem getFollowUpVisits() {
		return getItem("followUpVisits");
	}
	
	public StatusItem getScheduledFollowUpVisits() {
		return getItem("scheduledFollowUpVisits");
	}
	
	public StatusItem getSpecimenCollectionVisits() {
		return getItem("specimenCollectionVisits");
	}
	
	// used to store the link that should be used for a new intake visit
	public StatusItem getNewIntakeVisit() {
		return getItem("newIntakeVisit");
	}
	
	// used to store the link that should be used for the a new follow-up visit
	public StatusItem getNewFollowUpVisit() {
		return getItem("newFollowUpVisit");
	}
}
