<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>

<style type="text/css">
	table.resultsTable {
		border-collapse: collapse;
	}
	table.resultsTable td, table.resultsTable th {
		border-top: 1px black solid;
		border-bottom: 1px black solid;
	}
</style>

<button onClick="window.location = 'patientsTakingDrugs.list'">Another calculation</button>
<br/>

<table width="100%">
	<tr>
		<td bgcolor="#e0e0ff">
			Count Patients Taking
			<b><openmrs:format concept="${drugSet}"/></b>
			for
			<b>${cohort.description}</b>
			<b>(${cohort.size} patients)</b>
			on
			<b><openmrs:formatDate date="${onDate}"/></b> 
		</td>
	</tr>
</table>

<br/>

<table class="resultsTable" cellspacing="0" cellpadding="2">
	<tr>
		<th colspan="2">Medication</th>
		<th># patients</th>
	</tr>
	<c:forEach var="row" items="${patientsTaking}">
		<tr>
			<c:choose>
				<c:when test="${method == 'drug'}">
					<td><b>${row.key.name}</b></td>
					<td><small>${row.key.concept.name} ${row.key.doseStrength} ${row.key.units} ${row.key.dosageForm}</small></td>
				</c:when>
				<c:otherwise>
					<td colspan="2"><b><openmrs:format concept="${row.key}"/></b></td>
				</c:otherwise>
			</c:choose>
			<td align="right"><b>${row.value}</b></td>
		</tr>
	</c:forEach>
</table>

