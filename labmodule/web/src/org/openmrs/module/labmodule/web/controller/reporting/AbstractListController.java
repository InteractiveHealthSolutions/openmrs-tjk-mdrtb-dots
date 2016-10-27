package org.openmrs.module.labmodule.web.controller.reporting;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.comparator.PersonByNameComparator;
import org.openmrs.module.labmodule.exception.MdrtbAPIException;
import org.openmrs.module.labmodule.program.TbPatientProgram;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractListController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		
		// register binders for concepts, locations, and persons
		binder.registerCustomEditor(Concept.class, new ConceptEditor()); 
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Person.class, new PersonEditor());
	}
	
	
	@ModelAttribute("types")
	public Collection<ConceptAnswer> getPossibleSpecimenTypes() {
		return Context.getService(TbService.class).getPossibleSpecimenTypes();
	}
	
	@ModelAttribute("investigationPurposes")
	public Collection<ConceptAnswer> getPossibleInvestigationPurposes() {
		return Context.getService(TbService.class).getPossibleInvestigationPurposes();
	}
	
	@ModelAttribute("providers")
	public Collection<Person> getPossibleProviders() {
		// TODO: this should be customizable, so that other installs can define there own provider lists?
		Role provider = Context.getUserService().getRole("Provider");
		Collection<User> providers = Context.getUserService().getUsersByRole(provider);
			
		// add all the persons to a sorted set sorted by name
		SortedSet<Person> persons = new TreeSet<Person>(new PersonByNameComparator()); 
		
		for(User user : providers) {
			persons.add(user.getPerson());
		}
		
		return persons;
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getAllLocations() {
		return Context.getService(TbService.class).getAllLocations();
	}
	
	@ModelAttribute("labs")
	public Collection<Location> getAllLabs() {
		return Context.getService(TbService.class).getAllLocations();
	}
	
	@ModelAttribute("appearances")
	public Collection<ConceptAnswer> getPossibleSpecimenAppearances() {
		return Context.getService(TbService.class).getPossibleSpecimenAppearances();
	}
	
	@ModelAttribute("requestingFacilities")
	public Collection<ConceptAnswer> getRequestingFacility() {
		return Context.getService(TbService.class).getPossibleRequestingFacilities();
	}
	
	@ModelAttribute("microscopyResults")
	public Collection<ConceptAnswer> getMicroscopyResults() {
		return Context.getService(TbService.class).getPossibleMicroscopyResults();
	}
	
	@ModelAttribute("mtbResults")
	public Collection<ConceptAnswer> getPossibleMtbResults() {
		return Context.getService(TbService.class).getPossibleMtbResults();
	}
	
	@ModelAttribute("rifResults")
	public Collection<ConceptAnswer> getPossibleRifResistanceResults() {
		return Context.getService(TbService.class).getPossibleRifResistanceResults();
	}
	
	@ModelAttribute("inhResults")
	public Collection<ConceptAnswer> getPossibleInhResistanceResults() {
		return Context.getService(TbService.class).getPossibleRifResistanceResults();
	}
	
	@ModelAttribute("moxResults")
	public Collection<ConceptAnswer> getPossibleMoxResults() {
		return Context.getService(TbService.class).getPossibleMoxResults();
	}
	
	@ModelAttribute("cmResults")
	public Collection<ConceptAnswer> getPossibleCmResults() {
		return Context.getService(TbService.class).getPossibleCmResults();
	}
	
	@ModelAttribute("eResults")
	public Collection<ConceptAnswer> getPossibleEResults() {
		return Context.getService(TbService.class).getPossibleEResults();
	}
	
	@ModelAttribute("smearResults")
	public Collection<ConceptAnswer> getPossibleSmearResults() {
		return Context.getService(TbService.class).getPossibleSmearResults();
	}
	
	@ModelAttribute("smearMethods")
	public Collection<ConceptAnswer> getPossibleSmearMethods() {
		return Context.getService(TbService.class).getPossibleSmearMethods();
	}
	
	@ModelAttribute("cultureResults")
	public Collection<ConceptAnswer> getPossibleCultureResults() {
		return Context.getService(TbService.class).getPossibleCultureResults();
	}
	
	@ModelAttribute("cultureMethods")
	public Collection<ConceptAnswer> getPossibleCultureMethods() {
		return Context.getService(TbService.class).getPossibleCultureMethods();
	}
	
	@ModelAttribute("dstMethods")
	public Collection<ConceptAnswer> getPossibleDstMethods() {
		return Context.getService(TbService.class).getPossibleDstMethods();
	}
	
	@ModelAttribute("dstResults")
	public Collection<Concept> getPossibleDstResults() {
		return Context.getService(TbService.class).getPossibleDstResults();
	}
	
	@ModelAttribute("organismTypes")
	public Collection<ConceptAnswer> getPossibleOrganismTypes() {
		return Context.getService(TbService.class).getPossibleOrganismTypes();
	}
	
	@ModelAttribute("testTypes")
	public Collection<String> getPossibleTestTypes() {
		Collection<String> testTypes = new LinkedList<String>();
		testTypes.add("smear");
		testTypes.add("culture");
		testTypes.add("dst");
		testTypes.add("xpert");
		testTypes.add("hain");
		return testTypes;
	}
	
	@ModelAttribute("drugTypes")
	public Collection<Concept> getPossibleDrugTypes() {
		return Context.getService(TbService.class).getMdrtbDrugs();
	}
	
	@ModelAttribute("positiveResults")
	public Collection<Concept> getPositiveResults() {
		return TbUtil.getPositiveResultConcepts();
	}
	
	// used in the jquery in specimen.jsp to trigger hide/show of days of positivity field
	@ModelAttribute("positiveResultsIds")
	public Collection<String> getPositiveResultsIds() {
		Set<String> positiveResultsIds = new HashSet<String>();
		
		for (Concept positiveResult : TbUtil.getPositiveResultConcepts()) {
			positiveResultsIds.add(positiveResult.getConceptId().toString());
		}
		
		return positiveResultsIds;
	}
	
	@ModelAttribute("scanty")
	public Concept getConceptScanty() {		
		return Context.getService(TbService.class).getConcept(TbConcepts.SCANTY);
	}
	
	@ModelAttribute("waitingForTestResult")
	public Concept getWaitingForTestResultConcept() {
		return Context.getService(TbService.class).getConcept(TbConcepts.WAITING_FOR_TEST_RESULTS);
	}
	
	@ModelAttribute("dstTestContaminated")
	public Concept getDstTestContaiminated() {
		return Context.getService(TbService.class).getConcept(TbConcepts.DST_CONTAMINATED);
	}
	
	@ModelAttribute("otherMycobacteriaNonCoded")
	public Concept getOtherMycobacteriaNonCoded() {
		return Context.getService(TbService.class).getConcept(TbConcepts.OTHER_MYCOBACTERIA_NON_CODED);
	}	
	
	@ModelAttribute("xpertMtbBurdens")
	public Collection<ConceptAnswer> getPossibleXpertMtbBurdens() {
		return Context.getService(TbService.class).getPossibleXpertMtbBurdens();
	}
	
	@ModelAttribute("oblasts")
	public List<Oblast> getPossibleOblast(){
		return Context.getService(TbService.class).getOblasts();
	}
}
