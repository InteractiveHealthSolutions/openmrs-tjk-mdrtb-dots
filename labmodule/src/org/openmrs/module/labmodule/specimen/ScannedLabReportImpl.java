package org.openmrs.module.labmodule.specimen;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.exception.MdrtbAPIException;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.multipart.MultipartFile;


public class ScannedLabReportImpl implements ScannedLabReport {
	
	protected final Log log = LogFactory.getLog(getClass());
		
	Obs report;
	
	public ScannedLabReportImpl() {
	}
	
	// create a new scanned lab report object, given an existing encounter
	public ScannedLabReportImpl(Encounter encounter) {
	
		if(encounter == null) {
			throw new MdrtbAPIException ("Cannot create scanned lab report: encounter can not be null.");
		}
		
		report = new Obs (encounter.getPatient(), Context.getService(TbService.class).getConcept(TbConcepts.SCANNED_LAB_REPORT), encounter.getEncounterDatetime(), null);
	}
	
	// create a scanned lab report given an existing obs
	public ScannedLabReportImpl(Obs report) {
		
		try {
			this.report = Context.getObsService().getComplexObs(report.getId(), OpenmrsConstants.RAW_VIEW);
		}
		catch(Exception e) {
			throw new MdrtbAPIException ("Unable to retrieve scanned lab report. File may be missing.", e);
		}
	}

	public Object getScannedLabReport() {
		return this.report;
	}
	
	public Object getFile() {
		if (report.getComplexData() != null) {
			return report.getComplexData().getData();
		}
		else {
			return null;
		}
	}
	
    public String getFilename() {
    	if (report.getComplexData() != null) {
    		return report.getComplexData().getTitle();
    	}
    	else {
    		return null;
    	}
    }

    public String getId() {
	    return report.getId().toString();
    }
    
    public Location getLab() {
    	return report.getLocation();
    }
    
    public void setFile(MultipartFile file) {
    	ComplexData data = null;
    	
        try {
	        data = new ComplexData(file.getOriginalFilename(), file.getInputStream());
        }
        catch (IOException e) {
	        log.error("Unable to load scanned lab report", e);
        }
    	report.setComplexData(data);
    }
    
    public void setLab(Location lab) {
    	report.setLocation(lab);
    }
    
    /**
	  * Protected methods used for interacting with the matching MdrSpecimenImpl
	  */

    protected void voidScannedLabReport() {	
    	this.report.setVoided(true);
    	this.report.setVoidReason("voided by ScannedLabReportImpl class");
	}
    
	 protected Obs getObs() {
		 return report;
	 }
}
