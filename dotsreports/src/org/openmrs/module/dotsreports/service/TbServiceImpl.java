package org.openmrs.module.dotsreports.service;

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
import org.openmrs.module.dotsreports.MdrtbConceptMap;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.TbUtil;
import org.openmrs.module.dotsreports.comparator.PatientProgramComparator;
import org.openmrs.module.dotsreports.comparator.PersonByNameComparator;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;

import org.openmrs.module.dotsreports.program.TbPatientProgram;
import org.openmrs.module.dotsreports.service.db.MdrtbDAO;
import org.openmrs.module.dotsreports.specimen.Culture;
import org.openmrs.module.dotsreports.specimen.CultureImpl;
import org.openmrs.module.dotsreports.specimen.Dst;
import org.openmrs.module.dotsreports.specimen.DstImpl;
import org.openmrs.module.dotsreports.specimen.Smear;
import org.openmrs.module.dotsreports.specimen.SmearImpl;
import org.openmrs.module.dotsreports.specimen.Specimen;
import org.openmrs.module.dotsreports.specimen.SpecimenImpl;
import org.openmrs.module.reporting.common.ObjectUtil;

public class TbServiceImpl extends BaseOpenmrsService implements TbService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected MdrtbDAO dao;
	
	private MdrtbConceptMap conceptMap = new MdrtbConceptMap(); // TODO: should this be a bean?		
	
	// caches
	private Map<Integer,String> colorMapCache = null;

	public void setMdrtbDAO(MdrtbDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see TbService#getLocationsWithAnyProgramEnrollments()
	 */
	public List<Location> getLocationsWithAnyProgramEnrollments() {
		return dao.getLocationsWithAnyProgramEnrollments();
	}

	public Concept getConcept(String... conceptMapping) {
		return conceptMap.lookup(conceptMapping);
	}
	
	public Concept getConcept(String conceptMapping) {
		System.out.println ("CONC MAP:" + conceptMapping);
		return conceptMap.lookup(conceptMapping);
	}
	
	/**
	 * @see TbService#findMatchingConcept(String)
	 */
	public Concept findMatchingConcept(String lookup) {
    	if (ObjectUtil.notNull(lookup)) {
    		// First try MDR-TB module's known concept mappings
    		try {
    			return Context.getService(TbService.class).getConcept(new String[] {lookup});
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
	
	public List<Encounter> getTbEncounters(Patient patient) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, null, TbUtil.getTbEncounterTypes(), null, false);
	}
	
	
	
	public List<TbPatientProgram> getAllTbPatientPrograms() {
		return getAllTbPatientProgramsInDateRange(null, null);
	}
	
	
	
	public List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), startDate, endDate, null, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}
	
	public List<TbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getMdrtbProgram(), startDate, endDate, null, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}


	public List<TbPatientProgram> getAllTbPatientProgramsInDateRange(Date startDate, Date endDate) {
		// (program must have started before the end date of the period, and must not have ended before the start of the period)
		List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(null, getTbProgram(), null, endDate, startDate, null, false);
    	
	 	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
	}
public List<TbPatientProgram> getTbPatientPrograms(Patient patient) {
    	
    	List<PatientProgram> programs = Context.getProgramWorkflowService().getPatientPrograms(patient, getTbProgram(), null, null, null, null, false);
    	
    	// sort the programs so oldest is first and most recent is last
    	Collections.sort(programs, new PatientProgramComparator());
    	
    	List<TbPatientProgram> tbPrograms = new LinkedList<TbPatientProgram>();
    	
    	// convert to mdrtb patient programs
    	for (PatientProgram program : programs) {
    		tbPrograms.add(new TbPatientProgram(program));
    	}
    	
    	return tbPrograms;
    }

	
	public TbPatientProgram getMostRecentTbPatientProgram(Patient patient) {
    	List<TbPatientProgram> programs = getTbPatientPrograms(patient);
    	
    	if (programs.size() > 0) {
    		return programs.get(programs.size() - 1);
    	} 
    	else {
    		return null;
    	}
    }
	
	public List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate) {
		List<TbPatientProgram> programs = new LinkedList<TbPatientProgram>();
		
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if( (endDate == null || program.getDateEnrolled().before(endDate)) &&
	    			(program.getDateCompleted() == null || startDate == null || !program.getDateCompleted().before(startDate)) ) {
	    			programs.add(program);
	    	}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	public TbPatientProgram getTbPatientProgramOnDate(Patient patient, Date date) {
		for (TbPatientProgram program : getTbPatientPrograms(patient)) {
			if (program.isDateDuringProgram(date)) {
				return program;
			}
		}

		return null;
	}
	
	public TbPatientProgram getTbPatientProgram(Integer patientProgramId) {
		if (patientProgramId == null) {
			throw new MdrtbAPIException("Patient program Id cannot be null.");
		}
		else if (patientProgramId == -1) {
			return null;
		}
		else {
			PatientProgram program = Context.getProgramWorkflowService().getPatientProgram(patientProgramId);
			
			if (program == null || !program.getProgram().equals(getTbProgram())) {
				throw new MdrtbAPIException(patientProgramId + " does not reference a TB patient program");
			}
			
			else {
				return new TbPatientProgram(program);
			}
		}
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
	
	public Smear getSmear(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new SmearImpl(obs);
	}

	public Smear getSmear(Integer obsId) {
		return getSmear(Context.getObsService().getObs(obsId));
	}
	
	public Culture getCulture(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new CultureImpl(obs);
	}

	public Culture getCulture(Integer obsId) {
		return getCulture(Context.getObsService().getObs(obsId));
	}
	
	public Dst getDst(Obs obs) {
		// don't need to do much error checking here because the constructor will handle it
		return new DstImpl(obs);
	}

	public Dst getDst(Integer obsId) {
		return getDst(Context.getObsService().getObs(obsId));
	}
	
	public Program getTbProgram() {
    	return Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"));
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
		return this.getConcept(TbConcepts.SMEAR_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return this.getConcept(TbConcepts.SMEAR_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return this.getConcept(TbConcepts.CULTURE_RESULT).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return this.getConcept(TbConcepts.CULTURE_METHOD).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return this.getConcept(TbConcepts.DST_METHOD).getAnswers();
	}
	
	public Collection<Concept> getPossibleDstResults() {
		List<Concept> results = new LinkedList<Concept>();
		results.add(this.getConcept(TbConcepts.SUSCEPTIBLE_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.INTERMEDIATE_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.RESISTANT_TO_TB_DRUG));
		results.add(this.getConcept(TbConcepts.DST_CONTAMINATED));
		results.add(this.getConcept(TbConcepts.WAITING_FOR_TEST_RESULTS));
		
		return results;
	}
	
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return this.getConcept(TbConcepts.TYPE_OF_ORGANISM).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {	
		return this.getConcept(TbConcepts.SAMPLE_SOURCE).getAnswers();
	}
	
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return this.getConcept(TbConcepts.SPECIMEN_APPEARANCE).getAnswers();
	}
	
	   
    public Collection<ConceptAnswer> getPossibleAnatomicalSites() {
    	return this.getConcept(TbConcepts.ANATOMICAL_SITE_OF_TB).getAnswers();
    }
    
    /**
     * @return the List of Concepts that represent the Drugs within the passed Drug Set
     */
    public List<Concept> getDrugsInSet(String... conceptMapKey) {
    	return getDrugsInSet(Context.getService(TbService.class).getConcept(conceptMapKey));
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
    	return getDrugsInSet(TbConcepts.TUBERCULOSIS_DRUGS);
    }
    
    public List<Concept> getAntiretrovirals() {
    	return getDrugsInSet(TbConcepts.ANTIRETROVIRALS);
    }
    
    public Set<ProgramWorkflowState> getPossibleTbProgramOutcomes() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.TB_TX_OUTCOME));
    }

    public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_GROUP));
    }
  
    /*public Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment() {
    	return getPossibleWorkflowStates(Context.getService(TbService.class).getConcept(TbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX));
    }  */  
    
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
    	Program mdrtbProgram = Context.getProgramWorkflowService().getProgramByName(Context.getAdministrationService().getGlobalProperty("dotsreports.program_name"));
    	
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
    			if(!TbUtil.isInteger(mappingFields[0])) {
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
}
