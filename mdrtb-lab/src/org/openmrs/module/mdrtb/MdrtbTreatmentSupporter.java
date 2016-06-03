package org.openmrs.module.mdrtb;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

public class MdrtbTreatmentSupporter {
    private List<Obs> phoneNumbers;
    private Person person;
    
    //indicates whether or not the TS is available
    private Obs active;
    
    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public void setActive(Obs active) {
    	this.active = active;
    }

    public List<Obs> getPhoneNumbers() {
        if (this.phoneNumbers == null)
            phoneNumbers = new ArrayList<Obs>();
        return phoneNumbers;
    }
    public void setPhoneNumbers(List<Obs> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    public void addPhoneObs(Obs o){
        if (this.phoneNumbers == null)
            phoneNumbers = new ArrayList<Obs>();
        this.phoneNumbers.add(o);
    }

    
    public Obs getActive() {
    	return active;
    }
    
    public MdrtbTreatmentSupporter(){
        this.person = new Person();
        this.person.addAddress(new PersonAddress());
        
            PersonName pn = new PersonName();
            this.person.addName(pn);
            
            PersonAddress pa = new PersonAddress();
            this.person.addAddress(pa);
            

    }
    

     
}
