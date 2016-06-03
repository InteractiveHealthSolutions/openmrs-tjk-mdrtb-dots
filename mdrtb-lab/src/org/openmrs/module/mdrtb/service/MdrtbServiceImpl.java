package org.openmrs.module.mdrtb.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;

import org.openmrs.module.mdrtb.MdrtbConceptMap;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.comparator.PatientProgramComparator;
import org.openmrs.module.mdrtb.comparator.PersonByNameComparator;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.service.db.MdrtbDAO;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.HAIN;
import org.openmrs.module.mdrtb.specimen.HAINImpl;
import org.openmrs.module.mdrtb.specimen.ScannedLabReport;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenImpl;
import org.openmrs.module.mdrtb.specimen.Xpert;
import org.openmrs.module.mdrtb.specimen.XpertImpl;
import org.openmrs.module.reporting.common.ObjectUtil;

public class MdrtbServiceImpl extends BaseOpenmrsService implements MdrtbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected MdrtbDAO dao;
	
	private MdrtbConceptMap conceptMap = new MdrtbConceptMap(); // TODO: should this be a bean?		
	
	// caches
	private Map<Integer,String> colorMapCache = null;

	public void setMdrtbDAO(MdrtbDAO dao) {
		this.dao = dao;
	}
	
	
	/**
	 * @see MdrtbService#getLocationsWithAnyProgramEnrollments()
	 */
	public List<Location> getLocationsWithAnyProgramEnrollments() {
		return dao.getLocationsWithAnyProgramEnrollments();
	}

	public Concept getConcept(String... conceptMapping) {
		return conceptMap.lookup(conceptMapping);
	}
	
	public Concept getConcept(String conceptMapping) {
		return conceptMap.lookup(conceptMapping);
	}
	
	/**
	 * @see MdrtbService#findMatchingConcept(String)
	 */
	public Concept findMatchingConcept(String lookup) {
    	if (ObjectUtil.notNull(lookup)) {
    		// First try MDR-TB module's known concept mappings
    		try {
    			return Context.getService(MdrtbService.class).getConcept(new String[] {lookup});
    		}
    		catch (Exception e) {}
    		// Next try id/name
    		try {
    			Concept c = Context.getConceptService().getConcept(lookup);
    			if (c != null) {
    				return c;
    			}
    		}
    		catch (Exception e) {}
    		// Next try uuid 
        	try {
        		Concept c = Context.getConceptService().getConceptByUuid(lookup);
    			if (c != null) {
    				return c;
    			}
        	}
        	catch (Exception e) {}
    	}
    	return null;
	}

	public void resetConceptMapCache() {
		this.conceptMap.resetCache();
	}
	
	public List<Encounter> getMdrtbEncounters(Patient patient) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, null, MdrtbUtil.getMdrtbEncounterTypes(), null, false);
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientPrograms() {
		return getAllMdrtbPatientProgramsInDateRange(null, null);
	}
	
	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(), null, endDate, startDate, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		mdrtbPrograms.add(new MdrtbPatientProgram(program));
    	}
    	
    	return mdrtbPrograms;
	}
	
	public List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient) {
    	
    	List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getMdrtbProgram(), null, null, null, null, false);
    	
    	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<MdrtbPatientProgram> mdrtbPrograms = new LinkedList<MdrtbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		mdrtbPrograms.add(new MdrtbPatientProgram(program));
    	}
    	
    	return mdrtbPrograms;
    }

	
	public MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient) {
    	List<MdrtbPatientProgram> programs = getMdrtbPatientPrograms(patient);
    	
    	if (programs.size() > 0) {
    		return programs.get(programs.size() - 1);
    	} 
    	else {
    		return null;
    	}
    }
	
	public List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<MdrtbPatientProgram> programs = new LinkedList<MdrtbPatientProgram>();
		
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if( (endDate == null || program.getDateEnrolled().before(endDate)) &&
	    			(program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(startDate)) ) {
	    			programs.add(program);
	    	}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date) {
		for (MdrtbPatientProgram program : getMdrtbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}

		return null;
	}
	
	public MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		}
		else if (patientProgramId == -1) {
			return null;
		}
		else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getMdrtbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference an MDR-TB patient program");
			}
			
			else {
				return new MdrtbPatientProgram(program);
			}
		}
	}
	
	public Specimen createSpecimen(Patient patient) {
		// return null if the patient is null
		if(patient == null) {
			log.error("Unable to create specimen obj: createSpecimen called with null patient.");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(patient);
	}
	
	public Specimen getSpecimen(Integer specimenId) {
		return getSpecimen(Context.getEncounterService().getEncounter(specimenId));
	}
	
	public Specimen getSpecimen(Encounter encounter) {
		// return null if there is no encounter, or if the encounter if of the wrong type
		if(encounter == null || encounter.getEncounterType() != Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"))) {
			log.error("Unable to fetch specimen obj: getSpecimen called with invalid encounter");
			return null;
		}
		
		// otherwise, instantiate the specimen object
		return new SpecimenImpl(encounter);
	}
	
	public List<Specimen> getSpecimens(Patient patient) {
		return getSpecimens(patient, null, null, null);
	}
	
	public List<Specimen> getSpecimens(Patient patient, Date startDate, Date endDate) {	
		return getSpecimens(patient, startDate, endDate, null);
	}
	 
	public List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected) {
		List<Specimen> specimens = new LinkedList<Specimen>();
		List<Encounter> specimenEncounters = new LinkedList<Encounter>();
		
		// create the specific specimen encounter types
		EncounterType specimenEncounterType = Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("mdrtb.specimen_collection_encounter_type"));
		List<EncounterType> specimenEncounterTypes = new LinkedList<EncounterType>();
		specimenEncounterTypes.add(specimenEncounterType);
		
		specimenEncounters = Context.getEncounterService().getEncounters(patient, locationCollected, startDateCollected, endDateCollected, null, specimenEncounterTypes, null, false);
		
		for(Encounter encounter : specimenEncounters) {	
			specimens.add(new SpecimenImpl(encounter));
		}
		
		Collections.sort(specimens);
		return specimens;
	}
	
	public void saveSpecimen(Specimen specimen) {
		if (specimen == null) {
			log.warn("Unable to save specimen: specimen object is null");
			return;
		}
		
		// make sure getSpecimen returns the right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should an encounter)
		if(!(specimen.getSpecimen() instanceof Encounter)){
			throw new APIException("Not a valid specimen implementation for this service implementation.");
		}
		//We need the specimen encounters to potentially be viewable by a bacteriology htmlform:
		Encounter enc = (Encounter) specimen.getSpecimen();
		String formIdWithWhichToViewEncounter = Context.getAdministrationService().getGlobalProperty("mdrtb.formIdToAttachToBacteriologyEntry");
		try {
		    if (formIdWithWhichToViewEncounter != null && !formIdWithWhichToViewEncounter.equals(""))
		        enc.setForm(Context.getFormService().getForm(Integer.valueOf(formIdWithWhichToViewEncounter)));
		} catch (Exception ex){
		    log.error("Invalid formId found in global property mdrtb.formIdToAttachToBacteriologyEntry");
		}
		
		// otherwise, go ahead and do the save
		Context.getEncounterService().saveEncounter(enc);
	}
	
	public void deleteSpecimen(Integer specimenId) {
		Encounter encounter = Context.getEncounterService().getEncounter(specimenId);
		
		if (encounter == null) {
			throw new APIException("Unable to delete specimen: invalid specimen id " + specimenId);
		}
		else {
			Context.getEncounterService().voidEncounter(encounter, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void deleteTest(Integer testId) {
		Obs obs = Context.getObsService().getObs(testId);
		
		// the id must refer to a valid obs, which is a smear, culture, or dst construct
		if (obs == null || !(obs.getConcept().equals(this.getConcept(MdrtbConcepts.SMEAR_CONSTRUCT))
				|| obs.getConcept().equals(this.getConcept(MdrtbConcepts.CULTURE_CONSTRUCT)) 
				|| obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_CONSTRUCT)) )) {
			throw new APIException ("Unable to delete specimen test: invalid test id " + testId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public Smear createSmear(Specimen specimen) {			
		if (specimen == null) {
			log.error("Unable to create smear: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addSmear();
	}
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new SmearImpl(obs);
	}

	public Smear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public void saveSmear(Smear smear) {
		if (smear == null) {
			log.warn("Unable to save smear: smear object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(smear.getTest() instanceof Obs)) {
			throw new APIException("Not a valid smear implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) smear.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Culture createCulture(Specimen specimen) {			
		if (specimen == null) {
			log.error("Unable to create culture: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addCulture();
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new CultureImpl(obs);
	}

	public Culture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public void saveCulture(Culture culture) {
		if (culture == null) {
			log.warn("Unable to save culture: culture object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(culture.getTest() instanceof Obs)) {
			throw new APIException("Not a valid culture implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) culture.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	public Dst createDst(Specimen specimen) {		
		if (specimen == null) {
			log.error("Unable to create dst: specimen is null.");
			return null;
		}
		
		// add the culture to the specimen
		return specimen.addDst();
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new DstImpl(obs);
	}

	public Dst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public void saveDst(Dst dst) {
		if (dst == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getCulture returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(dst.getTest() instanceof Obs)) {
			throw new APIException("Not a valid dst implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) dst.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
		
	public void deleteDstResult(Integer dstResultId) {
		Obs obs = Context.getObsService().getObs(dstResultId);
		
		// the id must refer to a valid obs, which is a dst result
		if (obs == null || ! obs.getConcept().equals(this.getConcept(MdrtbConcepts.DST_RESULT)) ) {
			throw new APIException ("Unable to delete dst result: invalid dst result id " + dstResultId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void saveScannedLabReport(ScannedLabReport report) {
		if (report == null) {
			log.warn("Unable to save dst: dst object is null");
			return;
		}
		
		// make sure getScannedLabReport returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
		if(!(report.getScannedLabReport() instanceof Obs)) {
			throw new APIException("Not a valid scanned lab report implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) report.getScannedLabReport(), "voided by Mdr-tb module specimen tracking UI");
	}
	
	public void deleteScannedLabReport(Integer reportId) {
		Obs obs = Context.getObsService().getObs(reportId);
		
		// the id must refer to a valid obs, which is a scanned lab report
		if (obs == null || ! obs.getConcept().equals(this.getConcept(MdrtbConcepts.SCANNED_LAB_REPORT)) ) {
			throw new APIException ("Unable to delete scanned lab report: invalid report id " + reportId);
		}
		else {
			Context.getObsService().voidObs(obs, "voided by Mdr-tb module specimen tracking UI");
		}
	}
	
	public void processDeath(Patient patient, Date deathDate, Concept causeOfDeath) {
	
		// first call the main Patient Service process death method
		Context.getPatientService().processDeath(patient, deathDate, causeOfDeath, null);
		
		// if the most recent MDR-TB program is open, we need to close it
		MdrtbPatientProgram program = getMostRecentMdrtbPatientProgram(patient);
		
		if (program != null && program.getActive()) {
			program.setDateCompleted(deathDate);
			program.setOutcome(MdrtbUtil.getProgramWorkflowState(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIED)));
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
		
		// if the patient is hospitalized, we need to end the hospitalization
		if (program != null && program.getCurrentlyHospitalized()) {
			program.closeCurrentHospitalization(deathDate);
			Context.getProgramWorkflowService().savePatientProgram(program.getPatientProgram());
		}
	}
	
    public Program getMdrtbProgram() {
    	return Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    }
	
   public Collection<Person> getProviders() {
		// TODO: this should be customizable, so that other installs can define there own provider lists?
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
		
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		
		for (User user : providers) {
			persons.add(user.getPerson());
		}
		
		return persons;
	}
    
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return this.getConcept(MdrtbConcepts.SMEAR_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return this.getConcept(MdrtbConcepts.SMEAR_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return this.getConcept(MdrtbConcepts.CULTURE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return this.getConcept(MdrtbConcepts.CULTURE_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return this.getConcept(MdrtbConcepts.DST_METHOD).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(this.getConcept(MdrtbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(MdrtbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(MdrtbConcepts.WAITING_FOR_TEST_RESULTS));
		
		return results;
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return this.getConcept(MdrtbConcepts.TYPE_OF_ORGANISM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {	
		return this.getConcept(MdrtbConcepts.SAMPLE_SOURCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return this.getConcept(MdrtbConcepts.SPECIMEN_APPEARANCE).getAnswers();
	}
	
	   
    public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
    	return this.getConcept(MdrtbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
    }
    
    /**
     * @return the List of Concepts that represent the Drugs within the passed Drug Set
     */
    public List<Concept> getDrugsInSet(String... conceptMapKey) {
    	return getDrugsInSet(Context.getService(MdrtbService.class).getConcept(conceptMapKey));
    }
    
    /**
     * @return the List of Concepts that represent the Drugs within the passed Drug Set
     */
    public List<Concept> getDrugsInSet(Concept concept) {
    	List<Concept> drugs = new LinkedList<Concept>();
    	if (concept != null) {
    		List<ConceptSet> drugSet = Context.getConceptService().getConceptSetsByConcept(concept);
    		if (drugSet != null) {
				for (ConceptSet drug : drugSet) {
					drugs.add(drug.getConcept());
				}
    		}
    	}
    	return drugs;    	
    }
	
    public List<Concept> getMdrtbDrugs() {
    	return getDrugsInSet(MdrtbConcepts.TUBERCULOSIS_DRUGS);
    }
    
    public List<Concept> getAntiretrovirals() {
    	return getDrugsInSet(MdrtbConcepts.ANTIRETROVIRALS);
    }
    
    public Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes() {
    	return getPossibleWorkflowStates(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MDR_TB_TX_OUTCOME));
    }

    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse() {
    	return getPossibleWorkflowStates(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE));
    }
  
    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
    	return getPossibleWorkflowStates(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX));
    }    
    
    public String getColorForConcept(Concept concept) {
    	if(concept == null) {
    		log.error("Cannot fetch color for null concept");
    		return "";
    	}
    	
    	// initialize the cache if need be
    	if(colorMapCache == null) {
    		colorMapCache = loadCache(Context.getAdministrationService().getGlobalProperty("mdrtb.colorMap"));
    	}
    	
    	String color = "";
    	
    	try {
    		color = colorMapCache.get(concept.getId());
    	}
    	catch(Exception e) {
    		log.error("Unable to get color for concept " + concept.getId());
    		color = "white";
    	}
    	
    	return color;
    }
	
    public void resetColorMapCache() {
    	this.colorMapCache = null;
    }
    
	/**
	 * Utility functions
	 */
    
    private Set<ProgramWorkflowState> getPossibleWorkflowStates(Concept workflowConcept) {
    	// get the mdrtb program via the name listed in global properties
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("mdrtb.program_name"));
    	
    	// get the workflow via the concept name
    	for (ProgramWorkflow workflow : mdrtbProgram.getAllWorkflows()) {
    		if (workflow.getConcept().equals(workflowConcept)) {
    			return workflow.getStates(false);
    		}
    	}
    	return null;
    }
    
    
    private Map<Integer,String> loadCache(String mapAsString) {
    	Map<Integer,String> map = new HashMap<Integer,String>();
    	
    	if(StringUtils.isNotBlank(mapAsString)) {    	
    		for(String mapping : mapAsString.split("\\|")) {
    			String[] mappingFields = mapping.split(":");
    			
    			Integer conceptId = null;
    			
    			// if this is a mapping code, need to convert it to the concept id
    			if(!MdrtbUtil.isInteger(mappingFields[0])) {
    				Concept concept = getConcept(mappingFields[0]);
    				if (concept != null) {
    					conceptId = concept.getConceptId();
    				}
    				else {
    					throw new MdrtbAPIException("Invalid concept mapping value in the the colorMap global property.");
    				}
    			}
    			// otherwise, assume this is a concept id
    			else {
    				conceptId = Integer.valueOf(mappingFields[0]);
    			}
    			
    			map.put(conceptId, mappingFields[1]);
    		}
    	}
    	else {
    		// TODO: make this error catching a little more elegant?
    		throw new RuntimeException("Unable to load cache, cache string is null. Is required global property missing?");
    	}
    	
    	return map;
    }
    
    public List<String> getAllRayonsTJK() {
    	List<String> rayonList = null;
    	
    	return dao.getAllRayonsTJK();
    }
    

	public List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(), startDate, endDate, null, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<MdrtbPatientProgram> tbPrograms = new LinkedList<MdrtbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new MdrtbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}
	
	///////////////////////////////////////
	
	public Xpert createXpert(Specimen specimen) {			
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addXpert();
	}
	
	public Xpert getXpert(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new XpertImpl(obs);
	}

	public Xpert getXpert(Integer obsId) {
		return getXpert(Context.getObsService().getObs(obsId));
	}
	
	public void saveXpert(Xpert xpert) {
		if (xpert == null) {
			log.warn("Unable to save xpert: xpert object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(xpert.getTest() instanceof Obs)) {
			throw new APIException("Not a valid xpert implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) xpert.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	
	///////////////////////////
	
///////////////////////////////////////
	
	public HAIN createHAIN(Specimen specimen) {			
		if (specimen == null) {
			log.error("Unable to create xpert: specimen is null.");
			return null;
		}
		
		// add the smear to the specimen
		return specimen.addHAIN();
	}
	
	public HAIN getHAIN(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new HAINImpl(obs);
	}

	public HAIN getHAIN(Integer obsId) {
		return getHAIN(Context.getObsService().getObs(obsId));
	}
	
	public void saveHAIN(HAIN hain) {
		if (hain == null) {
			log.warn("Unable to save hain: hain object is null");
			return;
		}
		
		// make sure getSmear returns that right type
		// (i.e., that this service implementation is using the specimen implementation that it expects, which should return a observation)
	
		if(!(hain.getTest() instanceof Obs)) {
			throw new APIException("Not a valid hain implementation for this service implementation");
		}
		
		// otherwise, go ahead and do the save
		Context.getObsService().saveObs((Obs) hain.getTest(), "voided by Mdr-tb module specimen tracking UI");
		
	}
	//////////////////////////////
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return this.getConcept(MdrtbConcepts.MTB_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return this.getConcept(MdrtbConcepts.RIFAMPICIN_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return this.getConcept(MdrtbConcepts.ISONIAZID_RESISTANCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return this.getConcept(MdrtbConcepts.XPERT_MTB_BURDEN).getAnswers();
	}

}
