<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:require privilege="Edit Orders" otherwise="/login.htm" redirect="/module/mdrtb/regimen/manageDrugOrders.form"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/mdrtb/drugOrders.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/drugOrders.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patient.patientId}"/>

<openmrs:globalProperty var="doseUnits" key="mdrtb.drugDoseUnits" defaultValue="g,mg,ml,tab(s)"/>
<spring:message code="mdrtb.delete" text="Delete" var="deleteText"/>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#genericSelector').change(function() {
			limitDrug(this, 'drugSelector');
			$j('#drugSelector').val('');
		});
		limitDrug($j('#genericSelector'), 'drugSelector');
	});

	function validateAndSubmit() {
		
		var generic = $j('#genericSelector').val();
		var startDate = $j('#startDateField').val();
		var dose = $j('#doseSelector').val();
		var autoExpireDate = $j('#autoExpireField').val();
		var discontinuedDate = $j('#discontinuedDateField').val();
		var discontinuedReason = $j('#discontinuedReasonField').val();

		$j('#errorDisplay').html('');
		
		if (generic == '') {
			$j('#errorDisplay').append("<li><spring:message code='mdrtb.drugOrder.genericRequired' text='Please specify a Drug'/></li>");
		}
		if (startDate == '') {
			$j('#errorDisplay').append("<li><spring:message code='mdrtb.drugOrder.startDateRequired' text='Please specify a Start Date'/></li>");
		}
		if (dose != '') {
			// TODO: Check if this is a valid number
		}
		if (autoExpireDate != '') {
			// TODO: Check if this is after start date
		}
	    if (discontinuedDate != '') {
	    	if (discontinuedReason == '') {
	    		$j('#errorDisplay').append("<li><spring:message code='mdrtb.drugOrder.discontinuedReasonRequired' text='Please specify a discontinued reason'/></li>");
	    	}
	    	// TODO: Check if this is after startDate and before autoExpireDate (if autoExpire not null)
	    }
	    if (discontinuedReason != '') {
	    	if (discontinuedDate == '') {
	    		$j('#errorDisplay').append("<li><spring:message code='mdrtb.drugOrder.discontinuedDateRequired' text='Please specify a discontinued date'/></li>");
	    	}
	    }

		if ($j('#errorDisplay').html() == '') {
			$j('#drugOrderForm').submit();
		}
	}
	
</script>

<br/>
<form id="drugOrderForm" action="saveDrugOrder.form">
	<input type="hidden" name="patientId" value="${patientId}"/>
	<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="orderId" value="${orderId}"/>
	<b class="boxHeader">
		<c:choose>
			<c:when test="${empty orderId}"><spring:message code="mdrtb.new" text="New"/></c:when>
			<c:otherwise><spring:message code="mdrtb.edit" text="Edit"/></c:otherwise>
		</c:choose>
		<%--<spring:message code="mdrtb.treatment.${type}"/>--%> 
		<spring:message code="mdrtb.drugOrder" text="Drug Order"/> 
	</b>
	<div class="box">
		<ul id="errorDisplay" class="error"></ul>
		<table>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
				<td class="cellStyle">
					<select id="genericSelector" name="generic">
						<option value=""></option>
						<c:forEach items="${openmrs:sort(mdrtb:genericsInSet(regimenType.drugSet), 'name.name', false)}" var="c">
							<option value="${c.conceptId}" <c:if test="${c == drugOrder.concept}">selected</c:if>>${c.name.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<%--<tr>
				<th class="headerStyle"><spring:message code="mdrtb.formulation" text="Formulation"/></th>
				<td class="cellStyle">
					<select id="drugSelector" name="drug">
						<option value=""><spring:message code="mdrtb.unspecified" text="Unspecified"/></option>
						<c:forEach items="${mdrtb:drugsInSet(regimenType.drugSet)}" var="d">
							<option class="drugConcept drugConcept${d.concept.conceptId}" value="${d.drugId}" <c:if test="${d == drugOrder.drug}">selected</c:if>>${d.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr> --%>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
				<td class="cellStyle">
					<input id="doseSelector" type="text" size="10" name="dose" value="${drugOrder.dose}"/>
					<select name="units">
						<option value=""></option>
						<c:forEach items="${doseUnits}" var="u">
							<option value="${u}" <c:if test="${drugOrder.units == u}">selected</c:if>>${u}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
				<td class="cellStyle">
					<c:choose>
						<c:when test="${!empty drugOrder.frequency}">
							<input type="text" size="30" name="frequency" value="${drugOrder.frequency}"/>
						</c:when>
						<c:otherwise>
							<input type="text" size="5" name="perDay"/><spring:message code="mdrtb.perday"/>
							<input type="text" size="5" name="perWeek"/><spring:message code="mdrtb.perweek"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.startdate" text="Start Date"/></th>
				<td class="cellStyle">
					<input id="startDateField" type="text" name="startDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${drugOrder.startDate}" />" onFocus="showCalendar(this)" />
				</td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.autoExpireDate" text="Planned Stop Date"/></th>
				<td class="cellStyle">
					<input id="autoExpireField" type="text" name="autoExpireDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${drugOrder.autoExpireDate}" />" onFocus="showCalendar(this)" />
				</td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.instructions" text="Instructions"/></th>
				<td class="cellStyle"><textarea name="instructions" rows="2" cols="30">${drugOrder.instructions}</textarea></td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.discontinueddate" text="Discontinued Date"/></th>
				<td class="cellStyle">
					<input id="discontinuedDateField" type="text" name="discontinuedDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${drugOrder.discontinuedDate}" />" onFocus="showCalendar(this)" />
				</td>
			</tr>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.reasonDiscontinued" text="Reason discontinued"/></th>
				<td class="cellStyle">
					<c:set var="needExtraRow" value="${!empty drugOrder.discontinuedReason}"/>
					<select id="discontinuedReasonField" name="discontinuedReason">
						<option value=""></option>
						<c:forEach items="${mdrtb:answersToQuestion(regimenType.reasonForStoppingQuestion)}" var="a">
							<option value="${a.conceptId}" <c:if test="${drugOrder.discontinuedReason.conceptId == a.conceptId}"><c:set var="needExtraRow" value="false"/>selected</c:if>><mdrtb:format obj="${a}" tagName="preferred"/></option>
						</c:forEach>
						<c:if test="${needExtraRow}">
							<option value="${drugOrder.discontinuedReason.conceptId}" selected><mdrtb:format obj="${drugOrder.discontinuedReason}"/></option>
						</c:if>
					</select>
				</td>
			</tr>
		</table>
		<input type="button" onclick="validateAndSubmit();" value="<spring:message code="mdrtb.submit" text="Submit"/>"/>
		<input type="button" value="<spring:message code="mdrtb.cancel" text="Cancel"/>" onclick="document.location.href='manageDrugOrders.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"/>
	</div>
</form>
