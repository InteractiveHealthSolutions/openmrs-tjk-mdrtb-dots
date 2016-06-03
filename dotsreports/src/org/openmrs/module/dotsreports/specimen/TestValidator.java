package org.openmrs.module.dotsreports.specimen;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class TestValidator implements Validator {

	@SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return Test.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
	    Test test = (Test) target;
	    
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lab", "mdrtb.specimen.errors.noLab", "Please specify a laboratory.");
	    
	   // various tests on the dates
	    Date ordered = test.getDateOrdered();
	    Date received = test.getDateReceived();
	    Date started = test.getStartDate();
	    Date completed = test.getResultDate();
	    Date today = new Date();
	    
	    // make sure none of the dates are in the future
	    if (ordered != null && ordered.after(today)) {
	    	errors.rejectValue("dateOrdered", "mdrtb.specimen.errors.dateOrderedInFuture", "The date ordered cannot be in the future.");
	    }
	    if (received != null && received.after(today)) {
	    	errors.rejectValue("dateReceived", "mdrtb.specimen.errors.dateReceivedInFuture", "The date received cannot be in the future.");
	    }
	    if (started != null && started.after(today)) {
	    	errors.rejectValue("startDate", "mdrtb.specimen.errors.dateStartedInFuture", "The date started cannot be in the future.");
	    }
	    if (completed != null && completed.after(today)) {
	    	errors.rejectValue("resultDate", "mdrtb.specimen.errors.dateCompletedInFuture", "The date completed cannot be in the future.");
	    }
	    
	    // make sure the dates are in the right order
	    if (completed != null) {
	    	if (started != null && completed.before(started)) {
	    		errors.rejectValue("resultDate", "mdrtb.specimen.errors.dateCompletedBeforeDateStarted", "The date completed cannot be before the date started.");
	    	}
	    	if (received != null && completed.before(received)) {
	    		errors.rejectValue("resultDate", "mdrtb.specimen.errors.dateCompletedBeforeDateReceived", "The date completed cannot be before the date received.");
	    	}
	    	if (ordered != null && completed.before(ordered)) {
	    		errors.rejectValue("resultDate", "mdrtb.specimen.errors.dateCompletedBeforeDateOrdered", "The date completed cannot be before the date ordered.");
	    	}
	    }
	    
	    if (started != null) {
	    	if (received != null && started.before(received)) {
	    		errors.rejectValue("startDate", "mdrtb.specimen.errors.dateStartedBeforeDateReceived", "The date started cannot be before the date received.");
	    	}
	    	if (ordered != null && started.before(ordered)) {
	    		errors.rejectValue("startDate", "mdrtb.specimen.errors.dateStartedBeforeDateOrdered", "The date started cannot be before the date ordered.");
	    	}
	    }
	    
	    if (ordered != null && received != null && received.before(ordered)) {
	    	errors.rejectValue("dateReceived", "mdrtb.specimen.errors.dateReceivedBeforeDateOrdered", "The date received cannot be before the date ordered.");
	    }
	    
    }
}
