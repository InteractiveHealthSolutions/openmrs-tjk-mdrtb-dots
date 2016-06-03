<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="../mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<form method="post">
	<h2><spring:message code="mdrtb.sldreport.title"/></h2>
	
	<br/>
	
	<table>
		
		<tr>
			<td><spring:message code="mdrtb.sldreport.district"/></td>
			<td>
				
				<select name="location">
					<c:forEach var="loc" items="${locations}">
					  <c:choose>
						<c:when test="${loc.countyDistrict != null}">
							<option value="${loc.countyDistrict}">${loc.name}</option>
						</c:when>
					 </c:choose>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td align="right"><spring:message code="mdrtb.sldreport.startDate"/>:</td>
			<td><input name="startDate" type="text" size="10" onClick="showCalendar(this)"/></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="mdrtb.sldreport.endDate"/>:</td>
			<td><input name="endDate" type="text" size="10" onClick="showCalendar(this)"/></td>
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
			<td>&nbsp;<br/><input type="submit" value="<spring:message code="mdrtb.sldreport.calculate"/>"/></td>
		</tr>
	</table>
	

</form>

<%@ include file="../mdrtbFooter.jsp"%>