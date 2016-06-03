package org.openmrs.module.mdrtb.status;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;


public class NullLabResultsStatusRenderer implements LabResultsStatusRenderer {

    public StatusFlag createNoCulturesFlag() {
	    return null;
    }

    public StatusFlag createNoSmearsFlag() {
	    return null;
    }

    public String renderAnatomicalSite(StatusItem anatomicalStatus) {
	    return null;
    }

    public String renderConversion(StatusItem cultureConversion) {
	    return null;
    }
	    
    public void renderCulture(StatusItem diagnosticCulture, LabResultsStatus status) {
    }

    public String renderDrugResistanceProfile(List<Concept> drugs) {
	    return null;
    }

    public void renderPendingLabResults(StatusItem pendingLabResults, LabResultsStatus status) {
    }

    public void renderSmear(StatusItem diagnosticSmear, LabResultsStatus status) {
    }
    
    public void renderXpert(StatusItem diagnosticXpert, LabResultsStatus status) {
    }
    
    public void renderHAIN(StatusItem diagnosticHAIN, LabResultsStatus status) {
    }

    public String renderTbClassification(TbClassification classification) {
	    return null;
    }

}
