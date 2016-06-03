package org.openmrs.module.dotsreports.program;

import java.util.Date;

import org.openmrs.PatientState;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class TbPatientProgramHospitalizationValidator implements Validator {
	@SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
		return TbPatientProgram.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {   
    	TbPatientProgram program = (TbPatientProgram) target;
    	
    	// to make sure we don't throw more than one overlap error message
    	Boolean overlap = false;
    
    	Date previousHospitalizationStartDate = null;
    	
    	for (PatientState hospitalization : program.getAllHospitalizations()){
    		if (hospitalization.getStartDate() == null) {
    			errors.reject("mdrtb.program.errors.noHospitalizationStartDate", "Please specify an admission date.");
    		}
    		
    		if (hospitalization.getStartDate() != null && hospitalization.getStartDate().after(new Date())) {
    			errors.reject("mdrtb.program.errors.admissionDateInFuture","The admission date cannot be in the future.");
    		}
    		
    		if (hospitalization.getEndDate() != null && hospitalization.getEndDate().after(new Date())) {
    			errors.reject("mdrtb.program.errors.dischargeDateInFuture","The discharge date cannot be in the future.");
    		}
    		
    		if (hospitalization.getEndDate() != null && hospitalization.getStartDate().after(hospitalization.getEndDate())) {
    			errors.reject("mdrtb.program.errors.dischargeDateBeforeAdmissionDate", "The discharge date cannot be before the admission date.");
    		}
    		
    		if (previousHospitalizationStartDate == null) {
    			previousHospitalizationStartDate = hospitalization.getStartDate();
    		}
    		else if(!overlap && (hospitalization.getEndDate() == null || hospitalization.getEndDate().after(previousHospitalizationStartDate))){
    			overlap = true;
    			errors.reject("mdrtb.program.errors.hospitalizationOverlap", "Hospitalization dates cannot overlap.");
    		}
    	} 	
    }
}
