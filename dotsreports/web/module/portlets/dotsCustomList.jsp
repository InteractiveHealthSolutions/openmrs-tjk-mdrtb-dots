<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>


<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/dotsreports/multiselect/js/ui.multiselect.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/dotsreports/multiselect/css/ui.multiselect.css" />
<openmrs:htmlInclude file="/moduleResources/dotsreports/multiselect/js/plugins/localisation/jquery.localisation-min.js" />

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {

		$j('.multiselect').multiselect();
		
		$j('#customPatientTable').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 20,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": true,
			"bAutoWidth": true
		});

		$j('#configureButton').click(function() {
			$j('#columnChooser').show();
			$j('#patientResultTable').hide();
			$j(this).hide();
		});

		$j('#cancelButton').click(function() {
			$j('#columnChooser').hide();
			$j('#patientResultTable').show();
			$j('#configureButton').show();
		});

		<c:if test="${!empty model.columns}">
			$j("#columnChooser").hide();
		</c:if>
		<c:if test="${empty model.columns}">
			$j("#configureButton").hide();
		</c:if>
	});

	function outputToExcel() {
		var excelUrl = '${pageContext.request.contextPath}/module/dotsreports/reporting/customSummaryExport.form?patientIds=${model.patientIds}';
		<c:forEach items="${model.columns}" var="column">
			excelUrl += '&columns=${column}';
		</c:forEach>
		document.location.href = excelUrl;
	}
	
</script>

<style>
	.multiselect {
		width: 800px;
		height: 400px;
	}
	.ui-multiselect div.available {
		position: absolute; 
		left: 820px; 
	}
</style>

<div style="padding-left:5px; padding-right:5px; width:100%;">
	<div id="columnChooser">
		<select id="columnSelector" class="multiselect" multiple="multiple" name="columns">
			<c:forEach items="${model.columns}" var="column">
				<option value="${column}" selected>
					${model.availableColumns[column]}
				</option>
			</c:forEach>
			<c:forEach items="${model.availableColumns}" var="entry">
				<c:if test="${dotsreports:arrayContains(model.columns, entry.key) == 'false'}">
					<option value="${entry.key}">
						${entry.value}
					</option>
				</c:if>
			</c:forEach>
		</select>
		<b><spring:message code="dotsreports.optionally"/>, <spring:message code="dotsreports.sortBy"/></b><br/>
		<select name="sort">
			<option value=""></option>
			<c:forEach items="${model.availableColumns}" var="entry">
				<option value="${entry.key}"<c:if test="${model.sort == entry.key}"> selected</c:if>>
					${entry.value}
				</option>
			</c:forEach>
		</select>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<button><spring:message code="dotsreports.outputToList"/></button>
		&nbsp;&nbsp;
		<input type="button" value="<spring:message code="dotsreports.cancel"/>" id="cancelButton"/>
	</div>
</div>

<div id="patientResultTable">
	<c:if test="${!empty model.columns}">
		<div style="padding:5px;">
			<input type="button" id="configureButton" value="<spring:message code="dotsreports.configureColumns" text="Configure Columns"/>"/>
			<button onclick="outputToExcel(); return false;"><spring:message code="dotsreports.exportToExcel"/></button>
		</div>
		<hr/>
		<c:choose>
			<c:when test="${empty model.data}">
				<br/>
				<i>Il n'ya pas de patients</i>
			</c:when>
			<c:otherwise>
				<table id="customPatientTable" width="100%">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<c:forEach items="${model.columns}" var="c">
								<th class="patientTable" style="padding-right:10px;">${model.availableColumns[c]}</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${model.data}" var="p">
							<tr class="patientRow patientRow${p.patientId}">
								<td class="patientTable" style="white-space:nowrap; width:20px;">
									<a href="dashboard/dashboard.form?patientId=${p.patientId}">
										<img src="${pageContext.request.contextPath}/images/lookup.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
									</a>
								</td>
								<c:forEach items="${model.columns}" var="c">
									<td class="patientTable" style="padding-right:10px;">
										<c:if test="${!empty p[c]}">
											<c:choose>
												<c:when test="${dotsreports:instanceOf(p[c], 'java.util.Date')}">
													<openmrs:formatDate date="${p[c]}" format="${_dateFormatDisplay}"/>
												</c:when>
												<c:otherwise>${p[c]}</c:otherwise>
											</c:choose>
										</c:if>
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:otherwise>
		</c:choose>
	</c:if>
</div>