<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>
<script src='<%= request.getContextPath() %>/dwr/interface/LabFindPatient.js'></script>
<openmrs:globalProperty key="use_patient_attribute.healthCenter" var="useHealthCenter"/>
<openmrs:globalProperty key="use_patient_attribute.mothersName" var="useMothersName"/>
<openmrs:globalProperty key="use_patient_attribute.tribe" var="useTribe"/>
<openmrs:globalProperty key="mdrtb.findPatientNumResults" var="numResults" defaultValue="5"/>

<script type="text/javascript">

var classTmp2 = "";
var from2 = 0;
var jumps2 = ${numResults}; //this many patients at a time
var to2 = jumps2-1;
var retSize2 = 0;
var headerShown2 = 0;
var savedRet2 = new Array();
var mappedRet2 = new Array();
var showLoading2 = 0;

var $j = jQuery.noConflict();
$j(document).ready(function(){

	$j("#foo").click(function() {
		
			LabFindPatient.findLatestEncounterByUserId(${model.authenticatedUser.userId},'3', false, function(ret){
				from2 = 0; 
				to2 = jumps2-1; 
				if (ret.length <= to2)
					to2 = ret.length -1;
				savedRet2 = ret; 
				retSize2 = ret.length;
				drawTable2(savedRet2);
			});
		
	});
	$j('#foo').click();
	
});

function addRowEventsFindPatient2(){
	var tbody = document.getElementById('resTableBody_enc');
	var trs = tbody.getElementsByTagName("tr");
	for(i = 0; i < trs.length; i++){
		if (trs[i].firstChild.firstChild.innerHTML != ""){
			trs[i].onclick = function() {
				selectPatient2(this.firstChild.firstChild.innerHTML);
			}
			trs[i].onmouseover = function() {
				 mouseOver1(this);
			}
			trs[i].onmouseout = function() {
				$j('table.resTable_enc tbody tr:odd').addClass('oddRow');
					$j('table.resTable_enc tbody tr:even').addClass('evenRow');
				 mouseOut1(this);
				 $j('table.resTable_enc tbody tr:odd').addClass('oddRow');
					$j('table.resTable_enc tbody tr:even').addClass('evenRow');
			}
		}	
	}
}

function selectPatient2(input){
	<c:choose>
		<c:when test="${not empty model.callback}">
			${model.callback}(mappedRet2[input]);
   		</c:when>
   		<c:otherwise>
   			var array = input.split('---');
   			window.location='${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form?patientId=' + array[0] + '&labResultId=' + array[1] ;
   		</c:otherwise>
   	</c:choose>
}
function mouseOver1(input){
	classTmp2 = this.className;
	input.className="rowMouseOver";
	refresh1(this);	
}
function mouseOut1(input){
	input.className = classTmp2;
	refresh1(this);
}
function refresh1(input){
	if (input.className) input.className = input.className;
}
function formatDate(ymd) {
	if (ymd == null || ymd == '')
		return '';
	<c:choose>
		<c:when test="${model.locale == 'fr' || model.locale == 'en_GB'}">
			return ymd.substring(8, 10) + '/' + ymd.substring(5, 7) + '/' + ymd.substring(0, 4);
		</c:when>
		<c:otherwise>
			return ymd.substring(5, 7) + '/' + ymd.substring(8, 10) + '/' + ymd.substring(0, 4);
		</c:otherwise>
	</c:choose>
}

function getDateString2(d) {
	var str = '';
	if (d != null) {
		
		// get the month, day, year values
		var month = "";
		var day = "";
		var year = "";
		var date = d.getDate();
		
		if (date < 10)
			day += "0";
		day += date;
		var m = d.getMonth() + 1;
		if (m < 10)
			month += "0";
		month += m;
		if (d.getYear() < 1900)
			year = (d.getYear() + 1900);
		else
			year = d.getYear();
	
		var datePattern = '<openmrs:datePattern />';
		var sep = datePattern.substr(2,1);
		var datePatternStart = datePattern.substr(0,1).toLowerCase();
		
		if (datePatternStart == 'm') /* M-D-Y */
			str = month + sep + day + sep + year
		else if (datePatternStart == 'y') /* Y-M-D */
			str = year + sep + month + sep + day
		else /* (datePatternStart == 'd') D-M-Y */
			str = day + sep + month + sep + year
		
	}
	return str;
}



function drawTable2(ret){
	DWRUtil.removeAllRows('resTableBody_enc');
	mappedRet2 = new Array();
	var count = from2+1;
	var cellFuncs = [
		// the cell counter
		function(patient) {
			if (patient.patientId != null && patient.patientId != "NaN"){
			var patientIdDiv='<div style="display:none" id="patientIdDiv">'+patient.patientId+'---'+patient.encounterId+'</div>';
			mappedRet[patient.patientId]=patient;
			return patientIdDiv + count++ + ".  &nbsp;";
			}
		},
		//sample id
		function(patient) { 
			if (patient.patientId != null && patient.patientId != "NaN")
			return  patient.labNumber;
		
  		},
		//Date Collected 
		function(patient) {
			if (patient.patientId != null && patient.patientId != "NaN"){
				if (patient.birthdate != "" && patient.birthdate != "Unknown")
				return getDateString2(patient.recievedDate);
			} 	
		},
		// Tests
		function(patient) { 
			if (patient.patientId != null && patient.patientId != "NaN"){

				var testType = "";

				if(patient.microscopyTests > 0)
					testType = "-" +  "<spring:message code='labmodule.labEntry.bacterioscopy'/>";
					
				if(patient.xpertTests > 0){
					if(testType != "")
						testType =  testType + "<br>";
						
					testType = testType + "-" + "<spring:message code='labmodule.labEntry.xpert'/>";
				}	
				
				if(patient.hainTests > 0){
					if(testType != "")
						testType =  testType + "<br>";
						
					testType = testType + "-" + "<spring:message code='labmodule.labEntry.hainSplit'/>";
				}
				
				if(patient.hain2Tests > 0){
					if(testType != "")
						testType =  testType + "<br>";
					
					testType = testType + "-" + "<spring:message code='labmodule.labEntry.hain2Split'/>";
				}
				
				if(patient.cultureTests > 0){
					if(testType != "")
						testType =  testType + "<br>";
					
					testType = testType + "-" + "<spring:message code='labmodule.culture'/>";	
				}					
						
						
				return  testType;
			}
  		},
	  	// Suspect's Nam
		function(patient) { 
				if (patient.patientId != null && patient.patientId != "NaN")
				return  patient.givenName + " " + patient.middleName + " " + patient.familyName;
	  		}
		
		];
	var cellFuncsHeader = [
	function() {return " "},
		function() {return "<b><spring:message code='labmodule.sampleid'/></b>"},
		function() {return "<b><spring:message code='mdrtb.dateCollected'/></b>"},
		function() {return "<b><spring:message code='mdrtb.tests'/></b>"},
		function() {return "<b><spring:message code='labmodule.suspectName'/></b>"}
	];
	
	var cellFuncsNextN = [function(){return ""}];	
	
	var formatCountCell = function(options) { 
		var td = document.createElement("td"); 
		td.setAttribute('colSpan','10');
		td.setAttribute('align', 'center');
		return td; 
	}
	
		if (ret[from2]){
		if (ret.length != retSize2){
				DWRUtil.removeAllRows('resTableHeader_enc');
				if (headerShown2 == 1)
				headerShown2--;
				retSize2 = ret.length;
			}
		}
		if (headerShown2 == 0){
			DWRUtil.addRows('resTableHeader_enc',[""], cellFuncsNextN, {cellCreator:formatCountCell,escapeHtml:false});
			DWRUtil.addRows('resTableHeader_enc',[""], cellFuncsHeader, {escapeHtml:false});
			headerShown2 ++;
		}
		if (!ret[from2]){
				/* DWRUtil.removeAllRows('resTableHeader_id');
				headerShown--;
				if (ret.length == 0){
		 		//no records found:
		 		var cellFucsNoRecords = [function(){
			 		
	 			var searchText = '';
				if(document.getElementById("id").checked ==  true){
				 	var sb = document.getElementById("searchBox_name");
				 	searchText = sb.value;
				}
				
		 		return "<div><Br>&nbsp;&nbsp;&nbsp;<spring:message code="labresult.nopatientsfound"/> <i>"+searchText+"</i>.</div>";
		 		}];
		 		DWRUtil.addRows('resTableBody_id', ["nopatient"], cellFucsNoRecords,  {escapeHtml:false} );
		 		
		 		}  */
		 		
		}  else {
		DWRUtil.addRows('resTableBody_enc', getPartOfSavedRet2(from2, to2, ret), cellFuncs,  {escapeHtml:false} );
		 $j('table.resTable_enc tbody tr:odd').addClass('oddRow');
		 $j('table.resTable_enc tbody tr:even').addClass('evenRow');
		 $j('table.resTable_enc tbody tr').attr('onmouseover','javascript:mouseOver1(this);refresh1(this);');
		 $j('table.resTable_enc tbody tr').attr('onmouseout','javascript:mouseOut1(this); refresh1(this);');
		 addRowEventsFindPatient2();
		 fixHeader2();
		 $j('#results_box').css('display','');		
		} 						
}

function fixHeader2(){
		var thead = document.getElementById('resTableHeader_enc');
	var trs = thead.getElementsByTagName("tr");
	addClass(trs[0],"infoRow");
	addClass(trs[1],"oddRow");
	if (trs[0]){
		var toTmp = to2;
		if (toTmp > savedRet2.length)
		toTmp = savedRet2.length -1;
		toTmp++;
		
		td = trs[0].getElementsByTagName('td')[0];
		var fromTmp = from2 + 1;
		var nextN = "&nbsp;&nbsp;&nbsp;" + fromTmp + " <spring:message code="mdrtb.to"/> " + toTmp +" <spring:message code="mdrtb.of"/> " + savedRet2.length;
		
								var lastP = jumps2;
								if (savedRet2.length > toTmp){
									if (savedRet2.length - toTmp < lastP)
									lastP = savedRet2.length - toTmp;
								}
								var pipeTest = 0;
								if (savedRet2.length > jumps2 && toTmp != savedRet2.length){
								nextN = "<a href='javascript:next2()'><spring:message code="mdrtb.next"/> " + lastP + "</a>&nbsp;&nbsp;"+nextN;
								pipeTest = 1;
								}
								if (fromTmp > 1){
								if (pipeTest == 1)
								nextN = "<a href='javascript:previous2()'><spring:message code="mdrtb.previous"/> "+jumps2+"</a>&nbsp;|&nbsp;" + nextN;
								else
								nextN = "<a href='javascript:previous2()'><spring:message code="mdrtb.previous"/> "+jumps2+"</a>&nbsp;&nbsp;" + nextN;
								}
		td.innerHTML = nextN;
	}
	}			
function next2(){
	from2 = from2 + jumps2;
	if (to2 + jumps2 > savedRet2.length - 1)
		to2 = savedRet2.length - 1;
	else	
	to2 = to2 + jumps2;
	drawTable2(savedRet2);
}

function previous2(){
	to2 = from2 - 2;
	from2 = from2 - jumps2;
	if (to2 - from2 != jumps2)
		from2 = to2 - jumps2+1;
	if (to2 < jumps2)
		to2 = jumps2 - 1;
	if (from2 < 0)
		from2 = 0;			
	drawTable2(savedRet2);
}
function getPartOfSavedRet2(from2, to2, ret){
	var retTmp = new Array();
	if (ret[from2]){
		toTmp = to2;
		if (toTmp > ret.length)
			toTmp = ret.length -1;
		var c = 0;
		for (i = from2; i <= toTmp; i++){
			retTmp[c] = ret[i];
			c++;
		}
	}
	return retTmp;
}
	

function useMdrtbLoadingMessage(message) {
var loadingMessage;
if (message) loadingMessage = message;
else loadingMessage = "Loading...";

DWREngine.setPreHook(function() {
	showLoading2 = 1;
	setTimeout('function sitStill(){return "";}', 5000);
var disabledZone = $('disabledZone');
if (showLoading2 == 1){
if (!disabledZone) {
  disabledZone = document.createElement('div');
  disabledZone.setAttribute('id', 'disabledZone');
  document.body.appendChild(disabledZone);
  var messageZone = document.createElement('div');
  messageZone.setAttribute('id', 'messageZone');
  disabledZone.appendChild(messageZone);
  var text = document.createTextNode(loadingMessage);
  messageZone.appendChild(text);
}
else {
  $('messageZone').innerHTML = loadingMessage;
  disabledZone.style.visibility = 'visible';
}
}
});

DWREngine.setPostHook(function() {
$('disabledZone').style.visibility = 'hidden';
showLoading2 = 0;
});
}

</script>


<c:if test="${model.authenticatedUser != null}">

		<div id="lastTestResult">
			<a hidden id="foo" href="#">Test</a>
			<b class="boxHeader"><spring:message code="labmodule.lastEncounters" /> ${model.authenticatedUser.personName}</b>
			<div class="box" style="padding: 15px 15px 15px 15px;">
				
				<table id="resTable_enc" class="resTable" cellpadding="2" cellspacing="0" style="border-collapse: collapse">
					<thead id="resTableHeader_enc" class="resTableHeader"/>	
					<tbody class="resTableBody" id="resTableBody_enc" style="vertical-align: center"/>
					<tfoot id="resTableFooter_enc" class="resTableFooter"/>	
				</table>
				
		
			</div>
		</div> 
</c:if>

<script type="text/javascript">

	 
</script>