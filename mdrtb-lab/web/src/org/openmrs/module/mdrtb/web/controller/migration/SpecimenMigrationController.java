package org.openmrs.module.mdrtb.web.controller.migration;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.openmrs.module.mdrtb.status.HivStatus;
import org.openmrs.module.mdrtb.status.HivStatusCalculator;
import org.openmrs.module.mdrtb.status.LabResultsStatus;
import org.openmrs.module.mdrtb.status.LabResultsStatusCalculator;
import org.openmrs.module.mdrtb.status.NullHivStatusRenderer;
import org.openmrs.module.mdrtb.status.NullLabResultsStatusRenderer;
import org.openmrs.module.mdrtb.status.NullTreatmentStatusRenderer;
import org.openmrs.module.mdrtb.status.TreatmentStatus;
import org.openmrs.module.mdrtb.status.TreatmentStatusCalculator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenMigrationController {
	
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
	
	@RequestMapping("/module/mdrtb/migration/migration.form")
	public ModelAndView migrateSpecimenData() {
			
		 /**
			initialize();
			
			// create all the new concepts/mappings/etc we need to add for the migration
			// TODO: NOTE THAT WE WILL WANT TO MAKE SURE WE KEEP CONCEPTS IN SYNC BETWEEN SERVERS/DICTIONARYS
			addAndUpdateConcepts();
			
			// convert smears on Haiti forms
			convertSmearsOnIntakeAndFollowup();
			
			// migrate any existing Specimen Collection Encounters in the system
			// note: this needs to happen first
			migrateResultatsDeCrachetEncounters();
			
			// migrate any existing BAC and DST encounters in the system
			migrateBacAndDstEncounters();
			
			// clean up old concepts
			cleanUpConcepts();
			
			*/
			
		return new ModelAndView("/module/mdrtb/migration/migration");
	}
		
	
	@RequestMapping("/module/mdrtb/migration/addAndUpdateConcepts.form")
	public ModelAndView addAndUpdateConcepts() {
		
		// add concept mappings
		addConceptMapping("DRUG SENSITIVITY TEST CONTAMINATED","DST CONTAMINATED");
		addConceptMapping("WAITING FOR TEST RESULTS","WAITING FOR TEST RESULTS");
		addConceptMapping("LAB TEST REPORT, SCANNED","SCANNED LAB REPORT");
		addConceptMapping("APPEARANCE OF SPUTUM SPECIMEN","APPEARANCE OF SPECIMEN");
		addConceptMapping("SPUTUM","SPUTUM");
		addConceptMapping("STRONGLY POSITIVE","STRONGLY POSITIVE");
		addConceptMapping("MODERATELY POSITIVE", "MODERATELY POSITIVE");
		addConceptMapping("WEAKLY POSITIVE", "WEAKLY POSITIVE");
		addConceptMapping("POSITIVE", "POSITIVE");
		addConceptMapping("NEGATIVE", "NEGATIVE");
		addConceptMapping("CONTAMINATED", "CONTAMINATED");
		addConceptMapping("ANTIRETROVIRAL DRUGS", "ANTIRETROVIRAL DRUGS");
		
		
		// TODO: probably should add all the drug concepts that I mapped manually here...
		
		// add new concepts
		// NOTE: need to add french names as well!
		addConcept("TUBERCULOSIS SPECIMEN ID", "Specimen", "Text", "TUBERCULOSIS SPECIMEN ID", "org.openmrs.module.mdrtb");
		addConcept("TUBERCULOSIS TEST DATE ORDERED", "Question", "Date", "TUBERCULOSIS TEST DATE ORDERED", "org.openmrs.module.mdrtb"); // worry about there being two "Date" concept datatypes?
		addConcept("TUBERCULOSIS SPECIMEN COMMENTS", "Finding", "Text", "TUBERCULOSIS SPECIMEN COMMENTS", "org.openmrs.module.mdrtb");
		
		addConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT", "Misc", "N/A", "PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT", "org.openmrs.module.mdrtb"); 
		addConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT", "Test", "Coded", "PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT", "org.openmrs.module.mdrtb"); 
		
		// modify concepts
		
		// haiti-specific?
		// PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT
		Concept physicanSmearConstruct = Context.getConceptService().getConceptByName("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
		physicanSmearConstruct.setSet(true);
		physicanSmearConstruct.getConceptSets().add(new ConceptSet(Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT"), new Double(1)));
		Context.getConceptService().saveConcept(physicanSmearConstruct);
		
		// haiti-specific?
		// PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT
		Concept physicanSmearResult = Context.getConceptService().getConceptByName("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
		addAnswerConcept(physicanSmearResult, Context.getConceptService().getConceptByName("POSITIVE"));
		addAnswerConcept(physicanSmearResult, Context.getConceptService().getConceptByName("NEGATIVE"));
		
		// TUBERCULOSIS SMEAR MICROSOPY CONSTRUCT
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		smearConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_DATE_ORDERED), new Double(smearConstruct.getConceptSets().size())));
		smearConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TEST_START_DATE), new Double(smearConstruct.getConceptSets().size())));
		Context.getConceptService().saveConcept(smearConstruct);
		
		// TUBERCULOSIS CULTURE CONSTRUCT
		Concept cultureConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_CONSTRUCT);
		cultureConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM), new Double(cultureConstruct.getConceptSets().size())));
		cultureConstruct.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TYPE_OF_ORGANISM_NON_CODED), new Double(cultureConstruct.getConceptSets().size())));
		Context.getConceptService().saveConcept(cultureConstruct);
		
		// TUBERCULOSIS DRUG SENSITIVITY TEST RESULTS
		Concept dstResults = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_RESULT);
		dstResults.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_CONTAMINATED), new Double(dstResults.getConceptSets().size())));
		dstResults.getConceptSets().add(new ConceptSet(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS), new Double(dstResults.getConceptSets().size())));
		Context.getConceptService().saveConcept(dstResults);
		
		// TUBERCULOSIS CULTURE RESULT
		Concept cultureResult  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
		addAnswerConcept(cultureResult, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		addAnswerConcept(cultureResult, Context.getConceptService().getConceptByName("POSITIVE"));
		
		// TUBERCULOSIS DRUG SENSITIVITY TEST METHOD
		Concept testMethod  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DST_METHOD);
		addAnswerConcept(testMethod, Context.getConceptService().getConceptByName("OGAWA"));
		addAnswerConcept(testMethod, Context.getConceptService().getConceptByName("LOWENSTEIN-JENSEN"));
		
		// TUBERCULOSIS SMEAR RESULT
		Concept smearResult  = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		addAnswerConcept(smearResult, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		
		// THIOACETAZONE
		Concept thio = Context.getConceptService().getConceptByName("THIOACETAZONE");
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG),thio);
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG), thio);
		addAnswerConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG), thio);
		
		// Removed duplicate "referred by" mapping	
		Concept referred = Context.getConceptService().getConcept(3512);
		for(ConceptMap map : referred.getConceptMappings()) {
			if(map.getSourceCode().equals("REFERRED BY")) {
				referred.removeConceptMapping(map);
			}
		}
		
		Context.getConceptService().saveConcept(referred);
		
		// Removed duplicate "pulmonary" mapping	
		Concept pulmonary = Context.getConceptService().getConcept(831);
		for(ConceptMap map : pulmonary.getConceptMappings()) {
			if(map.getSourceCode().equals("PULMONARY TUBERCULOSIS")) {
				pulmonary.removeConceptMapping(map);
			}
		}
		Context.getConceptService().saveConcept(pulmonary);
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	}

	// haiti-specific
	@RequestMapping("/module/mdrtb/migration/connvertSmearsOnIntakeAndFollowup.form")
	public ModelAndView convertSmearsOnIntakeAndFollowup() {
		// fetch the encounter types associated with the Intake and Follow-up forms
		List<EncounterType> intakeOrFollowup = new LinkedList<EncounterType>();
		intakeOrFollowup.add(Context.getEncounterService().getEncounterType("MDR-TB Intake"));
		intakeOrFollowup.add(Context.getEncounterService().getEncounterType("MDR-TB Follow Up"));
		
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
		Concept physicanSmearConstruct = Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
		Concept physicanSmearResult = Context.getConceptService().getConcept("PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
		
		
		// loop through all these encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, intakeOrFollowup, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating Intake or Follow Up encounter " + encounter.getEncounterId());
				
				// cycle through all encounters and change any smear constructs or smear results to "physician-reported" smear constructs or results
				for(Obs obs : encounter.getAllObs(false)) {
					if(smearResult.equals(obs.getConcept())) {
						log.info("Changing obs " + obs.getId() + " to type PHYSICAN-REPORTED TUBERCULOSIS SMEAR RESULT");
						obs.setConcept(physicanSmearResult);
						Context.getEncounterService().saveEncounter(encounter);
					}
					else if(smearConstruct.equals(obs.getConcept())) {
						log.info("Changing obs " + obs.getId() + " to type PHYSICAN-REPORTED TUBERCULOSIS SMEAR MICROSCOPY CONSTRUCT");
						obs.setConcept(physicanSmearConstruct);
						Context.getEncounterService().saveEncounter(encounter);
					}
				}
			}
		}
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	}
	
	// Note: this is a PIH-specific use case
	@RequestMapping("/module/mdrtb/migration/migrateResultatsDeCrachetEncounters.form")
	public ModelAndView migrateResultatsDeCrachetEncounters() {
					
		// first we need to create a new dummy form that we will use to keep track of these encounters so that we can redisplay them in 
		// a similar format in the new system
		Form dummyMSPPForm = new Form();
		dummyMSPPForm.setEncounterType(Context.getEncounterService().getEncounterType("Specimen Collection"));
		dummyMSPPForm.setName("Resultats de Crachat");
		dummyMSPPForm.setPublished(true);
		dummyMSPPForm.setVersion("1.0");
		Context.getFormService().saveForm(dummyMSPPForm);
		
		// get the form id and also save it in a global property
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("pihhaiti.dummyMSPPFormId", dummyMSPPForm.getId().toString()));
		
		// also, we need to modify the existing mdrtb forms list to reference this new form
		GlobalProperty forms = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.mdrtb_forms_list");
		forms.setPropertyValue("MDR-TB Rendezvous:html|MDR-TB Donnees de Base:html");
		Context.getAdministrationService().saveGlobalProperty(forms);
		
		// fetch the specimen collection encounter type
		List<EncounterType> specimenEncounter = new LinkedList<EncounterType>();
		specimenEncounter.add(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type")));
		
		// loop thru all the specimen collection encounters
		for(Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null, null, specimenEncounter, null, false)) {
			// to handle any test patients where the encounter hasn't been voided for some reason
			if (encounter.getPatient().isVoided()) {
				log.info("Voiding encounter " + encounter.getId() + " because it belongs to a voided patient");
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
			else {
				log.info("Migrating resultats de crachet encounter " + encounter.getEncounterId());
			
				// pull all the smear results, and the appearance of the sputum specimen, out of the existing encounter
				Concept smearResultType = Context.getConceptService().getConceptByName("AFB SPUTUM SMEAR"); // NOTE: this is a PIH-specific concept
				Concept appearanceOfSpecimenType = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE); 
			
				List<Obs> smearResults = new LinkedList<Obs>();
				Obs appearanceOfSpecimen = null;
			  
				for(Obs obs : encounter.getAllObs()) {
					if(obs.getConcept().equals(smearResultType)) {
						smearResults.add(obs);
					}
					if(obs.getConcept().equals(appearanceOfSpecimenType)) {
						appearanceOfSpecimen = obs;
					}
				}
			
				// now create a new specimen for each result
				// (the assumption here is that each result is off a different specimen)
				for(Obs smearResult : smearResults) {		
					Specimen specimen = Context.getService(MdrtbService.class).createSpecimen(encounter.getPatient());
				
					// set the form id of the underlying encounter to the dummy MSPP form so that we can pull out this encounters
					((Encounter) specimen.getSpecimen()).setForm(dummyMSPPForm);
					
					// set the date created of the encounter underlying the specimen to the date created of the existing encounter
					// so that we can continue to group the specimens from this encounter together if need be
					((Encounter) specimen.getSpecimen()).setDateCreated(encounter.getDateCreated());
					
					
					// set the type to sputum
					specimen.setType(Context.getConceptService().getConceptByName("SPUTUM")); // NOTE: this is a PIH-specific concept
				
					// set the appearance if necessary
					if(appearanceOfSpecimen != null) {
						specimen.setAppearance(appearanceOfSpecimen.getValueCoded());
					}
				
					// set the date collected if it exists
					if(smearResult.getObsDatetime() != null) {
						specimen.setDateCollected(smearResult.getObsDatetime());
					}
					else {
						log.warn("No date collected recorded for encounter " + encounter.getId());
					}
				
					// set the provider
					specimen.setProvider(encounter.getProvider());
				
					// set the location
					specimen.setLocation(encounter.getLocation());
					
					// now create the smear for this specimen
					Smear smear = specimen.addSmear();
					
					// for these smears, the "lab" is just where the specimen was collected
					smear.setLab(encounter.getLocation());
					
					// now set the result
					smear.setResult(smearResult.getValueCoded());
					
					// set the result date based on the date of the original encounter
					smear.setResultDate(encounter.getEncounterDatetime());
					
					// then save the specimen
					Context.getService(MdrtbService.class).saveSpecimen(specimen);
					
					log.info("Added new specimen " + specimen.getId());
				}
			
				// void the existing encounter
				Context.getEncounterService().voidEncounter(encounter, "voided as part of mdr-tb migration");
			}
		}
		
		// now retire the old crachet form
		Form toRetire = Context.getFormService().getForm("Resultats de Crachat (MSPP)");
		Context.getFormService().retireForm(toRetire, "retired as part of mdr-tb migration");
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	
	}
	
	@RequestMapping("/module/mdrtb/migration/migrateBacAndDstEncounters.form")
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
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	}
	
	@RequestMapping("/module/mdrtb/migration/cleanUpConcepts.form")
	public ModelAndView cleanUpConcepts() {
		// remove AFB smear and appearance of specimen from smear construct
		Concept smearConstruct = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_CONSTRUCT);
		Collection<ConceptSet> sets = smearConstruct.getConceptSets();
		
		Iterator<ConceptSet> i = sets.iterator();
		
		while(i.hasNext()) {
			ConceptSet set = i.next();
			if(set.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE))
					|| set.getConcept().equals(Context.getConceptService().getConceptByName("AFB SPUTUM SMEAR"))) {
					i.remove();
			}
		}
		
		Context.getConceptService().saveConcept(smearConstruct);
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	}
	
    @RequestMapping("/module/mdrtb/migration/voidEncounters.form")
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
		
		
		 return new ModelAndView("/module/mdrtb/migration/migration");
	}
    
    @RequestMapping("/module/mdrtb/migration/addWorkflows.form")
    public ModelAndView addWorkflows() {
    
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    	
    	ProgramWorkflow previousDrug = new ProgramWorkflow();
    	previousDrug.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
    	previousDrug.setProgram(mdrtbProgram);
    	
    	ProgramWorkflowState newPatient = new ProgramWorkflowState();
    	newPatient.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW));
    	newPatient.setInitial(true);
    	newPatient.setTerminal(false);
    	previousDrug.addState(newPatient);
    	
    	ProgramWorkflowState previousFirstLine = new ProgramWorkflowState();
    	previousFirstLine.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY));
    	previousFirstLine.setInitial(true);
    	previousFirstLine.setTerminal(false);
    	previousDrug.addState(previousFirstLine);
    	
      	ProgramWorkflowState previousSecondLine = new ProgramWorkflowState();
      	previousSecondLine.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS));
      	previousSecondLine.setInitial(true);
      	previousSecondLine.setTerminal(false);
    	previousDrug.addState(previousSecondLine);
    	
    	mdrtbProgram.addWorkflow(previousDrug);
    	
    	ProgramWorkflow previousTreatment = new ProgramWorkflow();
    	previousTreatment.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX));
    	previousTreatment.setProgram(mdrtbProgram);
    	
    	ProgramWorkflowState newPatientTreatment = new ProgramWorkflowState();
    	newPatientTreatment.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW));
    	newPatientTreatment.setInitial(true);
    	newPatientTreatment.setTerminal(false);
    	previousTreatment.addState(newPatientTreatment);
    	
    	ProgramWorkflowState relapse = new ProgramWorkflowState();
    	relapse.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE));
    	relapse.setInitial(true);
    	relapse.setTerminal(false);
    	previousTreatment.addState(relapse);
    	
      	ProgramWorkflowState afterDefault = new ProgramWorkflowState();
      	afterDefault.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED));
      	afterDefault.setInitial(true);
      	afterDefault.setTerminal(false);
    	previousTreatment.addState(afterDefault);
    	
     	ProgramWorkflowState afterFailiureCat1 = new ProgramWorkflowState();
     	afterFailiureCat1.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_TREATMENT));
     	afterFailiureCat1.setInitial(true);
     	afterFailiureCat1.setTerminal(false);
    	previousTreatment.addState(afterFailiureCat1);
    	
    	ProgramWorkflowState afterFailiureCat2 = new ProgramWorkflowState();
     	afterFailiureCat2.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT));
     	afterFailiureCat2.setInitial(true);
     	afterFailiureCat2.setTerminal(false);
    	previousTreatment.addState(afterFailiureCat2);
    	
    	ProgramWorkflowState transfer = new ProgramWorkflowState();
    	transfer.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TRANSFER));
    	transfer.setInitial(true);
    	transfer.setTerminal(false);
    	previousTreatment.addState(transfer);
    	
    	ProgramWorkflowState other = new ProgramWorkflowState();
    	other.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER));
    	other.setInitial(true);
    	other.setTerminal(false);
    	previousTreatment.addState(other);
    	
    	mdrtbProgram.addWorkflow(previousTreatment);
    	
    	Context.getProgramWorkflowService().saveProgram(mdrtbProgram);
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/addHospitalizationWorkflow.form")
    public ModelAndView addHospitalizationWorkflow() {
    
    	// create the hospitalizaton workflow construct
    	Concept hospitalizationWorkflowConcept = new Concept();
    	hospitalizationWorkflowConcept.addName(new ConceptName("HOSPITALIZATION WORKFLOW", Context.getLocale()));
    	hospitalizationWorkflowConcept.setConceptClass(Context.getConceptService().getConceptClassByName("Workflow"));
    	hospitalizationWorkflowConcept.setDatatype(Context.getConceptService().getConceptDatatypeByName("N/A"));
    	Context.getConceptService().saveConcept(hospitalizationWorkflowConcept);
    	
    	// add the appropriate concept mappings
    	addConceptMapping("HOSPITALIZATION WORKFLOW", "HOSPITALIZATION WORKFLOW");
    	addConceptMapping("AMBULATORY","AMBULATORY");
    	
    	// need to add patient hospitalized manually since there are two in the dictionary!
    	Concept hospitalizedConcept = Context.getConceptService().getConcept(3389);
    	ConceptMap map = new ConceptMap();
		map.setSource(Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"));
		map.setSourceCode("HOSPITALIZED");
		hospitalizedConcept.addConceptMapping(map);
		Context.getConceptService().saveConcept(hospitalizedConcept);
    	
    	
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    	
    	ProgramWorkflow hospitalizationWorkflow = new ProgramWorkflow();
    	hospitalizationWorkflow.setConcept(hospitalizationWorkflowConcept);
    	hospitalizationWorkflow.setProgram(mdrtbProgram);
    	
    	ProgramWorkflowState hospitalized = new ProgramWorkflowState();
    	hospitalized.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOSPITALIZED));
    	hospitalized.setInitial(true);
    	hospitalized.setTerminal(false);
    	hospitalizationWorkflow.addState(hospitalized);
    	
    	ProgramWorkflowState ambulatory = new ProgramWorkflowState();
    	ambulatory.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AMBULATORY));
    	ambulatory.setInitial(true);
    	ambulatory.setTerminal(false);
    	hospitalizationWorkflow.addState(ambulatory);
    	
    	mdrtbProgram.addWorkflow(hospitalizationWorkflow);
    	
    	Context.getProgramWorkflowService().saveProgram(mdrtbProgram);
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/migrateHospitalizations.form")
	public ModelAndView migrateHospitalizations() {

		Concept typeOfPatientConcept = Context.getConceptService().getConcept(3289);
		Concept hospitalizedConcept = Context.getConceptService().getConcept(3389);
		Concept ambulatoryConcept = Context.getConceptService().getConcept(1664);
		Concept hospitalizedSinceLastVisitConcept = Context.getConceptService().getConcept(1715);
		
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
		
		
		return new ModelAndView("/module/mdrtb/migration/migration");
	}
    
    @RequestMapping("/module/mdrtb/migration/addPulmonary.form")
    public ModelAndView createPulmonary() {
    	Concept site = Context.getConceptService().getConcept(1452);
    	
    	for (ConceptAnswer answer : site.getAnswers()) {
    		site.removeAnswer(answer);
    	}
    	
    	site.addAnswer(new ConceptAnswer(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)));
    	site.addAnswer(new ConceptAnswer(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB)));
    	
    	Context.getConceptService().saveConcept(site);
    	
    	addConceptMapping("SITE OF TB DISEASE", "ANATOMICAL SITE OF TUBERCULOSIS");
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/addDaysToPositivity.form")
    public ModelAndView addDaysToPositivity() {
    	addConcept("TUBERCULOSIS CULTURE DAYS TO POSITIVITY", "Question", "Numeric", "DAYS TO POSITIVITY", "org.openmrs.module.mdrtb");
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/migrateLocations.form")
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
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/migrateRegistrationGroups.form")
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
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/retireOldWorkflows.form")
    public ModelAndView retireOldWorkflows() {
    	
    	// retire the patient status and culture status workflows
    	Concept cultureStatus = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_STATUS);
    	Concept patientStatus = Context.getConceptService().getConceptByName("MULTI-DRUG RESISTANT TUBERCULOSIS PATIENT STATUS");
    	
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName("MDR-TB PROGRAM");
   
    	for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
    		if (workflow.getConcept().equals(cultureStatus) || workflow.getConcept().equals(patientStatus)) {
    			// retire the workflow
    			mdrtbProgram.retireWorkflow(workflow);
    			// also retire all states associated with that workflow
    			for (ProgramWorkflowState state : workflow.getStates()) {
    				workflow.retireState(state);
    			}
    		}
    	}
     	Context.getProgramWorkflowService().saveProgram(mdrtbProgram);
 
     	// now void all culture status patient states
     	for (Patient patient : Context.getPatientService().getAllPatients(true)) {
     		for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
     			for (PatientState state : program.getPatientProgram().getStates()) {
     				if (state.getState().getProgramWorkflow().getConcept().equals(cultureStatus)) {
     					state.setVoided(true);
     					state.setVoidReason("voided as part of mdr-tb migration");
     					state.setVoidedBy(Context.getUserContext().getAuthenticatedUser());
     				}
     			}
     			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
     		}
     	}
     	
     	return new ModelAndView("/module/mdrtb/migration/migration");
	}
    
    @RequestMapping("/module/mdrtb/migration/retireStillOnTreatmentState.form")
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
     	
     	// now retire the "still on treatment" state
     	Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
     
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName("MDR-TB PROGRAM");
   
    	for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
    		if (workflow.getConcept().equals(outcome)) {
    			for (ProgramWorkflowState state : workflow.getStates()) {
    				if (state.getConcept().equals(stillOnTreatment)) {
    					workflow.retireState(state);
    				}
    			}
    		}
    	}
     	Context.getProgramWorkflowService().saveProgram(mdrtbProgram);
     	
     	
     	return new ModelAndView("/module/mdrtb/migration/migration");
    	
    }
    
    @RequestMapping("/module/mdrtb/migration/setEmptyProgramLocationsToUnknown.form")
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
    	
     	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    /**
     * We should do this manually instead
     
    @RequestMapping("/module/mdrtb/migration/removeWaitingForTestResults.form")
    public ModelAndView removeWaitingForTestResults() {
    	// remove Waiting for Test results as optons for Smear and Culture Results
    	Concept smearResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.SMEAR_RESULT);
    	Concept cultureResult = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CULTURE_RESULT);
    	Concept waitingForTestResults = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS);
    	
    	for (ConceptAnswer answer : smearResult.getAnswers()) {
    		if (answer.getAnswerConcept().equals(waitingForTestResults)) {
    			smearResult.removeAnswer(answer);
    			break;
    		}
    	}
    	Context.getConceptService().saveConcept(smearResult);
    	
    	for (ConceptAnswer answer : cultureResult.getAnswers()) {
    		if (answer.getAnswerConcept().equals(waitingForTestResults)) {
    			cultureResult.removeAnswer(answer);
    			break;
    		}
    	}
    	Context.getConceptService().saveConcept(cultureResult);
    	
    	// now void any result obs that are set to WAITING FOR TEST RESULTS
    	Concept [] concepts = {smearResult, cultureResult};
    	Concept [] answers = {waitingForTestResults};
    	
    	List<Obs> waitingObs = Context.getObsService().getObservations(null, null, Arrays.asList(concepts), Arrays.asList(answers), null, null, null, null, null, null, null, false);
    	for (Obs obs : waitingObs) {
    		Context.getObsService().voidObs(obs, "voided as part of mdr-tb migration");
    	}
    	
      	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    */
    
    @RequestMapping("/module/mdrtb/migration/setupDrugOrderConceptMappings.form")
    public ModelAndView createDrugOrderConceptMappings() {
    	
    	addConceptMapping("REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED", "REASON TUBERCULOSIS TREATMENT CHANGED OR STOPPED");
    	addConceptMapping("REASON ANTIRETROVIRALS CHANGED OR STOPPED","REASON ANTIRETROVIRALS CHANGED OR STOPPED");
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/createQuinolonesConceptMappings.form")
    public ModelAndView createQuinolonesMappings() {
    	
    	addConceptMapping("TUBERCULOSIS QUINOLONES", "QUINOLONES");
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/resetOrdersWithDiscontinuedDateInFuture.form")
    public ModelAndView setFutureDiscontinuedDateToNull() {
    	
    	// this migration is PIH Haiti specific--fixes a bug that occurred in the previous migration
    	
    	Date today = new Date();
    	 	
    	for (Order order : Context.getOrderService().getOrders()) {
    		
    		if (order != null && order.getDiscontinuedDate() != null && order.getDiscontinued() && order.getDiscontinuedDate().after(today)) {
    			log.warn("Resetting discontinued state on order " + order.getId() + " to null; discontinued date erronously in the future (" + order.getDiscontinuedDate() + ")");
    		
    			order.setDiscontinued(false);
    			order.setDiscontinuedDate(null);
    			order.setDiscontinuedBy(null);
    			order.setDiscontinuedReason(null);
    			
    			Context.getOrderService().saveOrder(order);
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/changeStateShortNames.form")
    public ModelAndView changeStateShortNames() {
    	addConceptShortName("CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO RESULT OF PREVIOUS TREATMENT", "Registration Group - Previous Treatment");
    	addConceptShortName("CATEGORY 4 TUBERCULOSIS CLASSIFICATION ACCORDING TO PREVIOUS DRUG USE", "Registration Group - Previous Drug Use");
       	addConceptShortName("MULTI-DRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME", "Outcome");
       	addConceptShortName("HOSPITALIZATION WORKFLOW", "Hospitalization");
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/addZLIdentifiers.form")
    public ModelAndView addZLIdentifiers() {
    	// add a ZL identifier for each non-voided patient that doesn't have one
    	
    	PatientIdentifierType zlIdentifier = Context.getPatientService().getPatientIdentifierTypeByName("ZL EMR ID");
    	
    	Location fixedLocation = null;
		String fixedLocationName = Context.getAdministrationService().getGlobalProperty("mdrtb.fixedIdentifierLocation");
		if (StringUtils.isNotBlank(fixedLocationName)) {
			fixedLocation = Context.getLocationService().getLocation(fixedLocationName);
			if (fixedLocation == null) {
				throw new MdrtbAPIException("Location referenced by mdrtb.fixedIdentifierLocation global prop does not exist.");
			}
		}
    	
    	
    	for (Patient patient : Context.getPatientService().getAllPatients()) {
    		Boolean hasZLId = false;
    		
    		for (PatientIdentifier identifier : patient.getActiveIdentifiers() ) {
    			if (identifier.getIdentifierType().equals(zlIdentifier)) {
    				hasZLId = true;
    			}
    		}
    		
    		if (!hasZLId) {
    			PatientIdentifier identifier = new PatientIdentifier(MdrtbUtil.assignIdentifier(zlIdentifier), zlIdentifier, fixedLocation);
				identifier.setPreferred(true);
				patient.addIdentifier(identifier);
				Context.getPatientService().savePatient(patient);
				log.info("Assigned ZL EMR ID " + identifier.getIdentifier() + " to patient " + patient.getId());
    		}
    		
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }

    @RequestMapping("/module/mdrtb/migration/closeOpenProgramsWithOutcomes.form")
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
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    
    @RequestMapping("/module/mdrtb/migration/migrateOutcomeStates.form")
    public ModelAndView migrateOutcomeStates() {
    	// add a new concept mapping
    	addConceptMapping("PATIENT CURED","CURED");
    	addConceptMapping("PATIENT DIED","DIED");
    	addConceptMapping("REGIMEN FAILURE","FAILED");
    	addConceptMapping("PATIENT DEFAULTED", "DEFAULTED");
    	
    	Program mdrtb = Context.getService(MdrtbService.class).getMdrtbProgram();
    	Concept outcome = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME);
    	
    	// fetch the outcome workflow
    	ProgramWorkflow outcomeWorkflow = null;
    	for (ProgramWorkflow workflow : mdrtb.getWorkflows()) {
    		if (workflow.getConcept().equals(outcome)) {
    			outcomeWorkflow = workflow;
    			break;
    		}
    	}
    	
    	// now change the concepts associated with the states
    	ProgramWorkflowState cured = outcomeWorkflow.getState(Context.getConceptService().getConcept(1585));
    	cured.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CURED));
    	
    	ProgramWorkflowState died = outcomeWorkflow.getState(Context.getConceptService().getConcept(1565));
    	died.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED));
    	
    	ProgramWorkflowState failed = outcomeWorkflow.getState(Context.getConceptService().getConcept(1587));
    	failed.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FAILED));
    	
    	ProgramWorkflowState defaulted = outcomeWorkflow.getState(Context.getConceptService().getConcept(1567));
    	defaulted.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED));
    	
    	Context.getProgramWorkflowService().saveProgram(mdrtb);
    	
    	// retire old concepts
    	//  TODO: IMPORTANT: are the concept ids the same on all systems?
    	retireConcept(1564);  // TREATMENT COMPLETED - TB CATEGORY IV
    	retireConcept(1585);  // CURED - TB
    	retireConcept(1563);  // CURED - TB CATEGORY IV
    	retireConcept(1565);  // DIED - TB 
    	retireConcept(1587);  // FAILED - TB
    	retireConcept(1566);  // FAILED - TB CATEGORY IV
    	retireConcept(1567);  // DEFAULTED - TB
    	retireConcept(1584);  // MULTIDRUG RESISTANT TUBERCULOSIS TREATMENT OUTCOME
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/addUnknownMapping.form")
    public ModelAndView addUnknownMapping() {
    	addConceptMapping("UNKNOWN","UNKNOWN");
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/pihaiti/migrate/markPatientsAsDeceased.form")
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
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/migrateRegistrationGroupStates.form")
    public ModelAndView migrateRegistrationGroupStates() {
    	// create the new concepts for NEW and TRANSFER
    	addConcept("NEW", "Misc", "N/A", "NEW", "org.openmrs.module.mdrtb");
    	addConcept("TRANSFER", "Misc", "N/A", "TRANSFER", "org.openmrs.module.mdrtb");
    	
    	// add other new concept mapping
    	addConceptMapping("RELAPSE","RELAPSE");
    	addConceptMapping("OTHER NON-CODED","OTHER");
    	
    	Program mdrtb = Context.getService(MdrtbService.class).getMdrtbProgram();
    	Concept prevDrugUse = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
    	Concept prevTreatment = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
    	
    	// fetch the workflows
    	ProgramWorkflow prevDrugUseWorkflow = null;
    	ProgramWorkflow prevTreatmentWorkflow = null;
    	for (ProgramWorkflow workflow : mdrtb.getWorkflows()) {
    		if (workflow.getConcept().equals(prevDrugUse)) {
    			prevDrugUseWorkflow = workflow;
    		}
    		if (workflow.getConcept().equals(prevTreatment)) {
    			prevTreatmentWorkflow = workflow;
    		}
    	}
    	
    	// now change the concepts associated with the states
    	ProgramWorkflowState newPatient = prevDrugUseWorkflow.getState(Context.getConceptService().getConcept(1523));
    	newPatient.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW));
    	
    	ProgramWorkflowState newPatientTreatment = prevTreatmentWorkflow.getState(Context.getConceptService().getConcept(1523));
    	newPatientTreatment.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEW));
    	
    	ProgramWorkflowState transfer = prevTreatmentWorkflow.getState(Context.getConceptService().getConcept(1529));
    	transfer.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TRANSFER));
    
    	ProgramWorkflowState relapse = prevTreatmentWorkflow.getState(Context.getConceptService().getConcept(1524));
    	relapse.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RELAPSE));
    	
    	ProgramWorkflowState other = prevTreatmentWorkflow.getState(Context.getConceptService().getConcept(1530));
    	other.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER));
    	
    	ProgramWorkflowState defaulted = prevTreatmentWorkflow.getState(Context.getConceptService().getConcept(1525));
    	defaulted.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED));
    	
    	Context.getProgramWorkflowService().saveProgram(mdrtb);
    	
    	// retire old concepts
    	//  TODO: IMPORTANT: are the concept ids the same on all systems?
    	retireConcept(1523);  // NEW MDR-TB PATIENT
    	retireConcept(1529);  // TRANSFER IN MDR-TB PATIENT
    	retireConcept(1524);  // RELAPSE MDR-TB PATIENT
    	retireConcept(1530);  // OTHER MDR-TB PATIENT
    	retireConcept(1525);  // TREATMENT AFTER DEFAULT MDR-TB PATIENT
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/addCommentsMapping.form")
    public ModelAndView addCommentsMapping() {
    	addConceptMapping("CLINICIAN NOTES","CLINICIAN NOTES");
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    // TODO: add script to retire the forms
    
    @RequestMapping("/module/mdrtb/migration/removeOldGlobalProps.form")
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
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/closeHospitaliationsForDeadPatients.form")
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
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    @RequestMapping("/module/mdrtb/migration/voidPersonsAssociatedWithVoidedPatients.form")
    public ModelAndView voidPersonsAssociatedWithVoidedPatients() {
    	for (Patient patient : Context.getPatientService().getAllPatients(true)) {
    		if (patient.isVoided() && !patient.isPersonVoided()) {
    			patient.setPersonVoided(patient.getVoided());
    			patient.setPersonVoidReason(patient.getVoidReason());			
    			Context.getPatientService().savePatient(patient);
    			log.info("Voiding person " + patient.getPersonId() + " because associated patient " + patient.getId()+ " has been voided");
    		}
    	}
    	
    	return new ModelAndView("/module/mdrtb/migration/migration");
    }
    
    // just a hacky test
    @RequestMapping("/module/mdrtb/pihhaiti/test/loadStatus.form")
    public ModelAndView loadStatus() {
    	List<LabResultsStatus> labResultsStatus = new LinkedList<LabResultsStatus>();
    	List<TreatmentStatus> treatmentStatus = new LinkedList<TreatmentStatus>();
    	List<HivStatus> hivStatus = new LinkedList<HivStatus>();
    	
    	LabResultsStatusCalculator labResultsCalc = new LabResultsStatusCalculator(new NullLabResultsStatusRenderer());
    	TreatmentStatusCalculator treatmentCalc = new TreatmentStatusCalculator(new NullTreatmentStatusRenderer());
    	HivStatusCalculator hivCalc = new HivStatusCalculator(new NullHivStatusRenderer());
    	
    	for (Patient patient : Context.getPatientService().getAllPatients()) {
    		hivStatus.add((HivStatus) hivCalc.calculate(patient));
    		
    		for (MdrtbPatientProgram program : Context.getService(MdrtbService.class).getMdrtbPatientPrograms(patient)) {
    			labResultsStatus.add((LabResultsStatus) labResultsCalc.calculate(program));
    			treatmentStatus.add((TreatmentStatus) (treatmentCalc.calculate(program)));
    		}
    	}
    	return new ModelAndView("/module/mdrtb/migration/migration");
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
		
	private void addConcept(String name, String clazz, String datatype, String map, String mapSource) {
		
		// can't use this because sometimes we may be adding concepts with the same/similiar name
	//if (Context.getConceptService().getConceptByName(name) != null) {
		//	log.warn("The concept with name " + name + " already exists.");
			//return;
		//}
		
		Concept concept = new Concept();
		ConceptName conceptName = new ConceptName();
		ConceptMap conceptMap = new ConceptMap();
		Set<ConceptNameTag> conceptNameTags = new HashSet<ConceptNameTag>();
		conceptNameTags.add(Context.getConceptService().getConceptNameTagByName("default"));
		conceptNameTags.add(Context.getConceptService().getConceptNameTagByName("preferred"));
		
		conceptName.setName(name);
		conceptName.setLocale(Locale.ENGLISH);
		conceptName.setTags(conceptNameTags);
		concept.addName(conceptName);
			
		concept.setConceptClass(Context.getConceptService().getConceptClassByName(clazz));
		concept.setDatatype(Context.getConceptService().getConceptDatatypeByName(datatype));
		
		conceptMap.setSourceCode(map);
		conceptMap.setSource(Context.getConceptService().getConceptSourceByName(mapSource));
		concept.addConceptMapping(conceptMap);
		Context.getConceptService().saveConcept(concept);
		
	}
	
	private void retireConcept(Integer id) {
		Concept retire = Context.getConceptService().getConcept(id);
		Context.getConceptService().retireConcept(retire, "retired as part of mdr-tb migration");
	}
	
	private void addConceptShortName(String conceptMapping, String shortName) {
		
		// TODO: test to make sure short name hasn't already been added
		
		Concept c = Context.getService(MdrtbService.class).getConcept(conceptMapping);
		ConceptName conceptName = new ConceptName();
		conceptName.setName(shortName);
		conceptName.setLocale(Locale.ENGLISH);
		Set<ConceptNameTag> conceptNameTags = new HashSet<ConceptNameTag>();
		conceptNameTags.add(Context.getConceptService().getConceptNameTagByName("short"));
		conceptName.setTags(conceptNameTags);
		c.addName(conceptName);
		Context.getConceptService().saveConcept(c);
	}
	
	private void addConceptMapping(String name, String mapping) {
		
		// TODO: test to make sure mapping hasn't already been added
		
		Concept concept = Context.getConceptService().getConceptByName(name);
		ConceptMap map = new ConceptMap();
		map.setSource(Context.getConceptService().getConceptSourceByName("org.openmrs.module.mdrtb"));
		map.setSourceCode(mapping);
		concept.addConceptMapping(map);
		Context.getConceptService().saveConcept(concept);
	}
 	
	public void addAnswerConcept(Concept concept, Concept answerConcept) {
		ConceptAnswer answer = new ConceptAnswer();
		answer.setAnswerConcept(answerConcept);
		answer.setDateCreated(new Date());
		answer.setCreator(Context.getAuthenticatedUser());
		// TODO: is this the right way to generate a Uuid?
		answer.setUuid(UUID.randomUUID().toString()); 
		concept.addAnswer(answer);
		Context.getConceptService().saveConcept(concept);
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