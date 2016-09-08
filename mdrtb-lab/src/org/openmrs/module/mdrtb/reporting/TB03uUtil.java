package org.openmrs.module.mdrtb.reporting;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.HAIN;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Xpert;

public class TB03uUtil {

	
	public static Culture getDiagnosticCulture(Patient patient) {
		Culture c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==0) {
				if(specimen.getCultures().size()>0) {
					c = specimen.getCultures().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Xpert getFirstXpert(Patient patient) {
		Xpert c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getXperts()!=null && specimen.getXperts().size() > 0) {
					c = specimen.getXperts().get(0);
					break;
				}
		}
		
		return c;
	}
	
	public static HAIN getFirstHAIN(Patient patient) {
		HAIN c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getHAINs()!=null && specimen.getHAINs().size() > 0) {
				c = specimen.getHAINs().get(0);
				break;
			}
		
		}
		
		return c;
	}
	
	public static Smear getDiagnosticSmear(Patient patient) {
		Smear c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==0) {
				if(specimen.getSmears().size()>0) {
					c = specimen.getSmears().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Smear getFollowupSmear(Patient patient, Integer month) {
		Smear c = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			if(specimen.getMonthOfTreatment()!=null && specimen.getMonthOfTreatment()==month.intValue()) {
				if(specimen.getSmears().size()>0) {
					c = specimen.getSmears().get(0);
					break;
				}
					
			}
		
		}
		
		return c;
	}
	
	public static Dst getDiagnosticDST(Patient patient) {
		Dst d = null;
		
		for (Specimen specimen : Context.getService(MdrtbService.class).getSpecimens(patient)) {//, startDateCollected, endDateCollected)) {
			
				if(specimen.getDsts().size()>0) {
					d = specimen.getDsts().get(0);
					break;
				}
					
		}
		
		return d;
	}
	
	
	
}
