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
package org.openmrs.api.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * Encounter-related database functions
 */
public interface EncounterDAO {
	
	/**
	 * Saves an encounter
	 * 
	 * @param encounter to be saved
	 * @throws DAOException
	 */
	public Encounter saveEncounter(Encounter encounter) throws DAOException;
	
	/**
	 * Purge an encounter from database.
	 * 
	 * @param encounter encounter object to be purged
	 */
	public void deleteEncounter(Encounter encounter) throws DAOException;
	
	/**
	 * Get encounter by internal identifier
	 * 
	 * @param encounterId encounter id
	 * @return encounter with given internal identifier
	 * @throws DAOException
	 */
	public Encounter getEncounter(Integer encounterId) throws DAOException;
	
	/**
	 * @param patientId
	 * @return all encounters for the given patient identifer
	 * @throws DAOException
	 */
	public List<Encounter> getEncountersByPatientId(Integer patientId) throws DAOException;
	
	/**
	 * @see org.openmrs.api.EncounterService#getEncounters(org.openmrs.Patient,
	 *      org.openmrs.Location, java.util.Date, java.util.Date, java.util.Collection,
	 *      java.util.Collection, java.util.Collection, boolean)
	 */
	public List<Encounter> getEncounters(Patient patient, Location location, Date fromDate, Date toDate,
	                                     Collection<Form> enteredViaForms, Collection<EncounterType> encounterTypes,
	                                     Collection<User> providers, boolean includeVoided);
	
	/**
	 * Save an Encounter Type
	 * 
	 * @param encounterType
	 */
	public EncounterType saveEncounterType(EncounterType encounterType);
	
	/**
	 * Purge encounter type from database.
	 * 
	 * @param encounterType
	 * @throws DAOException
	 */
	public void deleteEncounterType(EncounterType encounterType) throws DAOException;
	
	/**
	 * Get encounterType by internal identifier
	 * 
	 * @param encounterTypeId Internal Integer identifier for an EncounterType
	 * @return EncounterType with given internal identifier
	 * @throws DAOException
	 */
	public EncounterType getEncounterType(Integer encounterTypeId) throws DAOException;
	
	/**
	 * Get encounterType by name
	 * 
	 * @param name String representation of an encounterType
	 * @return EncounterType
	 * @throws DAOException
	 */
	public EncounterType getEncounterType(String name) throws DAOException;
	
	/**
	 * Get all encounter types
	 * 
	 * @return encounter types list
	 * @throws DAOException
	 */
	public List<EncounterType> getAllEncounterTypes(Boolean includeVoided) throws DAOException;
	
	/**
	 * Find Encounter Types matching the given name. Search string is case insensitive, so that
	 * "NaMe".equals("name") is true.
	 * 
	 * @param name
	 * @return all EncounterTypes that match
	 * @throws DAOException
	 */
	public List<EncounterType> findEncounterTypes(String name) throws DAOException;
	
	/**
	 * Gets the value of encounterDatetime currently saved in the database for the given encounter,
	 * bypassing any caches. This is used prior to saving an encounter so that we can change the obs
	 * if need be
	 * 
	 * @param encounter the Encounter go the the encounterDatetime of
	 * @return the encounterDatetime currently in the database for this encounter
	 * @should get saved encounter datetime from database
	 */
	public Date getSavedEncounterDatetime(Encounter encounter);
	
	/**
	 * Auto generated method comment
	 * 
	 * @param uuid
	 * @return
	 */
	public Encounter getEncounterByUuid(String uuid);
	
	/**
	 * Auto generated method comment
	 * 
	 * @param uuid
	 * @return
	 */
	public EncounterType getEncounterTypeByUuid(String uuid);
	
}
