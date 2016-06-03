package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;

public class MdrtbContactPerson {
    private Person person;
    private boolean isPatient;
    private boolean isTBPatient;
    private Obs knownMdrtbContact;
    private Obs testResult;
    private Obs testType;
    private Relationship relationship;
    private List<Obs> testResults;
    private PersonAddress address;
    private Obs phone;
    private PersonAttribute mdrtbContactId;
    
    
    


    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Obs> getTestResults() {
        if (testResults == null)
            return new ArrayList<Obs>();
        return testResults;
    }
    
    public void addTestResult(Obs o){
        if (testResults == null)
            testResults = new ArrayList<Obs>();
        testResults.add(o);
    }

    public void setTestResults(List<Obs> testResults) {
        this.testResults = testResults;
    }

    public boolean getIsPatient() {
        return isPatient;
    }
    public void setIsPatient(boolean val) {
        this.isPatient = val;
    }
//    public boolean isPatient(){
//        return this.isPatient();
//    }
    
    public boolean getIsTBPatient() {
        return isTBPatient;
    }
    public void setIsTBPatient(boolean isTBPatient) {
        this.isTBPatient = isTBPatient;
    }
//    public boolean isTBPatient(){
//        return isTBPatient();
//    }  
   
    public PersonAddress getAddress() {
        return address;
    }

    public void setAddress(PersonAddress address) {
        this.address = address;
    }

    public Obs getPhone() {
        return phone;
    }

    public void setPhone(Obs phone) {
        this.phone = phone;
    }

    public Obs getTestResult() {
        return testResult;
    }

    public void setTestResult(Obs testResult) {
        this.testResult = testResult;
    }

    public Obs getTestType() {
        return testType;
    }

    public void setTestType(Obs testType) {
        this.testType = testType;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public PersonAttribute getMdrtbContactId() {
        return mdrtbContactId;
    }

    public void setMdrtbContactId(PersonAttribute mdrtbContactId) {
        this.mdrtbContactId = mdrtbContactId;
    }

    public Obs getKnownMdrtbContact() {
        return knownMdrtbContact;
    }

    public void setKnownMdrtbContact(Obs knownMdrtbContact) {
        this.knownMdrtbContact = knownMdrtbContact;
    }


    
    
}
