<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>

<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>

<h2>${location.name} <openmrs:formatDate format="${_dateFormatDisplay}" date="${runDate}"/></h2>

<openmrs:portlet id="summaryPortlet" moduleId="mdrtb" url="${view}" patientIds="${patientIds}" parameters="locationId=${location.locationId}" />

<%@ include file="/WEB-INF/template/footer.jsp" %>
