package org.openmrs.module.mdrtb.web.controller.specimen;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/mdrtb/specimen/delete.form")
public class SpecimenDeleteController {

	// method to delete a test
	@RequestMapping(params="testId")
	public String deleteTest(@RequestParam(value = "testId", required = true) Integer testId, 
	                         @RequestParam(value = "specimenId", required = true) Integer specimenId,
	                         @RequestParam(value = "patientProgramId", required = true) Integer patientProgramId) {
		
		Context.getService(MdrtbService.class).deleteTest(testId);

		return "redirect:specimen.form?specimenId=" + specimenId + "&patientProgramId=" + patientProgramId;
	}
	
	// method to delete a specimen
	@RequestMapping(params = {"specimenId", "!testId"})
	public String deleteSpecimen(@RequestParam(value = "specimenId", required = true) Integer specimenId, 
	                             @RequestParam(value = "patientId", required = true) Integer patientId,
	                             @RequestParam(value = "patientProgramId", required = true) Integer patientProgramId) {
		
		Context.getService(MdrtbService.class).deleteSpecimen(specimenId);
		
		return "redirect:specimen.form?patientId=" + patientId + "&patientProgramId=" + patientProgramId;
	}
}
