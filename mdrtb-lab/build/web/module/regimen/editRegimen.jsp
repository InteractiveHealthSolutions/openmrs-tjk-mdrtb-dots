<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/mdrtb/regimen/manageRegimens.form"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/mdrtb/drugOrders.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/drugOrders.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patient.patientId}"/>
<mdrtb:regimenPortlet id="orderChange" patientId="${patientId}" type="${type}" changeDate="${changeDate}" url="orderChangePortlet"/>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>
