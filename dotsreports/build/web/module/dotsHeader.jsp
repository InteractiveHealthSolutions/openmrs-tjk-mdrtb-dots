<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ page import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<openmrs:htmlInclude file="/openmrs.js" />
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/dwr/engine.js" />
		<openmrs:htmlInclude file="/dwr/util.js" />
		<openmrs:htmlInclude file="/dwr/interface/DWRAlertService.js" />
		
		<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
		
		<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
		<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
		<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />
		
		<!-- set the date format to use throughout the module -->
		<c:set var="_dateFormatDisplay" value="dd/MMM/yyyy" scope="request"/>
		
		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title" /></title>
			</c:otherwise>
		</c:choose>

		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
			var $j = jQuery.noConflict();
		</script>
	</head>

<body>
<div id="pageBody">
<div id="userBar">
  <openmrs:authentication>
	<c:if test="${authenticatedUser != null}">
		<span id="userLoggedInAs" class="firstChild"> <spring:message
			code="header.logged.in" /> ${authenticatedUser.personName} </span>
		<span id="userLogout"> <a
			href='<%= request.getContextPath() %>/logout'><spring:message
			code="header.logout" /></a> </span>
		<span>
			<a href="${pageContext.request.contextPath}/options.form"><spring:message code="Navigation.options"/></a>
		</span>	
	</c:if>
	<c:if test="${authenticatedUser == null}">
		<c:redirect url="/index.htm" />
		<span id="userLoggedOut" class="firstChild"> <spring:message code="header.logged.out" /> </span>
		<span id="userLogIn">
			<a href="${pageContext.request.contextPath}/login.htm"><spring:message code="header.login" /></a>
		</span>
	</c:if>
	</openmrs:authentication> 

	<span id="userHelp"> <a href='<%= request.getContextPath() %>/moduleResources/dotsreports/MDR-TB_OpenMRS_Documentation.pdf'>
	<spring:message code="dotsreports.title" /> <spring:message code="header.help" /></a> </span>
	
	<span id="classicViewLink">
		<c:set var="id" value="${!empty patientId ? patientId : program.patient.id}"/>
		<c:if test="${!empty id}">
			<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${id}">
		</c:if>
		<c:if test="${empty id}">
			<a href="${pageContext.request.contextPath}/index.htm">
		</c:if>
				<spring:message code="dotsreports.switchToclassicView"/>
			</a>
	</span>
	
</div>

<div id="banner"><%@ include file="/WEB-INF/template/banner.jsp"%>
</div>



<openmrs:hasPrivilege privilege="View Navigation Menu">

	<div id="gutter"><%@ include file="dotsGutter.jsp"%>
	</div>

</openmrs:hasPrivilege>

<div id="content"><script type="text/javascript">
				//// prevents users getting popup alerts when viewing pages
				var handler = function(ex) {
					if (typeof ex == "string")
						window.status = "DWR warning/error: " + ex;
				};
				DWREngine.setErrorHandler(handler);
				DWREngine.setWarningHandler(handler);
			</script> <openmrs:forEachAlert>
	<c:if test="${varStatus.first}">
		<div id="alertOuterBox">
		<div id="alertInnerBox">
	</c:if>
	<div class="alert"><a href="#markRead"
		onClick="return markAlertRead(this, '${alert.alertId}')" HIDEFOCUS
		class="markAlertRead"> <img
		src="<%= request.getContextPath() %>/images/markRead.gif"
		alt='<spring:message code="Alert.mark"/>'
		title='<spring:message code="Alert.mark"/>' /> </a> ${alert.text}
	${alert.dateToExpire} <c:if test="${alert.satisfiedByAny}">
		<i class="smallMessage">(<spring:message
			code="Alert.mark.satisfiedByAny" />)</i>
	</c:if></div>
	<c:if test="${varStatus.last}"></div>
<div id="alertBar"><img
	src="<%= request.getContextPath() %>/images/alert.gif" align="center"
	alt='<spring:message code="Alert.unreadAlert"/>'
	title='<spring:message code="Alert.unreadAlert"/>' /> <c:if
	test="${varStatus.count == 1}">
	<spring:message code="Alert.unreadAlert" />
</c:if> <c:if test="${varStatus.count != 1}">
	<spring:message code="Alert.unreadAlerts"
		arguments="${varStatus.count}" />
</c:if></div>
</div>
</c:if>
</openmrs:forEachAlert>

<c:if test="${msg != null}">
	<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}"
		arguments="${msgArgs}" /></div>
</c:if>
<c:if test="${err != null}">
	<div id="openmrs_error"><spring:message code="${err}"
		text="${err}" arguments="${errArgs}" /></div>
</c:if>

<!--  </body>
</html> -->