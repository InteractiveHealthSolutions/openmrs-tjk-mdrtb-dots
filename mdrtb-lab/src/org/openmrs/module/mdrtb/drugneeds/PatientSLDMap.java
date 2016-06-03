package org.openmrs.module.mdrtb.drugneeds;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Patient;

public class PatientSLDMap {
	
	private Patient patient;
	private Date treatmentStartDate;
	
	private Boolean onCapreomycin;
	private Boolean onAmikacin;
	private Boolean onMoxifloxacin;
	private Boolean onLevofloxacin;
	private Boolean onProthionamide;
	private Boolean onCycloserine;
	private Boolean onPAS;
	private Boolean onPyrazinamide;
	private Boolean onEthambutol;
	private Boolean onOther1;
	private Boolean onOther2;
	private Boolean onOther3;
	private Boolean onOther4;
	private Boolean onOther5;

	
	
	public PatientSLDMap(Patient patient, Date treatmentStartDate,
			Boolean onCapreomycin, Boolean onAmikacin, Boolean onMoxifloxacin,
			Boolean onLevofloxacin, Boolean onProthionamide,
			Boolean onCycloserine, Boolean onPAS, Boolean onPyrazinamide,
			Boolean onEthambutol, Boolean onOther1, Boolean onOther2,
			Boolean onOther3, Boolean onOther4, Boolean onOther5) {
		super();
		this.patient = patient;
		this.treatmentStartDate = treatmentStartDate;
		this.onCapreomycin = onCapreomycin;
		this.onAmikacin = onAmikacin;
		this.onMoxifloxacin = onMoxifloxacin;
		this.onLevofloxacin = onLevofloxacin;
		this.onProthionamide = onProthionamide;
		this.onCycloserine = onCycloserine;
		this.onPAS = onPAS;
		this.onPyrazinamide = onPyrazinamide;
		this.onEthambutol = onEthambutol;
		this.onOther1 = onOther1;
		this.onOther2 = onOther2;
		this.onOther3 = onOther3;
		this.onOther4 = onOther4;
		this.onOther5 = onOther5;
	}

	
	
	public PatientSLDMap() {
		patient = null;
		treatmentStartDate = null;
		onCapreomycin = false;
		onAmikacin = false;
		onMoxifloxacin = false;
		onLevofloxacin = false;
		onProthionamide = false;
		onCycloserine = false;
		onPAS= false;
		onPyrazinamide = false;
		onEthambutol = false;
		onOther1 = false;
		onOther2 = false;
		onOther3 = false;
		onOther4 = false;
		onOther5 = false;
	
	}



	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Date getTreatmentStartDate() {
		return treatmentStartDate;
	}

	public void setTreatmentStartDate(Date treatmentStartDate) {
		this.treatmentStartDate = treatmentStartDate;
	}

	public Boolean getOnCapreomycin() {
		return onCapreomycin;
	}

	public void setOnCapreomycin(Boolean onCapreomycin) {
		this.onCapreomycin =onCapreomycin;
	}

	public Boolean getOnAmikacin() {
		return onAmikacin;
	}

	public void setOnAmikacin(Boolean onAmikacin) {
		this.onAmikacin = onAmikacin;
	}

	public Boolean getOnMoxifloxacin() {
		return onMoxifloxacin;
	}

	public void setOnMoxifloxacin(Boolean onMoxifloxacin) {
		this.onMoxifloxacin = onMoxifloxacin;
	}

	public Boolean getOnLevofloxacin() {
		return onLevofloxacin;
	}

	public void setOnLevofloxacin(Boolean onLevofloxacin) {
		this.onLevofloxacin = onLevofloxacin;
	}

	public Boolean getOnProthionamide() {
		return onProthionamide;
	}

	public void setOnProthionamide(Boolean onProthionamide) {
		this.onProthionamide = onProthionamide;
	}

	public Boolean getOnCycloserine() {
		return onCycloserine;
	}

	public void setOnCycloserine(Boolean onCycloserine) {
		this.onCycloserine = onCycloserine;
	}

	public Boolean getOnPAS() {
		return onPAS;
	}

	public void setOnPAS(Boolean onPAS) {
		this.onPAS = onPAS;
	}

	public Boolean getOnPyrazinamide() {
		return onPyrazinamide;
	}

	public void setOnPyrazinamide(Boolean onPyrazinamide) {
		this.onPyrazinamide = onPyrazinamide;
	}

	public Boolean getOnEthambutol() {
		return onEthambutol;
	}

	public void setOnEthambutol(Boolean onEthambutol) {
		this.onEthambutol = onEthambutol;
	}

	public Boolean getOnOther1() {
		return onOther1;
	}

	public void setOnOther1(Boolean onOther1) {
		this.onOther1 = onOther1;
	}

	public Boolean getOnOther2() {
		return onOther2;
	}

	public void setOnOther2(Boolean onOther2) {
		this.onOther2 = onOther2;
	}

	public Boolean getOnOther3() {
		return onOther3;
	}

	public void setOnOther3(Boolean onOther3) {
		this.onOther3 = onOther3;
	}

	public Boolean getOnOther4() {
		return onOther4;
	}

	public void setOnOther4(Boolean onOther4) {
		this.onOther4 = onOther4;
	}

	public Boolean getOnOther5() {
		return onOther5;
	}

	public void setOnOther5(Boolean onOther5) {
		this.onOther5 = onOther5;
	}

	/*public HashMap<String, Boolean> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Boolean> map) {
		this.map = map;
	}
	
	public void put(String key, Boolean value) {
		map.put(key, value);
	}
	
	public Boolean get(String key) {
		return map.get(key);
	}*/
	


}
