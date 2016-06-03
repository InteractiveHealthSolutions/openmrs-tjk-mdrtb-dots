package org.openmrs.module.mdrtb.status;



public interface VisitStatusRenderer {

	public void renderVisit(StatusItem visit, VisitStatus status);

	public void renderNewIntakeVisit(StatusItem newIntakeVisit, VisitStatus status);

	public void renderNewFollowUpVisit(StatusItem newFollowUpVisit, VisitStatus status);

}
