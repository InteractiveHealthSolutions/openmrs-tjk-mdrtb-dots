<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>

<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->

<!-- CUSTOM JQUERY  -->


<c:set var="defaultReturnUrl" value="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientProgramId=${patientProgramId}"/>

<div> <!-- start of page div -->

&nbsp;&nbsp;<a href="${!empty returnUrl ? returnUrl : defaultReturnUrl}"><spring:message code="mdrtb.back" text="Back"/></a>
<br/><br/>

<!-- VIEW BOX -->
<b class="boxHeader"><spring:message code="mdrtb.forms"/></b>
<div class="box">
<spring:message code="mdrtb.selectForm" text="Please select the form to use"/>:
<br/>
<table>
<tr><td>
<!-- <a href="${pageContext.request.contextPath}/module/mdrtb/form/${formType eq 'intake' ? 'intake' : 'followup'}.form?patientId=${patientId}&patientProgramId=${patientProgramId}&returnUrl=${returnUrl}&encounterId=-1">
<spring:message code="mdrtb.basicForm" text="Basic Form"/>
</a> -->
</td></tr>
<c:forEach var="form" items="${forms}">
<tr><td>
<a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${patientId}&formId=${form.id}&mode=NEW">
${form.name}
</a>
</td></tr>
</c:forEach>
</table>
</div>
<!-- END VIEW BOX -->

</div> <!-- end of page div -->

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>