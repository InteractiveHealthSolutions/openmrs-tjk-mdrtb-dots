package org.openmrs.module.mdrtb.validator;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.validator.PatientIdentifierValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * TODO: this handles some patient validation features that are missing from Openmrs 1.6
 * TODO: once we've upgraded to a more recent version of OpenMRS that handles patient validation better, we can discard this class
 */
public class PatientValidator implements Validator {
	
	/**
	 * Returns whether or not this validator supports validating a given class.
	 * 
	 * @param c The class to check for support.
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return Patient.class.isAssignableFrom(c);
	}
	
	/**
	 * Validates the given Patient. Currently just checks for errors in identifiers. TODO: Check for
	 * errors in all Patient fields.
	 * 
	 * @param obj The patient to validate.
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		Patient patient = (Patient) obj;
		
		// make sure a family name has been specified
		if(StringUtils.isBlank(patient.getFamilyName())) {
			errors.rejectValue("personName.familyName","mdrtb.person.familyName.required");
		}
		
		// make sure a given name has been specified
		if(StringUtils.isBlank(patient.getGivenName())) {
			errors.rejectValue("personName.givenName","mdrtb.person.givenName.required");
		}
		
		// Make sure they choose a gender
		if (StringUtils.isBlank(patient.getGender())) {
			errors.rejectValue("gender", "Person.gender.required");
		}
		
		// check patients birthdate against future dates and really old dates
		if (patient.getBirthdate() != null) {
			if (patient.getBirthdate().after(new Date()))
				errors.rejectValue("birthdate", "error.date.future");
			else {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.YEAR, -120); // patient cannot be older than 120 years old 
				if (patient.getBirthdate().before(c.getTime())) {
					errors.rejectValue("birthdate", "error.date.nonsensical");
				}
			}
		}
		
		// tests required if the patient has been marked as dead
		if (patient.isDead()) {
			if (patient.getDeathDate() == null) {
				errors.rejectValue("deathDate","mdrtb.dateOfDeath.errors.required");
			}
			else if (patient.getDeathDate().after(new Date())) {
				errors.rejectValue("deathDate", "mdrtb.dateOfDeath.errors.dateInFuture");
			}
			if (patient.getCauseOfDeath() == null) {
				errors.rejectValue("causeOfDeath", "mdrtb.causeOfDeath.errors.required");
			}
		}
		
		//	 Patient Info 
		if (patient.isVoided())
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "voidReason", "mdrtb.voidReason.errors.required");
				
		// Validate PatientIdentifers
		PatientIdentifierValidator piv = new PatientIdentifierValidator();
		if (patient != null && patient.getIdentifiers() != null) {
			for (PatientIdentifier identifier : patient.getIdentifiers()) {
				piv.validate(identifier, errors);
			}
		}
	}
}
