<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<openmrs:globalProperty var="doseUnits" key="mdrtb.drugDoseUnits" defaultValue="g,mg,ml,tab(s)"/>
<spring:message code="mdrtb.delete" text="Delete" var="deleteText"/>
<spring:message code="mdrtb.perday" var="perDayText"/>
<spring:message code="mdrtb.perweek" var="perWeekText"/>

<script type="text/javascript">
	var ${id}StandardRegimens = new Array();
	<c:forEach items="${history.type.suggestions}" var="rs">
		${id}StandardRegimens['${rs.codeName}'] = {
			'displayName':'${rs.displayName}',
			'codeName':'${rs.codeName}',
			'reasonForStarting':'${rs.reasonForStarting}',
			'drugComponents': [
				<c:forEach items="${rs.drugComponents}" var="dc" varStatus="dcStatus">
					{
						'orderId':'', 
						'generic':'${dc.generic}',
						//COMMENTED FOR TAJIKISTAN
						//'drugId':'${dc.drugId}',
						'dose':'${dc.dose}',
						'units':'${dc.units}',
						'frequency':'${dc.frequency}',
						'startDate':'<openmrs:formatDate date="${changeDate}" format="${_dateFormatDisplay}"/>',
						'autoExpireDate':'',
						'instructions':'${dc.instructions}'
					}<c:if test="${!dcStatus.last}">,</c:if>
				</c:forEach>
			]
		};
	</c:forEach>

	var ${id}GenericOptions = '<option value=""></option><c:forEach items="${openmrs:sort(mdrtb:genericsInSet(history.type.drugSet), \'name.name\', false)}" var="c"><option value="${c.conceptId}">${c.name.name}</option></c:forEach>';
	//var ${id}DrugOptions = '<option class="unspecified" value=""><spring:message code="mdrtb.unspecified" text="Unspecified"/></option><c:forEach items="${mdrtb:drugsInSet(history.type.drugSet)}" var="d"><option class="drugConcept drugConcept${d.concept.conceptId}" value="${d.drugId}">${d.name}</option></c:forEach>';
	var ${id}UnitOptions = '<option value=""></option><c:forEach items="${doseUnits}" var="u"><option value="${u}">${u}</option></c:forEach>';
	var ${id}Index = 0;

	$j(document).ready(function() {
		$j("#${id}AddStandardRegimen").click(function() {
			var codeName = $j('#${id}standardRegimenSelector').val();
			var regToAdd = ${id}StandardRegimens[codeName];
			for (var i=0; i<regToAdd.drugComponents.length; i++) {
				addDrug('${id}NewOrderMarker', regToAdd.drugComponents[i], ${id}GenericOptions, /*${id}DrugOptions,*/ ${id}UnitOptions, '${deleteText}', '${perDayText}', '${perWeekText}', ${id}Index++);
			}
			$j('#${id}standardRegimenSelector').val('');
		});

		//$j('#${id}individualDrugSelector').append(${id}DrugOptions);
		$j('#${id}individualGenericSelector').append(${id}GenericOptions).change(function() {
			limitDrug(this, '${id}individualDrugSelector');
		});

		$j('#${id}AddIndividualDrug').click(function() {
			addDrug('${id}NewOrderMarker', {
						'orderId':'', 
						'generic':$j('#${id}individualGenericSelector').val(), 
						//'drugId':$j('#${id}individualDrugSelector').val(), 
						'dose':'', 
						'units':'', 
						'frequency':'', 
						'instructions':'', 
						'startDate':'<openmrs:formatDate date="${changeDate}" format="${_dateFormatDisplay}"/>', 
						'autoExpireDate':''
					}, ${id}GenericOptions, /*${id}DrugOptions,*/ ${id}UnitOptions, '${deleteText}', '${perDayText}', '${perWeekText}', ${id}Index++
			);
			$j('#${id}individualGenericSelector').val('');
			//$j('#${id}individualDrugSelector').hide('');
		});

		<c:forEach items="${change.ordersStarted}" var="d" varStatus="orderStatus">
			addDrug('${id}NewOrderMarker', {
						'orderId':'${d.orderId}', 
						'generic':'${d.concept.conceptId}', 
						/*'drugId':'${d.drug.drugId}', */
						'dose':'${d.dose}', 
						'units':'${d.units}', 
						'frequency':'${d.frequency}', 
						'instructions':'${d.instructions}', 
						'startDate':'<openmrs:formatDate date="${d.startDate}" format="${_dateFormatDisplay}"/>', 
						'autoExpireDate':'<openmrs:formatDate date="${d.autoExpireDate}" format="dd/MM/yyyy"/>'
					}, ${id}GenericOptions, /*${id}DrugOptions,*/ ${id}UnitOptions, '${deleteText}', '${perDayText}', '${perWeekText}', ${id}Index++
			);
		</c:forEach>
	});

	function validateAndSubmit() {

		$j('#errorDisplay').html('');
		var changeDate = $j('#changeDateSelector').val();
		if (changeDate == '') {
			$j('#errorDisplay').append('<li><spring:message code="mdrtb.drugOrder.changeDateRequired" text="Please specify a Change Date"/></li>');
		}

		$j('.reasonField').not('.hidden').each(function() {
			if ($j(this).val() == '') {
	    		$j('#errorDisplay').append('<li><spring:message code="mdrtb.drugOrder.discontinuedReasonRequiredForAllStoppedOrders" text="Please specify a discontinued reason for all stopped orders"/></li>');
			}
		});

		$j('.genericDrugInput').each(function() {
			if ($j(this).val() == '') {
	    		$j('#errorDisplay').append('<li><spring:message code="mdrtb.drugOrder.drugRequiredForAllNewOrders" text="Please specify a drug for all new orders"/></li>');
			}
		});

		$j('.doseInput').each(function() {
			if ($j(this).val() != '') {
				//TODO: Validate that this is a number
			}
		});

		// TODO: Validate that startDate, autoExpireDate, and discontinueDates are in proper sequence.

		if ($j('#errorDisplay').html() == '') {
			$j('#regimenForm').submit();
		}
	}
</script>

<ul id="errorDisplay" class="error"></ul>
<br/>
<form id="regimenForm" action="saveRegimen.form">
	<input type="hidden" name="patientId" value="${patientId}"/>
	<input type="hidden" name="patientProgramId" value="${patientProgramId}"/>
	<input type="hidden" name="type" value="${type}"/>
	<input type="hidden" name="startingChangeDate" value="${mdrtb:formatDateDefault(changeDate)}"/>
	<b>
		<c:choose>
			<c:when test="${empty change}"><spring:message code="mdrtb.new" text="New"/></c:when>
			<c:otherwise><spring:message code="mdrtb.edit" text="Edit"/></c:otherwise>
		</c:choose>
		<spring:message code="mdrtb.treatment.${history.type.name}"/> 
		<spring:message code="mdrtb.changesOn" text="changes on"/> 
		<input id="changeDateSelector" type="text" name="changeDate" size="10" tabIndex="-1" value="<openmrs:formatDate date="${changeDate}" />" onFocus="showCalendar(this)" /><span class="datePatternHint"> (<openmrs:datePattern />)</span>
	</b>
	<br/>	
	<br/>
	<b class="boxHeader"><spring:message code="mdrtb.changesToExistingOrders" text="Changes to Existing Orders"/></b>
	<div class="box">
		<c:choose>
			<c:when test="${empty regimenAtStart.drugOrders}">
				<spring:message code="mdrtb.noExistingOrdersOnThisDate" text="No existing orders on this date"/>
			</c:when>
			<c:otherwise>
				<table>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
						<!-- COMMENTED FOR TAJIKISTAN -->
						<!--  <th class="headerStyle"><spring:message code="mdrtb.formulation" text="Formulation"/></th> -->
						<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.action" text="Action"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.reasonForStopping" text="Reason for stopping"/></th>
					</tr>
					<c:forEach items="${regimenAtStart.drugOrders}" var="drugOrder" varStatus="orderStatus">
						<c:set var="changeOrderIndex" value="${id}_${orderStatus.index}"/>
						<c:set var="isStopped" value="${mdrtb:collectionContains(change.ordersEnded, drugOrder)}"/>
						<tr>
							<td style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.concept.name.name}</td>
							<!-- COMMENTED FOR TAJIKISTAN -->
							<!-- <td style="text-align:left; padding-left:10px; padding-right:10px; white-space:nowrap;">${drugOrder.drug.name}</td> -->						
							<td class="cellStyle">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle">
								${drugOrder.frequency}
							</td>
							<td class="cellStyle">
								<input type="radio" tabIndex="-1" onchange="addClass('stopReason${changeOrderIndex}','hidden');removeClass('continueReason${changeOrderIndex}','hidden');" name="action:${drugOrder.orderId}" value="continue" <c:if test="${!isStopped}">checked</c:if>> 
								<spring:message code="mdrtb.continue" text="Continue"/>
								&nbsp;&nbsp;&nbsp;
								<input type="radio" tabIndex="-1" onchange="removeClass('stopReason${changeOrderIndex}','hidden');addClass('continueReason${changeOrderIndex}','hidden');" name="action:${drugOrder.orderId}" value="stop" <c:if test="${isStopped}">checked</c:if>> 
								<spring:message code="mdrtb.stop" text="Stop"/>
							</td>
							<td class="cellStyle">
								<c:set var="needExtraRow" value="${!empty drugOrder.discontinuedReason}"/>
								<select name="reason.${drugOrder.orderId}" id="stopReason${changeOrderIndex}" class="reasonField<c:if test="${!isStopped}"> hidden</c:if>">
									<option value=""></option>
									<c:forEach items="${mdrtb:answersToQuestion(history.type.reasonForStoppingQuestion)}" var="a">
										<option value="${a.conceptId}" <c:if test="${drugOrder.discontinuedReason.conceptId == a.conceptId}"><c:set var="needExtraRow" value="false"/>selected</c:if>><mdrtb:format obj="${a}" tagName="preferred"/></option>
									</c:forEach>
									<c:if test="${needExtraRow}">
										<option value="${drugOrder.discontinuedReason}" selected><mdrtb:format obj="${drugOrder.discontinuedReason}"/></option>
									</c:if>
								</select>
								<span id="continueReason${changeOrderIndex}" <c:if test="${isStopped}">class="hidden"</c:if>>
									<spring:message code="mdrtb.continued" text="Continued"/>
								</span>
							</td>
						</tr>
					</c:forEach>
				</table>	
			</c:otherwise>
		</c:choose>
	</div>
	<br/>
	<c:if test="${!empty history.type.reasonForStartingQuestion}">
		<b class="boxHeader"><spring:message code="mdrtb.changesToTreatmentType" text="Changes to Treatment Type"/></b>
		<div class="box">
			<table width="100%">
				<tr>
					<th class="headerStyle"><spring:message code="mdrtb.startingTreatmentType" text="Starting Treatment Type"/>:</th>
					<td class="cellStyle" valign="top" width="100%">
						<c:choose>
							<c:when test="${empty regimenAtStart.drugOrders}">
								<spring:message code="mdrtb.noOrdersAtStart" text="Not on treatment"/>
							</c:when>
							<c:otherwise>
								<mdrtb:format obj="${regimenAtStart.reasonForStarting.valueCoded}" defaultVal="mdrtb.none"/>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<th class="headerStyle"><spring:message code="mdrtb.changeToTreatmentType" text="Change to Treatment Type"/>:</th>
					<td class="cellStyle" valign="top" width="100%">
						<spring:message code="${empty regimenAtStart.drugOrders ? 'mdrtb.unspecified' : 'mdrtb.noChange'}" var="noChangeMsg"/>
						<openmrs_tag:conceptAnswerField formFieldName="reasonForStarting" concept="${mdrtb:getConcept(history.type.reasonForStartingQuestion)}" initialValue="${regimenAtEnd.reasonForStarting.valueCoded}" optionHeader="${noChangeMsg}"/>
					</td>
				</tr>
			</table>
		</div>
	</c:if>
	<br/>
	<b class="boxHeader"><spring:message code="mdrtb.newOrdersToStart" text="New Orders to Start"/></b>
	<div class="box">

		<table>
			<tr>
				<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
				<!-- COMMENTED FOR TAJIKISTAN -->
				<!-- <th class="headerStyle"><spring:message code="mdrtb.formulation" text="Formulation"/></th> -->
				<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/>/<spring:message code="mdrtb.unit" text="Unit"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.startdate" text="Start Date"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.autoExpireDate" text="Planned Stop Date"/></th>
				<th class="headerStyle"><spring:message code="mdrtb.instructions" text="Instructions"/></th>
			</tr>
			<tr id="${id}NewOrderMarker"><td colspan="6">&nbsp;</td></tr>
		</table>
	</div>
	
	<br/>
	<c:if test="${!empty history.type.suggestions}">
		<spring:message code="mdrtb.startStandardRegimen" text="Start a standard regimen"/>:
		<select id="${id}standardRegimenSelector" name="${id}standardRegimenSelector">
			<option value=""></option>
			<c:forEach items="${history.type.suggestions}" var="suggestion">
				<option value="${suggestion.codeName}"><spring:message code="${suggestion.displayName}" text="${suggestion.displayName}"/></option>
			</c:forEach>
		</select>
		<input type="button" id="${id}AddStandardRegimen" value="<spring:message code="mdrtb.add" text="Add"/>"/>
		<br/><b> - <spring:message code="mdrtb.or" text="or"/> - </b><br/>
	</c:if>
	<spring:message code="mdrtb.startIndividualDrugs" text="Start individual drugs"/>:
	<select id="${id}individualGenericSelector" name="${id}individualGenericSelector"></select>
	<select id="${id}individualDrugSelector" name="${id}individualDrugSelector" style="display:none;"></select>
	<input type="button" id="${id}AddIndividualDrug" value="<spring:message code="mdrtb.add" text="Add"/>"/>

	<br/><br/>
	<input type="button" value="<spring:message code="mdrtb.submit" text="Submit"/>" onclick="javacript:validateAndSubmit();"/>
	<input type="button" value="<spring:message code="mdrtb.cancel" text="Cancel"/>" onclick="document.location.href='manageDrugOrders.form?patientId=${patientId}&patientProgramId=${patientProgramId}'"/>
</form>