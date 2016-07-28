package org.openmrs.module.dotsreports.reporting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.regimen.Regimen;
import org.openmrs.module.dotsreports.regimen.RegimenHistory;
import org.openmrs.module.dotsreports.regimen.RegimenUtils;
import org.openmrs.module.dotsreports.reporting.data.Cohorts;
import org.openmrs.module.dotsreports.reporting.excel.SheetHelper;
import org.openmrs.module.dotsreports.reporting.excel.StyleHelper;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

public class PatientSummaryUtil {
	
	protected static Log log = LogFactory.getLog(PatientSummaryUtil.class);
    
    public static final String PATIENT_ID = "patientId";
    public static final String FULL_NAME = "fullName";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String BIRTHDATE = "birthdate";
    public static final String DEAD = "dead";
    public static final String DEATH_DATE = "deathDate";
    public static final String CAUSE_OF_DEATH = "causeOfDeath";
    public static final String PRIMARY_IDENTIFIER = "primaryIdentifier";
    
    public static final String STARTING_TB_REGIMEN = "startingTbRegimen";
    public static final String STARTING_TB_REGIMEN_DATE = "startingTbRegimenDate";
    public static final String STARTING_TB_REGIMEN_TYPE = "startingTbRegimenType";
    public static final String CURRENT_TB_REGIMEN = "currentTbRegimen";
    public static final String CURRENT_TB_REGIMEN_DATE = "currentTbRegimenDate";
    public static final String CURRENT_TB_REGIMEN_TYPE = "currentTbRegimenType";
    public static final String CURRENT_HIV_REGIMEN = "currentHivRegimen";
    
    public static final String ADDRESS_1 = "address1";
    public static final String CITY_VILLAGE = "cityVillage";
    public static final String FULL_ADDRESS = "fullAddress";
    
    public static final String RESISTANCE_LIST = "resistanceProfile";
    
    public static final String HIV_TEST_RESULT = TbConcepts.RESULT_OF_HIV_TEST[0];
    public static final String SMEAR_RESULT = TbConcepts.SMEAR_RESULT[0];
    public static final String CULTURE_RESULT = TbConcepts.CULTURE_RESULT[0];
    public static final String WEIGHT_RESULT = TbConcepts.WEIGHT[0];
    public static final String PULSE_RESULT = TbConcepts.PULSE[0];
    public static final String TEMPERATURE_RESULT = TbConcepts.TEMPERATURE[0];
    public static final String RESPIRATORY_RATE_RESULT = TbConcepts.RESPIRATORY_RATE[0];
    public static final String SYSTOLIC_BLOOD_PRESSURE_RESULT = TbConcepts.SYSTOLIC_BLOOD_PRESSURE[0];
    
    public static final String[] DEMOGRAPHICS_KEYS = {
    	PATIENT_ID, FULL_NAME, FIRST_NAME, LAST_NAME, GENDER, AGE, BIRTHDATE, DEAD, DEATH_DATE, CAUSE_OF_DEATH, 
    	ADDRESS_1, CITY_VILLAGE, FULL_ADDRESS
    };
    
    public static final String[] TB_REGIMEN_KEYS = {
    	STARTING_TB_REGIMEN, STARTING_TB_REGIMEN_DATE, STARTING_TB_REGIMEN_TYPE, 
    	CURRENT_TB_REGIMEN, CURRENT_TB_REGIMEN_DATE, CURRENT_TB_REGIMEN_TYPE
    };
    
    public static final String[] HIV_REGIMEN_KEYS = {
    	CURRENT_HIV_REGIMEN
    };
    
    public static final String[] OBS_KEYS = {
    	HIV_TEST_RESULT, SMEAR_RESULT, CULTURE_RESULT, WEIGHT_RESULT, PULSE_RESULT, TEMPERATURE_RESULT, 
    	RESPIRATORY_RATE_RESULT, SYSTOLIC_BLOOD_PRESSURE_RESULT
    };
    
    public static final String[] TEST_KEYS = {
    	RESISTANCE_LIST
    };
    
    public static final Map<String, String> getAvailableKeys() {
    	Map<String, String> keys = new LinkedHashMap<String, String>();
    	
    	for (String s : DEMOGRAPHICS_KEYS) {
    		keys.put(s, MessageUtil.translate("dotsreports."+s));
    	}
    	String primaryIdType = Context.getAdministrationService().getGlobalProperty("dotsreports.primaryPatientIdentifierType");
    	for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
    		keys.put("identifier."+pit.getName(), pit.getName());
    		if (pit.getName().equals(primaryIdType) || pit.getId().toString().equals(primaryIdType)) {
    			keys.put(PRIMARY_IDENTIFIER, MessageUtil.translate("dotsreports.identifier"));
    		}
    	}
    	for (PersonAttributeType pat : Context.getPersonService().getAllPersonAttributeTypes()) {
    		keys.put("attribute."+pat.getName(), pat.getName());
    	}
    	for (ProgramWorkflow wf : Context.getService(TbService.class).getTbProgram().getWorkflows()) {
    		String cn = formatConcept(wf.getConcept());
    		keys.put("state."+wf.getId(), cn);
    	}
    	for (String s : TB_REGIMEN_KEYS) {
    		keys.put(s, MessageUtil.translate("dotsreports."+s));
    	}
    	for (String s : HIV_REGIMEN_KEYS) {
    		keys.put(s, MessageUtil.translate("dotsreports."+s));
    	}
    	for (String s : OBS_KEYS) {
    		Concept c = Context.getService(TbService.class).getConcept(s); 
    		keys.put("obs."+s+".latest", MessageUtil.translate("dotsreports.latest") + " " + formatConcept(c));
    		keys.put("obs."+s+".latestDate", MessageUtil.translate("dotsreports.latest") + " " + formatConcept(c) + " " + MessageUtil.translate("dotsreports.date"));
    	}
    	for (String s : TEST_KEYS) {
    		keys.put(s, MessageUtil.translate("dotsreports."+s));
    	}
    	return keys;
    }
    
    public static Cohort getCohort(Integer patientId) {
    	Cohort c = new Cohort();
    	c.addMember(patientId);
    	return c;
    }
    
    public static Cohort getCohort(Location location) {
    	
    	TbService svc = Context.getService(TbService.class);
    	Date now = new Date();
    	
    	Program mdrProgram = svc.getTbProgram();
    	Cohort cohort = Context.getPatientSetService().getPatientsInProgram(mdrProgram, now, now);
        
        if (location != null) {
        	CohortDefinition cd = Cohorts.getLocationFilter(location, now, now, false);
	        Cohort atLocation;
            try {
	            atLocation = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext());
            }
            catch (EvaluationException e) {
	            throw new MdrtbAPIException("Unable to evalute location cohort",e);
            }
	        cohort = Cohort.intersect(cohort, atLocation);
        }
        return cohort;
    }

    public static Map<Integer, Map<String, Object>> getPatientSummaryData(Cohort cohort, List<String> columns, Date effectiveDate) {
    	
    	long ms = System.currentTimeMillis();
    	Map<Integer, Map<String, Object>> byPatient = new HashMap<Integer, Map<String, Object>>();
    	
    	if (columns == null) {
    		columns = new ArrayList<String>(getAvailableKeys().keySet());
    	}
    	if (effectiveDate == null) {
    		effectiveDate = new Date();
    	}
    	if (cohort == null) {
    		cohort = new Cohort();
    	}
 
        List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());
        List<Person> persons = new ArrayList<Person>();
        for (Patient p : patients) {
        	persons.add(p);
        }
        
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(cohort);
        Program mdrProgram = Context.getService(TbService.class).getTbProgram();
        
        Map<Integer, String> resistanceProfiles = null;
        Map<Concept, Map<Integer, Result>> logicResults = new HashMap<Concept, Map<Integer, Result>>();
        Map<ProgramWorkflow, Map<Integer, Result>> stateResults = new HashMap<ProgramWorkflow, Map<Integer, Result>>();
        
        log.warn("Retrieved Patients in: " + ((System.currentTimeMillis() - ms)/1000) + " seconds");
        ms = System.currentTimeMillis();
        
        for (Patient p : patients) {
        	log.debug("Evaluating: " + p.getPatientId());
            Map<String, Object> map = byPatient.get(p.getPatientId());
            if (map == null) {
                map = new HashMap<String, Object>();
                byPatient.put(p.getPatientId(), map);
            }
            
            map.put(PATIENT_ID, p.getPatientId());
        	
        	if (ObjectUtil.containsAny(columns, FULL_NAME, FIRST_NAME, LAST_NAME)) {
                PersonName pn = p.getPersonName();
                if (pn != null) {
                    String lastNameUpper = ObjectUtil.nvl(pn.getFamilyName(), "").toUpperCase();
                    String firstName = ObjectUtil.toString(" ", pn.getGivenName(), pn.getMiddleName());
                    map.put(FIRST_NAME, firstName);
                    map.put(LAST_NAME, lastNameUpper);
                    map.put(FULL_NAME, ObjectUtil.toString(", ", lastNameUpper, firstName));
            	}
        	}
        	
        	if (ObjectUtil.containsAny(columns, GENDER)) {
        		map.put(GENDER, MessageUtil.translate("mdrtb.gender."+p.getGender()));
        	}
        	
        	if (ObjectUtil.containsAny(columns, BIRTHDATE, AGE)) {
        		map.put(BIRTHDATE, formatDate(p.getBirthdate(), p.getBirthdateEstimated(), "mdrtb.unknown"));
        		map.put(AGE, p.getAge(effectiveDate));
        	}
        	
        	if (ObjectUtil.containsAny(columns, DEAD, DEATH_DATE, CAUSE_OF_DEATH)) {
        		if (p.getDead() && (p.getDeathDate() == null || p.getDeathDate().compareTo(effectiveDate) <= 0)) {
            		map.put(DEAD, MessageUtil.translate("general.true"));
            		map.put(DEATH_DATE, formatDate(p.getDeathDate(), null, "mdrtb.unknown"));
            		map.put(CAUSE_OF_DEATH, formatConcept(p.getCauseOfDeath()));
        		}
        		else {
        			map.put(DEAD, MessageUtil.translate("general.false"));
        		}
        	}
        	
        	if (ObjectUtil.containsAny(columns, FULL_ADDRESS, ADDRESS_1, CITY_VILLAGE)) {
        		PersonAddress pa = p.getPersonAddress();
        		if (pa != null) {
        			map.put(ADDRESS_1, pa.getAddress1());
        			map.put(CITY_VILLAGE, pa.getCityVillage());
        			map.put(FULL_ADDRESS, ObjectUtil.toString(", ", pa.getAddress1(), pa.getCityVillage()));
        		}
        	}
        	
        	if (ObjectUtil.containsAny(columns, PRIMARY_IDENTIFIER)) {
        		String primaryIdType = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
        		PatientIdentifier pi = p.getPatientIdentifier(primaryIdType);
        		map.put(PRIMARY_IDENTIFIER, (pi == null ? "" : pi.getIdentifier()));
        	}
        	
        	for (String c : columns) {
        		if (c.startsWith("identifier.")) {
        			PatientIdentifier pi = p.getPatientIdentifier(c.split("\\.")[1]);
        			map.put(c, (pi == null ? "" : pi.getIdentifier()));
        		}
        		else if (c.startsWith("attribute.")) {
        			PersonAttribute pa = p.getAttribute(c.split("\\.")[1]);
        			map.put(c, (pa == null ? "" : pa.getHydratedObject()));
        		}
        		else if (c.startsWith("obs.")) {
        			String[] split = c.split("\\.");
        			if (split.length < 3 || split[2].startsWith("latest")) {
        				Concept question = Context.getService(TbService.class).getConcept(split[1]);
        				Map<Integer, Result> m = logicResults.get(question);
        				if (m == null) {
        					m = MdrtbQueryService.getLatestObsResults(cohort, question);
        					logicResults.put(question, m);
        				}
        				Result r = m.get(p.getPatientId());
        				if (r != null) {
        					map.put(split[0]+"."+split[1]+".latest", formatObject(r.getResultObject(), "Error"));
        					map.put(split[0]+"."+split[1]+".latestDate", formatDate(r.getResultDate(), null, null));
        				}
        			}
        		}
        		else if (c.startsWith("state.")) {
        			String[] split = c.split("\\.");
        			String wfId = split[1];
        			ProgramWorkflow wf = mdrProgram.getWorkflow(Integer.parseInt(wfId));
    				Map<Integer, Result> m = stateResults.get(wf);
    				if (m == null) {
    					m = MdrtbQueryService.getActiveState(cohort, wf);
    					stateResults.put(wf, m);
    				}
    				Result r = m.get(p.getPatientId());
    				if (r != null) {
    					map.put(c, formatConcept(r.toConcept()) + " (" + formatDate(r.getResultDate(), null, null) + ")");
    				}
        		}
        	}
        	
        	if (ObjectUtil.containsAny(columns, RESISTANCE_LIST)) {
        		if (resistanceProfiles == null) {
        			resistanceProfiles = MdrtbQueryService.getResistanceProfilesByPatient(context, effectiveDate);
        		}
        		map.put(RESISTANCE_LIST, ObjectUtil.nvlStr(resistanceProfiles.get(p.getPatientId()), ""));
        	}
        	
        	if (ObjectUtil.containsAny(columns, TB_REGIMEN_KEYS)) {
        		RegimenHistory tbHistory = RegimenUtils.getTbRegimenHistory(p);
        		if (ObjectUtil.containsAny(columns, STARTING_TB_REGIMEN, STARTING_TB_REGIMEN_DATE, STARTING_TB_REGIMEN_TYPE)) {
    				Regimen r = tbHistory.getStartingRegimen();
    				if (r != null) {
	        			map.put(STARTING_TB_REGIMEN, formatObject(r, ""));
	    				map.put(STARTING_TB_REGIMEN_DATE, formatDate(r.getStartDate(), null, null));
	    				map.put(STARTING_TB_REGIMEN_TYPE, formatObject(r.getReasonForStarting(), ""));
    				}
        		}
        		if (ObjectUtil.containsAny(columns, CURRENT_TB_REGIMEN, CURRENT_TB_REGIMEN_DATE, CURRENT_TB_REGIMEN_TYPE)) {
    				Regimen r = tbHistory.getActiveRegimen();
        			map.put(CURRENT_TB_REGIMEN, formatObject(r, ""));
    				map.put(CURRENT_TB_REGIMEN_DATE, formatDate(r.getStartDate(), null, null));
    				map.put(CURRENT_TB_REGIMEN_TYPE, formatObject(r.getReasonForStarting(), ""));
        		}
        	}
    		if (ObjectUtil.containsAny(columns, CURRENT_HIV_REGIMEN)) {
    			RegimenHistory hivHistory = RegimenUtils.getHivRegimenHistory(p);
				Regimen r = hivHistory.getActiveRegimen();
    			map.put(CURRENT_HIV_REGIMEN, formatObject(r, ""));
    		}
        }
        
        ms = System.currentTimeMillis() - ms;
        log.warn("Retrieved remaining results in : " + ms + " ms");
        return byPatient;
    }
    
    public static String getConceptDisplay(Concept c, ConceptNameTag tag) {
		String s = c.getDisplayString();
		ConceptName name = null;
		if (tag != null) {
			name = c.findNameTaggedWith(tag);
		}
		if (name == null) {
			name = c.getShortestName(Context.getLocale(), false);
		}
		if (name != null) {
			s = name.getName();
		}
		return s;
    }
    
	public static void outputToExcel(HttpServletResponse response, Cohort c, List<String> columnList) throws IOException {
    	
		Date runDate = new Date();
    	Map<String, String> availableKeys = PatientSummaryUtil.getAvailableKeys();
    	Map<Integer, Map<String, Object>> data = PatientSummaryUtil.getPatientSummaryData(c, columnList, runDate);
    	
		String title = "export";

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(title);
		SheetHelper sh = new SheetHelper(sheet);
		StyleHelper helper = new StyleHelper(wb);
		
		sh.addCell("Patient Export " + " (" + data.size() + "), generated " + formatDate(runDate, null, null), helper.getStyle("bold,italic"));
		sh.nextRow();
		sh.nextRow();
		
		for (String column : columnList) {
			sh.addCell(availableKeys.get(column), helper.getStyle("bold"));
		}
		sh.nextRow();
		
		for (Map<String, Object> row : data.values()) {
			for (String column : columnList) {
				Object val = row.get(column);
				if (val != null && val instanceof Date) {
					sh.addCell((Date)val, helper.getStyle("date"));
				}
				else {
					sh.addCell(formatObject(val, ""));
				}
			}
			sh.nextRow();
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=export-" + System.currentTimeMillis() + ".xls");
		wb.write(response.getOutputStream());
	}
	
    public static String formatDate(Date d, Boolean estimated, String emptyMessage) {
    	if (d != null) {
    		DateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Context.getLocale());
    		return (estimated == Boolean.TRUE ? "~" : "") + df.format(d);
    	}
    	return (emptyMessage == null ? "" : MessageUtil.translate(emptyMessage));
    }
    
    public static String formatConcept(Concept c) {
    	if (c == null) {
    		return "";
    	}
    	return c.getBestShortName(Context.getLocale()).getName();
    }
    
	public static int compareDates(Date d1, Date d2, String format) {
		DateFormat df = new SimpleDateFormat(format, Context.getLocale());
		String s1 = df.format(d1);
		String s2 = df.format(d2);
		return s1.compareTo(s2);
	}
	
	public static String formatObject(Object o, String defaultVal) {
		if (o == null) {
			return defaultVal;
		}
		if (o instanceof Date) {
			return formatDate((Date)o, null, null);
		}
		if (o instanceof Regimen) {
			return RegimenUtils.formatRegimenGenerics((Regimen)o, " + ", defaultVal);
		}
		if (o instanceof Obs) {
			return formatObject(((Obs)o).getValueCoded(), defaultVal);
		}
		if (o instanceof Concept) {
			return formatConcept((Concept)o);
		}
		if (o instanceof OpenmrsMetadata) {
			return ((OpenmrsMetadata)o).getName();
		}
		if (o instanceof Collection) {
			return OpenmrsUtil.join((Collection<?>)o, ", ");
		}
		if (o instanceof Result) {
			return formatObject(((Result)o).getResultObject(), defaultVal);
		}
		return o.toString();
	}
}
