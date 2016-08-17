package org.openmrs.module.labmodule.specimen;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.SpecimenConstants.TestStatus;


public abstract class TestImpl implements Test {
	
	protected Obs test;  // the top-level obs that holds all the data for this smear
	
	/** implementing subclasses must override this method to define their type */
	public abstract String getTestType();
	
	public Object getTest() {
		return this.test;
	}
	
	public String getId() {
		if(this.test.getId() != null ) {
			return this.test.getId().toString();
		}
		else {
			return null;
		}
	}

	public TestStatus getStatus() {
		return calculateStatus();
	}
	
	public String getSpecimenId() {
		return this.test.getEncounter().getEncounterId().toString();
	}
	
	public String getAccessionNumber() {
		return this.test.getAccessionNumber();
	}
	
	public Date getDateCollected() {
		return this.test.getObsDatetime();
	}
	
	// unfortunately we have to implement this separately for each test type since they handle comments differently
	abstract public String getComments();
	
	public Date getDateOrdered() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_ORDERED), test);
	    	
		if (obs == null) {
			return null;
	    }
	    else {
	    	return obs.getValueDatetime();
	    }
	}
	    
	public Date getDateReceived() {
	    Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_RECEIVED), test);
	    	
	    if (obs == null) {
	    	return null;
	    }
	    else {
	    	return obs.getValueDatetime();
	    }
	}

	public Location getLab() {
		return test.getLocation();
	}
	
	public Date getResultDate() {
    	Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_RESULT_DATE), test);
    	
    	if (obs == null) {
    		return null;
    	}
    	else {
    		return obs.getValueDatetime();
    	}
    }
	
	public Date getStartDate() {
		Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_START_DATE), test);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueDatetime();
		}
	}

	 public void setAccessionNumber(String accessionNumber) {
		 test.setAccessionNumber(accessionNumber);
				
		 // also propagate this location to the all the child obs
		 if(test.getGroupMembers() != null) {
			for(Obs obs : test.getGroupMembers()) {
		    	if(!obs.isVoided()) {
		    		obs.setAccessionNumber(accessionNumber);
		    	}
			}
		 }
	 }
	
	 
	 // unfortunately we have to implement this separately for each test type since they handle comments differently
	 abstract public void setComments(String comments);
	
	 public void setDateOrdered(Date dateOrdered) {
	    Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_ORDERED), test);
	    
	    // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateOrdered == null) {
			return;
		}
	    
		// if we are trying to set the obs to null, simply void the obs
		if(dateOrdered == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {	
			// now create the new Obs and add it to the encounter
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_ORDERED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}		    
		
		// update the value
		obs.setValueDatetime(dateOrdered);
	}
	    
	 public void setDateReceived(Date dateReceived) {
	    Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_RECEIVED), test);
		
	    // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateReceived == null) {
			return;
		}
	    
		// if we are trying to set the obs to null, simply void the obs
		if(dateReceived == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
	    // create a new obs if needed
		if (obs == null) {		
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TEST_DATE_RECEIVED), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueDatetime(dateReceived);
	 }

	 public void setLab(Location location) {
	    test.setLocation(location);
			
		// also propagate this location to the all the child obs
	    if(test.getGroupMembers() != null) {
	    	for(Obs obs : test.getGroupMembers()) {
	    		if(!obs.isVoided()) {
	    			obs.setLocation(location);
	    		}
	    	}
	    }
	 }

	 public void setResultDate(Date resultDate) {
	    Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_RESULT_DATE), test);
	    
	    // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resultDate == null) {
			return;
		}
	    
		// if we are trying to set the obs to null, simply void the obs
		if(resultDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {			
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TEST_RESULT_DATE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		
		// now set the value
		obs.setValueDatetime(resultDate);
	 }
	 
	 public void setStartDate(Date startDate) {
		 Obs obs = TbUtil.getObsFromObsGroup(Context.getService(TbService.class).getConcept(TbConcepts.TEST_START_DATE), test);
		 
		 // if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && startDate == null) {
			return;
		}
		 
		// if we are trying to set the obs to null, simply void the obs
		if(startDate == null) {
			obs.setVoided(true);
			obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			return;
		}
		
		// create a new obs if needed
		if (obs == null) {
			obs = new Obs (test.getPerson(), Context.getService(TbService.class).getConcept(TbConcepts.TEST_START_DATE), test.getObsDatetime(), test.getLocation());
			obs.setEncounter(test.getEncounter());
			test.addGroupMember(obs);
		}
		 
		 // now set the value
		 obs.setValueDatetime(startDate);
	 }

	 /**
	  * Comparable interface method and utility methods
	  */
	 
	 public int compareTo(Test test1) {
		 Date recent0 = oldestDate(this);
		 Date recent1 = oldestDate(test1);
		 
		 if (recent0 == null) {
			 // use the actual obs datetime if there are no value dates
			 recent0 = this.getObs().getObsDatetime();
		 }
		 if (recent1 == null) {
			 recent1 = this.getObs().getObsDatetime();
		 }

		return recent0.compareTo(recent1);  
	 }
	 
	 // checks all the dates properties associated with the mdrtb test and returns the date that is most recent
	 private Date oldestDate(Test test) {
		 Date oldest = null;
		 
		 if (test.getDateOrdered() != null) {
			 if (oldest == null || test.getDateOrdered().before(oldest)) {
				 oldest = test.getDateOrdered();
			 }
		 }
		 if (test.getDateReceived() != null) {
			 if (oldest == null || test.getDateReceived().before(oldest)) {
				 oldest = test.getDateReceived();
			 }
		 }
		 if (test.getResultDate() != null) {
			 if (oldest == null || test.getResultDate().before(oldest)) {
				 oldest = test.getResultDate();
			 }
		 }
		 if (test.getStartDate() != null) {
			 if (oldest == null || test.getStartDate().before(oldest)) {
				 oldest = test.getStartDate();
			 }
		 }
		 
		 return oldest;
	 }
	 
	 /**
	  * Protected methods used for interacting with the matching MdrSpecimenImpl
	  */

	 protected Obs getObs() {
		 return test;
	 }
	    
	 /**
	  * Utility methods 
	  */
			    
	 /**
	  * Determines the current status of this test by examines the
	  * values of the various date fields
	  * Auto generated method comment
	  * 
	  * @return
	  */
	 private TestStatus calculateStatus() {
	    	
	    if (getResultDate() != null) {
	    	return TestStatus.COMPLETED;
	    }
	    else if (getStartDate() != null) {
	    	return TestStatus.STARTED;
	    }
	    else if (getDateReceived() != null) {
	    	return TestStatus.RECEIVED;
	    }
	    else if (getDateOrdered() != null) {
	    	return TestStatus.ORDERED;
	    }
	    else {
	    	return TestStatus.UNKNOWN;
	    }
	 }
}
