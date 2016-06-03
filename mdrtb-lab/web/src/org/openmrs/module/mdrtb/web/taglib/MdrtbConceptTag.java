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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.context.Context;

public class MdrtbConceptTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	private Concept concept;
	private String nameTag;
	private String nameVar;
	private String mappingVar;
	
	public int doStartTag() throws JspException {
		
		ConceptName n = concept.getName();
		
		if (StringUtils.isNotEmpty(nameTag)) {
			ConceptNameTag t = Context.getConceptService().getConceptNameTagByName(nameTag);
			n = concept.findNameTaggedWith(t);
		}

		if (nameVar != null) {
			pageContext.setAttribute(nameVar, n);
		}
		
		if (mappingVar != null) {
			for (ConceptMap m : concept.getConceptMappings()) {
				if (m.getSource().getName().equals("org.openmrs.module.mdrtb")) {
					pageContext.setAttribute(mappingVar, m.getSourceCode());
				}
			}
		}
		return EVAL_BODY_BUFFERED;
	}
	
    /**
     * @see Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        try {
            if (getBodyContent() != null) {
                getBodyContent().writeOut(getBodyContent().getEnclosingWriter());
            }
        }
        catch(java.io.IOException e)
        {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

	/**
	 * @return the nameTag
	 */
	public String getNameTag() {
		return nameTag;
	}

	/**
	 * @param nameTag the nameTag to set
	 */
	public void setNameTag(String nameTag) {
		this.nameTag = nameTag;
	}

	/**
	 * @return the nameVar
	 */
	public String getNameVar() {
		return nameVar;
	}

	/**
	 * @param nameVar the nameVar to set
	 */
	public void setNameVar(String nameVar) {
		this.nameVar = nameVar;
	}

	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * @return the mappingVar
	 */
	public String getMappingVar() {
		return mappingVar;
	}

	/**
	 * @param mappingVar the mappingVar to set
	 */
	public void setMappingVar(String mappingVar) {
		this.mappingVar = mappingVar;
	}
}
