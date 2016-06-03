package org.openmrs.module.dotsreports.regimen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utility method for use with Regimens
 */
public class RegimenUtils {

    protected static final Log log = LogFactory.getLog(RegimenUtils.class);
    
    /**
     * @return a List of Drugs whose Concept matches a Concept in the passed set
     */
    public static List<Drug> getDrugsWithGenericInSet(Concept drugSet) {
    	List<Drug> ret = new ArrayList<Drug>();
    	List<Drug> allDrugs = Context.getConceptService().getAllDrugs();
    	if (drugSet == null) {
    		return allDrugs;
    	}
    	List<Concept> generics = Context.getConceptService().getConceptsByConceptSet(drugSet);
    	for (Drug drug : allDrugs) {
    		if (generics.contains(drug.getConcept())) {
    			ret.add(drug);
    		}
    	}
    	return ret;
    }
    
    public static List<Concept> getGenericsForDrugSet(String drugSet) {	
		List<Concept> generics = new ArrayList<Concept>();
		if (ObjectUtil.isNull(drugSet) || drugSet.equals("*")) {
			ConceptClass drugClass = Context.getConceptService().getConceptClass(OpenmrsConstants.CONCEPT_CLASS_DRUG);
			generics = Context.getConceptService().getConceptsByClass(drugClass);
			if (drugSet.equals("*")) {
				for (RegimenType t : RegimenUtils.getRegimenTypes()) {
					if (!"*".equals(t.getDrugSet())) {
						Concept c = getMdrtbService().findMatchingConcept(drugSet);
						generics.removeAll(Context.getConceptService().getConceptsByConceptSet(c));
					}
				}
			}
		}
		else {
			Concept c = getMdrtbService().findMatchingConcept(drugSet);
			generics = Context.getConceptService().getConceptsByConceptSet(c);
		}
		return generics;
    }
    
    /**
     * @return the List of configured regimen types, from global property if configured, or default if not
     */
    @SuppressWarnings("unchecked")
    public static List<RegimenType> getRegimenTypes() {
    	
    	Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
    	
		XStream xstream = new XStream(new DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("regimenType", RegimenType.class);
		xstream.alias("regimenSuggestion", RegimenSuggestion.class);
		xstream.alias("drugSuggestion", DrugSuggestion.class);
    	
    	String xml = Context.getAdministrationService().getGlobalProperty("mdrtb.regimenTypeConfiguration");
    	if (ObjectUtil.isNull(xml)) {
    		InputStream is = null;
    		try {
    			is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/mdrtb/regimen/RegimenTypeConfiguration.xml");
    			//is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/mdrtb/regimen/RegimenTypeConfiguration_" + Context.getLocale().getLanguage() + ".xml");
    			log.debug("Returning regimen types from default configuration...");
    			return (List<RegimenType>)xstream.fromXML(is);
    		}
    		finally {
    			try {
    				is.close();
    			}
    			catch (Exception e) {}
    		}
    	}
    	
    	log.debug("Returning regimen types from global property configuration...");
		return (List<RegimenType>)xstream.fromXML(xml);
    }
    
    /**
     * @return the regimen type for the passed name
     */
    public static RegimenType getRegimenType(String name) {
    	for (RegimenType type : getRegimenTypes()) {
    		if (type.getName().equals(name)) {
    			return type;
    		}
    	}
    	return null;
    }
    
    /**
     * @return the full regimen history for the patient, categorized by type
     */
    public static Map<String, RegimenHistory> getRegimenHistory(Patient patient) {
    	Map<String, RegimenHistory> m = new LinkedHashMap<String, RegimenHistory>();

    	List<DrugOrder> orders = Context.getOrderService().getDrugOrdersByPatient(patient);
    	
    	// Add a RegimenHistory to the returned Map for each defined Regimen Type
    	for (RegimenType type : RegimenUtils.getRegimenTypes()) {
    		List<Concept> concepts = null;
    		if (type.getDrugSet() != null) {
    			concepts = getMdrtbService().getDrugsInSet(type.getDrugSet());
    		}
    		RegimenHistory history = new RegimenHistory(patient, type);
    		
    		// Add each DrugOrder that is appropriate for the Regimen Type, and remove it so that it is only found within one type
    		for (Iterator<DrugOrder> i = orders.iterator(); i.hasNext();) {
    			DrugOrder o = i.next();
    			if (concepts == null || concepts.contains(o.getConcept())) {
    				history.addDrugOrder(o);
    				i.remove();
    			}
    		}
    		
    		// Add each Observation that is appropriate for the Regimen Type
    		if (type.getReasonForStartingQuestion() != null) {
    			Concept c = getMdrtbService().getConcept(type.getReasonForStartingQuestion());
	    		for (Obs o : Context.getObsService().getObservationsByPersonAndConcept(patient, c)) {
	    			history.addReasonForStarting(o);
	    		}
    		}
    		
    		m.put(type.getName(), history);
    	}
    	return m;
    }
    
    /**
     * @param patient
     * @return the Tuberculosis Regimens for a Patient
     */
    public static RegimenHistory getTbRegimenHistory(Patient patient) {   	
    	return getRegimenHistory(patient).get("tb");
    }
    
    /**
     * @param patient
     * @return the HIV Regimens for a Patient
     */
    public static RegimenHistory getHivRegimenHistory(Patient patient) {
    	return getRegimenHistory(patient).get("hiv");	
    }
	
    /**
     * @return a String representation of the Generic drugs within this Regimen, separated by the passed String
     */
    public static String formatRegimenGenerics(Object regimen, String separator, String emptyCode) {
    	return formatConcepts(((Regimen)regimen).getUniqueGenerics(), separator, emptyCode);
    }
    
    /**
     * @return a String representation of the Generic drugs within the passed orders, separated by the passed String
     */
    public static String formatDrugOrders(Collection<DrugOrder> orders, String separator, String emptyCode) {
    	Set<Concept> s = new HashSet<Concept>();
    	for (DrugOrder d : orders) {
    		s.add(d.getConcept());
    	}
    	return formatConcepts(s, separator, emptyCode);
    }
    
    /**
     * @return a String representation of the Obs within the passed orders, separated by the passed String
     */
    public static String formatCodedObs(Collection<Obs> obs, String separator, String emptyCode) {
    	Set<Concept> s = new HashSet<Concept>();
    	for (Obs o : obs) {
    		if (o.getValueCoded() != null) {
    			s.add(o.getValueCoded());
    		}
    	}
    	return formatConcepts(s, separator, emptyCode);
    }
    
    /**
     * @return the formatted obs valueCoded
     */
    public static String formatCodedObs(Obs obs, String emptyCode) {
    	if (obs == null || obs.getValueCoded() == null) {
    		return emptyCode;
    	}
    	return formatCodedObs(Arrays.asList(obs), "", emptyCode);
    }
    
    /**
     * @return a String representation of the passed Concepts, using best short name, separated by the passed String
     */
    public static String formatConcepts(Collection<Concept> concepts, String separator, String emptyCode) {
    	if (concepts == null || concepts.isEmpty()) {
    		if (ObjectUtil.notNull(emptyCode)) {
    			return MessageUtil.translate(emptyCode);
    		}
    		return "";
    	}
    	List<String> shortNames = new ArrayList<String>();
    	for (Concept c : concepts) {
    		shortNames.add(c.getBestShortName(Context.getLocale()).getName());
    	}
    	Collections.sort(shortNames);
    	return OpenmrsUtil.join(shortNames, separator);
    }

    /** 
     * Strips the time component from a Calendar object, leaving only the date component
     */
    public static void stripTimeComponent(Calendar cal) {
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Strips the time component of a Date object, leaving only the date component
     */
    public static void stripTimeComponent(Date date) {
    	if (date == null) {
    		return;
    	}
    	else {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		stripTimeComponent(cal);
    		date = cal.getTime();
    	}
    }
    
    /**
     * @return the TbService
     */
    private static TbService getMdrtbService() {
    	return Context.getService(TbService.class);
    }
}