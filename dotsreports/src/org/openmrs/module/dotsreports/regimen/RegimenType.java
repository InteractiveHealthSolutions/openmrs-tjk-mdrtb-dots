package org.openmrs.module.dotsreports.regimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the metadata for how to group Drug Orders together to construct Regimens
 */
public class RegimenType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//***** PROPERTIES *****

	private String name;
	private String drugSet;
	private String reasonForStartingQuestion;
	private String reasonForStoppingQuestion;
	private List<RegimenSuggestion> suggestions;
 
	//***** CONSRUCTORS *****
	
	public RegimenType() {}
	
	public RegimenType(String name) {
		this.name = name;
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegimenType) {
			RegimenType that = (RegimenType)obj;
			return that.getName() != null && that.getName().equals(this.getName());
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the drugSet
	 */
	public String getDrugSet() {
		return drugSet;
	}

	/**
	 * @param drugSet the drugSet to set
	 */
	public void setDrugSet(String drugSet) {
		this.drugSet = drugSet;
	}

	/**
	 * @return the reasonForStartingQuestion
	 */
	public String getReasonForStartingQuestion() {
		return reasonForStartingQuestion;
	}

	/**
	 * @param reasonForStartingQuestion the reasonForStartingQuestion to set
	 */
	public void setReasonForStartingQuestion(String reasonForStartingQuestion) {
		this.reasonForStartingQuestion = reasonForStartingQuestion;
	}

	/**
	 * @return the reasonForStoppingQuestion
	 */
	public String getReasonForStoppingQuestion() {
		return reasonForStoppingQuestion;
	}

	/**
	 * @param reasonForStoppingQuestion the reasonForStoppingQuestion to set
	 */
	public void setReasonForStoppingQuestion(String reasonForStoppingQuestion) {
		this.reasonForStoppingQuestion = reasonForStoppingQuestion;
	}

	/**
	 * @return the suggestions
	 */
	public List<RegimenSuggestion> getSuggestions() {
		if (suggestions == null) {
			suggestions = new ArrayList<RegimenSuggestion>();
		}
		return suggestions;
	}

	/**
	 * @param suggestions the suggestions to set
	 */
	public void setSuggestions(List<RegimenSuggestion> suggestions) {
		this.suggestions = suggestions;
	}
}