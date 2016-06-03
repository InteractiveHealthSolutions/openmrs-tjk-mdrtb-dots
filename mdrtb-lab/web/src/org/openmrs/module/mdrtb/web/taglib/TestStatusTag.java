package org.openmrs.module.mdrtb.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mdrtb.specimen.Test;
import org.openmrs.module.mdrtb.web.util.TestStatusRenderer;


public class TestStatusTag extends TagSupport {

    private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());
	
	private Test test;
	
	private List<Test> tests;
	
	private String type;

	public int doStartTag() {
	
		String ret = "";
		
		if (test !=null) {
			if(type == null || type.equalsIgnoreCase("STANDARD")) {
				ret = TestStatusRenderer.renderStandardStatus(test);
			}
			else if(type.equalsIgnoreCase("SHORT")) {
				ret = TestStatusRenderer.renderShortStatus(test);
			}
			else {
				log.warn("Invalid value for attribute type of testStatus tag. Rendering standard type");
			}
		}	
		else if (tests != null && !tests.isEmpty()) {
			ret = TestStatusRenderer.renderGroupShortStatus(tests);
			
			if (!type.equalsIgnoreCase("SHORT")) {
				log.warn("Invalid value for attribute type of testStatus tag. Only short type allowed for groups of tests. Rendering standard type.");
			}
		}
			
		try {
			JspWriter w = pageContext.getOut();
			w.println(ret);
		}
		catch (IOException ex) {
			log.error("Error while starting dst result tag", ex);
		}
    	
    	return SKIP_BODY;
	}
	
	public int doEndTag() {
	    test = null;
	    tests = null;
	    type = null;
	    return EVAL_PAGE;
	}
	
	
	public void setTest(Test test) {
	    this.test = test;
    }

	public Test getTest() {
	    return test;
    }

	public void setType(String type) {
	    this.type = type;
    }

	public String getType() {
	    return type;
    }

	public void setTests(List<Test> tests) {
	    this.tests = tests;
    }

	public List<Test> getTests() {
	    return tests;
    } 	
}
