package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.Oblast;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.mdrtb.reporting.PreviewReportRenderer;
import org.openmrs.module.mdrtb.reporting.ReportSpecification;
import org.openmrs.module.mdrtb.reporting.data.DSTReportTJK;
import org.openmrs.module.mdrtb.reporting.data.MOHReport;
import org.openmrs.module.mdrtb.reporting.data.MOHReportTJK;
import org.openmrs.module.mdrtb.reporting.data.OutcomeReport;
import org.openmrs.module.mdrtb.reporting.data.OutcomeReportTJK;
import org.openmrs.module.mdrtb.reporting.data.TB07TJK;
import org.openmrs.module.mdrtb.reporting.data.TB08TJK;
import org.openmrs.module.mdrtb.reporting.data.WHOForm05;
import org.openmrs.module.mdrtb.reporting.data.WHOForm05TJK;
import org.openmrs.module.mdrtb.reporting.data.WHOForm07;
import org.openmrs.module.mdrtb.reporting.data.WHOForm07TJK;
import org.openmrs.module.mdrtb.service.MdrtbService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportingController {  
    
	/**
	 * Render a Report
	 */
    @RequestMapping("/module/mdrtb/reporting/reports")
	public void report(
        @RequestParam(required=false, value="type") Class<? extends ReportSpecification> type,
        HttpServletRequest request, ModelMap model) throws Exception {

    	List<ReportSpecification> availableReports = new ArrayList<ReportSpecification>();
    	availableReports.add(new TB07TJK());
    	
    	availableReports.add(new TB08TJK());
    	availableReports.add(new DSTReportTJK());
    	
    	
    	model.addAttribute("availableReports", availableReports);
    	
		model.addAttribute("type", type);
		if (type != null) {
			model.addAttribute("report", type.newInstance());
		}
		
		List<Oblast> oblasts = Context.getService(MdrtbService.class).getOblasts();
		model.addAttribute("oblasts", oblasts);
    }
    
	/**
	 * Render a Report
	 */
    @RequestMapping("/module/mdrtb/reporting/render")
	public void render(
        @RequestParam(required=true, value="type") Class<? extends ReportSpecification> type,
        @RequestParam(required=false, value="format") String format,
        @RequestParam(required=false, value="oblast") String oblast,
        HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

    	response.setContentType("text/html");
		try {
			ReportSpecification report = type.newInstance();
			
			Map<String, Object> parameters = new LinkedHashMap<String, Object>();
			for (Parameter p : report.getParameters()) {
				Object val = WidgetUtil.getFromRequest(request, "p."+p.getName(), p.getType(), p.getCollectionType());
				parameters.put(p.getName(), val);
			}
			parameters.put("oblast",oblast);
			EvaluationContext context = report.validateAndCreateContext(parameters);
			ReportData data = report.evaluateReport(context);
			RenderingMode mode = new RenderingMode(new PreviewReportRenderer(), "Preview", null, null);
			if (format != null) {
				for (RenderingMode m : report.getRenderingModes()) {
					if (m.getLabel().equals(format)) {
						mode = m;
					}
				}
			}
			mode.getRenderer().render(data, mode.getArgument(), response.getOutputStream());
		}
		catch (Exception e) {
			response.getOutputStream().print("<html><body><span class=\"error\">Error: " + e.getMessage() + "</span><br/><br/>");
			response.getOutputStream().print("<a href=\"#\" onclick=\"document.getElementById('errorDetailDiv').style.display = '';\">Error Details</a><br/><br/>");
			response.getOutputStream().print("<span id=\"errorDetailDiv\" style=\"display:none;\">");
			for (StackTraceElement ste : e.getStackTrace()) {
				response.getOutputStream().print(ste.toString());
			}
			response.getOutputStream().print("<span></body></html>");
		}
    }
}
