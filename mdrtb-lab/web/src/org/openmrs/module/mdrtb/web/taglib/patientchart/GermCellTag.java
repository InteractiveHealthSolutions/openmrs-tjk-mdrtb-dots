package org.openmrs.module.mdrtb.web.taglib.patientchart;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Culture;


public class GermCellTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    
	private final Log log = LogFactory.getLog(getClass());

    private List<Culture> cultures;

    public int doStartTag() {
    	
    	String ret = "";
    		
    	Concept organismType = null;
    	String organismTypeNonCoded = "";
    	
    	// run through the cultures and pull out the organism type
    	for(Culture culture : cultures) {
    		if(culture.getOrganismType() != null) {
    			if(organismType == null) {
    				organismType = culture.getOrganismType();
    				if(organismType.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MYCOBACTERIA_NON_CODED))) {
    					if(StringUtils.isEmpty(organismTypeNonCoded)) {
    						organismTypeNonCoded = culture.getOrganismTypeNonCoded();
    					}
    				}
    			}
    			// if we have a conflict between two cultures, report a conflict
    			else if(!organismType.equals(culture.getOrganismType()) || 
    				(StringUtils.isNotEmpty(organismTypeNonCoded) && !organismTypeNonCoded.equals(culture.getOrganismTypeNonCoded())) ) {
    				organismType = null;
    				ret = "Multiple";
    				break;
    			}
    		}
    	}
    	
    	if(organismType != null) {
    		if(organismType.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_MYCOBACTERIA_NON_CODED))) {
    			ret = organismTypeNonCoded;
    		}
    		else {
    			ret = organismType.getBestShortName(Context.getLocale()).toString();
    		}
    	}
    	
    	try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting dst result tag", ex);
		}
    	
    	return SKIP_BODY;
    }
    
    
    public int doEndTag() {
    	cultures= null;
    	return EVAL_PAGE;
    }
    
    
	public void setCultures(List<Culture> cultures) {
	    this.cultures = cultures;
    }

	public List<Culture> getCultures() {
	    return cultures;
    }

    
    
}
