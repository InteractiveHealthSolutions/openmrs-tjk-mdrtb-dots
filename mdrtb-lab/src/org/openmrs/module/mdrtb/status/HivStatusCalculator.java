package org.openmrs.module.mdrtb.status;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.service.MdrtbService;


public class HivStatusCalculator implements StatusCalculator {

	private HivStatusRenderer renderer;
	
	public HivStatusCalculator(HivStatusRenderer renderer) {
		this.setRenderer(renderer);
	}
	
	
    public Status calculate(Patient patient) {
    	HivStatus status = new HivStatus();
    
    	findHivStatusAndMostRecentTestResult(patient, status);
    	findCd4Count(patient, status);
    	findRegimens(patient, status);
		
    	return status;
    }


	public void setRenderer(HivStatusRenderer renderer) {
	    this.renderer = renderer;
    }


	public HivStatusRenderer getRenderer() {
	    return renderer;
    }

	/**
	 * Utility methods
	 */
	
	public void findHivStatusAndMostRecentTestResult(Patient patient, Status status) {
		// first, get the most recent HIV test result for this patient
    	List<Obs> testResults = Context.getObsService().getObservationsByPersonAndConcept(patient, 
    		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST));
    	
    	Obs mostRecentTestResult = null;
    	
    	if (testResults !=null && !testResults.isEmpty()) {
    		mostRecentTestResult = testResults.get(0);
    	}
    	
    	StatusItem testResultItem = new StatusItem(mostRecentTestResult);
    	renderer.renderMostRecentTestResult(testResultItem);
    	status.addItem("mostRecentTestResult", testResultItem);
    	
    	// now see if there are any instances of the "CO-INFECTED" obs
    	//  TODO: is this obs critical/important/relevant?
    	List<Obs> coninfectedObs = Context.getObsService().getObservationsByPersonAndConcept(patient, 
    		Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COINFECTED_ARVS));
    	
    	Obs coinfected = null;
    	
    	if (coninfectedObs !=null && !coninfectedObs.isEmpty()) {
    		coinfected = coninfectedObs.get(0);
    	}
    	
    	// determine if this patient is currently HIV positive
    	// TODO: right now we consider a patient positive if either the most recent test result is positive, or
    	// the most recent "coinfected and on arvs" obs is true; is this correct?
    	StatusItem hivStatus = new StatusItem();
    	Concept positive = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE);
    	
    	if ((mostRecentTestResult != null && positive.equals(mostRecentTestResult.getValueCoded()))
    			|| (coinfected !=null && coinfected.getValueAsBoolean() == true)) {
    		hivStatus.setValue(positive);
    	}
    	else if (mostRecentTestResult != null){
    		hivStatus.setValue(mostRecentTestResult.getValueCoded());
    	}
    	
    	renderer.renderHivStatus(hivStatus);
    	status.addItem("hivStatus", hivStatus);
	}
	
	public void findCd4Count(Patient patient, Status status) {
		StatusItem cd4Count = new StatusItem();
		
		List<Obs> cd4Counts = Context.getObsService().getObservationsByPersonAndConcept(patient, 
			Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CD4_COUNT));
		
		if (cd4Counts != null && !cd4Counts.isEmpty()) {
			cd4Count.setValue(cd4Counts.get(0));
		}
		
		renderer.renderCd4Count(cd4Count);
		status.addItem("mostRecentCd4Count", cd4Count);
	}
	
	public void findRegimens(Patient patient, Status status) {
    	
		List<StatusItem> regimenList = new LinkedList<StatusItem>();
		
		for (Regimen regimen : MdrtbUtil.getAntiretroviralRegimens(patient)) {
	    	
	    	StatusItem item = new StatusItem();
	    	
	    	item.setValue(regimen);
	    	renderer.renderRegimen(item);
	    	
	    	regimenList.add(item);
	    }
		
		// reverse the list so it is in reverse chronological order
		Collections.reverse(regimenList);
		
		status.addItem("regimens", new StatusItem(regimenList));
		
		// now find the current regimen--the first element in the list, if it is active
		// (we are explicitedly getting this here just so we can use a custom renderer for it)
		StatusItem currentRegimen = new StatusItem();
		if (regimenList != null && regimenList.size() > 0 && ((Regimen) regimenList.get(0).getValue()).isActive()) {
			currentRegimen = regimenList.get(0);
		}
		
		renderer.renderCurrentRegimen(currentRegimen);
		status.addItem("currentRegimen", currentRegimen);
		
		// TODO: right now we are just calling the ART treatment start date the start of the first antiretroviral regimen, 
		// but this might not be entirely accurate
		StatusItem artTreatment = new StatusItem(regimenList);
		renderer.renderArtTreatment(artTreatment);
    	status.addItem("artTreatment", artTreatment);
	}
}
