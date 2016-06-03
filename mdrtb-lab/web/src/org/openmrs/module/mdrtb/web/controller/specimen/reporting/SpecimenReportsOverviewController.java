package org.openmrs.module.mdrtb.web.controller.specimen.reporting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpecimenReportsOverviewController {

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		
		//bind dates
		SimpleDateFormat dateFormat = Context.getDateFormat();
    	dateFormat.setLenient(false);
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		
		// register binders for locations 
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@ModelAttribute("locations")
	public Collection<Location> getPossibleLocations() {
		return Context.getLocationService().getAllLocations(false);
	}
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/mdrtb/specimen/specimenReportsOverview.form")
	public ModelAndView showReports(@ModelAttribute("query") SpecimenReportsQuery query, BindingResult errors, 
	                                SessionStatus status, HttpServletRequest request, ModelMap map) {		
		
		// initialize the query if it is empty
		if (query.getStartDateCollected() == null && query.getEndDateCollected() == null && query.getDaysSinceCulture() == null && query.getLab() == null) {			
			initializeQuery(query);
		}
		// otherwise, save the values
		else {
			GlobalProperty daysSinceSmear = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.specimenReports.daysSinceSmear");
			GlobalProperty daysSinceCulture = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.specimenReports.daysSinceCulture");
			GlobalProperty defaultLabId = Context.getAdministrationService().getGlobalPropertyObject("mdrtb.specimenReports.defaultLabId");
			
			daysSinceSmear.setPropertyValue(query.getDaysSinceSmear().toString());
			daysSinceCulture.setPropertyValue(query.getDaysSinceCulture().toString());
			defaultLabId.setPropertyValue(query.getLab().getId().toString());
			
			Context.getAdministrationService().saveGlobalProperty(daysSinceCulture);
			Context.getAdministrationService().saveGlobalProperty(daysSinceCulture);
			Context.getAdministrationService().saveGlobalProperty(defaultLabId);
		}
		
		map.put("query", query);
		
		return new ModelAndView("/module/mdrtb/specimen/specimenReportsOverview", map);		
	}
	
	private void initializeQuery(SpecimenReportsQuery query) {
		Calendar startDate = Calendar.getInstance();
		startDate.add(Calendar.YEAR, -1);
		
		// set the default start date to one year ago, and the default end date to today's date
		query.setStartDateCollected(startDate.getTime());
		query.setEndDateCollected(new Date());
		
		query.setDaysSinceSmear(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("mdrtb.specimenReports.daysSinceSmear")));
		query.setDaysSinceCulture(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("mdrtb.specimenReports.daysSinceCulture")));
		query.setLab(Context.getLocationService().getLocation(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("mdrtb.specimenReports.defaultLabId"))));
	}
}
