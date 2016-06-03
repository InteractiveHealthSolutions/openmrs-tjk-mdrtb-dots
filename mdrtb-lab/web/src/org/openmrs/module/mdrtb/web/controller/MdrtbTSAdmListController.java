package org.openmrs.module.mdrtb.web.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbTreatmentSupporter;
import org.openmrs.module.mdrtb.propertyeditor.ObsEditor;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.propertyeditor.ConceptClassEditor;
import org.openmrs.propertyeditor.ConceptDatatypeEditor;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MdrtbTSAdmListController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(org.openmrs.Obs.class,
                new ObsEditor());
        binder.registerCustomEditor(org.openmrs.Concept.class,
                new ConceptEditor());
        NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
        binder.registerCustomEditor(java.lang.Integer.class,
                new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.lang.Double.class,
                new CustomNumberEditor(java.lang.Double.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, 
                new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(org.openmrs.ConceptClass.class, 
                new ConceptClassEditor());
        binder.registerCustomEditor(org.openmrs.ConceptDatatype.class, 
                new ConceptDatatypeEditor());
        binder.registerCustomEditor(org.openmrs.Location.class, 
                new LocationEditor());
    }
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {

        Map<String,Object> map = new HashMap<String,Object>();
        if (Context.isAuthenticated()){

            
            String dateFormat = Context.getDateFormat().toPattern();
            map.put("dateFormat", dateFormat);
            
            MessageSourceAccessor msa = getMessageSourceAccessor();
            map.put("daysOfWeek", "'" + msa.getMessage("mdrtb.sunday")+ "','" + msa.getMessage("mdrtb.monday")+ "','" + msa.getMessage("mdrtb.tuesday") + "','" + msa.getMessage("mdrtb.wednesday")+ "','" + msa.getMessage("mdrtb.thursday")+ "','" + msa.getMessage("mdrtb.friday")+ "','"
                    + msa.getMessage("mdrtb.saturday")+ "','" + msa.getMessage("mdrtb.sun")+ "','" + msa.getMessage("mdrtb.mon")+ "','"+ msa.getMessage("mdrtb.tues")+ "','"+ msa.getMessage("mdrtb.wed")+ "','"+ msa.getMessage("mdrtb.thurs")+ "','"+ msa.getMessage("mdrtb.fri")+ "','" + msa.getMessage("mdrtb.sat") + "'");
            map.put("monthsOfYear", "'" + msa.getMessage("mdrtb.january")+ "','"+ msa.getMessage("mdrtb.february")+ "','"+ msa.getMessage("mdrtb.march")+ "','"+ msa.getMessage("mdrtb.april")+ "','"+ msa.getMessage("mdrtb.may")+ "','"+ msa.getMessage("mdrtb.june")+ "','"+ msa.getMessage("mdrtb.july")+ "','"+ msa.getMessage("mdrtb.august")+ "','"
                    + msa.getMessage("mdrtb.september")+ "','"+ msa.getMessage("mdrtb.october")+ "','"+ msa.getMessage("mdrtb.november")+ "','"+ msa.getMessage("mdrtb.december")+ "','"+ msa.getMessage("mdrtb.jan")+ "','"+ msa.getMessage("mdrtb.feb")+ "','"+ msa.getMessage("mdrtb.mar")+ "','"+ msa.getMessage("mdrtb.apr")+ "','"+ msa.getMessage("mdrtb.may")+ "','"
                    + msa.getMessage("mdrtb.jun")+ "','"+ msa.getMessage("mdrtb.jul")+ "','"+ msa.getMessage("mdrtb.aug")+ "','"+ msa.getMessage("mdrtb.sept")+ "','"+ msa.getMessage("mdrtb.oct")+ "','"+ msa.getMessage("mdrtb.nov")+ "','"+ msa.getMessage("mdrtb.dec")+ "'");
            
        }
        return map ;
    }


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object, BindException exceptions) throws Exception {       

        if (Context.isAuthenticated()){
                
               List<MdrtbTreatmentSupporter> tss = (List<MdrtbTreatmentSupporter>) object;
               String deleteStringRoot = "del_checkbox_";
               String treatSupAttributeTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_person_attribute_type");
               String relationshipTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_relationship_type");
               
               PersonService ps = Context.getPersonService();
               PersonAttributeType pat = ps.getPersonAttributeTypeByName(treatSupAttributeTypeString);
               RelationshipType rt = ps.getRelationshipTypeByName(relationshipTypeString);
                for (int i = 0; i <= tss.size(); i++){
                  boolean test = false;
                  String newString = deleteStringRoot+i;
                  String isChecked = request.getParameter(newString);
                  if (isChecked != null){
                      Person p = ps.getPerson(Integer.valueOf(isChecked));
                      List<PersonAttribute> ats = p.getActiveAttributes();
                      for (PersonAttribute pa:ats){
                          if (pa.getAttributeType().getPersonAttributeTypeId().intValue() == pat.getPersonAttributeTypeId().intValue()){
                                  pa.voidAttribute("no longer a treatment supporter");
                                  test = true;
                          }       
                      }
                      if (test){
                          ps.savePerson(p);
                          
                          List<Relationship> rs = ps.getRelationshipsByPerson(p);
                          
                          for (Relationship r: rs){
                              if (r.getPersonA().equals(p) && rt.getRelationshipTypeId().intValue() == r.getRelationshipType().getRelationshipTypeId().intValue())
                              ps.voidRelationship(r, "person no longer a valid treatment supporter");
                          }
                          
              
                      }    
                  }
                  
               }

        }
                
        return new ModelAndView(new RedirectView(getSuccessView()));
    }


    /**
     * This class returns the form backing object.  This can be a string, a boolean, or a normal
     * java pojo.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception { 
        List<MdrtbTreatmentSupporter> ret = new ArrayList<MdrtbTreatmentSupporter>();
        if (Context.isAuthenticated()){
            MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
                Concept phoneConcept = (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TELEPHONE_NUMBER));
                
                //get the concept for TS Activity
                Concept tsActivityConcept = (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_SUPPORTER_CURRENTLY_ACTIVE));
                
                
                String treatSupAttributeTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_person_attribute_type");
                PersonService ps = Context.getPersonService();
                ObsService os = Context.getObsService();
                PersonAttributeType pat = ps.getPersonAttributeTypeByName(treatSupAttributeTypeString);
                List<Person> persons = ps.getPeople("%", false);
                for (Person p:persons){
                    if (p.getActiveAttributes().size() > 0){
                        for (PersonAttribute patTmp:p.getActiveAttributes()){
                            if (patTmp.getAttributeType().getPersonAttributeTypeId().intValue() == pat.getPersonAttributeTypeId().intValue()
                                    && !p.getDead() && !p.getPersonVoided()){
                                p.getAddresses();
                                MdrtbTreatmentSupporter mts = new MdrtbTreatmentSupporter();
                                mts.setPerson(p);
                                
                                    //get phone number routine:
                                    List<Obs> oPhones = os.getObservationsByPersonAndConcept(p, phoneConcept);
                                    for (Obs o: oPhones){
                                        if (!o.getVoided())
                                            mts.addPhoneObs(o);
                                    }
                                    
                                   //set the TS Activity field if it exists
                                    List<Obs> oActivity = os.getObservationsByPersonAndConcept(p, tsActivityConcept);
                                    if(oActivity.size()==1) {
                                 	   Obs o = oActivity.get(0);
                                 	   if(!o.getVoided()) {
                                 		   mts.setActive(oActivity.get(0));
                                 	   }
                                 	   
                                    }
                                   
                                    
                                ret.add(mts);
                            }
                        }
                        
                    }
                }
        }
        
        return ret;
    }    
}
