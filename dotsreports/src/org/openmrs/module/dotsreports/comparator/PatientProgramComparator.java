package org.openmrs.module.dotsreports.comparator;

import java.util.Comparator;

import org.openmrs.PatientProgram;


public class PatientProgramComparator implements Comparator<PatientProgram> {

	public int compare(PatientProgram program1, PatientProgram program2) {
		if(program1 == null || program1.getDateEnrolled() == null) {
	    	return 1;
	    } else if (program2 == null || program2.getDateEnrolled() == null) {
	    	return -1;
	    } else {
	    	return program1.getDateEnrolled().compareTo(program2.getDateEnrolled());
	    }
	}
}
