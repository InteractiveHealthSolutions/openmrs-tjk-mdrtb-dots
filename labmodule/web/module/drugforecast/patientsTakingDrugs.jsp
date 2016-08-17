<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<form method="post">
	<h2>Count Patients Taking Drugs</h2>
	
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
			<td align="right">On date:</td>
			<td><input name="onDate" type="text" size="10" onClick="showCalendar(this)"/></td>
		</tr>
<%--
		<tr>
			<td align="right">Method:</td>
			<td>
				<input type="radio" name="method" value="generic" checked="true"/>Generics
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="radio" name="method" value="drug"/>Drug formulations
			</td>
		</tr>
--%>
		<tr>
			<td align="right"></td>
			<td>&nbsp;<br/><input type="submit" value="Calculate"/></td>
		</tr>
	</table>

<input type="hidden" name="method" value="drug"/>	

</form>

