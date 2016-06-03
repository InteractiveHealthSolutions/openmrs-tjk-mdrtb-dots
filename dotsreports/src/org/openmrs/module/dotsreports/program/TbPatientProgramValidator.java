package org.openmrs.module.dotsreports.program;

import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.service.TbService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class TbPatientProgramValidator implements Validator {
	
	@SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return TbPatientProgram.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {   
    	TbPatientProgram program = (TbPatientProgram) target;
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateEnrolled", "mdrtb.program.errors.noDateEnrolled", "Please specify an enrollment date.");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "mdrtb.program.errors.noLocation", "Please specify an enrollment location.");
	    
	    // make sure, if the program is closed, that it must have an outcome
	    if (program.getDateCompleted() != null) {
	    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "outcome", "mdrtb.program.errors.noOutcome", "Please specify an outcome.");
	    }
	    
	    // make sure, if an outcome has been set, that it has a date completed
	    if (program.getOutcome() != null) {
	    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateCompleted", "mdrtb.program.errors.NoDateCompleted", "Please specify a date completed.");
	    }
	    
	    // make sure the program enrollment date is not in the future
	    if (program.getDateEnrolled() != null && program.getDateEnrolled().after(new Date())) {
	    	errors.rejectValue("dateEnrolled", "mdrtb.program.errors.dateEnrolledInFuture", "The date enrolled cannot be in the future.");
	    }
	    
	    // make sure the program completion date is not in the future
	    if (program.getDateCompleted() != null && program.getDateCompleted().after(new Date())) {
	    	errors.rejectValue("dateEnrolled", "mdrtb.program.errors.dateCompletedInFuture", "The date completed cannot be in the future.");
	    }
	    
	    // make sure that the enrollment date is before the date completed
	    if (program.getDateCompleted() != null && program.getDateEnrolled() != null && program.getDateCompleted().before(program.getDateEnrolled())) {
	    	errors.rejectValue("dateCompleted", "mdrtb.program.errors.dateCompletedBeforeDateEnrolled", "The program completion date cannot be before the enrollment date.");
	    }
	    
	    if (program.getDateEnrolled() != null) {
	    	// make sure this program doesn't overlap with any other programs
	    	// fetch all the programs between the date enrolled and date completed
	    	for (TbPatientProgram existingProgram : Context.getService(TbService.class).getTbPatientProgramsInDateRange(program.getPatient(), program.getDateEnrolled(), program.getDateCompleted())) {
	    		// the only program allowed during the current period is this new program
	    		if (!program.equals(existingProgram)) {
	    			errors.reject("mdrtb.program.errors.programOverlap", "This program overlaps with an existing MDR-TB program enrollment for this patient.");
	    			break;
	    		}	
	    	}
	    }
    }
}
