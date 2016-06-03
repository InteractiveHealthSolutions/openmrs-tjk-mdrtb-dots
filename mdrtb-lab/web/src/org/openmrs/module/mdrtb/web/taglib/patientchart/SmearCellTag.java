package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.util.List;

import org.openmrs.module.mdrtb.specimen.Bacteriology;


public class SmearCellTag extends AbstractBacteriologyCellTag {

    private static final long serialVersionUID = 1L;

    private List<Bacteriology> smears;

    public int doStartTag() {
    	renderCell(smears);
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	clearParameters();
    	smears = null;
    	return EVAL_PAGE;
    }
    
    
	public void setSmears(List<Bacteriology> smears) {
	    this.smears = smears;
    }

	public List<Bacteriology> getSmears() {
	    return smears;
    }
}
