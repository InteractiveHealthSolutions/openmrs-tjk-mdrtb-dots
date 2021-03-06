/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.arden;

import java.io.Writer;
import java.util.ArrayList;

/**
 * This class translates the CALL function of an MLM into a context.eval() of a rule
 */
public class Call {
	
	private String callVar = null;
	
	private String callMethod = null;
	
	private ArrayList<String> parameters = null;
	
	public Call(String callVar, String callMethod) {
		this.callVar = callVar;
		this.callMethod = callMethod;
		this.parameters = new ArrayList<String>();
	}
	
	public String getCallVar() {
		return callVar;
	}
	
	public void setCallVar(String callVar) {
		this.callVar = callVar;
	}
	
	public String getCallMethod() {
		return callMethod;
	}
	
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}
	
	public void write(Writer w) {
		try {
			
			for (int i = 0; i < parameters.size(); i++) {
				String currParam = parameters.get(i);
				w.append("\t\t\t\tvarLen = " + "\"" + currParam + "\"" + ".length();\n");
				
				w.append("\t\t\t\tvalue=userVarMap.get(" + "\"" + currParam + "\"" + ");\n");
				w.append("\t\t\t\tif(value != null){\n");
				w.append("\t\t\t\t\tparameters.put(\"param" + (i + 1) + "\"," + "value);\n");
				w.append("\t\t\t\t}\n");
				
				w.append("\t\t\t\t// It must be a result value or date\n");
				w.append("\t\t\t\telse if(" + "\"" + currParam + "\"" + ".endsWith(\"_value\"))\n");
				w.append("\t\t\t\t{\n");
				w.append("\t\t\t\t\tvariable = " + "\"" + currParam + "\"" + ".substring(0, varLen-6); // -6 for _value\n");
				w.append("\t\t\t\t\tif (resultLookup.get(variable) != null){\n");
				w.append("\t\t\t\t\t\tvalue = resultLookup.get(variable).toString();\n");
				w.append("\t\t\t\t\t}\n");
				w.append("\t\t\t\t}\n");
				w.append("\t\t\t\telse if(" + "\"" + currParam + "\"" + ".endsWith(\"_date\"))\n");
				w.append("\t\t\t\t{\n");
				w.append("\t\t\t\t\tvariable = " + "\"" + currParam + "\"" + ".substring(0, varLen-5); // -5 for _date\n");
				w.append("\t\t\t\t\tif (resultLookup.get(variable) != null){\n");
				w.append("\t\t\t\t\t\tvalue = resultLookup.get(variable).getResultDate().toString();\n");
				w.append("\t\t\t\t\t}\n");
				w.append("\t\t\t\t}\n");
				w.append("\t\t\t\telse if(" + "\"" + currParam + "\"" + ".endsWith(\"_object\"))\n");
				w.append("\t\t\t\t{\n");
				w.append("\t\t\t\t\tvariable = " + "\"" + currParam + "\"" + ".substring(0, varLen-7); // -5 for _object\n");
				w.append("\t\t\t\t\tif (resultLookup.get(variable) != null){\n");
				w.append("\t\t\t\t\t\tvalue = resultLookup.get(variable);\n");
				w.append("\t\t\t\t\t}\n");
				w.append("\t\t\t\t}\n");
				w.append("\t\t\t\telse\n");
				w.append("\t\t\t\t{\n");
				w.append("\t\t\t\t\tif (resultLookup.get(" + "\"" + currParam + "\"" + ") != null){\n");
				w.append("\t\t\t\t\t\tvalue = resultLookup.get(" + "\"" + currParam + "\"" + ").toString();\n");
				w.append("\t\t\t\t\t}\n");
				w.append("\t\t\t\t}\n");
				w.append("\t\t\t\tif(value != null){\n");
				w.append("\t\t\t\t\tparameters.put(\"param" + (i + 1) + "\"," + "value);\n");
				w.append("\t\t\t\t}\n");
				
				w.append("\t\t\t\telse\n");
				w.append("\t\t\t\t{\n");
				w.append("\t\t\t\t\tparameters.put(\"param" + (i + 1) + "\",\"" + currParam + "\");\n");
				w.append("\t\t\t\t}\n");
				
			}
			
			w.append("\t\t\t\t");
			w.append("if (ruleProvider != null) {\n");
			w.append("\t\t\t\t\t");
			w.append("ruleProvider.getRule(\"" + getCallMethod() + "\");\n");
			w.append("\t\t\t\t");
			w.append("}\n");
			w.append("\t\t\t\t");
			if (getCallVar() != null && getCallVar().length() > 0) {
				w.append("Result " + getCallVar() + " = ");
			}

			w.append("context.eval(patient.getPatientId(), \"" + getCallMethod() + "\",parameters);\n");
			w.append("\t\t\t\t");
			if (getCallVar() != null && getCallVar().length() > 0) {
				w.append("resultLookup.put(\"" + getCallVar() + "\"," + getCallVar() + ");\n");
			}
		}
		catch (Exception e) {}
	}
	
	public void addParameter(String parameter) {
		this.parameters.add(parameter);
	}
}
