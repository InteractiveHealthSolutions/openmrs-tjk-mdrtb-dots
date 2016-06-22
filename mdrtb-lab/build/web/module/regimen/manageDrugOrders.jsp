<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/mdrtb/regimen/manageDrugOrders.form"/>

<openmrs:htmlInclude file="/moduleResources/mdrtb/jquery.cookie.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/mdrtb/drugOrders.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/drugOrders.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${patient.patientId}" parameters="patientProgramId=${patientProgramId}"/>

<script type="text/javascript">
	$j(document).ready(function() {
		var savedTab = $j.cookie("mdrtbManageDrugOrderSelectedTab");
		if (savedTab != null && savedTab != '') {
			toggleSection(savedTab);
		}
		else {
			toggleSection('Drug');
		}

		$j('#existingTreatmentSelector').change(function() {
			var val = $j(this).val();
			if (val != '') {
				var split = val.split(":");
				$j('#existingSelectorType').val(split[0]);
				$j('#existingSelectorDate').val(split[1]);
				$j('#existingSelectorSubmit').removeAttr('disabled');
			}
			else {
				$j('#existingSelectorType').val('');
				$j('#existingSelectorDate').val('');
				$j('#existingSelectorSubmit').attr('disabled', 'disabled');
			}
		});
	});
	function toggleSection(show) {
		var hide = (show == 'Drug' ? 'Date' : 'Drug');
		$j('#ordersBy'+hide+'Link').hide();
		$j('#ordersBy'+hide+'Section').hide();
		$j('#ordersBy'+show+'Link').show();
		$j('#ordersBy'+show+'Section').show();
		$j.cookie("mdrtbManageDrugOrderSelectedTab", show);
	}
</script>

<table width="100%">
	<tr>
		<td class="cellStyle"><spring:message code="mdrtb.modifyExistingTreatmentChange" text="Modify an existing treatment change"/>:</td>
		<td class="cellStyle" width="100%">
			<form action="editRegimen.form" method="get">
				<input type="hidden" name="patientId" value="${patient.patientId}"/>
				<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
				<input id="existingSelectorType" type="hidden" name="type"/>
				<input id="existingSelectorDate" type="hidden" name="changeDate"/>
				<select id="existingTreatmentSelector">
					<option value=""></option>
					<c:forEach items="${regimenHistoryGroups}"  var="entry">
						<optGroup label="<spring:message code="mdrtb.treatment.${entry.key}"/>"></optGroup>
						<c:forEach items="${entry.value.regimenChanges}" var="changeEntry">
							<option value="${entry.key}:${mdrtb:formatDateDefault(changeEntry.key)}">
								<openmrs:formatDate format="${_dateFormatDisplay}" date="${changeEntry.key}"/>
								&nbsp;&nbsp;
								<spring:message code="mdrtb.started" text="Started"/>: 
								<mdrtb:format obj="${changeEntry.value.drugsStartedAndNotEnded}" separator=", " defaultVal="mdrtb.none"/>; 
								<spring:message code="mdrtb.ended" text="Ended"/>: 
								<mdrtb:format obj="${changeEntry.value.drugsEndedAndNotStarted}" separator=", " defaultVal="mdrtb.none"/>
							</option>
						</c:forEach>
					</c:forEach>
				</select>
				<input id="existingSelectorSubmit" type="submit" value="<spring:message code="mdrtb.submit" text="Submit"/>" disabled/>
			</form>
		</td>
	</tr>
	<tr>
		<td class="cellStyle"><spring:message code="mdrtb.addNewTreatmentChange" text="Add a new treatment change"/>:</td>
		<td class="cellStyle" width="100%">
			<form action="editRegimen.form" method="get">
				<input type="hidden" name="patientId" value="${patient.patientId}"/>
				<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
				<spring:message code="mdrtb.type" text="Type"/>
				<select name="type">
					<c:forEach items="${regimenHistoryGroups}" var="entry">
						<option value="${entry.key}"><spring:message code="mdrtb.treatment.${entry.key}"/></option>
					</c:forEach>
				</select>
				&nbsp;<spring:message code="mdrtb.onDate" text="on date"/>&nbsp;
				<input type="text" name="changeDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${changeDate}" />" onFocus="showCalendar(this)" />
				<input type="submit" value="<spring:message code="mdrtb.submit" text="Submit"/>"/>
			</form>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<a id="ordersByDrugLink" class="headerStyle" onclick="toggleSection('Date');"><spring:message code="mdrtb.switchToRegimenView" text="Switch to Regimen View"/></a>
			<a id="ordersByDateLink" class="headerStyle" onclick="toggleSection('Drug');"><spring:message code="mdrtb.switchToDrugView" text="Switch to Drug View"/></a>
		</td>
	</tr>
</table>

<table width="100%">
	<tr id="ordersByDrugSection">
		<td valign="top">
			<br/>
			<b class="boxHeader"><spring:message code="mdrtb.activeOrders" text="Active Orders"/></b>
			<div class="box">
				<mdrtb:regimenPortlet id="activeOrders" patientId="${patient.patientId}" url="activeOrderPortlet" parameters="alerts=true"/>
			</div>
			<br/>
			<b class="boxHeader"><spring:message code="mdrtb.completedOrders" text="Completed Orders"/></b>
			<div class="box">
				<mdrtb:regimenPortlet id="completedOrders" patientId="${patient.patientId}" url="completedOrderPortlet"/>
			</div>
		</td>
	</tr>
	<tr id="ordersByDateSection">
		<td valign="top">
			<br/>
			<c:forEach items="${regimenHistoryGroups}" var="entry">
				<b class="boxHeader">
					<spring:message code="mdrtb.treatment.${entry.key}"/>
				</b>
				<div class="box">
					<c:set var="editUrl" value="${pageContext.request.contextPath}/module/mdrtb/regimen/editRegimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}&type=${entry.key}&changeDate="/>
					<mdrtb:regimenHistory history="${entry.value}" dateFormat="${_dateFormatDisplay}" cssClass="regimenHistory" futureCssClass="future" invert="true" timeDescending="true" editLink="${editUrl}"/>
				</div>
				<br/>
			</c:forEach>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>
