package org.openmrs.module.mdrtb.web.util;


public class MdrtbWebUtil {

	/**
     * Appends the patientProgramId and patientId to specified url
     */
    public static String appendParameters(String url, Integer patientId, Integer patientProgramId) {
  
    	// append the patient id if necessary
    	if (patientId != null && !url.contains("patientId")) {
    		if (url.contains("?")) {
    			url = url + "&patientId=" + patientId;
    		}
    		else {
    			url = url + "?patientId=" + patientId;
    		}
    	}
    	
    	// append the patient program id if necessary
    	if (patientProgramId != null && !url.contains("patientProgramId")) {
    		if (url.contains("?")) {
    			url = url + "&patientProgramId=" + patientProgramId;
    		}
    		else {
    			url = url + "?patientProgramId=" + patientProgramId;
    		}
    	}
    	
    	return url;
    }

}
