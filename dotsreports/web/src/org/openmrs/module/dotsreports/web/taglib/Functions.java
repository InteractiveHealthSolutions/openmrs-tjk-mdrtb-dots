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
package org.openmrs.module.dotsreports.web.taglib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.regimen.Regimen;
import org.openmrs.module.dotsreports.regimen.RegimenChange;
import org.openmrs.module.dotsreports.regimen.RegimenHistory;
import org.openmrs.module.dotsreports.regimen.RegimenType;
import org.openmrs.module.dotsreports.regimen.RegimenUtils;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.common.ObjectUtil;

public class Functions {
	
	/**
	 * @return true if the passed collection contains the passed object
	 */
	public static boolean collectionContains(Collection<Object> c, Object o) {
		return c != null && c.contains(o);
	}
	
	/**
	 * @return true if the passed array contains the passed object
	 */
	public static boolean arrayContains(Object[] c, Object o) {
		if (c != null) {
			for (Object toCheck : c) {
				if (toCheck.equals(o)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return true if the passed collection contains the passed object
	 */
	public static boolean instanceOf(Object o, String className) {
		try {
			Class<?> c = Context.loadClass(className);
			return o != null && c.isAssignableFrom(o.getClass());
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to load class " + className);
		}
	}
	
	/**
	 * @return a List of Objects, ordered in the reverse of their original order
	 */
	public static <T> List<T> reverse(Collection<T> collection) {
		List<T> l = new ArrayList<T>(collection);
		Collections.reverse(l);
		return l;
	}
	
	/**
	 * @return a List of Objects, ordered in their natural order
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable> List<T> sort(Collection<T> collection) {
		List<T> l = new ArrayList<T>(collection);
		Collections.sort(l);
		return l;
	}
	
	/**
	 * @returns the Regimen on the passed Date
	 */
	public static RegimenChange changeOnDate(RegimenHistory rh, Date d) {
		return rh.getRegimenChanges().get(d);
	}
	
	/**
	 * @returns the Regimen on the passed Date
	 */
	public static Regimen regimenOnDate(RegimenHistory rh, Date d, Boolean includeChangesOnDate) {
		return rh.getRegimenOnDate(d, includeChangesOnDate);
	}
	
	/**
	 * @return the comparison result between the two passed objects
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable>int compare(T c1, T c2) {
		return c1.compareTo(c2);
	}
	
	/**
	 * @return true if the passed date is in the future
	 */
	public static boolean isFutureDate(Date d) {
		return d != null && d.compareTo(new Date()) > 0;
	}
	
	/**
	 * @return a String representation of a Date
	 */
	public static String formatDate(Date d, String format) {
		if (d == null) {
			return "";
		}
		return new SimpleDateFormat(format, Context.getLocale()).format(d);
	}
	
	/**
	 * @return a String representation of a Date
	 */
	public static String formatDateDefault(Date d) {
		if (d == null) {
			return "";
		}
		return Context.getDateFormat().format(d);
	}
	
	/**
	 * @return a Date from a String
	 */
	public static Date parseDate(String s, String format) {
		if (s == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(format,Context.getLocale()).parse(s.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @return the matching Concept
	 */
	public static Concept getConcept(String lookup) {
		return Context.getService(TbService.class).findMatchingConcept(lookup);
	}
	
	/**
	 * @return a List of Drugs who are in the set matching the passed conceptSet
	 */
	public static List<Concept> genericsInSet(String conceptSet) {
		return RegimenUtils.getGenericsForDrugSet(conceptSet);
	}
	
	/**
	 * @return a List of Drugs who are in the set matching the passed conceptSet
	 */
	public static List<Drug> drugsInSet(String conceptSet) {
		if (ObjectUtil.isNull(conceptSet)) {
			return Context.getConceptService().getAllDrugs();
		}
		if ("*".equals(conceptSet)) {
			List<Drug> drugs = Context.getConceptService().getAllDrugs();
			for (RegimenType t : RegimenUtils.getRegimenTypes()) {
				if (!"*".equals(t.getDrugSet())) {
					drugs.removeAll(drugsInSet(t.getDrugSet()));
				}
			}
		}
		Concept c = Context.getConceptService().getConcept(conceptSet);
		return RegimenUtils.getDrugsWithGenericInSet(c);
	}
	
	public static List<Concept> answersToQuestion(String question) {
		List<Concept> ret = new ArrayList<Concept>();
		Concept c = Context.getService(TbService.class).getConcept(question);
		if (c != null) {
			for (ConceptAnswer ca : c.getAnswers()) {
				ret.add(ca.getAnswerConcept());
			}
		}
		return ret;
	}
}
