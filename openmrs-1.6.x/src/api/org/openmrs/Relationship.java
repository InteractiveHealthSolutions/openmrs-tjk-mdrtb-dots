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

/**
 * Relationship
 */
public class Relationship extends BaseOpenmrsData implements java.io.Serializable {
	
	public static final long serialVersionUID = 323423L;
	
	// Fields
	
	private Integer relationshipId;
	
	private Person personA;
	
	private RelationshipType relationshipType;
	
	private Person personB;
	
	// Constructors
	
	/** default constructor */
	public Relationship() {
	}
	
	/** constructor with id */
	public Relationship(Integer relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	public Relationship(Person personA, Person personB, RelationshipType type) {
		this.personA = personA;
		this.personB = personB;
		this.relationshipType = type;
	}
	
	/**
	 * Does a shallow copy of this Relationship. Does NOT copy relationshipId
	 * 
	 * @return a copy of this relationship
	 */
	public Relationship copy() {
		return copyHelper(new Relationship());
	}
	
	/**
	 * The purpose of this method is to allow subclasses of Relationship to delegate a portion of
	 * their copy() method back to the superclass, in case the base class implementation changes.
	 * 
	 * @param target a Relationship that will have the state of <code>this</code> copied into it
	 * @return the Relationship that was passed in, with state copied into it
	 */
	protected Relationship copyHelper(Relationship target) {
		target.personA = getPersonA();
		target.relationshipType = getRelationshipType();
		target.personB = getPersonB();
		target.setCreator(getCreator());
		target.setDateCreated(getDateCreated());
		target.setVoided(isVoided());
		target.setVoidedBy(getVoidedBy());
		target.setDateVoided(getDateVoided());
		target.setVoidReason(getVoidReason());
		return target;
	}
	
	/**
	 * Compares two objects for similarity
	 * 
	 * @param obj
	 * @return boolean true/false whether or not they are the same objects
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Relationship) {
			Relationship m = (Relationship) obj;
			return (relationshipId.equals(m.getRelationshipId()));
		}
		return false;
	}
	
	public int hashCode() {
		if (this.getRelationshipId() == null)
			return super.hashCode();
		return this.getRelationshipId().hashCode();
	}
	
	// Property accessors
	
	/**
	 * @return Returns the personA.
	 */
	public Person getPersonA() {
		return personA;
	}
	
	/**
	 * @param personA The person to set.
	 */
	public void setPersonA(Person personA) {
		this.personA = personA;
	}
	
	/**
	 * @return Returns the relationship type.
	 */
	public RelationshipType getRelationshipType() {
		return relationshipType;
	}
	
	/**
	 * @param type The relationship type to set.
	 */
	public void setRelationshipType(RelationshipType type) {
		this.relationshipType = type;
	}
	
	/**
	 * @return Returns the relationshipId.
	 */
	public Integer getRelationshipId() {
		return relationshipId;
	}
	
	/**
	 * @param relationshipId The relationshipId to set.
	 */
	public void setRelationshipId(Integer relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	/**
	 * @return Returns the personB.
	 */
	public Person getPersonB() {
		return personB;
	}
	
	/**
	 * @param personB The relative to set.
	 */
	public void setPersonB(Person personB) {
		this.personB = personB;
	}
	
	/**
	 * @deprecated Use isVoided()
	 * @see #isVoided()
	 * @return Returns the voided.
	 */
	public Boolean getVoided() {
		return isVoided();
	}
	
	public String toString() {
		String relType = getRelationshipType() == null ? "NULL" : getRelationshipType().getaIsToB();
		return personA + " is the " + relType + " of " + personB;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		
		return getRelationshipId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setRelationshipId(id);
		
	}
	
}
