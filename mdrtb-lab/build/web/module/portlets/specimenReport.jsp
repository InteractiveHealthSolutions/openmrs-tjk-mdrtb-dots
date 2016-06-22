<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<b class="boxHeader" style="margin:0px"><spring:message code="${model.name}"/></b>
<div class="box" style="margin:0px;">

<table cellspacing="0" cellpadding="0" border="0">
<tr>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.patient" text="Patient"/></u></nobr></td>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.dateCollected" text="Date Collected"/></u></nobr></td>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.sampleid" text="Sample ID"/></u></nobr></td>
<td width="99%">&nbsp;</td>
</tr>

<c:forEach var="result" items="${model.results}" varStatus="i">
	<c:forEach var="specimen" items="${result.value}">
	<tr 
		<c:if test="${i.count % 2 == 0 }">class="evenRow"</c:if>
		<c:if test="${i.count % 2 != 0 }">class="oddRow"</c:if>>
	<td class="tableCell"><nobr><a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${result.key.id}">${result.key.personName}</a></nobr></td>
	<td class="tableCell"><nobr><a href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?specimenId=${specimen.id}"><openmrs:formatDate date="${specimen.dateCollected}" format="${_dateFormatDisplay}"/></a></nobr></td>
	<td class="tableCell"><nobr>${specimen.identifier}</nobr></td>
	<td>&nbsp;</td>
	</tr>
	</c:forEach>
</c:forEach>

</table>
</div>
