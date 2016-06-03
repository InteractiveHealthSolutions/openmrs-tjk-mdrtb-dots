package org.openmrs.module.dotsreports.regimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Represents a History of Regimen Changes for a Patient
 */
public class RegimenHistory {
	
    protected static final Log log = LogFactory.getLog(RegimenHistory.class);
    
    //***** PROPERTIES *****
    
    private Patient patient;
    private RegimenType type;
    private Map<Date, RegimenChange> regimenChanges;
    
    //***** CONSTRUCTORS *****
    
    public RegimenHistory(Patient patient) {
    	this.patient = patient;
    }
    
    public RegimenHistory(Patient patient, RegimenType type) {
    	this(patient);
    	this.type = type;
    }
    
    //***** INSTANCE METHODS *****
    
    /**
     * Adds a DrugOrder, which modifies Regimen Change events on both the start and end dates
     */
    public void addDrugOrder(DrugOrder order) {
    	
    	RegimenChange startChange = getRegimenChanges().get(order.getStartDate());
    	if (startChange == null) {
    		startChange = new RegimenChange(order.getStartDate());
    		getRegimenChanges().put(order.getStartDate(), startChange);
    	}
    	startChange.getOrdersStarted().add(order);
    	
    	Date endDate = (ObjectUtil.nvl(order.getDiscontinuedDate(), order.getAutoExpireDate()));
    	if (endDate != null) {
	    	RegimenChange endChange = getRegimenChanges().get(endDate);
	    	if (endChange == null) {
	    		endChange = new RegimenChange(endDate);
	    		getRegimenChanges().put(endDate, endChange);
	    	}
	    	endChange.getOrdersEnded().add(order);
    	}
    }
    
    /**
     * Adds an Observation, which modifies a Regimen Change to indicate the reason for starting
     */
    public void addReasonForStarting(Obs obs) {
    	RegimenChange obsDateChange = getRegimenChanges().get(obs.getObsDatetime());
    	if (obsDateChange == null) {
    		obsDateChange = new RegimenChange(obs.getObsDatetime());
    		getRegimenChanges().put(obs.getObsDatetime(), obsDateChange);
    	}
    	obsDateChange.setReasonForStarting(obs);    	
    }
    
    /**
     * @return List of all regimen change dates, ordered from earliest to latest
     */
    public List<Date> getRegimenChangeDates() {
    	return new ArrayList<Date>(getRegimenChanges().keySet());
    }
    
    /**
     * @return the initial regimen in the history
     */
    public Regimen getStartingRegimen() {
    	if (getRegimenChanges().isEmpty()) {
    		return null;
    	}
    	return getRegimenOnDate(getRegimenChangeDates().get(0));
    }
    
    /**
     * @return the Regimen active on the current date
     */
    public Regimen getActiveRegimen() {
    	return getRegimenOnDate(null);
    }
    
    /**
     * @return the List of future Drug Orders
     */
    public Set<DrugOrder> getPastDrugOrders() {
    	Set<DrugOrder> s = new HashSet<DrugOrder>();
    	Date today = new Date();
    	for (RegimenChange change : getRegimenChanges().values()) {
    		if (change.getChangeDate().compareTo(today) <= 0) {
    			s.addAll(change.getOrdersEnded());
    		}
    	}
    	return s;
    }
    
    /**
     * @return the List of future Drug Orders
     */
    public Set<DrugOrder> getFutureDrugOrders() {
    	Set<DrugOrder> s = new HashSet<DrugOrder>();
    	Date today = new Date();
    	for (RegimenChange change : getRegimenChanges().values()) {
    		if (change.getChangeDate().compareTo(today) > 0) {
    			s.addAll(change.getOrdersStarted());
    		}
    	}
    	return s;
    }
    
    /**
     * Returns the List of active DrugOrders on the passed Date
     * @param date the date on which to retrieve the passed Orders
     * @return the active DrugOrders on the passed Date
     */
    public Regimen getRegimenOnDate(Date date) {
    	return getRegimenOnDate(date, true);
    }
    
    /**
     * @return the List of active DrugOrders at the start of the passed Date (i.e. not including changes effective that day)
     */
    public Regimen getRegimenOnDate(Date date, boolean includeChangesOnDate) {
    	Regimen r = new Regimen();
    	date = (date == null ? new Date() : date);
    	int numChanges = getRegimenChangeDates().size();
    	for (int i=0; i<numChanges; i++) {
    		Date changeDate = (Date) getRegimenChangeDates().get(i);
    		boolean isLastChange = (i == numChanges-1);
    		int comparisonVal = (includeChangesOnDate ? 0 : -1);
    		if (changeDate.compareTo(date) <= comparisonVal) {
    			RegimenChange change = getRegimenChanges().get(changeDate);
    			r.setStartDate(changeDate);
    			r.setEndDate(isLastChange ? null : getRegimenChangeDates().get(i+1));
    			r.getDrugOrders().addAll(change.getOrdersStarted());
    			r.getDrugOrders().removeAll(change.getOrdersEnded());
    			r.setReasonForStarting(change.getReasonForStarting());
    		}
    	}
    	return r;
    }
    
    /**
     * Gets all the regimens in this history between two dates
     */
    public List<Regimen> getRegimensBetweenDates(Date fromDate, Date toDate, boolean inclusive) {
    	List<Regimen> regimens = new ArrayList<Regimen>();
    	
    	// strip the time elements out of the dates
    	RegimenUtils.stripTimeComponent(fromDate);
    	RegimenUtils.stripTimeComponent(toDate);
    	
    	// if the from date is not null, get the current regimen on that date
    	if (fromDate != null) {
    		Regimen regimen = getRegimenOnDate(fromDate);
    		
    		// ignore this regimen if it has no start date and no orders (i.e., if this date is before any regimen events have occurred)
    		if (regimen.getStartDate() != null && !regimen.getDrugOrders().isEmpty()) {
    			regimens.add(regimen);
    		}			
     	}
    	
    	for (Date d : getRegimenChangeDates()) {	
    		boolean beforeOk = (fromDate == null || (inclusive ? fromDate.compareTo(d) <= 0 : fromDate.compareTo(d) < 0));
    		boolean afterOk = (toDate == null || (inclusive ? toDate.compareTo(d) >= 0 : toDate.compareTo(d) > 0));
    		if (beforeOk && afterOk) {
    			Regimen regimen = getRegimenOnDate(d);
    			if (!regimens.contains(regimen)) {
    				regimens.add(getRegimenOnDate(d));
    			}
    		}
    	}
    	return regimens;
    }
    
    /**
     * @return all List of all Regimens in this History
     */
    public List<Regimen> getAllRegimens() {
    	return getRegimensBetweenDates(null, null, true);
    }
    
    /**
     * @param effectiveDate the date to check
     * @return all Regimens active before the passed Date
     */
    public List<Regimen> getRegimensBefore(Date effectiveDate) {
    	return getRegimensBetweenDates(null, effectiveDate, false);
    }
    
    /**
     * @param effectiveDate the date to check
     * @return all Regimens active after the passed Date
     */
    public List<Regimen> getRegimensDuring(Date fromDate, Date toDate) {
    	return getRegimensBetweenDates(fromDate, toDate, true);
    }
    
    /**
     * @param effectiveDate the date to check
     * @return all Regimens active after the passed Date
     */
    public List<Regimen> getRegimensAfter(Date effectiveDate) {
    	return getRegimensBetweenDates(effectiveDate, null, false);
    }
    
    /**
     * @return the first Regimen in the History whose reason for starting matches the passed type
     */
    public Regimen getFirstRegimenOfType(Concept type) {
    	for (Regimen r : getAllRegimens()) {
    		if (r.getReasonForStarting() != null && type.equals(r.getReasonForStarting().getValueCoded())) {
    			return r;
    		}
    	}
    	return null;
    }
    
    //***** PROPERTY ACCESS *****
    
	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the regimenChanges
	 */
	public Map<Date, RegimenChange> getRegimenChanges() {
		if (regimenChanges == null) {
			regimenChanges = new TreeMap<Date, RegimenChange>();
		}
		return regimenChanges;
	}

	/**
	 * @param regimenChanges the regimenChanges to set
	 */
	public void setRegimenChanges(Map<Date, RegimenChange> regimenChanges) {
		this.regimenChanges = regimenChanges;
	}

	/**
	 * @return the type
	 */
	public RegimenType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RegimenType type) {
		this.type = type;
	}
}
