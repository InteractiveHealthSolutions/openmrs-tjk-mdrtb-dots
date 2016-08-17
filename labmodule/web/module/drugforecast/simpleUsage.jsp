<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="..dotsHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<form method="post">
	<h2>Drug Usage Calculation</h2>
	
	<br/>
	
	<table>
		<tr>
			<td align="right">Which patients:</td>
			<td>${cohort.description} - ${cohort.size} patient(s)</i></td>
		</tr>
		<tr>
			<td align="right">Which drugs:</td>
			<td>
				<select name="drugSet">
					<c:forEach var="ds" items="${drugSets}">
						<option value="${ds.conceptId}">${ds.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td align="right">From date:</td>
			<td><input name="fromDate" type="text" size="10" onClick="showCalendar(this)"/></td>
		</tr>
		<tr>
			<td align="right">To date:</td>
			<td><input name="toDate" type="text" size="10" onClick="showCalendar(this)"/></td>
		</tr>
		<tr>
			<td align="right"></td>
			<td>&nbsp;<br/><input type="submit" value="Calculate"/></td>
		</tr>
	</table>	

</form>

