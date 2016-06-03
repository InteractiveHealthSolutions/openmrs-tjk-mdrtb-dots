package org.openmrs.module.dotsreports.specimen.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.comparator.PersonByNameComparator;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.dotsreports.specimen.Culture;
import org.openmrs.module.dotsreports.specimen.Smear;
import org.openmrs.module.dotsreports.specimen.Specimen;


public class SpecimenReportingTools {

	protected static final Log log = LogFactory.getLog(SpecimenReportingTools.class);
	
	/**
	 * Returns all specimens collected within the start and end date parameters that have no smear, bacteriology, or dst results associated with them
	 */
	public static Map<Patient,List<Specimen>> getSpecimensWithNoResults(Date startDateCollected, Date endDateCollected) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// load parameters to use in the query
		Concept smearResult = Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT);
		Concept resistant = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT_TO_TB_DRUG);
		Concept intermediate = Context.getService(TbService.class).getConcept(TbConcepts.INTERMEDIATE_TO_TB_DRUG);
		Concept suceptible = Context.getService(TbService.class).getConcept(TbConcepts.SUSCEPTIBLE_TO_TB_DRUG);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// select all the encounters of the specimen collection encounter type which are not voided and
		// were collected between the start and end date, and are NOT in the set of encounters that have a smear result, culture result, or 
		// DST result that is not voided and not null

		String sql = "select patient_id, encounter_id from encounter where encounter_type='" + specimenEncounterType.getId() + "' and voided='0' and encounter.encounter_id " +
						(startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
						(endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
						"and encounter.encounter_id not in (select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
						"and obs.voided='0' and value_coded is not null and " +
						"(obs.concept_id='" + smearResult.getId() + "' or obs.concept_id='" + cultureResult.getId() + "' " +
						"or obs.concept_id='" + resistant.getId() + "' or obs.concept_id='" + intermediate.getId() + "' or obs.concept_id='" + suceptible.getId() + "') )";
	
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		return convertQueryResultsToResultsMap(results);
	}
		

	public static Map<Patient,List<Specimen>> getSpecimensWithPositiveCultureButNoDstResults (Date startDateCollected, Date endDateCollected, Integer daysSinceCulture, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// load parameters to use in the query
		Concept cultureResult = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT);
		Concept resistant = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT_TO_TB_DRUG);
		Concept intermediate = Context.getService(TbService.class).getConcept(TbConcepts.INTERMEDIATE_TO_TB_DRUG);
		Concept suceptible = Context.getService(TbService.class).getConcept(TbConcepts.SUSCEPTIBLE_TO_TB_DRUG);
		Concept waitingForTestResults = Context.getService(TbService.class).getConcept(TbConcepts.WAITING_FOR_TEST_RESULTS);
		
		String positiveResultConceptIds = convertIntegerArrayToString(TbUtil.getPositiveResultConceptIds());
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));

		// select all encounters of the specimen collection encounter type that are not voided and and were collected within the start date
		// and end date and have a positive culture result from the selected lab and are NOT in the set of encounters
		// that have a DST result that is not voided and not null
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " + 
					 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
					 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
					 "and value_coded in (" + positiveResultConceptIds +") and obs.concept_id='" + cultureResult.getId() + "' " +
					 (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
					 "and encounter.encounter_id not in " + 
					 "(select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " +
					 "and value_coded is not null and value_coded != '" + waitingForTestResults.getId() + "' " +
					 "and obs.concept_id in ('" + resistant.getId() + "','" + intermediate.getId() + "','" + suceptible.getId() + "'));";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		Calendar dateToCompare = Calendar.getInstance(Context.getLocale());
		dateToCompare.add(Calendar.DAY_OF_YEAR, -daysSinceCulture);
		
		// now take the result set and remove any that have a positive culture result within the past "daysSinceCulture" days
		triage:
			for (List<Object> result : results) {
				Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
				Specimen specimen = Context.getService(TbService.class).getSpecimen((Integer) result.get(1));
				
				// only add patients that aren't dead
				if (!patient.isDead()) {
					for (Culture culture : specimen.getCultures()) {
						if (culture.getResultDate() != null && culture.getResultDate().after(dateToCompare.getTime())) {
							continue triage;
						}
					}
					if (!resultMap.containsKey(patient)) {
						resultMap.put(patient, new ArrayList<Specimen>());
					}
					resultMap.get(patient).add(specimen);
				}	
			}
		
		return resultMap;
	}
	
	
	public static Map<Patient,List<Specimen>> getSpecimensWithPositiveSmearButNoCultureResults (Date startDateCollected, Date endDateCollected, Integer daysSinceSmear, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// create all the parameters to use in the query
		Concept smearResult = Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT);
	
		String positiveResultConceptIds = convertIntegerArrayToString(TbUtil.getPositiveResultConceptIds());
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// select all encounters of the specimen collection encounter type that are not voided and and were collected within the start date
		// and end date and have a positive smear result from the selected lab and are NOT in the set of encounters
		// that have a culture result that is not voided and not null
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " + 
					 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
					 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
					 "and value_coded in (" + positiveResultConceptIds +") and obs.concept_id='" + smearResult.getId() + "' " +
					 (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
					 "and encounter.encounter_id not in " + 
					 "(select distinct encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " +
					 "and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' and obs.voided='0' " +
					 "and obs.concept_id='" + cultureResult.getId() + "');";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		Calendar dateToCompare = Calendar.getInstance(Context.getLocale());
		dateToCompare.add(Calendar.DAY_OF_YEAR, -daysSinceSmear);
		
		// now take the result set and remove any that have a positive smear result within the past "daysSinceSmear" days
		triage:
			for (List<Object> result : results) {
				Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
				Specimen specimen = Context.getService(TbService.class).getSpecimen((Integer) result.get(1));
				
				// only add patients that aren't dead
				if (!patient.isDead()) {
					for (Smear smear : specimen.getSmears()) {
						if (smear.getResultDate() != null && smear.getResultDate().after(dateToCompare.getTime())) {
							continue triage;
						}
					}
					if (!resultMap.containsKey(patient)) {
						resultMap.put(patient, new ArrayList<Specimen>());
					}
					resultMap.get(patient).add(specimen);
				}
			}
		
		return resultMap;
	}
	
	
	/**
	 * Returns all specimens collected within the start and end date parameters that have a test marked as "contaminated"
	 */
	public static Map<Patient,List<Specimen>> getSpecimensWithContaminatedTests(Date startDateCollected, Date endDateCollected, Integer [] labIds) {
		
		DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Concept smearResult = Context.getService(TbService.class).getConcept(TbConcepts.SMEAR_RESULT);
		Concept cultureResult = Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_RESULT);
		Concept contaminated = Context.getService(TbService.class).getConcept(TbConcepts.CONTAMINATED);
		Concept unsatisfactory = Context.getService(TbService.class).getConcept(TbConcepts.UNSATISFACTORY_SAMPLE);
		Concept dstContaminated = Context.getService(TbService.class).getConcept(TbConcepts.DST_CONTAMINATED);
		
		String labIdString = convertIntegerArrayToString(labIds);
		
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		
		// select all the encounters of the specimen collection encounter type that were collected within the startDate and endDate and are
		// not voided and have smear, culture or DST result from the selected lab that is marked as contaminated
		
		String sql = "select distinct encounter.patient_id, encounter.encounter_id from encounter, obs where encounter.encounter_id = obs.encounter_id " + 
						"and encounter_type='" + specimenEncounterType.getId() + "' and encounter.voided='0' " + 
						 (startDateCollected != null ? "and encounter.encounter_datetime >= '" + sqlFormat.format(startDateCollected) + "' " : "") +
						 (endDateCollected != null ? "and encounter.encounter_datetime <= '" + sqlFormat.format(endDateCollected) + "' " : "") +
						"and obs.voided='0' " + (labIdString != null ? (" and obs.location_id in (" + labIdString + ") ") : "") + 
						"and ( (concept_id in (" + smearResult.getId() + "," + cultureResult.getId()+ ") and value_coded in (" + contaminated.getId() + "," + unsatisfactory.getId() + ")) " + 
						"or concept_id = '" + dstContaminated.getId() + "');";
		
		List<List<Object>> results = Context.getAdministrationService().executeSQL(sql, true);
		
		return convertQueryResultsToResultsMap(results);
	}
	
	
	/**
	 * Utility functions
	 */
	
	/**
	 * Takes a SQL query result and converts it into the format we wish to return... a map with patients as the key
	 * and the list of specimens associated with that patient as the value
	 */
	private static Map<Patient,List<Specimen>> convertQueryResultsToResultsMap(List<List<Object>> results) {
	
		Map<Patient,List<Specimen>> resultMap = new TreeMap<Patient,List<Specimen>>(new PersonByNameComparator());
		
		for (List<Object> result : results) {
			Patient patient = Context.getPatientService().getPatient((Integer) result.get(0));
			Specimen specimen = Context.getService(TbService.class).getSpecimen((Integer) result.get(1));
			
			// only add patient that aren't dead
			if (!patient.isDead()) {
				if (!resultMap.containsKey(patient)) {
					resultMap.put(patient, new ArrayList<Specimen>());
				}
				resultMap.get(patient).add(specimen);
			}
		}
		
		return resultMap;
	}
	
	
	/**
	 * Converts an Integer array to String
	 */
	private static String convertIntegerArrayToString(Integer [] integers) {
		StringBuffer result = new StringBuffer(); 
	
		for (Integer i : integers) {
			result.append(i.toString() + ",");
		}
		result.deleteCharAt(result.length() - 1);  // delete the trailing comma
		
		return result.toString();
	}
}
