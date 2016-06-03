package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

public class RegimenHistoryTag extends TagSupport {

    public static final long serialVersionUID = 1L;
    protected final static Log log = LogFactory.getLog(RegimenHistoryTag.class);

    //***** PROPERTIES *****
    
    private RegimenHistory history;
    private String messagePrefix;
    private String cssClass;
    private String graphicResourcePath;
    private String dateFormat;
    private String activeCssClass;
    private String futureCssClass;
    private String editLink;
    private boolean invert = true;
    private boolean timeDescending = false;
    
    //***** INSTANCE METHDOS *****
    
	/**
	 * @see TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		StringBuilder sb = new StringBuilder();
		
		DateFormat df = (ObjectUtil.isNull(dateFormat) ? Context.getDateFormat() : new SimpleDateFormat(dateFormat, Context.getLocale()));
		String activeCss = ObjectUtil.nvlStr(activeCssClass, "activeRegimenRow");
		String futureCss = ObjectUtil.nvlStr(futureCssClass, "futureRegimenRow");
		String prefix = ObjectUtil.nvlStr(messagePrefix, "mdrtb.");
        String noneTitle = MessageUtil.translate(prefix + "none", "None");
        String dateTitle = MessageUtil.translate(prefix + "date", "Date");
        String durationTitle = MessageUtil.translate(prefix + "duration", "Duration");
        String typeTitle = MessageUtil.translate(prefix + "treatmentType", "Type");
        String futureTitle = MessageUtil.translate(prefix + "future", "Future");
        String daysTitle = MessageUtil.translate(prefix + "days", "days");
		
		List<Regimen> allRegimens = new ArrayList<Regimen>();
		if (history != null) {
			allRegimens = history.getAllRegimens();
		}
		if (allRegimens.isEmpty()) {
			sb.append(noneTitle);
		}
		else {
			// Order the Regimen entries appropriately
	        if (timeDescending) {
	        	Collections.reverse(allRegimens);
	        }
	        
	        // Construct sorted Map from Drug generic display name to Concept for all relevant drugs
	        Map<String, Concept> allDrugs = new TreeMap<String, Concept>();
	        for (Regimen r : allRegimens) {
	        	for (Concept c : r.getUniqueGenerics()) {
	        		allDrugs.put(c.getBestShortName(Context.getLocale()).getName(), c);
	        	}
	        }
			
			// Get the text or image to display when a drug is present
	        String drugIndicator = "X";
	        if (ObjectUtil.notNull(graphicResourcePath)) {
	        	drugIndicator = "<img src='" + graphicResourcePath + "' alt='X'>";
			}
	        
	        if (invert) { //time goes down
	        	
		        sb.append("<table class='" + cssClass + "'><tbody>");
		        sb.append("<tr><th>" + dateTitle + "</th>");
		        
	            for (String s : allDrugs.keySet()) {
	                sb.append("<th>" + s + "</th>");
	            }
	            sb.append("<th>" + durationTitle + "</th><th>" + typeTitle + "</th></tr>");
	            for (int i=0; i<allRegimens.size(); i++) {
	            	Regimen regimen = allRegimens.get(i);
	            	
	            	String rowClass = (i%2 == 0 ? "evenRow" : "oddRow");
	            	rowClass += (regimen.isFuture() ? " " + futureCss : regimen.isActive() ? " " + activeCss : "");
	
	                sb.append("<tr class='" + rowClass + "'>");
	                sb.append("<th>" + dateLink(regimen.getStartDate(), df) + "</th>");
	                
	                // If there are no active drugs in this Regimen, format this specially
	                if (regimen.getDrugOrders().isEmpty()) {
	                	sb.append("<td colspan=\"" + (allDrugs.size() + 2) + "\">" + noneTitle + "</td><tr>");
	                }
	                else {
	                	for (Concept c : allDrugs.values()) {
	                		sb.append("<td>" + (regimen.containsGeneric(c) ? drugIndicator : " &nbsp;") + "</td>");
	                	}
	                	sb.append("<td>" + (regimen.isFuture() ? futureTitle : regimen.getDurationInDays() + " " + daysTitle) + "</td>");

	                	sb.append("<td style=\"text-align:left;\" width=\"100%\">" + RegimenUtils.formatCodedObs(regimen.getReasonForStarting(), "") + "</td></tr>");
	                }
	            }
	            sb.append("</tbody></table>");
	        } 
	        else {  //time goes horizontal
	        	
		        sb.append("<table class='" + cssClass + "'><tbody>");

		        List<List<String>> rowData = new ArrayList<List<String>>();
		        int activeCol = -1;
		        
		        // Date Row
		        List<String> dateRow = new ArrayList<String>();
		        dateRow.add(dateTitle);
	            for (int i=0; i<allRegimens.size(); i++) {
	            	Regimen r = allRegimens.get(i);
	            	dateRow.add(dateLink(r.getStartDate(), df));
	            	if (r.isActive()) {
	            		activeCol = i+1;
	            	}
	            }
	            rowData.add(dateRow);
	            
	            // Drug Rows
	            for (String s : allDrugs.keySet()) {
	            	List<String> drugRow = new ArrayList<String>();
	            	drugRow.add(s);
	            	for (Regimen r : allRegimens) {
	            		drugRow.add((r.containsGeneric(allDrugs.get(s)) ? drugIndicator : " &nbsp;"));
	            	}
	            	rowData.add(drugRow);
	            }
	            
	            // Duration Row
	            List<String> durationRow = new ArrayList<String>();
	            durationRow.add(durationTitle);
	            for (Regimen r : allRegimens) {
	            	durationRow.add((r.isFuture() ? futureTitle : r.getDurationInDays() + " " + daysTitle));
	            }
	            rowData.add(durationRow);
	            
	            // Type Row
	            List<String> typeRow = new ArrayList<String>();
	            typeRow.add(typeTitle);
	            for (Regimen r : allRegimens) {
	            	typeRow.add(RegimenUtils.formatCodedObs(r.getReasonForStarting(), ""));
	            }
	            rowData.add(typeRow);         
	            
	            // Render Rows
	            for (int i=0; i<rowData.size(); i++) {
	            	List<String> row = rowData.get(i);
	            	
	            	String rowClass = (i%2 == 0 ? "oddRow" : "evenRow");
	            	
	            	sb.append("<tr class='" + rowClass + "'>");
	            	for (int j=0; j<row.size(); j++) {
	            		String cell = row.get(j);
		              	String cellClass = j==activeCol ? activeCss : rowClass;
	            		if (i == 0 || j == 0) {
	            			sb.append("<th class='" + cellClass + "'>" + cell + "</th>");
	            		}
	            		else {
	            			sb.append("<td class='" + cellClass + "'>" + cell + "</td>");
	            		}
	            	}
	            	sb.append("</tr>");
	            }
	            sb.append("</tbody></table>");
	        }
	    }

        try {
            pageContext.getOut().write(sb.toString());
        } 
        catch (IOException e) {
            log.error("Could not write to pageContext", e);
        }
        
        release();
        return SKIP_BODY;
	}
	
	/**
	 * @see TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
        history = null;
        messagePrefix = null;
        cssClass = null;
        graphicResourcePath = null;
        dateFormat = null;
        activeCssClass = null;
        futureCssClass = null;
        invert = true;
        timeDescending = false;
        return EVAL_PAGE;
	}
	
	/**
	 * @return the edit link for the given Date
	 */
	private String dateLink(Date date, DateFormat displayDateFormat) {
		if (ObjectUtil.isNull(getEditLink())) {
			return displayDateFormat.format(date);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"" + editLink + Context.getDateFormat().format(date) + "\">");
		sb.append(displayDateFormat.format(date));
		sb.append("</a>");
		return sb.toString();
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the history
	 */
	public RegimenHistory getHistory() {
		return history;
	}
	/**
	 * @param history the history to set
	 */
	public void setHistory(RegimenHistory history) {
		this.history = history;
	}
	/**
	 * @return the messagePrefix
	 */
	public String getMessagePrefix() {
		return messagePrefix;
	}
	/**
	 * @param messagePrefix the messagePrefix to set
	 */
	public void setMessagePrefix(String messagePrefix) {
		this.messagePrefix = messagePrefix;
	}
	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}
	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	/**
	 * @return the graphicResourcePath
	 */
	public String getGraphicResourcePath() {
		return graphicResourcePath;
	}
	/**
	 * @param graphicResourcePath the graphicResourcePath to set
	 */
	public void setGraphicResourcePath(String graphicResourcePath) {
		this.graphicResourcePath = graphicResourcePath;
	}
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * @return the activeCssClass
	 */
	public String getActiveCssClass() {
		return activeCssClass;
	}
	/**
	 * @param activeCssClass the activeCssClass to set
	 */
	public void setActiveCssClass(String activeCssClass) {
		this.activeCssClass = activeCssClass;
	}
	/**
	 * @return the futureCssClass
	 */
	public String getFutureCssClass() {
		return futureCssClass;
	}
	/**
	 * @param futureCssClass the futureCssClass to set
	 */
	public void setFutureCssClass(String futureCssClass) {
		this.futureCssClass = futureCssClass;
	}
	/**
	 * @return the editLink
	 */
	public String getEditLink() {
		return editLink;
	}
	/**
	 * @param editLink the editLink to set
	 */
	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}
	/**
	 * @return the invert
	 */
	public boolean isInvert() {
		return invert;
	}
	/**
	 * @param invert the invert to set
	 */
	public void setInvert(boolean invert) {
		this.invert = invert;
	}
	/**
	 * @return the timeDescending
	 */
	public boolean isTimeDescending() {
		return timeDescending;
	}
	/**
	 * @param timeDescending the timeDescending to set
	 */
	public void setTimeDescending(boolean timeDescending) {
		this.timeDescending = timeDescending;
	}
}
