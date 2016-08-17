<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>

<table width="100%">
<tr>
<td align="left">
	<ul id="menu">

		<li style="border-left-width: 0px;" <c:if test='<%= request.getRequestURI().contains("labEntry") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="mdrtb.overview" text="Overview"/></a></li>

		<li <c:if test='<%= request.getRequestURI().contains("microscopy") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/chart/chart.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="labmodule.labEntry.microscopy" text="Microscopy"/></a></li>
		
		<li <c:if test='<%= request.getRequestURI().contains("xpert") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/visits/visits.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="labmodule.labEntry.xpert" text="Xpert MTB/Rif"/></a></li>

		<li <c:if test='<%= request.getRequestURI().contains("hain") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/regimen/manageDrugOrders.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="labmodule.labEntry.hain" text="HAIN"/></a></li>	
	 	
		<li <c:if test='<%= request.getRequestURI().contains("hain2") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${model.patient.patientId}&patientProgramId=${patientProgramId}"><spring:message code="labmodule.labEntry.hain2" text="HAIN 2-го ряда"/></a></li>
	
	</ul>
</td>
</tr>
</table>


