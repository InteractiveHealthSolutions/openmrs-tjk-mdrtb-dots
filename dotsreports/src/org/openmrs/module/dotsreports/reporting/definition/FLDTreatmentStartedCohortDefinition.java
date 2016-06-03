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

@Localized("dotsreports.reporting.FLDTreatmentStartedCohortDefinition")
public class FLDTreatmentStartedCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date fromDate;
	
	@ConfigurationProperty(group="startDateGroup")
	private Date toDate;
	
	//private String drugSet;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public FLDTreatmentStartedCohortDefinition() {
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
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

//	public String getDrugSet() {
//		return drugSet;
//	}
//
//	public void setDrugSet(String drugSet) {
//		this.drugSet = drugSet;
//	}

}
