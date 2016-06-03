<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${! empty patientId ? patientId : specimen.patient.patientId}" parameters="patientProgramId=${patientProgramId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<!--  these are to make sure that the datepicker appears above the popup -->
<style type="text/css">
    td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- CUSTOM JQUERY  -->


<br/><br/>

<div align="center"> <!-- start of page div -->

<!-- PROGRAM ENROLLMENT BOX-->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.enrollment.enrollMdrtb" text="Enroll in MDR-TB Program"/></b>
<div class="box" style="margin:0px">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(errors.allErrors) > 0}">
	<c:forEach var="error" items="${errors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<form id="programEnroll" action="${pageContext.request.contextPath}/module/mdrtb/program/programEnroll.form?patientId=${patientId}&patientProgramId=-1" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td>
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td><input id="dateEnrolled" type="text" size="14" tabindex="-1" name="dateEnrolled" value="<openmrs:formatDate date='${program.dateEnrolled}'/>" onFocus="showCalendar(this)"/>
</td></tr>

<tr><td>
<spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>:</td><td>
<select name="location">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == program.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>
</td></tr>

<tr><td colspan="2">
<spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>
<select name="classificationAccordingToPreviousDrugUse">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousDrugUse" items="${classificationsAccordingToPreviousDrugUse}">
<option value="${classificationAccordingToPreviousDrugUse.id}" <c:if test="${classificationAccordingToPreviousDrugUse == program.classificationAccordingToPreviousDrugUse}">selected</c:if> >${classificationAccordingToPreviousDrugUse.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td colspan="2">
<spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:<br/>
<select name="classificationAccordingToPreviousTreatment">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousTreatment" items="${classificationsAccordingToPreviousTreatment}">
<option value="${classificationAccordingToPreviousTreatment.id}" <c:if test="${classificationAccordingToPreviousTreatment == program.classificationAccordingToPreviousTreatment}">selected</c:if> >${classificationAccordingToPreviousTreatment.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<c:if test="${hasActiveProgram}">
<tr><td>
<spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><input id="dateCompleted" type="text" size="14" name="dateCompleted" value="<openmrs:formatDate date='${program.dateCompleted}'/>" onFocus="showCalendar(this)"/>
</td></tr>
<tr><td>
<spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>
<select name="outcome">
<option value=""/>
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}">${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</c:if>

</table>
<button type="submit"><spring:message code="mdrtb.enrollment.enroll" text="Enroll in Program"/></button><button type="reset" onclick=window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${patientId}'><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>
<!-- END PROGRAM ENROLLMENT BOX -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>