package org.openmrs.module.dotsreports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;

public class MdrtbPatient {
    
    private Patient patient;
    private PatientIdentifier patientIdentifier;
    //TODO:   really, all patient obs?
    private Set<Obs> obs = new HashSet<Obs>();
    private List<Order> orders;
    private List<DrugOrder> futureDrugOrders ;
    private List<DrugOrder> currentDrugOrders ;
    private List<DrugOrder> completedDrugOrders;
    private PersonAttribute healthCenter;
    private PersonAttribute healthDistrict;
    private String givenName = "";
    private String middleName = "";
    private String familyName = "";
    private String familyNameTwo = "";
    private Obs treatmentStartDate;
    private Obs cultureConversion ;
    private Obs cultureReconversion;
    private Location location ;
    private Person provider;
    private PatientProgram patientProgram;
    private PatientState cultureStatus;   
    private Obs patientClassDrugUse;
    private Obs patientClassPrevTreatment;
    private Obs tbCaseClassification;
    private Obs pulmonary;
    private Obs extrapulmonary;
    private Obs tbLocation;   
    private Obs hivStatus;
    private Obs cd4;
    private Obs cd4percent;
    private PatientIdentifier artId;
    private Obs allergyComment;
    private Obs treatmentPlanComment;   
    private Obs durationOfPreviousTreatment;
    private Obs previousRegistrationNumber;
    private Obs previousTreatmentCenter;
    private Obs patientReferredBy;
    private Obs patientTransferredFrom;
    private Obs patientTransferredTo;
    private Obs nextScheduledVisit;
    private Obs onART;
    private Person treatmentSupporter;
    private Obs treatmentSupporterPhone;
    //private List<MdrtbContactPerson> contacts;
    private Map<Integer, String> oes = new HashMap<Integer, String>();
    private List<Concept> resistanceDrugConcepts = new ArrayList<Concept>();
    private List<Obs> stEmpIndObs = new ArrayList<Obs>();
    private List<Encounter> htmlEncList = new ArrayList<Encounter>();
    private Obs causeOfDeath;
    private Map<Object, Object> extra = new HashMap<Object, Object>();
    
    public List<Encounter> getHtmlEncList() {
        return htmlEncList;
    }

    public void setHtmlEncList(List<Encounter> htmlEncList) {
        this.htmlEncList = htmlEncList;
    }

    public List<Obs> getStEmpIndObs() {
        return stEmpIndObs;
    }

    public void setStEmpIndObs(List<Obs> stEmpIndObs) {
        this.stEmpIndObs = stEmpIndObs;
    }

    public List<Concept> getResistanceDrugConcepts() {
        return resistanceDrugConcepts;
    }

    public void setResistanceDrugConcepts(List<Concept> resistanceDrugConcepts) {
        this.resistanceDrugConcepts = resistanceDrugConcepts;
    }

    /*public List<MdrtbContactPerson> getContacts() {
        if (contacts == null)
            return new ArrayList<MdrtbContactPerson>();
        return contacts;
    }*/

    /*public void setContacts(List<MdrtbContactPerson> contacts) {
        this.contacts = contacts;
    }*/

    public Person getTreatmentSupporter() {
        return treatmentSupporter;
    }

    public void setTreatmentSupporter(Person treatmentSupporter) {
        this.treatmentSupporter = treatmentSupporter;
    }

    public Obs getNextScheduledVisit() {
        return nextScheduledVisit;
    }

    public void setNextScheduledVisit(Obs nextScheduledVisit) {
        this.nextScheduledVisit = nextScheduledVisit;
    }

    public Obs getOnART() {
        return onART;
    }

    public void setOnART(Obs onART) {
        this.onART = onART;
    }

	public Obs getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(Obs causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public Map<Object, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<Object, Object> extra) {
		this.extra = extra;
	}

	public Obs getDurationOfPreviousTreatment() {
        return durationOfPreviousTreatment;
    }

    public void setDurationOfPreviousTreatment(Obs durationOfPreviousTreatment) {
        this.durationOfPreviousTreatment = durationOfPreviousTreatment;
    }

    public Obs getPreviousRegistrationNumber() {
        return previousRegistrationNumber;
    }

    public void setPreviousRegistrationNumber(Obs previousRegistrationNumber) {
        this.previousRegistrationNumber = previousRegistrationNumber;
    }

    public Obs getPreviousTreatmentCenter() {
        return previousTreatmentCenter;
    }

    public void setPreviousTreatmentCenter(Obs previousTreatmentCenter) {
        this.previousTreatmentCenter = previousTreatmentCenter;
    }

    public Obs getPatientReferredBy() {
        return patientReferredBy;
    }

    public void setPatientReferredBy(Obs patientReferredBy) {
        this.patientReferredBy = patientReferredBy;
    }

    public Obs getPatientTransferredFrom() {
        return patientTransferredFrom;
    }

    public void setPatientTransferredFrom(Obs patientTransferredFrom) {
        this.patientTransferredFrom = patientTransferredFrom;
    }

    public Obs getPatientTransferredTo() {
        return patientTransferredTo;
    }

    public void setPatientTransferredTo(Obs patientTransferredTo) {
        this.patientTransferredTo = patientTransferredTo;
    }

    public Obs getAllergyComment() {
        return allergyComment;
    }

    public void setAllergyComment(Obs allergyComment) {
        this.allergyComment = allergyComment;
    }

    public Obs getTreatmentPlanComment() {
        return treatmentPlanComment;
    }

    public void setTreatmentPlanComment(Obs treatmentPlanComment) {
        this.treatmentPlanComment = treatmentPlanComment;
    }

    public Obs getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(Obs hivStatus) {
        this.hivStatus = hivStatus;
    }

    public Obs getCd4() {
        return cd4;
    }

    public void setCd4(Obs cd4) {
        this.cd4 = cd4;
    }

    public Obs getCd4percent() {
        return cd4percent;
    }

    public void setCd4percent(Obs cd4percent) {
        this.cd4percent = cd4percent;
    }

    public PatientIdentifier getArtId() {
        return artId;
    }

    public void setArtId(PatientIdentifier artId) {
        this.artId = artId;
    }


    public Obs getPatientClassDrugUse() {
        return patientClassDrugUse;
    }

    public void setPatientClassDrugUse(Obs patientClassDrugUse) {
        this.patientClassDrugUse = patientClassDrugUse;
    }

    public Obs getPatientClassPrevTreatment() {
        return patientClassPrevTreatment;
    }

    public void setPatientClassPrevTreatment(Obs patientClassPrevTreatment) {
        this.patientClassPrevTreatment = patientClassPrevTreatment;
    }

    public Obs getTbCaseClassification() {
        return tbCaseClassification;
    }

    public void setTbCaseClassification(Obs tbCaseClassification) {
        this.tbCaseClassification = tbCaseClassification;
    }

    public Obs getPulmonary() {
        return pulmonary;
    }

    public void setPulmonary(Obs pulmonary) {
        this.pulmonary = pulmonary;
    }

    public Obs getExtrapulmonary() {
        return extrapulmonary;
    }

    public void setExtrapulmonary(Obs extrapulmonary) {
        this.extrapulmonary = extrapulmonary;
    }

    public Obs getTbLocation() {
        return tbLocation;
    }

    public void setTbLocation(Obs tbLocation) {
        this.tbLocation = tbLocation;
    }

    public PatientState getCultureStatus() {
        return cultureStatus;
    }

    public void setCultureStatus(PatientState cultureStatus) {
        this.cultureStatus = cultureStatus;
    }

    public PatientProgram getPatientProgram() {
        return patientProgram;
    }

    public void setPatientProgram(PatientProgram patientProgram) {
        this.patientProgram = patientProgram;
    }

    public Obs getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(Obs treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }

    public Obs getCultureConversion() {
        return cultureConversion;
    }

    public void setCultureConversion(Obs cultureConversion) {
        this.cultureConversion = cultureConversion;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Person getProvider() {
        return provider;
    }

    public void setProvider(Person provider) {
        this.provider = provider;
    }

    public MdrtbPatient(){}
       
       public void setFutureDrugOrders(List<DrugOrder> d){
           this.futureDrugOrders = d;
       }
       
       public List<DrugOrder> getFutureDrugOrders(){
           return this.futureDrugOrders;
       }
       
       public void setCurrentDrugOrders(List<DrugOrder> d){
           this.currentDrugOrders = d;
       }
       
       public List<DrugOrder> getCurrentDrugOrders(){
           return this.currentDrugOrders;
       }
       
       public void setCompletedDrugOrders(List<DrugOrder> c){
           this.completedDrugOrders = c;
       }
       
       public List<DrugOrder> getCompletedDrugOrders(){
           return this.completedDrugOrders;
       }
       
       public void setOrders(List<Order> ord){
           this.orders = ord;
       }
       
       public List<Order> getOrders(){
           return this.orders;
       }
       public void addCompletedDrugOrder(DrugOrder o){
           if (this.completedDrugOrders == null){
               setCompletedDrugOrders((List<DrugOrder>) new ArrayList<DrugOrder>());
           }
           this.completedDrugOrders.add(o);
       }
       
       
       public void addCurrentDrugOrder(DrugOrder o){
           if (this.currentDrugOrders == null){
               setCurrentDrugOrders((List<DrugOrder>) new ArrayList<DrugOrder>());
           }
           this.currentDrugOrders.add(o);
       }
       
       public void addFutureDrugOrder(DrugOrder o){
           if (this.futureDrugOrders == null){
               setFutureDrugOrders((List<DrugOrder>) new ArrayList<DrugOrder>());
           }
           this.futureDrugOrders.add(o);
       }
       
       public void addOrder(Order o){
           if (this.orders == null){
               setOrders((List<Order>) new ArrayList<Order>());
           }
           this.orders.add(o);
       }
       
       public void setPatient(Patient p){
           this.patient = p;
       }
       public Patient getPatient(){
           return this.patient;
       }

       public void setObs(Set<Obs> o){
           this.obs = o;
       }
       
       public Set<Obs> getObs(){
           return this.obs;
       }
       
       public void addObs(Obs obs){
           if (this.obs == null)
               setObs((Set<Obs>) new HashSet<Obs>());
           this.obs.add(obs);
       }

       public String getGivenName(){
           return this.givenName;
       }
       
       public void setGivenName(String s){
           this.givenName = s;
       }
       public String getMiddleName(){
           return this.middleName;
       }
       public void setMiddleName(String s){
           this.middleName = s;
       }
       public String getFamilyName(){
           return this.familyName;
       }
       public void setFamilyName(String s){
           this.familyName = s;
       }

    public String getFamilyNameTwo() {
        return familyNameTwo;
    }

    public void setFamilyNameTwo(String familyNameTwo) {
        this.familyNameTwo = familyNameTwo;
    }

    public PatientIdentifier getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(PatientIdentifier patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public Obs getCultureReconversion() {
        return cultureReconversion;
    }

    public void setCultureReconversion(Obs cultureReconversion) {
        this.cultureReconversion = cultureReconversion;
    }

    public PersonAttribute getHealthCenter() {
        return healthCenter;
    }

    public void setHealthCenter(PersonAttribute healthCenter) {
        this.healthCenter = healthCenter;
    }

    public PersonAttribute getHealthDistrict() {
        return healthDistrict;
    }

    public void setHealthDistrict(PersonAttribute healthDistrict) {
        this.healthDistrict = healthDistrict;
    }

   /* public void addContact(MdrtbContactPerson mcp){
            if (contacts == null)
                contacts = new ArrayList<MdrtbContactPerson>();
            contacts.add(mcp);
        
    }*/

    public Map<Integer, String> getOes() {
        return oes;
    }

    public void setOes(Map<Integer, String> oes) {
        this.oes = oes;
    }
    public void addToOE(Integer key, String value){
        this.oes.put(key, value);
    }

    public Obs getTreatmentSupporterPhone() {
        return treatmentSupporterPhone;
    }

    public void setTreatmentSupporterPhone(Obs treatmentSupporterPhone) {
        this.treatmentSupporterPhone = treatmentSupporterPhone;
    }

    public void addEncounterToHtmlEncList(Encounter enc){
        if (this.htmlEncList == null)
            this.htmlEncList = new ArrayList<Encounter>();
        this.htmlEncList.add(enc);
    }
    
    /*
     * sort Encounters by encounterDatetime, desc
     */
    public void sortHtmlEncListByEncounterDatetime(){
        Collections.sort(this.htmlEncList, new Comparator<Encounter>() {
            public int compare(Encounter u1, Encounter u2) {
                return u2.getEncounterDatetime().compareTo(u1.getEncounterDatetime());
            }
        });
    }
       
}
