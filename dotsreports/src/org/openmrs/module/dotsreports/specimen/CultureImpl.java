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
 * An implementaton of a MdrtbCulture.  This wraps an ObsGroup and provides access to culture
 * data within the obsgroup.
 */
public class CultureImpl extends BacteriologyImpl implements Culture {

	public CultureImpl() {
	}
	
	// set up a culture object, given an existing obs
	public CultureImpl(Obs culture) {
		
		if(culture == null || !(culture.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize culture: invalid obs used for initialization.");
		}
		
		test = culture;
	}
	
	// create a new culture object, given an existing patient
	public CultureImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "culture";
	}
	
    public Integer getColonies() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test);
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return (obs.getValueNumeric()).intValue();
    	}
    }
    
    public String getComments() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getComment();
    	}
    }
    
    public Integer getDaysToPositivity() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test);
    	
    	if(obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
    }
    
    public Concept getMethod() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public Concept getOrganismType() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueCoded();
    	}
    }
    
    public String getOrganismTypeNonCoded() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueText();
    	}
    }
    
    public void setColonies(Integer colonies) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && colonies == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(colonies == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.COLONIES), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(colonies.doubleValue());
    }   

    public void setComments(String comments) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test);
    	
    	// if this obs has not been created, and there is no data to add, do nothing
    	if (obs == null && StringUtils.isBlank(comments)) {
    		return;
    	}
    	
    	// we don't need to test for comments == null here like the other obs because
    	// the comments are stored on the results obs
    	
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		obs.setComment(comments);
    }
    
    public void setDaysToPositivity(Integer daysToPositivity) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && daysToPositivity == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(daysToPositivity == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DAYS_TO_POSITIVITY), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(daysToPositivity.doubleValue());
    }   
    
    public void setMethod(Concept method) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test);
    	
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
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_METHOD), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(method);
    }
    
    public void setOrganismType(Concept organismType) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueCoded(organismType);
    }
    
    public void setOrganismTypeNonCoded(String organismType) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && organismType == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(organismType == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TYPE_OF_ORGANISM_NON_CODED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		obs.setValueText(organismType);
    }
}
