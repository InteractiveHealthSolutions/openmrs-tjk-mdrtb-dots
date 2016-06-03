<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.resistanceProfiles" text="Resistance Profiles"/></b>
<div class="box" style="margin:0px;">

<table cellpadding="0" cellspacing="0" border="0">

<tr>
	<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.patient" text="Patient"/></u></nobr></td>
	<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.enrollment.date" text="Enrollment Date"/></u></nobr></td>
	<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.resistanceProfile" text="Resistance Profile"/></u></nobr></td>
	<td width="99%">&nbsp;</td>
</tr>

<c:set var="i" value="0"/>
<c:forEach var="status" items="${labResultsStatus}">
	<c:if test="${! empty status.drugResistanceProfile.value}">
		<tr 
			<c:if test="${i % 2 == 0 }">class="evenRow"</c:if>
			<c:if test="${i % 2 != 0 }">class="oddRow"</c:if>>
		<td class="tableCell"><nobr><a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${status.patientProgram.patient.id}">${status.patientProgram.patient.personName}</a></nobr></td>
		<td class="tableCell"><nobr><openmrs:formatDate date="${status.patientProgram.dateEnrolled}" format="${_dateFormatDisplay}"/></nobr></td>
		<td class="tableCell"><nobr>${status.drugResistanceProfile.displayString}</nobr></td>
		<td>&nbsp;</td>
		</tr>
		<c:set var="i" value="${i+1}"/>
	</c:if>
</c:forEach>

</table>

</div>

<br/><br/>


<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>