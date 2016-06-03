<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<c:if test="${!empty param.title}"><h4>${param.title}</h4></c:if>

<c:choose>
	<c:when test="${!empty param.patientId}">
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" patientId="${param.patientId}" parameters="${param.parameters}" />
	</c:when>
	<c:when test="${!empty param.patientIds}">
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" patientIds="${param.patientIds}" parameters="${param.parameters}" />
	</c:when>
	<c:otherwise>
		<openmrs:portlet url="${param.url}" id="${param.id}" moduleId="${param.moduleId}" parameters="${param.parameters}" />
	</c:otherwise>
</c:choose>
