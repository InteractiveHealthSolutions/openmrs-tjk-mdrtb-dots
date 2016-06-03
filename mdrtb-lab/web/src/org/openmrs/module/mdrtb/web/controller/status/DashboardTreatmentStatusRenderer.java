package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbConstants.TreatmentState;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.status.TreatmentStatusRenderer;
import org.openmrs.module.reporting.common.MessageUtil;


public class DashboardTreatmentStatusRenderer implements TreatmentStatusRenderer {

    public String renderRegimen(Regimen regimen) {
    	  	
    	DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
    	
    	String regimenStr = RegimenUtils.formatRegimenGenerics(regimen, " + ", "mdrtb.none");
    	String startDateStr = df.format(regimen.getStartDate());
    	String endDateStr = (regimen.getEndDate() != null ? df.format(regimen.getEndDate()) : MessageUtil.translate("mdrtb.present"));
    	String typeStr = RegimenUtils.formatCodedObs(regimen.getReasonForStarting(), "");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<tr>");
    	sb.append("<td>" + regimenStr + "</td>");
    	sb.append("<td>" + startDateStr + "</td>");
    	sb.append("<td>" + endDateStr + "</td>");
    	sb.append("<td>" + typeStr + "</td>");
    	sb.append("</tr>");
    	
    	return sb.toString();
    }

    public String renderTreatmentState(TreatmentState state) {
	   if (state == TreatmentState.ON_TREATMENT) { 
		   return Context.getMessageSourceService().getMessage("mdrtb.onTreatment");
		   
	   }
	   else if (state == TreatmentState.NOT_ON_TREATMENT) {
		   return Context.getMessageSourceService().getMessage("mdrtb.notOnTreatment");	   
	   }
	   else {
		   return "";
	   }
    }

}
