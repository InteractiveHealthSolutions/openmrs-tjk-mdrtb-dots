<%@ include file="/WEB-INF/view/module/labmodule/include.jsp"%> 
<%@ include file="/WEB-INF/view/module/labmodule/dotsHeader.jsp"%>
<%@ taglib prefix="form" uri="/WEB-INF/view/module/mdrtb/resources/spring-form.tld"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/mdrtb/mdrtb.css"/>

<style type="text/css">
td
{
    padding:0 10px 0 10px;
}

form {
 margin: 0;
 padding: 0;
}
</style>

<openmrs:portlet url="mdrtbPatientHeader" id="mdrtbPatientHeader" moduleId="mdrtb" patientId="${patientId}"/>

<h3> <spring:message code="labmodule.labResult" /> </h3>
<br>

<!-- Start of left Column -->
<div id="leftcolumn" style="float: left; width:70%;  padding:0px 4px 4px 4px">

	<b class="boxHeader" style="margin:0px">
		&nbsp;
		<spring:message code="labmodule.labEntry.title" text="TB 05"/>
		<span id="specimen_edit_span" name="specimen_edit_span" style="float:right">
		    <A HREF="javascript:window.print()">Click to Print This Page</A>
		</span> 
	</b>
	
	<div class="box" id="" style="margin:0px">
	
		<table cellspacing="5" cellpadding="0" width="100%">
		
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="mdrtb.collectedBy" text="Collected By"/> : </font>
					&nbsp;
					${labResult.provider.personName}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="mdrtb.lab" text="Lab"/>:</font>
					&nbsp;
					${labResult.location.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.labNumber" text="lab Number"/>:</font>
					&nbsp;
					${labResult.labNumber}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateRecieve"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateCollected}" format="${_dateFormatDisplay}"/>
					&nbsp;&nbsp;
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.dateInvestigation"/> : </font>
					&nbsp;
					<openmrs:formatDate date="${labResult.investigationDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.requestingLab"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"> <spring:message code="labmodule.labEntry.requestingLabName"/> : </font>
					&nbsp;
					${labResult.requestingLabName.displayString}
					&nbsp;&nbsp;
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.investigationPurpose"/> :</font>
					&nbsp;
					${labResult.investigationPurpose.displayString}
				</td>
			</tr>
			
			<tr>
				<td> 
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.biologicalSpecimen"/> : </font>
					&nbsp;
					${labResult.biologicalSpecimen.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold; vertical-align:top"><spring:message code="labmodule.comments"/>:</font>
					&nbsp;
					${labResult.comments}
				</td>
			</tr>
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.peripheralLabInfo"/></b>
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.peripheralLabNo"/> : </font>
					&nbsp;
					${labResult.peripheralLabNumber}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.microscopyResult"/>:</font>
					&nbsp;
					${labResult.microscopyResult.displayString}
				</td>
			</tr>
			
			<tr>
				<td>
					<font style="font-weight:bold"><spring:message code="labmodule.labEntry.dateResult"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${labResult.dateResult}" format="${_dateFormatDisplay}"/>
				</td>
			</tr> 
		
		</table>
	</div>	
		
		
		<b class="boxHeader" style="margin:0px; width:100%;">
		&nbsp;
		<spring:message code="labmodule.labResults" text="Lab Results"/>
	</b>
	
	<div class="box" style="margin:0px">

	<table cellspacing="5" cellpadding="0" width="100%">
	
	<c:if test="${fn:length(labResult.microscopies) > 0}">
	
		<c:forEach var="microscopy" items="${labResult.microscopies}" varStatus="i">
		
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.bacterioscopy"/> ( ${i.count} )
					</b>
				</td>
			</tr>
			
			<tr><td>
			
			<table style="font-size: 13px">
			
			<tr>
				<td>
				
					<input hidden type="text" name="labResultId" value="${labResult.id}">
					<input hidden type="text" name="id" value="${microscopy.id}">
				
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${microscopy.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
								
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.appearance"/>:</font>
					&nbsp;
					${microscopy.sampleApperence.displayString}
						
					&nbsp;&nbsp;
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.bacterioscopy.result"/>:</font>
					&nbsp;
					${microscopy.sampleResult.displayString}
				</td>
			</tr>
								
			</table>
				
		</c:forEach>
	
	</c:if>
	
	<c:if test="${fn:length(labResult.xperts) > 0}">
	
		<c:forEach var="xpert" items="${labResult.xperts}" varStatus="i">
		   				
				<tr>
					<td>
						<br>
						<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.xpert"/> ( ${i.count} )
						</b>
					</td>
				</tr>
				
				<tr><td>
			
				<table style="font-size: 13px">
				
				<tr>
					<td>
					
						<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${xpert.id}">
						
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
						&nbsp;
						<openmrs:formatDate date="${xpert.resultDate}" format="${_dateFormatDisplay}"/>
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
						&nbsp;
						${xpert.mtbBurden.displayString}
						&nbsp;&nbsp;
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
						&nbsp;
						${xpert.rifResistance.displayString}
					</td>
				</tr>
							
				<tr>
					<td>
						<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.errorCode"/>:</font>
						&nbsp;
						${xpert.errorCode}
					</td>
				</tr>
				
				</table>
				</td></tr>
					
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS) > 0}">
	
		<c:forEach var="hain" items="${labResult.HAINS}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain"/> ( ${i.count} )
					</b>
				</td>
			</tr>
			
	<tr><td>

		<table style="font-size: 13px">
			
			<tr>
				<td>
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain.id}">
						
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
						
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain.mtbBurden.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.r"/>:</font>
					&nbsp;
					${hain.rifResistance.displayString}			
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.h"/>:</font>
					&nbsp;
					${hain.inhResistance.displayString}				
				</td>
		  </tr>
		  
		  </table>
		  </td></tr>
			
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.HAINS2) > 0}">
	
		<c:forEach var="hain2" items="${labResult.HAINS2}" varStatus="i">
			
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.labEntry.hain2"/> ( ${i.count} )
					</b>
				</td>
			</tr>
			
			<tr><td>

			<table style="font-size: 13px">
			
			<tr>
				<td>
				
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${hain2.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.analysisDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${hain2.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.xpert.mtb"/>:</font>
					&nbsp;
					${hain2.mtbBurden.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.mox"/>:</font>
					&nbsp;
					${hain2.moxResistance.displayString} 
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.km"/>:</font>
					&nbsp;
					${hain2.cmResistance.displayString}
					&nbsp;&nbsp;
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.hain2.e"/>:</font>
					&nbsp;
					${hain2.erResistance.displayString} 
				</td>
			</tr> 
			
			</table>
			</td></tr>
		
		</c:forEach>
		
	</c:if>	
	
	<c:if test="${fn:length(labResult.cultures) > 0}">
	
		<c:forEach var="culture" items="${labResult.cultures}" varStatus="i">
		
			<tr>
				<td>
					<br>
					<b class="boxHeader" style="margin:0px; width:100%"> <spring:message code="labmodule.culture"/> ( ${i.count} )
					</b>
				</td>
			</tr>
			
			<tr><td>

			<table style="font-size: 13px">
			
			<tr>
				<td>
				<input hidden type="text" name="labResultId" value="${labResult.id}">
						<input hidden type="text" name="id" value="${culture.id}">
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.testDate"/>:</font>
					&nbsp;
					<openmrs:formatDate date="${culture.resultDate}" format="${_dateFormatDisplay}"/>
				</td>
			</tr>
							
			<tr>
				<td>
					<font style="font-size:13px; font-weight:bold"><spring:message code="labmodule.labEntry.culture.result"/>:</font>
					&nbsp;
					${culture.result.displayString}
				</td>
			</tr> 
			
			</table>
			</td></tr>
		
		</c:forEach>
		
	</c:if>	
	
	</table>
	
	</div>
	
</div>

<!-- END OF LEFT-HAND COLUMN -->


<!-- Right-HAND COLUMN START -->
<div id="rightColumn" style=" float: right; padding:0px 4px 4px 4px; width:28%;">
 
	 <b class="boxHeader">
		&nbsp;
		<spring:message code="mdrtb.specimens" text="Specimens"/>
	</b> 

	<div class="box">
		<div id="specimenList">
		
		<c:choose>
			<c:when test="${fn:length(labResults) > 0}">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
					 
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="mdrtb.dateCollected" text="Date Collected"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold;"><nobr><u><spring:message code="labmodule.sampleid" text="Sample ID"/></u></nobr></td>
						<td class="tableCell" style="font-weight:bold"><nobr><u><spring:message code="mdrtb.tests" text="Tests"/></u></nobr></td>
					
					</tr>
				
					<c:forEach var="specimenListItem" items="${labResults}" varStatus="i">
						<tr 
							<c:if test="${i.count % 2 == 0 }">class="evenRow"</c:if>
							<c:if test="${i.count % 2 != 0 }">class="oddRow"</c:if>
							<c:if test="${specimenListItem.id == labResult.id}"> style="background-color : gold"</c:if>>
							
							<td class="tableCell"><nobr><a href="labResult.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}"><openmrs:formatDate date="${specimenListItem.dateCollected}" format="${_dateFormatDisplay}"/></a></nobr></td>
							<td class="tableCell"><nobr><a href="labResult.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">${specimenListItem.labNumber}</a></nobr></td>
							
							<td class="tableCell"><nobr><a href="labResult.form?patientId=${specimenListItem.patient.id}&labResultId=${specimenListItem.id}">
							
							<c:if test="${fn:length(specimenListItem.microscopies) > 0}">
								-<spring:message code="labmodule.labEntry.bacterioscopy"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.xperts) > 0}">
								-<spring:message code="labmodule.labEntry.xpert"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS) > 0}">
								-<spring:message code="labmodule.labEntry.hainSplit"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.HAINS2) > 0}">
								-<spring:message code="labmodule.labEntry.hain2Split"/> <br>
							</c:if>
							
							<c:if test="${fn:length(specimenListItem.cultures) > 0}">
								-<spring:message code="labmodule.culture"/> <br>
							</c:if>
							
							</a></nobr></td>
							
							
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
	
	<br> <br>


	<b class="boxHeader">
		&nbsp;
		<spring:message code="labmodule.search.status" text="Status"/>
	</b>
	<div class="box">
		<div style="padding:10px">
			<spring:message code="labmodule.search.status" text="Status"/>:
		    <b>${labResult.status}</b>
		    <br><br>
		    <c:if test="${labResult.status == 'Suspect'}">	
		    	<button type="button" onclick="location.href = '${pageContext.request.contextPath}/admin/patients/newPatient.form?patientId=${patientId}'"><spring:message code="labmodule.search.EnrollAsConfirm" text="Enroll as confirm"/></button>
		    </c:if>
		</div>
	</div>
	
</div>

<%@ include file="/WEB-INF/view/module/labmodule/dotsFooter.jsp"%>

<script type='text/javascript'>

</script>
