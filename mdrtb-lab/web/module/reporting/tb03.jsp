<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>

<form method="post">
	<h2>TB03 parameters</h2>
	
	<br/>
	
	<table>
		
		<tr>
		    <td align="right"><spring:message code="dotsreports.oblast" /></td>
			<td>
				<select name="oblast">
				    <option value=""></option>
					<c:forEach var="o" items="${oblasts}">
						<option value="${o.id}">${o.name}</option>
					</c:forEach>
				</select>
			</td>
		    
		    <tr>
		    <td align="right"><spring:message code="dotsreports.or" /></td>
		    </tr>
		
			<td align="right"><spring:message code="dotsreports.facility" /></td>
			<td>
				<select name="location">
				    <option value=""></option>
					<c:forEach var="loc" items="${locations}">
						<option value="${loc.id}">${loc.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td align="right"><spring:message code="dotsreports.year" /></td>
			<td><input name="year" type="text" size="4"/></td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td align="right"><spring:message code="dotsreports.quarter" /></td>
			<td><input name="quarter" type="text" size="7"/></td>
		</tr>
		 <tr>
		    <td align="right"><spring:message code="dotsreports.or" /></td>
		    </tr>
		<tr>
			<td align="right"><spring:message code="dotsreports.month" /></td>
			<td><input name="month" type="text" size="7"/></td>
		</tr>

		<tr>
			<td align="right"></td>
			<td>&nbsp;<br/><input type="submit" value="<spring:message code="dotsreports.submit" />"/></td>
		</tr>
	</table>

</form>

<%@ include file="../dotsFooter.jsp"%>