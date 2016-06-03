package org.openmrs.module.mdrtb.extension.html;

import org.openmrs.module.web.extension.PortletExt;

public abstract class PatientDetailPortletExtension extends PortletExt {

	/** 
	 * @see PortletExt#getPortletParameters()
	 */
	@Override
	public String getPortletParameters() {
		return "";
	}
	
	/**
	 * Return the HTML that goes between the link tags
	 * @return
	 */
	public abstract String getImagePath();
	
	/**
	 * Return the message code to use to display on the title of the popup
	 * @return
	 */
	public abstract String getTitle();
}
