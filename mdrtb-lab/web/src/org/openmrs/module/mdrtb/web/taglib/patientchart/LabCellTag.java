package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.Test;


public class LabCellTag extends TagSupport{
	
	private static final long serialVersionUID = 1L;
    
	private final Log log = LogFactory.getLog(getClass());

    private Specimen specimen;

    public int doStartTag() {
    	
    	StringBuffer ret = new StringBuffer();
    	
    	// determine all the lab locations associated with this specimen
    	Set<Location> labs = new HashSet<Location>();
    	
    	for (Test test : specimen.getTests()) {
    		labs.add(test.getLab());
    	}
    	
    	// hack to remove the "Unknown Location" location if it exists
    	Location unknown = Context.getLocationService().getLocation("Unknown Location");
    	if (unknown != null && labs.contains(unknown)) {
    		labs.remove(unknown);
    	}
    	
    	// now create the display string
    	for (Location lab : labs) {
    		ret.append(lab.getDisplayString() + ", ");
    	}
    	
    	// chop off the trailing comma
    	if (ret.length() > 1) {
    		ret.delete(ret.length() - 2, ret.length());
    	}
    		
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting location cell tag", ex);
		}
    	
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	setSpecimen(null);
    	return EVAL_PAGE;
    }


	public void setSpecimen(Specimen specimen) {
	    this.specimen = specimen;
    }


	public Specimen getSpecimen() {
	    return specimen;
    }
    
    
}
