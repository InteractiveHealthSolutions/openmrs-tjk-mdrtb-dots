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
package org.openmrs;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Every installation of OpenMRS should get a unique implementation id. If multiple sites use the
 * same dictionary/form setup, than those sites should share the same implementation id. The
 * ImplementationId is stored and verified on the openmrs servers.
 */
@Root
public class ImplementationId implements java.io.Serializable {
	
	public static final long serialVersionUID = 3752234110L;
	
	// Fields
	
	private String name;
	
	private String description;
	
	private String implementationId;
	
	private String passphrase;
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof ImplementationId) {
			ImplementationId other = (ImplementationId)o;
			
			if (getImplementationId() != null && other.getImplementationId() != null)
				return getImplementationId().equals(other.getImplementationId());
			
			return this == other;
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (getImplementationId() != null)
			return getImplementationId().hashCode() * 342 + 3;
		
		return super.hashCode();
	}
	
	/**
	 * Text describing this implementation. (e.g. Source for the AMPATH program in Kenya. Created by
	 * Paul Biondich)
	 * 
	 * @return Returns the description.
	 */
	@Element(data = true)
	public String getDescription() {
		return description;
	}
	
	/**
	 * Text describing this implementation. (e.g. Source for the AMPATH program in Kenya. Created by
	 * Paul Biondich)
	 * 
	 * @param description The description to set.
	 */
	@Element(data = true)
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * This is the unique id for this implementation. <br/>
	 * <br/>
	 * The implementation id corresponds to the hl7Code of the ConceptSource that this corresponds
	 * to.<br/>
	 * <br/>
	 * Must be limited to 20 characters and numbers. The characters "^" and "|" are not allowed.
	 * 
	 * @param implementationId the implementationId to set
	 * @return the implementationId
	 */
	@Attribute
	public String getImplementationId() {
		return implementationId;
	}
	
	/**
	 * This is the unique id for this implementation. <br/>
	 * <br/>
	 * The implementation id corresponds to the hl7Code of the ConceptSource that this corresponds
	 * to. <br/>
	 * <br/>
	 * Must be limited to 20 characters and numbers. The characters "^" and "|" are not allowed.
	 * 
	 * @param implementationId the implementationId to set
	 */
	@Attribute
	public void setImplementationId(String implementationId) {
		this.implementationId = implementationId;
	}
	
	/**
	 * This text is a long text string that is used to validate who uses an implementation id.
	 * Multiple installations of openmrs can use the same implmentation id, but they must all know
	 * the passphrase. (Note that if an implementation id is shared, it is assumed that those
	 * installations are the same implementation).
	 * 
	 * @return the passphrase
	 */
	@Element(data = true, required = false)
	public String getPassphrase() {
		return passphrase;
	}
	
	/**
	 * This text is a long text string that is used to validate who uses an implementation id.
	 * Multiple installations of openmrs can use the same implmentation id, but they must all know
	 * the passphrase. (Note that if an implementation id is shared, it is assumed that those
	 * installations are the same implementation).
	 * 
	 * @param passphrase the passphrase to set
	 */
	@Element(data = true, required = false)
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
	
	/**
	 * A descriptive name for this implementation (e.g. AMRS installation in Eldoret, Kenya)
	 * 
	 * @return Returns the name.
	 */
	@Element(data = true)
	public String getName() {
		return name;
	}
	
	/**
	 * A descriptive name for this implementation (e.g. AMRS installation in Eldoret, Kenya)
	 * 
	 * @param name The concept source name to set.
	 */
	@Element(data = true)
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Impl Id: " + getImplementationId() + " name: " + getName() + " desc: " + getDescription();
	}
}
