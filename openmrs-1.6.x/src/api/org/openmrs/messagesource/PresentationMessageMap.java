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
package org.openmrs.messagesource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A mapped collection of PresentationMessages, all of which are enforced to be in the same locale.
 */
public class PresentationMessageMap implements Map<String, PresentationMessage> {
	
	private Locale locale;
	
	private Map<String, PresentationMessage> internalMap = new HashMap<String, PresentationMessage>();
	
	/**
	 * Create a new PresentationMessageMap for the given locale.
	 * 
	 * @param locale
	 */
	public PresentationMessageMap(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		internalMap.clear();
	}
	
	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}
	
	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}
	
	/**
	 * @see java.util.Map#entrySet()
	 */
	public Set<java.util.Map.Entry<String, PresentationMessage>> entrySet() {
		return internalMap.entrySet();
	}
	
	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public PresentationMessage get(Object key) {
		return internalMap.get(key);
	}
	
	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}
	
	/**
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		return internalMap.keySet();
	}
	
	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 * @should should ignore non matching locale messages
	 */
	public PresentationMessage put(String key, PresentationMessage value) {
		PresentationMessage putValue = null;
		if (value.getLocale().equals(locale)) {
			putValue = internalMap.put(key, value);
		}
		return putValue;
	}
	
	/**
	 * Adds all entries from an input Map which have PresentationMessages from the same locale.
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 * @should filter out non matching locale messages from batch add
	 */
	public void putAll(Map<? extends String, ? extends PresentationMessage> t) {
		//Map<String, PresentationMessage> compatibleMap = new HashMap<String, PresentationMessage>();
		for (Entry<? extends String, ? extends PresentationMessage> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public PresentationMessage remove(Object key) {
		return internalMap.remove(key);
	}
	
	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		return internalMap.size();
	}
	
	/**
	 * @see java.util.Map#values()
	 */
	public Collection<PresentationMessage> values() {
		return internalMap.values();
	}
	
}
