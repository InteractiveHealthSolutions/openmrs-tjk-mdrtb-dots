/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Turns a list of concept ids "123 1234 1235" into a List of ConceptSets
 */
public class ConceptSetsEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private Collection<ConceptSet> originalConceptSets = null;
	
	/**
	 * Default constructor taking in the current sets on a concept 
	 * 
	 * @param conceptSets the current object on the concept
	 */
	public ConceptSetsEditor(Collection<ConceptSet> conceptSets) {
		if (conceptSets == null)
			originalConceptSets = new Vector<ConceptSet>();
		
		this.originalConceptSets = conceptSets;
	}
	
	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		log.debug("setting conceptSets with text: " + text);
		
		if (StringUtils.hasText(text)) {
			ConceptService cs = Context.getConceptService();
			String[] conceptIds = text.split(" ");
			List<Integer> requestConceptIds = new Vector<Integer>();
			//set up parameter Set for easier add/delete functions
			// and removal of duplicates
			for (String id : conceptIds) {
				id = id.trim();
				if (!id.equals("") && !requestConceptIds.contains(Integer.valueOf(id))) //remove whitespace, blank lines, and duplicate entries
					requestConceptIds.add(Integer.valueOf(id));
			}
			
			// used when adding in concept sets
			List<Integer> originalConceptSetIds = new ArrayList<Integer>(originalConceptSets.size());
			
			// remove all sets that aren't in the request (aka, that have been deleted by the user)
			Collection<ConceptSet> copyOfOriginalConceptSets = new ArrayList<ConceptSet>(originalConceptSets);
			for (ConceptSet origConceptSet : copyOfOriginalConceptSets) {
				if (!requestConceptIds.contains(origConceptSet.getConcept().getConceptId()))
					originalConceptSets.remove(origConceptSet);
				
				// add to quick list used when adding later
				originalConceptSetIds.add(origConceptSet.getConcept().getConceptId());
			}
			
			// insert all sets that are new (aka, that have been added by the user).
			// Also normalize all weight attributes
			for (int x = 0; x < requestConceptIds.size(); x++) {
				Integer requestConceptId = requestConceptIds.get(x);
				
				// if this isn't in the originalList, add it
				
				if (!originalConceptSetIds.contains(requestConceptId)) {
					// the null weight will be reset in the next step of normalization
					originalConceptSets.add(new ConceptSet(cs.getConcept(requestConceptId), new Double(x)));
				}
				else {
					// find this conceptId in the original set and set its weight
					for (ConceptSet conceptSet : originalConceptSets) {
						if (conceptSet.getConcept().getConceptId().equals(requestConceptId)) {
							conceptSet.setSortWeight(new Double(x));
						}
					}
				}
			}
			
		} else {
			originalConceptSets.clear();
		}
		
		setValue(originalConceptSets);
	}
	
}
