/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.validator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link ObsValidator} class.
 */
public class ObsValidatorTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if personId is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfPersonIdIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setConcept(Context.getConceptService().getConcept(5089));
		obs.setObsDatetime(new Date());
		obs.setValueNumeric(1.0);
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertFalse(errors.hasFieldErrors("valueNumeric"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if obsDatetime is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfObsDatetimeIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(5089));
		obs.setValueNumeric(1.0);
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertTrue(errors.hasFieldErrors("obsDatetime"));
		Assert.assertFalse(errors.hasFieldErrors("valueNumeric"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setObsDatetime(new Date());
		obs.setValueNumeric(1.0);
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertTrue(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertFalse(errors.hasFieldErrors("valueNumeric"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept datatype is boolean and valueBoolean is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptDatatypeIsBooleanAndValueBooleanIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(18));
		obs.setObsDatetime(new Date());
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertTrue(errors.hasFieldErrors("valueNumeric")); // Booleans translates to numeric at the moment
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept datatype is coded and valueCoded is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptDatatypeIsCodedAndValueCodedIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(4));
		obs.setObsDatetime(new Date());
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertTrue(errors.hasFieldErrors("valueCoded"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept datatype is date and valueDatetime is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptDatatypeIsDateAndValueDatetimeIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(20));
		obs.setObsDatetime(new Date());
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertTrue(errors.hasFieldErrors("valueDatetime"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept datatype is numeric and valueNumeric is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptDatatypeIsNumericAndValueNumericIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(5089));
		obs.setObsDatetime(new Date());
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertTrue(errors.hasFieldErrors("valueNumeric"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept datatype is text and valueText is null", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfConceptDatatypeIsTextAndValueTextIsNull() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(19));
		obs.setObsDatetime(new Date());
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertTrue(errors.hasFieldErrors("valueText"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if obs ancestors contains obs", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldFailValidationIfObsAncestorsContainsObs() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(5089));
		obs.setObsDatetime(new Date());
		obs.setValueNumeric(1.0);
		
		Set<Obs> group = new HashSet<Obs>();
		group.add(obs);
		obs.setGroupMembers(group);
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("person"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("obsDatetime"));
		Assert.assertFalse(errors.hasFieldErrors("valueNumeric"));
		Assert.assertTrue(errors.hasFieldErrors("groupMembers"));
	}
	
	/**
	 * @see {@link ObsValidator#validate(java.lang.Object, org.springframework.validation.Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all values present", method = "validate(java.lang.Object, org.springframework.validation.Errors)")
	public void validate_shouldPassValidationIfAllValuesPresent() throws Exception {
		Obs obs = new Obs();
		obs.setPerson(Context.getPersonService().getPerson(2));
		obs.setConcept(Context.getConceptService().getConcept(5089));
		obs.setObsDatetime(new Date());
		obs.setValueNumeric(1.0);
		
		Errors errors = new BindException(obs, "obs");
		new ObsValidator().validate(obs, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
}
