package org.openmrs.module.dotsreports.specimen;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.service.TbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class SmearImpl extends BacteriologyImpl implements Smear {
	
	public SmearImpl() {
	}

	// set up a smear object, given an existing obs
	public SmearImpl(Obs smear) {
		
		if(smear == null || !(smear.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize smear: invalid obs used for initialization.");
		}
		
		test = smear;
	}
	
	// create a new smear object, given an existing patient
	public SmearImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create smear: encounter can not be null.");
		}
		
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "smear";
	}
	
    public Integer getBacilli() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.BACILLI), test);
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
    }
    
    public String getComments() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
      
    public Concept getMethod() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_METHOD), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }

    public void setBacilli(Integer bacilli) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.BACILLI), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bacilli == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(bacilli == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.BACILLI), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(bacilli.doubleValue());
    }   

    public void setComments(String comments) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
    
    public void setMethod(Concept method) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_METHOD), test);
    	
    	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && method == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(method == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
    	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_METHOD), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }
}

