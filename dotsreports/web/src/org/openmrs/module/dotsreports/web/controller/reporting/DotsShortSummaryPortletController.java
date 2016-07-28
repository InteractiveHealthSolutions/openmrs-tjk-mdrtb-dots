package org.openmrs.module.dotsreports.web.controller.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.dotsreports.reporting.PatientSummaryUtil;

/**
 */
public class DotsShortSummaryPortletController extends DotsPatientSummaryPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Return the columns appropriate for this Patient Summary
	 * @return
	 */
	@Override
	protected String[] getSummaryColumns(HttpServletRequest request) {
		String[] columns = request.getParameterValues("columns");
		if (columns == null || columns.length == 0) {
			List<String> l = new ArrayList<String>();
			l.add(PatientSummaryUtil.PATIENT_ID);
			l.add(PatientSummaryUtil.FULL_NAME);
			l.add(PatientSummaryUtil.AGE);
			l.add(PatientSummaryUtil.GENDER);
			l.add(PatientSummaryUtil.CURRENT_TB_REGIMEN);
			l.add("state.6");
			l.add("obs.RESULT OF HIV TEST.latest");
			l.add("obs.RESULT OF HIV TEST.latestDate");
			l.add(PatientSummaryUtil.PRIMARY_IDENTIFIER);
			columns = l.toArray(new String[] {});
		}
		return columns;
	}
}
