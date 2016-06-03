package org.openmrs.module.dotsreports.regimen;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Represents a series of drugs given at the same time, generally for the same reason.
 * The startDate and endDate of the Regimen represent the start dates of the whole Regimen:
 * its individual components may have different startDate and endDate values.
 */
public class Regimen {

	//***** PROPERTIES *****
	
    private Date startDate;
    private Date endDate;
    private Obs reasonForStarting;
    private Set<DrugOrder> drugOrders;
    
    //***** CONSTRUCTORS *****
    
    public Regimen() {}
    
    //***** INSTANCE METHODS *****
    
    /**
     * @return whether the Regimen is currently active
     */
    public boolean isActive() {
        return isActive(null);
    }
    
    /**
     * @return whether the Regimen is in the future
     */
    public boolean isFuture() {
    	return startDate != null && startDate.compareTo(new Date()) > 0;
    }

    /**
     * @return whether the Regimen is active on the passed date
     */
    public boolean isActive(Date date) {
    	if (date == null) {
    		date = new Date();
    	}
    	boolean started = startDate != null && startDate.compareTo(date) <= 0;
    	boolean notEnded = endDate == null || endDate.compareTo(date) > 0;
    	return started && notEnded;
    }
    
    /**
     * @param fromDate the lower date bound to check, inclusive
     * @param toDate the upper date bound to check, inclusive
     * @param entirePeriod if true, checks that the Regimen is active for the entire period.  otherwise, checks that it is active ever during the period.
     * @return true if the regimen is active during the specified date range
     */
    public boolean isActive(Date fromDate, Date toDate, boolean entirePeriod) {
    	if (startDate == null) {
    		return false;
    	}
    	if (entirePeriod) {
    		boolean startedOnOrBefore = (startDate.compareTo(fromDate) <= 0);
    		boolean endedAfter = (endDate == null || endDate.compareTo(toDate) > 0);
    		return startedOnOrBefore && endedAfter;
    	}
    	boolean startedOnOrBeforeEnd = (startDate.compareTo(endDate) <= 0);
    	boolean endedOnOrAfterStart = (toDate == null || toDate.compareTo(startDate) >= 0);
    	return startedOnOrBeforeEnd && endedOnOrAfterStart;
    }
    
    /**
     * Returns whether the current regimen is empty (empty defined as having no drug orders)
     */
    public boolean isEmpty() {
    	return (getDrugOrders() == null || getDrugOrders().size() == 0);
    }
    
    
    /**
     * @return the duration of the Regimen in days.  If the start date is null or after the end date, returns -1
     */
    public int getDurationInDays() {
    	Date fromDate = startDate;
    	Date toDate = (endDate == null ? new Date() : endDate);
    	if (fromDate == null || fromDate.after(toDate)) {
    		return -1;
    	}
    	double days = (toDate.getTime() - fromDate.getTime()) / 1000 / 60 / 60 / 24;
    	return (int)days;
    }
    
    /**
     * @return the unique set of reasons why this Regimen was discontinued
     */
    public Set<Concept> getEndReasons() {
    	Set<Concept> c = new HashSet<Concept>();
    	if (getEndDate() != null) {
	    	for (DrugOrder o : getDrugOrders()) {
	    		if (getEndDate().equals(o.getDiscontinuedDate()) && o.getDiscontinuedReason() != null) {
	    			c.add(o.getDiscontinuedReason());
	    		}
	    	}
    	}
    	return c;
    }

    /**
     * @return the unique set of generic drugs within this Order
     */
    public Set<Concept> getUniqueGenerics() {
        Set<Concept> ret = new HashSet<Concept>();
        for (DrugOrder o : getDrugOrders()) {
            ret.add(o.getConcept());
        }
        return ret;
    }
    
    /**
     * @param generic the concept to match
     * @return the matching DrugOrder for the passed Drug Concept, or null if none found
     */
    public DrugOrder getMatchingDrugOrder(Concept generic) {
    	for (DrugOrder o : getDrugOrders()) {
    		if (o.getConcept() != null && o.getConcept().equals(generic)) {
    			return o;
    		}
    	}
    	return null;
    }
    
    /**
     * @param drug the drug to match
     * @return the matching DrugOrder for the passed Drug, or null if none found
     */
    public DrugOrder getMatchingDrugOrder(Drug drug) {
    	for (DrugOrder o : getDrugOrders()) {
    		if (o.getDrug() != null && o.getDrug().equals(drug)) {
    			return o;
    		}
    	}
    	return null;
    }
    
    /**
     * @param drug the Drug to check
     * @return whether this DrugRegimen contains this Drug
     */
    public boolean containsDrug(Drug drug) {
    	return getMatchingDrugOrder(drug) != null;
    }
    
    /**
     * @param concept the Concept to check
     * @return whether this DrugRegimen contains any DrugOrders with this Concept
     */
    public boolean containsGeneric(Concept concept) {
    	return getUniqueGenerics().contains(concept);
    }
    
	/**
     * @see Object#toString()
     */
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (DrugOrder o : getDrugOrders()) {
        	ret.append((ret.length() == 0 ? "" : " + ") + o.toString());
        }
        ret.append(" from " + startDate + " to " + endDate + " ");
        return ret.toString();
    }
    
    /**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Regimen) {
			Regimen that = (Regimen)obj;
			if (ObjectUtil.areEqual(this.getStartDate(), that.getStartDate())) {
				if (ObjectUtil.areEqual(this.getEndDate(), that.getEndDate())) {
					if (ObjectUtil.areEqual(this.getReasonForStarting(), that.getReasonForStarting())) {
						if (this.getDrugOrders().size() == that.getDrugOrders().size()) {
							for (DrugOrder order : this.getDrugOrders()) {
								if (!that.getDrugOrders().contains(order)) {
									return false;
								}
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		if (startDate != null) { hash += 31 * startDate.hashCode(); }
		if (endDate != null) { hash += 31 * endDate.hashCode(); }
		if (reasonForStarting != null) { hash += 31 * reasonForStarting.getObsId().hashCode(); }
		Set<Integer> orderIds = new TreeSet<Integer>();
		for (DrugOrder order : getDrugOrders()) {
			orderIds.add(order.getOrderId());
		}
		for (Integer orderId : orderIds) {
			hash += 31 * orderId.hashCode();
		}
		return hash;
	} 
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * @return the reasonForStarting
	 */
	public Obs getReasonForStarting() {
		return reasonForStarting;
	}

	/**
	 * @param reasonForStarting the reasosForStarting to set
	 */
	public void setReasonForStarting(Obs reasonForStarting) {
		this.reasonForStarting = reasonForStarting;
	}

	/**
	 * @return the drugOrders
	 */
	public Set<DrugOrder> getDrugOrders() {
		if (drugOrders == null) {
			drugOrders = new HashSet<DrugOrder>();
		}
		return drugOrders;
	}

	/**
	 * @param drugOrders the drugOrders to set
	 */
	public void setDrugOrders(Set<DrugOrder> drugOrders) {
		this.drugOrders = drugOrders;
	}

	/**
	 * @param drugOrder the DrugOrder to add
	 */
	public void addDrugOrder(DrugOrder drugOrder) {
		getDrugOrders().add(drugOrder);
	}
}