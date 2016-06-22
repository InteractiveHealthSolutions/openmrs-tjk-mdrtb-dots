<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/mdrtb/mdrtbHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${! empty patientId ? patientId : specimen.patient.patientId}"/>
<openmrs:portlet url="mdrtbSubheader" id="mdrtbSubheader" moduleId="mdrtb" patientId="${! empty patientId ? patientId : specimen.patient.patientId}" parameters="patientProgramId=${patientProgramId}"/>

<!-- TODO: clean up above paths so they use dynamic reference? -->
<!-- TODO: add privileges? -->

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">
	td {padding-left:4px; padding-right:4px; padding-top:2px; padding-bottom:2px; vertical-align:top}
</style>

<!-- JQUERY FOR THIS PAGE -->
<script type="text/javascript"><!--

	var $j = jQuery.noConflict();	

	// tests to see if a set contains a an object
	// TODO: is there an existing javascript function to do this?
	// TODO: if nothing else, should move this into some library file
	function contains(set, item) {
		for (var i in set) {
			if (set[i] == item) {
				return true;
			}
		}
		return false;
	}

	// hides all add and edit details boxes
	function hideDisplayBoxes(){
		$j('.addBox').hide();
		$j('.editBox').hide();
		$j('.detailBox').hide();
		$j('#details_-1').hide();
	}

	// hides all edit, add, and delete link elements (used to stop used from navigating away from an edit)
	function hideLinks() {
		$j('.edit').fadeOut();  // hide all the edit tests links
		$j('.delete').fadeOut(); // hide all the delete links 
		$j('#editSpecimen').fadeOut(); // hide the edit specimen link
		$j('#add').fadeOut(); // hide the "add a test" selector
	}

	// shows all edit, add, and delete elements (called when an edit is complete)
	function showLinks() {
		$j('.edit').fadeIn();  // show all the edit tests links
		$j('.delete').fadeIn(); // show all the delete links 
		$j('#editSpecimen').fadeIn(); // show the edit specimen link
		$j('#add').fadeIn(); // show the "add a test" selector
	}

	// resets the default dst results boxes in the "add dst" section
	function resetAddDstResults() {		
		$j('.addDstResult').hide().find('input,.dstResult').attr('value',''); // reset all the dst result values except drug type
		$j('.dstColonies').hide()	  // hide the colony input boxes
		
		// now reshow the boxes that contain the default drugs
		for (var i = 1; i <= ${fn:length(defaultDstDrugs)}; i++) {
			$j('#addDstResult_' + i).show();	
		}
		addDstResultCounter = ${fn:length(defaultDstDrugs)} + 1; // reset the add dst result counter
	}

	function showAddDstResultsWithData() {
		for (var i = 1; i < 31; i++) {
			if ($j('#addDstResult${testId}_' + i).find('.dstResult').val() != ''
				|| $j('#addDstResult${testId}_' + i).find('select').val() != '') {

				$j('#addDstResult${testId}_' + i).show();
				addDstResultCounter = i + 1;
			}
		}
	}
	
	$j(document).ready(function(){

		// set the add dst count to 1
		addDstResultCounter = 1;
		
		if (${fn:length(specimenErrors.allErrors) > 0}) {
			// switch to edit specimen if we are here because of specimen validation error
			hideLinks();
			$j('#details_specimen').hide();  // hide the specimen details box
			$j('#edit_specimen').show();  // show the edit speciment box		
		}
		else if (${fn:length(testErrors.allErrors) > 0}) {
			// switch to edit test if we are here because of a test validation error
			if(${! empty testId}) {
				// handle the "edit" case
				hideLinks();
				$j('#details_${testId}').hide();  // hide the selected details box
				$j('#edit_${testId}').show(); // show the selected edit box
				showAddDstResultsWithData(); // show any dst result rows that may have transitory data
				$j(document).scrollTop($j('#edit_${testId}').offset().top - 50); // jump to the edit box that has the error
			} 
			else {
				// handle the "add" case
				hideDisplayBoxes();
				hideLinks();
				$j('#add_${testType}').show(); // show the proper add a test box
				showAddDstResultsWithData(); // show any dst result rows thay may have transitory data
				$j(document).scrollTop($j('#add_${testType}').offset().top - 50); // jump to the edit box that has the error
			}
		}
		else {
			// show the proper detail windows if it has been specified
			// TODO: this does not work when a test is saved as the test id of a test gets changes whenever it is saved since
			// all the obs get voided and recreated;
			$j('#details_${testId}').show();
		}
		
		// event handlers to hide and show specimen edit box
		$j('#editSpecimen').click(function(){
			hideLinks();
			$j('#details_specimen').hide();  // hide the specimen details box
			$j('#edit_specimen').show();  // show the edit speciment box
		});

		$j('#cancelSpecimen').click(function(){
			// if this a cancel during a reload due to validation error, we need to reload
			// the entire page to "reset" the specimen model attribute, which is in a transient state
			if (${fn:length(specimenErrors.allErrors) > 0}) {
				window.location="specimen.form?patientId=${specimen.patient.patientId}&patientProgramId=${patientProgramId}";
			}
			else {
				// otherwise, just do a standard "cancel"		
				showLinks();
				$j('#edit_specimen').hide();  // hide the edit specimen box
				$j('#details_specimen').show();  // show the specimen details box
				$j('.scannedLabReport').show(); // show any scanned lab reports that may have been deleted
			}
		});

		// event handlers to display add boxes
		$j('#addButton').click(function(){
			hideDisplayBoxes();
			hideLinks();
			addDstResultCounter = ${fn:length(defaultDstDrugs)} + 1;   // set the dst counter based on how many default drugs we have 
			$j('#add_' + $j('#addSelect').attr('value')).show(); // show the proper add a test box
		});

		// event handler to handle the "quick test entry" add button at the top of the page
		$j('#quickEntryAddButton').click(function(){
			if($j('#quickEntryAddSelect').attr('value').indexOf("Set") != -1) {
				window.location='quickEntry.form?patientId=${! empty patientId ? patientId : specimen.patient.patientId}&patientProgramId=${patientProgramId}&testType=' + $j('#quickEntryAddSelect').attr('value') + "&numberOfTests=3";
			}
			else {
				window.location='quickEntry.form?patientId=${! empty patientId ? patientId : specimen.patient.patientId}&patientProgramId=${patientProgramId}&testType=' + $j('#quickEntryAddSelect').attr('value') + "&numberOfTests=1";
			}
		});
	
		// event handler to display edit detail boxes
		$j('.edit').click(function(){
			hideLinks();
			$j('#details_' + this.id).hide();  // hide the selected details box
			$j('#edit_' + this.id).show();  // show the selected edit box
		});

		// event handler to cancel an edit or add
		$j('.cancel').click(function(){	
			// if this a cancel during a reload due to validation error, we need to reload
			// the entire page to "reset" the specimen model attribute, which is in a transient state
			if (${fn:length(testErrors.allErrors) > 0}) {
				window.location="specimen.form?patientId=${specimen.patient.patientId}&patientProgramId=${patientProgramId}";
			}
			else {			
				hideDisplayBoxes();
				$j('.detailBox').show();  // show all the detail boxes
				showLinks();
				$j('.dstResult').show(); // show any dst results that may have been deleted
				resetAddDstResults();
			}
		});

		// event handler to:
		// 1) hide/show bacilli and colonies input boxes, and reset the value if needed
		// 2) hide/show the days to positivity input boxes, and reset the value if needed
		$j('.result').change(function() {
				// handle bacilli/colonies
				if ($j(this).attr('value') == ${scanty.id}) {
					// show the bacilli or colonies row in the same div as this element
					$j(this).closest('div').find('.bacilli').show();
					$j(this).closest('div').find('.colonies').show();
				}
				else {
					// hide the bacilli or colonies row in the same div as this element,
					// then find the bacilli/colonies input element and set it's value to empty
					$j(this).closest('div').find('.bacilli').hide().find('#bacilli').attr('value','');
					$j(this).closest('div').find('.colonies').hide().find('#colonies').attr('value','');
				}

			if (contains(${positiveResultsIds}, $j(this).attr('value'))) {
				// handle days to positivity (tests if the current value is within the set of positiveResults
				//if (${positiveResultsIds}.indexOf($j(this).attr('value')) != -1) {
					// show the days of positivity row in the same div as this element
					$j(this).closest('div').find('.daysToPositivity').show();
				}
				else {
					// hide the days of positivity row int he same div as the element
					// then find the days of positivity input element and set it's value to empty
					$j(this).closest('div').find('.daysToPositivity').hide().find('#daysToPositivity').attr('value','');
				}
		});
		
		// event handler to reset dst colonies if result is reset to an empty value
		$j('.dstResult').change(function() {
			if ($j(this).attr('value') == '' || $j(this).attr('value') == ${waitingForTestResult.id} || $j(this).attr('value') == ${dstTestContaminated.id} ) {
				$j(this).closest('tr').find('.dstColonies').hide().attr('value',null);
			}
			else {
				$j(this).closest('tr').find('.dstColonies').show();
			}
		});

		//event handler to hide/show organism non-coded selector, and reset the value if needed
		$j('.organismType').change(function() {
			if ($j(this).attr('value') == ${otherMycobacteriaNonCoded.id}) {
				// show the organism type non-code row in the same div element
				$j(this).closest('div').find('.organismTypeNonCoded').show();
			}
			else {
				// hide the organism type non-coded row in the same div as this element
				// then find the organism type non-coded inpout element and set it's value to empty
				$j(this).closest('div').find('.organismTypeNonCoded').hide().find('#organismTypeNonCoded').attr('value','');	
			}
		});

		//event handler to handle removing lab reports
		$j('.removeScannedLabReport').click(function() {
			$j(this).closest('.scannedLabReport').hide();    		// hide the lab report
			$j('#removeScannedLabReport' + $j(this).attr('value')).attr('value',$j(this).attr('value'));  	// set it's hidden input to the id of this scanned lab report
		});

		//event handler to handle removing dst results
		$j('.removeDstResult').click(function() {
			$j(this).closest('.dstResult').hide();   	// hide the dst result
			$j('#removeDstResult' + $j(this).attr('value')).attr('value',$j(this).attr('value'));  	// set it's hidden input to the id of this dst report
		});

		// event handle to handle adding dst results
		$j('.addDstResultRow').click(function() {
			if(addDstResultCounter < 30) {
				$j('#addDstResult' + $j(this).attr('value') + "_" + addDstResultCounter).show();
				addDstResultCounter++;
			}
		});

		//event handler to handle removing of dst rows that have been added, but not saved
		$j('.removeDstResultRow').click(function() {
			// hide the dst result row and reset the value of all the interior elements
			$j(this).closest('.addDstResult').hide().find('input,select').attr('value','');
		});
		
 	});
-->
</script>
<!-- END JQUERY -->

<!-- PAGE START -->
<div>

<!-- <button onclick="window.location='add.form?patientId=${! empty patientId ? patientId : specimen.patient.patientId}&patientProgramId=${patientProgramId}'"><spring:message code="mdrtb.addANewSpecimen" text="Add a new Specimen"/></button> -->
<span style="font-weight:bold"><spring:message code="mdrtb.addResultForNewSpecimen" text="Add results for new specimen(s)"/>:</span>  
<select id="quickEntryAddSelect">
	<option value="smear"><spring:message code="mdrtb.smear"/></option>
	<!--<option value="smearSet"><spring:message code="mdrtb.smearSet"/></option>-->
	<option value="culture"><spring:message code="mdrtb.culture"/></option>
	<option value="xpert"><spring:message code="mdrtb.xpert"/></option>
	<option value="hain"><spring:message code="mdrtb.hain"/></option>
	<option value="specimenOnly"><spring:message code="mdrtb.specimenOnly"/></option>
</select>
<button id="quickEntryAddButton" type="button"><spring:message code="mdrtb.add" text="Add"/></button>

<br/><br/>


<!-- LEFT-HAND COLUMN START -->
<div id="leftColumn" style="float: left; width:29%;  padding:0px 4px 4px 4px">

<b class="boxHeader"><spring:message code="mdrtb.specimens" text="Specimens"/></b>
<div class="box">

<div id="specimenList">

<c:choose>
<c:when test="${fn:length(specimens) > 0}">
<table cellspacing="0" cellpadding="0" border="0">
<tr>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.dateCollected" text="Date Collected"/></u></nobr></td>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.sampleid" text="Sample ID"/></u></nobr></td>
<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.tests" text="Tests"/></u></nobr></td>
<td width="99%">&nbsp;</td>
</tr>

<c:forEach var="specimenListItem" items="${specimens}" varStatus="i">
	<tr 
		<c:if test="${i.count % 2 == 0 }">class="evenRow"</c:if>
		<c:if test="${i.count % 2 != 0 }">class="oddRow"</c:if>
		<c:if test="${specimenListItem.id == specimen.id}"> style="background-color : gold"</c:if>>
	<td class="tableCell"><nobr><a href="specimen.form?specimenId=${specimenListItem.id}&patientProgramId=${patientProgramId}"><openmrs:formatDate date="${specimenListItem.dateCollected}" format="${_dateFormatDisplay}"/></a></nobr></td>
	<td class="tableCell"><nobr><a href="specimen.form?specimenId=${specimenListItem.id}&patientProgramId=${patientProgramId}">${specimenListItem.identifier}</a></nobr></td>
	<td class="tableCell"><nobr>
						<c:if test="${fn:length(specimenListItem.smears) > 0 }"><spring:message code="mdrtb.smear" text="Smear"/></c:if>
						<c:if test="${fn:length(specimenListItem.cultures) > 0 }"><spring:message code="mdrtb.culture" text="Culture"/></c:if>
						<c:if test="${fn:length(specimenListItem.dsts) > 0 }"><spring:message code="mdrtb.dst" text="DST"/></c:if>
						<c:if test="${fn:length(specimenListItem.xperts) > 0 }"><spring:message code="mdrtb.xpert" text="Xpert"/></c:if>
						<c:if test="${fn:length(specimenListItem.HAINs) > 0 }"><spring:message code="mdrtb.hain" text="HAIN"/></c:if>
						</nobr></td>
	<td width="99%">&nbsp;</td>
	</tr>
</c:forEach>
</table>
</c:when>

<c:otherwise>
	<spring:message code="mdrtb.noSpecimens" text="No specimen information available for this patient program."/>
</c:otherwise>
</c:choose>

</div>
</div>


</div>

<!-- END OF LEFT-HAND COLUMN -->

<!-- START OF RIGHT-HAND COLUMN -->

<div id="rightColumn" style="float: right; width:69%;  padding:0px 4px 4px 4px">

<c:if test="${! empty specimen}">

<!--  SPECIMEN SECTION -->
<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.specimenDetails" text="Specimen Details"/><span style="position: absolute; right:40px;"><a id="editSpecimen" class="edit" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a>
<a class="delete" href="delete.form?specimenId=${specimen.id}&patientId=${specimen.patient.patientId}&patientProgramId=${patientProgramId}" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteSpecimen" text="Are you sure you want to delete this specimen?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></span></b>

<div class="box" id="specimen" style="margin:0px">

<!-- START OF SPECIMEN DETAILS DIV -->
<div id="details_specimen" style="margin:0px">

<table cellspacing="0" cellpadding="0">

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td><td><nobr>${specimen.identifier}</nobr></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</nobr></td><td align="left"><nobr>${specimen.provider.personName}</nobr></td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</nobr></td><td><nobr>${specimen.type.displayString}</nobr></td> 
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.appearance" text="Appearance"/>:</nobr></td><td align="left">${specimen.appearance.displayString}</td>
<td width="100%">&nbsp;</td>
<!-- <td style="font-weight:bold"><nobr><spring:message code="mdrtb.locationCollected" text="Location Collected"/>:</td><td><nobr>${specimen.location.displayString}</nobr></td> -->

</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</nobr></td><td align="left"><nobr><openmrs:formatDate date="${specimen.dateCollected}" format="${_dateFormatDisplay}"/></nobr></td>
<td width="100%" colspan="3">&nbsp;</td>


</tr>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td><td colspan="4" align="left"><mdrtb:format obj="${specimen.comments}"/></td>

</tr>
<%--
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.scannedLabReports" text="Scanned Lab Reports"/>:</nobr></td>
<td colspan="3">
<c:forEach var="report" items="${specimen.scannedLabReports}">
<c:choose>
	<c:when test="${! empty report.filename && ! empty report.file}">
		<nobr><a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${report.id}&view=download&viewType=download">${report.filename}</a> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:when>
	<c:when test="${! empty report.filename && empty report.file}">
		<nobr>${report.filename} - <spring:message code="mdrtb.missingScannedLabReport"/> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:when>
	<c:otherwise>
		<nobr><spring:message code="mdrtb.missingScannedLabReport"/> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:otherwise>
</c:choose>
<br/>
</c:forEach>
</td>
<td width="100%">&nbsp;</td>
</tr>
 --%>
</table>
</div>
<!--  END OF SPECIMEN SECTION -->

<!--  EDIT SPECIMEN SECTION -->

<div id="edit_specimen"  style="display:none;margin:0px">

<form id="specimen" action="specimen.form?submissionType=specimen&specimenId=${specimen.id}&patientProgramId=${patientProgramId}&testId=-1" method="post" enctype="multipart/form-data">

<!--  DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(specimenErrors.allErrors) > 0}">
	<c:forEach var="error" items="${specimenErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<table cellspacing="0" cellpadding="0">

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td>
<td><input type="text" size="10" name="identifier" value="${specimen.identifier}"/></td>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.collectedBy" text="Collected By"/>:</nobr></td>
<td align="left">
<select name="provider">
<option value=""/>
<c:forEach var="provider" items="${providers}">
<option value="${provider.id}" <c:if test="${specimen.provider == provider}">selected</c:if> >${provider.personName}</option>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleType" text="Sample Type"/>:</nobr></td>
<td>
<select name="type">
<option value=""></option>
<c:forEach var="type" items="${types}">
<option value="${type.answerConcept.id}" <c:if test="${specimen.type == type.answerConcept}">selected</c:if> >${type.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<!-- 
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.locationCollected" text="Location Collected"/>:</nobr></td>
<td>
<select name="location">
<c:forEach var="location" items="${locations}">
<c:if test="${location.locationId==86 || location.locationId==87}">
<option value="${location.locationId}" <c:if test="${location == specimen.location}">selected</c:if> >${location.displayString}</option>
</c:if>
</c:forEach>
</select>	
</td> -->
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.appearance" text="Appearance"/>:</nobr></td>
<td align="left">
<select name="appearance">
<option value=""></option>
<c:forEach var="appearance" items="${appearances}">
<option value="${appearance.answerConcept.id}" <c:if test="${specimen.appearance == appearance.answerConcept}">selected</c:if> >${appearance.answerConcept.displayString}</option>
</c:forEach>
</select>

<td width="100%">&nbsp;</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCollected" text="Date Collected"/>:</nobr></td>
<td align="left"><nobr><openmrs_tag:dateField formFieldName="dateCollected" startValue="${specimen.dateCollected}"/></nobr></td>
<td width="100%" colspan="3">&nbsp;</td>
</tr>
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="4"><textarea name="comments" cols="100" rows="2">${specimen.comments}</textarea></td>

</tr>
<%--
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.scannedLabReports" text="Scanned Lab Reports"/>:</nobr></td>
<td colspan="3">
<c:forEach var="report" items="${specimen.scannedLabReports}">
<c:choose>
	<c:when test="${! empty report.filename && ! empty report.file}">
		<nobr><a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${report.id}&view=download&viewType=download">${report.filename}</a> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:when>
	<c:when test="${! empty report.filename && empty report.file}">
		<nobr>${report.filename} - <spring:message code="mdrtb.missingScannedLabReport"/> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:when>
	<c:otherwise>
		<nobr><spring:message code="mdrtb.missingScannedLabReport"/> - <spring:message code="mdrtb.lab"/>: ${report.lab.displayString}</nobr>
	</c:otherwise>
</c:choose>
<button class="removeScannedLabReport" value="${report.id}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button></nobr>
<input type="hidden" id="removeScannedLabReport${report.id}" name="removeScannedLabReport" value=""/></span>
<br/>
</c:forEach>
<nobr><input type="file" name="addScannedLabReport" size="50" value="${! empty addScannedLabReport ? addScannedLabReport.originalFilename : ''}"/> <spring:message code="mdrtb.lab"/>:
<select name="addScannedLabReportLocation">
<option value=""/>
<c:forEach var="location" items="${locations}">
<option value="${location.locationId}" <c:if test="${location == addScannedLabReportLocation}">selected</c:if>>${location.displayString}</option>
</c:forEach>
</select>	
</nobr>
</td>
<td width="100%">&nbsp;</td>
</tr>
 --%>
<tr>
<td colspan="5" align="left"><button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button type="reset" id="cancelSpecimen"><spring:message code="mdrtb.cancel" text="Cancel"/></button></td>
</tr>

</table>

</form>
</div>
<!-- END OF EDIT SPECIMEN SECTION -->

<br/>

<div id="tests" style="position:relative"> 
<b class="boxHeader"><spring:message code="mdrtb.tests" text="Tests"/></b>
<br/>


<div align="left" id="add">
&nbsp;&nbsp;<span style="font-weight:bold"><spring:message code="mdrtb.addANewLabTest" text="Add a new Lab Test:"/>:</span>
<select id="addSelect">
<c:forEach var="test" items="${testTypes}">
	<option value="${test}"><spring:message code="mdrtb.${test}"/></option>
</c:forEach>
</select>
<button id="addButton" type="button"><spring:message code="mdrtb.add" text="Add"/></button>
</div>

<br/>


<c:forEach var="test" items="${specimen.tests}" varStatus="testIteration">
<c:if test="${! empty test.id}"> <!--  hack to fix glitch that occurs when a validation error occurs when adding a test  -->

<!--  TEST DETAILS SECTION -->

<div align="center" id="details_${test.id}" class="detailBox">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if><span style="position: absolute; right:30px;"><a id="${test.id}" class="edit" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"><spring:message code="mdrtb.edit" text="edit"/></a>&nbsp;&nbsp;<a href="delete.form?testId=${test.id}&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" class="delete" onclick="return confirm('<spring:message code="mdrtb.confirmDeleteTest" text="Are you sure you want to delete this test?"/>')"><spring:message code="mdrtb.delete" text="delete"/></a></span></b>
<div class="box" style="margin:0px">
<table width = "50%" cellpadding="0" border="0">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td><td align="left"><nobr>${specimen.identifier}</nobr></td>
<!--<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateOrdered" text="Date ordered"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateOrdered}" format="${_dateFormatDisplay}"/></nobr></td> -->
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.lab" text="Lab"/>:</nobr></td><td align="left"><nobr>${test.lab}</nobr></td>
<!-- <td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateSampleReceived" text="Date sample received"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.dateReceived}" format="${_dateFormatDisplay}"/></nobr></td> -->
<td width="100%">&nbsp;</td>
</tr>


<c:if test="${test.testType eq 'dst'}">
<tr>
 <td style="font-weight:bold"><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td><td align="left"><nobr>${test.method.displayString}</nobr></td>


<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td><td><nobr><openmrs:formatDate date="${test.startDate}" format="${_dateFormatDisplay}"/></nobr></td>
</tr>
</c:if>

<c:if test="${test.testType eq 'smear' || test.testType eq 'culture' || test.testType eq 'xpert' || test.testType eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td><td align="left"><nobr>${test.result.displayString}</nobr></td>
</tr>
</c:if>
<!-- DIRTY HACK TO REMOVE DIRECT/INDIRECT FOR TAJIKSTAN. CHANGE TEST TO type eq 'dst' TO REVERT -->
<%--<c:if test="${test.testType eq 'dst'}">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td><td><nobr>
<c:if test="${test.direct == true}">
	<spring:message code="mdrtb.direct" text="Direct"/>	
</c:if>
<c:if test="${test.direct == false}">
	<spring:message code="mdrtb.indirect" text="Indirect"/>
</c:if>
</nobr></td>
</c:if>
--%>


<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td><td align="left"><nobr><openmrs:formatDate date="${test.resultDate}" format="${_dateFormatDisplay}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>



<c:if test="${test.testType eq 'smear' && test.result == scanty}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td><td align="left"><nobr>${test.bacilli}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture' && test.result == scanty}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td><td align="left"><nobr>${test.colonies}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>
<!-- DIRTY HACK TO REMOVE DAYS +VE FOR TAJIKSTAN. CHANGE FIRST PART OF TEST TO type eq 'culture' || type eq 'dst' TO REVERT -->
<c:if test="${test.testType eq 'no_test' && mdrtb:collectionContains(positiveResults, test.result)}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.daysToPositivity" text="Days to Positivity"/>:</nobr></td><td align="left"><nobr>${test.daysToPositivity}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<!-- DIRTY HACK TO REMOVE ORGANISM FOR TAJIKSTAN. CHANGE TEST TO type eq 'culture' || type eq 'dst' TO REVERT -->
<c:if test="${test.testType eq 'no_test'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td><td><nobr>${test.organismType.displayString}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<!-- DIRTY HACK TO REMOVE ORGANISM NON-CODED FOR TAJIKSTAN. CHANGE TEST TO type eq 'culture' || type eq 'dst' TO REVERT -->
<c:if test="${(test.testType eq 'no test') && test.organismType == otherMycobacteriaNonCoded}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td><td><nobr>${test.organismTypeNonCoded}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<!-- DIRTY HACK TO REMOVE COLONIES FOR TAJIKSTAN. CHANGE TEST TO type eq 'dst' TO REVERT -->
<c:if test="${test.testType eq 'no test'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td><td><nobr>${test.coloniesInControl}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'xpert'  || test.testType eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.rifResistance" text="RIF Resistance"/>:</nobr></td><td align="left"><nobr>${test.rifResistance.displayString}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.inhResistance" text="INH Resistance"/>:</nobr></td><td align="left"><nobr>${test.inhResistance.displayString}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>
<!-- 
<c:if test="${test.testType eq 'xpert'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.mtbBurden" text="MTB Burden"/>:</nobr></td><td align="left"><nobr>${test.mtbBurden.displayString}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.errorCode" text="Error Code"/>:</nobr></td><td align="left"><nobr>${test.errorCode}</nobr></td>
<td colspan="2">&nbsp;</td>
</tr>

 -->
</c:if>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td><td colspan="3" align="left"><mdrtb:format obj="${test.comments}"/></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${test.testType eq 'dst'}">
<!-- fetch the map of test results for this dst -->
<c:set var="resultsMap" value="${test.resultsMap}"/>
<br/>
<table cellpadding="0">
<tr>
<td style="font-weight:bold"><u><spring:message code="mdrtb.drug" text="Drug"/></u></td>
<!--  <td style="font-weight:bold"><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td> -->
<td style="font-weight:bold"><u><spring:message code="mdrtb.result" text="Result"/></u></td>
<!--  <td style="font-weight:bold"><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td> -->
</tr>
<c:forEach var="drugType" items="${drugTypes}">
<c:if test="${!empty resultsMap[drugType.id]}">
	<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
		<tr>
		<td><nobr>${dstResult.drug.displayString}</nobr></td>
		<!-- <td><nobr>${dstResult.concentration}</nobr></td> -->
		<td><nobr>${dstResult.result.displayString}</nobr></td>
		<!-- <td><nobr>${dstResult.colonies}</nobr></td> -->
		</tr>
	</c:forEach>
</c:if>
</c:forEach>
</table>
</c:if>
<!-- end of the DST table -->

</div>
<br/>
</div> <!-- end of details div -->

<!-- END OF TEST DETAILS SECTION -->

<!-- EDIT TESTS SECTION -->

<div align="center" id="edit_${test.id}" class="editBox" style="display:none">

<form id="${test.testType}" action="specimen.form?submissionType=${test.testType}&${test.testType}Id=${test.id}&testId=${test.id}&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${test.testType}"/><c:if test="${!empty test.accessionNumber}"> (${test.accessionNumber}) </c:if></b>
<div class="box" style="margin:0px">

<!-- DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(testErrors.allErrors) > 0}">
	<c:forEach var="error" items="${testErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<table cellpadding="0">

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleid" text="Sample ID"/>:</nobr></td>
<!-- <td><input type="text" name="accessionNumber" value="${test.accessionNumber}"/></td>-->
<td><input type="text" name="accessionNumber" value="${specimen.identifier}"/></td>
<%-- <td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateOrdered" text="Date ordered"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateOrdered" startValue="${test.dateOrdered}"/></nobr></td> --%>
<td width="100%">&nbsp;</td>
</tr>

<!--  awful hack for Tajikistan to ensure that only certain locations appear here -->
<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</td>
<td><select name="lab">
<c:forEach var="location" items="${locations}">
<c:if test="${test.testType eq 'dst'}">
 <c:if test="${(location.locationId == 85 || location.locationId == 2 || location.locationId == 88 || location.locationId == 89 || location.locationId == 90 || location.locationId == 91)}">
	<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.displayString}</option>
</c:if>	
</c:if>
<c:if test="${test.testType eq 'culture'}">
 <c:if test="${(location.locationId == 85 || location.locationId == 2 || location.locationId == 88)}">
	<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.displayString}</option>
</c:if>	
</c:if>
<c:if test="${test.testType eq 'smear' || test.testType eq 'xpert' || test.testType eq 'hain'}" >
<option value="${location.locationId}" <c:if test="${location == test.lab}">selected</c:if> >${location.displayString}</option>
</c:if>
</c:forEach>
</select>
</td>
<%-- <td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateSampleReceived" text="Date sample received"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="dateReceived" startValue="${test.dateReceived}"/></nobr></td> --%>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${test.testType eq 'dst'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td>
<td><select name="method">
<option value=""></option>
<c:forEach var="method" items="${test.testType eq 'smear'? smearMethods : (test.testType eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}" <c:if test="${method.answerConcept == test.method}">selected</c:if> >${method.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
</tr>
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate" startValue="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>
</c:if>



<c:if test="${test.testType eq 'smear' || test.testType eq 'culture'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${test.testType eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'xpert' || test.testType eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${mtbResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.rifResult" text="RIF Result"/>:</nobr></td>
<td><select name="rifResistance">
<option value=""></option>
<c:forEach var="rifresult" items="${rifResults}">
<option value="${rifresult.answerConcept.id}" <c:if test="${rifresult.answerConcept == test.rifResistance}">selected</c:if> >${rifresult.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>

<c:if test="${test.testType eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.inhResult" text="INH Result"/>:</nobr></td>
<td><select name="inhResistance">
<option value=""></option>
<c:forEach var="inhresult" items="${inhResults}">
<option value="${inhresult.answerConcept.id}" <c:if test="${inhresult.answerConcept == test.inhResistance}">selected</c:if> >${inhresult.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<!-- 
<c:if test="${test.testType eq 'xpert'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.mtbBurden" text="MTB Burden"/>:</nobr></td>
<td><select name="mtbBurden">
<option value=""></option>
<c:forEach var="mtbBurden" items="${xpertMtbBurdens}">
<option value="${mtbBurden.answerConcept.id}" <c:if test="${mtbBurden.answerConcept == test.mtbBurden}">selected</c:if> >${mtbBurden.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.errorCode" text="Error Code"/>:</nobr></td>
<td><input type="text" name="errorCode" id="errorCode" value="${test.errorCode}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
 -->
</c:if>

<%-- <c:if test="${test.testType eq 'dst'}">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option <c:if test="${test.direct}">selected </c:if>value="1"><spring:message code="mdrtb.direct" text="Direct"/></option>
<option <c:if test="${!test.direct}">selected </c:if>value="0"><spring:message code="mdrtb.indirect" text="Indirect"/></option>
</select></td>
</c:if>
</tr> --%>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate" startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>




<c:if test="${test.testType eq 'smear'}">
<tr class="bacilli" <c:if test="${test.result != scanty}"> style="display:none"</c:if>>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td>
<td><input type="text" name="bacilli" id="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'culture'}">
<tr class="colonies" <c:if test="${test.result != scanty}"> style="display:none;"</c:if>>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<%--<c:if test="${test.testType eq 'culture'}">
<tr class="daysToPositivity" <c:if test="${!mdrtb:collectionContains(positiveResults, test.result)}"> style="display:none;"</c:if>>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.daysToPositivity" text="Days To Positivity"/>:</nobr></td>
<td><input type="text" size="6" name="daysToPositivity" id="daysToPositivity" value="${test.daysToPositivity}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>--%>

<%--<c:if test="${test.testType eq 'culture' || test.testType eq 'dst'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.displayString}</option>
</c:forEach></td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" <c:if test="${test.organismType != otherMycobacteriaNonCoded}"> style="display:none;"</c:if>>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value="${test.organismTypeNonCoded}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${test.testType eq 'dst'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td>
<td><input type="text" name="coloniesInControl" value="${test.coloniesInControl}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>--%>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments">${test.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${test.testType eq 'dst'}">
<br/>
<table cellpadding="0">

<tr>
<td style="font-weight:bold"><u><spring:message code="mdrtb.drug" text="Drug"/></u></td>
<%-- <td style="font-weight:bold"><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td> --%>
<td style="font-weight:bold"><u><spring:message code="mdrtb.result" text="Result"/></u></td>
<%-- <td style="font-weight:bold"><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td> --%>
</tr>
<c:forEach var="drugType" items="${drugTypes}">
	<c:if test="${!empty resultsMap[drugType.id]}">
		<c:forEach var="dstResult" items="${resultsMap[drugType.id]}">
			<tr class="dstResult">
			<td><nobr>${dstResult.drug.displayString}</nobr></td>
			<!-- <td><nobr>${dstResult.concentration}</nobr></td> -->
			<td><nobr>${dstResult.result.displayString}</nobr></td>
			<!-- <td><nobr>${dstResult.colonies}</nobr></td> -->
			<td><button class="removeDstResult" value="${dstResult.id}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button>
				<input type="hidden" id="removeDstResult${dstResult.id}" name="removeDstResult" value=""/></td>
			</tr>
		</c:forEach>
	</c:if>
</c:forEach>
	
<!-- now the rows to add a new table -->
<!-- note that we just add thirty, blank, hidden rows here, which is kind of hacky, but makes the code simplier! :) -->
<!-- note that we have to add the test id to the tr so that jquery can find the right tr to hide/show 
	 when there are multiple dsts for a sample, but we don't need to add this count to the actual form fields because
	 we never update more than one test at any time -->
<c:forEach begin="1" end="30" varStatus="i">
	<tr id="addDstResult${test.id}_${i.count}" class="addDstResult" style="display:none">
	<td><select name="addDstResult${i.count}.drug">
		<option value=""></option>
		<c:forEach var="drug" items="${drugTypes}">
			<option value="${drug.id}" <c:if test="${! empty addDstResultDrug && addDstResultDrug[i.count - 1] == drug.id}">selected</c:if>>${drug.displayString}</option>
		</c:forEach>
		</select>
	</td>
	<td><input type="input" size="6" name="addDstResult${i.count}.concentration" value="${! empty addDstResultConcentration ? addDstResultConcentration[i.count - 1] : ''}"/></td>
	<td><select name="addDstResult${i.count}.result" class="dstResult">
		<option value=""></option>
		<c:forEach var="possibleResult" items="${dstResults}">
			<option value="${possibleResult.id}" <c:if test="${! empty addDstResultResult && addDstResultResult[i.count - 1] == possibleResult.id}">selected</c:if>>${possibleResult.displayString}</option>
		</c:forEach></td>
		</select>
	</td>
	<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="${! empty addDstResultColonies ? addDstResultColonies[i.count - 1] : ''}" class="dstColonies" style="display:none"/></td>
	<td><button class="removeDstResultRow" value="${i.count}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button></td>	
	</tr>
</c:forEach>

	<tr>
	<td><button class="addDstResultRow" value="${test.id}" type="button"><spring:message code="mdrtb.addDSTResult" text="Add DST result"/></button></td>
	<!--  set to 2 for TJK. Change to 4 if showing conc and colonies -->
	<td colspan="2"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button type="reset" id="${test.id}" class="cancel"><spring:message code="mdrtb.cancel" text="Cancel"/></button>

</form>

</div>
<br/>
</div>

</c:if> <!--  hack to fix glitch that occurs when a validation error occurs when adding a test  -->
</c:forEach>

<!--  END OF EDIT TESTS SECTION -->

<!-- ADD TEST SECTION -->
<!--  TODO: would really like to combine this with the edit tests section, to avoid all this code repeating... -->


<c:forEach var="type" items="${testTypes}">

<div align="center" id="add_${type}" class="addBox" style="display:none">

<form id="${type}" action="specimen.form?submissionType=${type}&${type}Id=-1&testId=-1&specimenId=${specimen.id}&patientProgramId=${patientProgramId}" method="post">

<b class="boxHeader" style="margin:0px"><spring:message code="mdrtb.${type}"/>: <spring:message code="mdrtb.add" text="Add"/></b>
<div class="box" style="margin:0px">

<!-- DISPLAY ANY ERROR MESSAGES -->
<c:if test="${fn:length(testErrors.allErrors) > 0}">
	<c:forEach var="error" items="${testErrors.allErrors}">
		<span class="error"><spring:message code="${error.code}"/></span><br/><br/>
	</c:forEach>
	<br/>
</c:if>

<table cellpadding="0">

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.sampleid]" text="Sample ID"/>:</nobr></td>
<td><input type="text" name="accessionNumber" value="${! empty test.accessionNumber ? test.accessionNumber : specimen.identifier}"/></td> <!--  if no accession number, default for the accession number of the first test -->
<td width="100%">&nbsp;</td>
</tr>

<tr>
<td style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</td>
<td><select name="lab">
<c:forEach var="location" items="${locations}">
<%--<option value="${location.locationId}" <c:if test="${location == test.lab || (fn:length(specimen.tests) > 0 && location == specimen.tests[0].lab)}">selected</c:if> >${location.displayString}</option> --%>
<c:if test="${type eq 'dst'}">
 <c:if test="${(location.locationId == 85 || location.locationId == 2 || location.locationId == 88 || location.locationId == 89 || location.locationId == 90 || location.locationId == 91)}">
	<option value="${location.locationId}" <c:if test="${location == test.lab || (fn:length(specimen.tests) > 0 && location == specimen.tests[0].lab)}">selected</c:if> >${location.displayString}</option>
</c:if>	
</c:if>
<c:if test="${type eq 'culture'}">
 <c:if test="${(location.locationId == 85 || location.locationId == 2 || location.locationId == 88)}">
	<option value="${location.locationId}" <c:if test="${location == test.lab || (fn:length(specimen.tests) > 0 && location == specimen.tests[0].lab)}">selected</c:if> >${location.displayString}</option>
</c:if>	
</c:if>
<c:if test="${type eq 'smear' || type eq 'xpert' || type eq 'hain'}" >
	<option value="${location.locationId}" <c:if test="${location == test.lab || (fn:length(specimen.tests) > 0 && location == specimen.tests[0].lab)}">selected</c:if> >${location.displayString}</option>
</c:if>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>

<c:if test="${type eq 'dst'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.method" text="Method"/>:</nobr></td>
<td <c:if test="${type eq 'dst'}">colspan="3" align="left"</c:if>><select name="method">
<option value=""></option>
<c:forEach var="method" items="${type eq 'smear'? smearMethods : (type eq 'culture' ? cultureMethods : dstMethods)}">
<option value="${method.answerConcept.id}" <c:if test="${method.answerConcept == test.method}">selected</c:if> >${method.answerConcept.displayString}</option>
</c:forEach>
</select>
</td>
<td width="100%">&nbsp;</td>
</tr>
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateStarted" text="Date started"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="startDate"  startValue="${test.startDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>
</c:if>


<c:if test="${type eq 'smear' || type eq 'culture'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${type eq 'smear' ? smearResults : cultureResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<c:if test="${type eq 'xpert' || type eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.result" text="Result"/>:</nobr></td>
<td><select name="result" class="result">
<option value=""></option>
<c:forEach var="result" items="${mtbResults}">
<option value="${result.answerConcept.id}" <c:if test="${result.answerConcept == test.result}">selected</c:if> >${result.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.rifResult" text="RIF Result"/>:</nobr></td>
<td><select name="rifResistance">
<option value=""></option>
<c:forEach var="rifresult" items="${rifResults}">
<option value="${rifresult.answerConcept.id}" <c:if test="${rifresult.answerConcept == test.rifResistance}">selected</c:if> >${rifresult.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>

<c:if test="${type eq 'hain'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.inhResult" text="INH Result"/>:</nobr></td>
<td><select name="inhResistance">
<option value=""></option>
<c:forEach var="inhresult" items="${inhResults}">
<option value="${inhresult.answerConcept.id}" <c:if test="${inhresult.answerConcept == test.inhResistance}">selected</c:if> >${inhresult.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<!-- 
<c:if test="${type eq 'xpert'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.mtbBurden" text="MTB Burden"/>:</nobr></td>
<td><select name="mtbBurden">
<option value=""></option>
<c:forEach var="mtbBurden" items="${xpertMtbBurdens}">
<option value="${mtbBurden.answerConcept.id}" <c:if test="${mtbBurden.answerConcept == test.mtbBurden}">selected</c:if> >${mtbBurden.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
</tr>
</c:if>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.errorCode" text="Error Code"/>:</nobr></td>
<td><input type="text" name="errorCode" id="errorCode" value="${test.errorCode}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
 -->

</c:if>
<%-- <c:if test="${type eq 'dst'}">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.directIndirect" text="Direct/Indirect"/>:</nobr></td>
<td><select name="direct">
<option value=""></option>
<option <c:if test="${test.direct}">selected </c:if>value="1"><spring:message code="mdrtb.direct" text="Direct"/></option>
<option <c:if test="${!test.direct}">selected </c:if>value="0"><spring:message code="mdrtb.indirect" text="Indirect"/></option>
</select></td>
</c:if>
</tr> --%>

<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.dateCompleted" text="Date completed"/>:</nobr></td>
<td><nobr><openmrs_tag:dateField formFieldName="resultDate"  startValue="${test.resultDate}"/></nobr></td>
<td width="100%">&nbsp;</td>
</tr>


<c:if test="${type eq 'smear'}">
<tr class="bacilli" style="display:none;">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofbacilli" text="Number of Bacilli"/>:</nobr></td>
<td><input type="text" name="bacilli" id="bacilli" value="${test.bacilli}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<c:if test="${type eq 'culture'}">
<tr class="colonies" style="display:none;">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.numberofcolonies" text="Number of Colonies"/>:</nobr></td>
<td><input type="text" name="colonies" id="colonies" value="${test.colonies}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<!-- DIRTY HACK TO REMOVE DAYS TO +VE FOR TAJIKSTAN. CHANGE TEST TO type eq 'culture' TO REVERT -->
<c:if test="${type eq 'no_test'}">
<tr class="daysToPositivity" style="display:none;">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.daysToPositivity" text="Days To Positivity"/>:</nobr></td>
<td><input type="text" size="6" name="daysToPositivity" id="daysToPositivity" value="${test.daysToPositivity}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<!-- DIRTY HACK TO REMOVE ORGANISM FOR TAJIKSTAN. CHANGE TEST TO type eq 'culture' || type eq 'dst' TO REVERT -->
<c:if test="${type eq 'no_test'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismType" text="Organism Type"/>:</nobr></td>
<td><select name="organismType" class="organismType">
<option value=""></option>
<c:forEach var="organismType" items="${organismTypes}">
<option value="${organismType.answerConcept.id}" <c:if test="${organismType.answerConcept == test.organismType}">selected</c:if> >${organismType.answerConcept.displayString}</option>
</c:forEach></td>
</select>
</td>
<td colspan="2">&nbsp;</td>
</tr>
<tr class="organismTypeNonCoded" style="display:none;">
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.organismTypeNonCoded" text="Organism Type Non-Coded"/>:</nobr></td>
<td><input type="text" name="organismTypeNonCoded" id="organismTypeNonCoded" value="${test.organismTypeNonCoded}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>

<%--
<c:if test="${type eq 'dst'}">
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.coloniesincontrol" text="Colonies in control"/>:</nobr></td>
<td><input type="text" name="coloniesInControl" value="${test.coloniesInControl}"/></td>
<td colspan="2">&nbsp;</td>
</tr>
</c:if>
 --%>
<tr>
<td style="font-weight:bold"><nobr><spring:message code="mdrtb.comments" text="Comments"/>:</nobr></td>
<td colspan="3"><textarea cols="60" rows="4" name="comments">${test.comments}</textarea></td>
<td width="100%">&nbsp;</td>
</tr>

</table>

<!-- handle the DST table -->
<c:if test="${type eq 'dst'}">
<br/>
<table cellpadding="0" width="100%">
<!-- note that we are simply adding 30 rows here, and populating and showing any default drugs in the first rows in the list -->
<tr>
<td style="font-weight:bold"><u><spring:message code="mdrtb.drug" text="Drug"/></u></td>
<!-- <td style="font-weight:bold"><u><spring:message code="mdrtb.concentration" text="Concentration"/></u></td> -->
<td style="font-weight:bold"><u><spring:message code="mdrtb.result" text="Result"/></u></td>
<!-- <td style="font-weight:bold"><u><spring:message code="mdrtb.colonies" text="Colonies"/></u></td> -->
</tr>	
	<c:forEach begin="1" end="30" varStatus="i">
		<tr id="addDstResult_${i.count}" class="addDstResult" <c:if test="${i.count > fn:length(defaultDstDrugs)}">style="display:none"</c:if> >
		<td><select id="addDstResult${i.count}.drug" name="addDstResult${i.count}.drug">
			<option value=""></option>
			<c:forEach var="drug" items="${drugTypes}">
				<!-- the test here is used to set the default drugs from the list as required -->
				<option value="${drug.id}" <c:if test="${((i.count <= fn:length(defaultDstDrugs)) && defaultDstDrugs[i.count - 1][0].id == drug.id) || (! empty addDstResultDrug && addDstResultDrug[i.count - 1] == drug.id)}">selected</c:if> >${drug.displayString}</option>
			</c:forEach>
			</select>
		</td>
		<%--<td><input type="input" size="6" name="addDstResult${i.count}.concentration" value="${! empty addDstResultConcentration ? addDstResultConcentration[i.count - 1] : defaultDstDrugs[i.count - 1][1]}"/></td>  --%>
		<td><select name="addDstResult${i.count}.result" class="dstResult">
			<option value=""></option>
			<c:forEach var="possibleResult" items="${dstResults}">
				<option value="${possibleResult.id}" <c:if test="${! empty addDstResultResult && addDstResultResult[i.count - 1] == possibleResult.id}">selected</c:if>>${possibleResult.displayString}</option>
			</c:forEach></td>
			</select>
		</td>
		<%--<td><input type="text" size="6" name="addDstResult${i.count}.colonies" value="${! empty addDstResultColonies ? addDstResultColonies[i.count - 1] : ''}" class="dstColonies" style="display:none"/></td>--%>
		<td><button class="removeDstResultRow" value="${i.count}" type="button"><spring:message code="mdrtb.remove" text="Remove"/></button></td>
		</tr>
	</c:forEach>

	<tr>
	<td><button class="addDstResultRow" value="" type="button"><spring:message code="mdrtb.addDSTResult" text="Add DST result"/></button></td>
	<td colspan="4"/>
	</tr>
	
</table>
<br/>
</c:if>
<!-- end of the DST table -->

<button type="submit"><spring:message code="mdrtb.save" text="Save"/></button><button class="cancel" type="reset"><spring:message code="mdrtb.cancel" text="Cancel"/></button>
</form>
</div>
</div>

</c:forEach> 

<!-- END ADD TEST SECTION -->

</div> <!-- END OF TEST DIV -->

</div> <!-- END OF SPECIMEN DIV -->

</c:if> <!-- close tag to ${! empty specimen} -->

</div> <!-- END OF RIGHT HAND COLUMN -->

</div> <!--  END OF PAGE -->

<!--  TODO: figure out footer alignment and add footer back in  -->
<!--  
<style type="text/css">
	#footer {position:fixed}
</style> -->
