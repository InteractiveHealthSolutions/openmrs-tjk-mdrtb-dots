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
package org.openmrs.web.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.DuplicateIdentifierException;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InsufficientIdentifiersException;
import org.openmrs.api.InvalidCheckDigitException;
import org.openmrs.api.InvalidIdentifierFormatException;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;

/**
 * DWR patient methods. The methods in here are used in the webapp to get data from the database via
 * javascript calls.
 * 
 * @see PatientService
 */
public class DWRPatientService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Search on the <code>searchValue</code>. If a number is in the search string, do an identifier
	 * search. Else, do a name search
	 * 
	 * @see PatientService#getPatients(String)
	 * @param searchValue string to be looked for
	 * @param includeVoided true/false whether or not to included voided patients
	 * @return Collection<Object> of PatientListItem or String
	 * @should return only patient list items with nonnumeric search
	 * @should return string warning if invalid patient identifier
	 * @should not return string warning if searching with valid identifier
	 * @should include string in results if doing extra decapitated search
	 * @should not return duplicate patient list items if doing decapitated search
	 * @should not do decapitated search if numbers are in the search string
	 * @should get results for patients that have edited themselves
	 * @should logged in user should load their own patient object
	 */
	public Collection<Object> findPatients(String searchValue, boolean includeVoided) {
		
		// the list to return
		List<Object> patientList = new Vector<Object>();
		
		PatientService ps = Context.getPatientService();
		Collection<Patient> patients;
		
		try {
			patients = ps.getPatients(searchValue);
		}
		catch (APIAuthenticationException e) {
			patientList.add("Error while attempting to find patients - " + e.getMessage());
			return patientList;
		}
		
		patientList = new Vector<Object>(patients.size());
		for (Patient p : patients)
			patientList.add(new PatientListItem(p));
		
		// if only 2 results found and a number was not in the
		// search, then do a decapitated search: trim each word
		// down to the first three characters and search again
		if (patientList.size() < 3 && !searchValue.matches(".*\\d+.*")) {
			String[] names = searchValue.split(" ");
			String newSearch = "";
			for (String name : names) {
				if (name.length() > 3)
					name = name.substring(0, 4);
				newSearch += " " + name;
			}
			
			newSearch = newSearch.trim();
			Collection<Patient> newPatients = ps.getPatients(newSearch);
			newPatients.removeAll(patients); // remove the potential first two patients from these hits
			if (newPatients.size() > 0) {
				patientList.add("Minimal patients returned. Results for <b>" + newSearch + "</b>");
				for (Patient p : newPatients) {
					PatientListItem pi = new PatientListItem(p);
					patientList.add(pi);
				}
			}
		}
		//no results found and a number was in the search --
		//should check whether the check digit is correct.
		else if (patientList.size() == 0 && searchValue.matches(".*\\d+.*")) {
			
			//Looks through all the patient identifier validators to see if this type of identifier
			//is supported for any of them.  If it isn't, then no need to warn about a bad check
			//digit.  If it does match, then if any of the validators validates the check digit
			//successfully, then the user is notified that the identifier has been entered correctly.
			//Otherwise, the user is notified that the identifier was entered incorrectly.
			
			Collection<IdentifierValidator> pivs = ps.getAllIdentifierValidators();
			boolean shouldWarnUser = true;
			boolean validCheckDigit = false;
			boolean identifierMatchesValidationScheme = false;
			
			for (IdentifierValidator piv : pivs) {
				try {
					if (piv.isValid(searchValue)) {
						shouldWarnUser = false;
						validCheckDigit = true;
					}
					identifierMatchesValidationScheme = true;
				}
				catch (UnallowedIdentifierException e) {}
			}
			
			if (identifierMatchesValidationScheme) {
				if (shouldWarnUser)
					patientList
					        .add("<p style=\"color:red; font-size:big;\"><b>WARNING: Identifier has been typed incorrectly!  Please double check the identifier.</b></p>");
				else if (validCheckDigit)
					patientList
					        .add("<p style=\"color:green; font-size:big;\"><b>This identifier has been entered correctly, but still no patients have been found.</b></p>");
			}
		}
		
		return patientList;
	}
	
	/**
	 * Convenience method for dwr/javascript to convert a patient id into a Patient object (or at
	 * least into data about the patient)
	 * 
	 * @param patientId the {@link Patient#getPatientId()} to match on
	 * @return a truncated Patient object in the form of a PatientListItem
	 */
	public PatientListItem getPatient(Integer patientId) {
		PatientService ps = Context.getPatientService();
		Patient p = ps.getPatient(patientId);
		PatientListItem pli = new PatientListItem(p);
		if (p.getAddresses() != null && p.getAddresses().size() > 0) {
			PersonAddress pa = (PersonAddress) p.getAddresses().toArray()[0];
			pli.setAddress1(pa.getAddress1());
			pli.setAddress2(pa.getAddress2());
		}
		return pli;
	}
	
	/**
	 * find all patients with duplicate attributes (searchOn)
	 * 
	 * @param searchOn
	 * @return list of patientListItems
	 */
	public Vector<Object> findDuplicatePatients(String[] searchOn) {
		Vector<Object> patientList = new Vector<Object>();
		
		try {
			List<String> options = new Vector<String>(searchOn.length);
			for (String s : searchOn)
				options.add(s);
			
			List<Patient> patients = Context.getPatientService().getDuplicatePatientsByAttributes(options);
			
			if (patients.size() > 200)
				patients.subList(0, 200);
			
			for (Patient p : patients)
				patientList.add(new PatientListItem(p));
		}
		catch (Exception e) {
			log.error(e);
			patientList.add("Error while attempting to find duplicate patients - " + e.getMessage());
		}
		
		return patientList;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param patientId
	 * @param identifierType
	 * @param identifier
	 * @param identifierLocationId
	 * @return
	 */
	public String addIdentifier(Integer patientId, String identifierType, String identifier, Integer identifierLocationId) {
		
		String ret = "";
		
		if (identifier == null || identifier.length() == 0)
			return "PatientIdentifier.error.general";
		PatientService ps = Context.getPatientService();
		LocationService ls = Context.getLocationService();
		Patient p = ps.getPatient(patientId);
		PatientIdentifierType idType = ps.getPatientIdentifierTypeByName(identifierType);
		//ps.updatePatientIdentifier(pi);
		Location location = ls.getLocation(identifierLocationId);
		log.debug("idType=" + identifierType + "->" + idType + " , location=" + identifierLocationId + "->" + location
		        + " identifier=" + identifier);
		PatientIdentifier id = new PatientIdentifier();
		id.setIdentifierType(idType);
		id.setIdentifier(identifier);
		id.setLocation(location);
		
		// in case we are editing, check to see if there is already an ID of this type and location
		for (PatientIdentifier previousId : p.getActiveIdentifiers()) {
			if (previousId.getIdentifierType().equals(idType) && previousId.getLocation().equals(location)) {
				log.debug("Found equivalent ID: [" + idType + "][" + location + "][" + previousId.getIdentifier()
				        + "], about to remove");
				p.removeIdentifier(previousId);
			} else {
				if (!previousId.getIdentifierType().equals(idType))
					log.debug("Previous ID id type does not match: [" + previousId.getIdentifierType().getName() + "]["
					        + previousId.getIdentifier() + "]");
				if (!previousId.getLocation().equals(location)) {
					log.debug("Previous ID location is: " + previousId.getLocation());
					log.debug("New location is: " + location);
				}
			}
		}
		
		p.addIdentifier(id);
		
		try {
			ps.savePatient(p);
		}
		catch (InvalidIdentifierFormatException iife) {
			log.error(iife);
			ret = "PatientIdentifier.error.formatInvalid";
		}
		catch (InvalidCheckDigitException icde) {
			log.error(icde);
			ret = "PatientIdentifier.error.checkDigit";
		}
		catch (IdentifierNotUniqueException inue) {
			log.error(inue);
			ret = "PatientIdentifier.error.notUnique";
		}
		catch (DuplicateIdentifierException die) {
			log.error(die);
			ret = "PatientIdentifier.error.duplicate";
		}
		catch (InsufficientIdentifiersException iie) {
			log.error(iie);
			ret = "PatientIdentifier.error.insufficientIdentifiers";
		}
		catch (PatientIdentifierException pie) {
			log.error(pie);
			ret = "PatientIdentifier.error.general";
		}
		
		return ret;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param patientId
	 * @param reasonForExitId
	 * @param dateOfExit
	 * @param causeOfDeath
	 * @param otherReason
	 * @return
	 */
	public String exitPatientFromCare(Integer patientId, Integer exitReasonId, String exitDateStr,
	                                  Integer causeOfDeathConceptId, String otherReason) {
		log.debug("Entering exitfromcare with [" + patientId + "] [" + exitReasonId + "] [" + exitDateStr + "]");
		String ret = "";
		
		PatientService ps = Context.getPatientService();
		ConceptService cs = Context.getConceptService();
		
		Patient patient = null;
		try {
			patient = ps.getPatient(patientId);
		}
		catch (Exception e) {
			patient = null;
		}
		
		if (patient == null) {
			ret = "Unable to find valid patient with the supplied identification information - cannot exit patient from care";
		}
		
		// Get the exit reason concept (if possible)
		Concept exitReasonConcept = null;
		try {
			exitReasonConcept = cs.getConcept(exitReasonId);
		}
		catch (Exception e) {
			exitReasonConcept = null;
		}
		
		// Exit reason error handling
		if (exitReasonConcept == null) {
			ret = "Unable to locate reason for exit in dictionary - cannot exit patient from care";
		}
		
		// Parse the exit date 
		Date exitDate = null;
		if (exitDateStr != null) {
			SimpleDateFormat sdf = Context.getDateFormat();
			try {
				exitDate = sdf.parse(exitDateStr);
			}
			catch (ParseException e) {
				exitDate = null;
			}
		}
		
		// Exit date error handling
		if (exitDate == null) {
			ret = "Invalid date supplied - cannot exit patient from care without a valid date.";
		}
		
		// If all data is provided as expected
		if (patient != null && exitReasonConcept != null && exitDate != null) {
			
			// need to check first if this is death or not
			String patientDiedConceptId = Context.getAdministrationService().getGlobalProperty("concept.patientDied");
			
			Concept patientDiedConcept = null;
			if (patientDiedConceptId != null) {
				patientDiedConcept = cs.getConcept(patientDiedConceptId);
			}
			
			// If there is a concept for death in the dictionary
			if (patientDiedConcept != null) {
				
				// If the exist reason == patient died 
				if (exitReasonConcept.equals(patientDiedConcept)) {
					
					Concept causeOfDeathConcept = null;
					try {
						causeOfDeathConcept = cs.getConcept(causeOfDeathConceptId);
					}
					catch (Exception e) {
						causeOfDeathConcept = null;
					}
					
					// Cause of death concept exists
					if (causeOfDeathConcept != null) {
						try {
							ps.processDeath(patient, exitDate, causeOfDeathConcept, otherReason);
						}
						catch (Exception e) {
							log.debug("Caught error", e);
							ret = "Internal error while trying to process patient death - unable to proceed.";
						}
					}
					// cause of death concept does not exist
					else {
						ret = "Unable to locate cause of death in dictionary - cannot proceed";
					}
				}

				// Otherwise, we process this as an exit 
				else {
					try {
						ps.exitFromCare(patient, exitDate, exitReasonConcept);
					}
					catch (Exception e) {
						log.debug("Caught error", e);
						ret = "Internal error while trying to exit patient from care - unable to exit patient from care at this time.";
					}
				}
			}

			// If the system does not recognize death as a concept, then we exit from care
			else {
				try {
					ps.exitFromCare(patient, exitDate, exitReasonConcept);
				}
				catch (Exception e) {
					log.debug("Caught error", e);
					ret = "Internal error while trying to exit patient from care - unable to exit patient from care at this time.";
				}
			}
			log.debug("Exited from care, it seems");
		}
		
		return ret;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param patientId
	 * @param locationId
	 * @return
	 */
	public String changeHealthCenter(Integer patientId, Integer locationId) {
		log.warn("Deprecated method in 'DWRPatientService.changeHealthCenter'");
		
		String ret = "";
		
		/*
		
		if ( patientId != null && locationId != null ) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			Location location = Context.getEncounterService().getLocation(locationId);
			
			if ( patient != null && location != null ) {
				patient.setHealthCenter(location);
				Context.getPatientService().updatePatient(patient);
			}
		}
		*/

		return ret;
	}
}
