<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/labmodule/dotsHeader.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/mdrtb/mdrtbListPatients.form" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
		
		$j('#patientTable').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 20,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		});

		$j('#displayDetailsPopup').dialog({ 
			title: 'dynamic', 
			autoOpen: false, 
			draggable: false, 
			resizable: false, 
			width: '95%', 
			modal: true, 
			open: function(a, b) { $j('#displayDetailsPopupLoading').show(); } 
		});
		$j("#displayDetailsPopupIframe").load(function() { $j('#displayDetailsPopupLoading').hide(); });
		
	});

	function loadUrlIntoDetailsPopup(title, urlToLoad) { 
		$j("#displayDetailsPopupIframe").attr("src", urlToLoad); 
		$j('#displayDetailsPopup').dialog('option', 'title', title).dialog('option', 'height', $j(window).height() - 50).dialog('open'); 
	}

</script>

<style>
	th {text-align:left;}
	th.patientTable,td.patientTable {text-align:center; white-space:nowrap; border: 1px solid black; font-size:small; padding-left:5px; padding-right:5px;}
</style>

<form method="get">

	<table class="patientTable" width="100%">
		<tr>
			<td width="25%" valign="top" class="patientTable">
				<table>
					<tr style="border-bottom:2px solid black; height:25px;"><td colspan="2" style="padding-left:10px; background-color:#C0C0C0; font-weight:bold;">
						<spring:message code="mdrtb.choosePatientsToDisplay" text="Choose patients to display"/>:
						<input type="submit" value="<spring:message code="mdrtb.listPatients" text="List"/>"/>
					</td></tr>
					<tr><td colspan="2">
						<br/>
						<table>
							<tr>
								<th><spring:message code="labmodule.oblast"/>:&nbsp;</th>
							    <td>
							    	<select name="oblast" style="width: 220px">
									 <option value=""></option>
									 <c:forEach items="${oblasts}" var="o">
										<option value="${o.id}">${o.name}</option>
									 </c:forEach>
									</select>
							    </td>
							</tr>
							<tr>
								<th><spring:message code="labmodule.district"/>:&nbsp;</th>
								<td>
									<select id="distict" name="district" style="width: 220px">
									<option value=""></option>
									<c:forEach var="location" items="${locations}">
										<option value="${location.locationId}">${location.displayString}</option>
									</c:forEach>
									</select>
								</td>
							</tr>
						</table>
						<br/>
					</td></tr>
					
					<tr>
					<th><spring:message code="labmodule.report.ageRange"/>
						<table>
							<tr>
								<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
								<td><input type="number" name="age_from" min="0"/></td>
							</tr>
							<tr>
								<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
								<td><input type="number" name="age_to" min="0"/></td>
							</tr>
						</table>
					
					<br/>
					</tr>
					
					<tr>
					<th colspan="2"><spring:message code="mdrtb.gender" text="Gender"/>&nbsp;
					<select name="gender" >:
					<option value=""></option>
					<option value="M"><spring:message code="mdrtb.male" text="Male"/></option>
					<option value="F"><spring:message code="mdrtb.female" text="Female"/></option>
					</select>
					<br/>
					</th>
					</tr>
					
					<tr>
					<td>
					
					<table>
							<tr>
								<th><br><u><spring:message code="labmodule.report.labData" text="Lab Data"/></u></th>
							</tr>
							<tr>
								<th> <spring:message code="labmodule.lab" text="Lab Name"/>:
									<select id="lab" name="lab">
										<option value=""></option>
										<c:forEach var="lab" items="${labs}">
											<option value="${lab.locationId}">${lab.displayString}</option>
										</c:forEach>
									</select>
								</th>
							</tr>
							
							<tr>
							 <td><br></td>
							</tr>
							
							<tr>
								<th> <spring:message code="labmodule.report.dateOfRequest" text="Date of Request"/> 
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="date_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="date_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							
							<tr>
							 <td><br></td>
							</tr>
							
							<tr>
								<th> <spring:message code="labmodule.labEntry.requestingLabName" text="requesting medical facility"/>: 
							    <br>
							    <select id="requestingLabName" name="requestingLabName" style="width: 190px">
										<option value=""></option>
										<c:forEach var="requestingFacility" items="${requestingFacilities}">
											<option value="${requestingFacility.answerConcept.id}">${requestingFacility.answerConcept.displayString}</option>
										</c:forEach>
								</select>
								<br/>
								</th>
							</tr>
							
							<tr>
							 <td><br></td>
							</tr>
							
							<tr>
								<th> <spring:message code="labmodule.labEntry.investigationPurpose"/>:
								<br>
								<select id="investigationPurpose" name="investigationPurpose">
								<option selected value=""></option>
									<c:forEach var="purpose" items="${investigationPurposes}">
										<option value="${purpose.answerConcept.id}">${purpose.answerConcept.displayString}</option>
									</c:forEach>
								</select>
								<br/>
								</th>
							</tr>
							
							<tr>
							 <td><br></td>
							</tr>
							
							<tr>
							<th> 
								<spring:message code="labmodule.labEntry.biologicalSpecimen"/>:
								<br>
								<select id="biologicalSpecimen" name="biologicalSpecimen">
									<option selected value=""></option>
										<c:forEach var="type" items="${types}">
											<option value="${type.answerConcept.id}">${type.answerConcept.displayString}</option>
										</c:forEach>
								</select>
							</th>
							</tr>
							
							<tr>
							 <td><br></td>
							</tr>
						</table>
					
					</td>
					</tr>
					
					<tr>
					<td>
					
					<table>
							<tr>
								<th>
									<input type="checkbox" name="peripheral" id="peripheral" value="peripheral" onClick="onClick(this)"> <u><spring:message code="labmodule.labEntry.peripheralLabInvestigation"/></u>
								</th>
							</tr>
							<tr id = "peripheral_columns_1" style="display:none;">
								<th> 
									<spring:message code="labmodule.labEntry.peripheralLabNo"/>:
									<br>
									<input type="text"  size="10" id="peripheralLabNo" name="peripheralLabNo">	
								</th>
							</tr>
							<tr id = "peripheral_columns_2" style="display:none;">
							  <td>
							   <br>
							  <td>
							<tr>
							<tr id = "peripheral_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.microscopyResult"/>:
									<br>
									<select  id=microscopyResult name=microscopyResult style="width: 220px">
									     <option selected value="" ></option>
											<c:forEach var="result" items="${microscopyResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
							<tr id = "peripheral_columns_4" style="display:none;">
							  <td>
							   <br>
							  <td>
							<tr>
							<tr id = "peripheral_columns_5" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.dateResult"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="result_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="result_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							
							<tr>
							<th>
								<br>
							</th>
							</tr>
					
					</table>
					
					</td>
					</tr>
					
					<tr>
					<td>
						<table>
							<tr>
								<th>
									<u><spring:message code="labmodule.report.labTestResult"/></u>
								</th>
							</tr>
							<tr>
								<th>
									<input type="checkbox" name="microscopy" id="microscopy" value="microscopy" onClick="onClick(this)"> <spring:message code="labmodule.labEntry.bacterioscopy"/>
								</th>
							</tr>
							<tr id="microscopy_columns_1" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.testDate"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="microscopy_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="microscopy_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							<tr id="microscopy_columns_2" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr  id="microscopy_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:
									&nbsp;
									<select id="sampleAppearance" name="sampleAppearance">
									<option selected value=""></option>
									<c:forEach var="appearance" items="${appearances}">
										<option value="${appearance.answerConcept.id}">${appearance.answerConcept.displayString}</option>
									</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="microscopy_columns_4" style="display:none;">
								<td>
									<br>
								</td>
							</tr>	
							<tr id="microscopy_columns_5" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.bacterioscopy.result"/>:&nbsp;
									<select id="sampleResult" name=sampleResult>
										     <option selected value=""></option>
												<c:forEach var="result" items="${microscopyResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="microscopy_columns_6" style="display:none;">
								<td>
									<br>
								</td>
							</tr>		
							
							<tr>
								<th>
									<input type="checkbox" name="xpert" id="xpert" value="xpert" onClick="onClick(this)"> <spring:message code="labmodule.labEntry.xpert"/>
								</th>
							</tr>
							<tr id="xpert_columns_1" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.testDate"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="xpert_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="xpert_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							<tr id="xpert_columns_2" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr id="xpert_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.xpert.mtb"/>: &nbsp;
									<select id="mtbXpertResult" name="mtbXpertResult">
									     <option selected value=""></option>
											<c:forEach var="result" items="${mtbResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="xpert_columns_4" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="xpert_columns_5" style="display:none;">
								<th>
								<spring:message code="labmodule.labEntry.xpert.r"/>:&nbsp;
								<select id = "rifXpertResult" name = "rifResult"">
										<option selected value=""></option>
											<c:forEach var="result" items="${rifResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="xpert_columns_6" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="xpert_columns_7" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.xpert.errorCode"/>:&nbsp;
									<input type="text"  size="10" name="xpertError" id="xpertError">
								</th>
							</tr>
							<tr id="xpert_columns_8" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr>
								<th>
									<input type="checkbox" name="hain" id="hain" value="hain" onClick="onClick(this)"> <spring:message code="labmodule.labEntry.hain"/>
								</th>
							</tr>
							<tr id="hain_columns_1" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.testDate"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="hain_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="hain_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							<tr id="hain_columns_2" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr id="hain_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.xpert.mtb"/>: &nbsp;
									<select id="mtbHainResult" name="mtbHainResult">
									     <option selected value=""></option>
											<c:forEach var="result" items="${mtbResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="hain_columns_4" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="hain_columns_5" style="display:none;">
								<th>
								<spring:message code="labmodule.labEntry.xpert.r"/>: &nbsp;
								<select id = "rifHainResult" name = "rifHainResult">
									<option selected value=""></option>
										<c:forEach var="result" items="${rifResults}">
											<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
										</c:forEach>
								</select>
								</th>
							</tr>
							<tr id="hain_columns_6" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr id="hain_columns_7" style="display:none;">
								<th>
								<spring:message code="labmodule.labEntry.xpert.h"/>: &nbsp;
									<select id = "inhHainResult" name = "inhHainResult">
											<option selected value=""></option>
												<c:forEach var="result" items="${inhResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
								</th>
							</tr>
							<tr id="hain_columns_8" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr>
								<th>
									<input type="checkbox" name="hain2" id="hain2" value="hain2" onClick="onClick(this)"> <spring:message code="labmodule.labEntry.hain2"/>
								</th>
							</tr>
							<tr id="hain2_columns_1" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.hain2.analysisDate"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="hain2_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="hain2_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							<tr id="hain2_columns_2" style="display:none;">
								<td>
									<br>
								</td>
							</tr>
							<tr id="hain2_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.xpert.mtb"/>: &nbsp;
									<select id="mtbHain2Result" name="mtbHain2Result">
									     <option selected value=""></option>
											<c:forEach var="result" items="${mtbResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
							<tr id="hain2_columns_4" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="hain2_columns_5" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.hain2.mox"/>: &nbsp;
									<select id="moxHain2Result" name="moxHain2Result">
										     <option selected value=""></option>
												<c:forEach var="result" items="${moxResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
								</th>
							</tr>
							<tr id="hain2_columns_6" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="hain2_columns_7" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.hain2.km"/>: &nbsp;
									<select id="cmHain2Result" name="cmHain2Result">
										     <option selected value=""></option>
												<c:forEach var="result" items="${cmResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>

								</th>
							</tr>
							<tr id="hain2_columns_8" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							<tr id="hain2_columns_9" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.hain2.e"/>: &nbsp;
									<select id="erHain2Result" name="erHain2Result" >
										     <option selected value=""></option>
												<c:forEach var="result" items="${eResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
								</th>
							</tr>
							<tr id="hain2_columns_10" style="display:none;">
								<td>
							 		<br>
								</td>
							</tr>
							
							<tr>
								<th>
									<input type="checkbox" name="culture" id="culture" value="culture" onClick="onClick(this)"> <spring:message code="labmodule.culture"/>
								</th>
							</tr>
							<tr id="culture_columns_1" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.testDate"/>:
									<table>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.from"/>:</th>
											<td><openmrs_tag:dateField formFieldName="culture_from" startValue=""/></td>
										</tr>
										<tr>
											<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="labmodule.report.to"/>:</th>
											<td><openmrs_tag:dateField formFieldName="culture_to" startValue=""/></td>
										</tr>
									</table>
								</th>
							</tr>
							<tr id="culture_columns_2" style="display:none;">
							<th>
								<br>
							</th>
							</tr>
							<tr id="culture_columns_3" style="display:none;">
								<th>
									<spring:message code="labmodule.labEntry.culture.result"/>:
									<br>
									<select  id=cultureResult name=cultureResult style="width: 220px">
									     <option selected value="" ></option>
											<c:forEach var="result" items="${microscopyResults}">
												<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
											</c:forEach>
									</select>
								</th>
							</tr>
						</table>
					</td>
					</tr>
					
				</table>
			</td>
			<td valign="top" width="100%" class="patientTable" style="padding-left:10px;">
				<table width="100%">
					<tr style="border-bottom:2px solid black;"><td colspan="2" style="background-color:#C0C0C0; font-weight:bold;">
						<spring:message code="labmodule.chooseColumnsToDisplay" text="Choose columns to display"/>: 
						<select name="displayMode">
							<option value="basic"<c:if test="${'basic' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.basicDetails"/></option>
							<%-- <option value="mdrtbShortSummary"<c:if test="${'mdrtbShortSummary' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.mdrtbShortSummary"/></option>
							<option value="mdrtbCustomList"<c:if test="${'mdrtbCustomList' == param.displayMode}"> selected</c:if>><spring:message code="mdrtb.mdrtbCustomList"/></option>
							 --%>
							<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDisplayModes" type="html">
								<option value="${extension.key}"<c:if test="${extension.key == param.displayMode}"> selected</c:if>>
									<spring:message code="${extension.label}"/>
								</option>
							</openmrs:extensionPoint>
						</select>
						<input type="submit" value="<spring:message code="mdrtb.listPatients" text="List"/>"/>
					</td></tr>
				</table>
				<br/>
				<c:set var="extensionFound" value="false"/>
				<openmrs:extensionPoint pointId="org.openmrs.mdrtb.listPatientDisplayModes" type="html">
					<c:if test="${extension.key == param.displayMode}">
						<c:set var="extensionFound" value="true"/>
						<openmrs:portlet moduleId="${extension.moduleId}" url="${extension.portletUrl}" patientIds="${patientIds}" />
					</c:if>
				</openmrs:extensionPoint>
				<c:if test="${extensionFound != 'true'}">
					<c:choose>
						<c:when test="${param.displayMode == 'mdrtbSummary'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbPatientSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'mdrtbShortSummary'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbShortSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'mdrtbCustomList'}">
							<openmrs:portlet moduleId="mdrtb" url="mdrtbCustomList" patientIds="${patientIds}" />
						</c:when>
						<c:otherwise>
							<table id="patientTable">
								<thead>
									<tr>
										<th rowspan="3">&nbsp;</th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.name"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="mdrtb.collectedBy"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="mdrtb.lab"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.sampleid"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="mdrtb.dateCollected"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.requestingLabName"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.investigationPurpose"/></th>
										<th rowspan="3" class="patientTable" style="padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.biologicalSpecimen"/></th>
										<th colspan="3" class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.peripheralLabInvestigation"/></th>
										<th colspan="9" class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy"/></th>
										<th colspan="4" class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert"/></th>
										<th colspan="4" class="patientTable" style="display: ${(hain == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.hain"/></th>
										<th colspan="5" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.hain2"/></th>
										<th colspan="2" class="patientTable" style="display: ${(culture == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.culture"/></th>
									</tr>	
									<tr>	
										<th class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.peripheralLabNo"/></th>		
										<th class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.microscopyResult"/></th>		
										<th class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.dateResult"/></th>	
										<th colspan="3" class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.bacterioscopy.sample1"/></th>
										<th colspan="3" class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.bacterioscopy.sample2"/></th>
										<th colspan="3" class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.bacterioscopy.sample3"/></th>
										<th rowspan="2" class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.testDate"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.mtb"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.r"/></th>	
										<th rowspan="2" class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.errorCode"/></th>
										<th rowspan="2" class="patientTable" style="display: ${(hain == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.testDate"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(hain == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.mtb"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(hain == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.r"/></th>	
										<th rowspan="2" class="patientTable" style="display: ${(hain == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.h"/></th>
										<th rowspan="2" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;" ><spring:message code="labmodule.labEntry.hain2.analysisDate"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.xpert.mtb"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.hain2.mox"/></th>	
										<th rowspan="2" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.hain2.km"/></th>
										<th rowspan="2" class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.hain2.e"/></th>		
										<th rowspan="2" class="patientTable" style="display: ${(culture == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.testDate"/></th>
										<th rowspan="2" class="patientTable" style="display: ${(culture == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.cultureResult"/></th>		
									</tr>
									<tr>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.testDate"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.result"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.testDate"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.result"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.testDate"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/></th>
										<th class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; padding-left: 10px !important; padding-right: 10px !important; border: 1px solid black;"><spring:message code="labmodule.labEntry.bacterioscopy.result"/></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${data}" var="p">
										<tr class="patientRow patientRow${p.patientId}">
											<td class="patientTable" style="white-space:nowrap; width:20px;">
												<a href="${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form?patientId=${p.patientId}&labResultId=${p.encounterId}">
													<img src="${pageContext.request.contextPath}/images/lookup.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
												</a>
											</td>
											<td class="patientTable" style="white-space:nowrap;">${p.name}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.collectedBy}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.location}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.testId}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.dateRecieve}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.requestingMedicalFacility}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.investigationPurpose}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.biologicalSpecimen}</td>
											<td class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; white-space:nowrap;"  >${p.peripheralLabNo}</td>
											<td class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; white-space:nowrap;" ">${p.microscopyResult}</td>
											<td class="patientTable" style="display: ${(peripheral == null) ? 'none': ''}; white-space:nowrap;" >${p.dateResult}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.microscopyResultDate_1}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleAppearence_1}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleResult_1}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.microscopyResultDate_2}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleAppearence_2}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleResult_2}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.microscopyResultDate_3}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleAppearence_3}</td>
											<td class="patientTable" style="display: ${(microscopy == null) ? 'none': ''}; white-space:nowrap;">${p.sampleResult_3}</td>
											
											<td class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; white-space:nowrap;">${p.xpertResultDate}</td>
											<td class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; white-space:nowrap;">${p.xpertMtbResult}</td>
											<td class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; white-space:nowrap;">${p.xpertRifResult}</td>
											<td class="patientTable" style="display: ${(xpert == null) ? 'none': ''}; white-space:nowrap;">${p.xpertErrorCode}</td>
											<td class="patientTable" style="display: ${(hain == null) ? 'none': ''}; white-space:nowrap;">${p.hainResultDate}</td>
											<td class="patientTable" style="display: ${(hain == null) ? 'none': ''}; white-space:nowrap;">${p.hainMtbResult}</td>
											<td class="patientTable" style="display: ${(hain == null) ? 'none': ''}; white-space:nowrap;">${p.hainRifResult}</td>
											<td class="patientTable" style="display: ${(hain == null) ? 'none': ''}; white-space:nowrap;">${p.hainInhResult}</td>
											<td class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; white-space:nowrap;">${p.hain2ResultDate}</td>
											<td class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; white-space:nowrap;">${p.hain2MtbResult}</td>
											<td class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; white-space:nowrap;" >${p.hain2MoxResult}</td>
											<td class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; white-space:nowrap;">${p.hain2CmResult}</td>
											<td class="patientTable" style="display: ${(hain2 == null) ? 'none': ''}; white-space:nowrap;">${p.hain2ErResult}</td>
											<td class="patientTable" style="display: ${(culture == null) ? 'none': ''}; white-space:nowrap;">${p.cultureResultDate}</td>
											<td class="patientTable" style="display: ${(culture == null) ? 'none': ''}; white-space:nowrap;">${p.cultureResult}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</c:if>
			</td>
		</tr>
	</table>
</form>

<!-- <div id="displayDetailsPopup"> 
	<div id="displayDetailsPopupLoading"><spring:message code="general.loading"/></div>
	<iframe id="displayDetailsPopupIframe" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe> 
</div> -->

<%@ include file="/WEB-INF/view/module/labmodule/dotsFooter.jsp"%>

<script>

function onClick(element) {

	if(element.id == 'peripheral'){
		if(element.checked){
			document.getElementById("peripheral_columns_1").style.display = "";
			document.getElementById("peripheral_columns_2").style.display = "";
			document.getElementById("peripheral_columns_3").style.display = "";
			document.getElementById("peripheral_columns_4").style.display = "";
			document.getElementById("peripheral_columns_5").style.display = "";
		}
		else{
			document.getElementById("peripheral_columns_1").style.display = "none";
			document.getElementById("peripheral_columns_2").style.display = "none";
			document.getElementById("peripheral_columns_3").style.display = "none";
			document.getElementById("peripheral_columns_4").style.display = "none";
			document.getElementById("peripheral_columns_5").style.display = "none";
		}	
	}
	else if(element.id == 'microscopy'){
		if(element.checked){
			document.getElementById("microscopy_columns_1").style.display = "";
			document.getElementById("microscopy_columns_2").style.display = "";
			document.getElementById("microscopy_columns_3").style.display = "";
			document.getElementById("microscopy_columns_4").style.display = "";
			document.getElementById("microscopy_columns_5").style.display = "";
			document.getElementById("microscopy_columns_6").style.display = "";
		}
		else{
			document.getElementById("microscopy_columns_1").style.display = "none";
			document.getElementById("microscopy_columns_2").style.display = "none";
			document.getElementById("microscopy_columns_3").style.display = "none";
			document.getElementById("microscopy_columns_4").style.display = "none";
			document.getElementById("microscopy_columns_5").style.display = "none";
			document.getElementById("microscopy_columns_6").style.display = "none";
		}	
	}
	else if(element.id == 'xpert'){
		if(element.checked){
			document.getElementById("xpert_columns_1").style.display = "";
			document.getElementById("xpert_columns_2").style.display = "";
			document.getElementById("xpert_columns_3").style.display = "";
			document.getElementById("xpert_columns_4").style.display = "";
			document.getElementById("xpert_columns_5").style.display = "";
			document.getElementById("xpert_columns_6").style.display = "";
			document.getElementById("xpert_columns_7").style.display = "";
			document.getElementById("xpert_columns_8").style.display = "";
		}
		else{
			document.getElementById("xpert_columns_1").style.display = "none";
			document.getElementById("xpert_columns_2").style.display = "none";
			document.getElementById("xpert_columns_3").style.display = "none";
			document.getElementById("xpert_columns_4").style.display = "none";
			document.getElementById("xpert_columns_5").style.display = "none";
			document.getElementById("xpert_columns_6").style.display = "none";
			document.getElementById("xpert_columns_7").style.display = "none";
			document.getElementById("xpert_columns_8").style.display = "none";
		}	
	}
	else if(element.id == 'hain'){
		if(element.checked){
			document.getElementById("hain_columns_1").style.display = "";
			document.getElementById("hain_columns_2").style.display = "";
			document.getElementById("hain_columns_3").style.display = "";
			document.getElementById("hain_columns_4").style.display = "";
			document.getElementById("hain_columns_5").style.display = "";
			document.getElementById("hain_columns_6").style.display = "";
			document.getElementById("hain_columns_7").style.display = "";
			document.getElementById("hain_columns_8").style.display = "";
		}
		else{
			document.getElementById("hain_columns_1").style.display = "none";
			document.getElementById("hain_columns_2").style.display = "none";
			document.getElementById("hain_columns_3").style.display = "none";
			document.getElementById("hain_columns_4").style.display = "none";
			document.getElementById("hain_columns_5").style.display = "none";
			document.getElementById("hain_columns_6").style.display = "none";
			document.getElementById("hain_columns_7").style.display = "none";
			document.getElementById("hain_columns_8").style.display = "none";
		}	
	}
	else if (element.id == 'hain2'){
		if(element.checked){
			document.getElementById("hain2_columns_1").style.display = "";
			document.getElementById("hain2_columns_2").style.display = "";
			document.getElementById("hain2_columns_3").style.display = "";
			document.getElementById("hain2_columns_4").style.display = "";
			document.getElementById("hain2_columns_5").style.display = "";
			document.getElementById("hain2_columns_6").style.display = "";
			document.getElementById("hain2_columns_7").style.display = "";
			document.getElementById("hain2_columns_8").style.display = "";
			document.getElementById("hain2_columns_9").style.display = "";
			document.getElementById("hain2_columns_10").style.display = "";
		}
		else{
			document.getElementById("hain2_columns_1").style.display = "none";
			document.getElementById("hain2_columns_2").style.display = "none";
			document.getElementById("hain2_columns_3").style.display = "none";
			document.getElementById("hain2_columns_4").style.display = "none";
			document.getElementById("hain2_columns_5").style.display = "none";
			document.getElementById("hain2_columns_6").style.display = "none";
			document.getElementById("hain2_columns_7").style.display = "none";
			document.getElementById("hain2_columns_8").style.display = "none";
			document.getElementById("hain2_columns_9").style.display = "none";
			document.getElementById("hain2_columns_10").style.display = "none";
		}	
	}
	else if (element.id == 'culture'){
		if(element.checked){
			document.getElementById("culture_columns_1").style.display = "";
			document.getElementById("culture_columns_2").style.display = "";
			document.getElementById("culture_columns_3").style.display = "";
		}
		else{
			document.getElementById("culture_columns_1").style.display = "none";
			document.getElementById("culture_columns_2").style.display = "none";
			document.getElementById("culture_columns_3").style.display = "none";
		}	
	}
}

</script>
