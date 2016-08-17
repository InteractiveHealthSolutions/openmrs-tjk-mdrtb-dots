package org.openmrs.module.labmodule.specimen;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;

/**
 * An implementation of the MdrtbSpecimen. This wraps an Encounter and provides access to the
 * various specimen-related data in Encounter
 */
public class LabResultImpl implements LabResult {
	
	// TODO: could potentially cache all the get/set variables in private instance variables here...
	// to make this more like a real "command object"; then could create a "initialize" method that
	// automatically runs all the getters (and thereby loading the cache)
	// could do this for all the specimen related objects
	// TODO: or, actually, perhaps the thing to do here is simply to pre-load the concept maps that the module uses?
	// this is all that would be required to "detach" this object from the business-layer (i think... for 
	// instance if we wanted to pass this object as a web service?)  (of course, not sure exactly how lazy loading would
	// affect this)
	
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Encounter encounter; // the encounter where information about the specimen is stored
	private String patientStatus;
	
	// TODO: we cache the result map; do we need to worry about resetting the cache ?
	// (right now it isn't much of an issue because in the web model a new specimen is instantiated during each request)
	private Map<Integer,List<DstResult>> dstResultsMap = null; 
	
	public LabResultImpl() {
		// empty constructor
	}
	
	// set up a LabResult object from an existing encounter
	public LabResultImpl(Encounter encounter) {
		this.encounter = encounter;
		
		String primaryIdentifier = Context.getAdministrationService().getGlobalProperty("mdrtb.primaryPatientIdentifierType");
		if(encounter.getPatient().getPatientIdentifier(primaryIdentifier) == null)
			patientStatus = "Suspect";
		else
			patientStatus = "Confirmed";
	}
	
	// initialize a new LabResult, given a patient
	public LabResultImpl(Patient patient) {
		
		if(patient == null) {
			throw new RuntimeException("Can't create new specimen if patient is null");
		}
		
		// set up the encounter for this specimen
		encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(Context.getAdministrationService().getGlobalProperty("labmodule.test_result_encounter_type")));
	}
	
	public Object getLabResult() {
		return this.encounter;
	}
	
	public String getId() {
		if (this.encounter.getId() != null) {
			return this.encounter.getId().toString();
		}
		else {
			return null;
		}
	}
	
	public void setLabNumber(String labNumber) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.LAB_NO), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && labNumber == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(labNumber)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(labNumber != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.LAB_NO), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(labNumber);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setInvestigationDate(Date dateInvestigation) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateInvestigation == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(dateInvestigation)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(dateInvestigation != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(dateInvestigation);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setRequestingLabName(Concept requestingLabName) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && requestingLabName == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(requestingLabName)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(requestingLabName != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(requestingLabName);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setInvestigationPurpose(Concept investigationPurpose) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && investigationPurpose == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(investigationPurpose)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(investigationPurpose != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(investigationPurpose);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setBiologicalSpecimen(Concept biologicalSpecimen) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.SAMPLE_SOURCE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && biologicalSpecimen == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(biologicalSpecimen)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(biologicalSpecimen != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.SAMPLE_SOURCE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(biologicalSpecimen);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setPeripheralLabNumber(String peripheralLabNumber) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.PERIPHERAL_LAB_NO), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && peripheralLabNumber == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(peripheralLabNumber)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(peripheralLabNumber != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.PERIPHERAL_LAB_NO), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(peripheralLabNumber);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setMicroscopyResult(Concept bacterioscopyResult) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && bacterioscopyResult == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(bacterioscopyResult)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(bacterioscopyResult != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(bacterioscopyResult);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setDateResult(Date dateResult) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.RESULT_DATE), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateResult == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(dateResult)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(dateResult != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.RESULT_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(dateResult);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setDateSampleSentToBacteriologicalLab(Date dateSampleSentToBacteriologicalLab) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.DATE_SAMPLE_SENT_TO_BACTERIOLOGICAL_LAB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && dateSampleSentToBacteriologicalLab == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(dateSampleSentToBacteriologicalLab)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(dateSampleSentToBacteriologicalLab != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.DATE_SAMPLE_SENT_TO_BACTERIOLOGICAL_LAB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(dateSampleSentToBacteriologicalLab);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setResultOfInvestigationOfBacteriologicalLab(String resultOfInvestigationOfBacteriologicalLab) {
		
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.RESULT_FROM_BACTERIOLOGICAL_LAB), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && resultOfInvestigationOfBacteriologicalLab == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has changed.
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(resultOfInvestigationOfBacteriologicalLab)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by lab module lab Entry tracking UI");
			}
				
			// now create the new Obs and add it to the encounter	
			if(resultOfInvestigationOfBacteriologicalLab != null) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.RESULT_FROM_BACTERIOLOGICAL_LAB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(resultOfInvestigationOfBacteriologicalLab);
				encounter.addObs(obs);
			}
		} 
	}
	
	public void setComments(String comments) {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_COMMENTS), encounter);
		
		// if this obs have not been created, and there is no data to add, do nothing
		if (obs == null && StringUtils.isBlank(comments)) {
			return;
		}
		
		// we only have to update this if the value has changed or this is a new obs
		if (obs == null || !StringUtils.equals(obs.getValueText(), comments)) {
			
			// void the existing obs if it exists
			// (we have to do this manually because openmrs doesn't void obs when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason("voided by Mdr-tb module specimen tracking UI");
			}
				
			// now create the new Obs and add it to the encounter
			if(StringUtils.isNotBlank(comments)) {
				obs = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_COMMENTS), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(comments);
				encounter.addObs(obs);
			}
		}
	}
	
	public void setPatient(Patient patient) {
		encounter.setPatient(patient);
		
		// also propagate this patient to all the other obs
		for(Obs obs : encounter.getAllObs()) {
			obs.setPerson(patient);
		}
	}
	
	public void setLocation(Location location) {
		encounter.setLocation(location);
		
		// also propagate this date to all the other obs
		for(Obs obs : encounter.getAllObs()) {
			obs.setLocation(location);
		}
	}
	
	public void setProvider(Person provider) {
		encounter.setProvider(provider);
	}
	
	public void setDateCollected(Date dateCollected) {
		encounter.setEncounterDatetime(dateCollected);
	
		// also propagate this date to all the other obs
		for(Obs obs : encounter.getAllObs()) {
			obs.setObsDatetime(dateCollected);
		}
	}

	public int compareTo(LabResult labResultToCompare) {
		return this.getDateCollected().compareTo(labResultToCompare.getDateCollected());
	}

	@Override
	public String getLabNumber() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.LAB_NO), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}

	@Override
	public Date getInvestigationDate() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_DATE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueDatetime();
		}
	}

	@Override
	public Concept getRequestingLabName() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.REQUESTING_MEDICAL_FACILITY), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}

	
	@Override
	public Concept getInvestigationPurpose() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.INVESTIGATION_PURPOSE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}

	@Override
	public Concept getBiologicalSpecimen() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.SAMPLE_SOURCE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}

	@Override
	public String getPeripheralLabNumber() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.PERIPHERAL_LAB_NO), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}

	@Override
	public Concept getMicroscopyResult() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueCoded();
		}
	}

	@Override
	public Date getDateResult() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.RESULT_DATE), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueDatetime();
		}
	}

	@Override
	public Date getDateSampleSentToBacteriologicalLab() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.DATE_SAMPLE_SENT_TO_BACTERIOLOGICAL_LAB), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueDatetime();
		}
	}

	@Override
	public String getResultOfInvestigationOfBacteriologicalLab() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.RESULT_FROM_BACTERIOLOGICAL_LAB), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
	}

	@Override
	public String getComments() {
		Obs obs = TbUtil.getObsFromEncounter(Context.getService(TbService.class).getConcept(TbConcepts.SPECIMEN_COMMENTS), encounter);
		
		if (obs == null) {
			return null;
		}
		else {
			return obs.getValueText();
		}
		
	}

	@Override
	public Patient getPatient() {
		return encounter.getPatient();
	}

	@Override
	public Location getLocation() {
		return encounter.getLocation();
	}

	@Override
	public Person getProvider() {
		return encounter.getProvider();
	}

	@Override
	public Date getDateCollected() {
		return encounter.getEncounterDatetime();
	}
	
	public Microscopy addMicroscopy() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		MicroscopyImpl microscopy = new MicroscopyImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(microscopy.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		microscopy.setLab(null);
		
		return microscopy;
	}
	
	
	public Xpert addXpert() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		XpertImpl xpert = new XpertImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(xpert.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		xpert.setLab(null);
		
		return xpert;
	}
	
	public Smear addSmear() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		SmearImpl smear = new SmearImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(smear.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		smear.setLab(null);
		
		return smear;
	}
	
	
	public HAIN addHAIN() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		HAINImpl hain = new HAINImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(hain.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		hain.setLab(null);
		
		return hain;
	}
	
	
	public HAIN2 addHAIN2() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		HAIN2Impl hain = new HAIN2Impl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(hain.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		hain.setLab(null);
		
		return hain;
	}
	
	
	public Culture addCulture() {
		// cast to an Impl so we can access protected methods from within the specimen impl
		CultureImpl culture = new CultureImpl(this.encounter);
		
		// add the smear to the master encounter
		this.encounter.addObs(culture.getObs());
		
		// we need to set the location back to null, since it will be set to the encounter location
		// when it is added to the location
		culture.setLab(null);
		
		return culture;
	}
	
	public List<Culture> getCultures() {
		List<Culture> cultures = new LinkedList<Culture>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.CULTURE_CONSTRUCT))) {
					cultures.add(new CultureImpl(obs));
				}
			}
		}
		Collections.sort(cultures);
		return cultures;
	}
	
	public List<HAIN2> getHAINS2(){
		List<HAIN2> hains2 = new LinkedList<HAIN2>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.HAIN_2_CONSTRUCT))) {
					hains2.add(new HAIN2Impl(obs));
				}
			}
		}
		Collections.sort(hains2);
		return hains2;
	}
	
	public List<HAIN> getHAINS(){
		List<HAIN> hains = new LinkedList<HAIN>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.HAIN_CONSTRUCT))) {
					hains.add(new HAINImpl(obs));
				}
			}
		}
		Collections.sort(hains);
		return hains;
	}
	
	public List<Xpert> getXperts(){
		List<Xpert> xpert = new LinkedList<Xpert>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.XPERT_CONSTRUCT))) {
					xpert.add(new XpertImpl(obs));
				}
			}
		}
		Collections.sort(xpert);
		return xpert;
	}
	
	public List<Microscopy> getMicroscopies(){
		List<Microscopy> microscopy = new LinkedList<Microscopy>();

		// iterate through all the obs groups, create smears from them, and add them to the list
		if(encounter.getObsAtTopLevel(false) != null) {
			for(Obs obs : encounter.getObsAtTopLevel(false)) {
				if (obs.getConcept().equals(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_CONSTRUCT))) {
					microscopy.add(new MicroscopyImpl(obs));
				}
			}
		}
		Collections.sort(microscopy);
		return microscopy;
	}
	
	public void voidPeripheralLabNo(){
		voidObs(Context.getService(TbService.class).getConcept(TbConcepts.PERIPHERAL_LAB_NO));
	}
	
	public void voidMicroscopyResult(){
		voidObs(Context.getService(TbService.class).getConcept(TbConcepts.MICROSCOPY_RESULT));
	}
	
	public void voidDateResult(){
		voidObs(Context.getService(TbService.class).getConcept(TbConcepts.RESULT_DATE));
	}
	
	public void voidObs(Concept concept){
		
		for(Obs obs : this.encounter.getAllObs()){
			
			if(obs.getConcept() == concept){
				
				obs.setVoided(true);
				break;
			}
			
		}
		
	}
	
	public void setStatus(String status){
		patientStatus = status;
	}
	
	public String getStatus(){
		return patientStatus;
	}
	
}
