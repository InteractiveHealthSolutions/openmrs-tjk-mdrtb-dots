<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>
<script src='<%= request.getContextPath() %>/dwr/interface/LabFindPatient.js'></script>
<openmrs:globalProperty key="use_patient_attribute.healthCenter" var="useHealthCenter"/>
<openmrs:globalProperty key="use_patient_attribute.mothersName" var="useMothersName"/>
<openmrs:globalProperty key="use_patient_attribute.tribe" var="useTribe"/>
<openmrs:globalProperty key="mdrtb.findPatientNumResults" var="numResults" defaultValue="5"/>

<script type="text/javascript">

	var classTmp1 = "";
	var from1 = 0;
	var jumps1 = ${numResults}; //this many patients at a time
	var to1 = jumps1-1;
	var retSize1 = 0;
	var headerShown1 = 0;
	var savedRet1 = new Array();
	var mappedRet1 = new Array();
	var showLoading1 = 0;

	var $j = jQuery.noConflict();	
	$j(document).ready(function(){
		$j('#results_box').css('display','none');
		
		$j("#findButton").click(function(){

			 var from = $j(dateFrom).val();
			 var to = $j(dateTo).val();

			 if(from == '' || to == ''){

				 alert("<spring:message code="labresult.selectDate"/>");
				 return false;
			 }
				
			LabFindPatient.findTestThroughDates($j('#dateFrom').val(), $j('#dateTo').val(), false, function(ret){
				from1 = 0; 
				to1 = jumps1-1; 
				if (ret.length <= to1)
					to1 = ret.length -1;
				savedRet1 = ret; 
				retSize1 = ret.length;
				drawTable1(savedRet1);
			});
				
		});		


		$j('#searchBox_name').keyup(function(){
				if ($j('#searchBox_name').val().length >= 2){
					if ($j('#includeRetired:checked').val() != null && $j('#includeRetired:checked').val() == 'on')
						includeRet = true;
					LabFindPatient.findTestsThroughId($j('#searchBox_name').val(), false, function(ret){
					from1 = 0; 
					to1 = jumps1-1; 
					if (ret.length <= to1)
						to1 = ret.length -1;
					savedRet1 = ret; 
					retSize1 = ret.length;
					drawTable1(savedRet1);});
				}
				else {
						$j('#results_box').hide();
				}
		});	

		$j('#searchBox_sname').keyup(function(){
				if ($j('#searchBox_sname').val().length > 2){
					if ($j('#includeRetired:checked').val() != null && $j('#includeRetired:checked').val() == 'on')
						includeRet = true;
					LabFindPatient.findTestsThroughSuspect('rabb', false, function(ret){
						from1 = 0; 
						to1 = jumps1-1; 
						if (ret.length <= to1)
							to1 = ret.length -1;
						savedRet1 = ret; 
						retSize1 = ret.length;
						drawTable1(savedRet1);});
				}
				else {
						$j('#results_box').hide();
				}
	});	
		
	});	


	/* var multiply = function(x,y) {

		LabFindPatient.findTestsThroughSuspect($j('#searchBox_sname').val(), false, function(ret){
			alert('Hello!')

		});
		
	     return (x * y);
	} */
	
	function addRowEventsFindPatient1(){
		var tbody = document.getElementById('resTableBody_id');
		var trs = tbody.getElementsByTagName("tr");
		for(i = 0; i < trs.length; i++){
			if (trs[i].firstChild.firstChild.innerHTML != ""){
				trs[i].onclick = function() {
					selectPatient1(this.firstChild.firstChild.innerHTML);
				}
				trs[i].onmouseover = function() {
					 mouseOver(this);
				}
				trs[i].onmouseout = function() {
					$j('table.resTable_id tbody tr:odd').addClass('oddRow');
  					$j('table.resTable_id tbody tr:even').addClass('evenRow');
					 mouseOut(this);
					 $j('table.resTable_id tbody tr:odd').addClass('oddRow');
  					$j('table.resTable_id tbody tr:even').addClass('evenRow');
				}
			}	
		}
	}
	
	function selectPatient1(input){
		<c:choose>
			<c:when test="${not empty model.callback}">
				${model.callback}(mappedRet1[input]);
				$j('#results_box').css('display','none');
	   			$j('#searchBox').val('');
	   		</c:when>
	   		<c:otherwise>
	   		var array = input.split('---');
	   		window.location='${pageContext.request.contextPath}/module/labmodule/lab/labResult.form?patientId=' + array[0] + '&labResultId=' + array[1] ;	
	   		</c:otherwise>
	   	</c:choose>
	}
	function mouseOver(input){
		classTmp1 = this.className;
		input.className="rowMouseOver";
		refresh(this);	
	}
	function mouseOut(input){
		input.className = classTmp1;
		refresh(this);
	}
	function refresh(input){
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

	function getDateString1(d) {
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


	
	function drawTable1(ret){
		DWRUtil.removeAllRows('resTableBody_id');
		mappedRet1 = new Array();
		var count = from1+1;
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
					return getDateString1(patient.recievedDate);
				} 	
			},
			// Tests
			function(patient) { 
				if (patient.patientId != null && patient.patientId != "NaN"){

					var testType = "";

					if(patient.microscopyTests > 0)
						testType = "-" + "<spring:message code='labmodule.labEntry.bacterioscopy'/>";
						
					if(patient.xpertTests > 0){
						if(testType != "")
							testType =  testType + "<br>";
							
						testType = testType + "-" + "<spring:message code='labmodule.labEntry.xpert'/>";
					}	
					
					if(patient.hainTests > 0){
						if(testType != "")
							testType =  testType + "<br>";
							
						testType = testType + "-" + "<spring:message code='labmodule.labEntry.hain'/>";
					}
					
					if(patient.hain2Tests > 0){
						if(testType != "")
							testType =  testType + "<br>";
						
						testType = testType + "-" + "<spring:message code='labmodule.labEntry.hain2'/>";
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
		  		},
		  	// age
		  	function(patient) {
				if (patient.patientId != null && patient.patientId != "NaN")
					return patient.age;
			},
			// gender
		  	function(patient) {
				if (patient.patientId != null && patient.patientId != "NaN")
					return patient.gender;
			},	
			// status
		  	function(patient) {
				if (patient.patientId != null && patient.patientId != "NaN")
					return patient.patientStatus;
			}	
			
			];
		var cellFuncsHeader = [
		function() {return " "},
			function() {return "<b><spring:message code='labmodule.sampleid'/></b>"},
			function() {return "<b><spring:message code='mdrtb.dateCollected'/></b>"},
			function() {return "<b><spring:message code='mdrtb.tests'/></b>"},
			function() {return "<b><spring:message code='labmodule.suspectName'/></b>"},
			function() {return "<b><spring:message code='mdrtb.age'/></b>"},
			function() {return "<b><spring:message code='mdrtb.gender'/></b>"},
			function() {return "<b><spring:message code='labmodule.search.status'/></b>"}
		];
		
		var cellFuncsNextN = [function(){return "t"}];	
		
		var formatCountCell = function(options) { 
			var td = document.createElement("td"); 
			td.setAttribute('colSpan','10');
			td.setAttribute('align', 'center');
			return td; 
		}
		
			if (ret[from1]){
			if (ret.length != retSize1){
					DWRUtil.removeAllRows('resTableHeader_id');
					if (headerShown1 == 1)
					headerShown1--;
					retSize1 = ret.length;
				}
			}
			if (headerShown1 == 0){
				DWRUtil.addRows('resTableHeader_id',[""], cellFuncsNextN, {cellCreator:formatCountCell,escapeHtml:false});
				DWRUtil.addRows('resTableHeader_id',[""], cellFuncsHeader, {escapeHtml:false});
				headerShown1 ++;
			}
			if (!ret[from1]){
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
			DWRUtil.addRows('resTableBody_id', getPartOfSavedRet1(from1, to1, ret), cellFuncs,  {escapeHtml:false} );
			 $j('table.resTable_id tbody tr:odd').addClass('oddRow');
			 $j('table.resTable_id tbody tr:even').addClass('evenRow');
			 $j('table.resTable_id tbody tr').attr('onmouseover','javascript:mouseOver(this);refresh(this);');
			 $j('table.resTable_id tbody tr').attr('onmouseout','javascript:mouseOut(this); refresh(this);');
			 addRowEventsFindPatient1();
			 fixHeader1();
			 $j('#results_box').css('display','');		
			} 						
	}

	function fixHeader1(){
   		var thead = document.getElementById('resTableHeader_id');
		var trs = thead.getElementsByTagName("tr");
		addClass(trs[0],"infoRow");
		addClass(trs[1],"oddRow");
		if (trs[0]){
			var toTmp = to1;
			if (toTmp > savedRet1.length)
			toTmp = savedRet1.length -1;
			toTmp++;
			
			td = trs[0].getElementsByTagName('td')[0];
			var fromTmp = from1 + 1;
			var nextN = "&nbsp;&nbsp;&nbsp;" + fromTmp + " <spring:message code="mdrtb.to"/> " + toTmp +" <spring:message code="mdrtb.of"/> " + savedRet1.length;
			
									var lastP = jumps1;
									if (savedRet1.length > toTmp){
										if (savedRet1.length - toTmp < lastP)
										lastP = savedRet1.length - toTmp;
									}
									var pipeTest = 0;
									if (savedRet1.length > jumps1 && toTmp != savedRet1.length){
									nextN = "<a href='javascript:next1()'><spring:message code="mdrtb.next"/> " + lastP + "</a>&nbsp;&nbsp;"+nextN;
									pipeTest = 1;
									}
									if (fromTmp > 1){
									if (pipeTest == 1)
									nextN = "<a href='javascript:previous1()'><spring:message code="mdrtb.previous"/> "+jumps1+"</a>&nbsp;|&nbsp;" + nextN;
									else
									nextN = "<a href='javascript:previous1()'><spring:message code="mdrtb.previous"/> "+jumps1+"</a>&nbsp;&nbsp;" + nextN;
									}
			td.innerHTML = nextN;
		}
   	}			
	function next1(){
		from1 = from1 + jumps1;
		if (to1 + jumps1 > savedRet1.length - 1)
			to1 = savedRet1.length - 1;
		else	
		to1 = to1 + jumps1;
		drawTable1(savedRet1);
	}
	
	function previous1(){
		to1 = from1 - 1;
		from1 = from1 - jumps1;
		if (to1 - from1 != jumps1)
			from1 = to1 - jumps1+1;
		if (to1 < jumps1)
			to1 = jumps1 - 1;
		if (from1 < 0)
			from1 = 0;			
		drawTable1(savedRet1);
	}
	function getPartOfSavedRet1(from1, to1, ret){
		var retTmp = new Array();
		if (ret[from1]){
			toTmp = to1;
			if (toTmp > ret.length)
				toTmp = ret.length -1;
			var c = 0;
			for (i = from1; i <= toTmp; i++){
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
 	showLoading1 = 1;
 	setTimeout('function sitStill(){return "";}', 5000);
    var disabledZone = $('disabledZone');
    if (showLoading1 == 1){
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
    showLoading1 = 0;
  });
}
		
</script>


<c:if test="${model.authenticatedUser != null}">

		<div id="findTestResult">
			
			<b class="boxHeader"><spring:message code="labmodule.labSearch.search" /></b>
			<div class="box" style="padding: 15px 15px 15px 15px;">
				
				<spring:message code="labmodule.search.searchLabResults" />
				<input type="radio" onClick="setOption()" name="search_lab" id="date" value="date" checked> <spring:message code="labmodule.search.dateRange" />
				<input type="radio" onClick="setOption()" name="search_lab" id="id" value="id"><spring:message code="labmodule.search.testId" />
				<input type="radio" onClick="setOption()" name="search_lab" id="suspect_name" value="suspect_name"><spring:message code="labmodule.search.name" />
 				<br>
				
				<br>
				
				<span hidden id="pname_search">
					<spring:message code="labmodule.search.findLabResult"/>
					<input type="text" value="" id="searchBox_name" id="searchBox_name" style="width:50%;">
				</span>
				
				<span hidden id="sname_search">
					<spring:message code="labmodule.search.findLabResult"/>
					<input type="text" value="" id="searchBox_sname" id="searchBox_sname" style="width:50%;">
				</span>
				
				<span id="date_search">
					<spring:message code="labmodule.search.findLabResult"/>
					&nbsp;&nbsp;
					
					<spring:message code="labmodule.search.dateFrom"/>
					<input type="date" id="dateFrom" name="dateFrom">
					&nbsp;
					
					<spring:message code="labmodule.search.dateTo"/>
					<input type="date" id="dateTo" name="dateTo">
					&nbsp;&nbsp;
					
					<img title="Find" name="findButton" id="findButton" src="${pageContext.request.contextPath}/moduleResources/labmodule/find.gif" alt="add" border="0" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='default'"/>
				</span>
				
				<div id="results_box">
						<table id="resTable_id" class="resTable" cellpadding="2" cellspacing="0" style="border-collapse: collapse">
							<thead id="resTableHeader_id" class="resTableHeader"/>	
							<tbody class="resTableBody" id="resTableBody_id" style="vertical-align: center"/>
							<tfoot id="resTableFooter_id" class="resTableFooter"/>	
						</table>
				</div>
		
			</div>
		</div> 
</c:if>

<script type="text/javascript">

function init() {
 	useMdrtbLoadingMessage('<spring:message code="mdrtb.loadingmessage"/>');
 	/* var val = multiply(2,2);
 	alert(val); */
 }
 window.onload=init;
 document.getElementById('date').checked = true;
 
 
function setOption(){

	 if (document.getElementById('id').checked) {
		 document.getElementById('date_search').style.display = "none"; 
	     document.getElementById('pname_search').style.display = "block";
	     document.getElementById('sname_search').style.display = "none";
	 }
	 else if (document.getElementById('date').checked){
		document.getElementById('date_search').style.display = "block"; 
	    document.getElementById('pname_search').style.display = "none";
	    document.getElementById('sname_search').style.display = "none";
	}
	 else{

		 document.getElementById('date_search').style.display = "none"; 
	     document.getElementById('pname_search').style.display = "none";
	     document.getElementById('sname_search').style.display = "block";
		 
	} 

	 document.getElementById('searchBox_name').value  = '';
	 document.getElementById('searchBox_sname').value  = '';
     document.getElementById('dateFrom').value  = '';
     document.getElementById('dateTo').value  = '';

     document.getElementById('results_box').style.display = "none"; 
}

	 
 </script>