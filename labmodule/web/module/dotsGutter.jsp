<ul id="navList">
	<openmrs:hasPrivilege privilege="View Lab Functions">
		<li>
			<a href="${pageContext.request.contextPath}/module/labmodule/labIndex.form"><spring:message code="labmodule.title.homepage" /></a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li><a href="${pageContext.request.contextPath}/admin">
			<spring:message code="Navigation.administration" /></a>
		</li>
	</openmrs:hasPrivilege>
</ul>