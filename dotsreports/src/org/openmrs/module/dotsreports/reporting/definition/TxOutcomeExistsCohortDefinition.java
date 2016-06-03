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
package org.openmrs.module.dotsreports.reporting.definition;

import java.util.Date;


import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("mdrtb.reporting.TxOutcomeExistsCohortDefinition")
public class TxOutcomeExistsCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;
    
	@ConfigurationProperty(group="resultDateGroup")
	private Date minResultDate;
	
	@ConfigurationProperty(group="resultDateGroup")
	private Date maxResultDate;
	
	private String outcomeType;
	
	
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public TxOutcomeExistsCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the minResultDate
	 */
	public Date getMinResultDate() {
		return minResultDate;
	}

	/**
	 * @param minResultDate the minResultDate to set
	 */
	public void setMinResultDate(Date minResultDate) {
		this.minResultDate = minResultDate;
	}

	/**
	 * @return the maxResultDate
	 */
	public Date getMaxResultDate() {
		return maxResultDate;
	}

	/**
	 * @param maxResultDate the maxResultDate to set
	 */
	public void setMaxResultDate(Date maxResultDate) {
		this.maxResultDate = maxResultDate;
	}

	public String getOutcomeType() {
		return outcomeType;
	}

	public void setOutcomeType(String outcomeType) {
		this.outcomeType = outcomeType;
	}

	

	
}
