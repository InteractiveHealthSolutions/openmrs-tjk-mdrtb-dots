<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<%@ taglib prefix="wgt" uri="/WEB-INF/view/module/htmlwidgets/resources/htmlwidgets.tld" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.dimensions.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.js" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.tooltip.css" />
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}" parameters="patientProgramId=${patientProgramId}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
	.ac_results {  z-index: 9999; } <!-- make any auto-completes appear on top of modal dialogs -->
	.ui-dialog-titlebar-close{display: none;}  <!--hides the close button on the pop-ups -->
</style>

<!-- JQUERY FOR THIS PAGE -->

<script type="text/javascript">

	var $j = jQuery.noConflict();	

	$j(document).ready(function(){

		$j('#programEditPopup').dialog({
			autoOpen: ${(fn:length(programEditErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.editProgram" text="Edit Program"/>',
			width: '50%',
			position: [150,150],
			zIndex:990
		});

		$j('#programEditButton').click(function() {
			$j('#programEditPopup').dialog('open');
		});

		$j('#programEditCancelButton').click(function() {
			if (${fn:length(programEditErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#programEditPopup').dialog('close');
			}
		});
		
		$j('#programClosePopup').dialog({
			autoOpen: ${(fn:length(programCloseErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.closeProgram" text="Close Program"/>',
			width: '50%',
			position: [150,150],
			zIndex:990
		});

		$j('#programCloseButton').click(function() {
			$j('#programClosePopup').dialog('open');
		});

		$j('#programCloseCancelButton').click(function() {
			if (${fn:length(programCloseErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#programClosePopup').dialog('close');
			}
		});

		$j('#programDeleteButton').click(function() {
			if (confirm('<spring:message code="mdrtb.confirmDeleteProgram" text="Are you sure you want to delete this program?"/>')) {
				window.location="${pageContext.request.contextPath}/module/mdrtb/program/programDelete.form?patientProgramId=${patientProgramId}";
			}
		});
 		
		$j('#hospitalizationsEditPopup').dialog({
			autoOpen: ${(fn:length(hospitalizationErrors.allErrors) > 0 ? true : false)},
			modal: true,
			draggable: false,
			closeOnEscape: false,
			title: '<spring:message code="mdrtb.editHospitalization" text="Edit Hospitalization"/>',
			width: '40%',
			position: [150,150],
			zIndex:990
		});

		// opens the pop-up to edit a hospitalization and populates the fill-ins with the appropriate dates
		$j('.hospitalizationsEditLink').click(function() {
			$j('#startDate').val($j(this).closest('tr').find('.admissionDate').html());
			$j('#endDate').val($j(this).closest('tr').find('.dischargeDate').html());
			$j('#hospitalizationStateId').val($j(this).attr('id'));
			$j('#hospitalizationsEditPopup').dialog('open');
		});
		
		$j('#hospitalizationsAddButton').click(function() {
			$j('#startDate').val('');
			$j('#endDate').val('');
			$j('#hospitalizationStateId').val('');
			$j('#hospitalizationsEditPopup').dialog('open');
		});

		$j('#hospitalizationsEditCancelButton').click(function() {
			if (${fn:length(hospitalizationErrors.allErrors) > 0}) {
				// if we are in the middle of a validation error, we need to do a page reload on cancel
				window.location="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}";
			} 
			else {
				// otherwise, just close the popup
				$j('#hospitalizationsEditPopup').dialog('close');
			}
		});

		// event handler to hide/show date of death/cause of death fields
		$j('.outcome').change(function() {
				if ($j(this).attr('value') == ${patientDied.id}) {
					// show patient died fields
					$j(this).closest('div').find('.patientDiedField').show();
				}
				else {
					// hide the patient died fields and reset their values
					$j(this).closest('div').find('.patientDiedField').hide().find('#causeOfDeath').attr('value','');
					$j(this).closest('div').find('.patientDiedField').find('#causeOfDeathTextField').attr('value','');
				}
		});
	});
</script>
<!-- END JQUERY -->

<div align="center" style="position:relative"> <!-- start of page div -->

<!--  
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.alerts" text="Alerts"/></b>
<div class="box" style="margin:0px">
<table cellspacing="0" cellpadding="0">
<c:forEach var="flag" items="${flags}">
<tr><td>${flag.message}</td></tr>
</c:forEach>
</table>
</div>

<br/>
-->

<!-- START LEFT-HAND COLUMN -->
<div id="leftColumn" style="float: left; width:49%;  padding:0px 4px 4px 4px">

<!--  MDR-TB PROGRAM STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.programStatus" text="Program Status"/></b>
<div class="box" style="margin:0px;">

<table cellpadding="0" cellspacing="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td width="75%"><openmrs:formatDate date="${program.dateEnrolled}" format="${_dateFormatDisplay}"/></td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.enrollment.location" text="Enrollment Location"/>:</td><td>${program.location.displayString}</td></tr>
</table>

<br/>

<table cellpadding="0" cellspacing="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:</td>
<td>
<c:choose>
	<c:when test="${! empty program.classificationAccordingToPreviousDrugUse.concept.displayString}">
		${program.classificationAccordingToPreviousDrugUse.concept.displayString}
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.unknown"/>
	</c:otherwise>
</c:choose>
</td>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:</td>
<td>
<c:choose>
	<c:when test="${! empty program.classificationAccordingToPreviousTreatment.concept.displayString}">
		${program.classificationAccordingToPreviousTreatment.concept.displayString}
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.unknown"/>
	</c:otherwise>
</c:choose>
</td>
</table>

<br/>

<table cellpadding="0" cellspacing="0">
<c:if test="${!program.active}">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><openmrs:formatDate date="${program.dateCompleted}" format="${_dateFormatDisplay}"/></td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>${program.outcome.concept.displayString}</td></tr>
</c:if>
</table>

<button id="programEditButton"><spring:message code="mdrtb.editProgram" text="Edit Program"/></button> <c:if test="${program.active}"><button id="programCloseButton"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button></c:if>
</div>

<!--  EDIT PROGRAM POPUP -->
<div id="programEditPopup" style="display:none">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(programEditErrors.allErrors) > 0}">
	<c:forEach var="error" items="${programEditErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
</c:if>

<form id="programEdit" action="${pageContext.request.contextPath}/module/mdrtb/program/programEdit.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/>:</td><td><input id="dateEnrolled" type="text" size="14" tabindex="-1" name="dateEnrolled" value="<openmrs:formatDate date='${program.dateEnrolled}'/>" onFocus="showCalendar(this)"/>
</td></tr>
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.enrollment.Location" text="Enrollment Location"/>:</td><td>
<select id="location" name="location">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == program.location}">selected</c:if> >${location.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td colspan="2" style="font-weight:bold">
<spring:message code="mdrtb.previousDrugClassification" text="Registration Group - Previous Drug Use"/>:<br/>
<select name="classificationAccordingToPreviousDrugUse">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousDrugUse" items="${classificationsAccordingToPreviousDrugUse}">
<option value="${classificationAccordingToPreviousDrugUse.id}" <c:if test="${classificationAccordingToPreviousDrugUse == program.classificationAccordingToPreviousDrugUse}">selected</c:if> >${classificationAccordingToPreviousDrugUse.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<tr><td colspan="2" style="font-weight:bold">
<spring:message code="mdrtb.previousTreatmentClassification" text="Registration Group - Previous Treatment"/>:<br/>
<select name="classificationAccordingToPreviousTreatment">
<option value=""/>
<c:forEach var="classificationAccordingToPreviousTreatment" items="${classificationsAccordingToPreviousTreatment}">
<option value="${classificationAccordingToPreviousTreatment.id}" <c:if test="${classificationAccordingToPreviousTreatment == program.classificationAccordingToPreviousTreatment}">selected</c:if> >${classificationAccordingToPreviousTreatment.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>

<c:if test="${!program.active}">
	<tr><td style="font-weight:bold">
	<spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><input id="dateCompleted" type="text" size="14" name="dateCompleted" value="<openmrs:formatDate date='${program.dateCompleted}'/>" onFocus="showCalendar(this)"/>
	</td></tr>
	<tr><td style="font-weight:bold">
	<spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>
	<select class="outcome" name="outcome">
	<option value=""/>
	<c:forEach var="outcome" items="${outcomes}">
	<option value="${outcome.id}" <c:if test="${outcome == program.outcome}">selected</c:if> >${outcome.concept.displayString}</option>
	</c:forEach>
	</select>	
	</td></tr>
	<c:if test="${! program.patient.dead}">
		<tr class="patientDiedField"<c:if test="${program.outcome != patientDied}"> style="display:none"</c:if>><td style="font-weight:bold">
		<spring:message code="mdrtb.causeOfDeath" text="Cause of Death"/>:</td><td><wgt:widget id="causeOfDeath" name="causeOfDeath" type="org.openmrs.Concept"/>
	</td></tr>
</c:if>
</c:if>
</table>

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button type="reset" id="programEditCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
<button type="reset" id="programDeleteButton"><spring:message code="mdrtb.deleteProgram" text="Delete Program"/></button>

</form>
</div>

<!-- END EDIT PROGRAM POPUP-->

<!-- CLOSE PROGRAM POPUP -->

<div id="programClosePopup" style="display:none">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(programCloseErrors.allErrors) > 0}">
	<c:forEach var="error" items="${programCloseErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
</c:if>

<form id="programClose" action="${pageContext.request.contextPath}/module/mdrtb/program/programClose.form?patientProgramId=${program.id}" method="post" >
<table cellspacing="2" cellpadding="2">
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.completionDate" text="Completion Date"/>:</td><td><input id="dateToClose" type="text" size="14" tabindex="-1" name="dateCompleted"  value="<openmrs:formatDate date='${program.dateCompleted}'/>" onFocus="showCalendar(this)"/>
</td></tr>
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.outcome" text="Outcome"/>:</td><td>
<select class="outcome" name="outcome">
<option value=""/>
<c:forEach var="outcome" items="${outcomes}">
<option value="${outcome.id}"  <c:if test="${outcome == program.outcome}">selected</c:if> >${outcome.concept.displayString}</option>
</c:forEach>
</select>	
</td></tr>
</td></tr>
<c:if test="${! program.patient.dead}">
	<tr class="patientDiedField"<c:if test="${program.outcome != patientDied}"> style="display:none"</c:if>><td style="font-weight:bold">
	<spring:message code="mdrtb.causeOfDeath" text="Cause of Death"/>:</td>
	<td><wgt:widget id="causeOfDeath" name="causeOfDeath" type="org.openmrs.Concept" object="${program.patient}" property="causeOfDeath"/></td>
	</tr>
</c:if>
</table>
<button type="submit"><spring:message code="mdrtb.closeProgram" text="Close Program"/></button> <button type="reset" id="programCloseCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>

</form>
</div>

<!-- END CLOSE PROGRAM POPUP -->

<!--  END MDR-TB PROGRAM STATUS BOX -->

<br/>

<!-- TREATMENT STATUS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.treatmentStatus" text="Treatment Status"/>: ${status.treatmentStatus.treatmentState.displayString}</b>
<div class="box" style="margin:0px">

<c:if test="${fn:length(status.treatmentStatus.regimens.value) > 0 }">
<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.regimen" text="Regimen"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.startdate" text="Start Date"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.enddate" text="End Date"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.regimenType" text="Type"/></nobr></td>
</tr>
<c:forEach var="regimen" items="${status.treatmentStatus.regimens.value}">
${regimen.displayString}
</c:forEach>
</table>
</c:if>
<br/>
<button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/regimen/manageDrugOrders.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.editTreatment" text="Edit Treatment"/></button>

</div>

<!-- END TREATMENT STATUS BOX -->

<br/>

<!--  VISIT STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.visitStatus" text="Visit Status"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr><td style="font-weight:bold"><spring:message code="mdrtb.intake" text="Intake"/>:</td><td>
<c:choose> 
	<c:when test="${! empty status.visitStatus.intakeVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.intakeVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.intakeVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<c:if test="${empty status.visitStatus.intakeVisits.value}">
<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newIntakeVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addIntakeVisit" text="Add Intake Visit"/></button>
</td></tr>
</c:if>



<tr><td style="font-weight:bold"><spring:message code="mdrtb.mostRecentFollowUp" text="Most Recent Follow-up"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.followUpVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.followUpVisits.value[fn:length(status.visitStatus.followUpVisits.value) - 1].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<!-- 
<tr><td><spring:message code="mdrtb.mostRecentSpecimenCollection" text="Most Specimen Collection"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.specimenCollectionVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.specimenCollectionVisits.value[fn:length(status.visitStatus.specimenCollectionVisits.value) - 1].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>
-->

<tr><td style="font-weight:bold"><spring:message code="mdrtb.nextScheduledFollowUp" text="Next Scheduled Follow-up"/>:</td><td> 
<c:choose>
	<c:when test="${! empty status.visitStatus.scheduledFollowUpVisits.value}">
		<a href="${pageContext.request.contextPath}${status.visitStatus.scheduledFollowUpVisits.value[0].link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}">${status.visitStatus.scheduledFollowUpVisits.value[0].displayString}</a>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.none" text="None"/>
	</c:otherwise>
</c:choose>
</td></tr>

<tr><td colspan="2">
	<button onclick="window.location='${pageContext.request.contextPath}${status.visitStatus.newFollowUpVisit.link}&returnUrl=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form%3FpatientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addFollowUpVisit" text="Add Follow-up Visit"/></button>
</td></tr>

</table>

</div>

<!--  END VISIT STATUS BOX -->

<br/>

<!--  START HOSPITALIZATIONS STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.hospitalizations" text="Hospitalizations"/>: 
<c:choose>
	<c:when test="${program.currentlyHospitalized}">
		<spring:message code="mdrtb.currentlyHospitalized" text="Currently hospitalized"/>
	</c:when>
	<c:otherwise>
		<spring:message code="mdrtb.notCurrentlyHospitalized" text="Not currently hospitalized"/>
	</c:otherwise>
</c:choose></b>
<div class="box" style="margin:0px">
<c:if test="${!empty program.allHospitalizations}">

<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.admissionDate" text="Admission Date"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dischargeDate" text="Discharge Date"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.duration" text="Duration"/></nobr></td>
<td>&nbsp;</td>
<td width="95%">&nbsp;</td>
</tr>

<c:forEach var="hospitalization" items="${program.allHospitalizations}">
<!-- the hidden "admissionDate" and "dischargeDate" spans are used to hold the dates in "edit" format for use by jquery when opening the edit pop-up -->
<tr>
<td><span class="admissionDate" style="display:none"><openmrs:formatDate date="${hospitalization.startDate}"/></span><openmrs:formatDate date="${hospitalization.startDate}" format="${_dateFormatDisplay}"/></td>
<td>
<c:choose>
 	<c:when test="${!empty hospitalization.endDate}"> 
		<span class="dischargeDate" style="display:none"><openmrs:formatDate date="${hospitalization.endDate}"/></span><openmrs:formatDate date="${hospitalization.endDate}" format="${_dateFormatDisplay}"/>
    </c:when>
	<c:otherwise>
		<spring:message code="mdrtb.currentlyHospitalized" text="Currently hospitalized"/>
	</c:otherwise>
</c:choose>
</td>
<td>
<c:if test="${!empty hospitalization.endDate}">
	<mdrtb:duration startDate="${hospitalization.startDate}" endDate="${hospitalization.endDate}" format="days"/> 
	<spring:message code="mdrtb.days" text="days"/>
</c:if>
</td>
<td><a id="${hospitalization.id}" class="hospitalizationsEditLink" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a></td>
<td><a href="${pageContext.request.contextPath}/module/mdrtb/program/hospitalizationsDelete.form?patientProgramId=${program.id}&hospitalizationStateId=${hospitalization.id}" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteHospitalization" text="Are you sure you want to delete this hospitalization?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></td>
</tr>
</c:forEach>
</table>
</c:if>

<br/>
<button id="hospitalizationsAddButton"><spring:message code="mdrtb.addHospitalization" text="Add Hospitalization"/></button> 

</div>

<!-- END HOSPITALIZATIONS STATUS BOX -->


<!--  EDIT HOSPITALIZATIONS POPUP -->
<div id="hospitalizationsEditPopup">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(hospitalizationErrors.allErrors) > 0}">
	<c:forEach var="error" items="${hospitalizationErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
</c:if>

<form id="hospitalizationsEdit" action="${pageContext.request.contextPath}/module/mdrtb/program/hospitalizationsEdit.form?patientProgramId=${program.id}" method="post" >
<input type="hidden" id="hospitalizationStateId" name="hospitalizationStateId" value="${hospitalizationStateId}"/>
<table cellspacing="2" cellpadding="2">
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.admissionDate" text="Admission Date"/>:</td><td><input id="startDate" type="text" size="14" tabindex="-1" name="startDate" value="<openmrs:formatDate date='${admissionDate}'/>" onFocus="showCalendar(this)" style="z-index:1"/>
</td></tr>
<tr><td style="font-weight:bold">
<spring:message code="mdrtb.dischargeDate" text="Discharge Date"/>:</td><td><input id="endDate" type="text" size="14" tabindex="-1" name="endDate" value="<openmrs:formatDate date='${dischargeDate}'/>" onFocus="showCalendar(this)" style="z-index:99999"/>
</td></tr>
</table>
<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button> <button type="reset" id="hospitalizationsEditCancelButton"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
	
</form>
</div>
<!-- END EDIT HOSPITALIZATION POPUP-->

</div>

<!-- END LEFT COLUMN -->

<!--  START RIGHT COLUMN -->

<div id="rightColumn" style="float:right; width:49%; padding:0px 4px 4px 4px">

<!-- MDR-TB DIAGNOSIS BOX -->

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.mdrtbDiagnosis" text="MDR-TB Diagnosis"/></b>
<div class="box" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr><td style="font-weight:bold"><spring:message code="mdrtb.resistanceType" text="Resistance Type"/>:</td><td>${status.labResultsStatus.tbClassification.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.resistanceProfile" text="Resistance Profile"/>:</td><td>${status.labResultsStatus.drugResistanceProfile.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.site" text="Site"/>:</td><td>${status.labResultsStatus.anatomicalSite.displayString}</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>
<td>&nbsp;</td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted"/></nobr></td>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.diagnosticSmear" text="Diagnostic Smear"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.diagnosticSmear.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticSmear.link}">${status.labResultsStatus.diagnosticSmear.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticSmear.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.diagnosticSmear.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticSmear.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.diagnosticCulture" text="Diagnostic Culture"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.diagnosticCulture.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticCulture.link}">${status.labResultsStatus.diagnosticCulture.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticCulture.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.diagnosticCulture.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticCulture.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.diagnosticXpert" text="Diagnostic Xpert"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.diagnosticXpert.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticXpert.link}">${status.labResultsStatus.diagnosticXpert.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticXpert.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.diagnosticXpert.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticXpert.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.diagnosticHAIN" text="Diagnostic HAIN"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.diagnosticHAIN.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.diagnosticHAIN.link}">${status.labResultsStatus.diagnosticHAIN.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticHAIN.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.diagnosticHAIN.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.diagnosticHAIN.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>

</table>

<br/>

<c:if test="${empty status.labResultsStatus.diagnosticSmear.value || empty status.labResultsStatus.diagnosticCulture.value}">
	<tr><td><button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addTestResults" text="Add Test Results"/></button></td></tr>
</c:if>

</div>

<!-- END MDR-TB DIAGNOSIS BOX -->

<br/>

<!-- LAB RESULTS STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.labResultsStatus" text="Bacteriology Status"/></b>
<div class="box" style="margin:0px">

<!--  TODO: get rid of these flags if they aren't being used -->

<table cellspacing="0" cellpadding="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.smearStatus" text="Smear Status"/>:</td><td>${status.labResultsStatus.smearConversion.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.culturestatus" text="Culture Status"/>:</td><td>${status.labResultsStatus.cultureConversion.displayString}</td></tr>
</table>

<br/>

<table cellspacing="0" cellpadding="0" border="2px" width="100%">
<tr>
<td>&nbsp;</td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted"/></td>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentSmear" text="Most Recent Smear"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentSmear.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentSmear.link}">${status.labResultsStatus.mostRecentSmear.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentSmear.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentSmear.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentSmear.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.mostRecentCulture" text="Most Recent Culture"/></td>
<c:choose>
	<c:when test="${! empty status.labResultsStatus.mostRecentCulture.value}">
		<td><mdrtb:a href="${pageContext.request.contextPath}${status.labResultsStatus.mostRecentCulture.link}">${status.labResultsStatus.mostRecentCulture.value.result.displayString}</mdrtb:a></td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentCulture.value.dateCollected}" format="${_dateFormatDisplay}"/></td>
		<td>${status.labResultsStatus.mostRecentCulture.value.lab.displayString}</td>
		<td><openmrs:formatDate date="${status.labResultsStatus.mostRecentCulture.value.resultDate}" format="${_dateFormatDisplay}"/></td>
	</c:when>
	<c:otherwise>
		<td colspan="4" align="center"><spring:message code="mdrtb.none" text="None"/></td>
	</c:otherwise>
</c:choose>
</tr>
</table>

<br/>
<button onclick="window.location='${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addTestResults" text="Add Test Results"/></button>
<br/>

<c:if test="${fn:length(status.labResultsStatus.pendingLabResults.value) > 0}"> 
<br/>
<table cellspacing="0" cellpadding="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.pendingLabResults" text="Pending Lab Results"/></td></tr>
<c:forEach var="pendingLabResult" items="${status.labResultsStatus.pendingLabResults.value}">
<tr><td><a href="${pageContext.request.contextPath}${pendingLabResult.link}">${pendingLabResult.displayString}</a></td></tr>
</c:forEach>
</table>
</c:if>
</div>

<!-- END LAB RESULTS STATUS BOX -->

<br/>

<!-- HIV STATUS BOX -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.hivStatus" text="HIV Status"/>: ${status.hivStatus.hivStatus.displayString}</b>
<div class="box" style="margin:0px">

<table cellspacing cellpadding="0">
<tr><td style="font-weight:bold"><spring:message code="mdrtb.mostRecentTestResult" text="Most Recent Test Result"/>:</td><td>${status.hivStatus.mostRecentTestResult.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.artTreatment" text="ART Treatment"/>:</td><td>${status.hivStatus.artTreatment.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.currentRegimen" text="Current Regimen"/>:</td><td>${status.hivStatus.currentRegimen.displayString}</td></tr>
<tr><td style="font-weight:bold"><spring:message code="mdrtb.mostRecentCd4Count" text="Most Recent CD4 Count"/>:</td><td>${status.hivStatus.mostRecentCd4Count.displayString}</td></tr>
</table>

</div>
<!-- END HIV STATUS BOX -->

</div>

<!-- END OF RIGHT COLUMN -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>