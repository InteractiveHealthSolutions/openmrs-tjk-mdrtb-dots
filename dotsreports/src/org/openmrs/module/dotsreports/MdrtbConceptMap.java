package org.openmrs.module.dotsreports;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.exception.ErrorFetchingConceptException;
import org.openmrs.module.dotsreports.exception.MissingConceptException;

/**
 * This class defines all of the Concept Mappings that are required/used by this module
 * and provides a cache-enabled utility method for retrieving them
 */
public class MdrtbConceptMap {

	protected final Log log = LogFactory.getLog(getClass());
	
	// The Concept Map name-space
    public static final String MDRTB_CONCEPT_MAPPING_CODE = "org.openmrs.module.mdrtb";
    
	// Private Cache for Concepts
	private Map<String, Concept> cache = new HashMap<String, Concept>();
	
	/**
	 * Utility method which retrieves a mapped MDR-TB concept by code
	 */
	public Concept lookup(String [] conceptMapping) {
		
		try {
			
			for(int i=0;i<conceptMapping.length; i++) {
				System.out.println(">>>> " + conceptMapping[i]);
			}
			// see if we have have the concept in the cache
			Concept concept = cache.get(conceptMapping[0]);
		
			// if we've found a concept, return it
			if(concept != null) {
				return concept;
			}
		
			// if not, test all the mappings in the array
			for(String mapName : conceptMapping) {		
				concept = Context.getConceptService().getConceptByMapping(mapName, MDRTB_CONCEPT_MAPPING_CODE);
				//concept = Context.getConceptService().getConceptByName(mapName);//, MDRTB_CONCEPT_MAPPING_CODE);
			
				// if we've found a match, initialize it and return it
				if(concept != null) {
					initializeEverythingAboutConcept(concept);
					cache.put(conceptMapping[0], concept);
					return concept;
				}
			}
		}
		catch(Exception e) {
			throw new ErrorFetchingConceptException("Error fetching concept for mapping " + conceptMapping[0], e);
		}
		
		// if we didn't find a match, fail hard
		throw new MissingConceptException("Can't find concept for mapping " + conceptMapping[0]);
	}
	

	/**
	 * Utility method which retrieves a mapped MDR-TB concept by code
	 */
	public Concept lookup(String conceptMapping) {
		String [] search = new String[1];
		search[0] = conceptMapping;
		return lookup(search);
	}
	
	/**
	 * Resets the cache
	 */
	public void resetCache() {
		cache.clear();
	}
	
	/**
	 * @return all of the defined Concept Mappings
	 */
	public Set<String[]> getAllConceptMappings() {
		Set<String[]> ret = new TreeSet<String[]>();
		for (Field f : TbConcepts.class.getFields()) {
			
			// TODO: make sure this array reflection works--this has not yet been used or tested
			
			if (f.getType() == Array.class) {
				int modifier = f.getModifiers();
				if (Modifier.isFinal(modifier) && Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
					try {
						Object value = f.get(null);
						if (value != null) {
							ret.add((String []) value);
						}
					}
					catch (IllegalAccessException iae) {
						throw new RuntimeException("Unable to access field: " + f, iae);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Initialize concept so subsequent loading is quick for lazy collections
	 */
	protected void initializeEverythingAboutConcept(Concept c) {
		if (c != null) {
			c.getDatatype().getHl7Abbreviation();
			for (ConceptName cns : c.getNames()){
				Collection<ConceptNameTag> tags = cns.getTags();
				for (ConceptNameTag cnTag : tags){
					cnTag.getTag();
				}
            }
			Collection<ConceptAnswer> cas = c.getAnswers();
			if (cas != null) {
				for (ConceptAnswer ca : cas) {
					Collection<ConceptName> cnsTmp = ca.getAnswerConcept().getNames();
					for (ConceptName cn:cnsTmp) {
						Collection<ConceptNameTag> tags = cn.getTags();
						for (ConceptNameTag cnTag:tags) {
							cnTag.getTag();
						}
					}
				}
			}
		}
	}
}
