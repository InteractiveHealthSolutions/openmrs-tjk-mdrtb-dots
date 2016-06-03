package org.openmrs.module.mdrtb.web.controller.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.status.HivStatusRenderer;
import org.openmrs.module.mdrtb.status.StatusItem;


public class DashboardHivStatusRenderer implements HivStatusRenderer {

    public void renderCd4Count(StatusItem cd4Count) {
    	if(cd4Count.getValue() == null || ((Obs) cd4Count.getValue()).getValueNumeric() == null) {
    		cd4Count.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.unknown"));
    	}
    	else {
    		cd4Count.setDisplayString(((Obs) cd4Count.getValue()).getValueNumeric().toString());
    	}
    }

    public void renderHivStatus(StatusItem hivStatus) {
	    if (hivStatus.getValue() == null) {
	    	hivStatus.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.unknown"));
	    }
	    else {
	    	hivStatus.setDisplayString(((Concept) hivStatus.getValue()).getDisplayString());
	    }
	    
    }

    public void renderMostRecentTestResult(StatusItem mostRecentTestResult) {
	    if (mostRecentTestResult.getValue() == null || ((Obs) mostRecentTestResult.getValue()).getValueCoded() == null) {
	    	mostRecentTestResult.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.unknown"));
	    }
	    else {
	    	Obs result = (Obs) mostRecentTestResult.getValue();

	    	DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
			String params [] = {result.getValueCoded().getDisplayString(), df.format(result.getObsDatetime())};
	    	
	    	mostRecentTestResult.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.testResultsStatus", params,
			    "{0} on {1}", Context.getLocale()));
	    }
    }

    public void renderRegimen(StatusItem regimenItem) {
	   Regimen regimen = (Regimen) regimenItem.getValue();
	
	   if(regimen != null) {
	   		// first we need to pull out all the drugs in this regimen
   			List<Concept> generics = new LinkedList<Concept>();
   			for (DrugOrder component : regimen.getDrugOrders()) {
   				// TODO: note that we are operating on generics, not the drug itself
   				generics.add(component.getConcept());
   			}
   	
   			// sort the drug list
   			generics = MdrtbUtil.sortAntiretrovirals(generics);
	   
   			regimenItem.setDisplayString(DashboardStatusRendererUtil.renderDrugList(generics));
	   }
    }
    
    // just need to handle the null case here, since the base regimen rendering has been handled in "renderRegimen" method
    public void renderCurrentRegimen(StatusItem regimenItem) {
    	if (regimenItem.getValue() == null) {
    		regimenItem.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.none"));
    	}
    }
    
    @SuppressWarnings("unchecked")
    public void renderArtTreatment(StatusItem artTreatment) {
    	List<StatusItem> regimens = (List<StatusItem>) artTreatment.getValue();
    	
    	if (regimens == null || regimens.isEmpty()) {
    		artTreatment.setDisplayString(Context.getMessageSourceService().getMessage("mdrtb.notOnTreatment"));
    	}
    	else {
    		// remember, the regimen list is in reverse order
    		Date startDate = ((Regimen) regimens.get(regimens.size() - 1).getValue()).getStartDate();
    		Date endDate = ((Regimen) regimens.get(0).getValue()).getStartDate();
    		
    		DateFormat df = new SimpleDateFormat(MdrtbConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
    		artTreatment.setDisplayString(df.format(startDate) + " - " + (endDate == null ? df.format(endDate) : 
    			Context.getMessageSourceService().getMessage("mdrtb.present")));
    	}
    }
}
