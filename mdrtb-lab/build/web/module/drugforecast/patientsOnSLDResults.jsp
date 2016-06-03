<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="../mdrtbHeader.jsp"%>

<style type="text/css">
	table.resultsTable {
		border-collapse: collapse;
	}
	table.resultsTable td, table.resultsTable th {
		border-top: 1px black solid;
		border-bottom: 1px black solid;
	}
</style>
<!-- 
<button onClick="window.location = 'patientsOnSLD.list'"><spring:message code="mdrtb.sldreport.calculate"/></button>
<br/>
 -->

<center>
<h5><spring:message code="mdrtb.sldreport.name"/></h5>
</center>
<br/><br/>
<table width="100%" border="1">
	<tr>
		<td bgcolor="#e0e0ff">
			<spring:message code="mdrtb.sldreport.title"/>
			<b><openmrs:format concept="${drugSet}"/></b>
			
			<spring:message code="mdrtb.sldreport.between"/>
			<b><openmrs:formatDate date="${startDate}"/></b> <spring:message code="mdrtb.sldreport.and"/>  <b><openmrs:formatDate date="${endDate}"/></b>
		</td>
	</tr>
</table>

<br/>

<table cellspacing="0" cellpadding="2" border="1">
	<tr>
		<td colspan="2"><spring:message code="mdrtb.sldreport.oblast"/></td>
		<td colspan="15">${location}</td>
	</tr>
	<tr>
		<td colspan="2"><spring:message code="mdrtb.sldreport.titleOfFacility"/></td>
		<td colspan="15"></td>
	</tr>
	<tr>
		<td colspan="2"><spring:message code="mdrtb.sldreport.dateOfCompletion"/></td>
		<td colspan="15">${today}</td>
	</tr>
	<tr >
		<td colspan="2"><spring:message code="mdrtb.sldreport.responsiblePerson"/>n</td>
		<td colspan="15"></td>
	</tr>
	<tr>
		<td><spring:message code="mdrtb.sldreport.number"/></td>
		<td><spring:message code="mdrtb.sldreport.patientName"/></td>
		<td><spring:message code="mdrtb.sldreport.treatmentStartDate"/></td>
		<td>Cm 1 g</td>
		<td>Am 0.5 g</td>
		<td>Mxf 400 mg</td>
		<td>Lxf 250 mg</td>
		<td>Pto 250 mg</td>
		<td>Cs 250 mg</td>
		<td>PAS 4 g</td>
		<td>Z 400 mg</td>
		<td>E 400 mg</td>
		<td><spring:message code="mdrtb.sldreport.other"/> 1</td>
		<td><spring:message code="mdrtb.sldreport.other"/> 2</td>
		<td><spring:message code="mdrtb.sldreport.other"/> 3</td>
		<td><spring:message code="mdrtb.sldreport.other"/> 4</td>
		<td><spring:message code="mdrtb.sldreport.other"/> 5</td>
	</tr>
	<c:forEach var="obj" items="${patientSet}" varStatus="ct">
		<tr>
			<td>${ct.count}</td>
			<td>${obj.patient.personName.givenName} ${obj.patient.personName.familyName}</td>
			<td>${obj.treatmentStartDate}</td>
			<c:choose>
				<c:when test="${obj.onCapreomycin == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onAmikacin == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onMoxifloxacin == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onLevofloxacin == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onProthionamide == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onCycloserine == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onPAS == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onPyrazinamide == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onEthambutol == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onOther1 == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onOther2 == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onOther3 == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onOther4 == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${obj.onOther5 == true}">
					<td align="center"><b>X</b></td>
				</c:when>
				<c:otherwise>
				
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>
			
		</tr>
	</c:forEach>
</table>

<%@ include file="../mdrtbFooter.jsp"%>