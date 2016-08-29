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
package org.openmrs.module.mdrtb.reporting.definition;

import java.util.Date;


import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("mdrtb.reporting.SLDRegimenTypeCohortDefinition")
public class SLDRegimenTypeCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;
    
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private Concept regType;
	
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SLDRegimenTypeCohortDefinition() {
		super();
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



	public Concept getRegType() {
		return regType;
	}



	public void setRegType(Concept regType) {
		this.regType = regType;
	}
	
	



	
}
