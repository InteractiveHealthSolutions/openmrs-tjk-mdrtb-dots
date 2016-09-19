package org.openmrs.module.labmodule.web.controller.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor.AQUA;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.reporting.Oblast;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LabPatientListController extends AbstractListController{
    
    @RequestMapping("/module/labmodule/reporting/labListPatients")
    public void showPatientList(
            		@RequestParam(value="oblast", required=false) String oblast,
            		@RequestParam(value="district", required=false) Location district,
            		@RequestParam(value="age_from", required=false) Integer ageFrom,
            		@RequestParam(value="age_to", required=false) Integer ageTo,
            		@RequestParam(value="gender", required=false) String gender,
            		@RequestParam(value="lab", required=false) Location lab,
            		@RequestParam(value="date_from", required=false) Date dateFrom,
            		@RequestParam(value="date_to", required=false) Date dateTo,
            		@RequestParam(value="requestingLabName", required=false) Concept requestingLabName,
            		@RequestParam(value="investigationPurpose", required=false) Concept investigationPurpose,
            		@RequestParam(value="biologicalSpecimen", required=false) Concept biologicalSpecimen,
            		@RequestParam(value="peripheralLabNo", required=false) String peripheralLabNo,
            		@RequestParam(value="microscopyResult", required=false) Concept microscopyResult,
            		@RequestParam(value="result_from", required=false) Date resultDateFrom,
            		@RequestParam(value="result_to", required=false) Date resultDateTo,
            		@RequestParam(value="peripheral", required=false) String[] peripheral,
            		@RequestParam(value="microscopy", required=false) String[] microscopy,
            		@RequestParam(value="xpert", required=false) String[] xpert,
            		@RequestParam(value="hain", required=false) String[] hain,
            		@RequestParam(value="hain2", required=false) String[] hain2,
            		@RequestParam(value="culture", required=false) String[] culture,
            		ModelMap model) {
    	
    	if(oblast != null){
	    	Oblast o = null;
			if(!oblast.equals("") && district == null)
				o =  Context.getService(TbService.class).getOblast(Integer.parseInt(oblast));
			
			List<Location> locList = new ArrayList<Location>();
			if(o != null && district == null)
				locList = Context.getService(TbService.class).getLocationsFromOblastName(o);
			else if (district != null)
				locList.add(district);
			
	        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	        
	        if(locList.isEmpty()){
		        List<LabResult> labResults = Context.getService(TbService.class).getLabResults(null, null, null, null);
		        				        
		        for(LabResult lr : labResults){
		        	
		        	Map<String, Object> mMap = new HashMap<String, Object>();
		        	
	        		int age = lr.getPatient().getAge();
	        			        		
	        	    if(ageFrom != null && ageFrom > age)	
	        			continue;
	        	    
	        	    if(ageTo != null && ageTo < age)
		        		continue;
		        	
	        	    if(!gender.equals("") && !lr.getPatient().getGender().equals(gender))
	        	    	continue;
	        	    
	        	    if(lab != null && lr.getLocation() != lab)
	        	    	continue;
	        	    
	        	    if(dateFrom != null &&  dateFrom.after(lr.getDateCollected()))
	        	    	continue;
	        	    
	        	    if(dateTo != null &&  dateTo.before(lr.getDateCollected()))
	        	    	continue;
	        	    
	        	    if(requestingLabName != null && requestingLabName != lr.getRequestingLabName())
	        	    	continue;
	        	    
	        	    if(investigationPurpose != null && investigationPurpose != lr.getInvestigationPurpose())
	        	    	continue;
	        	    
	        	    if(biologicalSpecimen != null && biologicalSpecimen != lr.getBiologicalSpecimen())
	        	    	continue;
	        	    
	        	    if(!peripheralLabNo.equals("") && !peripheralLabNo.equals(lr.getPeripheralLabNumber()))
	        	    	continue;
	        	    
	        	    if(microscopyResult != null && microscopyResult != lr.getMicroscopyResult())
	        	    	continue;
	        	    
	        	    if(resultDateFrom != null &&  resultDateFrom.after(lr.getDateResult()))
	        	    	continue;
	        	    
	        	    if(resultDateTo != null &&  resultDateTo.before(lr.getDateResult()))
	        	    	continue;
	        	    
	        	    List<Culture> cultures = lr.getCultures();
	        	    if(culture != null && cultures.size() == 0)
	        	    	continue;
	        	    else{
	        	    	
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	/*for(int j = 0; i < cultures.size(); i++){
	        	    		
	        	    		if(date != null && date.iscultures.get(j).getResultDate())
	        	    		
	        	    	}*/
	        	    		
	        	    	
	        	    }
		        	
		        	mMap.put("patientId", lr.getPatient().getId());
		        	mMap.put("encounterId", lr.getId());
		        	
		            mMap.put("name", lr.getPatient().getPersonName());
		            mMap.put("collectedBy", lr.getProvider().getPersonName());
		            mMap.put("location", lr.getLocation().getName());
		            mMap.put("testId", lr.getLabNumber());
		            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		            mMap.put("dateRecieve", lr.getDateCollected() == null ? "" : df.format(lr.getDateCollected()));
		            mMap.put("requestingMedicalFacility", lr.getRequestingLabName() == null ? "" : lr.getRequestingLabName().getName());
		            mMap.put("investigationPurpose", lr.getInvestigationPurpose() == null ? "" : lr.getInvestigationPurpose().getName());
		            mMap.put("biologicalSpecimen", lr.getBiologicalSpecimen() == null ? "" : lr.getBiologicalSpecimen().getName());
		            
		            mMap.put("peripheralLabNo", lr.getPeripheralLabNumber() == null ? "" : lr.getPeripheralLabNumber());
		            mMap.put("microscopyResult", lr.getMicroscopyResult() == null ? "" : lr.getMicroscopyResult().getName());	
		            mMap.put("dateResult", lr.getDateResult() == null ? "" : df.format(lr.getDateResult()));
		            
		            
		            
		            data.add(mMap);
		        }
	        }
	        else{
	        	for(Location loc : locList){
	        		
	        		List<LabResult> labResults = Context.getService(TbService.class).getLabResults(null, null, null, loc);
			        	        		
			        for(LabResult lr : labResults){
			        	
			        	Map<String, Object> mMap = new HashMap<String, Object>();
			        	
		        		int age = lr.getPatient().getAge();
		        			        		
		        	    if(ageFrom != null && ageFrom > age)	
		        			continue;
		        	    
		        	    if(ageTo != null && ageTo < age)
			        		continue;
			        	
		        	    if(!gender.equals("") && !lr.getPatient().getGender().equals(gender))
		        	    	continue;
		        	    
		        	    if(lab != null && lr.getLocation() != lab)
		        	    	continue;
		        	    
		        	    if(dateFrom != null &&  (dateFrom.after(lr.getDateCollected()) || dateFrom.equals(lr.getDateCollected())))
		        	    	continue;
		        	    
		        	    if(dateTo != null && ( dateTo.before(lr.getDateCollected()) || dateTo.equals(lr.getDateCollected())))
		        	    	continue;
		        	    
		        	    if(requestingLabName != null && requestingLabName != lr.getRequestingLabName())
		        	    	continue;
		        	    
		        	    if(investigationPurpose != null && investigationPurpose != lr.getInvestigationPurpose())
		        	    	continue;
		        	    
		        	    if(biologicalSpecimen != null && biologicalSpecimen != lr.getBiologicalSpecimen())
		        	    	continue;
		        	    
		        	    if(!peripheralLabNo.equals("") && !peripheralLabNo.equals(lr.getPeripheralLabNumber()))
		        	    	continue;
		        	    
		        	    if(microscopyResult != null && microscopyResult != lr.getMicroscopyResult())
		        	    	continue;
		        	    
		        	    if(resultDateFrom != null &&  resultDateFrom.after(lr.getDateResult()))
		        	    	continue;
		        	    
		        	    if(resultDateTo != null &&  resultDateTo.before(lr.getDateResult()))
		        	    	continue;
			        	
			        	mMap.put("patientId", lr.getPatient().getId());
			        	mMap.put("encounterId", lr.getId());
			            
			        	mMap.put("name", lr.getPatient().getPersonName());
			            mMap.put("collectedBy", lr.getProvider().getPersonName());
			            mMap.put("location", lr.getLocation().getName());
			            mMap.put("testId", lr.getLabNumber());
			            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			            mMap.put("dateRecieve", lr.getDateCollected() == null ? "" : df.format(lr.getDateCollected()));
			            mMap.put("requestingMedicalFacility", lr.getRequestingLabName() == null ? "" : lr.getRequestingLabName().getName());
			            mMap.put("investigationPurpose", lr.getInvestigationPurpose() == null ? "" : lr.getInvestigationPurpose().getName());
			            mMap.put("biologicalSpecimen", lr.getBiologicalSpecimen() == null ? "" : lr.getBiologicalSpecimen().getName());
			            
			            mMap.put("peripheralLabNo", lr.getPeripheralLabNumber() == null ? "" : lr.getPeripheralLabNumber());
			            mMap.put("microscopyResult", lr.getMicroscopyResult() == null ? "" : lr.getMicroscopyResult().getName());	
			            mMap.put("dateResult", lr.getDateResult() == null ? "" : df.format(lr.getDateResult()));
			            
			            data.add(mMap);
			        }
	        		
	        	}
	        	
	        }
	        
	        if(peripheral != null)
	        	model.addAttribute("peripheral", "yes");
	        if(microscopy != null)
	        	model.addAttribute("microscopy", "yes");
	        if(xpert != null)
	        	model.addAttribute("xpert", "yes");
	        if(hain != null)
	        	model.addAttribute("hain", "yes");
	        if(hain2 != null)
	        	model.addAttribute("hain2", "yes");
	        if(culture != null)
	        	model.addAttribute("culture", "yes");
	        
	        model.addAttribute("data", data);
        }
    	
    }
}
