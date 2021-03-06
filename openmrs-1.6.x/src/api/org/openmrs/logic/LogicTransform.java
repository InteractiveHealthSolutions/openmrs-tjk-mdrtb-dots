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
package org.openmrs.logic;

import org.openmrs.logic.op.Count;
import org.openmrs.logic.op.First;
import org.openmrs.logic.op.Last;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.op.TransformOperator;
import org.openmrs.logic.result.Result;

/**
 * LogicTransform is internal representation of the transformation applied to the LogicCriteria <br /><br />
 * 
 * Example: <br />
 * <code>count()</code> will apply the {@link Count} operator and return the total number of {@link Result} instead of the Result itself
 * <code>first(2)</code> will apply the {@link First} operator and return the first two {@link Result} out of the entire Result
 * 
 */
public class LogicTransform {
	
	private Operator transformOperator = null;
	
	private Integer numResults = null;
	
	private String sortColumn = null;
	
	public LogicTransform(Operator transformOperator, Integer numResults) {
		this.transformOperator = transformOperator;
		this.numResults = numResults;
	}
	
	public LogicTransform(Operator transformOperator) {
		this.transformOperator = transformOperator;
	}
	
	/**
	 * Get the {@link TransformOperator} in this LogicTransform object
	 * 
	 * @return the current TransformOperator
	 */
	public Operator getTransformOperator() {
		return transformOperator;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		if (transformOperator != null) {
			result.append(transformOperator);
		}
		
		if (numResults != null) {
			result.append(" " + numResults);
		}
		
		if (sortColumn != null) {
			result.append(" ordered by " + sortColumn);
		}
		return result.toString();
	}
	
	/**
	 * Get number of {@link Result} object should be returned by the current criteria. <br>
	 * Only for {@link First} and {@link Last}
	 */
	public Integer getNumResults() {
		return numResults;
	}
	
	public String getSortColumn() {
		return sortColumn;
	}
	
	/**
	 * Set number of {@link Result} object should be returned by the current criteria. <br>
	 * Only for {@link First} and {@link Last}
	 * 
	 * @param numResults the total Result expected from this criteria
	 */
	public void setNumResults(Integer numResults) {
		this.numResults = numResults;
	}
	
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LogicTransform)) {
			return false;
		}
		
		LogicTransform compTransform = (LogicTransform) obj;
		
		if (!safeEquals(this.transformOperator, compTransform.getTransformOperator())) {
			return false;
		}
		
		if (!safeEquals(numResults, compTransform.getNumResults())) {
			return false;
		}
		
		if (!safeEquals(sortColumn, compTransform.getSortColumn())) {
			return false;
		}
		
		return true;
	}
	
	private boolean safeEquals(Object a, Object b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transformOperator == null) ? 0 : transformOperator.hashCode());
		result = prime * result + ((numResults == null) ? 0 : numResults.hashCode());
		result = prime * result + ((sortColumn == null) ? 0 : sortColumn.hashCode());
		return result;
	}
}
