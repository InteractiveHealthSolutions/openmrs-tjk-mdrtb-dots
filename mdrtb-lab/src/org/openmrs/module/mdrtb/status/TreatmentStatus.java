package org.openmrs.module.mdrtb.status;

import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;


public class TreatmentStatus extends Status {

	public TreatmentStatus(MdrtbPatientProgram program) {
	    super(program);
    }

	public StatusItem getTreatmentState() {
		return getItem("treatmentState");
	}
	
	public StatusItem getRegimens() {
		return getItem("regimens");
	}
	
}
