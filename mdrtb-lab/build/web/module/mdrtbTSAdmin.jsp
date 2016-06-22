<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script type="text/javascript">
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
</script>
<h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="mdrtb.title" />&nbsp;<spring:message code="mdrtb.managetreatmentsupporters"/></td></tr></table></h2>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />
<br>
<br>

<c:if test="${!empty listObj}">

	<!-- givenName, familyName, gender, cityVillage, DOB, phone -->
	<a href="mdrtbTSAdmin.form?personId="><spring:message code="mdrtb.createatreatmentsupporter" /></a><br>
	<br>
	<form method="post">
	<table class="portletTable" style="font-size:80%;">
		<tr>
			<th><spring:message code="mdrtb.removepersonastreatmentsupporter" /></th>
			<th><spring:message code="mdrtb.name" /></th>
			<th><spring:message code="mdrtb.surname" /></th>
			<th><spring:message code="mdrtb.gender" /></th>
			<th><spring:message code="mdrtb.healthcentervillageofsupporterTS" /></th>
			<th><spring:message code="mdrtb.treatmentsupporterbirthdateTS" /></th>
			<th><spring:message code="mdrtb.phone" /></th>
			<th><spring:message code="mdrtb.tsActive" /></th>
		</tr>
		<c:set var="itemCount" scope="page" value="0"/>
		<c:forEach items="${listObj}" var="ts" varStatus="varStatus">
			<c:set var="itemCount" scope="page" value="${varStatus.index}"/>
			<c:set var="rowClass" scope="page">
					<c:choose><c:when test="${varStatus.index % 2 == 0}">oddRow</c:when><c:otherwise>evenRow</c:otherwise></c:choose>
			</c:set>
			<tr class="${rowClass}">
				<td style="text-align:center"><input type="checkbox" value="${ts.person.personId}" name="del_checkbox_${varStatus.count}"></td>
				<td><a href="mdrtbTSAdmin.form?personId=${ts.person.personId}">${ts.person.givenName}</a></td>
				<td><a href="mdrtbTSAdmin.form?personId=${ts.person.personId}">${ts.person.familyName}</a></td>
				<td>${ts.person.gender}</td>
				<td><c:if test="${!empty ts.person.addresses}"><c:set var="stopad" scope="page" value="0" /><c:forEach items="${ts.person.addresses}" var="address" varStatus="varStatusAd"><c:if test="${stopad==0}">${address.cityVillage}</c:if><c:set var="stopad" scope="page" value="1" /></c:forEach></c:if></td>
				<td><openmrs:formatDate date="${ts.person.birthdate}" format="${dateFormat}" /></td>
				<td><c:forEach items="${ts.phoneNumbers}" var="phone" varStatus="varPH">${phone.valueText}<c:if test="${!varPH.last}">,<br></c:if></c:forEach></td>
				<td>${ts.active.valueCoded.name.name}</td>
			</tr>	
		</c:forEach>
	</table><br>
	&nbsp;&nbsp;&nbsp;
	<input type="submit" name="submit" value="<spring:message code="mdrtb.delete" />">&nbsp;&nbsp;
	</form>
</c:if>
<c:if test="${!empty formObj}">

	<form method="post">
	<table class="portletTable">
		<tr>
			<td><span style="color:black">*</span> <spring:message code="mdrtb.name" /></td>
			<td>
				<spring:bind path="formObj.person.names[0].givenName">
					<input type="text" style="width:100px" name="${status.expression}" id="givenName" value="${status.value}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><span style="color:black">*</span> <spring:message code="mdrtb.surname" /></td>
			<td>
				<spring:bind path="formObj.person.names[0].familyName">
					<input type="text" style="width:100px" name="${status.expression}" id="familyName" value="${status.value}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
				
			</td>
		</tr>
		<tr>
			<td><span style="color:black">*</span> <spring:message code="mdrtb.treatmentsupporterbirthdate" /></td>
			<td>
				<spring:bind path="formObj.person.birthdate">
					<input type="text" style="width:100px" name="${status.expression}" id="resTestCompleteCultureDate" value="${status.value}" onMouseDown="$(this).date_input()">
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><span style="color:black">*</span> <spring:message code="mdrtb.gender" /></td>
			<td>
				<spring:bind path="formObj.person.gender">
					<select name="${status.expression}" id="gender">
						<option value=" "></option>
						
									<option value="M"
										<c:if test="${'M' == status.value}">
											SELECTED
										</c:if>
									><spring:message code="mdrtb.male" /></option>
									
									<option value="F"
										<c:if test="${'F' == status.value}">
											SELECTED
										</c:if>
									><spring:message code="mdrtb.female" /></option>
						
					</select>
					<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
					</c:if>
					</spring:bind>
			</td>
		</tr>	
		<tr>
			<td><spring:message code="mdrtb.healthcentervillageofsupporter" /></td>
			<td>
				<spring:bind path="formObj.person.addresses[0].cityVillage">
					<input type="text" style="width:100px" name="${status.expression}" id="cityVillage" value="${status.value}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><spring:message code="mdrtb.phone" /></td>
			<td>
				<spring:bind path="formObj.phoneNumbers[0].valueText">
					<input type="text" style="width:100px" name="${status.expression}" id="phone" value="${status.value}" >
						<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
						</c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><spring:message code="mdrtb.tsActive" /></td>
			<td>
				<spring:bind path="formObj.active.valueCoded">
					<select name="${status.expression}" id="active">
							
							<c:forEach items="${activityAnswers}" var="answer">
								<option value="${answer}" 
									<c:if test="${answer == status.value}">
										SELECTED
									</c:if>
								>${answer.name.name}</option>
							</c:forEach>
					</select>
				</spring:bind>
			</td>
		</tr>
		
	</table><span style="color:black">* = <spring:message code="mdrtb.required" /></span><br><br>
	<input type="submit" name="submit" value="<spring:message code="mdrtb.save" />">&nbsp;&nbsp;
	<input type="submit" name="submit" value="<spring:message code="mdrtb.cancel" />">
	</form>
</c:if>



<c:if test="${empty formObj}">
<c:if test="${empty listObj}">
	<!-- givenName, familyName, gender, cityVillage, DOB, phone -->
	<a href="mdrtbTSAdmin.form?personId="><spring:message code="mdrtb.createatreatmentsupporter" /></a><br>
	<br>
	<form method="post">
	<table class="portletTable" style="font-size:80%;">
		<tr>
			<th><spring:message code="mdrtb.removepersonastreatmentsupporter" /></th>
			<th><spring:message code="mdrtb.name" /></th>
			<th><spring:message code="mdrtb.surname" /></th>
			<th><spring:message code="mdrtb.gender" /></th>
			<th><spring:message code="mdrtb.healthcentervillageofsupporterTS" /></th>
			<th><spring:message code="mdrtb.treatmentsupporterbirthdateTS" /></th>
			<th><spring:message code="mdrtb.phone" /></th>
			<th><spring:message code="mdrtb.tsActive" /></th>
		</tr>
		<c:set var="itemCount" scope="page" value="0"/>
		<c:if test="${itemCount == 0}">
			<Tr><td colspan="7" style="text-align='center'"><i><spring:message code="mdrtb.none" /></i></td></Tr>
		</c:if>
	</table><br>
	&nbsp;&nbsp;&nbsp;
	<input type="submit" name="submit" value="<spring:message code="mdrtb.delete" />">&nbsp;&nbsp;
	</form>
</c:if>
</c:if>


<%@ include file="mdrtbFooter.jsp"%>