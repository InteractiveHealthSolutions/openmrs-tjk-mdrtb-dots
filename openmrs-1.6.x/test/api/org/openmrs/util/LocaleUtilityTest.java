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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Behavior-driven unit tests for {@link LocaleUtility} class
 */
public class LocaleUtilityTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should confirm matching language as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldConfirmMatchingLanguageAsCompatible() throws Exception {
		Locale lhs = Locale.ENGLISH;
		Locale rhs = Locale.ENGLISH;
		
		assertTrue(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should not confirm different language as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldNotConfirmDifferentLanguageAsCompatible() throws Exception {
		Locale lhs = Locale.ENGLISH;
		Locale rhs = Locale.FRENCH;
		
		assertFalse(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should confirm matching country as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldConfirmMatchingCountryAsCompatible() throws Exception {
		Locale lhs = Locale.US;
		Locale rhs = Locale.US;
		
		assertTrue(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should not confirm different country as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldNotConfirmDifferentCountryAsCompatible() throws Exception {
		Locale lhs = Locale.US;
		Locale rhs = Locale.UK;
		
		assertFalse(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * Two locales, where one has no country specified, but where the language matches, should be
	 * considered compatible.
	 * 
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should confirm same language missing country as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldConfirmSameLanguageMissingCountryAsCompatible() throws Exception {
		Locale lhs = Locale.US;
		Locale rhs = Locale.ENGLISH;
		
		assertTrue(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * Two locales, where one has no country specified, but where the language does not match,
	 * should not be considered compatible.
	 * 
	 * @see {@link LocaleUtility#areCompatible(Locale,Locale)}
	 */
	@Test
	@Verifies(value = "should confirm different language missing country as compatible", method = "areCompatible(Locale,Locale)")
	public void areCompatible_shouldConfirmDifferentLanguageMissingCountryAsCompatible() throws Exception {
		Locale lhs = Locale.US;
		Locale rhs = Locale.FRENCH;
		
		assertFalse(LocaleUtility.areCompatible(lhs, rhs));
	}
	
	/**
	 * @see {@link LocaleUtility#getDefaultLocale()}
	 */
	@Test
	@Verifies(value = "should not fail with bogus global property value", method = "getDefaultLocale()")
	public void getDefaultLocale_shouldNotFailWithBogusGlobalPropertyValue() throws Exception {
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "asdfasdf"));
		
		// not check for nonnullness
		Assert.assertNotNull(LocaleUtility.getDefaultLocale());
	}
	
	/**
	 * @see {@link LocaleUtility#getDefaultLocale()}
	 */
	@Test
	@Verifies(value = "should not fail with empty global property value", method = "getDefaultLocale()")
	public void getDefaultLocale_shouldNotFailWithEmptyGlobalPropertyValue() throws Exception {
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, ""));
		
		// not check for nonnullness
		Assert.assertNotNull(LocaleUtility.getDefaultLocale());
	}
	
	/**
	 * @see {@link LocaleUtility#getDefaultLocale()}
	 */
	@Test
	@Verifies(value = "should not return null if global property does not exist", method = "getDefaultLocale()")
	public void getDefaultLocale_shouldNotReturnNullIfGlobalPropertyDoesNotExist() throws Exception {
		// sanity check
		Assert.assertNull(Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE));
		
		// not check for nonnullness
		Assert.assertNotNull(LocaleUtility.getDefaultLocale());
	}
	
	/**
	 * @see {@link LocaleUtility#getDefaultLocale()}
	 */
	@Test
	@Verifies(value = "should return locale object for global property", method = "getDefaultLocale()")
	public void getDefaultLocale_shouldReturnLocaleObjectForGlobalProperty() throws Exception {
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "ja"));
		
		Assert.assertEquals(Locale.JAPANESE, LocaleUtility.getDefaultLocale());
	}
	
	/**
	 * @see {@link LocaleUtility#fromSpecification(String)}
	 */
	@Test
	@Verifies(value = "should get locale from two character language code", method = "fromSpecification(String)")
	public void fromSpecification_shouldGetLocaleFromTwoCharacterLanguageCode() throws Exception {
		Assert.assertEquals(Locale.ENGLISH, LocaleUtility.fromSpecification("en"));
	}
	
	/**
	 * @see {@link LocaleUtility#fromSpecification(String)}
	 */
	@Test
	@Verifies(value = "should get locale from language code and country code", method = "fromSpecification(String)")
	public void fromSpecification_shouldGetLocaleFromLanguageCodeAndCountryCode() throws Exception {
		Assert.assertEquals(Locale.UK, LocaleUtility.fromSpecification("en_GB"));
	}
	
	/**
	 * @see {@link LocaleUtility#fromSpecification(String)}
	 */
	@Test
	@Verifies(value = "should get locale from language code country code and variant", method = "fromSpecification(String)")
	public void fromSpecification_shouldGetLocaleFromLanguageCodeCountryCodeAndVariant() throws Exception {
		Locale locale = LocaleUtility.fromSpecification("en_US_Traditional_WIN");
		Assert.assertEquals(Locale.US.getLanguage(), locale.getLanguage());
		Assert.assertEquals(Locale.US.getCountry(), locale.getCountry());
		Assert.assertEquals("Traditional,WIN", locale.getDisplayVariant());
	}
	
}
