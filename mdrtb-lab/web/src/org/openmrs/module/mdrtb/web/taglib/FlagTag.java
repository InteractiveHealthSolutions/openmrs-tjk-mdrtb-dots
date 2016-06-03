package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mdrtb.status.StatusItem;


public class FlagTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    
	private final Log log = LogFactory.getLog(getClass());
    
    private StatusItem item;
    
    public int doStartTag() {
    	
		String ret = "";
		
		if (item.getFlagged()) {
			ret = "**";
		}
		
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting flag result tag", ex);
		}
    	
    	return SKIP_BODY;
	}
	
	public int doEndTag() {
	    item = null;
	    return EVAL_PAGE;
	}

	public void setItem(StatusItem item) {
	    this.item = item;
    }

	public StatusItem getItem() {
	    return item;
    }

}
