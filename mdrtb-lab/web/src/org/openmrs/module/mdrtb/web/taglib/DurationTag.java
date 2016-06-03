package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mdrtb.exception.MdrtbAPIException;


public class DurationTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private final Log log = LogFactory.getLog(getClass());
    
    private Date startDate;
    
    private Date endDate;
    
    private String format;
    
    public int doStartTag() {
    	
		String ret = "";
		
		// only calculate a difference if both dates aren't null
		if (startDate != null && endDate != null) {
		
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
		
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);
		
			long diff = end.getTimeInMillis() - start.getTimeInMillis();
			long diffMinutes = diff / (60 * 1000);
			long diffHours = diff / (60 * 60 * 1000);
			long diffDays = diff / (24 * 60 * 60 * 1000);
		
			if (format.compareToIgnoreCase("minutes") == 0) {
				ret = String.valueOf(diffMinutes);
			}
			else if (format.compareToIgnoreCase("hours") == 0) {
				ret = String.valueOf(diffHours);
			}
			else if (format.compareToIgnoreCase("days") == 0) {
				ret = String.valueOf(diffDays);
			}
			else {
				throw new MdrtbAPIException("Invalid format specified for duration tag.");
			}
		}
		
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting duration tag", ex);
		}
    	
    	return SKIP_BODY;
	}
	
	public int doEndTag() {
	    startDate = null;
	    endDate = null;
	    format = null;
	    return EVAL_PAGE;
	}

	
    public Date getStartDate() {
    	return startDate;
    }

	
    public void setStartDate(Date startDate) {
    	this.startDate = startDate;
    }

	
    public Date getEndDate() {
    	return endDate;
    }

	
    public void setEndDate(Date endDate) {
    	this.endDate = endDate;
    }

	
    public String getFormat() {
    	return format;
    }

	
    public void setFormat(String format) {
    	this.format = format;
    }
    
}
