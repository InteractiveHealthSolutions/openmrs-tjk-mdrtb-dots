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
package org.openmrs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.InvalidCharactersPasswordException;
import org.openmrs.api.ShortPasswordException;
import org.openmrs.api.WeakPasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.TestUtil;
import org.openmrs.test.Verifies;

/**
 * Tests the methods in {@link OpenmrsUtil} TODO: finish adding tests for all methods
 */
public class OpenmrsUtilTest extends BaseContextSensitiveTest {
	
	private static GlobalProperty luhnGP = new GlobalProperty(
	        OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_PATIENT_IDENTIFIER_VALIDATOR,
	        OpenmrsConstants.LUHN_IDENTIFIER_VALIDATOR);
	
	/**
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		authenticate();
		
		Context.getAdministrationService().saveGlobalProperty(luhnGP);
	}
	
	/**
	 * Test the check digit method
	 * 
	 * @see {@link OpenmrsUtil#getCheckDigit(String)}
	 */
	@Test
	@Verifies(value = "should get valid check digits", method = "getCheckDigit(String)")
	public void getCheckDigit_shouldGetValidCheckDigits() throws Exception {
		
		String[] ids = { "9", "99", "999", "123MT", "asdf" };
		int[] cds = { 1, 2, 3, 2, 8 };
		
		for (int i = 0; i < ids.length; i++) {
			assertEquals(OpenmrsUtil.getCheckDigit(ids[i]), cds[i]);
		}
		
	}
	
	/**
	 * Test check digit validation methods
	 * 
	 * @see {@link OpenmrsUtil#isValidCheckDigit(String)}
	 */
	@Test
	@Verifies(value = "should validate correct check digits", method = "isValidCheckDigit(String)")
	public void isValidCheckDigit_shouldValidateCorrectCheckDigits() throws Exception {
		
		String[] ids2 = { "9-1", "99-2", "999-3", "123MT-2", "asdf-8", "12abd-7" };
		String[] ids2Char = { "9-b", "99-c", "999-d", "123MT-c", "asdf-i", "12abd-h" };
		for (int i = 0; i < ids2.length; i++) {
			assertTrue(OpenmrsUtil.isValidCheckDigit(ids2[i]));
			assertTrue(OpenmrsUtil.isValidCheckDigit(ids2Char[i]));
		}
	}
	
	/**
	 * @see {@link OpenmrsUtil#isValidCheckDigit(String)}
	 */
	@Test
	@Verifies(value = "should not validate invalid check digits", method = "isValidCheckDigit(String)")
	public void isValidCheckDigit_shouldNotValidateInvalidCheckDigits() throws Exception {
		String[] ids3 = { "asdf-7", "9-2", "9-4" };
		for (int i = 0; i < ids3.length; i++) {
			assertFalse(OpenmrsUtil.isValidCheckDigit(ids3[i]));
		}
	}
	
	/**
	 * @see {@link OpenmrsUtil#isValidCheckDigit(String)}
	 */
	@Test
	@Verifies(value = "should throw error if given an invalid character in id", method = "isValidCheckDigit(String)")
	public void isValidCheckDigit_shouldThrowErrorIfGivenAnInvalidCharacterInId() throws Exception {
		String[] ids4 = { "#@!", "234-3-3", "-3", "2134" };
		for (int i = 0; i < ids4.length; i++) {
			try {
				OpenmrsUtil.isValidCheckDigit(ids4[i]);
				fail("An exception was not thrown for invalid identifier: " + ids4[i]);
			}
			catch (Exception e) {}
		}
	}
	
	/**
	 * test the collection contains method
	 * 
	 * @see {@link OpenmrsUtil#collectionContains(Collection<*>,Object)}
	 */
	@Test
	@Verifies(value = "should use equals method for comparison instead of compareTo given List collection", method = "collectionContains(Collection<*>,Object)")
	public void collectionContains_shouldUseEqualsMethodForComparisonInsteadOfCompareToGivenListCollection()
	                                                                                                        throws Exception {
		
		ArrayList<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>();
		
		PatientIdentifier pi = new PatientIdentifier();
		pi.setIdentifier("123");
		pi.setIdentifierType(new PatientIdentifierType(1));
		pi.setDateCreated(new Date());
		pi.setCreator(new User(1));
		
		identifiers.add(pi);
		
		// sanity check
		identifiers.add(pi);
		assertFalse("Lists should accept more than one object", identifiers.size() == 1);
		
		pi.setDateCreated(null);
		pi.setCreator(null);
		
		assertTrue("Just because the date is null, doesn't make it not in the list anymore", OpenmrsUtil.collectionContains(
		    identifiers, pi));
	}
	
	/**
	 * test the collection contains method
	 * 
	 * @see {@link OpenmrsUtil#collectionContains(Collection<*>,Object)}
	 */
	@Test
	@Verifies(value = "should use equals method for comparison instead of compareTo given SortedSet collection", method = "collectionContains(Collection<*>,Object)")
	public void collectionContains_shouldUseEqualsMethodForComparisonInsteadOfCompareToGivenSortedSetCollection()
	                                                                                                             throws Exception {
		
		SortedSet<PatientIdentifier> identifiers = new TreeSet<PatientIdentifier>();
		
		PatientIdentifier pi = new PatientIdentifier();
		pi.setIdentifier("123");
		pi.setIdentifierType(new PatientIdentifierType(1));
		pi.setDateCreated(new Date());
		pi.setCreator(new User(1));
		
		identifiers.add(pi);
		
		// sanity check
		identifiers.add(pi);
		assertTrue("There should still be only 1 identifier in the patient object now", identifiers.size() == 1);
		
		pi.setDateCreated(null);
		pi.setCreator(null);
		
		assertTrue("Just because the date is null, doesn't make it not in the list anymore", OpenmrsUtil.collectionContains(
		    identifiers, pi));
	}
	
	/**
	 * When given a null parameter, the {@link OpenmrsUtil#url2file(java.net.URL)} method should
	 * quietly fail by returning null
	 * 
	 * @see {@link OpenmrsUtil#url2file(URL)}
	 */
	@Test
	@Verifies(value = "should return null given null parameter", method = "url2file(URL)")
	public void url2file_shouldReturnNullGivenNullParameter() throws Exception {
		assertNull(OpenmrsUtil.url2file(null));
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail with digit only password by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithDigitOnlyPasswordByDefault() throws Exception {
		OpenmrsUtil.validatePassword("admin", "12345678", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail with digit only password if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithDigitOnlyPasswordIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT, "true");
		OpenmrsUtil.validatePassword("admin", "12345678", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with digit only password if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithDigitOnlyPasswordIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_NON_DIGIT, "false");
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "false");
		OpenmrsUtil.validatePassword("admin", "12345678", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail with char only password by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithCharOnlyPasswordByDefault() throws Exception {
		OpenmrsUtil.validatePassword("admin", "testonly", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail with char only password if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithCharOnlyPasswordIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT, "true");
		OpenmrsUtil.validatePassword("admin", "testonly", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with char only password if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithCharOnlyPasswordIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_DIGIT, "false");
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "false");
		OpenmrsUtil.validatePassword("admin", "testonly", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail without upper and lower case password by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithoutUpperAndLowerCasePasswordByDefault() throws Exception {
		OpenmrsUtil.validatePassword("admin", "test0nl1", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail without upper and lower case password if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithoutUpperAndLowerCasePasswordIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "true");
		OpenmrsUtil.validatePassword("admin", "test0nl1", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass without upper and lower case password if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithoutUpperAndLowerCasePasswordIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_REQUIRES_UPPER_AND_LOWER_CASE, "false");
		OpenmrsUtil.validatePassword("admin", "test0nl1", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = WeakPasswordException.class)
	@Verifies(value = "should fail with password equals to user name by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithPasswordEqualsToUserNameByDefault() throws Exception {
		OpenmrsUtil.validatePassword("Admin1234", "Admin1234", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = WeakPasswordException.class)
	@Verifies(value = "should fail with password equals to user name if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithPasswordEqualsToUserNameIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID, "true");
		OpenmrsUtil.validatePassword("Admin1234", "Admin1234", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with password equals to user name if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithPasswordEqualsToUserNameIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID, "false");
		OpenmrsUtil.validatePassword("Admin1234", "Admin1234", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = WeakPasswordException.class)
	@Verifies(value = "should fail with password equals to system id by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithPasswordEqualsToSystemIdByDefault() throws Exception {
		OpenmrsUtil.validatePassword("admin", "Admin1234", "Admin1234");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = WeakPasswordException.class)
	@Verifies(value = "should fail with password equals to system id if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithPasswordEqualsToSystemIdIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID, "true");
		OpenmrsUtil.validatePassword("admin", "Admin1234", "Admin1234");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with password equals to system id if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithPasswordEqualsToSystemIdIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CANNOT_MATCH_USERNAME_OR_SYSTEMID, "false");
		OpenmrsUtil.validatePassword("admin", "Admin1234", "Admin1234");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = ShortPasswordException.class)
	@Verifies(value = "should fail with short password by default", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithShortPasswordByDefault() throws Exception {
		OpenmrsUtil.validatePassword("admin", "1234567", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = ShortPasswordException.class)
	@Verifies(value = "should fail with short password if not allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithShortPasswordIfNotAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH, "6");
		OpenmrsUtil.validatePassword("admin", "12345", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with short password if allowed", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithShortPasswordIfAllowed() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_MINIMUM_LENGTH, "0");
		OpenmrsUtil.validatePassword("admin", "H4t", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test(expected = InvalidCharactersPasswordException.class)
	@Verifies(value = "should fail with password not matching configured regex", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldFailWithPasswordNotMatchingConfiguredRegex() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CUSTOM_REGEX,
		    "[A-Z][a-z][0-9][0-9][a-z][A-Z][a-z][a-z][a-z][a-z]");
		OpenmrsUtil.validatePassword("admin", "he11oWorld", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should pass with password matching configured regex", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldPassWithPasswordMatchingConfiguredRegex() throws Exception {
		TestUtil.saveGlobalProperty(OpenmrsConstants.GP_PASSWORD_CUSTOM_REGEX,
		    "[A-Z][a-z][0-9][0-9][a-z][A-Z][a-z][a-z][a-z][a-z]");
		OpenmrsUtil.validatePassword("admin", "He11oWorld", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should allow password to contain non alphanumeric characters", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldAllowPasswordToContainNonAlphanumericCharacters() throws Exception {
		OpenmrsUtil.validatePassword("admin", "Test1234?", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should allow password to contain white spaces", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldAllowPasswordToContainWhiteSpaces() throws Exception {
		OpenmrsUtil.validatePassword("admin", "Test *&^ 1234? ", "1-8");
	}
	
	/**
	 * @see {@link OpenmrsUtil#getDateFormat(Locale)}
	 */
	@Test
	@Verifies(value = "should return a pattern with four y characters in it", method = "getDateFormat(Locale)")
	public void getDateFormat_shouldReturnAPatternWithFourYCharactersInIt() throws Exception {
		Assert.assertEquals("MM/dd/yyyy", OpenmrsUtil.getDateFormat(Locale.US).toLocalizedPattern());
		Assert.assertEquals("dd/MM/yyyy", OpenmrsUtil.getDateFormat(Locale.UK).toLocalizedPattern());
		Assert.assertEquals("tt.MM.uuuu", OpenmrsUtil.getDateFormat(Locale.GERMAN).toLocalizedPattern());
		Assert.assertEquals("dd-MM-yyyy", OpenmrsUtil.getDateFormat(new Locale("pt", "pt")).toLocalizedPattern());
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsUpperAndLowerCase(String)}
	 */
	@Test
	@Verifies(value = "should return true if string contains upper and lower case", method = "containsUpperAndLowerCase(String)")
	public void containsUpperAndLowerCase_shouldReturnTrueIfStringContainsUpperAndLowerCase() throws Exception {
		Assert.assertTrue(OpenmrsUtil.containsUpperAndLowerCase("Hello"));
		Assert.assertTrue(OpenmrsUtil.containsUpperAndLowerCase("methodName"));
		Assert.assertTrue(OpenmrsUtil.containsUpperAndLowerCase("the letter K"));
		Assert.assertTrue(OpenmrsUtil.containsUpperAndLowerCase("The number 10"));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsUpperAndLowerCase(String)}
	 */
	@Test
	@Verifies(value = "should return false if string does not contain lower case characters", method = "containsUpperAndLowerCase(String)")
	public void containsUpperAndLowerCase_shouldReturnFalseIfStringDoesNotContainLowerCaseCharacters() throws Exception {
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase("HELLO"));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase("THE NUMBER 10?"));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase(""));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase(null));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsUpperAndLowerCase(String)}
	 */
	@Test
	@Verifies(value = "should return false if string does not contain upper case characters", method = "containsUpperAndLowerCase(String)")
	public void containsUpperAndLowerCase_shouldReturnFalseIfStringDoesNotContainUpperCaseCharacters() throws Exception {
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase("hello"));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase("the number 10?"));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase(""));
		Assert.assertFalse(OpenmrsUtil.containsUpperAndLowerCase(null));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsOnlyDigits(String)}
	 */
	@Test
	@Verifies(value = "should return true if string contains only digits", method = "containsOnlyDigits(String)")
	public void containsOnlyDigits_shouldReturnTrueIfStringContainsOnlyDigits() throws Exception {
		Assert.assertTrue(OpenmrsUtil.containsOnlyDigits("1234567890"));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsOnlyDigits(String)}
	 */
	@Test
	@Verifies(value = "should return false if string contains any non-digits", method = "containsOnlyDigits(String)")
	public void containsOnlyDigits_shouldReturnFalseIfStringContainsAnyNonDigits() throws Exception {
		Assert.assertFalse(OpenmrsUtil.containsOnlyDigits("1.23"));
		Assert.assertFalse(OpenmrsUtil.containsOnlyDigits("123A"));
		Assert.assertFalse(OpenmrsUtil.containsOnlyDigits("12 3"));
		Assert.assertFalse(OpenmrsUtil.containsOnlyDigits(""));
		Assert.assertFalse(OpenmrsUtil.containsOnlyDigits(null));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsDigit(String)}
	 */
	@Test
	@Verifies(value = "should return true if string contains any digits", method = "containsDigit(String)")
	public void containsDigit_shouldReturnTrueIfStringContainsAnyDigits() throws Exception {
		Assert.assertTrue(OpenmrsUtil.containsDigit("There is 1 digit here."));
	}
	
	/**
	 * @see {@link OpenmrsUtil#containsDigit(String)}
	 */
	@Test
	@Verifies(value = "should return false if string contains no digits", method = "containsDigit(String)")
	public void containsDigit_shouldReturnFalseIfStringContainsNoDigits() throws Exception {
		Assert.assertFalse(OpenmrsUtil.containsDigit("ABC .$!@#$%^&*()-+=/?><.,~`|[]"));
		Assert.assertFalse(OpenmrsUtil.containsDigit(""));
		Assert.assertFalse(OpenmrsUtil.containsDigit(null));
	}
	
	/**
	 * The validate password method should be in a separate jvm here so that the Context and
	 * services are not available to the validatePassword (similar to how its used in the
	 * initialization wizard), but that is not possible to set up on a test-by-test basis, so we
	 * settle by making the user context not available.
	 * 
	 * @see {@link OpenmrsUtil#validatePassword(String,String,String)}
	 */
	@Test
	@Verifies(value = "should still work without an open session", method = "validatePassword(String,String,String)")
	public void validatePassword_shouldStillWorkWithoutAnOpenSession() throws Exception {
		Context.closeSession();
		OpenmrsUtil.validatePassword("admin", "1234Password", "systemId");
	}
	
	@Test
	@Verifies(value = "openmrsDateFormat should parse valid date", method = "getDateFormat(Locale)")
	public void openmrsDateFormat_shouldParseValidDate() throws Exception {
		SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en", "GB"));
		sdf.parse("20/12/2001");
		
		sdf = OpenmrsUtil.getDateFormat(new Locale("en", "US"));
		sdf.parse("12/20/2001");
	}
	
	@Test
	@Verifies(value = "openmrsDateFormat should not allow dates with invalid days or months", method = "getDateFormat(Locale)")
	public void openmrsDateFormat_shouldNotAllowDatesWithInvalidDaysOrMonths() throws Exception {
		
		try {
			SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en", "GB"));
			sdf.parse("1/13/2001");
			Assert.fail("Date with invalid month should throw exception.");
		}
		catch (ParseException e) {}
		
		try {
			SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en", "GB"));
			sdf.parse("32/1/2001");
			Assert.fail("Date with invalid day should throw exception.");
		}
		catch (ParseException e) {}
		
		try {
			SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en", "US"));
			sdf.parse("13/1/2001");
			Assert.fail("Date with invalid month should throw exception.");
		}
		catch (ParseException e) {}
		
		try {
			SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en", "US"));
			sdf.parse("1/32/2001");
			Assert.fail("Date with invalid day should throw exception.");
		}
		catch (ParseException e) {}
	}
	
	@Test
	@Verifies(value = "openmrsDateFormat should allow single digit dates and months", method = "getDateFormat(Locale)")
	public void openmrsDateFormat_shouldAllowSingleDigitDatesAndMonths() throws Exception {
		
		SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en"));
		sdf.parse("1/1/2001");
		
	}
	
	@Test
	@Verifies(value = "openmrsDateFormat should not allow two-digit years", method = "getDateFormat(Locale)")
	public void openmrsDateFormat_shouldNotAllowTwoDigitYears() throws Exception {
		
		try {
			SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(new Locale("en"));
			sdf.parse("01/01/01");
			Assert.fail("Date with two-digit year should throw exception.");
		}
		catch (ParseException e) {}
		
	}
	
	/**
	 * @see {@link OpenmrsUtil#getDateFormat(Locale)}
	 */
	@Test
	@Verifies(value = "should not allow the returned SimpleDateFormat to be modified", method = "getDateFormat(Locale)")
	public void getDateFormat_shouldNotAllowTheReturnedSimpleDateFormatToBeModified()
			throws Exception {
		// start with a locale that is not currently cached by getDateFormat() 
		Locale locale = new Locale("hk");
		Assert.assertTrue("default locale is potentially already cached", 
			!Context.getLocale().equals(locale));
		
		// get the initially built dateformat from getDateFormat()
		SimpleDateFormat sdf = OpenmrsUtil.getDateFormat(locale);
		Assert.assertNotSame("initial dateFormatCache entry is modifiable", 
			OpenmrsUtil.getDateFormat(locale), sdf);
		
		// verify changing the pattern on our variable does not affect the cache
		sdf.applyPattern("yyyymmdd");
		Assert.assertTrue("initial dateFormatCache pattern is modifiable", 
			!OpenmrsUtil.getDateFormat(locale).toPattern().equals(sdf.toPattern()));

		// the dateformat cache now contains the format for this locale; checking
		// a second time will guarantee we are looking at cached data and not the
		// initially built dateformat
		sdf = OpenmrsUtil.getDateFormat(locale);
		Assert.assertNotSame("cached dateFormatCache entry is modifiable", 
			OpenmrsUtil.getDateFormat(locale), sdf);
		
		// verify changing the pattern on our variable does not affect the cache
		sdf.applyPattern("yyyymmdd");
		Assert.assertTrue("cached dateFormatCache pattern is modifiable", 
			!OpenmrsUtil.getDateFormat(locale).toPattern().equals(sdf.toPattern()));
	}
}
