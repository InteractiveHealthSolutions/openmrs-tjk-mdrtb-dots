<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="dotsHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/dotsreports/dotsreports.css"/>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<!-- <h2><table><tr><td><img src="${pageContext.request.contextPath}/moduleResources/dotsreports/who_logo.bmp" alt="logo WHO" style="height:50px; width:50px;" border="0"/></td><td>&nbsp;<spring:message code="dotsreports.title" /></td></tr></table></h2> -->
<h2><table width="90%"><tr>
<td align="left" width="30%" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/dotsreports/TJK_logo.jpg" alt="logo Tajikistan" style="height:78px; width:87px;" border="0"/>&nbsp;<spring:message code="dotsreports.title" /></td>
<td align="right" width="40%">&nbsp;</td>
<td align="right" width="20%"><img src="${pageContext.request.contextPath}/moduleResources/dotsreports/WHO_Euro_logo.jpg" alt="logo WHO Euro" style="height:78px; width:60px;" border="0"/>&nbsp;<img src="${pageContext.request.contextPath}/moduleResources/dotsreports/gfatm_square.jpg" alt="logo GFATM" style="height:78px; width:83px;" border="0"/></td>
</td>

</tr></table></h2>

<br />
<table class="indexTable">
	<tr>
		<td width=60% valign='top'>

			
			<table class="indexInner">
			
				<%--  <tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="dotsreports.patientLists"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<a href="${pageContext.request.contextPath}/module/dotsreports/dotsListPatients.form">
						<spring:message code="dotsreports.viewListPatientPage"/>
					</a>
					<br/><br/>
					<c:forEach var="e" items="${patientLists}">
						<a href="${pageContext.request.contextPath}/${e.value}"><spring:message code="${e.key}"/></a><br/>
					</c:forEach>
					<br/>
					<%-- <c:if test="${showCohortBuilder}">
						<a href="${pageContext.request.contextPath}/cohortBuilder.list">
							<spring:message code="dotsreports.cohortBuilder" text="Cohort Builder"/>
						</a><br/>
					</c:if>

					<openmrs:extensionPoint pointId="org.openmrs.dotsreports.linksList.listPatientLinks" type="html">
						<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
							<spring:message code="${extension.title}"/>:
							<c:forEach items="${extension.links}" var="link">
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>&nbsp;&nbsp;
							</c:forEach>
							<br/>
						</openmrs:hasPrivilege>
					</openmrs:extensionPoint>
				</td></tr> --%>
				
				<c:set var="reportsFound" value="f"/>
				<tr><td style = "background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">&nbsp;&nbsp;
						<spring:message code="dotsreports.reports"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr class="${rowClass}"><td>
					<c:forEach var="entry" items="${reports}" varStatus="varStatus">
						<c:set var="reportsFound" value="t"/>
						<a href="${pageContext.request.contextPath}/${entry.key}">
							${entry.value}
						</a><br/>
					</c:forEach>
					<openmrs:extensionPoint pointId="org.openmrs.dotsreports.linksList.reportLinks" type="html">
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
						<i> &nbsp; <spring:message code="dotsreports.noReports"/></i><br/>
					</c:if>
				</td></tr>
				<%--<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						&nbsp;&nbsp;<spring:message code="dotsreports.viewdrugrequirements" />&nbsp;&nbsp;
					</b>
				</td></tr>
				<tr><td>
					<a href="drugforecast/simpleUsage.list"><spring:message code="dotsreports.simpleDrugUsage"/></a><br/>
					<a href="drugforecast/patientsTakingDrugs.list"><spring:message code="dotsreports.numberofpatientstakingeachdrug" /></a>
				</td></tr> --%>
			
			</table>
		</td>
	</tr>
</table>
<br>&nbsp;<br>

<%@ include file="dotsFooter.jsp"%>
