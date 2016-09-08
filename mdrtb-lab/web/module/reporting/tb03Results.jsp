<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>


<style>
th.rotate {
  /* Something you can count on */
  height: 350px;
  white-space: nowrap;
  valign: middle;
  
  
}

th.rotate > div {
  transform: 
    /* Magic Numbers */
    translate(0px, 100px)
    /* 45 is really 360 - 45 */
    rotate(270deg);
  width: 30px;
  align: centre;
  
}

td.rotate {
  /* Something you can count on */
  height: 150px;
  white-space: nowrap;
  valign: middle;
  
  
}

td.rotate > div {
  transform: 
    /* Magic Numbers */
    translate(0px, 100px)
    /* 45 is really 360 - 45 */
    rotate(270deg);
  width: 30px;
  align: centre;
  
}

th.subrotate {
  /* Something you can count on */
  
  white-space: nowrap;
  valign: middle;
  
}

th.subrotate > div {
  transform: 
    /* Magic Numbers */
    translate(0px, 65px)
    /* 45 is really 360 - 45 */
    rotate(270deg);
  width: 50px;
  align: centre;
}

th.dst {
  
  valign: middle;
 
  
}

th.dst > div {
  
  
  width: 30px;
  
}

th.widedst {
  
  valign: middle;
 
  
}

th.widedst > div {
  
  
  width: 55px;
  
}

th.normal {
  /* Something you can count on */
  
  white-space: nowrap;
  valign: middle;
  
}

th.reggroup {
  /* Something you can count on */
  height: 50px;
  white-space: nowrap;
  valign: middle;
  
}


table.resultsTable {
		border-collapse: collapse;
}

table.resultsTable td, table.resultsTable th {
		border-top: 1px black solid;
		border-bottom: 1px black solid;
		border-right: 1px black solid;
		border-left: 1px black solid;
	}
</style>
<meta http-equiv="content-type" content="text/plain; charset=UTF-8"/>
<script type="text/javascript">
var tableToExcel = (function() {
  var uri = 'data:application/vnd.ms-excel;base64,'
    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>TB03</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/></head><body><table>{table}</table></body></html>'
    , base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) }
    , format = function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }) }
  return function(table, name) {
    if (!table.nodeType) table = document.getElementById(table)
    var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}
    window.location.href = uri + base64(format(template, ctx))
  }
})()
</script>
</head>
<body>
<input type="button" onclick="tableToExcel('tb03', 'TB03')" value="Export to Excel" />
<table border=".5pt" id="tb03">
  
   <tr>
     <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.registrationNumber"/></span></div></th>
     <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.dateOfRegistration"/></span></div></th>
     <th rowspan="4"><spring:message code="dotsreports.tb03.fullName"/></th>
     <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.gender"/></span></div></th>
     <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.age"/></th>
     <th rowspan="4"><spring:message code="dotsreports.tb03.address"/></th>
     <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.mfForIP"/></th>
	 <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.treatmentRegimen"/></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.tbLocalization"/></span></div></th>
	 <th class="reggroup" colspan="8" ><spring:message code="dotsreports.tb03.registrationGroup"/></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.transferFrom"/></span></div></th>
	 <th class="reggroup" colspan="3" ><spring:message code="dotsreports.tb03.tbHivActivities"/></th>
	 <th class="reggroup" colspan="9" ><spring:message code="dotsreports.tb03.diagnosticTestResults"/></th>
	 <th class="normal" rowspan="2" rowspan="2"><spring:message code="dotsreports.tb03.dstSampleCollectionDate"/></th>
	 <th class="reggroup" colspan="17" rowspan="1"><spring:message code="dotsreports.tb03.dst"/></th>
	 
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.drugResistance"/></span></div></th>
	 <th class="reggroup" colspan="6" ><spring:message code="dotsreports.tb03.smearMonitoring"/></th>
	 <th class="reggroup" colspan="6" ><spring:message code="dotsreports.tb03.treatmentOutcome"/></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.canceled"/></span></div></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.startedRegimen2"/></span></div></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.transferOut"/></span></div></th>
	 <th class="rotate" rowspan="4"><div><span><spring:message code="dotsreports.tb03.notes"/></span></div></th>
  </tr>
   <tr>
   	 
   	  <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.new"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.relapseAfter"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.relapseAfter"/></span></div></th>
   	
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.defaultAfter"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.defaultAfter"/> </span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.failureAfter"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.failureAfter"/></span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.other"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.hivTest"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.artTest"/></span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.cpTest"/></span></div></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.microscopy"/></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.genexpert"/></th>
   	 <th class="normal" colspan="3"><spring:message code="dotsreports.tb03.hain"/></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.culture"/></th>
   	 <th class="dst" rowspan="3"><div>R</div></th>
   	 <th class="dst" rowspan="3"><div>H</div></th>
   	 <th class="dst" rowspan="3"><div>E</div></th>
   	 <th class="dst" rowspan="3"><div>S</div></th>
   	 <th class="dst" rowspan="3"><div>Z</div></th>
   	 <th class="dst" rowspan="3"><div>Km</div></th>
   	 <th class="dst" rowspan="3"><div>Am</div></th>
   	 <th class="dst" rowspan="3"><div>Cm</div></th>
   	 <th class="widedst" rowspan="3"><div>Ofx/Lfx</div></th>
   	 <th class="dst" rowspan="3"><div>Mfx</div></th>
   	 <th class="dst" rowspan="3"><div>Pto</div></th>
   	 <th class="dst" rowspan="3"><div>Cs</div></th>
   	 <th class="dst" rowspan="3"><div>PAS</div></th>
   	 <th class="dst" rowspan="3"><div>Lzd</div></th>
   	 <th class="dst" rowspan="3"><div>Cfz</div></th>
   	 <th class="dst" rowspan="3"><div>Bdq</div></th>
   	 <th class="dst" rowspan="3"><div>Dlm</div></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.m234"/><br style="mso-data-placement:same-cell;" /><spring:message code="dotsreports.tb03.month"/></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.five"/> <spring:message code="dotsreports.tb03.month"/></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.m68"/> <spring:message code="dotsreports.tb03.month"/></th>
   	  <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.cured"/></span></div></th>
   	   <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.txCompleted"/></span></div></th>
   	   <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.failure"/></span></div></th>
   	 <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.died"/></th>
   	  <th class="subrotate" rowspan="3"><div><span><spring:message code="dotsreports.tb03.ltfu"/></span></div></th>
   	 
   </tr>
   <tr>
       <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.dateOfBirth"/></th>
   	 <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.mfForFP"/></th>
   	 <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.treatmentStartDate"/></th>
   	 <!-- <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th>
   	 <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th>
   	 <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th> -->
   	
       <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.microscopyResult"/></th>
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.xpertResult"/></th>
   	   <th class="normal"><spring:message code="dotsreports.tb03.date"/></th>
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.hainCultureResult"/></th>
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.hainCultureResult"/></th>
   	   <th class="normal" rowspan="2"><spring:message code="dotsreports.tb03.dstResultDate"/></th>
   	    
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.result"/></th>
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.result"/></th>
   	   <th class="normal" colspan="2"><spring:message code="dotsreports.tb03.result"/></th>
   	
   	   <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.ofTb"/></span></div></th>
   	   <th class="subrotate" rowspan="2"><div><span><spring:message code="dotsreports.tb03.ofOther"/></span></div></th>
   	   
   	    
   	 
   	   
   	   
   </tr>
   <tr>
        <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th>
   	 <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th>
   	 <th class="normal" rowspan="1">I</th>
   	 <th class="normal" rowspan="1">II</th>
        <th class="normal"><spring:message code="dotsreports.tb03.date"/></th>
       <th class="normal"><spring:message code="dotsreports.tb03.startDate"/></th>
       <th class="normal"><spring:message code="dotsreports.tb03.startDate"/></th>
       
        <th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		<th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		<th><spring:message code="dotsreports.tb03.hResult"/></th>
   		<th><spring:message code="dotsreports.tb03.rResult"/></th>
   		<th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		   
   	  <!--  <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	   <th class="normal">Date</th>
   	    -->
   		<th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		<th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   		<th><spring:message code="dotsreports.tb03.date"/></th>
   		<th><spring:message code="dotsreports.tb03.testNumber"/></th>
   </tr>
<c:forEach var="row" items="${patientSet}">
 <tr>
 <td rowspan="2"><div><span>${row.identifier}</span></div></td>
 <td rowspan="2">${row.tb03RegistrationDate}</td>
 <td rowspan="2">${row.patient.personName.familyName}, ${row.patient.personName.givenName}</td>
 <td rowspan="2" align="center">${row.patient.gender}</td>
 <td align="center">${row.ageAtTB03Registration }</td>
 <td rowspan="2">${row.patient.personAddress.stateProvince},<br/>${row.patient.personAddress.countyDistrict},${row.patient.personAddress.address1}</td> 
 <td>${row.intensivePhaseFacility }</td>
 <td>${row.treatmentRegimen }</td>
 <td align="center" rowspan="2">${row.siteOfDisease}</td>
 <c:forEach begin="0" end="8" varStatus="loop">
    <td align="center" rowspan="2">
      <c:if test="${row.regGroup == loop.index }">&#10004;</c:if>
    </td>
</c:forEach>
 <td>${row.hivTestResult }</td>
 <td rowspan="2">${row.artStartDate }</td>
 <td rowspan="2">${row.cpStartDate }</td>
 <td colspan="2" align="center">${row.diagnosticSmearResult }</td>
 <td colspan="2" align="center">${row.xpertMTBResult } ${row.xpertRIFResult } </td>
 <td align="center">${row.hainTestDate } </td>
 <td colspan="2" align="center">${row.hainMTBResult }</td>
  <td colspan="2" align="center">${row.cultureResult }</td>
  <td>${ row.dstCollectionDate}</td>
  <td rowspan="2" align="center">${ row.dstR}</td>
  <td rowspan="2" align="center">${ row.dstH }</td>
 <td rowspan="2" align="center">${ row.dstE }</td>
  <td rowspan="2" align="center">${ row.dstS }</td>
  <td rowspan="2" align="center">${ row.dstZ }</td>
  <td rowspan="2" align="center">${ row.dstKm }</td>
 <td rowspan="2" align="center">${ row.dstAm }</td>
 <td rowspan="2" align="center">${ row.dstCm }</td>
  <td rowspan="2" align="center">${ row.dstOfx }</td>
  <td rowspan="2" align="center">${ row.dstMfx }</td>
 <td rowspan="2" align="center">${ row.dstPto }</td>
 <td rowspan="2" align="center">${ row.dstCs }</td>
  <td rowspan="2" align="center">${ row.dstPAS }</td>
  <td rowspan="2" align="center">${ row.dstLzd }</td>
 <td rowspan="2" align="center">${ row.dstCfz }</td>
 <td rowspan="2" align="center">${ row.dstBdq}</td>
 <td rowspan="2"align="center">${ row.dstDlm }</td>
  
  <td rowspan="2">${row.drugResistance }</td>
  <c:choose>
 	 <c:when test="${row.reg1New}">
  		<td colspan="2" align="center">${row.month2SmearResult} / ${row.month3SmearResult}</td>
  		<td colspan="2" align="center">${row.month5SmearResult}</td>
  		<td colspan="2" align="center">${row.month6SmearResult}</td>
  	</c:when>
  	<c:when test="${row.reg1Rtx}">
  		<td colspan="2" align="center">${row.month3SmearResult} / ${row.month4SmearResult}</td>
  		<td colspan="2" align="center">${row.month5SmearResult}</td>
  		<td colspan="2" align="center">${row.month8SmearResult}</td>
  	</c:when>
  	<c:otherwise>
  	<td colspan="2">&nbsp;</td>
  	<td colspan="2">&nbsp;</td>
  	<td colspan="2">&nbsp;</td>
  	</c:otherwise>
  </c:choose>
  <c:forEach begin="0" end="8" varStatus="loop">
    <td align="center" rowspan="2">
      <c:if test="${row.tb03TreatmentOutcome == loop.index }">&#10004;</c:if>
    </td>
</c:forEach>
  <td rowspan="2">${row.notes }</td>
 </tr>
 <tr>
 	<td>${row.dateOfBirth}</td>
 	<td>${row.continuationPhaseFacility }</td>
 	<td>${row.tb03TreatmentStartDate }</td>
 	<td>${row.hivTestDate }</td>
 	<td>${row.diagnosticSmearDate }</td>
 	<td>${row.diagnosticSmearTestNumber }</td>
 	<td>${row.xpertTestDate }</td>
 	<td>${row.xpertTestNumber }</td>
 	<td>${row.hainTestNumber }</td>
 	<td align="center">${row.hainINHResult }</td>
 	<td align="center">${row.hainRIFResult }</td>
 	<td>${row.cultureTestDate }</td>
 	<td>${row.cultureTestNumber }</td>
 	<td>${ row.dstResultDate}</td>
 	 <c:choose>
 	 <c:when test="${row.reg1New}">
  		<td align="center">${row.month2SmearDate} / ${row.month3SmearDate}</td>
  		<td align="center">${row.month2TestNumber} / ${row.month3TestNumber}</td>
  		<td>${row.month5SmearDate}</td>
  		<td>${row.month5TestNumber}</td>
  		<td>${row.month6SmearDate}</td>
  		<td>${row.month6TestNumber}</td>
  	</c:when>
  	<c:when test="${row.reg1Rtx}">
  		<td align="center">${row.month3SmearDate} / ${row.month4SmearDate}</td>
  		<td align="center">${row.month3TestNumber} / ${row.month4TestNumber}</td>
  		<td>${row.month5SmearDate}</td>
  		<td>${row.month5TestNumber}</td>
  		<td>${row.month8SmearDate}</td>
  		<td>${row.month8TestNumber}</td>
  	</c:when>
  	<c:otherwise>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  	<td>&nbsp;</td>
  	</c:otherwise>
  </c:choose>
 </tr>
</c:forEach>

</table>

<%@ include file="../dotsFooter.jsp"%>
