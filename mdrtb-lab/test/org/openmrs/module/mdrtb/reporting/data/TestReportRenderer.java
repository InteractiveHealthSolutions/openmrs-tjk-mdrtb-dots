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
package org.openmrs.module.mdrtb.reporting.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
public class TestReportRenderer extends SimpleHtmlReportRenderer {
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new PrintWriter(out);
		for (String key : results.getDataSets().keySet()) {
			w.write(key + ":\n");
			DataSet dataset = results.getDataSets().get(key);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();

			if (dataset instanceof MapDataSet) {
				DataSetRow data = dataset.iterator().next();
				if (dataset.getDefinition() instanceof CohortCrossTabDataSetDefinition) {
					CohortCrossTabDataSetDefinition cdd = (CohortCrossTabDataSetDefinition) dataset.getDefinition();

					List<String> rows = new ArrayList<String>(cdd.getRows().keySet());
					List<String> cols = new ArrayList<String>(cdd.getColumns().keySet());
					
					if (rows.isEmpty()) {
						for (String colName : cols) {
							w.write(colName + ": ");
							Object colValue = data.getColumnValue(colName);
							if (colValue != null) {
								w.write(Integer.toString(((Cohort) colValue).size()));
							}
							w.write("\n");
						}
					}
					else if (cols.isEmpty()) {
						for (String rowName : rows) {
							w.write(rowName + ": ");
							Object colValue = data.getColumnValue(rowName);
							if (colValue != null) {
								w.write(Integer.toString(((Cohort) colValue).size()));
							}
							w.write("\n");
						}
					}
					else {
						w.write("\t");
						for (String colName : cols) {
							w.write(colName + "\t");
						}
						w.write("\n");
						for (String rowName : rows) {
							w.write(rowName + "\t");
							for (String colName : cols) {
								String dataKey = ObjectUtil.decodeStr(rowName, "", rowName + ".") + colName;
								Object colValue = data.getColumnValue(dataKey);
								if (colValue != null) {
									if (colValue instanceof Cohort) {
										w.write(Integer.toString(((Cohort) colValue).size()));
									} else {
										w.write(colValue.toString());
									}
								}
								w.write("\t");
							}
							w.write("\n");
						}
					}
				}
				else {
					for (DataSetColumn column : columns) {
						w.write(column.getLabel() + ": ");
						Object colValue = data.getColumnValue(column);
						if (colValue != null) {
							if (colValue instanceof Cohort) {
								w.write(Integer.toString(((Cohort) colValue).size()));
							} else {
								w.write(colValue.toString());
							}
						}
						w.write("\n");
					}
				}
			}
			else {
				for (DataSetColumn column : columns) {
					w.write(column.getName()+"\t");
				}
				w.write("\n");

				for (DataSetRow row : dataset) {
					for (DataSetColumn column : columns) {
						Object colValue = row.getColumnValue(column.getName());
						if (colValue != null) {
							if (colValue instanceof Cohort) {
								w.write(Integer.toString(((Cohort) colValue).size()));
							} else {
								w.write(colValue.toString());
							}
						}
						w.write("\t");
					}
					w.write("\n");
				}
			}
		}
		w.flush();
	}
}
