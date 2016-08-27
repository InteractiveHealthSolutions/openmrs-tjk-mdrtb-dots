<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>

<form method="post">
	<h2>TB03 parameters</h2>
	
	<br/>
	
	<table>
		
		<tr>
			<td align="right">Location:</td>
			<td>
				<select name="location">
					<c:forEach var="loc" items="${locations}">
						<option value="${loc.id}">${loc.name}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td align="right">Year</td>
			<td><input name="year" type="text" size="4"/></td>
		</tr>
		<tr>
			<td align="right">Quarter</td>
			<td><input name="quarter" type="text" size="7"/></td>
		</tr>

		<tr>
			<td align="right"></td>
			<td>&nbsp;<br/><input type="submit" value="Submit"/></td>
		</tr>
	</table>

</form>

<%@ include file="../dotsFooter.jsp"%>