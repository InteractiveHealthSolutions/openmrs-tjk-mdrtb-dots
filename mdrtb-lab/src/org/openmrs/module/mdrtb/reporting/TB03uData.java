package org.openmrs.module.mdrtb.reporting;

import java.util.Date;
import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class TB03uData {
	
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
	private String month2SmearResult;
	private String month2SmearDate;
	private String month2TestNumber;
	private String month3SmearResult;
	private String month3SmearDate;
	private String month3TestNumber;
	private String month4SmearResult;
	private String month4SmearDate;
	private String month4TestNumber;
	private String month5SmearResult;
	private String month5SmearDate;
	private String month5TestNumber;
	private String month6SmearResult;
	private String month6SmearDate;
	private String month6TestNumber;
	private String month8SmearResult;
	private String month8SmearDate;
	private String month8TestNumber;
	private Integer tb03TreatmentOutcome;
	private String tb03TreatmentOutcomeDate;
	private Boolean diedOfTB;
	private String notes;
	private Boolean reg1New;
	private Boolean reg1Rtx;
    private HashMap<String, String> dstResults;
    private String dstCollectionDate;
    private String dstResultDate;
    private String dstR;
    private String dstH;
    private String dstZ;
    private String dstE;
    private String dstS;
    private String dstKm;
    private String dstAm;
    private String dstCm;
    private String dstOfx;
    private String dstMfx;
    private String dstPto;
    private String dstCs;
    private String dstPAS;
    private String dstLzd;
    private String dstCfz;
    private String dstBdq;
    private String dstDlm;
    
   
    
    
	public TB03uData() {
		// TODO Auto-generated constructor stub
		dstResults = new HashMap<String,String>();
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

	public String getMonth2SmearResult() {
		return month2SmearResult;
	}

	public void setMonth2SmearResult(String month2SmearResult) {
		this.month2SmearResult = month2SmearResult;
	}

	public String getMonth2SmearDate() {
		return month2SmearDate;
	}

	public void setMonth2SmearDate(String month2SmearDate) {
		this.month2SmearDate = month2SmearDate;
	}

	public String getMonth3SmearResult() {
		return month3SmearResult;
	}

	public void setMonth3SmearResult(String month3SmearResult) {
		this.month3SmearResult = month3SmearResult;
	}

	public String getMonth3SmearDate() {
		return month3SmearDate;
	}

	public void setMonth3SmearDate(String month3SmearDate) {
		this.month3SmearDate = month3SmearDate;
	}

	public String getMonth4SmearResult() {
		return month4SmearResult;
	}

	public void setMonth4SmearResult(String month4SmearResult) {
		this.month4SmearResult = month4SmearResult;
	}

	public String getMonth4SmearDate() {
		return month4SmearDate;
	}

	public void setMonth4SmearDate(String month4SmearDate) {
		this.month4SmearDate = month4SmearDate;
	}

	public String getMonth6SmearResult() {
		return month6SmearResult;
	}

	public void setMonth6SmearResult(String month6SmearResult) {
		this.month6SmearResult = month6SmearResult;
	}

	public String getMonth6SmearDate() {
		return month6SmearDate;
	}

	public void setMonth6SmearDate(String month6SmearDate) {
		this.month6SmearDate = month6SmearDate;
	}

	public String getMonth8SmearResult() {
		return month8SmearResult;
	}

	public void setMonth8SmearResult(String month8SmearResult) {
		this.month8SmearResult = month8SmearResult;
	}

	public String getMonth8SmearDate() {
		return month8SmearDate;
	}

	public void setMonth8SmearDate(String month8SmearDate) {
		this.month8SmearDate = month8SmearDate;
	}

	public Integer getTb03TreatmentOutcome() {
		return tb03TreatmentOutcome;
	}

	public void setTb03TreatmentOutcome(Integer tb03TreatmentOutcome) {
		System.out.println("---->" + tb03TreatmentOutcome);
		if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.cured.conceptId")))
			this.tb03TreatmentOutcome = 0;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.txCompleted.conceptId")))
			this.tb03TreatmentOutcome = 1;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.txFailure.conceptId")))
			this.tb03TreatmentOutcome = 2;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.died.conceptId")))
		{
			if(diedOfTB)
				this.tb03TreatmentOutcome = 3;
			else
				this.tb03TreatmentOutcome = 4;
		}
			
		
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.ltfu.conceptId")))
			this.tb03TreatmentOutcome = 5;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.canceled.conceptId")))
			this.tb03TreatmentOutcome = 6;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.sld2.conceptId")))
			this.tb03TreatmentOutcome = 7;
		else if(tb03TreatmentOutcome == Integer.parseInt(Context.getAdministrationService().getGlobalProperty("dotsreports.outcome.transferout.conceptId")))
			this.tb03TreatmentOutcome = 8;
		
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMonth2TestNumber() {
		return month2TestNumber;
	}

	public void setMonth2TestNumber(String month2TestNumber) {
		this.month2TestNumber = month2TestNumber;
	}

	public String getMonth3TestNumber() {
		return month3TestNumber;
	}

	public void setMonth3TestNumber(String month3TestNumber) {
		this.month3TestNumber = month3TestNumber;
	}

	public String getMonth4TestNumber() {
		return month4TestNumber;
	}

	public void setMonth4TestNumber(String month4TestNumber) {
		this.month4TestNumber = month4TestNumber;
	}

	public String getMonth6TestNumber() {
		return month6TestNumber;
	}

	public void setMonth6TestNumber(String month6TestNumber) {
		this.month6TestNumber = month6TestNumber;
	}

	public String getMonth8TestNumber() {
		return month8TestNumber;
	}

	public void setMonth8TestNumber(String month8TestNumber) {
		this.month8TestNumber = month8TestNumber;
	}

	public Boolean getReg1New() {
		return reg1New;
	}

	public void setReg1New(Boolean reg1New) {
		this.reg1New = reg1New;
	}

	public Boolean getReg1Rtx() {
		return reg1Rtx;
	}

	public void setReg1Rtx(Boolean reg1Rtx) {
		this.reg1Rtx = reg1Rtx;
	}

	public String getMonth5SmearResult() {
		return month5SmearResult;
	}

	public void setMonth5SmearResult(String month5SmearResult) {
		this.month5SmearResult = month5SmearResult;
	}

	public String getMonth5SmearDate() {
		return month5SmearDate;
	}

	public void setMonth5SmearDate(String month5SmearDate) {
		this.month5SmearDate = month5SmearDate;
	}

	public String getMonth5TestNumber() {
		return month5TestNumber;
	}

	public void setMonth5TestNumber(String month5TestNumber) {
		this.month5TestNumber = month5TestNumber;
	}

	public HashMap getDstResults() {
		return dstResults;
	}


	public String getDstCollectionDate() {
		return dstCollectionDate;
	}

	public void setDstCollectionDate(String dstCollectionDate) {
		this.dstCollectionDate = dstCollectionDate;
	}

	public String getDstResultDate() {
		return dstResultDate;
	}

	public void setDstResultDate(String dstResultDate) {
		this.dstResultDate = dstResultDate;
	}

	public String getDstR() {
		return dstResults.get("R");
	}

	public void setDstR(String dstR) {
		this.dstR = dstR;
	}

	public String getDstH() {
		return dstResults.get("H");
	}

	public void setDstH(String dstH) {
		this.dstH = dstH;
	}

	public String getDstZ() {
		return dstResults.get("Z");
	}

	public void setDstZ(String dstZ) {
		this.dstZ = dstZ;
	}

	public String getDstE() {
		return dstResults.get("E");
	}

	public void setDstE(String dstE) {
		this.dstE = dstE;
	}

	public String getDstS() {
		return dstResults.get("S");
	}

	public void setDstS(String dstS) {
		this.dstS = dstS;
	}

	public String getDstKm() {
		return dstResults.get("KM");
	}

	public void setDstKm(String dstKm) {
		this.dstKm = dstKm;
	}

	public String getDstAm() {
		return dstResults.get("AM");
	}

	public void setDstAm(String dstAm) {
		this.dstAm = dstAm;
	}

	public String getDstCm() {
		return dstResults.get("CM");
	}

	public void setDstCm(String dstCm) {
		this.dstCm = dstCm;
	}

	public String getDstOfx() {
		return dstResults.get("OFX");
	}

	public void setDstOfx(String dstOfx) {
		this.dstOfx = dstOfx;
	}

	public String getDstMfx() {
		return dstResults.get("Moxi");
	}

	public void setDstMfx(String dstMfx) {
		this.dstMfx = dstMfx;
	}

	public String getDstPto() {
		return dstResults.get("Pto");
	}

	public void setDstPto(String dstPto) {
		this.dstPto = dstPto;
	}

	public String getDstCs() {
		return dstResults.get("CS");
	}

	public void setDstCs(String dstCs) {
		this.dstCs = dstCs;
	}

	public String getDstPAS() {
		return dstResults.get("PAS");
	}

	public void setDstPAS(String dstPAS) {
		this.dstPAS = dstPAS;
	}

	public String getDstLzd() {
		return dstResults.get("LZD");
	}

	public void setDstLzd(String lzd) {
		this.dstLzd = lzd;
	}

	public String getDstCfz() {
		return dstResults.get("CFZ");
	}

	public void setDstCfz(String cfz) {
		this.dstCfz = cfz;
	}

	public String getDstBdq() {
		return dstResults.get("BDQ");
	}

	public void setDstBdq(String bdq) {
		this.dstBdq = bdq;
	}

	public String getDstDlm() {
		return dstResults.get("DLM");
	}

	public void setDstDlm(String dlm) {
		dstDlm = dlm;
	}

	public void setDstResults(HashMap<String, String> dstResults) {
		this.dstResults = dstResults;
	}
	
		
	
	
	
	

}
