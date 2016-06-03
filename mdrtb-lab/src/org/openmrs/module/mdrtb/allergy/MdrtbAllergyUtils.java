package org.openmrs.module.mdrtb.allergy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.service.MdrtbService;

public class MdrtbAllergyUtils {

    protected final Log log = LogFactory.getLog(getClass());
        
    public static Map<Patient, List<MdrtbAllergyStringObj>> getPatientAllergies(List<Patient> pList){
        
    
        Map<Patient, List<MdrtbAllergyStringObj>> ret = new HashMap<Patient, List<MdrtbAllergyStringObj>>();
        List<Concept> cList = new ArrayList<Concept>();
        cList.add(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_CONSTRUCT));
        List<Person> persList = new ArrayList<Person>();
        for (Patient p: pList)
            persList.add(p);
        List<Obs> oList = Context.getObsService().getObservations(persList, null, cList, null, null, null, null, null, null, null, null, false);
        Concept effect = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT);
        Concept effectNC = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_NON_CODED);
        Concept medication = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_MEDICATION);
        Concept medicationNC = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_MEDICATION_NON_CODED);
        Concept actionTaken = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_ACTION_TAKEN);
        Concept adverseEffectDate = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ADVERSE_EFFECT_DATE);
        
        for (Patient p : pList){
           ArrayList<MdrtbAllergyStringObj> allergyList = new ArrayList<MdrtbAllergyStringObj>(); 
           for (Obs oParent :  oList){     
               if (oParent.getPersonId().equals(p.getPatientId())){
                   MdrtbAllergyStringObj maso = new MdrtbAllergyStringObj();
                   for (Obs o : oParent.getGroupMembers()){
                       if (o.getConcept().getConceptId().equals(effectNC.getConceptId())){
                           maso.setEffect(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(effect.getConceptId()) && o.getValueCoded() != null){
                           maso.setEffect(o.getValueCoded().getBestShortName(Context.getLocale()).getName());
                       } else if (o.getConcept().getConceptId().equals(medicationNC.getConceptId())){
                           maso.setMedication(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(medication.getConceptId()) && o.getValueCoded() != null){
                           maso.setMedication(o.getValueCoded().getBestShortName(Context.getLocale()).getName());
                       } else if (o.getConcept().getConceptId().equals(actionTaken.getConceptId())){
                           maso.setSupportingTreatment(o.getValueText());
                       } else if (o.getConcept().getConceptId().equals(adverseEffectDate.getConceptId()) && o.getValueDatetime() != null){
                           maso.setDate(o.getValueDatetime());
                       } else if (o.getConcept().getConceptId().equals(adverseEffectDate.getConceptId()) && o.getValueDatetime() == null){
                           maso.setDate(o.getObsDatetime());
                       }
                   }
                   allergyList.add(maso);
               }
           }
           
           Collections.sort(allergyList, new Comparator<MdrtbAllergyStringObj>() {
               public int compare(MdrtbAllergyStringObj left, MdrtbAllergyStringObj right) {
            	  
            	   Date leftDate = left.getDate();
            	   Date rightDate = right.getDate();
            	   
            	   if (leftDate == null && rightDate == null) {
            		   return 0;
            	   }
            	   else if (leftDate == null) {
            		   return 1;
            	   }
            	   else if (rightDate == null) {
            		   return -1;
            	   }
            	   else {
            		   return (left.getDate()).compareTo(right.getDate());
            	   }
               }
               
           });
           
           ret.put(p, allergyList);
        }
        
        return ret;
    }
      
}
