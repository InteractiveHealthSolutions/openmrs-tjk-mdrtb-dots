package org.openmrs.module.labmodule.specimen;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

/**
 * An implementaton of a MdrtbSmear.  This wraps an ObsGroup and provides access to smear
 * data within the obsgroup.
 */
public class HAIN2Impl extends TestImpl implements HAIN2 {
	
	public HAIN2Impl() {
	}

	// set up a xpert object, given an existing obs
	public HAIN2Impl(Obs hain) {
		
		if(hain == null || !(hain.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize xpert: invalid obs used for initialization.");
		}
		
		test = hain;
	}
	
	// create a new smear object, given an existing patient
	public HAIN2Impl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create hain: encounter can not be null.");
		}
		
		// note that we are setting the location null--tests don't immediately inherit the location of the parent encounter
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "hain";
	}
	
    
    
    public String getComments() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
      
   

    public void setComments(String comments) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
   

	@Override
	public Concept getResult() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setResult(Concept mtbResult) {
		
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test);
    	
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
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MTB_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(mtbResult);
	}
	
	@Override
	public Concept getMoxResistance() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MOX), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMoxResistance(Concept moxResistance) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MOX), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && moxResistance == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(moxResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MOX), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(moxResistance);
		
	}

	@Override
	public Concept getCmResistance() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CM), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setCmResistance(Concept cmResistance) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CM), test);
    	
   	 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && cmResistance == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(cmResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CM), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(cmResistance);
		
	}

	@Override
	public Concept getErResistance() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.E), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setErResistance(Concept erResistance) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.E), test);
    	
   	 	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && erResistance == null) {
			return;
		}
		
		// if we are trying to set the obs to null, simply void the obs
		if(erResistance == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
   	
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.E), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(erResistance);
	}		
	
	@Override
	public Concept getMtbBurden() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}

	@Override
	public void setMtbBurden(Concept mtbBurden) {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test);
    	
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
				obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.XPERT_MTB_BURDEN), test.getObsDatetime(), test.getLocation());
				obs.setEncounter(test.getEncounter());
				test.addGroupMember(obs);
			}
			
			// now save the value
			obs.setValueCoded(mtbBurden);
	}
}

