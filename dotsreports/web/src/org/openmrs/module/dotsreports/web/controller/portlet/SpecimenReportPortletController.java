package org.openmrs.module.dotsreports.web.controller.portlet;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.exception.MdrtbAPIException;
import org.openmrs.module.dotsreports.specimen.Specimen;
import org.openmrs.module.dotsreports.specimen.reporting.SpecimenReportingTools;
import org.openmrs.web.controller.PortletController;


public class SpecimenReportPortletController extends PortletController {
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		Map<Patient,List<Specimen>> results = null;
		
		Integer [] labs = new Integer[1];
		
		if (StringUtils.isNotBlank((String) model.get("lab"))) {
			labs[0] = (Context.getLocationService().getLocation(Integer.valueOf((String) model.get("lab")))).getId();
		}
	
		Date startDateCollected = null;
		Date endDateCollected = null;
		
		if (StringUtils.isNotBlank((String) model.get("startDateCollected"))) {
			try {
	            startDateCollected = Context.getDateFormat().parse((String) model.get("startDateCollected"));
            }
            catch (ParseException e) {
	            log.error("Unable to parse start date collected", e);
            }
		}
		
		if (StringUtils.isNotBlank((String) model.get("endDateCollected"))) {
			try {
	            endDateCollected = Context.getDateFormat().parse((String) model.get("endDateCollected"));
            }
            catch (ParseException e) {
	            log.error("Unable to parse end date collected", e);
            }
		}
		
		Integer daysSinceSmear = 0;
		
		if (StringUtils.isNotBlank((String) model.get("daysSinceSmear"))) {
			daysSinceSmear = Integer.valueOf((String) model.get("daysSinceSmear"));
		}
		
		Integer daysSinceCulture = 0;
		
		if (StringUtils.isNotBlank((String) model.get("daysSinceCulture"))) {
			daysSinceCulture = Integer.valueOf((String) model.get("daysSinceCulture"));
		}
		
		// get the program we are currently viewing	
		if (StringUtils.isNotBlank((String) model.get("report"))) {
			String report = (String) model.get("report");
			
			if (report.equals("specimensWithNoResults")) {
				results = SpecimenReportingTools.getSpecimensWithNoResults(startDateCollected, endDateCollected);
				model.put("name", "mdrtb.specimensWithNoResults");
			}
			else if (report.equals("specimensWithPositiveCultureButNoDstResults")) {
				results = SpecimenReportingTools.getSpecimensWithPositiveCultureButNoDstResults(startDateCollected, endDateCollected, daysSinceCulture, (labs.length !=0 ? labs : null));
				model.put("name", "mdrtb.specimensWithPositiveCultureButNoDstResults");
			}
			else if (report.equals("specimensWithPositiveSmearButNoCultureResults")) {
				results = SpecimenReportingTools.getSpecimensWithPositiveSmearButNoCultureResults(startDateCollected, endDateCollected, daysSinceSmear, (labs.length !=0 ? labs : null));
				model.put("name", "mdrtb.specimensWithPositiveSmearButNoCultureResults");
			}
			else if (report.equals("specimensWithContaminatedTest")) {
				results = SpecimenReportingTools.getSpecimensWithContaminatedTests(startDateCollected, endDateCollected, (labs.length !=0 ? labs : null));
				model.put("name", "mdrtb.specimensWithContaminatedTests");
			}
			
			model.put("results", results);
		}
		else {
			throw new MdrtbAPIException("Specimen report portlet requires a report parameter.");
		}
	}	
}
