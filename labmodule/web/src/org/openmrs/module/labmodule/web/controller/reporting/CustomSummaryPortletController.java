package org.openmrs.module.labmodule.web.controller.reporting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class CustomSummaryPortletController extends PatientSummaryPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Return the columns appropriate for this Patient Summary
	 * @return
	 */
	@Override
	protected String[] getSummaryColumns(HttpServletRequest request) {
		String[] columns = request.getParameterValues("columns");
		if (columns == null || columns.length == 0) {
			columns = new String[] {};
		}
		return columns;
	}
}
