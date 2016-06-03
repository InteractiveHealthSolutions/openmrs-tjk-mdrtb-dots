package org.openmrs.module.mdrtb.patientchart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.program.MdrtbPatientProgram;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.SpecimenUtil;


public class PatientChartFactory {
	
	protected final Log log = LogFactory.getLog(getClass());

	public PatientChart createPatientChart(PatientProgram program) {
		
		Patient patient = program.getPatient();
		PatientChart chart = new PatientChart();
		
		if (patient == null) {
			log.warn("Can't fetch patient chart, patient is null");
			return null;
		}
		
		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
		
		// first, fetch all the specimens for this patient
		List<Specimen> specimens = mdrtbProgram.getSpecimensDuringProgram();
		
		// if no specimens, operate on the an empty set
		if (specimens == null) {
			specimens = new LinkedList<Specimen>();
		}
		
		// the getSpecimen method should return the specimens sorted, but just in case it is changed
		Collections.sort(specimens);
		
		// now we group the specimens by day
		// TODO: make this configurable so that we don't always do this?
		SpecimenUtil.groupSpecimensByDay(specimens);
		
		// also get the regimen history for the patient
		RegimenHistory regimenHistory = RegimenUtils.getTbRegimenHistory(patient);
		
		// calculate all the state change components we need to display on the chart
		List<RecordComponent> stateChangeRecordComponents = createAllStateChangeRecordComponents(program);
		
		// get the treatment start date to use as the chart start date
		Date treatmentStartDate = mdrtbProgram.getTreatmentStartDateDuringProgram();
		
		// get the treatment end date
		Date treatmentEndDate = mdrtbProgram.getTreatmentEndDateDuringProgram();
		
		// determine if the patient have ever been on treatment
		Boolean hasBeenOnTreatment = (treatmentStartDate != null);
		
		// if not on treatment, and no specimens, nothing to chart, return null!
		if(!hasBeenOnTreatment && (specimens == null || specimens.isEmpty())) {
			return null;
		}
		
		// if the patient has been on treatment, but there is no treatment end date, (i.e, treatment is active), set the treatment end date to today
		if(hasBeenOnTreatment && treatmentEndDate == null) {
			treatmentEndDate = new Date();
		}
		
		Calendar recordStartDate = Calendar.getInstance();
		Calendar recordEndDate;
		Record record;
		
		// if the patient has never been on treatment, start at program start date
		if(!hasBeenOnTreatment) {
			recordStartDate.setTime(program.getDateEnrolled());
			recordStartDate.set(Calendar.DAY_OF_MONTH, 1);
			recordEndDate = (Calendar) recordStartDate.clone();
			recordEndDate.add(Calendar.MONTH, 1);	
		}
		// if there's a treatment start date, that is the start date of the first record (in this case, month 0)
		else {
			recordStartDate.setTime(treatmentStartDate);

			// now we create the PRIOR and BASELINE rows
			
			// first, we want to get all specimens collected more than a month before the treatment start date (for the prior row)
			recordStartDate.add(Calendar.MONTH, -1);  // set the periodStartDate one month back and use it as the endDate parameter to createRecordComponents
			record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, null, recordStartDate);
			record.setLabel(Context.getMessageSourceService().getMessage("mdrtb.prior"));
			chart.getRecords().add(record);
		
			// now all the specimens collected in the month prior to treatment start date (for the baseline row)
			recordEndDate = (Calendar) recordStartDate.clone();
			recordEndDate.add(Calendar.MONTH, 1); // create an end date one month after the startDate
			record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, recordStartDate, recordEndDate);
			record.setLabel(Context.getMessageSourceService().getMessage("mdrtb.baseline"));
			chart.getRecords().add(record);
			
			// increment
			recordStartDate.add(Calendar.MONTH, 1);
			recordEndDate.add(Calendar.MONTH, 1);
		}
		
		// now go through the add all the other specimens
		Integer iteration = 0;
	
		// loop until we are out of specimens, or until we've passed the treatment end date, whatever is later
		while(specimens.size() > 0 || (treatmentEndDate != null && (!recordStartDate.getTime().after(treatmentEndDate))
										&& (!recordEndDate.getTime().after(new Date())) ) ) {
			record = createChartRecord(specimens, stateChangeRecordComponents, regimenHistory, recordStartDate, recordEndDate);
			
			// label rows by treatment month if has been on treatment, otherwise label by actual month
			if(hasBeenOnTreatment) {
				record.setLabel(iteration.toString());
			}
			else {
				DateFormat format = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
				record.setLabel(format.format(recordStartDate.getTime()));
			}
			
			// add the record to the chart
			chart.getRecords().add(record);
			
			// increment
			recordStartDate.add(Calendar.MONTH, 1);
			recordEndDate.add(Calendar.MONTH, 1);
			iteration++;
		}
		
		// add the possible drug types
		List<Concept> drugTypes = getDrugTypesForChart(chart);
		
		chart.setDrugTypes(drugTypes);
		
		return chart;
	}
	
	 /**
	 * Utility functions
	 */
	
	/**
	 * Creates a record for the patient chart in the specified range
	 */
	private Record createChartRecord(List<Specimen> specimens, List<RecordComponent> stateChangeRecordComponents, RegimenHistory regimenHistory, Calendar recordStartDate, Calendar recordEndDate) {
		// get the specimen components for this period
		List<RecordComponent> components = createSpecimenRecordComponents(specimens, regimenHistory, recordStartDate, recordEndDate);
		
		// if there are no specimen components, simply gather all the regimens the patient was on during this time period
		// and put it on a specimen component
		if(components.size() == 0) {
			List<Regimen> regimens = regimenHistory.getRegimensDuring((recordStartDate != null ? recordStartDate.getTime() : null), recordEndDate.getTime());
			// note that we set the date for this component to be one millisecond after the start date for the record; this way regimen-only components
			// sort after any other components with a date equal to the record start date (i.e., so that a treatment start date event sorts before any
			// regimen information)
			components.add(new SpecimenRecordComponent((recordStartDate != null ? new Date(recordStartDate.getTime().getTime() + 1) : null), regimens));
		}
		
		// now add the state change components for this period
		components.addAll(getStateChangeRecordComponentsBeforeDate(stateChangeRecordComponents, recordEndDate));
		
		// sort the components
		Collections.sort(components);
		
		// create a new record using those components
		return new Record(components);
	}
	
	
	/**
	 * Assembles all the specimen components for a record
	 */
	private List<RecordComponent> createSpecimenRecordComponents(List<Specimen> specimens, RegimenHistory regimenHistory, Calendar startDate, Calendar endDate) {
		
		List<RecordComponent> components = new LinkedList<RecordComponent>();
		
		// get all the specimens to include in this record, i.e. all specimens collected during this time period
		List<Specimen> specimensToAdd = getSpecimensBeforeDate(specimens, endDate);
		
		Date dateCounter;
		if(startDate != null) {
			dateCounter = startDate.getTime();
		}
		else {
			// if the startdate is null (i.e., this is the "prior" case) set start date to the collection date of the first specimen
			if(specimensToAdd != null && specimensToAdd.size() > 0) {
				dateCounter = specimensToAdd.get(0).getDateCollected();
			}
			else {
				// otherwise, return an empty set
				return components;
			}
		}
		
		// now create a new record component for each specimen
		if(specimensToAdd.size() > 0) {
			ListIterator<Specimen> i = specimensToAdd.listIterator();
			
			while (i.hasNext()) {
				Specimen specimenToAdd = i.next();
				List<Regimen> regimens;
			
				// fetch the regimens that we want to display in this component
				if(!i.hasNext()) {
					regimens = regimenHistory.getRegimensDuring(dateCounter,endDate.getTime()); 
				}
				else{
					regimens = regimenHistory.getRegimensDuring(dateCounter,specimenToAdd.getDateCollected());
					dateCounter = specimenToAdd.getDateCollected();
				}
						
				// add the components
				components.add(new SpecimenRecordComponent(specimenToAdd, regimens));
			}
		}
				
		return components;
	}
	
	/**
	 * Assembles all state change components for the entire chart
	 */
	private List<RecordComponent> createAllStateChangeRecordComponents(PatientProgram program) {
		
		MdrtbPatientProgram mdrtbProgram = new MdrtbPatientProgram(program);
		
		List<RecordComponent> stateChangeRecordComponents = new LinkedList<RecordComponent>();
		
		// the only state we are worried about at this point is the treatment start date and treatment end date
		if(mdrtbProgram.getTreatmentStartDateDuringProgram() != null) {
			stateChangeRecordComponents.add(new StateChangeRecordComponent(mdrtbProgram.getTreatmentStartDateDuringProgram(), Context.getMessageSourceService().getMessage("mdrtb.treatmentstartdate")));
		}
			
		if(mdrtbProgram.getTreatmentEndDateDuringProgram() != null) {
			stateChangeRecordComponents.add(new StateChangeRecordComponent(mdrtbProgram.getTreatmentEndDateDuringProgram(), Context.getMessageSourceService().getMessage("mdrtb.treatmentEndDate")));
		}
		
		Collections.sort(stateChangeRecordComponents);
		
		return stateChangeRecordComponents;
	}
	
	// TODO: IMPORTANT: the assumption this method makes is that list is in descending date order
	// also, this method pulls all the components it returns off the list of component passed to it;
	// this method is intended to be use with the getPatientChart API method
	private List<RecordComponent> getStateChangeRecordComponentsBeforeDate(List<RecordComponent> components, Calendar compareDate) {
		List<RecordComponent> results = new LinkedList<RecordComponent>();
		Calendar stateChangeDate = Calendar.getInstance();
		
		while(!components.isEmpty()) {
			stateChangeDate.setTime(components.get(0).getDate());
			if(stateChangeDate.before(compareDate)) {
				results.add(components.get(0));
				components.remove(components.get(0));
			}
			else {
				// we don't need to keep checking since the the dates are in order
				break;
			}
		}
		
		return results;
	}
	
	// IMPORTANT: the assumption this method makes is that list of specimens are ordered in descending date order
	// also, this method pulls all the specimens it returns off the list of specimens passed to it;
	// this method is intended to be use with the getPatientChart API method
	private List<Specimen> getSpecimensBeforeDate(List<Specimen> specimens, Calendar compareDate) {
		List<Specimen> results = new LinkedList<Specimen>();
		Calendar specimenDate = Calendar.getInstance();
		
		while(!specimens.isEmpty()) {
			specimenDate.setTime(specimens.get(0).getDateCollected());
			if(specimenDate.before(compareDate)) {
				results.add(specimens.get(0));
				specimens.remove(specimens.get(0));
			}
			else {
				// we don't need to keep checking since the the dates are in order
				break;
			}
		}
		
		return results;
	}
	
	/**
	 * Retrieves the drug types to display in the chart from global property
	 */
	private List<Concept> getDrugTypesForChart(PatientChart patientChart) {
		// get all the possible drug types to display--this method also returns them in the order we want to display them
		return Context.getService(MdrtbService.class).getMdrtbDrugs();
	}
}
