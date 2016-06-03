package org.openmrs.module.dotsreports.regimen;

import java.io.Serializable;

/**
 * Represents a single drug or generic within a Regimen Suggestion
 */
public class DrugSuggestion implements Serializable {
    
	private static final long serialVersionUID = 1L;
    
    //***** CONSTRUCTORS *****
    
    public DrugSuggestion() {}
    
    //***** PROPERTY ACCESS *****
    
    private String generic;
    private String drugId;
    private String dose;
    private String units;
    private String frequency;
    private String instructions;
    private boolean asNeeded = false;

	//***** PROPERTIES *****

	/**
	 * @return the generic
	 */
	public String getGeneric() {
		return generic;
	}
	/**
	 * @param generic the generic to set
	 */
	public void setGeneric(String generic) {
		this.generic = generic;
	}
	/**
	 * @return the drugId
	 */
	public String getDrugId() {
		return drugId;
	}
	/**
	 * @param drugId the drugId to set
	 */
	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}
	/**
	 * @return the dose
	 */
	public String getDose() {
		return dose;
	}
	/**
	 * @param dose the dose to set
	 */
	public void setDose(String dose) {
		this.dose = dose;
	}
	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}
	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the instructions
	 */
	public String getInstructions() {
		return instructions;
	}
	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	/**
	 * @return the asNeeded
	 */
	public boolean isAsNeeded() {
		return asNeeded;
	}
	/**
	 * @param asNeeded the asNeeded to set
	 */
	public void setAsNeeded(boolean asNeeded) {
		this.asNeeded = asNeeded;
	}
}
