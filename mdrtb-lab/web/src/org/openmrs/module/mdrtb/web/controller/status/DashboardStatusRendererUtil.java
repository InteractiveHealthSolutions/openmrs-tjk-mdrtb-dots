package org.openmrs.module.mdrtb.web.controller.status;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;


public class DashboardStatusRendererUtil {

	public static String renderDrugList(List<Concept> drugList) {
		StringBuffer profile = new StringBuffer();
    	
    	for (Concept drug : drugList) {
    		profile.append(drug.getBestShortName(Context.getLocale()).toString() + " + ");
    	}
    	
    	// remove the last plus sign and spaces
    	if(profile.length() > 3) {
    		profile.delete(profile.length() - 3, profile.length());
    	}
    	
	    return profile.toString();
	}
	
}
