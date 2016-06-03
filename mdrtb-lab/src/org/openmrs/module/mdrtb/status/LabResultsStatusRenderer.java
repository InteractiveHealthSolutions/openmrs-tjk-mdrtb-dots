package org.openmrs.module.mdrtb.status;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.mdrtb.MdrtbConstants.TbClassification;

public interface LabResultsStatusRenderer {
	
	public void renderSmear(StatusItem diagnosticSmear, LabResultsStatus status);
	
	public void renderCulture(StatusItem diagnosticCulture, LabResultsStatus status);
	
	public void renderXpert(StatusItem diagnosticXpert, LabResultsStatus status);
	
	public void renderHAIN(StatusItem diagnosticHAIN, LabResultsStatus status);

	public void renderPendingLabResults(StatusItem pendingLabResults, LabResultsStatus status);
	
	public String renderDrugResistanceProfile(List<Concept> drugs);
	
	public String renderTbClassification(TbClassification classification);
	
	public String renderConversion(StatusItem cultureConversion);
	
	public String renderAnatomicalSite(StatusItem anatomicalStatus);	
	
	public StatusFlag createNoSmearsFlag();
	
	public StatusFlag createNoCulturesFlag();
}
