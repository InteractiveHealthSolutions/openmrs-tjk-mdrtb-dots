<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>
<c:set var="mdrtbId" scope="page" value="" />
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/index.htm" />

	<%-- Header showing preferred name, id, and treatment status --%>
		<div id="patientHeader" class="boxHeader">
		
		<div align="right">
		
			<a href="newPatient.form?patientId=${model.patient.id}">
				<img title="Edit" src="${pageContext.request.contextPath}/moduleResources/labmodule/edit.gif" alt="edit" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
			</a>
	
		</div>
		
		<div id="patientHeaderPatientName">${model.patient.personName}</div>
		<div id="patientHeaderPreferredIdentifier">
			<c:if test="${!empty model.primaryIdentifier}">
				<span class="patientHeaderPatientIdentifier"><span id="patientHeaderPatientIdentifierType">${model.primaryIdentifier.identifierType.name}:</span> ${model.primaryIdentifier.identifier}</span>
			</c:if>
		
			<!--  TODO: delete this once we are sure we don't want it
			<c:if test="${fn:length(model.patient.activeIdentifiers) > 0}">
				<c:if test="${!empty model.obj.patientIdentifier}">
					<span class="patientHeaderPatientIdentifier"><span id="patientHeaderPatientIdentifierType">${model.obj.patientIdentifier.identifierType.name}:</span> ${model.obj.patientIdentifier.identifier}</span>
					<c:set var="mdrtbId" scope="page" value="${model.obj.patientIdentifier}" />
				</c:if>
				<c:if test="${empty model.obj.patientIdentifier}">
					<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="0" end="0">
						<span class="patientHeaderPatientIdentifier"><span id="patientHeaderPatientIdentifierType">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>:</span> ${identifier.identifier}</span>
					</c:forEach>
				</c:if>
			</c:if>
			-->
		</div>
		<table id="patientHeaderGeneralInfo">
			<tr>
				<td id="patientHeaderPatientGender">
					<c:if test="${model.patient.gender == 'M'}"><img src="${pageContext.request.contextPath}/images/male.gif" alt='<spring:message code="Person.gender.male"/>' id="maleGenderIcon"/></c:if>
					<c:if test="${model.patient.gender == 'F'}"><img src="${pageContext.request.contextPath}/images/female.gif" alt='<spring:message code="Person.gender.female"/>' id="femaleGenderIcon"/></c:if>
				</td>
				<td id="patientHeaderPatientAge">
					<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.beforePatientHeaderPatientAge" type="html" parameters="patientId=${model.patient.patientId}" />
					<c:if test="${model.patient.age > 0}">${model.patient.age} <spring:message code="Person.age.years"/></c:if>
					<c:if test="${model.patient.age == 0}">< 1 <spring:message code="Person.age.year"/></c:if>
					<span id="patientHeaderPatientBirthdate"><c:if test="${not empty model.patient.birthdate}">(<c:if test="${model.patient.birthdateEstimated}">~</c:if><openmrs:formatDate date="${model.patient.birthdate}" type="medium"  format="${_dateFormatDisplay}"/>)</c:if><c:if test="${empty model.patient.birthdate}"><spring:message code="Person.age.unknown"/></c:if></span>
				</td>
				
				<!--  
				<openmrs:globalProperty key="use_patient_attribute.tribe" defaultValue="false" var="showTribe"/>
				<c:if test="${showTribe}">
					<td id="patientHeaderPatientTribe">
						<spring:message code="Patient.tribe"/>:
						<b>${model.patient.tribe.name}</b>
						<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientTribe" type="html" parameters="patientId=${model.patient.patientId}" />
					</td>
				</c:if>
				-->
				
				<c:if test="${not empty model.patient.attributeMap['Health Center']}">
					<td id="patientHeaderHealthCenter">
						<spring:message code="mdrtb.healthcenter"/>:
						<b>${model.patient.attributeMap['Health Center'].hydratedObject}</b>
					</td>
				</c:if>
				<td id="patientDashboardHeaderExtension">
					<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.Header" type="html" parameters="patientId=${model.patient.patientId}" />
				</td>
				<td style="width: 100%;">&nbsp;</td>
				
				
				<!--  TODO: delete this once we are sure we don't need it 
				<td id="patientHeaderOtherIdentifiers">
					
					<c:if test="${fn:length(model.patient.activeIdentifiers) > 1}">
						<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="1" end="1">
							<c:if test="${mdrtbId != identifier}">
								<span class="patientHeaderPatientIdentifier">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>: ${identifier.identifier}</span>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${fn:length(model.patient.activeIdentifiers) > 2}">
						<div id="patientHeaderMoreIdentifiers">
							<c:forEach var="identifier" items="${model.patient.activeIdentifiers}" begin="2">
								<c:if test="${mdrtbId != identifier}">
									<span class="patientHeaderPatientIdentifier">${identifier.identifierType.name}<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.afterPatientHeaderPatientIdentifierType" type="html" parameters="identifierLocation=${identifier.location.name}"/>: ${identifier.identifier}</span>
								</c:if>
							</c:forEach>
						</div>
					</c:if>
				</td>
				
				<c:if test="${fn:length(model.patient.activeIdentifiers) > 2}">
					<td width="32">
						<small><a id="patientHeaderShowMoreIdentifiers" onclick="return showMoreIdentifiers()" title='<spring:message code="patientDashboard.showMoreIdentifers"/>'><spring:message code="general.nMore" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/></a></small>
					</td>
				</c:if>
				-->
				
			</tr>
		</table>
	</div>
	<div style="padding:5px;">
		<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.bottomPatientHeader" type="html" parameters="patientId=${model.patient.patientId}" />
	</div>
	<script type="text/javascript">
		function showMoreIdentifiers() {
			if (identifierElement.style.display == '') {
				linkElement.innerHTML = '<spring:message code="general.nMore" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "none";
			}
			else {
				linkElement.innerHTML = '<spring:message code="general.nLess" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "";
			}
		}
		
		var identifierElement = document.getElementById("patientHeaderMoreIdentifiers");
		var linkElement = document.getElementById("patientHeaderShowMoreIdentifiers");
		if (identifierElement)
			identifierElement.style.display = "none";
		
	</script>
	