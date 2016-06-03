package org.openmrs.module.dotsreports.comparator;

import java.util.Comparator;

import org.openmrs.PatientState;


public class PatientStateComparator implements Comparator<PatientState> {

    public int compare(PatientState state1, PatientState state2) {
    	if (state1 == null || state1.getStartDate() == null) {
    		return -1;
    	}
    	else if (state2 == null || state2.getStartDate() == null) {
    		return 1;
    	}
    	else {
    		return state1.getStartDate().compareTo(state2.getStartDate());
    	}
    }
}
