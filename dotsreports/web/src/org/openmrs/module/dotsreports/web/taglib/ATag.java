package org.openmrs.module.dotsreports.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ATag extends TagSupport {

    private static final long serialVersionUID = 1L;
    
	private final Log log = LogFactory.getLog(getClass());
    
    private String href;
    
    public int doStartTag() {
    	
    	if (StringUtils.isNotBlank(this.href)) {
    		String ret = "<a href=\"" + href + "\">";
		
    		try {
    			JspWriter w = pageContext.getOut();
    			w.println(ret);
    		}
    		catch (IOException ex) {
    			log.error("Error while starting flag result tag", ex);
    		}
    	}
    	
    	return EVAL_BODY_INCLUDE;
	}
	
	public int doEndTag() {
		
		if (StringUtils.isNotBlank(this.href)) {
			String ret = "</a>";
			try {
				JspWriter w = pageContext.getOut();
				w.println(ret);
			}
			catch (IOException ex) {
				log.error("Error while starting flag result tag", ex);
			}
		}
			
	    this.href = null;
	    return EVAL_PAGE;
	}

	public void setHref(String href) {
	    this.href = href;
    }

	public String getHref() {
	    return href;
    }
}
