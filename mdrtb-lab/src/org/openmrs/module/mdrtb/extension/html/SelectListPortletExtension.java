package org.openmrs.module.mdrtb.extension.html;

import org.openmrs.module.web.extension.PortletExt;

public abstract class SelectListPortletExtension extends PortletExt {

	/** 
	 * @see PortletExt#getPortletParameters()
	 */
	@Override
	public String getPortletParameters() {
		return "";
	}
	
	/**
	 * Return the key that identifies this option
	 * @return
	 */
	public abstract String getKey();
	
	/**
	 * Return the message code to use to display this option
	 * @return
	 */
	public abstract String getLabel();
}
