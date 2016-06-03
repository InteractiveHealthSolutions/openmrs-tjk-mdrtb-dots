package org.openmrs.module.mdrtb.specimen;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.service.MdrtbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class XpertImpl extends TestImpl implements Xpert {
	
	public XpertImpl() {
	}

	// set up a xpert object, given an existing obs
	public XpertImpl(Obs xpert) {
		
		if(xpert == null || !(xpert.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize xpert: invalid obs used for initialization.");
		}
		
		test = xpert;
	}
	
	// create a new smear object, given an existing patient
	public XpertImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create xpert: encounter can not be null.");
		}
		
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs (encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "xpert";
	}
	
    
    
    public String getComments() {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
      
   

    public void setComments(String comments) {
    	Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
   

	@Override
	public Concept getResult() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResult(Concept mtbResult) {
		
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && mtbResult == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(mtbResult == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mtbResult);
	}

	@Override
	public Concept getRifResistance() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setRifResistance(Concept rifResistance) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && rifResistance == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(rifResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(rifResistance);
		
	}

	@Override
	public String getErrorCode() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE), test);
    	
    	if (obs == null || obs.getValueText() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueText();
    	}
	}

	@Override
	public void setErrorCode(String code) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && code == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(code == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ERROR_CODE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueText(code);
		
	}
	
	@Override
	public Concept getMtbBurden() {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMtbBurden(Concept mtbBurden) {
		Obs obs = MdrtbUtil.getObsFromObsGroup(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test);
    	
	   	 // if this obs have not been created, and there is no data to add, do nothing
			if (obs == null && mtbBurden == null) {
				return;
			}
			
			// if we are trying to set the obs to null, simply void the obs
			if(mtbBurden == null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
				return;
			}
	   	
			// initialize the obs if needed
			if (obs == null) {		
				obs = new Obs (test.getPerson(), Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.XPERT_MTB_BURDEN), test.getObsDatetime(), test.getLocation());
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
			
			// now save the value
			obs.setValueCoded(mtbBurden);
	}
}

