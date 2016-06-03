package org.openmrs.module.dotsreports.specimen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.service.TbService;

/**
 * An implementation of a MdrtbDst.  This wraps an ObsGroup and provides access to dst
 * data within the obsgroup.
 */
public class DstImpl extends TestImpl implements Dst {

	Map<Integer,List<DstResult>> resultsMap = null; // TODO: we need to cache the results map... do we need to worry about timing it out? 
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public DstImpl() {
	}
	
	// set up a dst object, given an existing obs
	public DstImpl(Obs dst) {
		
		if(dst == null || !(dst.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.DST_CONSTRUCT)))) {
			throw new RuntimeException ("Cannot initialize dst: invalid obs used for initialization.");
		}
		
		test = dst;
	}
	
	// create a new culture object, given an existing patient
	public DstImpl(Encounter encounter) {
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create culture: encounter can not be null.");
		}
		
		test = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.DST_CONSTRUCT), encounter.getEncounterDatetime(), null);
	}
	
	@Override
	public String getTestType() {
		return "dst";
	}

	public DstResult addResult() {
		// create a new obs for the result, set to the proper values
		Obs resultObs = new Obs(this.test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_RESULT), this.test.getObsDatetime(), this.test.getLocation());
		resultObs.setEncounter(this.test.getEncounter());
		
		// add the result to this obs group
		this.test.addGroupMember(resultObs);
		
		// now create and return a new DstResult
		return new DstResultImpl(resultObs);
	}
	
    public Integer getColoniesInControl() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES_IN_CONTROL), test);
    	
    	if (obs == null || obs.getValueNumeric() == null) {
    		return null;
    	}
    	else {
    		return obs.getValueNumeric().intValue();
    	}
    }

    public String getComments() {
    	return test.getComment();
    }
    
    public Boolean getDirect() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DIRECT_INDIRECT), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueAsBoolean();
    	}
    }

    public Concept getMethod() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_METHOD), test);
    	
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

    public List<DstResult> getResults() {
    	List<DstResult> results = new LinkedList<DstResult>();
		
		// iterate through all the obs groups, create dst results from them, and add them to the list
		if(test.getGroupMembers() != null) {
			for(Obs obs : test.getGroupMembers()) {
				// need to filter for voided obs, since get group members returns voided and non-voided
				if (!obs.isVoided() && obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.DST_RESULT))) {
					results.add(new DstResultImpl(obs));
				}
			}
		}
		return results;
    }
    
    // Note this is created ONCE per instantiation, for performance reasons, so if underlying drugs change, this will be inaccurate
    public Map<Integer,List<DstResult>> getResultsMap() {  
    
    	if (resultsMap == null) {   		
    		resultsMap = new HashMap<Integer,List<DstResult>>();
    	
    		// map the results based on a key created by concatenating the string representation of the drug concept id and the
    		// string representation of the concentration
    		for(DstResult result : getResults()) {
    			
    			Integer drug = result.getDrug().getId();
    			
    			// if a result for this drug already exists in the map, attach this result to that list
    			if(resultsMap.containsKey(drug)) {
    				resultsMap.get(drug).add(result);
    				// re-sort, so that the concentrations are in order
    				Collections.sort(resultsMap.get(drug));
    			}
    			// otherwise, create a new entry for this drug
    			else {
    				List<DstResult> drugResults = new LinkedList<DstResult>();
    				drugResults.add(result);
    				resultsMap.put(drug, drugResults);
    			}
    			
    			// TODO: remove this when we are sure we don't need it
    			/**
    			if(result.getConcentration() != null) {
    				resultsMap.put((result.getDrug().getId()).toString() + "|" + result.getConcentration().toString(), result);
    			}
    			else {
    				resultsMap.put((result.getDrug().getId()).toString(), result);
    			}
    			*/
    		}
    	}
    	
    	return resultsMap;
    }
    
    public void removeResult(DstResult result) {
    	((DstResultImpl) result).voidResult();
    }
    
    public void setColoniesInControl(Integer coloniesInControl) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.COLONIES_IN_CONTROL), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && coloniesInControl == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(coloniesInControl == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
    	// initialize the obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.COLONIES_IN_CONTROL), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueNumeric(coloniesInControl.doubleValue());
    }

    public void setComments(String comments) {
    	test.setComment(comments);
    }
    
    public void setDirect(Boolean direct) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DIRECT_INDIRECT), test);
    	
    	// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && direct == null) {
			return;
		}
    	
		// if we are trying to set the obs to null, simply void the obs
		if(direct == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// initialize the obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DIRECT_INDIRECT), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now save the value
		// is this the proper way to cast from Boolean to Double?
		obs.setValueNumeric(direct ? 1.0 : 0.0);
    }

    public void setMethod(Concept method) {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.DST_METHOD), test);
    	
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
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.DST_METHOD), test.getObsDatetime(), test.getLocation());
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
