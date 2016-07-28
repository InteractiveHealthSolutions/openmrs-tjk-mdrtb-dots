package org.openmrs.module.dotsreports.web.controller.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dotsreports.reporting.PatientSummaryUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.web.controller.PortletController;

/**
 * Provides a Patient Summary Portlet
 */
public class DotsPatientSummaryPortletController extends PortletController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Return the columns appropriate for this Patient Summary
	 * @return
	 */
	protected String[] getSummaryColumns(HttpServletRequest request) {
		return null; // Default to all columns
	}

	@SuppressWarnings("unchecked")
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Integer patientId = (Integer)model.get("patientId");
		String lId = (String)model.get("locationId");
		String patientIds = (String) model.get("patientIds");
		Date runDate = new Date();
		model.put("availableColumns", PatientSummaryUtil.getAvailableKeys());
		
		final String sort = ObjectUtil.nvlStr(request.getParameter("sort"), "fullName");
		model.put("sort", sort);
		
		Cohort c = null;
		
		if (patientId != null) {
			c = PatientSummaryUtil.getCohort(patientId);
		}
		else if (StringUtils.isNotEmpty(lId)) {
			Location l = Context.getLocationService().getLocation(Integer.valueOf(lId));
			c = PatientSummaryUtil.getCohort(l);
		}
		else if (StringUtils.isNotEmpty(patientIds)) {
			c = new Cohort(patientIds);
		}
		
		String[] cols = getSummaryColumns(request);
		List<String> colList = (cols == null ? null : Arrays.asList(cols));
		model.put("columns", cols);
		data.addAll(PatientSummaryUtil.getPatientSummaryData(c, colList, runDate).values());

		if (data.size() == 1) {
			model.put("patient", data.get(0));
		}
		
		Collections.sort(data, new Comparator<Map<String, Object>>() {
		    public int compare(Map<String, Object> left, Map<String, Object> right) {
		    	Object l = left.get(sort);
		    	Object r = right.get(sort);
		    	if (l == null) {
		    		if (r == null) { return 0; }
		    		return 1;
		    	}
		    	else {
		    		if (r == null) { return -1; }
		    		if (l instanceof Comparable && r instanceof Comparable) {
		    			return ((Comparable)l).compareTo((Comparable)r);
		    		}
		    		return l.toString().compareTo(r.toString());
		    	}
		    }
		});    
        model.put("data", data);
        model.put("runDate", runDate);
	}
}
