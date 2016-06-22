<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${!empty patientId ? patientId : program.patient.id}" parameters="patientProgramId=${patientProgramId}"/>


<!-- TODO: clean up above paths so they use dynamic reference -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->
<script type="text/javascript">

	var $j = jQuery.noConflict();

	function validateAndSubmit() {

		var numberOfTests = ${numberOfTests};
		
		var location = $j('#location').val();
		var provider = $j('#provider').val();
		var lab = $j('#lab').val();
		
		$j('#errorDisplay').html('');
		
		if (location == '') {
			$j('#errorDisplay').append("<li><spring:message code='mdrtb.specimen.errors.noLocation' text='Please specify a location.'/></li>");
		}
		if (provider == '') {
			$j('#errorDisplay').append("<li><spring:message code='mdrtb.specimen.errors.noCollector' text='Please specify who collected this sample.'/></li>");
		}
		if (lab == '') {
			$j('#errorDisplay').append("<li><spring:message code='mdrtb.specimen.errors.noLab' text='Please specify a laboratory.'/></li>");
		}

		// check all the individual tests
		for(i = 1; i <= numberOfTests; i++) {
			var dateCollected = $j('#dateCollected' + i).val();
			//var dateTested = $j('#dateTested' + i).val();
			var identifier = $j('#identifier' + i).val();
			var type = $j('#type' + i).val();
			var appearance = $j('#appearance' + i).val();
			var result = $j('#result' + i).val();
			
			// date collected is only required parameter (unless an entire row is blank)
			if ((identifier != '' | type != '' | appearance !='' | result !='') &&
				dateCollected == '') {
					$j('#errorDisplay').append("<li><spring:message code='mdrtb.specimen.errors.noDateCollected' text='Please specify the date collected.'/></li>");
			}
			
			// date collected is only required parameter (unless an entire row is blank)
			//if ((identifier != '' | type != '' | appearance !='' | result !='') &&
			//	dateTested == '') {
			//		$j('#errorDisplay').append("<li><spring:message code='mdrtb.specimen.errors.noDateTested' text='Please specify the date tested.'/></li>");
			//}

			// TODO: add validation of collection date;  we could use the openmrs:datePattern tag, and the
			// parseSimpleDate library function in openmrs.js, but this will fail with localized patterns, like the French "(jj/mm/aaaa"	
		}
		
		if ($j('#errorDisplay').html() == '') {
			$j('#quickEntry').submit();
		}
	}	


</script>

<b class="boxHeader"><spring:message code="mdrtb.add${testType}Results"/></b>
<!-- display the proper header depending on whether we were adding a specimen, or a smear/culture -->

<div class="box">
<form id="quickEntry" name="quickEntry" action="quickEntry.form?patientId=${patientId}&patientProgramId=${patientProgramId}&numberOfTests=${numberOfTests}&testType=${testType}" method="post">

<!-- display error messages -->
<ul id="errorDisplay" class="error"></ul>

<table>
<tr>
<th><!--<spring:message code="mdrtb.locationCollected" text="Location Collected"/>--></th>
<td>
<input type="hidden" id="location" name="location" value="86">
<!-- <option value="86" selected>ПМСП</option>

</select>	 -->	
</td>
</tr>

<tr>
<th><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</th>
<td>
<select id="provider" name="provider">
<option value=""/>
<c:forEach var="provider" items="${providers}">
	<option value="${provider.id}">${provider.personName}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<th><spring:message code="mdrtb.lab" text="Lab"/>:</th>
<td><select id="lab" name="lab">
<option value=""></option>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}">${location.displayString}</option>
</c:forEach>
</select>
</td>
</tr>

</table>

<table>
<tr>
<th><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</th>
<th><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</th>
<th><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</th>
<th><spring:message code="mdrtb.appearance" text="Appearance"/>:</th>


<c:if test="${testType eq 'xpert' || testType eq 'hain' || testType eq 'smear' || testType eq 'culture'}" >
<th><spring:message code="mdrtb.dateCompleted" text="Date Completed"/>:</th>


<th><spring:message code="mdrtb.result" text="Result"/>:</th>
</c:if>

<c:if test="${testType eq 'xpert' || testType eq 'hain'}" >
<th><spring:message code="mdrtb.rifResistance" text="RIF Resistance"/>:</th>

<c:if test="${testType eq 'hain'}" >
<th><spring:message code="mdrtb.inhResistance" text="INH Resistance"/>:</th>
</c:if>
<!-- 
<c:if test="${testType eq 'xpert'}" >
<th><spring:message code="mdrtb.mtbBurden" text="MTB Burden"/>:</th>
</c:if>

<th><spring:message code="mdrtb.errorCode" text="Error Code"/>:</th>
-->
</c:if>
</tr>


<c:forEach var="i" begin="1" end="${numberOfTests}" step="1">
<tr>
<td><openmrs_tag:dateField formFieldName="dateCollected${i}" startValue=""/></td>
<td><input id="identifier${i}" name="identifier${i}" type="text" size="10"/></td>

<td>
<select id="type${i}" name="type${i}">
<option value=""></option>
<c:forEach var="type" items="${types}">
	<option value="${type.answerConcept.id}" <c:if test="${type.answerConcept.id == defaultSampleType.id}">selected</c:if>>${type.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>

<td>
<select id="appearance${i}" name="appearance${i}">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
	<option value="${appearance.answerConcept.id}">${appearance.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>

<c:if test="${testType eq 'xpert' || testType eq 'hain' || testType eq 'smear' || testType eq 'culture'}" >
<td><openmrs_tag:dateField formFieldName="dateCompleted${i}" startValue=""/></td>
</c:if>

<c:if test="${testType eq 'smear' || testType eq 'culture'}">
<td>
<select id="result${i}" name="result${i}" class="result">
<option value=""></option>
<c:forEach var="result" items="${testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}">${result.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</c:if>



<c:if test="${testType eq 'xpert' || testType eq 'hain'}">
<td>
<select id="result${i}" name="result${i}" class="result">
<option value=""></option>
<c:forEach var="mtbResult" items="${mtbResults}">
<option value="${mtbResult.answerConcept.id}">${mtbResult.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>

<td>
<select id="rifResult${i}" name="rifResult${i}">
<option value=""></option>
<c:forEach var="rifResult" items="${rifResults}">
<option value="${rifResult.answerConcept.id}">${rifResult.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>

<c:if test="${testType eq 'hain'}">
<td>
<select id="inhResult${i}" name="inhResult${i}">
<option value=""></option>
<c:forEach var="inhResult" items="${inhResults}">
<option value="${inhResult.answerConcept.id}">${inhResult.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</c:if>

<!-- 
<c:if test="${testType eq 'xpert'}">
<td>
<select id="mtbBurden${i}" name="mtbBurden${i}">
<option value=""></option>
<c:forEach var="mtbBurden" items="${xpertMtbBurdens}">
<option value="${mtbBurden.answerConcept.id}">${mtbBurden.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</c:if>

<td>
<input id="errorCode${i}" name="errorCode${i}" type="text" size="10"/>
</td>
 -->
</c:if>

</tr>
</c:forEach>
</table>

<br/>

<button type="button" onclick="validateAndSubmit();" ><spring:message code="mdrtb.save" text="Save"/></button><a style="text-decoration:none" href="${pageContext.request.contextPath}/module/mdrtb/specimen/specimen.form?patientId=${patientId}&patientProgramId=${patientProgramId}"><button type="button"><spring:message code="mdrtb.cancel" text="Cancel"/></button></a>

</form>
</div>

<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbFooter.jsp"%>

