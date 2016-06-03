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
package org.openmrs.module.dotsreports.reporting.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.LogicUtil;

/**
 * Hack which returns the latest MDRTB program enrollment date for all patients.
 */

public class ProgramDataSourceMDRTB implements LogicDataSource {
	
	private static Log log = LogFactory.getLog(ProgramDataSourceMDRTB.class);
	private static final Collection<String> keys = new ArrayList<String>();

	/**
	 * @see LogicDataSource#read(LogicContext, Cohort, .LogicCriteria)   
	 */
	public Map<Integer, Result> read(LogicContext context, Cohort patients, LogicCriteria criteria) {
		
		log.info("read patient programs for " + patients.size() + " patients, criteria " + criteria);
		Map<Integer, Result> resultSet = new HashMap<Integer, Result>();
		Collection<PatientProgram> patientPrograms = getPatientPrograms(patients, criteria);
		
		for (PatientProgram patientProgram : patientPrograms) {
			Integer personId = patientProgram.getPatient().getPersonId();
			
			Result result = new Result(patientProgram.getProgram().getConcept());
            result.setResultDate(patientProgram.getDateEnrolled());

			if (result != null) {
				if (!resultSet.containsKey(personId)) {
					resultSet.put(personId, result);
				} 
				else {
				    Result oldResult = resultSet.get(personId);
				    if (oldResult.getResultDate().before(result.getResultDate())){
				        resultSet.remove(personId);
				        resultSet.put(personId, result);
				    }    
				}
			}
		}

		LogicUtil.applyAggregators(resultSet, criteria, patients);
		return resultSet;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getDefaultTTL()
	 */
	public int getDefaultTTL() {
		return 60 * 30; // 30 minutes
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getKeys()
	 */
	public Collection<String> getKeys() {
		return keys;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#hasKey(java.lang.String)
	 */
	public boolean hasKey(String key) {
		return getKeys().contains(key);
	}
	
	/**
	 * Get the patient programs for the
	 * 
	 * @param patients
	 * @param criteria
	 * @return
	 */
	public Collection<PatientProgram> getPatientPrograms(Cohort patients, LogicCriteria criteria) {
		  Collection<PatientProgram> patientPrograms = new ArrayList<PatientProgram>();
		  ProgramWorkflowService service = Context.getProgramWorkflowService();

		      
		      String token = criteria.getRootToken();
    		  Program prog = service.getProgramByName(token);
    		  List<Program> progList = new ArrayList<Program>();
    		  progList.add(prog);
    		  patientPrograms = service.getPatientPrograms(patients, progList);


		//log.info("Patient programs: " + patientPrograms.size());
		return patientPrograms;
		
	}
	
    
    public void addKey(String key) {
        getKeys().add(key);
    }
}
