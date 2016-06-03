package org.openmrs.module.mdrtb.status;

import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.regimen.Regimen;


public class NullTreatmentStatusRenderer implements TreatmentStatusRenderer {

    public String renderRegimen(Regimen regimen) {
	    return null;
    }

    public String renderTreatmentState(TreatmentState state) {
	    return null;
    }

}
