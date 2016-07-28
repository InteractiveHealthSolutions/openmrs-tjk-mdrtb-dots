<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="dotsHeader.jsp"%>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/dotsreports/dotsListPatients.form" />

<openmrs:htmlInclude file="/moduleResources/dotsreports/dotsreports.css"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
		
		$j('#patientTable').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 20,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		});

		$j('#displayDetailsPopup').dialog({ 
			title: 'dynamic', 
			autoOpen: false, 
			draggable: false, 
			resizable: false, 
			width: '95%', 
			modal: true, 
			open: function(a, b) { $j('#displayDetailsPopupLoading').show(); } 
		});
		$j("#displayDetailsPopupIframe").load(function() { $j('#displayDetailsPopupLoading').hide(); });
		
	});

	function loadUrlIntoDetailsPopup(title, urlToLoad) { 
		$j("#displayDetailsPopupIframe").attr("src", urlToLoad); 
		$j('#displayDetailsPopup').dialog('option', 'title', title).dialog('option', 'height', $j(window).height() - 50).dialog('open'); 
	}

</script>

<style>
	th {text-align:left;}
	th.patientTable,td.patientTable {text-align:left; white-space:nowrap; border: 1px solid black; font-size:small; padding-left:5px; padding-right:5px;}
</style>

<form method="get">

	<table class="patientTable" width="100%">
		<tr>
			<td width="25%" valign="top" class="patientTable">
				<table>
					<tr style="border-bottom:2px solid black; height:25px;"><td colspan="2" style="padding-left:10px; background-color:#C0C0C0; font-weight:bold;">
						<spring:message code="dotsreports.choosePatientsToDisplay" text="Choose patients to display"/>:
						<input type="submit" value="<spring:message code="mdrtb.listPatients" text="List"/>"/>
					</td></tr>
					<tr><td colspan="2">
						<br/>
						<table>
							<tr>
								<th><spring:message code="general.name"/>&nbsp;</th>
								<td><input type="text" name="name" value="${name}"/></td>
							</tr>
							<tr>
								<th><spring:message code="general.id"/>&nbsp;</th>
								<td><input type="text" name="identifier" value="${identifier}"/></td>
							</tr>
						</table>
						<br/>
					</td></tr>
					
					<tr><th colspan="2"><spring:message code="dotsreports.enrolledLocation" text="Enrolled Location"/>
							<select name="location">
								<option value=""><spring:message code="mdrtb.all" text="All"/></option>
								<c:forEach items="${patientLocations}" var="l">
									<option value="${l.locationId}" <c:if test="${location == l}">selected</c:if>>${l.name}</option>
								</c:forEach>
							</select>
						</th>
					</tr>
					
					<tr><td colspan="2">
						<br/>
						<table>
						
					<tr><th><spring:message code="dotsreports.minimumAgeAtTxStart" text="Minimum Age at<br>Treatment Start"/>&nbsp;</th><td><input type="text" name="minage" value="${minage}" /></td></tr>
					<tr><th><spring:message code="dotsreports.maximumAgeAtTxStart" text="Maximum Age at<br>Treatment Start"/>&nbsp;</th><td><input type="text" name="maxage" value="${maxage}" /></td></tr>
					
				
						</table>
						<br/>
					<tr>
					<th colspan="2"><spring:message code="dotsreports.gender" text="Gender"/>&nbsp;<select name="gender" >
					<option value=""></option>
					<option value="M"><spring:message code="dotsreports.male" text="Male"/></option>
					<option value="F"><spring:message code="dotsreports.female" text="Female"/></option>
					</select>
					</th>
					</tr>
					<tr><th colspan="2"><br/><spring:message code="dotsreports.enrollment"/></th></tr>
					<tr>
						<td><spring:message code="dotsreports.year"/></td>
						<td><input type="text" name="year" value="${year}"/>
					</tr>
					<tr>
						<td><spring:message code="dotsreports.quarter"/></td>
						<td><input type="quarter" name="quarter" value="${quarter}"/>
					</tr>
					<!-- <tr>
						<td><input type="radio" name="enrollment" value="ever"<c:if test="${empty enrollment || enrollment == 'ever'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="dotsreports.enrollment.ever"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="current"<c:if test="${enrollment == 'current'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="dotsreports.enrollment.current"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="previous"<c:if test="${enrollment == 'previous'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="dotsreports.enrollment.previous"/></td>
					</tr>
					<tr>
						<td><input type="radio" name="enrollment" value="never"<c:if test="${enrollment == 'never'}"> checked</c:if>/>&nbsp;</td>
						<td><spring:message code="dotsreports.enrollment.never"/></td>
					</tr>-->
					
					<c:forEach items="${openmrs:sort(tbProgram.workflows, 'concept.name.name', false)}" var="wf">
						<c:if test="${!wf.retired}">
							<tr><th colspan="2"><br/><dotsreports:format obj="${wf.concept}"/></th></tr>
							<c:forEach items="${openmrs:sort(wf.states, 'concept.name.name', false)}" var="wfs">
								<c:if test="${!wfs.retired}">
									<tr>
										<td><input type="checkbox" name="states" value="${wfs.programWorkflowStateId}"<c:if test="${dotsreports:collectionContains(states, wfs)}"> checked</c:if>/>&nbsp;</td>
										<td><dotsreports:format obj="${wfs.concept}"/><br/></td>
									</tr>				
								</c:if>			
							</c:forEach>
						</c:if>
					</c:forEach>
				</table>
			</td>
			<td valign="top" width="100%" class="patientTable" style="padding-left:10px;">
				<table width="100%">
					<tr style="border-bottom:2px solid black;"><td colspan="2" style="background-color:#C0C0C0; font-weight:bold;">
						<spring:message code="dotsreports.chooseColumnsToDisplay" text="Choose columns to display"/>: 
						<select name="displayMode">
							<option value="basic"<c:if test="${'basic' == param.displayMode}"> selected</c:if>><spring:message code="dotsreports.basicDetails"/></option>
							<option value="dotsreportsShortSummary"<c:if test="${'dotsreportsShortSummary' == param.displayMode}"> selected</c:if>><spring:message code="dotsreports.dotsShortSummary"/></option>
							<option value="dotsreportsCustomList"<c:if test="${'dotsreportsCustomList' == param.displayMode}"> selected</c:if>><spring:message code="dotsreports.dotsCustomList"/></option>
							
							<openmrs:extensionPoint pointId="org.openmrs.dotsreports.listPatientDisplayModes" type="html">
								<option value="${extension.key}"<c:if test="${extension.key == param.displayMode}"> selected</c:if>>
									<spring:message code="${extension.label}"/>
								</option>
							</openmrs:extensionPoint>
						</select>
						<input type="submit" value="<spring:message code="dotsreports.listPatients" text="List"/>"/>
					</td></tr>
				</table>
				<br/>
				<c:set var="extensionFound" value="false"/>
				<openmrs:extensionPoint pointId="org.openmrs.dotsreports.listPatientDisplayModes" type="html">
					<c:if test="${extension.key == param.displayMode}">
						<c:set var="extensionFound" value="true"/>
						<openmrs:portlet moduleId="${extension.moduleId}" url="${extension.portletUrl}" patientIds="${patientIds}" />
					</c:if>
				</openmrs:extensionPoint>
				<c:if test="${extensionFound != 'true'}">
					<c:choose>
						<c:when test="${param.displayMode == 'dotsreportsSummary'}">
							<openmrs:portlet moduleId="dotsreports" url="dotsreportsPatientSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'dotsreportsShortSummary'}">
							<openmrs:portlet moduleId="dotsreports" url="dotsreportsShortSummary" patientIds="${patientIds}" />
						</c:when>
						<c:when test="${param.displayMode == 'dotsreportsCustomList'}">
							<openmrs:portlet moduleId="dotsreports" url="dotsreportsCustomList" patientIds="${patientIds}" />
						</c:when>
						<c:otherwise>
							<table id="patientTable">
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th class="patientTable"><spring:message code="general.id"/></th>
										<th class="patientTable"><spring:message code="general.name"/></th>
										<th class="patientTable"><spring:message code="dotsreports.ageUpper"/></th>
										<th class="patientTable"><spring:message code="dotsreports.gender"/></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${patients}" var="p">
										<tr class="patientRow patientRow${p.patientId}">
											<td class="patientTable" style="white-space:nowrap; width:20px;">
												<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${p.patientId}">
													<img src="${pageContext.request.contextPath}/images/lookup.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
												</a>						
												<openmrs:extensionPoint pointId="org.openmrs.dotsreports.listPatientDetailPortlets" type="html">
													<a href="javascript:void(0)" onClick="loadUrlIntoDetailsPopup('<spring:message code="${extension.title}"/>', '${pageContext.request.contextPath}/module/dotsreports/viewPortlet.htm?patientId=${p.patientId}&moduleId=${extension.moduleId}&id=${extension.moduleId}&url=${extension.portletUrl}'); return false;">
														<img src="${pageContext.request.contextPath}${extension.imagePath}" title="<spring:message code="${extension.title}"/>" border="0" align="top" />
													</a>
												</openmrs:extensionPoint>
											</td>
											<td class="patientTable" style="white-space:nowrap;">${p.patientIdentifier.identifier}</td>
											<td class="patientTable" >${p.familyName}, ${p.givenName} ${p.middleName}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.age}</td>
											<td class="patientTable" style="white-space:nowrap;">${p.gender}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</c:if>
			</td>
		</tr>
	</table>
</form>

<div id="displayDetailsPopup"> 
	<div id="displayDetailsPopupLoading"><spring:message code="general.loading"/></div>
	<iframe id="displayDetailsPopupIframe" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe> 
</div>

<%@ include file="dotsFooter.jsp"%>
