package org.openmrs.module.mdrtb.web.controller.migration;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenMigrationGenericController {
	
	/**
	 * IMPORTANT NOTE:
	 * 
	 * THESE MIGRATION SCRIPTS SHOULD BE RUN BY A USER WHO HAS HIS/HER DEFAULT PROFILE
	 * LANGUAGE SET TO ENGLISH, SINCE THE MODULE RELIES ON FETCHING CONCEPTS BY ENGLISH
	 * NAME IN ORDER TO WORK
	 */
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Set<Concept> testConstructConcepts;
	private Map<String,Specimen> specimenMap = new HashMap<String,Specimen>();

	@RequestMapping("/module/mdrtb/migration/generic/migrateBacAndDstEncounters.form")
	public ModelAndView migrateBacAndDstEncounters() {
		
		initialize();
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
	
		// loop thru all the bac and dst encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			// also void any encounters with no obs
			if (encounter.getPatient().isVoided()) {
				log.warn("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else if (encounter.getAllObs().size() == 0) {
				log.warn("Voiding encounter " + encounter.getId() + " because it has no non-voided obs");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating bac/dst results encounter " + encounter.getEncounterId());
			
				Boolean moveObsAndVoidEncounter = false;
				Specimen specimen = null;
			
				// first we need to figure out if we are going to need to create a new specimen for this encounter or not by checking if accession numbers
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getAccessionNumber() != null && specimenMap.get(obs.getAccessionNumber().toUpperCase()) != null) {
						specimen = specimenMap.get(obs.getAccessionNumber().toUpperCase());
						moveObsAndVoidEncounter = true; // since this specimen already exists, we need to move all the obs to the encounter associated with the existing specimen
						break;
					}
				}
			
				// if the specimen is still null at this point, we need to create a new once
				if(!moveObsAndVoidEncounter) {
					specimen = createSpecimenFromEncounter(encounter);
				}
				
				// now make sure all accession numbers within this encounter map to this specimen
				for(Obs obs : encounter.getAllObs()) {
					if(!StringUtils.isEmpty(obs.getAccessionNumber())) {
						if(specimenMap.get(obs.getAccessionNumber().toUpperCase()) != null && specimenMap.get(obs.getAccessionNumber().toUpperCase()) != specimen) {
							log.warn("Specimen " + specimen.getId() + " and specimen " + specimenMap.get(obs.getAccessionNumber().toUpperCase()).getId() + " may be the same. They share the same accession number.");
						}
						else {
							specimenMap.put(obs.getAccessionNumber().toUpperCase(),specimen);
						}
					}
				}
			
			
				// now we need to iterate through all the test obs to pull out specific information to update specimen data
				for(Obs obs : encounter.getObsAtTopLevel(false)) {
					// check to see if this is a test construct				
					if(testConstructConcepts.contains(obs.getConcept())) {
						Obs colonies = null;
					
						log.info("Processing test with id " + obs.getId());
						
						// need to create a place to add any child obs to add to this test, so that we don't throw a concurrent access exception
						// while iterating through the group members; currently this is only used by the organism non-coded case;
						List<Obs> obsToAddToTest = new LinkedList<Obs>();
						
						// iterate through all the obs in the test
						for(Obs childObs : obs.getGroupMembers()) {	
							
							// TODO: do I want to ignore voided obs... get Group Members doesn't do so automatically
							
							log.info("Processing test obs " + childObs.getId() + " of concept type " + childObs.getConcept());
							
							// check to see if this is a the sample source obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE))) {
								// copy and void the existing sample source
								compareAndSetSampleSource(specimen, childObs);
								childObs.setVoided(true);
								//Context.getObsService().voidObs(childObs, "voided as part of mdr-tb migration");
							}	
					
							// check to see if this is a smear or culture result obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT)) || childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT))) {
								// set the date collected based on the value datetime of this obs, and reset the value datetime of this obs
								if(childObs.getValueDatetime() != null) {
									compareAndSetDateCollected(specimen, childObs);
									childObs.setValueDatetime(null);
								}
								// set the accession number on construct to the accession number on the result obs
								if(!StringUtils.isEmpty(childObs.getAccessionNumber())) {
									log.info("Setting accession number on obs " + obs.getId() + " to " + childObs.getAccessionNumber());
									obs.setAccessionNumber(childObs.getAccessionNumber());
								}
							}
					
							// see if this a dst with a sputum collection date on it
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPUTUM_COLLECTION_DATE))) {
								if(childObs.getValueDatetime() != null) {
									compareAndSetDateCollected(specimen, childObs);
								}
								// for some reason, the accession number for DST tests is stored in this construct
								if(!StringUtils.isEmpty(childObs.getAccessionNumber())) {
									log.info("Setting accession number on obs " + obs.getId() + " to " + childObs.getAccessionNumber());
									obs.setAccessionNumber(childObs.getAccessionNumber());
								}
								// for some reason, comments are stored on the sputum collection date; we need to move them to the DST construct
								if(StringUtils.isNotEmpty(childObs.getComment())) {
									obs.setComment(childObs.getComment());
								}
								// now set the sputum collection date obs as voided
								childObs.setVoided(true);
							}
							
							// change all DST contaminated to the proper type
							// NOTE: PIH Haiti specific functionality??
							if(obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT)) && childObs.getConcept().equals(Context.getConceptService().getConceptByName("CONTAMINATED"))) {
								childObs.setConcept(Context.getConceptService().getConceptByName("DRUG SENSITIVITY TEST CONTAMINATED"));
								log.info("Changing concept on obs " + obs.getConcept().getId() + " from CONTAMINATED to DRUG SENSITIVITY TEST CONTAMINATED");
							}
							
							// check to see if this is a colonies obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COLONIES))) {
								// only use the most recent colonies obs (to handle bug where colonies was being stored multiple times)
								if(compareAndUpdateColonies(childObs,colonies)) {
									log.warn("Encounter " + encounter.getId() + " has multiple colonies obs with different values. Using obs with most recent datetime.");
								}
							}
							
							// check to see if this is a organism-type non-coded obs
							if(childObs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED))) {
								// see if we can convert this to a coded type
								if(StringUtils.isNotBlank(childObs.getValueText())) {
									if(childObs.getValueText().equalsIgnoreCase("M. TUBERCULOSIS COMPLEX")) {	
										// if so, convert this to a coded obs
										changeOrganismTypeToCoded(obs, childObs, Context.getConceptService().getConceptByName("M. TUBERCULOSIS COMPLEX"));
										log.info("Changed type of organism non-coded to coded M. TUBERCULOSIS COMPLEX for test " + obs.getId());
									}
									else if(childObs.getValueText().equalsIgnoreCase("M. ABSCESSUS")) {
										changeOrganismTypeToCoded(obs, childObs, Context.getConceptService().getConceptByName("M. ABSCESSUS"));
										log.info("Changed type of organism non-coded obs to coded M. ABSCESSUS for test " + obs.getId());
									}
								}
							}
						}
						
						// now add any new obs that we've created during the looping
						if(obsToAddToTest.size() > 0) {
							for(Obs obsToAdd : obsToAddToTest) {
								obs.addGroupMember(obsToAdd);
							}
						}
					}
							
				}
				if(moveObsAndVoidEncounter) {
					log.info("Moving obs on encounter " + encounter.getId() + " to specimen encounter " + specimen.getId());
					for(Obs obs : encounter.getAllObs()) {
						obs.setEncounter((Encounter) specimen.getSpecimen());
					}
					// Note: moving the voiding of encounters to another controller afterwards to solve issue with all the obs getting voided;
				}
			
				Context.getService(MdrtbService.class).saveSpecimen(specimen);
			}
		}
		
		return new ModelAndView("/module/mdrtb/migration/generic/migration");
	}
	
    @RequestMapping("/module/mdrtb/migration/generic/voidEncounters.form")
	public ModelAndView voidEncounters() {
	
		// this migration controller is meant to run AFTER the specimen migration controller, to void all the BAC and DST encounters
		// (all data in these encounters should have been been migrated to new specimen encounters by the specimen migration controller)
		// for some reason I can't get this encounter voiding to work unless it is in a separate controller
		
		// fetch the bac and dst encounter types
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_bacteriology")));
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.test_result_encounter_type_DST")));
	
		// now void all unused encounters
		// loop thru all the bac and dst encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			if (encounter.getAllObs().size() == 0) {
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
		 
		
		// retire Bacteriology and DST encounter-types
		Context.getEncounterService().retireEncounterType(Context.getEncounterService().getEncounterType("Bacteriology Result"), "retired as part of MDR-TB migration");
		Context.getEncounterService().retireEncounterType(Context.getEncounterService().getEncounterType("DST Result"), "retired as part of MDR-TB migration");
		
		
		 return new ModelAndView("/module/mdrtb/migration/generic/migration");
	}
    
    @RequestMapping("/module/mdrtb/migration/generic/migrateHospitalizations.form")
	public ModelAndView migrateHospitalizations() {

		Concept typeOfPatientConcept = Context.getConceptService().getConcept("TYPE OF PATIENT");
		Concept hospitalizedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOSPITALIZED);
		Concept ambulatoryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AMBULATORY);
		Concept hospitalizedSinceLastVisitConcept = Context.getConceptService().getConceptByName("PATIENT HOSPITALIZED SINCE LAST VISIT");
		
		Concept [] conceptParams = {typeOfPatientConcept, hospitalizedSinceLastVisitConcept};
		
		// loop through all patients
		for (Person patient : Context.getPatientService().getAllPatients(false)) {
			
			Person [] patientArray = {patient};				
			
			// get all the possible hospitalization obs from the database
			List<Obs> status = Context.getObsService().getObservations(Arrays.asList(patientArray), null, Arrays.asList(conceptParams), null, null, null, null, null, null, null, null, false);
	
			Collections.reverse(status);
			
			if (status.size() > 0) {
			
				// get all the programs for this patient
				List<MdrtbPatientProgram> programs = Context.getService(MdrtbService.class).getMdrtbPatientPrograms(Context.getPatientService().getPatient(patient.getId()));
				
				Boolean isHospitalized = false;
				Date hospitalizationDate = null;
				
				// now loop thru all the status obs
				for (Obs obs : status) {
						
					// see if this is ambulatory/hospitalized phase
					if ((obs.getConcept().equals(typeOfPatientConcept) && obs.getValueCoded().equals(hospitalizedConcept)) 
							|| (obs.getConcept().equals(hospitalizedSinceLastVisitConcept) && obs.getValueAsBoolean() == true)) {
					
						// set the patient as hospitalized if necessary
						if (!isHospitalized) {
							isHospitalized = true;
							hospitalizationDate = obs.getObsDatetime();
						}
						// if the patient is already hospitalized, we don't need to do anything in this case
	
					}
					// if the patient is ambulatory
					else if ((obs.getConcept().equals(typeOfPatientConcept) && obs.getValueCoded().equals(ambulatoryConcept))
							|| (obs.getConcept().equals(hospitalizedSinceLastVisitConcept) && obs.getValueAsBoolean() == false)) {
						
						if (isHospitalized) {								
							createHospitalization(programs, patient, hospitalizationDate, obs.getObsDatetime());
							
							// set the patient as not hospitalized, and set the hospitalization date back to null
							isHospitalized = false;
							hospitalizationDate = null;
						}	
					}	
				}
				
				// handle a current hospitalization
				if (isHospitalized) {
					createHospitalization(programs, patient, hospitalizationDate, null);
				}
			}
		}
		
		
		return new ModelAndView("/module/mdrtb/migration/generic/migration");
	}
    
    @RequestMapping("/module/mdrtb/migration/generic/migrateLocations.form")
    public ModelAndView migrateLocations() throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    	
    	PersonAttributeType healthCenterType = Context.getPersonService().getPersonAttributeTypeByName("Health Center");
    	
    	// loop thru all persons and see if we can identify a location for them
    	for (Patient patient : Context.getPatientService().getAllPatients()) {
    		Location location = null;
    		
    		// first see if this person has a healthcenter
    		PersonAttribute healthCenter = patient.getAttribute(healthCenterType);
    		
    		if (healthCenter != null) {
    			location = (Location) healthCenter.getHydratedObject();
    		}
    		// if no health center, see if all the persons encounters are at the same location
    		else {
    			List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
    			
    			if (encounters != null && encounters.size() > 0) {
    				Boolean singleLocation = false;
        			Location currentLocation = null;
        			
    				for (Encounter encounter : encounters) {
    					if (encounter.getLocation() != null) {
    						if (currentLocation != null && encounter.getLocation() != currentLocation) {
    							singleLocation = false;
    							break;
    						}
    						else {
    							singleLocation = true;
    							currentLocation = encounter.getLocation();
    						}
    					}
    				}
    				
    				if (singleLocation) {
    					location = currentLocation;
    				}
    			}
    		}
    		
    		// now, if we've determined a location for this patient, assign this location to all patient programs without a location
    		if (location != null) {
    			
    			// is there a screwy location?
    			if (location.getId() == null) {
    				log.error("location " + location.getDisplayString() + " does not have an ID");
    			}
    			else {
    				for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
    					if (program.getLocation() == null) {
    						program.setLocation(location); // yes, this seems pointless, but I'm doin it to avoid a hiberate error
    						Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
    						log.info("Set location for patient " + patient.getId() + " and patient program " + program.getId() + " to " + location.getDisplayString());
    					}
    				}
    			}
    		}
    	} 	
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/generic/migrateRegistrationGroups.form")
    public ModelAndView migrateRegistrationGroups() {
    	Concept previousDrugUse = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
    	Concept previousTreatment = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
    	
    	Collection<ProgramWorkflowState> previousDrugUseStates = Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousDrugUse();
    	Collection<ProgramWorkflowState> previousTreatmentStates = Context.getService(MdrtbService.class).getPossibleClassificationsAccordingToPreviousTreatment();
    	
    	// loop thru all persons and see if they have obs for either registration group options
    	for (Patient patient : Context.getPatientService().getAllPatients()) {
    		
    		// loop thru all the category 4 previous drug group registration group for this patient
    		for (Obs previousDrugUseObs : Context.getObsService().getObservationsByPersonAndConcept(patient, previousDrugUse)) {
    			
    			// find out the proper patient program (if any) to set this state on
    			Date previousProgramEndDate = null;
    			for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
    				if ( (program.getDateCompleted() == null || previousDrugUseObs.getObsDatetime().before(program.getDateCompleted()) &&
        				(previousProgramEndDate == null || previousDrugUseObs.getObsDatetime().after(previousProgramEndDate)))) {
    					
    					// set the appropriate state for this patient
    					for (ProgramWorkflowState state : previousDrugUseStates) {
    						if (state.getConcept().equals(previousDrugUseObs.getValueCoded())) {
    							program.setClassificationAccordingToPreviousDrugUse(state);
    	    					Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
    	    					log.info("Set previous workflow state previous drug use to " + state.toString() + " for program " + program.getPatientProgram().toString() + " of patient " + patient.toString());
    						}
    					}
        			}
    				previousProgramEndDate = program.getDateCompleted();
    			}
    			// now void this obs
    			Context.getObsService().voidObs(previousDrugUseObs, "voided as part of mdrtb-migration");
    		}
    		
    		// loop thru all the category 4 previous treatment  registration group for this patient
    		for (Obs previousTreatmentObs : Context.getObsService().getObservationsByPersonAndConcept(patient, previousTreatment)) {
    			
    			// find out the proper patient program (if any) to set this state on
    			Date previousProgramEndDate = null;
    			for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
    				if ( (program.getDateCompleted() == null || previousTreatmentObs.getObsDatetime().before(program.getDateCompleted()) &&
        				(previousProgramEndDate == null || previousTreatmentObs.getObsDatetime().after(previousProgramEndDate)))) {
    					
    					// set the appropriate state for this patient
    					for (ProgramWorkflowState state : previousTreatmentStates) {
    						if (state.getConcept().equals(previousTreatmentObs.getValueCoded())) {
    							program.setClassificationAccordingToPreviousTreatment(state);
    	    					Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
    	    					log.info("Set previous workflow state previous treatment to " + state.toString() + " for program " + program.getPatientProgram().toString() + " of patient " + patient.toString());
    						}
    					}
        			}
    				previousProgramEndDate = program.getDateCompleted();
    			}
    			// now void this obs
    			Context.getObsService().voidObs(previousTreatmentObs, "voided as part of mdrtb-migration");
    		}
    	}
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
     
    @RequestMapping("/module/mdrtb/migration/generic/retireStillOnTreatmentState.form")
    public ModelAndView retireStillOnTreatmentState() {

    	Concept stillOnTreatment = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.STILL_ON_TREATMENT);
    	
     	// we are getting rid of the "still on treatment" workflow state; for all patients with the "outcome" workflow set to this state,
     	// the workflow should be set to null
     	for (Patient patient : Context.getPatientService().getAllPatients(true)) {
     		for (MdrtbPatientProgram mdrtbProgram : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
     			if (mdrtbProgram.getOutcome() != null && mdrtbProgram.getOutcome().getConcept().equals(stillOnTreatment)) {
     				mdrtbProgram.setOutcome(null);
     				Context.getProgramWorkflowService().savePatientProgram(mdrtbProgram.getPatientProgram());
     			}
     		}
     	}
     	
     	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/generic/setEmptyProgramLocationsToUnknown.form")
    public ModelAndView setEmptyProgramLocationsToUnknown() {
    	Location unknown = Context.getLocationService().getLocation("Unknown Location");
    	Program mdrtb = Context.getService(MdrtbService.class).getMdrtbProgram();
    	
    	for (PatientProgram program : Context.getProgramWorkflowService().getPatientPrograms(null, mdrtb, null, null, null, null, false)) {
    		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
    		
    		if (mdrtbProgram.getLocation() == null) {
    			mdrtbProgram.setLocation(unknown);
    			Context.getProgramWorkflowService().savePatientProgram(mdrtbProgram.getPatientProgram());
    		}
    	}
    	
     	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
        
    @RequestMapping("/module/mdrtb/migration/generic/closeOpenProgramsWithOutcomes.form")
    public ModelAndView closeOpenProgramsWithOutcomes() {
    	
    	Program mdrtb = Context.getService(MdrtbService.class).getMdrtbProgram();
    	Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
    	
    	for (PatientProgram program : Context.getProgramWorkflowService().getPatientPrograms(null, mdrtb, null, null, null, null, false)) {
    		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
    		
    		if (mdrtbProgram.getOutcome() != null && mdrtbProgram.getOutcome().getConcept() != null && mdrtbProgram.getDateCompleted() == null) {
    			PatientState outcomeState = getPatientState(program, outcome);
    			mdrtbProgram.setDateCompleted(outcomeState.getStartDate());
    			Context.getProgramWorkflowService().savePatientProgram(mdrtbProgram.getPatientProgram());
    			log.info("Closed patient program " + program.getId() + " on " + mdrtbProgram.getDateCompleted() + " because program had previously assigned outcome of " + mdrtbProgram.getOutcome());
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
   
    @RequestMapping("/module/mdrtb/pihaiti/migrate/generic/markPatientsAsDeceased.form")
    public ModelAndView markPatientsAsDeceased() {
    	
    	Program mdrtb = Context.getService(MdrtbService.class).getMdrtbProgram();
    	ProgramWorkflowState died = MdrtbUtil.getProgramWorkflowState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED));
    	Concept unknown = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN);
    	
    	for (PatientProgram program : Context.getProgramWorkflowService().getPatientPrograms(null, mdrtb, null, null, null, null, false)) {
    		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
    		if (mdrtbProgram.getOutcome() != null && mdrtbProgram.getOutcome().equals(died) && !program.getPatient().isDead()) {
    			Patient patient = program.getPatient();
    			Context.getPatientService().processDeath(patient, program.getDateCompleted(), unknown, null);
    			log.info("Marking patient " + patient.getId() + " as deceased on " + program.getDateCompleted());
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    // TODO: add script to retire the forms
    
    @RequestMapping("/module/mdrtb/migration/generic/removeOldGlobalProps.form")
    public ModelAndView removeOldGlobalProps() {
    	String [] GlOBAL_PROPS_TO_REMOVE = {"mdrtb.location_list",
    										"mdrtb.lab_list",
    										"mdrtb.culture_method_concept",
    										"mdrtb.DST_methods",
    										"mdrtb.smear_method_concept",
    										"mdrtb.anatomical_locations_concept",
    										"mdrtb.DST_drug_list",
    										"mdrtb.DST_result_list",
    										"mdrtb.culture_result_list",
    										"mdrtb.smear_result_list",
    										"mdrtb.mdrtb_default_provider",
    										"mdrtb.first_line_drugs",
    										"mdrtb.injectible_drugs",
    										"mdrtb.quinolones",
    										"mdrtb.other_second_line",
    										"mdrtb.discontinue_drug_order_reasons",
    										"mdrtb.default_discontinue_drug_order_reason",
    										"mdrtb.max_num_bacteriologies_or_dsts_to_add_at_once",
    										"mdrtb.organism_type",
    										"mdrtb.positive_culture_concepts",
    										"mdrtb.ART_identifier_type",
    										"mdrtb.dst_color_coding_red",
    										"mdrtb.dst_color_coding_green",
    										"mdrtb.dst_color_coding_yellow",
    										"mdrtb.in_mdrtb_program_cohort_definition_id",
    										"mdrtb.conversion_definition_interval",
    										"mdrtb.conversion_definition_number",
    										"mdrtb.red_list",
    										"mdrtb.green_list",
    										"mdrtb.yellow_list",
    										"mdrtb.lab_test_order_type",
    										"mdrtb.listPatientsLocationMethod",
    										"mdrtb.unknownLocationName",
    										"mdrtb.show_lab",
    										"mdrtb.patient_dashboard_tab_conf",
    										"mdrtb.dstContradicatesDrugWarningColor",
    										"mdrtb.probableResistanceWarningColor",
    										"mdrtb.enableResistanceProbabilityWarning",
    										"mdrtb.enable_specimen_tracking",
    										"mdrtb.date_format_string",
    										"mdrtb.patientIdentifierLocationToPrefixList"}; // note that this last prop might still be used in Rwanda?
    	
    	for (String propertyName : GlOBAL_PROPS_TO_REMOVE) {
    		GlobalProperty prop = Context.getAdministrationService().getGlobalPropertyObject(propertyName);
    		if (prop == null) {
    			log.warn("No global property found with code " + propertyName);
    		}
    		else {
    			Context.getAdministrationService().purgeGlobalProperty(prop);
    			log.info("Removed global property " + propertyName);
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/generic/closeHospitaliationsForDeadPatients.form")
    public ModelAndView closeHospitalizationsForDeadPatients() {
    	
    	// loop thru all the patients
    	for (Patient patient : Context.getPatientService().getAllPatients(false)) {
    		// see if the patient is dead
    		if (patient.getDead()) {
    			// now get all the MDR-TB patient programs for this patient
    			for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
    				// see if there is an active hospitalization for this program
    				if (program.getCurrentlyHospitalized()) {
    					program.closeCurrentHospitalization(patient.getDeathDate());
    					Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
    					log.info("Closed hospitalization for deceased patient " + patient.getId() + " on " + patient.getDeathDate());
    				}
    			}
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/generic/voidPersonsAssociatedWithVoidedPatients.form")
    public ModelAndView voidPersonsAssociatedWithVoidedPatients() {
    	for (Patient patient : Context.getPatientService().getAllPatients(true)) {
    		if (patient.isVoided() && !patient.isPersonVoided()) {
    			patient.setPersonVoided(patient.getVoided());
    			patient.setPersonVoidReason(patient.getVoidReason());			
    			Context.getPatientService().savePatient(patient);
    			log.info("Voiding person " + patient.getPersonId() + " because associated patient " + patient.getId()+ " has been voided");
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/generic/migration");
    }
    
    
    private void initialize() {
		testConstructConcepts = new HashSet<Concept>();
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT));
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT));
		testConstructConcepts.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONSTRUCT));
	}
    
    private void createHospitalization(List<MdrtbPatientProgram> programs, Person patient, Date admissionDate, Date dischargeDate) {
    	
    	for(MdrtbPatientProgram program : programs) {
    		if (program.getDateCompleted() == null || admissionDate.before(program.getDateCompleted()) 
    				&& (dischargeDate == null || dischargeDate.after(program.getDateEnrolled()))) {
    			program.addHospitalization(admissionDate, dischargeDate);
    			log.info("Creating hospitalization for patient # " + patient.getId() +" from " + admissionDate + " to " + dischargeDate);
    			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
    			return;
    		}
    	}
    	
    	log.error("Unable to add hospitalization for patient # " + patient.getId() + " from " + admissionDate + " to " + dischargeDate);
    }
	
	private Specimen createSpecimenFromEncounter(Encounter encounter) {
		// change the encounter type to "specimen collection" encounter
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		
		// now instantiate a new specimen using this encounter
		Specimen specimen = new SpecimenImpl(encounter);
		
		// set the patient and provider of the specimen to the patient and provider of the underlying encounter
		// (not really needed, but just in case)
		specimen.setPatient(encounter.getPatient());
		specimen.setProvider(encounter.getProvider());
		specimen.setLocation(encounter.getLocation());
		
		// NOTE: PIH-HAITI specific functionality...
		// if the location on the initial encounter is a the MSLI, set the location to unknown (b/c specimen would never be COLLECTED at MSLI
		if(encounter.getLocation().getId() == 5) {
			specimen.setLocation(Context.getLocationService().getLocation(1));
		}
		
		return specimen;
	}
		
	private void compareAndSetSampleSource(Specimen specimen, Obs obs) {
		// nothing to do if no value for this obs
		if(obs.getValueCoded() == null) {
			return;
		}
		
		// fetch the obs on this specimen that holds the sample source
		Obs type = null;
		for(Obs obs2 : ((Encounter) specimen.getSpecimen()).getObsAtTopLevel(false)){
			if(obs2.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SAMPLE_SOURCE))) {
				type = obs;
				break;
			}
		}
		
		if(type == null) {
			specimen.setType(obs.getValueCoded());
			return;
		}
		if(type.getValueCoded() != obs.getValueCoded()) {
			log.warn("Mismatched sample type on specimen " + specimen.getId() + "; using sample source with most recent obs date");
		}
		
		if(type.getObsDatetime() == null || (obs.getObsDatetime() != null && type.getObsDatetime().before(obs.getObsDatetime())) ) {
			type = Obs.newInstance(obs);
		}
	}
	
	private void compareAndSetDateCollected(Specimen specimen, Obs obs) {
		// nothing to do if no value for this obs
		if(obs.getValueDatetime() == null) {
			return;
		}
		
		Date datetime = ((Encounter) specimen.getSpecimen()).getEncounterDatetime();
		
		if(datetime == null) {
			datetime = obs.getValueDatetime();
			return;
		}
		
		if(datetime.before(obs.getValueDatetime())) {
			log.warn("Mismatched collection date on specimen " + specimen.getId() + "; using oldest date");
			return;
		}
		
		if(datetime.after(obs.getValueDatetime())) {
			log.warn("Mismatched collection date on specimen " + specimen.getId() + "; using oldest date");
			datetime = obs.getValueDatetime();
			return;
		}
		
	}
		
	// to fix an existing bug where colony obs were being stored in multiple
	private Boolean compareAndUpdateColonies(Obs source, Obs target) {
		Boolean returnValue = null;
		
		if(source == null) {
			return false;
		}
		
		if(target == null) {
			target = source;
			return false;
		}
		
		if(source.getValueNumeric() != target.getValueNumeric()) {
			returnValue = true;
		}
				
		if(target.getObsDatetime() == null || (source.getObsDatetime() != null && source.getObsDatetime().before(target.getObsDatetime()))) {
			log.info("Voiding colony observation " + target.getId());
			target.setVoided(true);
			//Context.getObsService().voidObs(target, "voided as part of mdr-tb migration");
			target = source;  // is this kosher?
		}
		else {
			log.info("Voiding colony observation " + target.getId());
			source.setVoided(true);
			//Context.getObsService().voidObs(source, "voided as part of mdr-tb migration");
		}
		
		return returnValue;
		
	}
	
	private void changeOrganismTypeToCoded(Obs test, Obs nonCoded, Concept type) {
		Boolean foundCoded = false;
		
		// cycle through all the obs on this test, find the *coded* type of organism and set it to the proper value
		for(Obs obs : test.getGroupMembers()) {
			if(obs.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM))) {
				obs.setValueCoded(type);
				foundCoded = true;
				break;
			}
		}
		
		// if we have found the coded obs, go ahead and voided the non-coded one
		if(foundCoded) {
			nonCoded.setVoided(true);
		}
		else {
			// we should never reach this case, but if for some reason there isn't a coded obs on this test, change the non-coded obs into a coded one
			nonCoded.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM));
			nonCoded.setValueCoded(type);
			nonCoded.setValueText(null);
		}
	}
	
	/**
	 * Gets the state for a workflow 
	 * 
	 * Note that this method operates under the assumption that there is only one non-voided
	 * state per workflow at any one time.  For a generic workflow, this would not be a valid
	 * assumption, but for the Classification and Outcome workflows we are working with, this should be true.
	 */
	private PatientState getPatientState (PatientProgram program, Concept workflowConcept) {
		for (PatientState state : program.getStates()) {
			if (state.getState().getProgramWorkflow().getConcept().equals(workflowConcept) && !state.getVoided()) {
				return state;
			}
		}
		
		return null;
	}
}