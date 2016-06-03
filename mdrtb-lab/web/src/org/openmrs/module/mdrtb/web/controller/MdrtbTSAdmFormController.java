package org.openmrs.module.mdrtb.web.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.ConceptService;
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

public class MdrtbTSAdmFormController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    private final static String YES = "YES";

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
            
            //get the potential answers for the TS Activity concept and make them available to the JSP 
            Concept tsActivityConcept = (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_SUPPORTER_CURRENTLY_ACTIVE));
            List<Concept> activityAnswers = new ArrayList<Concept>();
            if (tsActivityConcept == null)
                    throw new RuntimeException("Could not find concept for treatment supporter activity");
            Collection<ConceptAnswer> cons = tsActivityConcept.getAnswers(false);
            for (ConceptAnswer c : cons){
            	activityAnswers.add(c.getAnswerConcept());
            }
            if (activityAnswers == null || activityAnswers.size() == 0)
                    log.warn("No concept answers found for treatment supporter activity.");
            map.put("activityAnswers", activityAnswers);
        }
        return map ;
    }


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object, BindException exceptions) throws Exception {       
        
        if (Context.isAuthenticated()){
            String action = request.getParameter("submit");
            
            MessageSourceAccessor msa = this.getMessageSourceAccessor();
            if (action.equals(msa.getMessage("mdrtb.save"))){
                MdrtbTreatmentSupporter mts = (MdrtbTreatmentSupporter) object;
                Person p = mts.getPerson();
                List<Obs> o = mts.getPhoneNumbers();
                Obs activeObs = mts.getActive();
                
                if (p.getPersonCreator() == null)
                    p.setPersonCreator(Context.getAuthenticatedUser());
                if (p.getPersonDateCreated() == null)
                    p.setPersonDateCreated(new Date());
                p.setPersonVoided(false);
                
                if (p.getBirthdate() != null && (p.getGivenName() != null || p.getFamilyName() != null)){
                    
                    //make sure person is a treatment supporter:
                    
                        List<PersonAttribute> pas = p.getActiveAttributes();
                        boolean paTest = false;
                        String treatSupAttributeTypeString = Context.getAdministrationService().getGlobalProperty("mdrtb.treatment_supporter_person_attribute_type");
                        PersonService ps = Context.getPersonService();
                        PersonAttributeType pat = ps.getPersonAttributeTypeByName(treatSupAttributeTypeString);
                        for (PersonAttribute pa:pas){
                            if (pa.getAttributeType().getPersonAttributeTypeId().intValue() == pat.getPersonAttributeTypeId().intValue())
                                paTest = true;
                        }
                        if (!paTest){
                            PersonAttribute paNew = new PersonAttribute();
                            paNew.setAttributeType(pat);
                            paNew.setCreator(Context.getAuthenticatedUser());
                            paNew.setDateCreated(new Date());
                            paNew.setVoided(false);
                            paNew.setValue("treatment supporter");
                            paNew.setPerson(p);
                            p.addAttribute(paNew);
                        }
                        
                        for (PersonAddress pa : p.getAddresses()){
                            if (pa.getCreator() == null)
                            pa.setCreator(Context.getAuthenticatedUser());
                            if (pa.getDateCreated() == null)
                            pa.setDateCreated(new Date());
                            if (pa.getVoided() == null)
                            pa.setVoided(false);
                        }
                        
                        for (PersonName pn : p.getNames()){
                            if (pn.getCreator() == null)
                            pn.setCreator(Context.getAuthenticatedUser());
                            if (pn.getDateCreated() == null)
                            pn.setDateCreated(new Date());
                            if (pn.getVoided() == null)
                            pn.setVoided(false);
                            
                        }
                    
                    ps.savePerson(p);
                    ObsService os = Context.getObsService();
                    for (Obs ob: o){
                        ob.setObsDatetime(new Date());                    
                        ob.setPerson(p);
                        os.saveObs(ob, "updating treatment supporter phone number");
                    }
                    
                    //update the TS activity
                    activeObs.setPerson(p);
                    activeObs.setObsDatetime(new Date());
                    os.saveObs(activeObs, "updating treatment supporter activity status");
                    
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
       
       if (Context.isAuthenticated()){
           MdrtbTreatmentSupporter p = new MdrtbTreatmentSupporter();
           MdrtbService ms = (MdrtbService) Context.getService(MdrtbService.class);
           Concept phoneConcept = (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TELEPHONE_NUMBER));
           ConceptService cs = Context.getConceptService();
           
           //get the concept for TS Activity
           Concept tsActivityConcept = (Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TREATMENT_SUPPORTER_CURRENTLY_ACTIVE));
          
           ObsService os = Context.getObsService();
           String personId = request.getParameter("personId");
           if (personId != null && !personId.equals("")){
               try{
               Person pers = Context.getPersonService().getPerson(Integer.valueOf(personId));
               pers.getAddresses();
               p.setPerson(pers);
               //get phone number routine:
               List<Obs> oPhones = os.getObservationsByPersonAndConcept(pers, phoneConcept);
               for (Obs o: oPhones){
                   if (!o.getVoided())
                       p.addPhoneObs(o);
               }
               
               //set the TS Activity if it has been assigned
               List<Obs> oActivity = os.getObservationsByPersonAndConcept(pers, tsActivityConcept);
               if(oActivity.size()==1) {
            	   Obs o = oActivity.get(0);
            	   if(!o.getVoided()) {
            		   p.setActive(oActivity.get(0));
            	   }
            	   
               }
              
               
               } catch (Exception ex){
                   log.error("Failed to load person.", ex);
               }
           } 
           
           if (p.getPhoneNumbers().size() == 0){
               Obs o = new Obs();
               o.setConcept(phoneConcept);
               o.setCreator(Context.getAuthenticatedUser());
               o.setDateCreated(new Date());
               o.setVoided(false);
               if (o.getLocation() == null)
                   o.setLocation(Context.getLocationService().getLocation("Unknown Location"));
               p.addPhoneObs(o);
           }
           
           //if not assigned, set to YES by default
           if(p.getActive() == null) {
        	   Obs o = new Obs();
        	   o.setConcept(tsActivityConcept);
        	   o.setCreator(Context.getAuthenticatedUser());
        	   o.setDateCreated(new Date());
        	   o.setVoided(false);
        	   if(o.getLocation()==null)
        		   o.setLocation(Context.getLocationService().getLocation("Unknown Location"));
        	   Concept yesConcept = cs.getConceptByName(YES);
        	   o.setValueCoded(yesConcept);
        	   p.setActive(o);
           }
           return p;
       } else return "";
       
    }    
    
}
