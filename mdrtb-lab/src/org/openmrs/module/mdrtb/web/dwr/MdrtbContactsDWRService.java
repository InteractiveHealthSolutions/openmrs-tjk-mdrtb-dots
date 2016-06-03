package org.openmrs.module.mdrtb.web.dwr;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.service.MdrtbService;

@Deprecated
public class MdrtbContactsDWRService {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    public boolean updateAddress(Integer personId, Integer addressId, String address1, String address2, String township, String city, String district, String region, String phone) {
        boolean ret = true;
        try{
            PersonService perS = Context.getPersonService();
            Person contact = perS.getPerson(personId);
            for (PersonAddress pa: contact.getAddresses()){
                if (pa.getPersonAddressId().intValue() == addressId){
                    boolean saveTest = false;
                    
                     
                        if ((pa.getAddress1() == null && address1 != null) || (pa.getAddress1()!= null && !pa.getAddress1().equals(address1))){
                            pa.setAddress1(address1);
                            saveTest = true;
                        }
                        if ((pa.getAddress2() == null && address2 != null) || (pa.getAddress2() != null && !pa.getAddress2().equals(address2))){
                            pa.setAddress2(address2);
                            saveTest = true;
                        }
                        if ((pa.getTownshipDivision() == null && township != null) || (pa.getTownshipDivision() != null && !pa.getTownshipDivision().equals(township))){
                            pa.setTownshipDivision(township);
                            saveTest = true;
                        }
                        if ((pa.getCityVillage() == null && city != null) || (pa.getCityVillage() != null && !pa.getCityVillage().equals(city))){
                            pa.setCityVillage(city);
                            saveTest = true;
                        }
                        if ((pa.getCountyDistrict() == null && district != null) || (pa.getCountyDistrict() != null && !pa.getCountyDistrict().equals(district))){
                            pa.setCountyDistrict(district);
                            saveTest = true;
                        }
                        if ((pa.getRegion() == null && region != null) || (pa.getRegion() != null && !pa.getRegion().equals(region))){
                            pa.setRegion(region);
                            saveTest = true;
                        } 
                    if (saveTest)
                        perS.savePerson(contact);
                    break;
                }
            }
            
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            ObsService os = Context.getObsService();
            List<Obs> oListPhone = os.getObservationsByPersonAndConcept(contact, Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TELEPHONE_NUMBER));
               if (oListPhone.size() > 0){
                Obs ophone = oListPhone.get(oListPhone.size()-1);
              
                if (!ophone.getValueText().equals(phone)){
                    ophone.setValueText(phone);
                    os.saveObs(ophone, "updating phone number");
                }
               } else {
                   
                   Obs o = new Obs();
                   o.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TELEPHONE_NUMBER));
                   o.setCreator(Context.getAuthenticatedUser());
                   o.setDateCreated(new Date());
                   LocationService ls = Context.getLocationService();
                   Location unknownLocation = ls.getLocation("Unknown Location");
                   o.setLocation(unknownLocation);
                   o.setObsDatetime(new Date());
                   o.setPerson(contact);
                   o.setValueText(phone);
                   o.setVoided(false);
                   os.saveObs(o, "");
                   
               }
            
        } catch (Exception ex){ret=false;log.info("updateAddress DWR service failed", ex);}
        
        return ret;
    } 
    
    
    public boolean updateContact(Integer patientId, Integer personId, Integer relationshipId, String newRelationshipTypeString, String givenName, String familyName,String popupContactIdVal, Integer popupKnownMDRVal ){
        boolean ret = true;
        try{
            PersonService perS = Context.getPersonService();
            Person contact = perS.getPerson(personId);
            Patient patient = Context.getPatientService().getPatient(patientId);
            boolean saveTest = false;
            PersonName pn = contact.getPersonName();
            if ((pn.getGivenName() == null && givenName != null)|| (pn.getGivenName() != null && !pn.getGivenName().equals(givenName))){
                pn.setGivenName(givenName);
                saveTest = true;
            }
            if ((pn.getFamilyName() == null && familyName != null) || (pn.getFamilyName() != null && !pn.getFamilyName().equals(familyName))){
                pn.setFamilyName(familyName);
                saveTest = true;
            }    
            
            //person attribute: String popupContactIdVal, 
            String contactAttributType = Context.getAdministrationService().getGlobalProperty("mdrtb.patient_contact_id_attribute_type");
            PersonAttributeType contactAttType = perS.getPersonAttributeTypeByName(contactAttributType);
            if (contactAttType != null && popupContactIdVal != null && !popupContactIdVal.equals("")){              
                boolean createNew = true;
                for (PersonAttribute patt : contact.getActiveAttributes()){
                    if (!patt.isVoided() && patt.getAttributeType().equals(contactAttType) && !patt.getValue().equals(popupContactIdVal)){
  
                        patt.setValue(popupContactIdVal);      
                        createNew = false;
                        saveTest = true;
                    }    
                }
                if (createNew){
                    if (popupContactIdVal != null && !popupContactIdVal.equals("")){
                        PersonAttribute pa = new PersonAttribute();
                        pa.setAttributeType(contactAttType);
                        pa.setCreator(Context.getAuthenticatedUser());
                        pa.setDateCreated(new Date());
                        pa.setPerson(contact);
                        pa.setValue(popupContactIdVal);
                        pa.setVoided(false);
                        contact.addAttribute(pa);
                        saveTest = true;
                    }
                }          
            }
            
            
            
            if (saveTest)
                perS.savePerson(contact);
                
                Relationship r = perS.getRelationship(relationshipId);
                String relationshipKey = newRelationshipTypeString.substring(newRelationshipTypeString.length()-1, newRelationshipTypeString.length());
                newRelationshipTypeString = newRelationshipTypeString.substring(0, newRelationshipTypeString.length()-1);
            boolean test = false;
            if (r.getRelationshipType().getRelationshipTypeId().intValue() != Integer.valueOf(newRelationshipTypeString).intValue()){
                r.setRelationshipType(perS.getRelationshipType(Integer.valueOf(newRelationshipTypeString)));
                test = true;
            }
            
            if (relationshipKey.equals("A") && r.getPersonA().getPersonId() != contact.getPersonId()){
                r.setPersonA(contact);
                r.setPersonB(patient);
                test = true;
            } else if (relationshipKey.equals("B") && r.getPersonA().getPersonId() != patient.getPersonId()){
                r.setPersonB(contact);
                r.setPersonA(patient);
                test = true;
            }
            
           if (test)
               perS.saveRelationship(r);
            
            
            //OBs Integer popupKnownMDRVal 
          //knownmdr
            
            
            if (popupKnownMDRVal != null){
            
              //newKnownMDRString
                if (popupKnownMDRVal != null){
                    MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                    ObsService os = Context.getObsService();
                    Concept cKnown = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_CONTACT_IS_KNOWN_PRIOR_OR_CURRENT_MDR_TB_CASE);
                    List<Obs> oList = os.getObservationsByPersonAndConcept(contact, cKnown);
                    if (oList != null && oList.size() > 0){
                        Obs oInner = oList.get(oList.size() -1);
                        oInner.setValueNumeric(popupKnownMDRVal.doubleValue());
                        os.saveObs(oInner, "");
                    } else {
                        Obs o = new Obs();
                        o.setConcept(cKnown);
                        o.setCreator(Context.getAuthenticatedUser());
                        o.setDateCreated(new Date());
                        
                        o.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                        o.setObsDatetime(new Date());
                        o.setPerson(contact);
                        o.setValueNumeric(popupKnownMDRVal.doubleValue());
                        o.setVoided(false);
                        os.saveObs(o, "");
                    }
                }  
            }
        } catch (Exception ex){ret=false;log.info("updateAddress DWR service failed", ex);}
            
        return ret;
    }
    
    public PersonAddress createNewAddress(Integer personId, String address1, String address2, String township, String city, String district, String region, String phone) {
        PersonAddress pa = null;
        
        try {
            Person person = Context.getPersonService().getPerson(personId);
            pa = new PersonAddress();
            pa.setAddress1(address1);
            pa.setAddress2(address2);
            pa.setTownshipDivision(township);
            pa.setCityVillage(city);
            pa.setCountyDistrict(district);
            pa.setRegion(region);
            pa.setVoided(false);
            pa.setCreator(Context.getAuthenticatedUser());
            pa.setDateCreated(new Date());
            pa.setPerson(person);
            person.addAddress(pa);
            person = Context.getPersonService().savePerson(person);
            
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
            
            Obs o = new Obs();
            o.setConcept(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TELEPHONE_NUMBER));
            o.setCreator(Context.getAuthenticatedUser());
            o.setDateCreated(new Date());
            LocationService ls = Context.getLocationService();
            Location unknownLocation = ls.getLocation("Unknown Location");
            o.setLocation(unknownLocation);
            o.setObsDatetime(new Date());
            o.setPerson(person);
            o.setValueText(phone);
            o.setVoided(false);
            Context.getObsService().saveObs(o, "");
        
            return pa;
            
            
        } catch (Exception ex) {log.error("DWR unable to create peronAddres" , ex);}   
        return pa;
    }    
    
    public boolean makePatient(Integer personId, Integer relationshipId, String identifier, Integer patientIdentifierTypeId){
            try{
                PersonService perS = Context.getPersonService();
                PatientService ps = Context.getPatientService();
                Person p = perS.getPerson(personId);
                
                if (!p.isPatient()){
                    
                    Patient patient = new Patient();
                    patient.setPatientId(personId);
                    patient.setDateCreated(new Date());
                    patient.setCreator(Context.getAuthenticatedUser());
                    patient.setVoided(false);
                    patient.setGender(p.getGender());
                    patient.setBirthdate(p.getBirthdate());
                    
                    
                    PatientIdentifier pi = new PatientIdentifier();
                    pi.setCreator(Context.getAuthenticatedUser());
                    pi.setDateCreated(new Date());
                    pi.setIdentifier("tmp");
                    pi.setPatient(patient);
                        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(patientIdentifierTypeId);
                    pi.setIdentifierType(pit);
                    Location unknownLocation = Context.getLocationService().getLocation("Unknown Location");
                    pi.setLocation(unknownLocation);
                    pi.setPreferred(true);
                    pi.setVoided(false);     
                    patient.addIdentifier(pi);
                    
                    
                    patient = Context.getPatientService().savePatient(patient);       
               
                    
                    Integer patientIdTmp = patient.getPatientId();
                    
                    Context.clearSession();
                    
                    patient = ps.getPatient(personId);
                    PatientIdentifier piTmp = new PatientIdentifier();
                    piTmp.setCreator(Context.getAuthenticatedUser());
                    piTmp.setDateCreated(new Date());
                    piTmp.setIdentifier(identifier);
                    piTmp.setPatient(patient);
                         piTmp.setIdentifierType(pit);
                     piTmp.setLocation(unknownLocation);
                    piTmp.setPreferred(true);
                    piTmp.setVoided(false);     
                    patient.addIdentifier(piTmp);
//                    
//                    
                    patient = ps.savePatient(patient);
                 
                    
                    patient = ps.getPatient(patientIdTmp);
             
                    for (PatientIdentifier pitTmp:patient.getActiveIdentifiers()){
                        pitTmp.setVoided(true);
                        pitTmp.setDateVoided(new Date());
                        pitTmp.setVoidReason("");
                        pitTmp.setDateVoided(new Date());
                    }
                    patient = ps.savePatient(patient);
               
                    ps.voidPatient(patient, "Temporary row need to make a person a patient.");

                    p = perS.getPerson(patientIdTmp);
                    
                    
                    perS.voidPerson(p, "Temporary row need to make a person a patient.");
                    
                }
                return true;
            } catch (Exception ex){
                log.error("failed to make contact a patient", ex);
                return false;
            }    
    }
    
    public boolean removeSupporter(Integer patientId, Integer supporterId){
        try {
            Patient p = Context.getPatientService().getPatient(patientId);
            PersonService ps = Context.getPersonService();
            Person supporter = ps.getPerson(supporterId);
            String relationshipTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_relationship_type");
            RelationshipType rt = ps.getRelationshipTypeByName(relationshipTypeString);
            
            for (Relationship r : ps.getRelationshipsByPerson(supporter)){
                if (r.getPersonA().getPersonId().intValue() == supporter.getPersonId().intValue()
                        && r.getPersonB().getPersonId().intValue() == p.getPatientId().intValue()
                        && r.isVoided() == false 
                        && r.getRelationshipType().getRelationshipTypeId().intValue() == rt.getRelationshipTypeId().intValue()){
                        ps.voidRelationship(r, "treatment supporter relationship voided.");
                }
                
                
            }
            
            
            return true;
        } catch (Exception ex){
            return false;
        }
        
    }
    
}
