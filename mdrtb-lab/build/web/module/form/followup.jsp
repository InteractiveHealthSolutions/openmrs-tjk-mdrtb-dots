<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->
<c:set var="defaultReturnUrl" value="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}"/>
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#edit').click(function(){
			$j('#viewVisit').hide();
			$j('#editVisit').show();
		});

		$j('#cancel').click(function(){
			if (${(empty followup.id) || (followup.id == -1) || fn:length(errors.allErrors) > 0}) {
				// if we are in the middle of a validation error, or doing an "add" we need to do a page reload on cancel
				window.location="${!empty returnUrl ? returnUrl : defaultReturnUrl}";
			} 
			else {
				// otherwise, just hide the edit popup and show the view one	
				$j('#editVisit').hide();
				$j('#viewVisit').show();
			}
		});
		
	});


-->

</script>

<br/>

<div> <!-- start of page div -->

&nbsp;&nbsp;<a href="${!empty returnUrl ? returnUrl : defaultReturnUrl}"><spring:message code="mdrtb.back" text="Back"/></a>
<br/><br/>

<!-- VIEW BOX -->
<div id="viewVisit" <c:if test="${(empty followup.id) || (followup.id == -1) || fn:length(errors.allErrors) > 0}"> style="display:none" </c:if>>
<b class="boxHeader"><spring:message code="mdrtb.followupForm" text="Follow-Up Form"/>
<span style="position: absolute; right:30px;"><a id="edit" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a>&nbsp;&nbsp;<a href="${pageContext.request.contextPath}/module/mdrtb/visits/delete.form?visitId=${followup.id}&patientProgramId=${patientProgramId}" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteVisit" text="Are you sure you want to delete this visit?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></span>
</b>
<div class="box">

<table>
 
<tr>
<td><spring:message code="mdrtb.date" text="Date"/>:</td>
<td><openmrs:formatDate date="${followup.encounterDatetime}" format="${_dateFormatDisplay}"/></td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.location" text="Location"/>:</td>
<td>${followup.location.displayString}</td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.provider" text="Provider"/>:</td>
<td>${followup.provider.personName}</td>
</tr>

<tr>
<td><spring:message code="Patient.weight" text="Weight"/>:</td>
<td>${followup.weight} ${!empty followup.weight ? 'kg' : ''}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.temperature" text="Temperature"/>:</td>
<td>${followup.temperature} ${!empty followup.temperature ? 'C' : ''}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.pulse" text="Pulse"/>:</td>
<td>${followup.pulse}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.systolicBloodPressure" text="Systolic Blood Pressure"/>:</td>
<td>${followup.systolicBloodPressure} ${!empty followup.systolicBloodPressure ? 'mmHg' : ''}</td>
</tr>

<tr>
<td><spring:message code="mdrtb.respiratoryRate" text="Respiratory Rate"/>:</td>
<td>${followup.respiratoryRate}</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.clinicianNotes" text="Clinician Notes"/>:</td>
<td><mdrtb:format obj="${followup.clinicianNotes}"/></td>
</tr>

</table>

</div>
</div>
<!-- END VIEW BOX -->

<!-- EDIT BOX -->
<div id="editVisit" <c:if test="${(!empty followup.id) && (followup.id != -1) && fn:length(errors.allErrors) == 0}"> style="display:none" </c:if>>
<b class="boxHeader"><spring:message code="mdrtb.followupForm" text="Follow-Up Form"/></b>
<div class="box">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<c:if test="${error.code != 'methodInvocation'}">
			<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
		</c:if>
	</c:forEach>
	<br/>
</c:if>

<form name="followup" action="followup.form?patientId=${patientId}&patientProgramId=${patientProgramId}&encounterId=${!empty followup.id ? followup.id : -1}" method="post">
<input type="hidden" name="returnUrl" value="${returnUrl}" />

<table>
 
<tr>
<td><spring:message code="mdrtb.date" text="Date"/>:</td>
<td><openmrs_tag:dateField formFieldName="encounterDatetime" startValue="${followup.encounterDatetime}"/></td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.location" text="Location"/>:</td>
<td>
<select name="location">
<option value=""></option>
<c:forEach var="location" items="${locations}">
	<option value="${location.id}" <c:if test="${followup.location == location}">selected</c:if>>${location.displayString}</option>
</c:forEach>
</select>
</td>
</tr>
 
<tr>
<td><spring:message code="mdrtb.provider" text="Provider"/>:</td>
<td>
<select name="provider">
<option value=""></option>
<c:forEach var="provider" items="${providers}">
	<option value="${provider.id}" <c:if test="${followup.provider == provider}">selected</c:if>>${provider.personName}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td valign="top"><spring:message code="Patient.weight" text="Weight"/>:</td>
<td><input name="weight" size="8" value="${followup.weight}"/> kg</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.temperature" text="Temperature"/>:</td>
<td><input name="temperature" size="8" value="${followup.temperature}"/> C</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.pulse" text="Pulse"/>:</td>
<td><input name="pulse" size="8" value="${followup.pulse}"/></td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.systolicBloodPressure" text="Systolic Blood Pressure"/>:</td>
<td><input name="systolicBloodPressure" size="8" value="${followup.systolicBloodPressure}"/> mmHg</td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.respiratoryRate" text="Respiratory Rate"/>:</td>
<td><input name="respiratoryRate" size="8" value="${followup.respiratoryRate}"/></td>
</tr>

<tr>
<td valign="top"><spring:message code="mdrtb.clinicianNotes" text="Clinician Notes"/>:</td>
<td><textarea  cols="100" rows="15" name="clinicianNotes">${followup.clinicianNotes}</textarea></td>
</tr>


</table>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button id="cancel" type="reset"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	
</form>
</div>
</div>
<!-- END EDIT BOX -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>