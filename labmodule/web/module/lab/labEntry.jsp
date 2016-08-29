<%@ include file="/WEB-INF/view/module/labmodule/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/labmodule/dotsHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>


<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<style type="text/css">
td
{
    padding:0 10px 0 10px;
}

form {
 margin: 0;
 padding: 0;
}
</style>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>

<h3> <spring:message code="labmodule.labDataEntry" /> </h3>

<br>

<!-- LEFT-HAND COLUMN START -->
<div id="leftColumn" style=" float: left; padding:0px 4px 4px 4px; width:33%;">
 
	 <b class="boxHeader">
		&nbsp;
		<spring:message code="mdrtb.specimens" text="Specimens"/>
		<span style="float:right">
			<openmrs:hasPrivilege privilege="Add Test Result">
			<img title="Add" class="add" id="quickEntryAddButton" onclick="onClick(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			</openmrs:hasPrivilege>
		</span> 
	</b> 

	<div class="box">
		<div id="specimenList">
		
		<c:choose>
			<c:when test="${fn:length(labResults) > 0}">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
					    <openmrs:hasPrivilege privilege="Edit Test Result">
						<td></td>
						</openmrs:hasPrivilege>
						<openmrs:hasPrivilege privilege="Delete Test Result">
						<td></td>
						</openmrs:hasPrivilege>
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="mdrtb.dateCollected" text="Date Collected"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="labmodule.sampleid" text="Sample ID"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.tests" text="Tests"/></u></nobr></td>
					
					</tr>
				
					<c:forEach var="specimenListItem" items="${labResults}" varStatus="i">
						<tr 
							<c:if test="${i.count % 2 == 0 }">class="evenRow"</c:if>
							<c:if test="${i.count % 2 != 0 }">class="oddRow"</c:if>
							<c:if test="${specimenListItem.id == labResult.id}"> style="background-color : gold"</c:if>>
						
							<openmrs:hasPrivilege privilege="Edit Test Result">
							<td>
								<a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}&edit=true">
									<img title="Edit" src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</a>
							</td>
							</openmrs:hasPrivilege>
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<td> 
								<form id="delete_${i.count}" action="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}&submissionType=delete&testType=labResult" method="post">
									<img title="Delete" id='deleteTest_${i.count}_${specimenListItem.labNumber}' onclick="deleteTest(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</form>	
							</td> 
							</openmrs:hasPrivilege>
							
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}"><openmrs:formatDate date="${specimenListItem.dateCollected}" format="${_dateFormatDisplay}"/></a></nobr></td>
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">${specimenListItem.labNumber}</a></nobr></td>
							
							<td class="tableCell"><nobr><a href="labEntry.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">
							
							<c:if test="${fn:length(specimenListItem.microscopies) > 0}">
								-<spring:message code="labmodule.labEntry.bacterioscopy"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.xperts) > 0}">
								-<spring:message code="labmodule.labEntry.xpert"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS) > 0}">
								-<spring:message code="labmodule.labEntry.hainSplit"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS2) > 0}">
								-<spring:message code="labmodule.labEntry.hain2Split"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.cultures) > 0}">
								-<spring:message code="labmodule.culture"/> <br>
							</c:if>
							
							</a></nobr></td>
							
							
						</tr>
					</c:forEach>
				</table>
			</c:when>
			
			<c:otherwise>
				<spring:message code="mdrtb.noSpecimens" text="No specimen information available for this patient program."/>
			</c:otherwise>
			
		</c:choose>
		
		</div>
	</div>
	
	<br> <br>


	<b class="boxHeader">
		&nbsp;
		<spring:message code="labmodule.search.status" text="Status"/>
	</b>
	<div class="box">
		<div style="padding:10px">
			<spring:message code="labmodule.search.status" text="Status"/>:
		    <b>${status}</b>
		</div>
	</div>

</div>
<!-- END OF LEFT-HAND COLUMN -->

<div id="old_column" style="float: right; width:65%;  padding:0px 4px 4px 4px">

	<b class="boxHeader" style="margin:0px">
		&nbsp;
		<spring:message code="mdrtb.specimenDetails" text="Specimen Details"/>
		<span id="specimen_edit_span" name="specimen_edit_span" style="float:right">
			<openmrs:hasPrivilege privilege="Edit Test Result">
				<img title="Edit" id='editSpecimenDetailButton' onclick="onClick(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			</openmrs:hasPrivilege>
		</span> 
	</b>

	<div class="box" id="specimen_view" style="margin:0px">
		
		<table cellspacing="5" cellpadding="0" width="100%">
		
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="mdrtb.collectedBy" text="Collected By"/> : </font>
					&nbsp;
					${labResult.provider.personName}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					${labResult.location.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="mdrtb.labNumber" text="lab Number"/>:</font>
					&nbsp;
					${labResult.labNumber}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateCollected}" format="${_dateFormatDisplay}"/>
					&nbsp;&nbsp;
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.investigationDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
					&nbsp;
					${labResult.requestingLabName.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
					&nbsp;
					${labResult.investigationPurpose.displayString}
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					${labResult.biologicalSpecimen.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
					&nbsp;
					${labResult.comments}
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
					&nbsp;
					${labResult.peripheralLabNumber}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
					&nbsp;
					${labResult.microscopyResult.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateResult}" format="${_dateFormatDisplay}"/>
				</td>
			</tr> 
		
		</table>
	</div>
	
	<div class="box" id="specimen_edit" style="margin:0px">
		
	<form id="updateTestResults" name="updateTestResults" action="labEntry.form?patientId=${patientId}&labResultId=${labResult.id}&submissionType=specimen" method="post">
		
		<table cellspacing="5" cellpadding="0" width="100%">
		
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.collectedBy" text="Collected By"/> : </font>
					&nbsp;
					<select name="provider_e" id="provider_e">
						<c:forEach var="provider" items="${providers}">
							<option value="${provider.id}" <c:if test="${provider.id == labResult.provider.id}">selected</c:if> >${provider.personName}</option>
						</c:forEach></td>
					</select>
				</td>
			</tr>		
			
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					<select id="lab_e" name="lab_e">
							<c:forEach var="location" items="${locations}">
								<option value="${location.locationId}" <c:if test="${location.locationId == labResult.location.locationId}">selected</c:if> >${location.displayString}</option>
							</c:forEach>
					</select>
					
				</td>
			</tr>	
				
			<tr>
				<td>		
					<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.labNumber" text="lab Number"/>:</font>
					&nbsp;
					<input type="text"  size="10" id="labNumber_e" name="labNumber_e" value="${labResult.labNumber}">
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateRecieve_e" startValue="${labResult.dateCollected}"/>
				</td>
			</tr>
			
			<tr>
				<td>	
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateInvestigation_e" startValue="${labResult.investigationDate}"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
					&nbsp;
					<select id="requestingLabName_e" name="requestingLabName_e">
							<option hidden selected value=""></option>
								<c:forEach var="requestingFacility" items="${requestingFacilities}">
									<option value="${requestingFacility.answerConcept.id}" <c:if test="${requestingFacility.answerConcept == labResult.requestingLabName}">selected</c:if> >${requestingFacility.answerConcept.displayString}</option>
							</c:forEach>
					</select>
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
					&nbsp;
					<select id="investigationPurpose_e" name="investigationPurpose_e">
						<c:forEach var="purpose" items="${investigationPurposes}">
							<option value="${purpose.answerConcept.id}" <c:if test="${purpose.answerConcept == labResult.investigationPurpose}">selected</c:if> >${purpose.answerConcept.displayString}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					<select id="biologicalSpecimen_e" name="biologicalSpecimen_e">
							<c:forEach var="type" items="${types}">
								<option value="${type.answerConcept.id}" <c:if test="${type.answerConcept == labResult.biologicalSpecimen}">selected</c:if> >${type.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
					&nbsp;
					<textarea rows="4" cols="50" name="comments_e" >${labResult.comments}</textarea>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
				
					<input checked hidden type="checkbox" id="peripheral_e" name="peripheral_e" value="peripheral"/>
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
					&nbsp;
					<input type="text"  size="10" id="peripheralLabNo_e" name="peripheralLabNo_e" value="${labResult.peripheralLabNumber}">
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
					&nbsp;
					<select id=microscopyResult_e name=microscopyResult_e>
							<option selected value=""></option>
							<c:forEach var="result" items="${microscopyResults}">
								<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == labResult.microscopyResult}">selected</c:if> >${result.answerConcept.displayString}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
					&nbsp;
					<openmrs_tag:dateField formFieldName="dateResult_e" startValue="${labResult.dateResult}"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<openmrs:hasPrivilege privilege="Edit Test Result">
						<button type="button" id="updateSpecimen" onclick='validateAndSubmit(this)'><spring:message code="mdrtb.save" text="Save"/></button>
					</openmrs:hasPrivilege>
					<button type="reset" id="cancelUpdateSpecimen" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
				</td>
			</tr> 
		
		</table>
		
	</form>	
	</div>
	
	<br>
	
	<form>
				 
		<div id = "labResult_div">
			<font style="font-weight:bold"><spring:message code="labmodule.labEntry.addResultFor"/> : </font>
			&nbsp;
			<select id="test_selected">
				<option hidden selected value> </option>
				<option value="bacterioscopy"><spring:message code="labmodule.labEntry.bacterioscopy"/></option>
				 <option value="xpert"><spring:message code="labmodule.labEntry.xpert"/></option>
				 <option value="hain"><spring:message code="labmodule.labEntry.hain"/></option>
				 <option value="hain2"><spring:message code="labmodule.labEntry.hain2"/></option>
				 <option value="culture"><spring:message code="labmodule.culture"/></option>
			</select>
			&nbsp;
			<img title="Add" class="add" id="addTest" onclick="onClick(this)" src="${pageContext.request.contextPath}/moduleResources/labmodule/add.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			<button hidden id="cancelTest" type=reset text="Cancel"/>
		</div>
		
	</form>
	
	<table cellspacing="2" cellpadding="0" width="100%">
			
		<tr>
			<td>
				
				<!-- BacterioScopy Div -->
				<div id = "bacterioscopyDiv">
					
					<b class="boxHeader" style="margin:0px">
						&nbsp;
						<spring:message code="labmodule.labEntry.bacterioscopy" text="Microscopy"/>
					</b>
						
					<form id="addMicroscopyResults" name="addMicroscopyResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=microscopy&microscopyId=0" method="post">

						<div class="box">
							<table cellspacing="2" cellpadding="0">
								
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="${labResult.provider.id}">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="sampleDate" startValue=""/>
									</td>
								</tr>
									
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
										&nbsp;
										<select id="sampleAppearance" name="sampleAppearance">
										<option hidden selected value=""></option>
										<c:forEach var="appearance" items="${appearances}">
											<option value="${appearance.answerConcept.id}">${appearance.answerConcept.displayString}</option>
										</c:forEach>
										</select>
									</td>
								</tr>			
										
								<tr>
									<td>			
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
										&nbsp;
										<select id="sampleResult" name=sampleResult>
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addMicroscopyTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>
										<button type = "reset" id="cancelBacterioscopy" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
									
							</table>
						</div>
								
					</form>
						
				</div>
					<!-- End of BacterioScopy Div -->
				
				<!-- GeneXpert DIV -->	
				<div id="xpertDiv">
				
					<form id="addXpertResults" name="addXpertResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=xpert&xpertId=0" method="post">
					
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.xpert" text="Xpert MTB/Rif"/>
						</b>
					
						<div class="box">
							
							<table cellspacing="2" cellpadding="0">
							
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="${labResult.provider.id}">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="xpertTestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbXpertResult" name="mtbResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
										&nbsp;
										<select id = "rifXpertResult" name = "rifResult">
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${rifResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
										&nbsp;
										<input type="text"  size="10" name="xpertError" id="xpertError">
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addXpertTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelXpert" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
								
							</table>
							
						</div>
					
					</form>
				
				</div>
				<!-- END of GeneXpert DIV -->
				
				<!-- HAIN DIV -->	
				<div id="hainDiv">
				
					<form id="addHAINResults" name="addHAINResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=hain&hainId=0" method="post">

						
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.hain" text="Hain"/>
						</b>
						
						<div class="box">
							
							<table  cellspacing="2" cellpadding="0">
							
								<tr>
									<td>
									
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="${labResult.provider.id}">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
									
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hainTestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbHainResult" name="mtbResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
										&nbsp;
										<select id = "rifHainResult" name = "rifResult">
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${rifResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
										&nbsp;
										<select id = "inhHainResult" name = "inhResult">
											<option hidden selected value=""></option>
												<c:forEach var="result" items="${inhResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addHainTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelHain" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
							
							</table>
							
						</div>
						
					</form>
					
				</div>
				<!-- END OF HAIN DIV -->		
					
					<!-- HAIN 2 DIV -->	
				<div id = "hain2Div">
					
					<form id="addHAIN2Results" name="addHAIN2Results" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=hain2&hain2Id=0" method="post">

						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.labEntry.hain2" text="Hain 2"/>
						</b>
						
						<div class="box">
							
							<table  cellspacing="2" cellpadding="0">
							
								<tr>
									<td>
									
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="${labResult.provider.id}">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
									
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="hain2TestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
										&nbsp;
										<select id="mtbHain2Result" name="mtbResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${mtbResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
										&nbsp;
										<select id="moxHain2Result" name="moxResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${moxResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
										&nbsp;
										<select id="cmHain2Result" name="cmResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${cmResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
										&nbsp;&nbsp;
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
										&nbsp;
										<select id="erHain2Result" name="erResult">
										     <option hidden selected value=""></option>
												<c:forEach var="result" items="${eResults}">
													<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
												</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addHain2Test" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelHain2" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
							
							</table>
							
						</div>
						<!-- END OF HAIN2 DIV -->		
						
					</form>
					
				</div>
				
				<!-- Start Culture Div -->
				<div id="cultureDiv">
				
					<form id="addCultureResults" name="addCultureResults" action="labEntry.form?patientId=${patientId}&=${labResultId}&submissionType=culture&cultureId=0" method="post">
					
						<b class="boxHeader" style="margin:0px">
							&nbsp;
							<spring:message code="labmodule.culture" text="Culture"/>
						</b>
					
						<div class="box">
							
							<table cellspacing="2" cellpadding="0">
							
								<tr>
									<td>
										<input hidden type="text" name="labResultId" value="${labResult.id}">
										<input hidden type="text" name="provider" value="${labResult.provider.id}">
										<input hidden type="text" name="lab" value="${labResult.location.locationId}">
										
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
										&nbsp;
										<openmrs_tag:dateField formFieldName="cultureTestDate" startValue=""/>
									</td>
								</tr>
								
								<tr>
									<td>
										<font style="font-weight:bold"><spring:message code="labmodule.labEntry.culture.result"/>:</font>
										&nbsp;
										<select id="cultureResult" name="cultureResult">
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
										</select>
									</td>
								</tr>
								
								<tr>
									<td>
										<br>
									</td>
								</tr>
								
								<tr>
									<td>
									<openmrs:hasPrivilege privilege="Add Test Result">
										<button type="button" id="addCultureTest" onClick="saveTest(this)"><spring:message code="mdrtb.save" text="Save"/></button>
									</openmrs:hasPrivilege>	
										<button type = "reset" id="cancelCulture" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
									</td>
								</tr>
								
							</table>
							
						</div>
					
					</form>
				
				</div>
				<!-- END of Culture Div -->
					
			</td>
		</tr>
			<!-- END OF LAB RESULT -->
	</table>
	
	<br>

	
	<b class="boxHeader" style="margin:0px; width:100%;">
		&nbsp;
		<spring:message code="labmodule.labResults" text="Lab Results"/>
	</b>
	
	<div class="box" style="margin:0px">

	<table cellspacing="5" cellpadding="0" width="100%">
	
	<c:if test="${fn:length(labResult.microscopies) > 0}">
	
		<c:forEach var="microscopy" items="${labResult.microscopies}" varStatus="i">
		
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.bacterioscopy"/> ( ${i.count} )
					<span id="microscopy_edit_span_${i.count}" name="specimen_edit_span" style="float:right">
						<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteMicroscopySpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
						</openmrs:hasPrivilege>
					</span>
					</b>
				</td>
			</tr>
			
			<tr><td>
			
			<form id="deleteMicroscopyResults_${i.count}" name="deleteMicroscopyResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
				
					<input hidden type="text" name="labResultId" value="${labResult.id}">
					<input hidden type="text" name="id" value="${microscopy.id}">
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${microscopy.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
								
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
					&nbsp;
					${microscopy.sampleApperence.displayString}
						
					&nbsp;&nbsp;
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
					&nbsp;
					${microscopy.sampleResult.displayString}
				</td>
			</tr>
								
			</table>
			</form>
				
		</c:forEach>
	
	</c:if>
	
	<c:if test="${fn:length(labResult.xperts) > 0}">
	
		<c:forEach var="xpert" items="${labResult.xperts}" varStatus="i">
		   				
				<tr>
					<td>
						<br>
						<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.xpert"/> ( ${i.count} )
						<span id="xpert_edit_span_${i.count}" name="xpert_edit_span" style="float:right">
						    <openmrs:hasPrivilege privilege="Delete Test Result">
						    <img title="Delete" id="deleteXpertSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
						</span>
						</b>
					</td>
				</tr>
				
				<tr><td>
			
				<form id="deleteXpertResults_${i.count}" name="deleteXpertResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

				<table style="font-size: 13px">
				
				<tr>
					<td>
					
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${xpert.id}">
						
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${xpert.resultDate}" format="${_dateFormatDisplay}"/>
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
						&nbsp;
						${xpert.mtbBurden.displayString}
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
						&nbsp;
						${xpert.rifResistance.displayString}
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
						&nbsp;
						${xpert.errorCode}
					</td>
				</tr>
				
				</table>
				</form>
				</td></tr>
					
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS) > 0}">
	
		<c:forEach var="hain" items="${labResult.HAINS}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain"/> ( ${i.count} )
					<span id="hain_edit_span_${i.count}" name="hain_edit_span" style="float:right">
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteHainSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
						</span>
					</b>
				</td>
			</tr>
			
	<tr><td>
			<form id="deleteHainResults_${i.count}" name="deleteHainResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

		<table style="font-size: 13px">
			
			<tr>
				<td>
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain.id}">
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
						
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain.mtbBurden.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
					&nbsp;
					${hain.rifResistance.displayString}			
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
					&nbsp;
					${hain.inhResistance.displayString}				
				</td>
		  </tr>
		  
		  </table>
		  </form>
		  </td></tr>
			
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS2) > 0}">
	
		<c:forEach var="hain2" items="${labResult.HAINS2}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain2"/> ( ${i.count} )
					<span id="hain2_edit_span_${i.count}" name="hain2_edit_span" style="float:right">
							<openmrs:hasPrivilege privilege="Delete Test Result">
							<img title="Delete" id="deleteHain2Span_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
							</openmrs:hasPrivilege>
					</span></b>
				</td>
			</tr>
			
			<tr><td>
			<form id="deletehain2Results_${i.count}" name="deletehain2Results_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
				
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain2.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain2.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain2.mtbBurden.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
					&nbsp;
					${hain2.moxResistance.displayString} 
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
					&nbsp;
					${hain2.cmResistance.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
					&nbsp;
					${hain2.erResistance.displayString} 
				</td>
			</tr> 
			
			</table>
			</form>
			</td></tr>
		
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.cultures) > 0}">
	
		<c:forEach var="culture" items="${labResult.cultures}" varStatus="i">
		
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.culture"/> ( ${i.count} )
						<span id="culture_edit_span_${i.count}" name="culture_edit_span" style="float:right">
								<openmrs:hasPrivilege privilege="Delete Test Result">
								<img title="Delete" id="deleteCultureSpan_${i.count}" class="edit" onclick='deleteTest(this)' src="${pageContext.request.contextPath}/moduleResources/labmodule/delete.gif" alt="delete" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
								</openmrs:hasPrivilege>
						</span>
					</b>
				</td>
			</tr>
			
			<tr><td>
			<form id="deleteCultureResults_${i.count}" name="deleteCultureResults_${i.count}" action="labEntry.form?patientId=${patientId}&submissionType=deleteTest" method="post">

			<table style="font-size: 13px">
			
			<tr>
				<td>
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${culture.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${culture.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.culture.result"/>:</font>
					&nbsp;
					${culture.result.displayString}
				</td>
			</tr> 
			
			</table>
			</form>
			</td></tr>
		
		</c:forEach>
		
	</c:if>	
	
	</table>
	
	</div>
	
	</div>
	
	

<!-- START 'ADD NEW SPECIMEN' RIGHT-HAND COLUMN -->
<div id="new_column" style="float: right; width:65%;  padding:0px 4px 4px 4px">

	<b class="boxHeader" style="margin:0px">
		&nbsp;
		<spring:message code="mdrtb.specimenDetails" text="Specimen Details"/>
	</b>
 
	<div class="box" id="specimen" style="margin:0px">

		<!-- START OF Specimen Detail DIV -->
		<div id="details_specimen" style="margin:0px">
		
			<form id="addTestResults" name="addTestResults" action="labEntry.form?patientId=${patientId}&submissionType=specimen" method="post">
			
				<table cellspacing="5" cellpadding="0" width="100%" >
			
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.collectedBy" text="Collected By"/> : </font>
							&nbsp;
							<select id="provider" name="provider">
								<option hidden selected value=""/>
									<c:forEach var="provider" items="${providers}">
										<option value="${provider.id}">${provider.personName}</option>
									</c:forEach>
							</select>
						</td>
					</tr>		
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
							&nbsp;
							<select id="lab" name="lab">
								<option hidden selected value=""></option>
									<c:forEach var="location" items="${locations}">
										<option value="${location.locationId}">${location.displayString}</option>
									</c:forEach>
							</select>
						</td>	
					</tr>
					<tr>
						<td>		
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.labNumber"/> : </font>
							&nbsp;
							<input type="text"  size="10" id="labNumber" name="labNumber">
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateRecieve" startValue=""/>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
							&nbsp;
							<openmrs_tag:dateField formFieldName="dateInvestigation" startValue=""/>
						</td>
					</tr>
					
					<tr>
						<td>
							<br>
							<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
							&nbsp;
							<select id="requestingLabName" name="requestingLabName">
								<option hidden selected value=""></option>
									<c:forEach var="requestingFacility" items="${requestingFacilities}">
										<option value="${requestingFacility.answerConcept.id}">${requestingFacility.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					<tr>
					
					<tr>
						<td>	
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
							&nbsp;
							<select id="investigationPurpose" name="investigationPurpose">
								<option hidden selected value=""></option>
									<c:forEach var="purpose" items="${investigationPurposes}">
										<option value="${purpose.answerConcept.id}">${purpose.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
				
					<tr>
						<td> 
							<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
							&nbsp;
							<select id="biologicalSpecimen" name="biologicalSpecimen">
								<option hidden selected value=""></option>
									<c:forEach var="type" items="${types}">
										<option value="${type.answerConcept.id}">${type.answerConcept.displayString}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td>
							<br>
						</td>
					</tr>
					
					<tr>
						<td>
							<font style="font-size:13px; font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
							&nbsp;
							<textarea rows="4" cols="50" name="comments"></textarea>
						</td>
					</tr>
					
					<tr>
						<td>
							<input type="checkbox" id="peripheral" name="peripheral" value="peripheral" onclick='onClick(this)'/> <font size=3> <spring:message code="labmodule.labEntry.peripheralLabInfo"/> </font>
							
							<div id="peripheral_div">
							
								<table>
								
									<tr>
										<td>
											<br>
											<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
										</td>
									</tr>
									
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
											&nbsp;
											<input type="text"  size="10" id="peripheralLabNo" name="peripheralLabNo">
										</td>
									</tr>
									
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
											&nbsp;
											<select  id=microscopyResult name=microscopyResult>
											     <option hidden selected value=""></option>
													<c:forEach var="result" items="${microscopyResults}">
														<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
													</c:forEach>
											</select>
										</td>
									</tr>
									
									<tr>
										<td>
											<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
											&nbsp;
											<openmrs_tag:dateField formFieldName="dateResult" startValue=""/>
										</td>
									</tr>
								
								</table>
							
							</div>
							
						</td>
					</tr>
					
					
					<tr>
						<td>
							<br>
						</td>
					</tr>
					
					<tr>
						<td>
						<openmrs:hasPrivilege privilege="Add Test Result">
							<button type="button" id="saveSpecimen" onclick="validateAndSubmit(this);"><spring:message code="mdrtb.save" text="Save"/></button>
						</openmrs:hasPrivilege>	
							<button type="reset" id="cancelSpecimen" onclick='onClick(this)'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
						</td>
					</tr>
				
				</table>
	
			</form>
		
		</div>
		<!-- END OF Specimen Detail DIV -->
	</div>

</div>
<!-- END RIGHT-HAND COLUMN -->

<br> <br>

<%@ include file="/WEB-INF/view/module/labmodule/dotsFooter.jsp"%>

<script type='text/javascript'>

	document.getElementById('new_column').style.display = "none";
	document.getElementById('old_column').style.display = "none";

	sortSelect(document.getElementById('microscopyResult'));
	

	if(window.location.href.indexOf("labResultId") > -1) {
		document.getElementById('old_column').style.display = "block";
		document.getElementById("specimen_edit_span").style.display = "block";

		if(window.location.href.indexOf("edit") > -1) {
			document.getElementById("specimen_edit").style.display = "block";
			document.getElementById("specimen_view").style.display = "none";
		}
		else{
			document.getElementById("specimen_edit").style.display = "none";
			document.getElementById("specimen_view").style.display = "block";
		}
		
		document.getElementById('bacterioscopyDiv').style.display = "none";
	 	document.getElementById('xpertDiv').style.display = "none";
	 	document.getElementById('hainDiv').style.display = "none";
	 	document.getElementById('hain2Div').style.display = "none";
	 	document.getElementById('cultureDiv').style.display = "none";
	 	document.getElementById('peripheral_div').style.display = "none";
    } 

	
	function validateAndSubmit(obj) {

		var theId = obj.id;

		if(theId == 'updateSpecimen'){

			var e = document.getElementById('provider_e');
			var provider = e.options[e.selectedIndex].value;
	
			e = document.getElementById('lab_e');
			var lab = e.options[e.selectedIndex].value;

			e = document.getElementById('labNumber_e');
			var labNumber = e.value;
	
			e = document.getElementById('dateRecieve_e');
			var dateRecieve = e.value;
	
			e = document.getElementById('dateInvestigation_e');
			var dateInvestigation = e.value;
	
			e = document.getElementById('requestingLabName_e');
			var requestFacility = e.options[e.selectedIndex].text;
	
			e = document.getElementById('investigationPurpose_e');
			var investigationPurpose = e.options[e.selectedIndex].text;
	
			e = document.getElementById('biologicalSpecimen_e');
			var biologicalSpecimen = e.options[e.selectedIndex].text;

			e = document.getElementById('peripheralLabNo_e');
			var peripherallabNumber = e.value;

			e = document.getElementById('microscopyResult_e');
			var microscopyResult = e.options[e.selectedIndex].text;

			e = document.getElementById('dateResult_e');
			var dateResult = e.value;
			
			var errorText = '';	
	
			if (provider == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCollector' text='Please specify who collected this sample.'/>" + "\n";
			}
			if (lab == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLab' text='Please specify a laboratory.'/>" + "\n";
			}
			if (labNumber == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLabNumber' text='Please specify Lab Test Number.'/>" + "\n";
			}
			if (dateRecieve == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateReceived' text='Please specify a Recieve Date.'/>" + "\n";
			}
			else if(isFutureDate(dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='The date received must not be in the future.'/>" + "\n";
			}
			if (dateInvestigation == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateInvestigation' text='Please specify a Investigation Date.'/>" + "\n";
			}
			else if(isFutureDate(dateInvestigation)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateInvestigationInFuture' text='The date investigation must not be in the future.'/>" + "\n";
			}
			if (requestFacility == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRequestingFacility' text='Please Specify the requesting medical facility.'/>" + "\n";
			}
			if (investigationPurpose == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInvestigationPurpose' text='Please Specify the purpose of investigation.'/>" + "\n";
			}
			if (biologicalSpecimen == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noBiologicalSpecimen' text='Please Specify the biological specimen.'/>" + "\n";
			}
			if(peripherallabNumber == ''){

				if(microscopyResult != '' || dateResult != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noPeripheralLabNumber' text='Please Specify the Peripheral Lab Number.'/>" + "\n";
			}
			if(microscopyResult == ''){

				if(peripherallabNumber != '' || dateResult != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please Specify the Microscopy Result.'/>" + "\n";
			}
			if(dateResult == ''){

				if(microscopyResult != '' || peripherallabNumber != '')
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please Specify the Date of result.'/>" + "\n";
			}
			
			
			if(errorText == '') {
				document.forms["updateTestResults"].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
			
		}

		else if(theId == 'saveSpecimen'){

			var e = document.getElementById('provider');
			var provider = e.options[e.selectedIndex].value;
	
			e = document.getElementById('lab');
			var lab = e.options[e.selectedIndex].value;
	
			e = document.getElementById('dateRecieve');
			var dateRecieve = e.value;

			e = document.getElementById('labNumber');
			var labNumber = e.value;
			
			e = document.getElementById('dateInvestigation');
			var dateInvestigation = e.value;
	
			e = document.getElementById('requestingLabName');
			var requestFacility = e.options[e.selectedIndex].text;
	
			e = document.getElementById('investigationPurpose');
			var investigationPurpose = e.options[e.selectedIndex].text;
	
			e = document.getElementById('biologicalSpecimen');
			var biologicalSpecimen = e.options[e.selectedIndex].text;

			e = document.getElementById('peripheralLabNo');
			var peripheralLabNumber = e.value;

			e = document.getElementById('microscopyResult');
			var microscopyResult = e.options[e.selectedIndex].text;

			e = document.getElementById('dateResult');
			var dateResult = e.value;

			var errorText = ''
	
			if (provider == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noCollector' text='Please specify who collected this sample.'/>" + "\n";
			}
			if (lab == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLab' text='Please specify a laboratory.'/>" + "\n";
			}
			if (labNumber == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noLabNumber' text='Please specify Lab Test Number.'/>" + "\n";
			}
			if (dateRecieve == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateReceived' text='Please specify a Recieve Date.'/>" + "\n";
			}
			else if(isFutureDate(dateRecieve)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateReceivedInFuture' text='The date received must not be in the future.'/>" + "\n";
			}
			if (dateInvestigation == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateInvestigation' text='Please specify a Investigation Date.'/>" + "\n";
			}
			else if(isFutureDate(dateInvestigation)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateInvestigationInFuture' text='The date investigation must not be in the future.'/>" + "\n";
			}
			if (requestFacility == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noRequestingFacility' text='Please Specify the requesting medical facility.'/>" + "\n";
			}
			if (investigationPurpose == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noInvestigationPurpose' text='Please Specify the purpose of investigation.'/>" + "\n";
			}
			if (biologicalSpecimen == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noBiologicalSpecimen' text='Please Specify the biological specimen.'/>" + "\n";
			}
			if(document.getElementById('peripheral').checked){

				if (peripheralLabNumber == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noPeripheralLabNumber' text='Please specify Peripheral Lab Number.'/>" + "\n";
				}
				if (microscopyResult == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
				}
				if (dateResult == '') {
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
				}
				else if(isFutureDate(dateResult)){
					errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The date result must not be in the future.'/>" + "\n";
				}
				
			}
			
			if(errorText == '') {
				document.forms["addTestResults"].submit();
			}
			else{
	
				errorText = "Fix following error(s) to continue:\n" + errorText;
				confirm(errorText); 
				return false;
			}
		}
		
	}	

	// ON CLICK ...
	function onClick(obj){
		
		 var theId = obj.id;

		 // Show Specimen and Test form
		 if(theId == 'quickEntryAddButton'){

			 var r = true;
			 
			 if(document.getElementById('new_column').style.display == 'block'){
				 r = confirm("Are you sure you want to open new 'Add Specimen' form? All unsaved information from previous form will be discarded.");
			 }	 
			
			 if (r == true) {
				 document.getElementById('new_column').style.display = "block";
				 document.getElementById('labResult_div').style.display = "block"; 
				 document.getElementById('old_column').style.display = "none";
				 
				 document.getElementById("cancelSpecimen").click();
				 document.getElementById("cancelBacterioscopy").click();
				 document.getElementById("cancelXpert").click();
				 document.getElementById("cancelHain").click();
				 document.getElementById("cancelHain2").click();
				 document.getElementById("cancelTest").click();
				 document.getElementById("cancelCulture").click();
			 }
			
		 }

		 // Add Test Form a/c to selection
		 else if(theId == 'addTest'){

			 var e = document.getElementById('test_selected');
			 var testResult = e.options[e.selectedIndex].value;	

			 if(testResult != ''){
			 
				 document.getElementById('labResult_div').style.display = "none";
				 document.getElementById('bacterioscopyDiv').style.display = "none";
				 document.getElementById('xpertDiv').style.display = "none";
				 document.getElementById('hainDiv').style.display = "none";
				 document.getElementById('hain2Div').style.display = "none";
				 document.getElementById('cultureDiv').style.display = "none";

			 }
			 
			 if(testResult == 'bacterioscopy')
				document.getElementById('bacterioscopyDiv').style.display = "block";
			 else if(testResult == 'xpert')
					document.getElementById('xpertDiv').style.display = "block";
			 else if(testResult == 'hain')
					document.getElementById('hainDiv').style.display = "block";
			 else if(testResult == 'hain2')
					document.getElementById('hain2Div').style.display = "block";
			 else if(testResult == 'culture')
					document.getElementById('cultureDiv').style.display = "block";				
						
		}
		
	    else if(theId == 'cancelBacterioscopy'){

			 document.getElementById('bacterioscopyDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();

		}
			
	    else if (theId == 'cancelXpert'){

			 document.getElementById('xpertDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
			
		else if (theId == 'cancelHain'){

			 document.getElementById('hainDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
			
		else if (theId == 'cancelHain2'){

			 document.getElementById('hain2Div').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}

		else if (theId == 'cancelCulture'){

			 document.getElementById('cultureDiv').style.display = "none";
			 document.getElementById('labResult_div').style.display = "block";	
			 document.getElementById("cancelTest").click();	

		}
			
		else if (theId == 'cancelSpecimen'){

			 document.getElementById('labResult_div').style.display = "block"; 
			
			 document.getElementById('bacterioscopyDiv').style.display = "none";
			 document.getElementById('xpertDiv').style.display = "none";
			 document.getElementById('hainDiv').style.display = "none";
			 document.getElementById('hain2Div').style.display = "none";
			 document.getElementById('cultureDiv').style.display = "none";
			 document.getElementById('peripheral_div').style.display = "none";

			 document.getElementById("cancelSpecimen").click();
			 document.getElementById("cancelBacterioscopy").click();
			 document.getElementById("cancelXpert").click();
			 document.getElementById("cancelHain").click();
			 document.getElementById("cancelHain2").click();
			 document.getElementById("cancelTest").click();
			 document.getElementById("cancelCulture").click();
		}

		else if (theId == 'peripheral'){

			if (document.getElementById('peripheral').checked) {
				document.getElementById('peripheral_div').style.display = "block";
	        } else {
	        	document.getElementById('peripheral_div').style.display = "none";
	        }
			
		}

		else if (theId == 'editSpecimenDetailButton'){

			document.getElementById("specimen_edit_span").style.display = "none";
			document.getElementById("specimen_edit").style.display = "block";
			document.getElementById("specimen_view").style.display = "none";
			
		}

		else if (theId == 'cancelUpdateSpecimen'){

			document.getElementById("specimen_edit_span").style.display = "block";
			document.getElementById("specimen_edit").style.display = "none";
			document.getElementById("specimen_view").style.display = "block";
			
		}	

  }

	function stringToDate(_date,_format,_delimiter)
	{
	            var formatLowerCase=_format.toLowerCase();
	            var formatItems=formatLowerCase.split(_delimiter);
	            var dateItems=_date.split(_delimiter);
	            var monthIndex=formatItems.indexOf("mm");
	            var dayIndex=formatItems.indexOf("dd");
	            var yearIndex=formatItems.indexOf("yyyy");
	            var month=parseInt(dateItems[monthIndex]);
	            month-=1;
	            var formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);
	            return formatedDate;
	}

	function isFutureDate(dateRecieve)
	{
		var dateR;
		if(dateRecieve.includes("/")){
			dateR = stringToDate(dateRecieve,"mm/dd/yyyy","/");	
		}	
		else if (dateRecieve.includes(".")){
			dateR = stringToDate(dateRecieve,"dd.mm.yyyy",".");	
		}	
			
		var now = new Date();
		
		if(dateR > now)
			return true;
		else 
			return false;	

	}

	function deleteTest(obj){

		var theId = obj.id;

		var idArray = theId.split("_");
		
		if(idArray[0] == 'deleteMicroscopySpan'){

			var flag = confirm("Are you sure you want to delete the Microscopy test Record?");
			
			if(flag == true)
				document.forms["deleteMicroscopyResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteXpertSpan'){

			var flag = confirm("Are you sure you want to delete the Xpert test Record?");
			
			if(flag == true)
				document.forms["deleteXpertResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteHainSpan'){

			var flag = confirm("Are you sure you want to delete the Hain test Record?");
			
			if(flag == true)
				document.forms["deleteHainResults_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteHain2Span'){

		var flag = confirm("Are you sure you want to delete the Hain-2 test Record?");
			
			if(flag == true)
				document.forms["deletehain2Results_"+idArray[1]].submit();
			
		}
		else if(idArray[0] == 'deleteCultureSpan'){

		var flag = confirm("Are you sure you want to delete the Culture test Record?");
			
			if(flag == true)
				document.forms["deleteCultureResults_"+idArray[1]].submit();
			
		}

		else if(idArray[0] == 'deleteTest'){

			var flag = confirm("Are you sure you want to delete the whole specimen Record for lab test #:"+idArray[2]);
			
			if(flag == true)
				document.getElementById("delete_"+idArray[1]).submit();
	
		}
	}

	function saveTest(obj){

		var theId = obj.id;
		var formName = '';

		var errorText = '';
		
		if (theId == 'addMicroscopyTest'){
			formName = "addMicroscopyResults";

			var e = document.getElementById('sampleResult');
			var result = e.options[e.selectedIndex].value;
	
			e = document.getElementById('sampleDate');
			var resultDate = e.value;

			e = document.getElementById('sampleAppearance');
			var appearance = e.options[e.selectedIndex].value;
			
			if (result == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			if (appearance == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noAppearance' text='Please specify Sample Appearance.'/>" + "\n";
			}
			
		}
		else if (theId == 'addXpertTest'){
			formName = "addXpertResults";

			var e = document.getElementById('xpertTestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbXpertResult');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifXpertResult');
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('xpertError');
			var error = e.value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
		}
		else if (theId == 'addHainTest'){
			formName = "addHAINResults";

			var e = document.getElementById('hainTestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbHainResult');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('rifHainResult');
			var rifResult = e.options[e.selectedIndex].value;

			e = document.getElementById('inhHainResult');
			var inhResult = e.options[e.selectedIndex].value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}			
		}
		else if (theId == 'addHain2Test'){
			formName = "addHAIN2Results";

			var e = document.getElementById('hain2TestDate');
			var resultDate = e.value;

			e = document.getElementById('mtbHain2Result');
			var mtbResult = e.options[e.selectedIndex].value;

			e = document.getElementById('moxHain2Result');
			var moxResult = e.options[e.selectedIndex].value;

			e = document.getElementById('cmHain2Result');
			var cmResult = e.options[e.selectedIndex].value;

			e = document.getElementById('erHain2Result');
			var erResult = e.options[e.selectedIndex].value;

			if (mtbResult == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}	
			
		}
		else if (theId == 'addCultureTest'){
			formName = "addCultureResults";

			var e = document.getElementById('cultureTestDate');
			var resultDate = e.value;

			e = document.getElementById('cultureResult');
			var result = e.options[e.selectedIndex].value;

			if (result == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noMicroscopyResult' text='Please specify Microscopy Result.'/>" + "\n";
			}
			if (resultDate == '') {
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.noDateResult' text='Please specify date Result.'/>" + "\n";
			}
			else if(isFutureDate(resultDate)){
				errorText = errorText + "<spring:message code='labmodule.labEntry.errors.dateResultInFuture' text='The result date must not be in the future.'/>" + "\n";
			}
			
		}

		if(errorText == '') {
			document.forms[formName].submit();
		}
		else{

			errorText = "Fix following error(s) to continue:\n" + errorText;
			confirm(errorText); 
			return false;
		}
		
	}

	function sortSelect(selElem) {
	    var tmpAry = new Array();
	    for (var i=0;i<selElem.options.length;i++) {
	        tmpAry[i] = new Array();
	        tmpAry[i][0] = selElem.options[i].text;
	        tmpAry[i][1] = selElem.options[i].value;
	    }
	    tmpAry.sort();
	    while (selElem.options.length > 0) {
	        selElem.options[0] = null;
	    }
	    for (var i=0;i<tmpAry.length;i++) {
	        var op = new Option(tmpAry[i][0], tmpAry[i][1]);
	        selElem.options[i] = op;
	    }
	    return;
	}


</script>
