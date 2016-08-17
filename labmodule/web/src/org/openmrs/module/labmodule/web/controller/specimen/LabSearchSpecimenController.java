package org.openmrs.module.labmodule.web.controller.specimen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Bacteriology;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst;
import org.openmrs.module.labmodule.specimen.DstResult;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResult;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.ScannedLabReport;
import org.openmrs.module.labmodule.specimen.Smear;
import org.openmrs.module.labmodule.specimen.Specimen;
import org.openmrs.module.labmodule.specimen.SpecimenValidator;
import org.openmrs.module.labmodule.specimen.TestValidator;
import org.openmrs.module.labmodule.specimen.Xpert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/labmodule/lab/labResult.form")
public class LabSearchSpecimenController extends AbstractSpecimenController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Returns the default list of drugs to display in the DST add test results box
	 */
	@ModelAttribute("defaultDstDrugs")
	public List<List<Object>> getDefaultDstDrugs() {
		return TbUtil.getDefaultDstDrugs();
	}
	
	/**
	 * Returns the specimen that should be used to bind a form posting to
	 * 
	 * @param specimenId
	 * @return
	 */
	@ModelAttribute("specimen")
	public Specimen getSpecimen(@RequestParam(required = false, value="specimenId") Integer specimenId) {
		// for now, if no specimen is specified, default to the most recent specimen within the program
		System.out.println("->>>>>>" + specimenId);
		
		if (specimenId != null) {
			return Context.getService(TbService.class).getSpecimen(Context.getEncounterService().getEncounter(specimenId));
		}
		else {
			return null;
		}
	}
		
	
	@SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET) 
	public ModelAndView showSpecimen(@RequestParam(required = false, value = "testId") String testId, ModelMap map) {
		
		// add the testId to the model map if there is one (this is used to make sure the proper detail page is shown after an edit)
		// TODO: unfortunately, this does not currently work since whenever an obs is saved it gets voided and recreated, so this
		// test id is no longer valid
		if (StringUtils.isEmpty(testId)) {
			testId = "-1";
		}
		map.put("testId", testId);
		
		return new ModelAndView("/module/labmodule/lab/labResult", map);
	}
	
	
    
   
   
   
    
    
    
}
