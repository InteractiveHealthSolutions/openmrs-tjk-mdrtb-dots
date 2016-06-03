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
package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsUtil;

public class RegimenPortletTag extends ImportSupport {
	
	public static final long serialVersionUID = 21L;
	protected final static Log log = LogFactory.getLog(RegimenPortletTag.class);

	//***** PROPERTIES *****
	
	private String id;
	private Integer patientId;
	private String type;
	private RegimenHistory history;
	private Date changeDate;
	private String parameters;

	//***** INSTANCE METHODS *****
	
	/**
	 * @return pageContext
	 */
	public PageContext getPageContext() {
		return this.pageContext;
	}
	
	/**
	 * @see ImportSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		try {
			id = ObjectUtil.nvlStr(id, "");
			if (ObjectUtil.isNull(url) || (patientId == null && history == null)) {
				String message = "RegimenPortletTag must be passed a url and either a patientId or history attribute";
				log.warn(message);
				pageContext.getOut().print(message);
				return super.doStartTag();
			}
			
			if (!url.endsWith("portlet")) {
				url += ".portlet";
			}
			url = "/module/mdrtb/regimen/" + url;
			pageContext.getOut().print("<div class='portlet' id='" + id + "'>");
			
			pageContext.getRequest().setAttribute("org.openmrs.portlet.id", id);
			pageContext.getRequest().setAttribute("org.openmrs.portlet.patientId", patientId);
			pageContext.getRequest().setAttribute("org.openmrs.module.mdrtb.portlet.regimenType", type);
			pageContext.getRequest().setAttribute("org.openmrs.module.mdrtb.portlet.regimenHistory", history);
			pageContext.getRequest().setAttribute("org.openmrs.module.mdrtb.portlet.changeDate", changeDate);
			pageContext.getRequest().setAttribute("org.openmrs.portlet.parameters", getParameterMap());
		}
		catch (IOException e) {
			log.error("Error while closing portlet tag", e);
		}
		return super.doStartTag();
	}
	
	/**
	 * @see ImportSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
		int i = super.doEndTag();
		try {
			pageContext.getOut().print("</div>");
			patientId = null;
			history = null;
			url = null;
			parameters = null;
		}
		catch (IOException e) {
			log.error("Error while closing portlet tag", e);
		}
		return i;
	}
	
	/**
	 * @return the parameters in Map form
	 */
	public Map<String, String> getParameterMap() {
		return OpenmrsUtil.parseParameterList(parameters);
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the patientId
	 */
	public Integer getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

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
	 * @return the changeDate
	 */
	public Date getChangeDate() {
		return changeDate;
	}

	/**
	 * @param changeDate the changeDate to set
	 */
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
}
