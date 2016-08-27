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
    translate(0px, 60px)
    /* 45 is really 360 - 45 */
    rotate(270deg);
  width: 30px;
  align: centre;
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
</style>
</head>
<body>
<table class="resultsTable">
   <tr>
     <th class="rotate" rowspan="4"><div><span>Registration Number</span></div></th>
     <th class="rotate" rowspan="4"><div><span>Date of Registration</span></div></th>
     <th rowspan="4">Full Name</th>
     <th class="rotate" rowspan="4"><div><span>Gender (M/F)</span></div></th>
     <th class="normal" rowspan="2">Age</th>
     <th rowspan="4">Address</th>
     <th class="normal" rowspan="2">MF for<br/>IP</th>
	 <th class="normal" rowspan="2">Treatment<br/>Regimen</th>
	 <th class="rotate" rowspan="4"><div><span>TB Localization (P/EP)</span></div></th>
	 <th class="reggroup" colspan="8" >Registration Group</th>
	 <th class="rotate" rowspan="4"><div><span>Transferred from</span></div></th>
	 <th class="reggroup" colspan="3" >TB/HIV activities</th>
	 <th class="reggroup" colspan="9" >Diagnostic Test Results</th>
	 <th class="reggroup" colspan="14" rowspan="2">DST (S/R))</th>
	 <th class="rotate" rowspan="4"><div><span>Drug resistance</br>(RR/PDR/MDR/XDR/no/unknown)</span></div></th>
	 <th class="reggroup" colspan="6" >Monitoring of smear microscopy</th>
	 <th class="reggroup" colspan="6" >Treatment Outcome and Date</th>
	 <th class="rotate" rowspan="4"><div><span>Diagnosis Canceled</span></div></th>
	 <th class="rotate" rowspan="4"><div><span>Remarks</span></div></th>
  </tr>
   <tr>
   	 
   	  <th class="subrotate" rowspan="3"><div><span>New Case</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>Relapse After Regimen 1</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>Relapse After Regimen 2</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>After Regimen 1 Default</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>After Regimen 2 Default</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>After Regimen 1 Failure</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>After Regimen 2 Failure</span></div></th>
   	 <th class="subrotate" rowspan="3"><div><span>Other</span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span>HIV test<br/>(neg/pos/unknown)</span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span>ART test  (yes/no)</span></div></th>
   	 <th class="subrotate" rowspan="2"><div><span>CP  (yes/no)</span></div></th>
   	 <th class="normal" colspan="2">Microscopy</th>
   	 <th class="normal" colspan="2">GeneXpert</th>
   	 <th class="normal" colspan="3">HAIN</th>
   	 <th class="normal" colspan="2">Culture</th>
   	 <th class="normal" colspan="2">2(3)/3(4)<br/>month</th>
   	 <th class="normal" colspan="2">5 month</th>
   	 <th class="normal" colspan="2">6(8) month</th>
   	  <th class="subrotate" rowspan="3"><div><span>Cured</span></div></th>
   	   <th class="subrotate" rowspan="3"><div><span>Treatment Completed</span></div></th>
   	   <th class="subrotate" rowspan="3"><div><span>Treatment Failure</span></div></th>
   	 <th class="normal" colspan="2">Died</th>
   	  <th class="subrotate" rowspan="3"><div><span>Lost to follow-up</span></div></th>
   	 
   </tr>
   <tr>
       <th class="normal" rowspan="2">Date of<br/>Birth</th>
   	 <th class="normal" rowspan="2">MF for<br/>FP</th>
   	 <th class="normal" rowspan="2">Date of<br/>Treatment<br/>Start</th>
   	
       <th class="normal" colspan="2">Result<br/>(1+, 2+, 3+, AFB)</th>
   	   <th class="normal" colspan="2">Result<br/>(+/- ; R - R/S/U)</th>
   	   <th class="normal">Date</th>
   	   <th class="normal" colspan="2">Result (+/-)</th>
   	   <th class="normal" colspan="2">Result (+/-)</th>
   	   <th class="normal">H</th>
   	 <th class="normal">R</th>
   	 <th class="normal">E</th>
   	 <th class="normal">S</th>
   	 <th class="normal">Z</th>
   	 <th class="normal">Km</th>
   	 <th class="normal">Am</th>
   	 <th class="normal">Cm</th>
   	 <th class="normal">FQ</th>
   	 <th class="normal">Pto/<br/>Eto</th>
   	 <th class="normal">Cs</th>
   	 <th class="normal">Bdq</th>
   	 <th class="normal">Dlm</th>
   	 <th class="normal">Other</th>
   	 <th class="normal" colspan="2">Result</th>
   	   <th class="normal" colspan="2">Result</th>
   	   <th class="normal" colspan="2">Result</th>
   	
   	   <th class="subrotate" rowspan="2"><div><span>of TB</span></div></th>
   	   <th class="subrotate" rowspan="2"><div><span>of other reasons</span></div></th>
   	   
   	    
   	 
   	   
   	   
   </tr>
   <tr>
        <th class="normal">Date</th>
       <th class="normal">Date of<br/>Start</th>
       <th class="normal">Date of<br/>Start</th>
       
        <th>Date</th>
   		<th>Test No.</th>
   		<th>Date</th>
   		<th>Test No.</th>
   		<th>Test No</th>
   		<th>H (R/S/U)</th>
   		<th>R (R/S/U)</th>
   		<th>Date</th>
   		<th>Test No.</th>
   		   
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
   	   <th class="normal">Date</th>
   	   
   		<th>Date</th>
   		<th>Test No.</th>
   		<th>Date</th>
   		<th>Test No.</th>
   		<th>Date</th>
   		<th>Test No.</th>
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
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">&nbsp;</td>
  <td rowspan="2">${row.drugResistance }</td>
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
 </tr>
</c:forEach>

</table>

<span>${num}</span>
<%@ include file="../dotsFooter.jsp"%>
