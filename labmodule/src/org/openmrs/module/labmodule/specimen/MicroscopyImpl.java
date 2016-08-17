package org.openmrs.module.labmodule.specimen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

/**
 * An implementaton of a MdrtbCulture.  This wraps an ObsGroup and provides access to culture
 * data within the obsgroup.
 */
public class MicroscopyImpl extends BacteriologyImpl implements Microscopy {

	public MicroscopyImpl() {
	}
	
	// set up a culture object, given an existing obs
	public MicroscopyImpl(Obs microscopy) {
		
		if(microscopy == null || !(microscopy.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize culture: invalid obs used for initialization.");
		}
		
		test = microscopy;
	}
	
	// create a new culture object, given an existing patient
	public MicroscopyImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "microscopy";
	}
	
	public Concept getSampleApperence(){
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_APPEARANCE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}
	public void setSampleApperence(Concept apperence){
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_APPEARANCE), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && apperence == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(apperence == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_APPEARANCE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(apperence);
	}
	
	public Concept getSampleResult(){
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
	}
	public void setSampleResult(Concept result){
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && result == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(result == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(result);
	}
	
	public void setLocation(Location location) {
		test.setLocation(location);
		
		// also propagate this date to all the other grouped obs
		for(Obs obs : test.getGroupMembers()) {
			obs.setLocation(location);
		}
	}
	
	public void setDateCollected(Date dateCollected) {
		test.setObsDatetime(dateCollected);
	
		// also propagate this date to all the other grouped obs
		for(Obs obs : test.getGroupMembers()) {
			obs.setDateCreated(new Date());
		}
	}
	

	/* (non-Javadoc)
	 * @see org.openmrs.module.labmodule.specimen.TestImpl#getComments()
	 */
	@Override
	public String getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.labmodule.specimen.TestImpl#setComments(java.lang.String)
	 */
	@Override
	public void setComments(String comments) {
		// TODO Auto-generated method stub
		
	}
	
}
