/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dotsreports.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Encapsulates the logic for rendering a modal dialog
 */
public class ModalDialogTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	protected static final Log log = LogFactory.getLog(ModalDialogTag.class);
	
	//***** PROPERTIES *****

	private String sourceId;						// The id of the element that opens the dialog
	private String targetId;						// The id of the element to open in the dialog
	private String event;							// The event to bind to
	private Boolean modal = false;
	private Boolean autoOpen = false;
	private Boolean draggable = false;
	private Boolean resizable = false;
	private Boolean closeOnEscape = false;
	private String width = "90%";
	private String height = null;
	private String position = "top";
	private Integer zIndex = 100;
	private String title = "";

	//***** INSTANCE METHODS *****
	
    /**
     * @see Tag#doStartTag()
     */
	public int doStartTag() throws JspException {
		StringBuilder sb = new StringBuilder();
		sb.append("<script type=\"text/javascript\">");
		sb.append("	$j(document).ready(function(){");
		sb.append("		$j('#" + targetId + "').dialog({");
		sb.append("			modal: " + ObjectUtil.nvl(modal, true) + ",");
		sb.append("			autoOpen: " + ObjectUtil.nvl(autoOpen, false) + ",");
		sb.append("			draggable: " + ObjectUtil.nvl(draggable, false) + ",");
		sb.append("			resizable: " + ObjectUtil.nvl(resizable, false) + ",");
		sb.append("			closeOnEscape: " + ObjectUtil.nvl(closeOnEscape, false) + ",");
		sb.append("			width: '" + ObjectUtil.nvl(width, "90%") + "',");
		sb.append("			height: " + ObjectUtil.nvlStr(height, "$j(window).height()-50") + ",");
		sb.append("			position:  '" + ObjectUtil.nvlStr(position, "top") + "',");
		sb.append("			zIndex: " + ObjectUtil.nvlStr(zIndex, 100) + ",");
		sb.append("			title: '" + ObjectUtil.nvlStr(title, "") + "'");
		sb.append("		});");
		if (ObjectUtil.notNull(sourceId)) {
			sb.append("$j('#" + sourceId + "')." + ObjectUtil.nvlStr(event, "click") + "(function(event){");
			sb.append("		$j('#" + targetId + "').dialog('open');");
			sb.append("});");
		}
		sb.append("});");
		sb.append("</script>");
		
		try {
			JspWriter w = pageContext.getOut();
			w.println(sb);
		}
		catch (IOException ex) {
			log.error("Error while starting duration tag", ex);
		}
		return SKIP_BODY;
	}
	
    /**
     * @see Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
    	modal = false;
    	autoOpen = false;
    	draggable = false;
    	resizable = false;
    	closeOnEscape = false;
    	width = "90%";
    	height = null;
    	position = "top";
    	zIndex = 100;
    	title = "";
	    return EVAL_PAGE;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}

	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}
	
	/**
	 * @return the modal
	 */
	public Boolean getModal() {
		return modal;
	}

	/**
	 * @param modal the modal to set
	 */
	public void setModal(Boolean modal) {
		this.modal = modal;
	}

	/**
	 * @return the autoOpen
	 */
	public Boolean getAutoOpen() {
		return autoOpen;
	}

	/**
	 * @param autoOpen the autoOpen to set
	 */
	public void setAutoOpen(Boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	/**
	 * @return the draggable
	 */
	public Boolean getDraggable() {
		return draggable;
	}

	/**
	 * @param draggable the draggable to set
	 */
	public void setDraggable(Boolean draggable) {
		this.draggable = draggable;
	}

	/**
	 * @return the resizable
	 */
	public Boolean getResizable() {
		return resizable;
	}

	/**
	 * @param resizable the resizable to set
	 */
	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	/**
	 * @return the closeOnEscape
	 */
	public Boolean getCloseOnEscape() {
		return closeOnEscape;
	}

	/**
	 * @param closeOnEscape the closeOnEscape to set
	 */
	public void setCloseOnEscape(Boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the zIndex
	 */
	public Integer getzIndex() {
		return zIndex;
	}

	/**
	 * @param zIndex the zIndex to set
	 */
	public void setzIndex(Integer zIndex) {
		this.zIndex = zIndex;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
