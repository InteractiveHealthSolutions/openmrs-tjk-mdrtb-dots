package org.openmrs.module.dotsreports.reporting;

import java.util.Date;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class TB03Data {
	
	private Patient patient;
	private String identifier;
	private String tb03RegistrationDate;
	private Integer ageAtTB03Registration;
	private String dateOfBirth;
	private String intensivePhaseFacility;
	private String continuationPhaseFacility;
	private String treatmentRegimen;
	private String tb03TreatmentStartDate;
	private String siteOfDisease;
	private Integer regGroup;
	private String hivTestResult;
	private String hivTestDate;
	private String artStartDate;
	private String cpStartDate;
	private String diagnosticSmearResult;
	private String diagnosticSmearTestNumber;
	private String diagnosticSmearDate;
	private String xpertMTBResult;
	private String xpertRIFResult;
	private String xpertTestDate;
	private String xpertTestNumber;
	private String hainMTBResult;
	private String hainINHResult;
	private String hainRIFResult;
	private String hainTestDate;
	private String hainTestNumber;
	private String cultureResult;
	private String cultureTestDate;
	private String cultureTestNumber;
	private String drugResistance;
	private Integer tb03TreatmentOutcome;
	private String tb03TreatmentOutcomeDate;
	private Boolean diedOfTB;

	public TB03Data() {
		// TODO Auto-generated constructor stub
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTb03RegistrationDate() {
		return tb03RegistrationDate;
	}

	public void setTb03RegistrationDate(String tb03RegistrationDate) {
		this.tb03RegistrationDate = tb03RegistrationDate;
	}

	public Integer getAgeAtTB03Registration() {
		return ageAtTB03Registration;
	}

	public void setAgeAtTB03Registration(Integer ageAtTB03Registration) {
		this.ageAtTB03Registration = ageAtTB03Registration;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getIntensivePhaseFacility() {
		return intensivePhaseFacility;
	}

	public void setIntensivePhaseFacility(String intensivePhaseFacility) {
		this.intensivePhaseFacility = intensivePhaseFacility;
	}

	public String getContinuationPhaseFacility() {
		return continuationPhaseFacility;
	}

	public void setContinuationPhaseFacility(String continuationPhaseFacility) {
		this.continuationPhaseFacility = continuationPhaseFacility;
	}

	public String getTreatmentRegimen() {
		return treatmentRegimen;
	}

	public void setTreatmentRegimen(String treatmentRegimen) {
		this.treatmentRegimen = treatmentRegimen;
	}

	public String getTb03TreatmentStartDate() {
		return tb03TreatmentStartDate;
	}

	public void setTb03TreatmentStartDate(String tb03TreatmentStartDate) {
		this.tb03TreatmentStartDate = tb03TreatmentStartDate;
	}

	public String getSiteOfDisease() {
		return siteOfDisease;
	}

	public void setSiteOfDisease(String siteOfDisease) {
		this.siteOfDisease = siteOfDisease;
	}

	public Integer getRegGroup() {
		return regGroup;
	}

	public void setRegGroup(Integer regGroup) {
		
		if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.new.conceptId")))
			this.regGroup = 0;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterRelapse1.conceptId")))
			this.regGroup = 1;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterRelapse2.conceptId")))
			this.regGroup = 2;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterDefault1.conceptId")))
			this.regGroup = 3;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterDefault2.conceptId")))
			this.regGroup = 4;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterFailure1.conceptId")))
			this.regGroup = 5;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.afterFailure2.conceptId")))
			this.regGroup = 6;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.other.conceptId")))
			this.regGroup = 7;
		else if(regGroup == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.transferIn.conceptId")))
			this.regGroup = 8;
		
		
	}

	public String getHivTestResult() {
		return hivTestResult;
	}

	public void setHivTestResult(String hivTestResult) {
		this.hivTestResult = hivTestResult;
	}

	public String getHivTestDate() {
		return hivTestDate;
	}

	public void setHivTestDate(String hivTestDate) {
		this.hivTestDate = hivTestDate;
	}

	public String getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(String artStartDate) {
		this.artStartDate = artStartDate;
	}

	public String getCpStartDate() {
		return cpStartDate;
	}

	public void setCpStartDate(String cpStartDate) {
		this.cpStartDate = cpStartDate;
	}

	public String getDiagnosticSmearResult() {
		return diagnosticSmearResult;
	}

	public void setDiagnosticSmearResult(String diagnosticSmearResult) {
		this.diagnosticSmearResult = diagnosticSmearResult;
	}

	public String getDiagnosticSmearTestNumber() {
		return diagnosticSmearTestNumber;
	}

	public void setDiagnosticSmearTestNumber(String diagnosticSmearTestNumber) {
		this.diagnosticSmearTestNumber = diagnosticSmearTestNumber;
	}

	public String getDiagnosticSmearDate() {
		return diagnosticSmearDate;
	}

	public void setDiagnosticSmearDate(String diagnosticSmearDate) {
		this.diagnosticSmearDate = diagnosticSmearDate;
	}

	public String getXpertMTBResult() {
		return xpertMTBResult;
	}

	public void setXpertMTBResult(String xpertMTBResult) {
		this.xpertMTBResult = xpertMTBResult;
	}

	public String getXpertRIFResult() {
		return xpertRIFResult;
	}

	public void setXpertRIFResult(String xpertRIFResult) {
		this.xpertRIFResult = xpertRIFResult;
	}

	public String getXpertTestDate() {
		return xpertTestDate;
	}

	public void setXpertTestDate(String xpertTestDate) {
		this.xpertTestDate = xpertTestDate;
	}

	public String getXpertTestNumber() {
		return xpertTestNumber;
	}

	public void setXpertTestNumber(String xpertTestNumber) {
		this.xpertTestNumber = xpertTestNumber;
	}

	public String getHainMTBResult() {
		return hainMTBResult;
	}

	public void setHainMTBResult(String hainMTBResult) {
		this.hainMTBResult = hainMTBResult;
	}

	public String getHainINHResult() {
		return hainINHResult;
	}

	public void setHainINHResult(String hainINHResult) {
		this.hainINHResult = hainINHResult;
	}

	public String getHainRIFResult() {
		return hainRIFResult;
	}

	public void setHainRIFResult(String hainRIFResult) {
		this.hainRIFResult = hainRIFResult;
	}

	public String getHainTestDate() {
		return hainTestDate;
	}

	public void setHainTestDate(String hainTestDate) {
		this.hainTestDate = hainTestDate;
	}

	public String getHainTestNumber() {
		return hainTestNumber;
	}

	public void setHainTestNumber(String hainTestNumber) {
		this.hainTestNumber = hainTestNumber;
	}

	public String getCultureResult() {
		return cultureResult;
	}

	public void setCultureResult(String cultureResult) {
		this.cultureResult = cultureResult;
	}

	public String getCultureTestDate() {
		return cultureTestDate;
	}

	public void setCultureTestDate(String cultureTestDate) {
		this.cultureTestDate = cultureTestDate;
	}

	public String getCultureTestNumber() {
		return cultureTestNumber;
	}

	public void setCultureTestNumber(String cultureTestNumber) {
		this.cultureTestNumber = cultureTestNumber;
	}

	public String getDrugResistance() {
		return drugResistance;
	}

	public void setDrugResistance(String drugResistance) {
		this.drugResistance = drugResistance;
	}

	public Integer getTb03TreatmentOutcome() {
		return tb03TreatmentOutcome;
	}

	public void setTb03TreatmentOutcome(Integer tb03TreatmentOutcome) {
		System.out.println("---->" + tb03TreatmentOutcome);
		if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.cured.conceptId")))
			this.tb03TreatmentOutcome = 0;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.txCompleted.conceptId")))
			this.tb03TreatmentOutcome = 1;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.txFailure.conceptId")))
			this.tb03TreatmentOutcome = 2;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.died.conceptId")))
			this.tb03TreatmentOutcome = 3;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.ltfu.conceptId")))
			this.tb03TreatmentOutcome = 6;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.canceled.conceptId")))
			this.tb03TreatmentOutcome = 6;
		
		
		System.out.println("---->" + this.tb03TreatmentOutcome);
	}

	public String getTb03TreatmentOutcomeDate() {
		return tb03TreatmentOutcomeDate;
	}

	public void setTb03TreatmentOutcomeDate(String tb03TreatmentOutcomeDate) {
		this.tb03TreatmentOutcomeDate = tb03TreatmentOutcomeDate;
	}

	public Boolean getDiedOfTB() {
		return diedOfTB;
	}

	public void setDiedOfTB(Boolean diedOfTB) {
		this.diedOfTB = diedOfTB;
	}
	
	
	
	

}
