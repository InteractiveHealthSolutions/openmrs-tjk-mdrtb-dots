package org.openmrs.module.dotsreports.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;

import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.util.OpenmrsUtil;

/**
 * Queries to support MDR-TB Module Reporting
 */
public class MdrtbQueryService {

    protected static final Log log = LogFactory.getLog(MdrtbQueryService.class);

    /**
     * @return the Cohort of patients whose test results indicate resistance to at least one of the passed drugs
     */
    public static Cohort getPatientsResistantToAnyDrugs(EvaluationContext context, Date minResultDate, Date maxResultDate, Concept... drugs) {
    	
    	Integer resistant = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT_TO_TB_DRUG).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + resistant + " ");
    	addOptionalDateClause(q, "and o.obs_datetime >= ", minResultDate);
    	addOptionalDateClause(q, "and o.obs_datetime <= ", maxResultDate);
    	q.append("and o.value_coded in (");
		for (int i=0; i<drugs.length; i++) {
			q.append(drugs[i].getConceptId());
			if ((i+1)<drugs.length) {
				q.append(",");
			}
		}	
    	q.append(") ");
    	
    	return executeQuery(q.toString(), context);
    }
    
    /**
     * Returns the patients who have tested resistant for x or more drugs during the given time period, where x = numberOfDrugs
     */
    public static Cohort getPatientsResistantToNumberOfDrugs(EvaluationContext context, Date minResultDate, Date maxResultDate, Integer numberOfDrugs) { 
    
    	Integer resistant = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT_TO_TB_DRUG).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select patient from ");
    	q.append("((select patient, count(*) as resistance_count from ");
    	q.append("((select p.patient_id as patient, o.value_coded from patient p, obs o where p.patient_id = o.person_id and p.voided =0 ");
    	q.append("and o.voided = 0 and o.concept_id = '" + resistant + "' ");
    	addOptionalDateClause(q, "and o.obs_datetime >= ", minResultDate);
    	addOptionalDateClause(q, "and o.obs_datetime <= ", maxResultDate);
    	q.append("group by o.value_coded,p.patient_id) as inner_table) ");
    	q.append("group by patient) as inner_table_2) ");
    	q.append("where resistance_count >= " + numberOfDrugs);
    	
    	return executeQuery(q.toString(), context);
    }
    
    public static Cohort getCohortWithResistanceProfile(EvaluationContext context, Date maxResultDate, String profile) {
    	Map<String, Cohort> profiles = getResistanceProfiles(context, maxResultDate);
    	return ObjectUtil.nvl(profiles.get(profile), new Cohort());
    }
    
    public static Map<Integer, String> getResistanceProfilesByPatient(EvaluationContext context, Date maxResultDate) {
    	Map<Integer, String> ret = new HashMap<Integer, String>();
    	for (Map.Entry<String, Cohort> e : getResistanceProfiles(context, maxResultDate).entrySet()) {
    		for (Integer pId : e.getValue().getMemberIds()) {
    			ret.put(pId, e.getKey());
    		}
    	}
    	return ret;
    }
    
    /**
     * @return the Cohort of patients whose test results indicate resistance to at least one of the passed drugs
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Cohort> getResistanceProfiles(EvaluationContext context, Date maxResultDate) {
    	
    	String key = "resistanceProfile::" + DateUtil.formatDate(maxResultDate, "yyyy-MM-dd");
    	Map<String, Cohort> profiles = (Map<String, Cohort>)context.getFromCache(key);
    	if (profiles != null) {
    		return profiles;
    	}
    	
    	Map<String, Cohort> ret = new TreeMap<String, Cohort>();
    	
    	Integer resistant = Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT_TO_TB_DRUG).getId(); 
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id, o.value_coded, n.name ");
    	q.append("from 		patient p, obs o, concept_name n ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and	 	o.value_coded = n.concept_id ");
    	q.append("and		o.concept_id = " + resistant + " ");
    	addOptionalDateClause(q, "and o.obs_datetime <= ", maxResultDate);
    	if (context.getBaseCohort() != null) {
    		q.append("and	p.patient_id in (" + context.getBaseCohort().getCommaSeparatedPatientIds() + ")");
    	}
    	List<List<Object>> results = Context.getAdministrationService().executeSQL(q.toString(), true);
    	Map<Object, Map<Object, String>> patDrugs = new HashMap<Object, Map<Object, String>>();
    	for (List<Object> l : results) {
    		Object pId = l.get(0);
    		Object cId = l.get(1);
    		String cName = l.get(2).toString();
    		Map<Object, String> patMap = patDrugs.get(pId);
    		if (patMap == null) {
    			patMap = new HashMap<Object, String>();
    			patDrugs.put(pId, patMap);
    		}
    		String curr = patMap.get(cId);
    		if (curr == null || curr.length() > cName.length()) {
    			patMap.put(cId, cName);
    		}
    	}
    	
    	Comparator<String> dstComparator = new Comparator<String>() {
    		final List<String> order = Arrays.asList(new String[]{"H","R","E","Z","S"});
			public int compare(String s1, String s2) {
				int i1 = order.indexOf(s1);
				String c1 = i1 == -1 ? s1 : "AAAA" + i1;
				int i2 = order.indexOf(s2);
				String c2 = i2 == -1 ? s2 : "AAAA" + i2;
				return c1.compareTo(c2);
			}
    	};
    	
    	for (Object pId : patDrugs.keySet()) {
    		Set<String> drugNames = new TreeSet<String>(dstComparator);
    		drugNames.addAll(patDrugs.get(pId).values());
    		String s = OpenmrsUtil.join(drugNames, "+");
    		Cohort c = ret.get(s);
    		if (c == null) {
    			c = new Cohort();
    		}
    		c.addMember(new Integer(pId.toString()));
    		ret.put(s, c);
    	}
    	
    	return ret;
    }
	
	/**
	 * @see org.openmrs.module.mdrtb.db.MdrtbCohortDAO#getPatientsFirstStartingDrugs(java.util.Date, java.util.Date, Concept)
	 */
	public static Cohort getPatientsFirstStartingDrugs(EvaluationContext context, Date fromDate, Date toDate, Concept drugSet) {
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select	o.patient_id ");
    	q.append("from		patient p, orders o, concept_set s ");
    	q.append("where		p.patient_id = o.patient_id ");
    	q.append("and		p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = s.concept_id and s.concept_set = " + drugSet.getConceptId() + " ");
    	q.append("group by	o.patient_id ");
    	q.append("having	1=1 ");
    	addOptionalDateClause(q, "and min(o.start_date) >= ", fromDate);
    	addOptionalDateClause(q, "and min(o.start_date) <= ", toDate);
    	
    	return executeQuery(q.toString(), context);
	}
	
	/**
	 * @return the most recent Obs Result for the given Cohort and Question
	 */
	public static Map<Integer, Result> getLatestObsResults(Cohort cohort, Concept question) {
		Map<Integer, Result> ret = new HashMap<Integer, Result>();
		
	   	StringBuilder q = new StringBuilder();
    	q.append("select	o.person_id, o.obs_datetime, o.value_coded, o.value_numeric, o.value_datetime ");
    	q.append("from		patient p, obs o ");
    	q.append("where		p.patient_id = o.person_id ");
    	q.append("and		p.voided = 0 and o.voided = 0 ");
    	q.append("and		(o.value_coded is not null or o.value_datetime is not null or o.value_numeric is not null) ");
    	q.append("and		o.concept_id = " + question.getConceptId() + " ");
    	q.append("order by	o.obs_datetime asc ");
    	
    	for (List<Object> row : Context.getAdministrationService().executeSQL(q.toString(), true)) {
    		Integer patientId = (Integer)row.get(0);
    		Result r = new Result();
    		r.setResultDate((Date) row.get(1));
    		r.setResultObject(ObjectUtil.coalesce(row.get(2), row.get(3), row.get(4)));
    		if (row.get(2) != null) {
        		Concept result = Context.getConceptService().getConcept((Integer)row.get(2));
        		r.setResultObject(result);    			
    		}
    		ret.put(patientId, r);
    	}
    	
    	return ret;
	}
	
	/**
	 * @return the active PatientState Result for the given ProgramWorkflow and Cohort
	 */
	public static Map<Integer, Result> getActiveState(Cohort cohort, ProgramWorkflow workflow) {
		Map<Integer, Result> ret = new HashMap<Integer, Result>();
		
	   	StringBuilder q = new StringBuilder();
    	q.append("select	p.patient_id, s.concept_id, ps.start_date ");
    	q.append("from		patient p, patient_program pp, patient_state ps, program_workflow_state s ");
    	q.append("where		p.patient_id = pp.patient_id ");
    	q.append("and		pp.patient_program_id = ps.patient_program_id ");
    	q.append("and		ps.state = s.program_workflow_state_id ");
    	q.append("and		p.voided = 0 and pp.voided = 0 and ps.voided = 0 ");
    	q.append("and		s.program_workflow_id = " + workflow.getProgramWorkflowId() + " ");
    	q.append("and		ps.end_date is null ");
    	
    	for (List<Object> row : Context.getAdministrationService().executeSQL(q.toString(), true)) {
    		Integer patientId = (Integer)row.get(0);
    		Result r = new Result();
    		Concept result = Context.getConceptService().getConcept((Integer)row.get(1));
    		r.setValueCoded(result);
    		r.setResultObject(result);
    		r.setResultDate((Date) row.get(2));
    		ret.put(patientId, r);
    	}
    	
    	return ret;
	}
	
	/**
	 * Utility method to get add a Date Clause
	 */
	private static void addOptionalDateClause(StringBuilder sb, String baseClause, Date d) {
		if (d != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Context.getLocale());
			sb.append(baseClause + "'" + df.format(d) + "' ");
		}
	}
	
	private static void addOptionalNumericClause(StringBuilder sb, String baseClause, Integer i) {
		if(i!=null)
			sb.append(baseClause  + i + " ");
	}
	
	/**
	 * Utility method to evaluate a query into a Cohort
	 */
	private static Cohort executeQuery(String query, EvaluationContext context) {
		Cohort c = Context.getService(CohortQueryService.class).executeSqlQuery(query, context.getParameterValues());
		if (context.getBaseCohort() != null) {
			c = Cohort.intersect(c, context.getBaseCohort());
		}
		return c;
	}
	
	/**
     * @return the Cohort of patients with DST rest results
     */
    public static Cohort getPatientsWithBasisForDiagnosis(EvaluationContext context, Date minResultDate, Date maxResultDate, String diagnosisType) {
    	
    	Integer basisForDiagnosis = Context.getService(TbService.class).getConcept(TbConcepts.BASIS_FOR_TB_DIAGNOSIS).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + basisForDiagnosis + " ");
    	addOptionalDateClause(q, "and o.obs_datetime >= ", minResultDate);
    	addOptionalDateClause(q, "and o.obs_datetime <= ", maxResultDate);
    	
    	if(diagnosisType!=null) {
    		q.append("and o.value_coded = " + Context.getService(TbService.class).getConcept(diagnosisType).getConceptId());
    	}
    	
    	
    	/*q.append("and o.value_coded in (");
		for (int i=0; i<drugs.length; i++) {
			q.append(drugs[i].getConceptId());
			if ((i+1)<drugs.length) {
				q.append(",");
			}
		}	
    	q.append(") ");*/
    	
    	return executeQuery(q.toString(), context);
    }
    
	/**
     * @return the Cohort of patients with DST rest results
     */
    public static Cohort getPatientsWithTxOutcome(EvaluationContext context, Date minResultDate, Date maxResultDate, String outcomeType) {
    	
    	Integer basisForDiagnosis = Context.getService(TbService.class).getConcept(TbConcepts.TB_TX_OUTCOME).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + basisForDiagnosis + " ");
    	addOptionalDateClause(q, "and o.obs_datetime >= ", minResultDate);
    	addOptionalDateClause(q, "and o.obs_datetime <= ", maxResultDate);
    	
    	if(outcomeType!=null) {
    		q.append("and o.value_coded = " + Context.getService(TbService.class).getConcept(outcomeType).getConceptId());
    	}
    	
    	
    	/*q.append("and o.value_coded in (");
		for (int i=0; i<drugs.length; i++) {
			q.append(drugs[i].getConceptId());
			if ((i+1)<drugs.length) {
				q.append(",");
			}
		}	
    	q.append(") ");*/
    	
    	return executeQuery(q.toString(), context);
    }
    
    /**
     * @return the Cohort of patients with DST rest results
     */
    public static Cohort getPatientsWithDOTSTxStartDate(EvaluationContext context, Date minResultDate, Date maxResultDate) {
    	
    	Integer txStartDate = Context.getService(TbService.class).getConcept(TbConcepts.DOTS_TREATMENT_START_DATE).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + txStartDate + " ");
    	addOptionalDateClause(q, "and o.value_datetime >= ", minResultDate);
    	addOptionalDateClause(q, "and o.value_datetime <= ", maxResultDate);
    	
    	
    	
    	/*q.append("and o.value_coded in (");
		for (int i=0; i<drugs.length; i++) {
			q.append(drugs[i].getConceptId());
			if ((i+1)<drugs.length) {
				q.append(",");
			}
		}	
    	q.append(") ");*/
    	
    	return executeQuery(q.toString(), context);
    }
    
    /**
     * @return the Cohort of patients with DST rest results
     */
    public static Cohort getPatientsWithAgeAtDOTSRegistration(EvaluationContext context, Integer minAge, Integer maxAge, Date startDate, Date endDate) {
    	
    	Integer ageAtDotsRegistration = Context.getService(TbService.class).getConcept(TbConcepts.AGE_AT_REGISTRATION).getConceptId();
    	
    	StringBuilder q = new StringBuilder();
    	q.append("select 	p.patient_id ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + ageAtDotsRegistration + " ");
    	addOptionalDateClause(q, "and o.obs_datetime >= ", startDate);
    	addOptionalDateClause(q, "and o.obs_datetime <= ", endDate);
    	addOptionalNumericClause(q, "and o.value_numeric >= ", minAge);
    	addOptionalNumericClause(q, "and o.value_numeric <= ", maxAge);
    	
    	
    	
    	/*q.append("and o.value_coded in (");
		for (int i=0; i<drugs.length; i++) {
			q.append(drugs[i].getConceptId());
			if ((i+1)<drugs.length) {
				q.append(",");
			}
		}	
    	q.append(") ");*/
    	
    	return executeQuery(q.toString(), context);
    }
}
