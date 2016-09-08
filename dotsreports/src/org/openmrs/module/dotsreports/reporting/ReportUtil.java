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
package org.openmrs.module.dotsreports.reporting;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.TbConcepts;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.program.TbPatientProgram;
import org.openmrs.module.dotsreports.reporting.data.Cohorts;
import org.openmrs.module.dotsreports.reporting.definition.DotsDstResultCohortDefinition;
import org.openmrs.module.dotsreports.service.TbService;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Utility methods
 */
public class ReportUtil {
	
	public static CodedObsCohortDefinition getCodedObsCohort(TimeModifier tm, Integer questionId, 
																  Date fromDate, Date toDate, SetComparator operator, 
																  Integer... answerIds) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(tm);
		cd.setQuestion(Context.getConceptService().getConcept(questionId));
		cd.setOnOrAfter(fromDate);
		cd.setOnOrBefore(toDate);
		cd.setOperator(operator);
		List<Concept> answers = new ArrayList<Concept>();
		for (Integer id : answerIds) {
			answers.add(Context.getConceptService().getConcept(id));
		}
		cd.setValueList(answers);
		return cd;
	}
	
	public static DotsDstResultCohortDefinition getDstProfileCohort(String profile, Date effectiveDate) {
		DotsDstResultCohortDefinition cd = new DotsDstResultCohortDefinition();
		cd.setMaxResultDate(effectiveDate);
		cd.setSpecificProfile(profile);
		return cd;
	}
	
	public static CohortDefinition getCompositionCohort(Map<String, Mapped<? extends CohortDefinition>> entries, String operator) {
		if (entries.size() == 1) {
			return entries.values().iterator().next().getParameterizable();
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Mapped<? extends CohortDefinition>> cd : entries.entrySet()) {
			d.addSearch(cd.getKey(), cd.getValue().getParameterizable(), cd.getValue().getParameterMappings());
			if (s.length() > 0) {
				s.append(" " + operator + " ");
			}
			s.append(cd.getKey());
		}
		d.setCompositionString(s.toString());
		return d;
	}
	

	public static CohortDefinition getCompositionCohort(String operator, List<CohortDefinition> definitions) {
		if (definitions.size() == 1) {
			return definitions.get(0);
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		int i = 1;
		for (CohortDefinition cd : definitions) {
			if (cd != null) {
				d.addSearch(""+i, cd, null);
				if (s.length() > 0) {
					s.append(" " + operator + " ");
				}
				s.append(i++);
			}
		}
		d.setCompositionString(s.toString());
		return d;
	}
	
	/*public static CohortDefinition getCompositionCohort(String string, List<CohortDefinition> cohortDefinitions) {
		if (string.size() == 1) {
			return string.values().iterator().next().getParameterizable();
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Mapped<? extends CohortDefinition>> cd : string.entrySet()) {
			d.addSearch(cd.getKey(), cd.getValue().getParameterizable(), cd.getValue().getParameterMappings());
			if (s.length() > 0) {
				s.append(" " + cohortDefinitions + " ");
			}
			s.append(cd.getKey());
		}
		d.setCompositionString(s.toString());
		return d;
	}*/
	
	public static CohortDefinition getCompositionCohort(String operator, CohortDefinition... definitions) {
		if (definitions.length == 1) {
			return definitions[0];
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		int i = 1;
		for (CohortDefinition cd : definitions) {
			if (cd != null) {
				d.addSearch(""+i, cd, null);
				if (s.length() > 0) {
					s.append(" " + operator + " ");
				}
				s.append(i++);
			}
		}
		d.setCompositionString(s.toString());
		return d;
	}
	
	public static CohortDefinition minus(CohortDefinition base, CohortDefinition... toSubtract) {
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		d.addSearch("base", base, null);
		StringBuilder s = new StringBuilder("base AND NOT (");
		int i = 1;
		for (CohortDefinition cd : toSubtract) {
			d.addSearch(""+i, cd, null);
			if (i > 1) {
				s.append(" OR ");
			}
			s.append(i++);
		}
		s.append(")");
		d.setCompositionString(s.toString());
		return d;
	}
	
	/**
	 * @return a new EvaluationContext with the passed parameters and a number of useful additional parameters
	 */
	public static EvaluationContext constructContext(Map<String, Object> parameters) {
		EvaluationContext context = new EvaluationContext();
		
		if (parameters != null) {
			for (Map.Entry<String, Object> e : parameters.entrySet()) {
				context.addParameterValue(e.getKey(), e.getValue());
				// For any metadata parameters, add the metadata name so the renderer has access to it
				if (e.getValue() != null && e.getValue() instanceof OpenmrsMetadata) {
					context.addParameterValue(e.getKey() + "Name", ((OpenmrsMetadata)e.getValue()).getName());
				}
			}
		}
		
		context.addParameterValue("generationDate", new Date());
		context.addParameterValue("generatedBy", Context.getAuthenticatedUser().getPersonName().getFullName());
		return context;
	}
	
	/**
	 * Looks up a resource on the class path, and returns a RenderingMode based on it
	 * @throws UnsupportedEncodingException 
	 */
	public static RenderingMode renderingModeFromResource(String label, String resourceName) {
		InputStreamReader reader;
		
        try {
	        reader = new InputStreamReader(OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName), "UTF-8");    
        }
        catch (UnsupportedEncodingException e) {
	        throw new MdrtbAPIException("Error reading template from stream", e);
        }
        
		if (reader != null) {
			final ReportDesign design = new ReportDesign();
			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("template");
			String extension = resourceName.substring(resourceName.lastIndexOf("."));
			resource.setExtension(extension);
			String contentType = "text/plain";
			for (ContentType type : ContentType.values()) {
				if (type.getExtension().equals(extension)) {
					contentType = type.getContentType();
				}
			}
			resource.setContentType(contentType);
			ReportRenderer renderer = null;
			try {
				resource.setContents(IOUtils.toByteArray(reader, "UTF-8"));
			}
			catch (Exception e) {
				throw new RuntimeException("Error reading template from stream", e);
			}
			
			design.getResources().add(resource);
			if ("xls".equals(extension)) {
				renderer = new ExcelTemplateRenderer() {
					public ReportDesign getDesign(String argument) {
						return design;
					}
				};
			}
			else {
				renderer = new TextTemplateRenderer() {
					public ReportDesign getDesign(String argument) {
						return design;
					}
				};
			}
			return new RenderingMode(renderer, label, extension, null);
		}
		return null;
	}
	
	public static Map<String, Date> getPeriodDates(Integer year, String quarter, String month) {
		
		if(quarter!=null && quarter.length()==0)
				quarter = null;
		
		if(month!=null && month.length()==0)
			month = null;
		
		System.out.println("YEAR:" + year);
		System.out.println("QTR:" + quarter);
		System.out.println("MONTH:" + month);
		
		// Validate input and construct start and end months
		int startMonth =1;
		int endMonth = 12;
		
		int startDate = 0;
		int endDate = 0;
		
		int startYear = year.intValue();
		
		
		// if the year is null, we don't have start and end dates, want to query from the beginning of time until today
		if (year == null && month == null && quarter == null) {
			Map<String, Date> periodDates = new HashMap<String, Date>();;
			periodDates.put("startDate", null);
			periodDates.put("endDate", new Date());
			
			return periodDates;
		}
		
		if (year == null || year < 1900 || year > 2100) {
			throw new IllegalArgumentException("Please enter a valid year");
		}
		
		if (quarter==null && month==null) {
			startDate = 26;
			endDate = 25;
			
				startMonth = 12;
				endMonth = 12;
				startYear = year.intValue()-1;
			
		}
		
		else 
		{
			if (quarter != null) {
		
			
				if (month != null) {
					throw new IllegalArgumentException("Please enter either a quarter or a month");
				}
			
				String[] quarters = quarter.split("-");
				if(quarters==null || quarters.length==0 || quarters.length>2) {
					throw new IllegalArgumentException("Please enter either a single quarter (e.g. 3 or a range (e.g. 2-4)");
				}
				int startQuarter = 0;
				int endQuarter = 0;
			
				try {
					startQuarter = Integer.parseInt(quarters[0]);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					throw new IllegalArgumentException("Please enter either a single quarter (e.g. 3 or a range (e.g. 2-4)");
				}
				endQuarter = startQuarter;
			
				if(quarters.length==2) {
					endQuarter= Integer.parseInt(quarters[1]);
				}
			
				if(startQuarter > endQuarter) {
					throw new IllegalArgumentException("Start quarter must be less than end quarter");
				}
			
				/*endMonth = quarter*3;
				startMonth = endMonth-2;*/
				startDate = 26;
				endDate = 25;
				if(startQuarter==1) {
					startMonth = 12;
					endMonth = 3;
					startYear = year.intValue()-1;
				}
			
				else if(startQuarter==2) {
					startMonth = 3;
					endMonth = 6;
				}

				else if(startQuarter==3) {
					startMonth = 6;
					endMonth = 9;
				}

				else if(startQuarter==4) {
					startMonth = 9;
					endMonth = 12;
				}
			
				if(startQuarter!=endQuarter) {
					
					if(endQuarter==2) {
						endMonth = 6;
					}
				
					else if(endQuarter==3) {
						endMonth = 9;
					}
				
					else if(endQuarter==4) {
						endMonth = 12;
					}
			
				}
			}
			
		if (month != null) {
			
			String[] months = month.split("-");
			if(months==null || months.length==0 || months.length>2) {
				throw new IllegalArgumentException("Please enter either a single month (e.g. 3 or a range (e.g. 2-7)");
			}
			try {
				startMonth = Integer.parseInt(months[0]);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				throw new IllegalArgumentException("Please enter either a single quarter (e.g. 3 or a range (e.g. 2-4)");
			}
			endMonth = startMonth;
			
			if(months.length==2) {
				endMonth= Integer.parseInt(months[1]);
			}
			
			
			
			if(startMonth==0 || startMonth>12) {
				throw new IllegalArgumentException("Start of month range must be between 1 and 12");
			}
			
			if(endMonth==0 || endMonth>12) {
				throw new IllegalArgumentException("End of month range must be between 1 and 12");
			}
			
			if(startMonth > endMonth) {
				throw new IllegalArgumentException("Start month must be less than end month");
			}
		}
		}
		Map<String, Date> periodDates = new HashMap<String, Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, startYear);
		c.set(Calendar.MONTH, startMonth-1);
		if(startDate!=0)
			c.set(Calendar.DATE, startDate);
		else
			c.set(Calendar.DATE, 1);
		periodDates.put("startDate", c.getTime());
		
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, endMonth-1);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		if(endDate!=0)
			c.set(Calendar.DATE, endDate);
		else
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		periodDates.put("endDate", c.getTime());
		
		return periodDates;
	}
	
	/*public static Map<String, Date> getPeriodDates(Integer year, Integer quarter, Integer month) {
		
		// Validate input and construct start and end months
		int startMonth =1;
		int endMonth = 12;
		
		int startDate = 0;
		int endDate = 0;
		
		int startYear = year.intValue();
		
		
		// if the year is null, we don't have start and end dates, want to query from the beginning of time until today
		if (year == null && month == null && quarter == null) {
			Map<String, Date> periodDates = new HashMap<String, Date>();;
			periodDates.put("startDate", null);
			periodDates.put("endDate", new Date());
			
			return periodDates;
		}
		
		if (year == null || year < 1900 || year > 2100) {
			throw new IllegalArgumentException("Please enter a valid year");
		}
		
		if (quarter==null && month==null) {
			startDate = 26;
			endDate = 25;
			
				startMonth = 12;
				endMonth = 12;
				startYear = year.intValue()-1;
			
		}
		
		else 
		{
			if (quarter != null) {
		
			if (quarter < 1 || quarter > 4) {
				throw new IllegalArgumentException("Please enter a valid quarter (1-4)");
			}
			if (month != null) {
				throw new IllegalArgumentException("Please enter either a quarter or a month");
			}
			endMonth = quarter*3;
			startMonth = endMonth-2;
			startDate = 26;
			endDate = 25;
			if(quarter.intValue()==1) {
				startMonth = 12;
				endMonth = 3;
				startYear = year.intValue()-1;
			}
			
			else if(quarter.intValue()==2) {
				startMonth = 3;
				endMonth = 6;
			}

			else if(quarter.intValue()==3) {
				startMonth = 6;
				endMonth = 9;
			}

			else if(quarter.intValue()==4) {
				startMonth = 9;
				endMonth = 12;
			}
		}
		if (month != null) {
			if (month < 1 || month > 12) {
				throw new IllegalArgumentException("Please enter a valid month (1-12)");
			}
			startMonth = month;
			endMonth = month;
		}
		}
		Map<String, Date> periodDates = new HashMap<String, Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, startYear);
		c.set(Calendar.MONTH, startMonth-1);
		if(startDate!=0)
			c.set(Calendar.DATE, startDate);
		else
			c.set(Calendar.DATE, 1);
		periodDates.put("startDate", c.getTime());
		
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, endMonth-1);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		if(endDate!=0)
			c.set(Calendar.DATE, endDate);
		else
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		periodDates.put("endDate", c.getTime());
		
		return periodDates;
	}*/
	
	public static Map<String, Date> getPeriodDatesOld(Integer year, Integer quarter, Integer month) {
		
		// Validate input and construct start and end months
		int startMonth = 1;
		int endMonth = 12;
		
		
		// if the year is null, we don't have start and end dates, want to query from the beginning of time until today
		if (year == null && month == null && quarter == null) {
			Map<String, Date> periodDates = new HashMap<String, Date>();;
			periodDates.put("startDate", null);
			periodDates.put("endDate", new Date());
			
			return periodDates;
		}
		
		if (year == null || year < 1900 || year > 2100) {
			throw new IllegalArgumentException("Please enter a valid year");
		}
		
		if (quarter != null) {
			if (quarter < 1 || quarter > 4) {
				throw new IllegalArgumentException("Please enter a valid quarter (1-4)");
			}
			if (month != null) {
				throw new IllegalArgumentException("Please enter either a quarter or a month");
			}
			endMonth = quarter*3;
			startMonth = endMonth-2;
		}
		if (month != null) {
			if (month < 1 || month > 12) {
				throw new IllegalArgumentException("Please enter a valid month (1-12)");
			}
			startMonth = month;
			endMonth = month;
		}
		
		Map<String, Date> periodDates = new HashMap<String, Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, startMonth-1);
		c.set(Calendar.DATE, 1);
		periodDates.put("startDate", c.getTime());
		c.set(Calendar.MONTH, endMonth-1);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		periodDates.put("endDate", c.getTime());
		
		return periodDates;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getDotsOutcomesFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(TbService.class).getConcept(TbConcepts.TB_TX_OUTCOME);
		
		CohortDefinition cured =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.CURED), startDate, endDate);
		
		CohortDefinition complete =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_COMPLETE), startDate, endDate);
		
		CohortDefinition failed =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.FAILED), startDate, endDate);
	
		/*CohortDefinition defaulted =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULTED), startDate, endDate);*/
		
		CohortDefinition ltfu =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.LOST_TO_FOLLOWUP), startDate, endDate);
		
		CohortDefinition startedSLD =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.STARTED_SLD_TX), startDate, endDate);
		
		CohortDefinition died =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.DIED), startDate, endDate);
		
		CohortDefinition transferred =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_TRANSFERRED_OUT), startDate, endDate);
		
		CohortDefinition canceled =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.CANCELLED), startDate, endDate);
		
		CohortDefinition stillEnrolled =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		map.put("Cured", cured);
		map.put("TreatmentCompleted", complete);
		map.put("Died", died);
		map.put("Failed", failed);
		
		map.put("Defaulted", ltfu);
		map.put("TransferredOut", transferred);
		map.put("Canceled", canceled);
		map.put("StartedSLD", startedSLD);
		//map.put("StillEnrolled", stillEnrolled);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbOutcomesFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(TbService.class).getConcept(TbConcepts.MDR_TB_TX_OUTCOME);
		
		CohortDefinition cured =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.CURED), startDate, endDate);
		
		CohortDefinition complete =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_COMPLETE), startDate, endDate);
		
		CohortDefinition failed =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.FAILED), startDate, endDate);
	
		/*CohortDefinition defaulted =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DEFAULTED), startDate, endDate);*/
		
		CohortDefinition ltfu =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.LOST_TO_FOLLOWUP), startDate, endDate);
		
		CohortDefinition died =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.DIED), startDate, endDate);
		
		CohortDefinition transferred =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_TRANSFERRED_OUT), startDate, endDate);
		
		CohortDefinition stillEnrolled =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		map.put("Cured", cured);
		map.put("TreatmentCompleted", complete);
		map.put("Failed", failed);
		map.put("Defaulted", ltfu);
		map.put("Died", died);
		map.put("TransferredOut", transferred);
		map.put("StillEnrolled", stillEnrolled);
		
		return map;
	}
	/*@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbPreviousDrugUseFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();

		Concept workflowConcept = Context.getService(TbService.class).getConcept(TbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_DRUG_USE);
		
		CohortDefinition newPatient = Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.NEW), startDate, endDate);
		
		CohortDefinition previousFirstLine = Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.PREVIOUSLY_TREATED_FIRST_LINE_DRUGS_ONLY), startDate, endDate);
		
		CohortDefinition previousSecondLine = Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.PREVIOUSLY_TREATED_SECOND_LINE_DRUGS), startDate, endDate);
		
		CohortDefinition unknown = Cohorts.getDotsPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
		
		map.put("New", newPatient);
		map.put("PreviousFirstLine", previousFirstLine);
		map.put("PreviousSecondLine", previousSecondLine);
		map.put("Unknown", unknown);
		
		return map;
	}*/
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getDotsRegistrationGroupsFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(TbService.class).getConcept(TbConcepts.PATIENT_GROUP);
		
		CohortDefinition newPatient =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.NEW), startDate, endDate);
		
		CohortDefinition relapse1 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE_AFTER_REGIMEN_1), startDate, endDate);
		
		CohortDefinition relapse2 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE_AFTER_REGIMEN_2), startDate, endDate);
		
		CohortDefinition afterDefault1 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULT_AFTER_REGIMEN_1), startDate, endDate);
		
		CohortDefinition afterDefault2 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULT_AFTER_REGIMEN_2), startDate, endDate);
	
		CohortDefinition treatmentAfterFailure1 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.AFTER_FAILURE_REGIMEN_1), startDate, endDate);
		
		CohortDefinition treatmentAfterFailure2 =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.AFTER_FAILURE_REGIMEN_2), startDate, endDate);
		
		/*CohortDefinition failureCatII =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT), startDate, endDate);*/
		
		CohortDefinition transferred =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER), startDate, endDate);
		
		CohortDefinition other =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.OTHER), startDate, endDate);
		
		
		
		//List<Concept> others = new ArrayList<Concept>();
		//others.add(Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER));
		//others.add(Context.getService(TbService.class).getConcept(TbConcepts.OTHER));
		//others.add(Context.getService(TbService.class).getConcept(TbConcepts.UNKNOWN));
		//others.add();
		
		
		//CohortDefinition otherStar =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, others, startDate, endDate);
		//CohortDefinition unknown =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		
		
		map.put("New", newPatient);
		map.put("Relapse1", relapse1);
		map.put("Relapse2", relapse2);
		map.put("AfterDefault1", afterDefault1);
		map.put("AfterDefault2", afterDefault2);
		//map.put("AfterFailure", treatmentAfterFailure);
		map.put("AfterFailure1", treatmentAfterFailure1);
		map.put("AfterFailure2", treatmentAfterFailure2);
		//map.put("AfterFailureCategoryII", failureCatII);
		map.put("TransferredIn", transferred);
		map.put("Other", other);
		
		//map.put("Unknown", unknown);
		
		//map.put("Other", otherStar);
		
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,CohortDefinition> getMdrtbPreviousTreatmentFilterSet(Date startDate, Date endDate) {
		Map<String,CohortDefinition> map = new HashMap<String,CohortDefinition>();
		
		Concept workflowConcept = Context.getService(TbService.class).getConcept(TbConcepts.CAT_4_CLASSIFICATION_PREVIOUS_TX);
		
		/*CohortDefinition newPatient =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.NEW), startDate, endDate);
		
		CohortDefinition relapse =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE_AFTER_REGIMEN_1), startDate, endDate);
		
		CohortDefinition afterDefault =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULTED), startDate, endDate);
	
		CohortDefinition failureCatI =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.AFTER_FAILURE_REGIMEN_1), startDate, endDate);
		
		CohortDefinition failureCatII =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT), startDate, endDate);
		
		CohortDefinition transferred =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
			 Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER), startDate, endDate);
		
		CohortDefinition other =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.OTHER), startDate, endDate);
		
		CohortDefinition other =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.OTHER), startDate, endDate);
		
		List<Concept> others = new ArrayList<Concept>();
		others.add(Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER));
		others.add(Context.getService(TbService.class).getConcept(TbConcepts.OTHER));
		others.add(Context.getService(TbService.class).getConcept(TbConcepts.UNKNOWN));
		//others.add();
		
		
		//CohortDefinition otherStar =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, others, startDate, endDate);
		//CohortDefinition unknown =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
	
		
		
		map.put("New", newPatient);
		map.put("Relapse", relapse);
		map.put("AfterDefault", afterDefault);
		map.put("AfterFailureCategoryI", failureCatI);
		map.put("AfterFailureCategoryII", failureCatII);
		map.put("TransferredIn", transferred);
		map.put("Other", other);
		//map.put("Unknown", unknown);
		map.put("AfterFailure", ReportUtil.getCompositionCohort("OR",failureCatI,failureCatII) );
		//map.put("Other", otherStar);
*/		
		CohortDefinition newPatient =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.NEW), startDate, endDate);
			
			CohortDefinition relapse1 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE_AFTER_REGIMEN_1), startDate, endDate);
			
			CohortDefinition relapse2 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
					 Context.getService(TbService.class).getConcept(TbConcepts.RELAPSE_AFTER_REGIMEN_2), startDate, endDate);
			
			CohortDefinition afterDefault1 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULT_AFTER_REGIMEN_1), startDate, endDate);
			
			CohortDefinition afterDefault2 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
					 Context.getService(TbService.class).getConcept(TbConcepts.DEFAULT_AFTER_REGIMEN_2), startDate, endDate);
		
			CohortDefinition treatmentAfterFailure1 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.AFTER_FAILURE_REGIMEN_1), startDate, endDate);
			
			CohortDefinition treatmentAfterFailure2 =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
					 Context.getService(TbService.class).getConcept(TbConcepts.AFTER_FAILURE_REGIMEN_2), startDate, endDate);
			
			/*CohortDefinition failureCatII =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.TREATMENT_AFTER_FAILURE_OF_FIRST_RETREATMENT), startDate, endDate);*/
			
			CohortDefinition transferred =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER), startDate, endDate);
			
			CohortDefinition other =  Cohorts.getMdrtbPatientProgramStateFilter(workflowConcept, 
				 Context.getService(TbService.class).getConcept(TbConcepts.OTHER), startDate, endDate);
			
			
			
			//List<Concept> others = new ArrayList<Concept>();
			//others.add(Context.getService(TbService.class).getConcept(TbConcepts.TRANSFER));
			//others.add(Context.getService(TbService.class).getConcept(TbConcepts.OTHER));
			//others.add(Context.getService(TbService.class).getConcept(TbConcepts.UNKNOWN));
			//others.add();
			
			
			//CohortDefinition otherStar =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, others, startDate, endDate);
			//CohortDefinition unknown =  Cohorts.getDotsPatientProgramStateFilter(workflowConcept, new ArrayList(), startDate, endDate);
		
			
			
			map.put("New", newPatient);
			map.put("Relapse1", relapse1);
			map.put("Relapse2", relapse2);
			map.put("AfterDefault1", afterDefault1);
			map.put("AfterDefault2", afterDefault2);
			//map.put("AfterFailure", treatmentAfterFailure);
			map.put("AfterFailure1", treatmentAfterFailure1);
			map.put("AfterFailure2", treatmentAfterFailure2);
			//map.put("AfterFailureCategoryII", failureCatII);
			map.put("TransferredIn", transferred);
			map.put("Other", other);
		return map;
	}
	
	/**
	 * Returns a map of patients ids to the list of active mdrtb patient programs for that patient during the given date range
	 */
	public static Map<Integer,List<TbPatientProgram>> getTbPatientProgramsInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = new HashMap<Integer,List<TbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (TbPatientProgram program : Context.getService(TbService.class).getAllTbPatientProgramsInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!tbPatientProgramsMap.containsKey(patientId)) {
				tbPatientProgramsMap.put(patientId, new ArrayList<TbPatientProgram>());
			}
			
			// add the program
			tbPatientProgramsMap.get(patientId).add(program);
		}
		
		return tbPatientProgramsMap;
	}
	
	public static Map<Integer,List<TbPatientProgram>> getTbPatientProgramsEnrolledInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = new HashMap<Integer,List<TbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (TbPatientProgram program : Context.getService(TbService.class).getAllTbPatientProgramsEnrolledInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!tbPatientProgramsMap.containsKey(patientId)) {
				tbPatientProgramsMap.put(patientId, new ArrayList<TbPatientProgram>());
			}
			
			// add the program
			tbPatientProgramsMap.get(patientId).add(program);
		}
		
		return tbPatientProgramsMap;
	}
	
	public static Map<Integer,List<TbPatientProgram>> getMdrTbPatientProgramsEnrolledInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = new HashMap<Integer,List<TbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (TbPatientProgram program : Context.getService(TbService.class).getAllMdrtbPatientProgramsEnrolledInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!tbPatientProgramsMap.containsKey(patientId)) {
				tbPatientProgramsMap.put(patientId, new ArrayList<TbPatientProgram>());
			}
			
			// add the program
			tbPatientProgramsMap.get(patientId).add(program);
		}
		
		return tbPatientProgramsMap;
	}
	
	/**
	 * Returns a map of patients ids to the list of active mdrtb patient programs for that patient during the given date range
	 */
	public static Map<Integer,List<TbPatientProgram>> getMdrtbPatientProgramsInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<TbPatientProgram>> mdrtbPatientProgramsMap = new HashMap<Integer,List<TbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (TbPatientProgram program : Context.getService(TbService.class).getAllMdrtbPatientProgramsInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!mdrtbPatientProgramsMap.containsKey(patientId)) {
				mdrtbPatientProgramsMap.put(patientId, new ArrayList<TbPatientProgram>());
			}
			
			// add the program
			mdrtbPatientProgramsMap.get(patientId).add(program);
		}
		
		return mdrtbPatientProgramsMap;
	}
	
	public static Map<Integer,List<TbPatientProgram>> getMdrtbPatientProgramsEnrolledInDateRangeMap(Date startDate, Date endDate) {
		
		Map<Integer,List<TbPatientProgram>> tbPatientProgramsMap = new HashMap<Integer,List<TbPatientProgram>>();
		
		// get all the mdrtb patient programs
		for (TbPatientProgram program : Context.getService(TbService.class).getAllMdrtbPatientProgramsEnrolledInDateRange(startDate, endDate)) {
			Integer patientId = program.getPatient().getId();
			
			// create a new entry for this patient if we don't already have it
			if (!tbPatientProgramsMap.containsKey(patientId)) {
				tbPatientProgramsMap.put(patientId, new ArrayList<TbPatientProgram>());
			}
			
			// add the program
			tbPatientProgramsMap.get(patientId).add(program);
		}
		
		return tbPatientProgramsMap;
	}
	
	public static DateObsCohortDefinition getDateObsCohort(TimeModifier tm, Concept question, 
			  Date fromDate, Date toDate) {
		
		
		DateObsCohortDefinition def = new DateObsCohortDefinition();
		def.setQuestion(question);
		def.setTimeModifier(tm);
		/*def.setOnOrAfter(fromDate);
		def.setOnOrBefore(toDate);*/
		
		
		
		if(fromDate !=null) {
			def.setOperator1(RangeComparator.GREATER_EQUAL);
			def.setValue1(fromDate);
		}
			
		
		
		
		if(toDate != null) {
			def.setOperator2(RangeComparator.LESS_EQUAL);
			def.setValue2(toDate);
		}
			
		
		return def;
		
	}
	
}