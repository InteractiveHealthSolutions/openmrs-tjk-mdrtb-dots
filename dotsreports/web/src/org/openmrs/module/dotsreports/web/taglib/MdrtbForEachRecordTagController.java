package org.openmrs.module.dotsreports.web.taglib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;


public class MdrtbForEachRecordTagController extends BodyTagSupport {

    public static final long serialVersionUID = 1232300L;
    
    private final Log log = LogFactory.getLog(getClass());

    private String name;
    private Object select;
    private String reportObjectType;
    private String concept;
    private String conceptSet;
    private String programName;
    private String workflowNames;
    private Iterator records;
    private String filterList;

    public int doStartTag() {
        
        records = null;
        
        Locale locale = Context.getLocale();
        
        if (name.equals("patientIdentifierType")) {

            PatientService ps = Context.getPatientService();
            List<PatientIdentifierType> pitsOut = new ArrayList<PatientIdentifierType>();
            List<PatientIdentifierType> pits = Context.getPatientService().getAllPatientIdentifierTypes();
            
            //records = ps.getPatientIdentifierTypes().iterator();
                if (filterList != null && filterList.equals("") == false){
                    try{
                    for (StringTokenizer st = new StringTokenizer(filterList, "|"); st.hasMoreTokens(); ) {
                        String s = st.nextToken();
                        PatientIdentifierType pitGlobalProp = ps.getPatientIdentifierTypeByName(s);
                        if (pitGlobalProp != null){
                            for (PatientIdentifierType p:pits){
                                if (p.equals(pitGlobalProp))
                                    pitsOut.add(p);
                            }
                        }
                        
                        if (pitsOut.size() == 0)
                            pitsOut = pits;
                       
                    }
                    }catch (Exception e){
                        log.error("PatientIdentifierTypes in global properties are invalid.");
                    }
                } else {
                    pitsOut = pits;
                }
                records = pitsOut.iterator();
        }
        else if (name.equals("relationshipType")) {
            PersonService ps = Context.getPersonService();
            records = ps.getAllRelationshipTypes().iterator();
        }
        else if (name.equals("encounterType")) {
            EncounterService es = Context.getEncounterService();
            records = es.getAllEncounterTypes().iterator();
        }
        else if (name.equals("location")) {
            List<Location> locationOut = new ArrayList<Location>();
            List<Location> locations = Context.getLocationService().getAllLocations(false);

                if (filterList != null && filterList.equals("") == false){
                    try{
                    for (StringTokenizer st = new StringTokenizer(filterList, "|"); st.hasMoreTokens(); ) {
                        String s = st.nextToken();
                        Location locGlobalProp = Context.getLocationService().getLocation(s);
                        if (locGlobalProp != null){
                            for (Location l:locations){
                                if (l.equals(locGlobalProp))
                                    locationOut.add(l);
                            }
                        }
                       
                    }
                    }catch (Exception e){
                        log.error("PatientIdentifierTypes in global properties are invalid.");
                    }
                } else {
                   locationOut = locations;
                }
                records = locationOut.iterator();
        }
//        else if (name.equals("tribe")) {
//            PatientService ps = Context.getPatientService();
//            records = ps.getTribes().iterator();
//        }
        else if (name.equals("cohort")) {
            List<Cohort> cohorts = Context.getCohortService().getAllCohorts();
            records = cohorts.iterator();
        }
        else if (name.equals("form")) {
            List<Form> forms = Context.getFormService().getAllForms();
            records = forms.iterator();
        }

        else if (name.equals("civilStatus")) {
            ConceptService cs = Context.getConceptService();
            Concept civilStatus = cs.getConcept(OpenmrsConstants.CIVIL_STATUS_CONCEPT_ID);
            if (civilStatus == null)
                log.error("OpenmrsConstants.CIVIL_STATUS_CONCEPT_ID is defined incorrectly.");
            
            records = civilStatus.getAnswers().iterator();
            
            Map<String, String> opts = new HashMap<String, String>();
            for (ConceptAnswer a : civilStatus.getAnswers()) {
                opts.put(a.getAnswerConcept().getConceptId().toString(), a.getAnswerConcept().getName(locale, false).getName());
            }
            records = opts.entrySet().iterator();
            if (select != null)
                select = select.toString() + "=" + opts.get(select);
        }
        else if (name.equals("gender")) {
            Map<String, String> opts = OpenmrsConstants.GENDER();
            records = opts.entrySet().iterator();
            if (select != null)
                select = select.toString() + "=" + opts.get(select);
        }
        else if (name.equals("workflowProgram")) {
            List<org.openmrs.Program> ret = Context.getProgramWorkflowService().getAllPrograms();
            records = ret.iterator();
        }
        else if (name.equals("workflow")) {
        	List<ProgramWorkflow> workflows = new ArrayList<ProgramWorkflow>();
        	Program p = Context.getProgramWorkflowService().getProgramByName(programName);
        	if (StringUtils.hasText(workflowNames)) {
        		for (StringTokenizer st = new StringTokenizer(workflowNames, "|"); st.hasMoreTokens(); ) {
        			String workflowName = st.nextToken();
        			workflows.add(p.getWorkflowByName(workflowName));
        		}
        	}
        	else {
        		workflows.addAll(p.getAllWorkflows());
        	}
        	records = workflows.iterator();
        }
        else if (name.equals("state")) {
        	List<ProgramWorkflowState> filteredStates = new ArrayList<ProgramWorkflowState>();
        	Program p = Context.getProgramWorkflowService().getProgramByName(programName);
        	if (StringUtils.hasText(workflowNames)) {
        		for (StringTokenizer st = new StringTokenizer(workflowNames, "|"); st.hasMoreTokens(); ) {
        			String workflowName = st.nextToken();
        			ProgramWorkflow wf = p.getWorkflowByName(workflowName);
        			filteredStates.addAll(wf.getStates());
        		}
        	}
        	records = filteredStates.iterator();
        }
        else if (name.equals("role")) {
            List<Role> ret = Context.getUserService().getAllRoles();
            records = ret.iterator();
        }
        else if (name.equals("user")) {
        	List<User> users = new ArrayList<User>();
        	if (StringUtils.hasText(filterList)) {
        		for (StringTokenizer st = new StringTokenizer(filterList, "|"); st.hasMoreTokens(); ) {
        			String r = st.nextToken();
        			Role role = Context.getUserService().getRole(r);
        			if (role == null) {
        				throw new IllegalArgumentException("An invalid role of " + r + " was specified.");
        			}
        			users.addAll(Context.getUserService().getUsersByRole(role));
        		}
        	}
        	else {
        		users.addAll(Context.getUserService().getAllUsers());
        	}
        	Collections.sort(users, new Comparator<User>() {
    			public int compare(User u1, User u2) {
    				return u1.getPersonName().compareTo(u2.getPersonName());
    			}
    		});
    		records = users.iterator();
        }
        else if (name.equals("conceptSet")) {
            if (conceptSet == null)
                throw new IllegalArgumentException("Must specify conceptSet");
            Concept c = OpenmrsUtil.getConceptByIdOrName(conceptSet);
            if (c == null)
                throw new IllegalArgumentException("Can't find conceptSet " + conceptSet);
            List<Concept> list = Context.getConceptService().getConceptsByConceptSet(c);
            records = list.iterator();
        }
        else if (name.equals("answer")) {
            if (concept == null)
                throw new IllegalArgumentException("Must specify concept");
            Concept c = OpenmrsUtil.getConceptByIdOrName(concept);
            if (c == null)
                throw new IllegalArgumentException("Can't find concept " + concept);
            if (c.getAnswers() != null)
                records = c.getAnswers().iterator();
            else
                records = new ArrayList<Concept>().iterator();
        }
        else {
            log.error(name + " not found in ForEachRecord list");
        }
            
        if (records == null || records.hasNext() == false) {
            records = null;
            return SKIP_BODY;
        }
        else
            return EVAL_BODY_BUFFERED;
        
    }

    /**
     * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
     */
    public void doInitBody() throws JspException {
        if (records.hasNext()) {
            Object obj = records.next();
            iterate(obj);
        }
    }

    /**
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException {
        if(records.hasNext()) {
            Object obj = records.next();
            iterate(obj);
            return EVAL_BODY_BUFFERED;
        }
        else
            return SKIP_BODY;
    }
    
    @SuppressWarnings("unchecked")
    private void iterate(Object obj) {
        if (obj != null) {
            if (name.equals("gender")) {
                Map.Entry<String, String> e = (Map.Entry<String, String>)obj;
                e.setValue(e.getValue().toLowerCase());
                obj = e;
            }
            pageContext.setAttribute("record", obj);
            
            boolean isSelected = obj.equals(select) || (select instanceof Collection && ((Collection)select).contains(obj));
            pageContext.setAttribute("selected", isSelected ? "selected" : "");
            
            if (name.equals("civilStatus")) { //Kludge until this in the db and not a HashMap
                String str = obj.toString();
                pageContext.setAttribute("selected", str.equals(select) ? "selected" : "");
            }
        }
        else {
            pageContext.removeAttribute("record");
            pageContext.removeAttribute("selected");
        }
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        try
        {
            if(getBodyContent() != null && records != null)
                getBodyContent().writeOut(getBodyContent().getEnclosingWriter());
        }
        catch(java.io.IOException e)
        {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the select.
     */
    public Object getSelect() {
        return select;
    }

    /**
     * @param select The select to set.
     */
    public void setSelect(Object select) {
        this.select = select;
    }

    public String getReportObjectType() {
        return reportObjectType;
    }

    public void setReportObjectType(String reportObjectType) {
        this.reportObjectType = reportObjectType;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getConceptSet() {
        return conceptSet;
    }

    public void setConceptSet(String conceptSet) {
        this.conceptSet = conceptSet;
    }
    public String getFilterList(){
        return this.filterList;
    }
    
    public void setFilterList(String str){
        this.filterList = str;
    }

	public String getProgramName() {
		return programName;
	}
	
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	public String getWorkflowNames() {
		return workflowNames;
	}
	
	public void setWorkflowNames(String workflowNames) {
		this.workflowNames = workflowNames;
	}
}
