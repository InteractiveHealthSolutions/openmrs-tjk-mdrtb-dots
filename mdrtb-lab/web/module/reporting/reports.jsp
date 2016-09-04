<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="wgt" uri="/WEB-INF/view/module/htmlwidgets/resources/htmlwidgets.tld" %>
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/mdrtb/mdrtbIndex.form" />			

<style>
	.reportTable th,td {
		text-align:left; vertical-align:top;
	}
	simple-html-dataset table {
		width:100%; padding:5px; border:1px solid black;
	}
</style>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j('#reportData').load(function(event){
			var ht = $j("#reportData").height($j(window).height()-80);
		});
		$j('#cancelButton').click(function(event){
			document.location.href = '${pageContext.request.contextPath}/module/mdrtb/mdrtbIndex.form';
		});
	});
</script>

<div id="page">
	<c:choose>
		<c:when test="${empty report}">
			<table style="border 1px solid black;">
				<tr><th style="background-color:#C0C0C0; font-weight:bold;">Available Reports</th></tr>
				<c:forEach items="${availableReports}" var="r">
					<tr><th>
						<a href="reports.form?type=${r.class.name}">${r.name}</a><br/>
						<span style="font-size:smaller;">${r.description}</span>
					</th></tr>				
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<table width="100%">
				<tr>
					<td style="border:1px solid black; padding:5px;">
						<form id="reportParameterForm" action="render.form" target="reportData">
							<input type="hidden" name="type" value="${type.name}"/>
							<b>${report.name}</b><br/>
							<span style="font-size:smaller;">${report.description}</span><br/><br/>
							<b><spring:message code="dotsreports.oblast"/></b><br/>
							<select name="oblast">
								 <option value=""></option>
								<c:forEach items="${oblasts}" var="o">
									<option value="${o.id}">${o.name}</option>
								</c:forEach>
							</select>
							
							<br/> or <br/>
							<c:forEach items="${report.parameters}" var="p">
								<b>${p.label}:</b><br/>
								<wgt:widget id="param-${p.name}" name="p.${p.name}" type="${p.type.name}" defaultValue="${parameters[p.name]}"/>
								<br/><br/>
							</c:forEach>
							<b><spring:message code="mdrtb.outputFormat"/></b><br/>
							<select name="format">
								<c:forEach items="${report.renderingModes}" var="mode">
									<option value="${mode.label}">${mode.label}</option>
								</c:forEach>
								<option value=""><spring:message code="mdrtb.preview"/></option>
							</select>
							<br/><br/>
							<input type="submit" id="actionButton" value="<spring:message code="mdrtb.run"/>" />
							<input type="button" id="cancelButton" value="<spring:message code="mdrtb.close"/>" />
						</form>
					</td>
					<td style="width:100%; white-space:nowrap; vertical-align:top;">
						<iframe id="reportData" name="reportData" style="width:100%;"></iframe>
					</td>
				</tr>
			</table>
		</c:otherwise>
	</c:choose>
</div>