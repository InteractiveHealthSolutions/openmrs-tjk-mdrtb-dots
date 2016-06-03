package org.openmrs.module.mdrtb.status;

import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.regimen.Regimen;


public interface TreatmentStatusRenderer {

	public String renderRegimen(Regimen regimen);

	public String renderTreatmentState(TreatmentState state);

}
