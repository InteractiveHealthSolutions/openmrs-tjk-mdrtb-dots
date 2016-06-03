package org.openmrs.module.dotsreports.regimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Regimen Suggestion used by the MDR-TB module to define standard regimens
 */
public class RegimenSuggestion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //***** CONSTRUCTORS *****
    
    public RegimenSuggestion() {}
    
    //***** PROPERTIES *****
    
    private String codeName;
    private String displayName;
	private String reasonForStarting;
    private List<DrugSuggestion> drugComponents;
    
	//***** INSTANCE METHODS *****
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegimenSuggestion) {
			RegimenSuggestion that = (RegimenSuggestion)obj;
			return that.getCodeName() != null && that.getCodeName().equals(this.getCodeName());
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getCodeName().hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getCodeName();
	}
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the codeName
	 */
	public String getCodeName() {
		return codeName;
	}
	/**
	 * @param codeName the codeName to set
	 */
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the reasonForStarting
	 */
	public String getReasonForStarting() {
		return reasonForStarting;
	}
	/**
	 * @param reasonForStarting the reasonForStarting to set
	 */
	public void setReasonForStarting(String reasonForStarting) {
		this.reasonForStarting = reasonForStarting;
	}
	/**
	 * @return the drugSuggestionList
	 */
	public List<DrugSuggestion> getDrugComponents() {
		if (drugComponents == null) {
			drugComponents = new ArrayList<DrugSuggestion>();
		}
		return drugComponents;
	}
	/**
	 * @param drugComponents the drugComponents to set
	 */
	public void setDrugComponents(List<DrugSuggestion> drugComponents) {
		this.drugComponents = drugComponents;
	}
	/**
	 * @param drugSuggestion the suggestion to add
	 */
	public void addDrugComponent(DrugSuggestion drugComponent) {
		getDrugComponents().add(drugComponent);
	}
}
