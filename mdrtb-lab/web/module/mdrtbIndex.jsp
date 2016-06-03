<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<!-- <h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="mdrtb.title" /></td></tr></table></h2> -->
<h2><table width="90%"><tr>
<td align="left" width="20%" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/TJK_logo.jpg" alt="logo Tajikistan" style="height:78px; width:87px;" border="0"/>&nbsp;<spring:message code="mdrtb.title" /></td>
<td align="right" width="50%">&nbsp;</td>
<td align="right" width="20%"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/WHO_Euro_logo.jpg" alt="logo WHO Euro" style="height:78px; width:60px;" border="0"/>&nbsp;<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/gfatm_square.jpg" alt="logo GFATM" style="height:78px; width:83px;" border="0"/></td>
</td>

</tr></table></h2>

<br />
<table class="indexTable">
	<tr>
		<td width=60% valign='top'>
			<openmrs:portlet id="mdrtbFindPatient" url="mdrtbFindPatient" parameters="size=full|postURL=${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form|showIncludeVoided=false|viewType=shortEdit" moduleId="mdrtb"/>
			<openmrs:hasPrivilege privilege="Add Patients">
				<br/><br/>
				<openmrs:portlet id="mdrtbAddPatient" url="mdrtbAddPatient" parameters="personType=patient|postURL=mdrtbEditPatient.form|successURL=/module/mdrtb/dashboard/dashboard.form|viewType=shortEdit" moduleId="mdrtb" />
			</openmrs:hasPrivilege>
			
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.bottomLeft" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
		</td>
		<td>&nbsp;&nbsp;&nbsp;
		</td>
		<td valign='top'>
			<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.upperRight" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div class="box">
						
						<b class="boxHeader"><spring:message code="${extension.title}"/></b>
						<ul id="menu">
							<c:forEach items="${extension.links}" var="link">
								<c:choose>
									<c:when test="${fn:startsWith(link.key, 'module/')}">
										<%-- Added for backwards compatibility for most links --%>
										<li><a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a></li>
									</c:when>
									<c:otherwise>
										<%-- Allows for external absolute links  --%>
										<li><a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a></li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</div>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
			
			<table class="indexInner">
			
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="mdrtb.patientLists"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbListPatients.form">
						<spring:message code="mdrtb.viewListPatientPage"/>
					</a>
					<br/><br/>
					<c:forEach var="e" items="${patientLists}">
						<a href="${pageContext.request.contextPath}/${e.value}"><spring:message code="${e.key}"/></a><br/>
					</c:forEach>
					<br/>
					<!-- <c:if test="${showCohortBuilder}">
						<a href="${pageContext.request.contextPath}/cohortBuilder.list">
							<spring:message code="mdrtb.cohortBuilder" text="Cohort Builder"/>
						</a><br/>
					</c:if> -->

					<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.listPatientLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<spring:message code="${extension.title}"/>:
							<c:forEach items="${extension.links}" var="link">
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>&nbsp;&nbsp;
							</c:forEach>
							<br/>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>
				</td></tr>
				
				<c:set var="reportsFound" value="f"/>
				<tr><td style = "background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="mdrtb.mdrreports"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<c:forEach var="entry" items="${reports}" varStatus="varStatus">
						<c:set var="reportsFound" value="t"/>
						<a href="${pageContext.request.contextPath}/${entry.key}">
							${entry.value}
						</a><br/>
					</c:forEach>
					<openmrs:extensionPoint pointId="org.openmrs.mdrtb.linksList.reportLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<c:forEach items="${extension.links}" var="link">
								<c:set var="reportsFound" value="t"/>
								<a href="${pageContext.request.contextPath}/${link.key}">
									${link.value}
								</a><br/>
							</c:forEach>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>
					<c:if test="${reportsFound == 'f'}">
						<i> &nbsp; <spring:message code="mdrtb.noReports"/></i><br/>
					</c:if>
				</td></tr>
				
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					 <b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						&nbsp;&nbsp;<spring:message code="mdrtb.dotsreports" />&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr><td>
					<!-- <a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a><br/> -->
					<a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.dotsreports.reporting.data.DOTS07TJKUpdated"><spring:message code="mdrtb.dotsreport07" /></a><br/>
					<a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.dotsreports.reporting.data.DOTS08TJKUpdated"><spring:message code="mdrtb.dotsreport08" /></a><br/>
				
								
				</td></tr>
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					 <b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						&nbsp;&nbsp;<spring:message code="mdrtb.otherreports" />&nbsp;&nbsp;
					</b>
				</td></tr>
				
				<tr><td>
					<!-- <a href="drugforecast/simpleUsage.list"><spring:message code="mdrtb.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="mdrtb.numberofpatientstakingeachdrug" /></a><br/> -->
					<a href="drugforecast/patientsOnSLD.list"><spring:message code="mdrtb.sldreport" /></a><br/>
					<a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.dotsreports.reporting.data.Form8"><spring:message code="mdrtb.dotsreportform08" /></a><br/>
					<a href="../dotsreports/reporting/reports.form?type=org.openmrs.module.dotsreports.reporting.data.DOTSMDRReport"><spring:message code="mdrtb.dotsmdrreport" /></a><br/>
				</td></tr>
			
				
			
			</table>
		</td>
	</tr>
</table>
<br>&nbsp;<br>

<%@ include file="mdrtbFooter.jsp"%>
