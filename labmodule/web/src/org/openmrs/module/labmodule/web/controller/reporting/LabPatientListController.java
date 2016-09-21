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
import org.hibernate.dialect.IngresDialect;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.Xpert;
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
            		@RequestParam(value="microscopy_from", required=false) Date microscopyDateFrom,
            		@RequestParam(value="microscopy_to", required=false) Date microscopyDateTo,
            		@RequestParam(value="sampleAppearance", required=false) Concept sampleAppearance,
            		@RequestParam(value="sampleResult", required=false) Concept sampleResult,
            		@RequestParam(value="xpert", required=false) String[] xpert,
            		@RequestParam(value="xpert_from", required=false) Date xpertDateFrom,
            		@RequestParam(value="xpert_to", required=false) Date xpertDateTo,
            		@RequestParam(value="xpertError", required=false) String xpertError,
            		@RequestParam(value="mtbXpertResult", required=false) Concept mtbXpertResult,
            		@RequestParam(value="rifXpertResult", required=false) Concept rifXpertResult,
            		@RequestParam(value="hain", required=false) String[] hain,
            		@RequestParam(value="hain_from", required=false) Date hainDateFrom,
            		@RequestParam(value="hain_to", required=false) Date hainDateTo,
            		@RequestParam(value="mtbHainResult", required=false) Concept mtbHainResult,
            		@RequestParam(value="rifHainResult", required=false) Concept rifHainResult,
            		@RequestParam(value="inhHainResult", required=false) Concept inhHainResult,
            		@RequestParam(value="hain2", required=false) String[] hain2,
            		@RequestParam(value="hain2_from", required=false) Date hain2DateFrom,
            		@RequestParam(value="hain2_to", required=false) Date hain2DateTo,
            		@RequestParam(value="mtbHain2Result", required=false) Concept mtbHain2Result,
            		@RequestParam(value="moxHain2Result", required=false) Concept moxHain2Result,
            		@RequestParam(value="cmHain2Result", required=false) Concept cmHain2Result,
            		@RequestParam(value="erHain2Result", required=false) Concept erHain2Result,
            		@RequestParam(value="culture", required=false) String[] culture,
            		@RequestParam(value="culture_from", required=false) Date cultureDateFrom,
            		@RequestParam(value="culture_to", required=false) Date cultureDateTo,
            		@RequestParam(value="cultureResult", required=false) Concept cultureResult,
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
	        	    
	        	    Culture c = null;
	        	    List<Culture> cultures = lr.getCultures();
	        	    if(cultures.size() != 0){
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < cultures.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = cultures.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(cultures.get(j).getResultDate())){
	        	    			date = cultures.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	c = cultures.get(i);
	        	    }
	        	    
	        	    HAIN2 h2 = null;
	        	    List<HAIN2> hains2 = lr.getHAINS2();
	        	    if(hains2.size() != 0){
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < hains2.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = hains2.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(hains2.get(j).getResultDate())){
	        	    			date = hains2.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	h2 = hains2.get(i);
	        	    }
	        	    
	        	    HAIN h = null;
	        	    List<HAIN> hains = lr.getHAINS();
	        	    if(hains.size() != 0){
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < hains.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = hains.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(hains.get(j).getResultDate())){
	        	    			date = hains.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	h = hains.get(i);
	        	    }
	        	    
	        	    Xpert x = null;
	        	    List<Xpert> xperts = lr.getXperts();
	        	    if(xperts.size() != 0){
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < xperts.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = xperts.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(xperts.get(j).getResultDate())){
	        	    			date = xperts.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	x = xperts.get(i);
	        	    }
	        	    
	        	    Microscopy m1 = null;
	        	    Microscopy m2 = null;
	        	    Microscopy m3 = null;
	        	    List<Microscopy> microscopies = lr.getMicroscopies();
	        	    if(microscopies.size() != 0){
	        	    	
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < microscopies.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(microscopies.get(j).getResultDate())){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	m1 = microscopies.get(i);
	        	    	microscopies.remove(i);
	        	    }
	        	    
	        	    if(microscopy != null && microscopies.size() != 0){
	        	    	
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < microscopies.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(microscopies.get(j).getResultDate())){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	m2 = microscopies.get(i);
	        	    	microscopies.remove(i);
	        	    }
	        	    
	        	    if(microscopy != null && microscopies.size() != 0){
	        	    	
	        	    	Date date = null;
	        	    	int i = 0;
	        	    	
	        	    	for(int j = 0; j < microscopies.size(); j++){
	        	    		
	        	    		if(date == null){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    		else if(date.before(microscopies.get(j).getResultDate())){
	        	    			date = microscopies.get(j).getResultDate();
	        	    			i = j;
	        	    		}
	        	    	}

	        	    	m3 = microscopies.get(i);
	        	    	microscopies.remove(i);
	        	    }
	        	    
	        	    Boolean microscopy1Flag = false;
	        	    Boolean microscopy2Flag = false;
	        	    Boolean microscopy3Flag = false;
	        	    Boolean hainFlag = false;
	        	    Boolean hain2Flag = false;
	        	    Boolean xpertFlag = false;
	        	    Boolean cultureFlag = false;
	        	    
	        	    if(microscopy != null && m1 != null){
	        	    	microscopy1Flag = true;
	        	    	microscopy2Flag = true;
	        	    	microscopy3Flag = true;
	        	    }
	        	    if(hain != null && h != null)
	        	    	hainFlag = true;
	        	    if(hain2 != null && h2 != null)
	        	    	hain2Flag = true;
	        	    if(xpert != null && x != null)
	        	    	xpertFlag = true;
	        	    if(culture != null && c != null)
	        	    	cultureFlag = true;
	        	   
	        	   if(culture != null && c != null){
		        	    if(cultureDateFrom != null &&  cultureDateFrom.after(c.getResultDate()))
		        	    	cultureFlag = false;
		        	    
		        	    if(cultureDateTo != null &&  cultureDateTo.before(c.getResultDate()))
		        	    	cultureFlag = false;
		        	    
		        	    if(cultureResult != null && cultureResult != c.getResult())
		        	    	cultureFlag = false;
	        	   }
	        	   
	        	   if(hain2 != null && h2 != null){
		        	    if(hain2DateFrom != null &&  hain2DateFrom.after(h2.getResultDate()))
		        	    	hain2Flag = false;
		        	    
		        	    if(hain2DateTo != null &&  hain2DateTo.before(h2.getResultDate()))
		        	    	hain2Flag = false;
		        	    
		        	    if(mtbHain2Result != null && mtbHain2Result != h2.getResult())
		        	    	hain2Flag = false;
		        	    
		        	    if(moxHain2Result != null && moxHain2Result != h2.getMoxResistance())
		        	    	hain2Flag = false;
		        	    
		        	    if(cmHain2Result != null && cmHain2Result != h2.getCmResistance())
		        	    	hain2Flag = false;
		        	    
		        	    if(erHain2Result != null && erHain2Result != h2.getErResistance())
		        	    	hain2Flag = false;
	        	   }
	        	   
	        	   if(hain != null && h != null){
		        	    if(hainDateFrom != null &&  hainDateFrom.after(h.getResultDate()))
		        	    	hainFlag = false;
		        	    
		        	    if(hainDateTo != null &&  hainDateTo.before(h.getResultDate()))
		        	    	hainFlag = false;
		        	    
		        	    if(mtbHainResult != null && mtbHainResult != h.getResult())
		        	    	hainFlag = false;
		        	    
		        	    if(rifHainResult != null && rifHainResult != h.getRifResistance())
		        	    	hainFlag = false;
		        	    
		        	    if(inhHainResult != null && inhHainResult != h.getInhResistance())
		        	    	hainFlag = false;
	        	   }
	        	   
	        	   if(xpert != null && x != null){
		        	    if(xpertDateFrom != null &&  xpertDateFrom.after(x.getResultDate()))
		        	    	xpertFlag = false;
		        	    
		        	    if(xpertDateTo != null &&  xpertDateTo.before(x.getResultDate()))
		        	    	xpertFlag = false;
		        	    
		        	    if(mtbHainResult != null && mtbHainResult != h.getResult())
		        	    	xpertFlag = false;
		        	    
		        	    if(rifXpertResult != null && rifXpertResult != x.getRifResistance())
		        	    	xpertFlag = false;
		        	    
		        	    if(!xpertError.equals("") && !xpertError.equals(x.getErrorCode()))
		        	    	xpertFlag = false;
		        	    
	        	   }
	        	   
	        	   if(microscopy != null && m1 != null){
		        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m1.getResultDate()))
		        	    	microscopy1Flag = false;
		        	    
		        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m1.getResultDate()))
		        	    	microscopy1Flag = false;
		        	    
		        	    if(sampleAppearance != null && sampleAppearance != m1.getSampleApperence())
		        	    	microscopy1Flag = false;
		        	    
		        	    if(sampleResult != null && sampleAppearance != m1.getSampleResult())
		        	    	microscopy1Flag = false;
	        	   }
	        	   
	        	   if(microscopy != null && m2 != null){
		        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m2.getResultDate()))
		        	    	microscopy2Flag = false;
		        	    
		        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m2.getResultDate()))
		        	    	microscopy2Flag = false;
		        	    
		        	    if(sampleAppearance != null && sampleAppearance != m2.getSampleApperence())
		        	    	microscopy2Flag = false;
		        	    
		        	    if(sampleResult != null && sampleAppearance != m2.getSampleResult())
		        	    	microscopy3Flag = false;
	        	   }
	        	   
	        	   if(microscopy != null && m3 != null){
		        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m3.getResultDate()))
		        	    	microscopy3Flag = false;
		        	    
		        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m3.getResultDate()))
		        	    	microscopy3Flag = false;
		        	    
		        	    if(sampleAppearance != null && sampleAppearance != m3.getSampleApperence())
		        	    	microscopy3Flag = false;
		        	    
		        	    if(sampleResult != null && sampleAppearance != m3.getSampleResult())
		        	    	microscopy3Flag = false;
	        	   }
	        	    
	        	   if(!(culture == null && hain == null && hain2 == null && xpert == null && microscopy == null)){
		        	   
		        		  Boolean flag = false;
		        		  if(culture != null && cultureFlag)
		        			  flag = true;
		        		  if(hain2 != null && hain2Flag)
		        			  flag = true;
		        		  if(hain != null && hainFlag)
		        			  flag = true;
		        		  if(xpert != null && xpertFlag)
		        			  flag = true;
		        		  if(microscopy != null && microscopy1Flag)
		        			  flag = true;
		        		  
		        		  if(!flag)
			        		  continue;
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
		            
		            if(c != null){
			            mMap.put("cultureResultDate", c.getResultDate() == null ? "" : df.format(c.getResultDate()));
		        	    mMap.put("cultureResult", c.getResult().getName());
		            }
		            
		            if(h2 != null){
			            mMap.put("hain2ResultDate", h2.getResultDate() == null ? "" : df.format(h2.getResultDate()));
		        	    mMap.put("hain2MtbResult", h2.getResult().getName());
		        	    mMap.put("hain2MoxResult", h2.getMoxResistance() == null ? "" : h2.getMoxResistance().getName());
		        	    mMap.put("hain2CmResult",  h2.getCmResistance() == null ? "" : h2.getCmResistance().getName());
		        	    mMap.put("hain2ErResult",  h2.getErResistance() == null ? "" : h2.getErResistance().getName());
		            }
		            
		            if(h != null){
	        	    	mMap.put("hainResultDate", h.getResultDate() == null ? "" : df.format(h.getResultDate()));
		        	    mMap.put("hainMtbResult", h.getResult().getName());
		        	    mMap.put("hainRifResult", h.getRifResistance() == null ? "" : h.getRifResistance().getName());
		        	    mMap.put("hainInhResult",  h.getInhResistance() == null ? "" : h.getInhResistance().getName());
	        	    }
		            
		            if(x != null){
	        	    	mMap.put("xpertResultDate", x.getResultDate() == null ? "" : df.format(x.getResultDate()));
		        	    mMap.put("xpertMtbResult", x.getResult().getName());
		        	    mMap.put("xpertRifResult", x.getRifResistance() == null ? "" : x.getRifResistance().getName());
		        	    mMap.put("xpertErrorCode",  x.getErrorCode());
	        	    }
		            
		            if(m1 != null){
	        	    	mMap.put("microscopyResultDate_1", m1.getResultDate() == null ? "" : df.format(m1.getResultDate()));
		        	    mMap.put("sampleAppearence_1", m1.getSampleApperence().getName());
		        	    mMap.put("sampleResult_1", m1.getSampleResult().getName());
	        	    }
		            
		            if(m2 != null){
	        	    	mMap.put("microscopyResultDate_2", m2.getResultDate() == null ? "" : df.format(m2.getResultDate()));
		        	    mMap.put("sampleAppearence_2", m2.getSampleApperence().getName());
		        	    mMap.put("sampleResult_2", m2.getSampleResult().getName());
	        	    }
		            
		            if(m3 != null){
	        	    	mMap.put("microscopyResultDate_3", m3.getResultDate() == null ? "" : df.format(m3.getResultDate()));
		        	    mMap.put("sampleAppearence_3", m3.getSampleApperence().getName());
		        	    mMap.put("sampleResult_3",m3.getSampleResult().getName());
	        	    }
		            
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
		        	    
		        	    Culture c = null;
		        	    List<Culture> cultures = lr.getCultures();
		        	    if(cultures.size() != 0){
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < cultures.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = cultures.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(cultures.get(j).getResultDate())){
		        	    			date = cultures.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	c = cultures.get(i);
		        	    }
		        	    
		        	    HAIN2 h2 = null;
		        	    List<HAIN2> hains2 = lr.getHAINS2();
		        	    if(hains2.size() != 0){
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < hains2.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = hains2.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(hains2.get(j).getResultDate())){
		        	    			date = hains2.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	h2 = hains2.get(i);
		        	    }
		        	    
		        	    HAIN h = null;
		        	    List<HAIN> hains = lr.getHAINS();
		        	    if(hains.size() != 0){
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < hains.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = hains.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(hains.get(j).getResultDate())){
		        	    			date = hains.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	h = hains.get(i);
		        	    }
		        	    
		        	    Xpert x = null;
		        	    List<Xpert> xperts = lr.getXperts();
		        	    if(xperts.size() != 0){
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < xperts.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = xperts.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(xperts.get(j).getResultDate())){
		        	    			date = xperts.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	x = xperts.get(i);
		        	    }
		        	    
		        	    Microscopy m1 = null;
		        	    Microscopy m2 = null;
		        	    Microscopy m3 = null;
		        	    List<Microscopy> microscopies = lr.getMicroscopies();
		        	    if(microscopies.size() != 0){
		        	    	
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < microscopies.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(microscopies.get(j).getResultDate())){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	m1 = microscopies.get(i);
		        	    	microscopies.remove(i);
		        	    }
		        	    
		        	    if(microscopies.size() != 0){
		        	    	
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < microscopies.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(microscopies.get(j).getResultDate())){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	m2 = microscopies.get(i);
		        	    	microscopies.remove(i);
		        	    }
		        	    
		        	    if(microscopies.size() != 0){
		        	    	
		        	    	Date date = null;
		        	    	int i = 0;
		        	    	
		        	    	for(int j = 0; j < microscopies.size(); j++){
		        	    		
		        	    		if(date == null){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    		else if(date.before(microscopies.get(j).getResultDate())){
		        	    			date = microscopies.get(j).getResultDate();
		        	    			i = j;
		        	    		}
		        	    	}

		        	    	m3 = microscopies.get(i);
		        	    	microscopies.remove(i);
		        	    }
		        	   
		        	    Boolean microscopy1Flag = false;
		        	    Boolean microscopy2Flag = false;
		        	    Boolean microscopy3Flag = false;
		        	    Boolean hainFlag = false;
		        	    Boolean hain2Flag = false;
		        	    Boolean xpertFlag = false;
		        	    Boolean cultureFlag = false;
		        	    
		        	    if(microscopy != null && m1 != null){
		        	    	microscopy1Flag = true;
		        	    	microscopy2Flag = true;
		        	    	microscopy3Flag = true;
		        	    }
		        	    if(hain != null && h != null)
		        	    	hainFlag = true;
		        	    if(hain2 != null && h2 != null)
		        	    	hain2Flag = true;
		        	    if(xpert != null && x != null)
		        	    	xpertFlag = true;
		        	    if(culture != null && c != null)
		        	    	cultureFlag = true;
			        
		        	   if(culture != null && c != null){
			        	    if(cultureDateFrom != null &&  cultureDateFrom.after(c.getResultDate()))
			        	    	cultureFlag = false;
			        	    
			        	    if(cultureDateTo != null &&  cultureDateTo.before(c.getResultDate()))
			        	    	cultureFlag = false;
			        	    
			        	    if(cultureResult != null && cultureResult != c.getResult())
			        	    	cultureFlag = false;
		        	   }
		        	   
		        	   if(hain2 != null && h2 != null){
			        	    if(hain2DateFrom != null &&  hain2DateFrom.after(h2.getResultDate()))
			        	    	hain2Flag = false;
			        	    
			        	    if(hain2DateTo != null &&  hain2DateTo.before(h2.getResultDate()))
			        	    	hain2Flag = false;
			        	    
			        	    if(mtbHain2Result != null && mtbHain2Result != h2.getResult())
			        	    	hain2Flag = false;
			        	    
			        	    if(moxHain2Result != null && moxHain2Result != h2.getMoxResistance())
			        	    	hain2Flag = false;
			        	    
			        	    if(cmHain2Result != null && cmHain2Result != h2.getCmResistance())
			        	    	hain2Flag = false;
			        	    
			        	    if(erHain2Result != null && erHain2Result != h2.getErResistance())
			        	    	hain2Flag = false;
		        	   }
		        	   
		        	   if(hain != null && h != null){
			        	    if(hainDateFrom != null &&  hainDateFrom.after(h.getResultDate()))
			        	    	hainFlag = false;
			        	    
			        	    if(hainDateTo != null &&  hainDateTo.before(h.getResultDate()))
			        	    	hainFlag = false;
			        	    
			        	    if(mtbHainResult != null && mtbHainResult != h.getResult())
			        	    	hainFlag = false;
			        	    
			        	    if(rifHainResult != null && rifHainResult != h.getRifResistance())
			        	    	hainFlag = false;
			        	    
			        	    if(inhHainResult != null && inhHainResult != h.getInhResistance())
			        	    	hainFlag = false;
		        	   }
		        	   
		        	   if(xpert != null && x != null){
			        	    if(xpertDateFrom != null &&  xpertDateFrom.after(x.getResultDate()))
			        	    	xpertFlag = false;
			        	    
			        	    if(xpertDateTo != null &&  xpertDateTo.before(x.getResultDate()))
			        	    	xpertFlag = false;
			        	    
			        	    if(mtbHainResult != null && mtbHainResult != h.getResult())
			        	    	xpertFlag = false;
			        	    
			        	    if(rifXpertResult != null && rifXpertResult != x.getRifResistance())
			        	    	xpertFlag = false;
			        	    
			        	    if(!xpertError.equals("") && !xpertError.equals(x.getErrorCode()))
			        	    	xpertFlag = false;
			        	    
		        	   }
		        	   
		        	   if(microscopy != null && m1 != null){
			        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m1.getResultDate()))
			        	    	microscopy1Flag = false;
			        	    
			        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m1.getResultDate()))
			        	    	microscopy1Flag = false;
			        	    
			        	    if(sampleAppearance != null && sampleAppearance != m1.getSampleApperence())
			        	    	microscopy1Flag = false;
			        	    
			        	    if(sampleResult != null && sampleAppearance != m1.getSampleResult())
			        	    	microscopy1Flag = false;
		        	   }
		        	   
		        	   if(microscopy != null && m2 != null){
			        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m2.getResultDate()))
			        	    	microscopy2Flag = false;
			        	    
			        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m2.getResultDate()))
			        	    	microscopy2Flag = false;
			        	    
			        	    if(sampleAppearance != null && sampleAppearance != m2.getSampleApperence())
			        	    	microscopy2Flag = false;
			        	    
			        	    if(sampleResult != null && sampleAppearance != m2.getSampleResult())
			        	    	microscopy3Flag = false;
		        	   }
		        	   
		        	   if(microscopy != null && m3 != null){
			        	    if(microscopyDateFrom != null &&  microscopyDateFrom.after(m3.getResultDate()))
			        	    	microscopy3Flag = false;
			        	    
			        	    if(microscopyDateTo != null &&  microscopyDateTo.before(m3.getResultDate()))
			        	    	microscopy3Flag = false;
			        	    
			        	    if(sampleAppearance != null && sampleAppearance != m3.getSampleApperence())
			        	    	microscopy3Flag = false;
			        	    
			        	    if(sampleResult != null && sampleAppearance != m3.getSampleResult())
			        	    	microscopy3Flag = false;
		        	   }
		        	   
		        	   if(!(culture == null && hain == null && hain2 == null && xpert == null && microscopy == null)){
			        	   
		        		  Boolean flag = false;
		        		  if(culture != null && cultureFlag)
		        			  flag = true;
		        		  if(hain2 != null && hain2Flag)
		        			  flag = true;
		        		  if(hain != null && hainFlag)
		        			  flag = true;
		        		  if(xpert != null && xpertFlag)
		        			  flag = true;
		        		  if(microscopy != null && microscopy1Flag)
		        			  flag = true;
		        		  
		        		  if(!flag)
			        		  continue;
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
			            
		        	    if(c != null){
				            mMap.put("cultureResultDate", c.getResultDate() == null ? "" : df.format(c.getResultDate()));
			        	    mMap.put("cultureResult", c.getResult().getName());
			            }
		        	    
		        	    if(h2 != null){
				            mMap.put("hain2ResultDate", h2.getResultDate() == null ? "" : df.format(h2.getResultDate()));
			        	    mMap.put("hain2MtbResult", h2.getResult().getName());
			        	    mMap.put("hain2MoxResult", h2.getMoxResistance() == null ? "" : h2.getMoxResistance().getName());
			        	    mMap.put("hain2CmResult",  h2.getCmResistance() == null ? "" : h2.getCmResistance().getName());
			        	    mMap.put("hain2ErResult",  h2.getErResistance() == null ? "" : h2.getErResistance().getName());
			            }
		        	    
		        	    if(h != null){
		        	    	mMap.put("hainResultDate", h.getResultDate() == null ? "" : df.format(h.getResultDate()));
			        	    mMap.put("hainMtbResult", h.getResult().getName());
			        	    mMap.put("hainRifResult", h.getRifResistance() == null ? "" : h.getRifResistance().getName());
			        	    mMap.put("hainInhResult",  h.getInhResistance() == null ? "" : h.getInhResistance().getName());
		        	    }
		        	    
		        	    if(x != null){
		        	    	mMap.put("xpertResultDate", x.getResultDate() == null ? "" : df.format(x.getResultDate()));
			        	    mMap.put("xpertMtbResult", x.getResult().getName());
			        	    mMap.put("xpertRifResult", x.getRifResistance() == null ? "" : x.getRifResistance().getName());
			        	    mMap.put("xpertErrorCode",  x.getErrorCode());
		        	    }
		        	    
		        	    if(m1 != null){
		        	    	mMap.put("microscopyResultDate_1", m1.getResultDate() == null ? "" : df.format(m1.getResultDate()));
			        	    mMap.put("sampleAppearence_1", m1.getSampleApperence().getName());
			        	    mMap.put("sampleResult_1", m1.getSampleResult().getName());
		        	    }
			            
			            if(m2 != null){
		        	    	mMap.put("microscopyResultDate_2", m2.getResultDate() == null ? "" : df.format(m2.getResultDate()));
			        	    mMap.put("sampleAppearence_2", m2.getSampleApperence().getName());
			        	    mMap.put("sampleResult_2", m2.getSampleResult().getName());
		        	    }
			            
			            if(m3 != null){
		        	    	mMap.put("microscopyResultDate_3", m3.getResultDate() == null ? "" : df.format(m3.getResultDate()));
			        	    mMap.put("sampleAppearence_3", m3.getSampleApperence().getName());
			        	    mMap.put("sampleResult_3",m3.getSampleResult().getName());
		        	    }
		        	    
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
