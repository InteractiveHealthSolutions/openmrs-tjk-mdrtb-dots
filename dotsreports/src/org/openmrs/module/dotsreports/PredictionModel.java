package org.openmrs.module.dotsreports;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class PredictionModel {
	
	protected static final Log log = LogFactory.getLog(PredictionModel.class);
	
	public static final double intercept = -4.56350;
	
	public enum RiskFactor {

		Male("Male", 0.06011),
		HivPositive("HIV Positive", -0.27236),
		PreviousTreatment("Previous Treatment", 1.39232),
		SmearPositive("Smear Positive", 0.54125),
		KnownHouseholdContact("Known TB Contact", 0.23557);
		
		private double coefficient;
		private String name;
		RiskFactor(String name, double coefficient) { 
			this.name = name;
			this.coefficient = coefficient; 
		}
		public String getName() { return name; }
		public double getCoefficient() { return coefficient; }
	};
    
	public static Map<RiskFactor, Boolean> getRiskFactors(Patient p) {
		
		Map<RiskFactor, Boolean> m = new HashMap<RiskFactor, Boolean>();
		
		m.put(RiskFactor.Male, "M".equals(p.getGender()));
		m.put(RiskFactor.HivPositive, hasAnyObsValue(p, 3753, 703)); // HIV Status = Positive
		m.put(RiskFactor.PreviousTreatment, hasAnyObsValue(p, 6371)); // Previous TB Treatment Regimen has been answered
		m.put(RiskFactor.SmearPositive, hasAnyObsValue(p, 3052, 1408, 1409, 1410, 3047, 703)); // Smear has one of a number of possible positives
		m.put(RiskFactor.KnownHouseholdContact, hasBooleanObsValue(p, 2133, true)); // Contact avec une personne tuberculeuse
		
		return m;
	}
	
	public static double calculateRiskProbability(Patient p, int numDecimalPlaces) {
		return calculateRiskProbability(getRiskFactors(p), numDecimalPlaces);
	}
	
	public static double calculateRiskProbability(Map<RiskFactor, Boolean> riskFactors, int numDecimalPlaces) {
		double z = intercept;
		for (Map.Entry<RiskFactor, Boolean> e : riskFactors.entrySet()) {
			log.debug("Risk factor: " + e.getKey() + " = " + e.getValue());
			if (e.getValue()) {
				z += e.getKey().getCoefficient();
			}
		}
		double v = 100 / (1 + Math.exp(-1 * z));
		log.debug("Result = " + v);
		BigDecimal bd = new BigDecimal(v);
	    bd = bd.setScale(numDecimalPlaces, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	/**
	 * If valueConceptIds exist, check against them.  Otherwise, just check that the question has any obs values
	 */
	public static boolean hasAnyObsValue(Patient p, Integer conceptId, Integer...valueConceptIds) {
    	StringBuilder q = new StringBuilder();
    	q.append("select 	count(*) ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	o.person_id = " + p.getPersonId() + " ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + conceptId + " ");
    	if (valueConceptIds != null && valueConceptIds.length > 0) {
	    	q.append("and o.value_coded in (");
			for (int i=0; i<valueConceptIds.length; i++) {
				q.append(valueConceptIds[i]);
				if ((i+1)<valueConceptIds.length) {
					q.append(",");
				}
			}	
	    	q.append(") ");
    	}
    	return !"0".equals(Context.getAdministrationService().executeSQL(q.toString(), true).get(0).get(0).toString());
	}

	/**
	 * If valueConceptIds exist, check against them.  Otherwise, just check that the question has any obs values
	 */
	public static boolean hasBooleanObsValue(Patient p, Integer conceptId, boolean valueBoolean) {
    	StringBuilder q = new StringBuilder();
    	q.append("select 	count(*) ");
    	q.append("from 		patient p, obs o ");
    	q.append("where 	p.patient_id = o.person_id ");
    	q.append("and	 	o.person_id = " + p.getPersonId() + " ");
    	q.append("and	 	p.voided = 0 and o.voided = 0 ");
    	q.append("and		o.concept_id = " + conceptId + " ");
    	if (valueBoolean) {
    		q.append("and		(o.value_numeric = 1 or o.value_coded in (1065,2257))");
    	}
    	else {
    		q.append("and		(o.value_numeric = 0 or o.value_coded in (1066,2258))");
    	}
    	return !"0".equals(Context.getAdministrationService().executeSQL(q.toString(), true).get(0).get(0).toString());
	}
}
