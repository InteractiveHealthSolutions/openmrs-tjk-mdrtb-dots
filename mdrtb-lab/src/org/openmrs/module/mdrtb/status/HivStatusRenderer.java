package org.openmrs.module.mdrtb.status;



public interface HivStatusRenderer {

	public void renderMostRecentTestResult(StatusItem testResultItem);

	public void renderHivStatus(StatusItem hivStatus);

	public void renderCd4Count(StatusItem cd4Count);

	public void renderRegimen(StatusItem regimen);

	public void renderCurrentRegimen(StatusItem currentRegimen);

	public void renderArtTreatment(StatusItem artTreatment);

}
