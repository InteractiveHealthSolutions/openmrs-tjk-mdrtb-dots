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
package org.openmrs.api.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.MimeType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.aop.RequiredDataAdvice;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ObsDAO;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

/**
 * Default implementation of the Observation Service
 * 
 * @see org.openmrs.api.ObsService
 */
public class ObsServiceImpl extends BaseOpenmrsService implements ObsService {
	
	/**
	 * The data access object for the obs service
	 */
	protected ObsDAO dao;
	
	/**
	 * Report handlers that have been registered. This is filled via {@link #setHandlers(Map)} and
	 * spring's applicationContext-service.xml object
	 */
	private static Map<String, ComplexObsHandler> handlers = null;
	
	/**
	 * Default empty constructor for this obs service
	 */
	public ObsServiceImpl() {
	}
	
	/**
	 * @see org.openmrs.api.ObsService#setObsDAO(org.openmrs.api.db.ObsDAO)
	 */
	public void setObsDAO(ObsDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * Clean up after this class. Set the static var to null so that the classloader can reclaim the
	 * space.
	 * 
	 * @see org.openmrs.api.impl.BaseOpenmrsService#onShutdown()
	 */
	@Override
	public void onShutdown() {
		handlers = null;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#saveObs(org.openmrs.Obs, String)
	 */
	public Obs saveObs(Obs obs, String changeMessage) throws APIException {
		if (null != obs && null != obs.getConcept() && obs.getConcept().isComplex()
		        && null != obs.getComplexData().getData()) {
			// save or update complexData object on this obs
			// this is done before the database save so that the obs.valueComplex
			// can be filled in by the handler.
			ComplexObsHandler handler = getHandler(obs);
			if (null != handler) {
				handler.saveObs(obs);
			} else {
				throw new APIException("Unknown handler for " + obs.getConcept());
			}
		}
		
		if (obs.getObsId() == null) {
			Context.requirePrivilege(OpenmrsConstants.PRIV_ADD_OBS);
			return dao.saveObs(obs);
		} else {
			Context.requirePrivilege(OpenmrsConstants.PRIV_EDIT_OBS);
			
			if (changeMessage == null)
				throw new APIException("ChangeMessage is required when updating an obs in the database");
			
			// get a copy of the passed in obs and save it to the
			// database. This allows us to create a new row and new obs_id
			// this method doesn't copy the obs_id
			Obs newObs = Obs.newInstance(obs);
			
			// unset any voided properties on the new obs
			newObs.setVoided(false);
			newObs.setVoidReason(null);
			newObs.setDateVoided(null);
			newObs.setVoidedBy(null);
			// unset the creation stats
			newObs.setCreator(null);
			newObs.setDateCreated(null);
			
			RequiredDataAdvice.recursivelyHandle(SaveHandler.class, newObs, changeMessage);
			
			// save the new row to the database with the changes that
			// have been made to it
			dao.saveObs(newObs);
			
			// void out the original observation to keep it around for
			// historical purposes
			try {
				Context.addProxyPrivilege(OpenmrsConstants.PRIV_DELETE_OBS);
				String reason = changeMessage + " (new obsId: " + newObs.getObsId() + ")";
				
				// fetch a clean copy of this obs from the database so that
				// we don't write the changes to the database when we save
				// the fact that the obs is now voided
				Context.evictFromSession(obs);
				obs = getObs(obs.getObsId());
				
				// calling this via the service so that AOP hooks are called 
				Context.getObsService().voidObs(obs, reason);
				
			}
			finally {
				Context.removeProxyPrivilege(OpenmrsConstants.PRIV_DELETE_OBS);
			}
			
			return newObs;
		}
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObs(java.lang.Integer)
	 */
	public Obs getObs(Integer obsId) throws APIException {
		return dao.getObs(obsId);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#updateObs(org.openmrs.Obs)
	 * @deprecated
	 */
	@Deprecated
	public void updateObs(Obs obs) throws APIException {
		Context.getObsService().saveObs(obs, obs.getVoidReason());
	}
	
	/**
	 * Voids an Obs If the Obs argument is an obsGroup, all group members will be voided.
	 * 
	 * @see org.openmrs.api.ObsService#voidObs(org.openmrs.Obs, java.lang.String)
	 * @param obs the Obs to void
	 * @param reason the void reason
	 * @throws APIException
	 */
	public Obs voidObs(Obs obs, String reason) throws APIException {
		return dao.saveObs(obs);
	}
	
	/**
	 * Unvoids an Obs
	 * <p>
	 * If the Obs argument is an obsGroup, all group members with the same dateVoided will also be
	 * unvoided.
	 * 
	 * @see org.openmrs.api.ObsService#unvoidObs(org.openmrs.Obs)
	 * @param obs the Obs to unvoid
	 * @return the unvoided Obs
	 * @throws APIException
	 */
	public Obs unvoidObs(Obs obs) throws APIException {
		return dao.saveObs(obs);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#purgeObs(org.openmrs.Obs, boolean)
	 */
	public void purgeObs(Obs obs, boolean cascade) throws APIException {
		if (purgeComplexData(obs) == false) {
			throw new APIException("Unable to purge complex data for obs: " + obs);
		}
		
		if (cascade) {
			throw new APIException("Cascading purge of obs not yet implemented");
			// TODO delete any related objects here before deleting the obs
			// obsGroups objects?
			// orders?
		}
		
		dao.deleteObs(obs);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#purgeObs(org.openmrs.Obs)
	 */
	public void purgeObs(Obs obs) throws APIException {
		purgeObs(obs, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getMimeTypes()
	 * @deprecated use {@link #getAllMimeTypes()}
	 */
	@Deprecated
	public List<MimeType> getMimeTypes() throws APIException {
		return getAllMimeTypes();
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getAllMimeTypes()
	 * @deprecated
	 */
	@Deprecated
	public List<MimeType> getAllMimeTypes() throws APIException {
		return dao.getAllMimeTypes(true);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getAllMimeTypes(boolean)
	 * @deprecated
	 */
	@Deprecated
	public List<MimeType> getAllMimeTypes(boolean includeRetired) {
		return dao.getAllMimeTypes(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#saveMimeType(org.openmrs.MimeType)
	 * @deprecated
	 */
	@Deprecated
	public MimeType saveMimeType(MimeType mimeType) throws APIException {
		return dao.saveMimeType(mimeType);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#voidMimeType(org.openmrs.MimeType, java.lang.String)
	 * @deprecated
	 */
	@Deprecated
	public MimeType voidMimeType(MimeType mimeType, String reason) throws APIException {
		// TODO implement voidMimeType
		throw new APIException("Not yet implemented");
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getMimeType(java.lang.Integer)
	 * @deprecated
	 */
	@Deprecated
	public MimeType getMimeType(Integer mimeTypeId) throws APIException {
		return dao.getMimeType(mimeTypeId);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#purgeMimeType(org.openmrs.MimeType)
	 * @deprecated
	 */
	@Deprecated
	public void purgeMimeType(MimeType mimeType) {
		dao.deleteMimeType(mimeType);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(java.util.List, java.util.List,
	 *      java.util.List, java.util.List, List, List, java.util.List, java.lang.Integer,
	 *      java.lang.Integer, java.util.Date, java.util.Date, boolean)
	 */
	public List<Obs> getObservations(List<Person> whom, List<Encounter> encounters, List<Concept> questions,
	                                 List<Concept> answers, List<PERSON_TYPE> personTypes, List<Location> locations,
	                                 List<String> sort, Integer mostRecentN, Integer obsGroupId, Date fromDate, Date toDate,
	                                 boolean includeVoidedObs) throws APIException {
		if (whom == null)
			whom = new Vector<Person>();
		
		if (encounters == null)
			encounters = new Vector<Encounter>();
		
		if (questions == null)
			questions = new Vector<Concept>();
		
		if (answers == null)
			answers = new Vector<Concept>();
		
		if (personTypes == null)
			personTypes = new Vector<PERSON_TYPE>();
		
		if (locations == null)
			locations = new Vector<Location>();
		
		if (sort == null)
			sort = new Vector<String>();
		if (sort.isEmpty())
			sort.add("obsDatetime");
		
		// default to returning all observations
		if (mostRecentN == null)
			mostRecentN = -1;
		
		return dao.getObservations(whom, encounters, questions, answers, personTypes, locations, sort, mostRecentN,
		    obsGroupId, fromDate, toDate, includeVoidedObs);
	}
	
	/**
	 * This implementation queries the obs table comparing the given <code>searchString</code> with
	 * the patient's identifier, encounterId, and obsId
	 * 
	 * @see org.openmrs.api.ObsService#getObservations(java.lang.String)
	 */
	public List<Obs> getObservations(String searchString) {
		
		// search on patient identifier
		PatientService ps = Context.getPatientService();
		List<Patient> patients = ps.getPatients(null, searchString, null, false);
		List<Person> persons = new Vector<Person>();
		persons.addAll(patients);
		
		// try to search on encounterId
		EncounterService es = Context.getEncounterService();
		List<Encounter> encounters = new Vector<Encounter>();
		try {
			Encounter e = es.getEncounter(Integer.valueOf(searchString));
			if (e != null)
				encounters.add(e);
		}
		catch (NumberFormatException e) {
			// pass
		}
		
		List<Obs> returnList = new Vector<Obs>();
		
		if (encounters.size() > 0 || persons.size() > 0)
			returnList = getObservations(persons, encounters, null, null, null, null, null, null, null, null, null, false);
		
		// try to search on obsId
		try {
			Obs o = getObs(Integer.valueOf(searchString));
			if (o != null)
				returnList.add(o);
		}
		catch (NumberFormatException e) {
			// pass
		}
		
		return returnList;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#createObs(org.openmrs.Obs)
	 * @deprecated
	 */
	@Deprecated
	public void createObs(Obs obs) throws APIException {
		Context.getObsService().saveObs(obs, null);
	}
	
	/**
	 * Correct use case:
	 * 
	 * <pre>
	 * Obs parent = new Obs();
	 * Obs child1 = new Obs();
	 * Obs child2 = new Obs();
	 * 
	 * parent.addGroupMember(child1);
	 * parent.addGroupMember(child2);
	 * </pre>
	 * 
	 * @deprecated This method should no longer need to be called on the api. This was meant as
	 *             temporary until we created a true ObsGroup pojo.
	 * @see org.openmrs.api.ObsService#createObsGroup(org.openmrs.Obs[])
	 */
	@Deprecated
	public void createObsGroup(Obs[] obs) throws APIException {
		if (obs == null || obs.length < 1)
			return; // silently tolerate calls with missing/empty parameter
			
		String conceptIdStr = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_MEDICAL_RECORD_OBSERVATIONS, "1238");
		// fail silently if a default obs group is not defined
		if (conceptIdStr == null || conceptIdStr.length() == 0)
			return;
		
		Integer conceptId = Integer.valueOf(conceptIdStr);
		Concept defaultObsGroupConcept = Context.getConceptService().getConcept(conceptId);
		
		// if they defined a bad concept, bail
		if (defaultObsGroupConcept == null)
			throw new APIException("There is no concept defined with concept id: " + conceptIdStr
			        + "You should correctly define the default obs group concept id with the global propery"
			        + OpenmrsConstants.GLOBAL_PROPERTY_MEDICAL_RECORD_OBSERVATIONS);
		
		Obs obsGroup = new Obs();
		obsGroup.setConcept(defaultObsGroupConcept);
		
		for (Obs member : obs) {
			obsGroup.addGroupMember(member);
		}
		
		updateObs(obsGroup);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#deleteObs(org.openmrs.Obs)
	 * @deprecated use #purgeObs(Obs)
	 */
	@Deprecated
	public void deleteObs(Obs obs) throws APIException {
		Context.getObsService().purgeObs(obs);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservationsByPerson(org.openmrs.Person)
	 */
	public List<Obs> getObservationsByPerson(Person who) {
		List<Person> whom = new Vector<Person>();
		whom.add(who);
		return getObservations(whom, null, null, null, null, null, null, null, null, null, null, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(org.openmrs.Person, boolean includeVoided)
	 * @deprecated use {@link #getObservationsByPerson(Person)}
	 */
	@Deprecated
	public Set<Obs> getObservations(Person who, boolean includeVoided) {
		if (includeVoided == true)
			throw new APIException("Voided observations are no longer allowed to be queried");
		
		Set<Obs> obsSet = new HashSet<Obs>();
		obsSet.addAll(getObservationsByPerson(who));
		
		return obsSet;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(org.openmrs.Concept, org.openmrs.Location,
	 *      java.lang.String, java.lang.Integer, boolean includeVoided)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservations(Concept c, Location loc, String sort, Integer personType, boolean includeVoided) {
		List<Concept> questions = new Vector<Concept>();
		questions.add(c);
		List<Location> locations = new Vector<Location>();
		locations.add(loc);
		
		// make the sort list from the given sort string
		List<String> sortList = makeSortList(sort);
		
		return getObservations(null, null, questions, null, getPersonTypeEnumerations(personType), locations, sortList,
		    null, null, null, null, includeVoided);
	}
	
	/**
	 * Convenience method for turning a string like "location.locationId asc, obs.valueDatetime
	 * desc" into a list of strings to sort on
	 * 
	 * @param sort string
	 * @return simple list of strings to sort on without asc/desc
	 */
	private List<String> makeSortList(String sort) {
		List<String> sortList = new Vector<String>();
		if (sort != null && !"".equals(sort)) {
			for (String sortPart : sort.split(",")) {
				
				sortPart = sortPart.trim();
				
				// split out the asc/desc part if applicable
				if (sortPart.contains(" "))
					sortPart = sortPart.substring(0, sortPart.indexOf(" "));
				
				// add the current sort to the list of things to sort on
				if (!"".equals(sort))
					sortList.add(sortPart);
			}
		}
		
		return sortList;
	}
	
	/**
	 * This method should be removed when all methods using an Integer personType are removed. This
	 * method does a bitwise compare on <code>personType</code> and returns a list of PERSON_TYPEs
	 * that are comparable
	 * 
	 * @param personType Integer corresponding to {@link ObsService#PERSON}, {@link ObsService#USER}
	 *            , or {@link ObsService#PATIENT},
	 * @return the enumeration that corresponds to the given integer (old way of doing it)
	 */
	@SuppressWarnings("deprecation")
	private List<PERSON_TYPE> getPersonTypeEnumerations(Integer personType) {
		List<PERSON_TYPE> personTypes = new Vector<PERSON_TYPE>();
		if (personType == null) {
			personTypes.add(PERSON_TYPE.PERSON);
			return personTypes;
		} else if ((personType & ObsService.PATIENT) == ObsService.PATIENT) {
			personTypes.add(PERSON_TYPE.PATIENT);
			return personTypes;
		} else if ((personType & ObsService.USER) == ObsService.USER) {
			personTypes.add(PERSON_TYPE.USER);
			return personTypes;
		} else {
			// default to an all-encompassing search
			return personTypes;
		}
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(org.openmrs.Person, org.openmrs.Concept,
	 *      boolean)
	 * @deprecated
	 */
	@Deprecated
	public Set<Obs> getObservations(Person who, Concept question, boolean includeVoided) {
		List<Obs> obs = getObservationsByPersonAndConcept(who, question);
		Set<Obs> obsSet = new HashSet<Obs>();
		obsSet.addAll(obs);
		return obsSet;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservationsByPersonAndConcept(org.openmrs.Person, org.openmrs.Concept)
	 */
	public List<Obs> getObservationsByPersonAndConcept(Person who, Concept question) throws APIException {
		List<Person> whom = new Vector<Person>();
		if (who != null && who.getPersonId() != null)
			whom.add(who);
		List<Concept> questions = new Vector<Concept>();
		questions.add(question);
		
		return getObservations(whom, null, questions, null, null, null, null, null, null, null, null, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getLastNObservations(java.lang.Integer, org.openmrs.Person,
	 *      org.openmrs.Concept, boolean includeVoided)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getLastNObservations(Integer n, Person who, Concept question, boolean includeVoided) {
		List<Person> whom = new Vector<Person>();
		whom.add(who);
		List<Concept> questions = new Vector<Concept>();
		questions.add(question);
		
		return getObservations(whom, null, questions, null, null, null, null, n, null, null, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(org.openmrs.Concept, java.lang.String,
	 *      java.lang.Integer, boolean includeVoided)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservations(Concept question, String sort, Integer personType, boolean includeVoided) {
		List<Concept> questions = new Vector<Concept>();
		questions.add(question);
		
		// make the sort list from the given sort string
		List<String> sortList = makeSortList(sort);
		
		return getObservations(null, null, questions, null, getPersonTypeEnumerations(personType), null, sortList, null,
		    null, null, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservationsAnsweredByConcept(org.openmrs.Concept,
	 *      java.lang.Integer, boolean includeVoided)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservationsAnsweredByConcept(Concept answer, Integer personType, boolean includeVoided) {
		List<Concept> answers = new Vector<Concept>();
		answers.add(answer);
		
		return getObservations(null, null, null, answers, getPersonTypeEnumerations(personType), null, null, null, null,
		    null, null, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getNumericAnswersForConcept(org.openmrs.Concept,
	 *      java.lang.Boolean, java.lang.Integer, boolean includeVoided)
	 * @deprecated
	 */
	@Deprecated
	public List<Object[]> getNumericAnswersForConcept(Concept question, Boolean sortByValue, Integer personType,
	                                                  boolean includeVoided) {
		List<String> sortList = new Vector<String>();
		if (sortByValue) {
			sortList.add("valueNumeric");
		}
		
		List<Concept> questions = new Vector<Concept>();
		questions.add(question);
		
		List<Obs> obs = getObservations(null, null, questions, null, getPersonTypeEnumerations(personType), null, sortList,
		    null, null, null, null, includeVoided);
		
		List<Object[]> returnList = new Vector<Object[]>();
		
		for (Obs o : obs) {
			returnList.add(new Object[] { o.getObsId(), o.getObsDatetime(), o.getValueNumeric() });
		}
		
		return returnList;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(org.openmrs.Encounter)
	 * @deprecated use org.openmrs.Encounter#getObs()
	 */
	@Deprecated
	public Set<Obs> getObservations(Encounter whichEncounter) {
		return whichEncounter.getObs();
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getVoidedObservations()
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getVoidedObservations() {
		return getObservations(null, null, null, null, null, null, null, null, null, null, null, true);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#findObservations(java.lang.String, boolean,
	 *      java.lang.Integer)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> findObservations(String search, boolean includeVoided, Integer personType) {
		// ignoring voided and personTypes now
		return getObservations(search);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#findObsByGroupId(java.lang.Integer)
	 * @deprecated -- should use obs.getGroupMembers
	 */
	@Deprecated
	public List<Obs> findObsByGroupId(Integer obsGroupId) {
		return getObservations(null, null, null, null, null, null, null, null, obsGroupId, null, null, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObsByUuid(java.lang.String)
	 */
	public Obs getObsByUuid(String uuid) throws APIException {
		return dao.getObsByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(List, Date, Date, boolean)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservations(List<Concept> concepts, Date fromDate, Date toDate, boolean includeVoided) {
		return getObservations(null, null, concepts, null, null, null, null, null, null, fromDate, toDate, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(List, Date, Date)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservations(List<Concept> concepts, Date fromDate, Date toDate) {
		return getObservations(null, null, concepts, null, null, null, null, null, null, fromDate, toDate, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getObservations(Cohort, List, Date, Date)
	 * @deprecated
	 */
	@Deprecated
	public List<Obs> getObservations(Cohort patients, List<Concept> concepts, Date fromDate, Date toDate) {
		List<Person> persons = new Vector<Person>();
		
		if (patients != null)
			for (Integer memberId : patients.getMemberIds())
				persons.add(new Person(memberId));
		
		return getObservations(persons, null, concepts, null, null, null, null, null, null, fromDate, toDate, false);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getComplexObs(Integer, String)
	 */
	public Obs getComplexObs(Integer obsId, String view) throws APIException {
		Obs obs = dao.getObs(obsId);
		
		if (obs != null && obs.isComplex()) {
			return getHandler(obs).getObs(obs, view);
		}
		
		return obs;
	}
	
	/**
	 * Internal method to remove ComplexData when an Obs is purged.
	 */
	protected boolean purgeComplexData(Obs obs) throws APIException {
		if (obs.isComplex()) {
			ComplexObsHandler handler = getHandler(obs);
			if (null != handler) {
				return handler.purgeComplexData(obs);
			}
		}
		
		return true;
	}
	
	/**
	 * Convenience method to get the ComplexObsHandler associated with a complex Obs. Returns the
	 * ComplexObsHandler. Returns null if the Obs.isComplexObs() is false or there is an error
	 * instantiating the handler class.
	 * 
	 * @param obs A complex Obs.
	 * @return ComplexObsHandler for the complex Obs. or null on error.
	 */
	public ComplexObsHandler getHandler(Obs obs) throws APIException {
		if (obs.getConcept().isComplex()) {
			// Get the ConceptComplex from the ConceptService then return its
			// handler.
			if (obs.getConcept() == null)
				throw new APIException("Unable to get the handler for obs: " + obs + " because the concept is null");
			
			String handlerString = Context.getConceptService().getConceptComplex(obs.getConcept().getConceptId())
			        .getHandler();
			
			if (handlerString == null)
				throw new APIException("Unable to get the handler for obs: " + obs + " and concept: " + obs.getConcept()
				        + " because the handler is null");
			
			return this.getHandler(handlerString);
		}
		
		return null;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getHandler(java.lang.String)
	 */
	public ComplexObsHandler getHandler(String key) {
		return handlers.get(key);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#setHandlers(Map)
	 * @see #registerHandler(String, ComplexObsHandler)
	 */
	public void setHandlers(Map<String, ComplexObsHandler> newHandlers) throws APIException {
		for (Map.Entry<String, ComplexObsHandler> entry : newHandlers.entrySet()) {
			registerHandler(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * @see org.openmrs.api.ObsService#getHandlers()
	 */
	public Map<String, ComplexObsHandler> getHandlers() throws APIException {
		if (handlers == null)
			handlers = new LinkedHashMap<String, ComplexObsHandler>();
		
		return handlers;
	}
	
	/**
	 * @see org.openmrs.api.ObsService#registerHandler(String, ComplexObsHandler)
	 */
	public void registerHandler(String key, ComplexObsHandler handler) throws APIException {
		getHandlers().put(key, handler);
	}
	
	/**
	 * @see org.openmrs.api.ObsService#registerHandler(String, String)
	 */
	@SuppressWarnings("unchecked")
	public void registerHandler(String key, String handlerClass) throws APIException {
		try {
			Class loadedClass = OpenmrsClassLoader.getInstance().loadClass(handlerClass);
			registerHandler(key, (ComplexObsHandler) loadedClass.newInstance());
			
		}
		catch (Exception e) {
			throw new APIException("Unable to load and instantiate handler", e);
		}
	}
	
	/**
	 * @see org.openmrs.api.ObsService#removeHandler(java.lang.String)
	 */
	public void removeHandler(String key) {
		handlers.remove(key);
	}
	
}
