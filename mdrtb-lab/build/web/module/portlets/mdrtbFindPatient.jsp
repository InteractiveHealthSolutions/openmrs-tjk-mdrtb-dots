<%@ page errorPage="/errorhandler.jsp" %>
<%@ page import="org.openmrs.web.WebConstants" %>
<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp" %>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbFindPatient.js'></script>
<openmrs:globalProperty key="use_patient_attribute.healthCenter" var="useHealthCenter"/>
<openmrs:globalProperty key="use_patient_attribute.mothersName" var="useMothersName"/>
<openmrs:globalProperty key="use_patient_attribute.tribe" var="useTribe"/>
<openmrs:globalProperty key="mdrtb.findPatientNumResults" var="numResults" defaultValue="5"/>
<script type="text/javascript">
		var classTmp = "";
		var from = 0;
		var jumps = ${numResults}; //this many patients at a time
		var to = jumps-1;
		var retSize = 0;
		var headerShown = 0;
		var savedRet = new Array();
		var mappedRet = new Array();
		var showLoading = 0;
		var $j = jQuery.noConflict();		
		$j(document).ready(function(){
   				$j('#results').css('display','none');
   				$j('#searchBox').val('');
   					$j('#searchBox').keyup(function(){
   						if ($j('#searchBox').val().length > 2){
	   						if ($j('#includeRetired:checked').val() != null && $j('#includeRetired:checked').val() == 'on')
	   							includeRet = true;
	   						MdrtbFindPatient.findPatients($j('#searchBox').val(), false, function(ret){
	   						from = 0; 
	   						to = jumps-1; 
							if (ret.length <= to)
								to = ret.length -1;
	   						savedRet = ret; 
	   						retSize = ret.length;
	   						drawTable(savedRet);});
   						}
   						else {
   	   						$j('#results').hide();
   						}
   			});	
 		});
	
	function addRowEventsFindPatient(){
		var tbody = document.getElementById('resTableBody');
		var trs = tbody.getElementsByTagName("tr");
		for(i = 0; i < trs.length; i++){
			if (trs[i].firstChild.firstChild.innerHTML != ""){
				trs[i].onclick = function() {
					selectPatient(this.firstChild.firstChild.innerHTML);
				}
				trs[i].onmouseover = function() {
					 mouseOver(this);
				}
				trs[i].onmouseout = function() {
					$j('table.resTable tbody tr:odd').addClass('oddRow');
  					$j('table.resTable tbody tr:even').addClass('evenRow');
					 mouseOut(this);
					 $j('table.resTable tbody tr:odd').addClass('oddRow');
  					$j('table.resTable tbody tr:even').addClass('evenRow');
				}
			}	
		}
	}
	
	function selectPatient(input){
		<c:choose>
			<c:when test="${not empty model.callback}">
				${model.callback}(mappedRet[input]);
				$j('#results').css('display','none');
	   			$j('#searchBox').val('');
	   		</c:when>
	   		<c:otherwise>
	   			window.location='${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=' + input;
	   		</c:otherwise>
	   	</c:choose>
	}
	function mouseOver(input){
		classTmp = this.className;
		input.className="rowMouseOver";
		refresh(this);	
	}
	function mouseOut(input){
		input.className = classTmp;
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

	function getDateString(d) {
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
	
	
	function drawTable(ret){
   						DWRUtil.removeAllRows('resTableBody');
   						mappedRet = new Array();
   						var count = from+1;
   						var cellFuncs = [
   								// the cell counter
								function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN"){
									var patientIdDiv='<div style="display:none" id="patientIdDiv">'+patient.patientId+'</div>';
									mappedRet[patient.patientId]=patient;
									return patientIdDiv + count++;
									}
								},
								function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN")
										return patient.identifier;
								},
								//first name 
								function(patient) { 
										if (patient.patientId != null && patient.patientId != "NaN"){
										return patient.givenName;
										} else {
											return patient;
								 	      }
							  		},
							  	//middle name
								function(patient) { 
										if (patient.patientId != null && patient.patientId != "NaN")
										return  patient.middleName;
							  		},
							  	//family name
							  	function(patient) { 
										if (patient.patientId != null && patient.patientId != "NaN")
										return  patient.familyName;
									
							  		},
							  	function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN")
										return patient.age;
								},
							  	function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN")
										return patient.gender;
								},
								<c:if test ="${!empty useTribe}">
								<c:if test ="${useTribe}">
								function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN"){
										if (patient.tribe != "" && patient.tribe != "Unknown")
										return patient.tribe;
									} 	
								},
								</c:if>
								</c:if>
								<c:if test ="${!empty useMothersName}">
								<c:if test ="${useMothersName}">
								function(patient) {
									var mnString = "test";
									if (patient.patientId != null && patient.patientId != "NaN"){
										if (patient.attributes != null){
												mnString = patient.attributes['Mother\'s Name'];
										}
									} 
									return mnString;	
								},
								</c:if>
								</c:if>
								function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN"){
										if (patient.birthdateEstimated != "" && patient.birthdateEstimated != "Unknown"){
											if (patient.birthdateEstimated)
											return "\~";
										}
									}	
								},
								function(patient) {
									if (patient.patientId != null && patient.patientId != "NaN"){
										if (patient.birthdate != "" && patient.birthdate != "Unknown")
										return getDateString(patient.birthdate);
									} 	
								}
								<c:if test ="${!empty useHealthCenter}">
								<c:if test ="${useHealthCenter}">
								, function(patient) {
									var hcString = " ";
									if (patient.patientId != null && patient.patientId != "NaN"){
										if (patient.attributes != null){
												hcString = patient.attributes['Health Center'];
										}
									} 
									return hcString;	
								}
								</c:if>
								</c:if>
								];
							var cellFuncsHeader = [
							function() {return " "},
							function() {return "<b><spring:message code='mdrtb.Identifier'/></b>"},
							function() {return "<b><spring:message code='mdrtb.first'/></b>"},
							function() {return "<b><spring:message code='mdrtb.middle'/></b>"},
							function() {return "<b><spring:message code='mdrtb.last'/></b>"},
							function() {return "<b><spring:message code='mdrtb.age'/></b>"},
							function() {return "<b><spring:message code='mdrtb.gender'/></b>"},
							<c:if test ="${!empty useTribe}">
								<c:if test ="${useTribe}">
									function() {return "<b><spring:message code='mdrtb.tribe'/></b>"},
								</c:if>
							</c:if>	
							<c:if test ="${!empty useMothersName}">
								<c:if test ="${useMothersName}">
									function() {return "<b><spring:message code='mdrtb.mothersname'/></b>"},
								</c:if>
							</c:if>	
							function() {return " "},
							function() {return '<b><spring:message code="mdrtb.birthdate"/></b>'}
							
							<c:if test ="${!empty useHealthCenter}">
								<c:if test ="${useHealthCenter}">		
									,function() {return "<b><spring:message code='mdrtb.healthcenter'/></b>"}
								</c:if>
							</c:if>
							];
							
							var cellFuncsNextN = [function(){return "t"}];	
							
							var formatCountCell = function(options) { 
								var td = document.createElement("td"); 
								td.setAttribute('colSpan','10');
								td.setAttribute('align', 'center');
								return td; 
							}
							
								if (ret[from]){
   								if (ret.length != retSize){
   										DWRUtil.removeAllRows('resTableHeader');
   										if (headerShown == 1)
   										headerShown--;
   										retSize = ret.length;
   									}
   								}
								if (headerShown == 0){
									DWRUtil.addRows('resTableHeader',[""], cellFuncsNextN, {cellCreator:formatCountCell,escapeHtml:false});
									DWRUtil.addRows('resTableHeader',[""], cellFuncsHeader, {escapeHtml:false});
									headerShown ++;
								}
								if (!ret[from]){
   										DWRUtil.removeAllRows('resTableHeader');
   										headerShown--;
   										if (ret.length == 0){
   								 		//no records found:
   								 		var cellFucsNoRecords = [function(){
   								 		var sb = document.getElementById("searchBox");
   								 		var searchText = sb.value;
   								 		return "<div><Br>&nbsp;&nbsp;&nbsp;<spring:message code="mdrtb.nopatientsfound"/> <i>"+searchText+"</i>.</div>";
   								 		}];
   								 		DWRUtil.addRows('resTableBody', ["nopatient"], cellFucsNoRecords,  {escapeHtml:false} );
   								 		
   								 		}
   								 		
   								}  else {
								DWRUtil.addRows('resTableBody', getPartOfSavedRet(from, to, ret), cellFuncs,  {escapeHtml:false} );
   								 $j('table.resTable tbody tr:odd').addClass('oddRow');
  								 $j('table.resTable tbody tr:even').addClass('evenRow');
   								 $j('table.resTable tbody tr').attr('onmouseover','javascript:mouseOver(this);refresh(this);');
   								 $j('table.resTable tbody tr').attr('onmouseout','javascript:mouseOut(this); refresh(this);');
   								 addRowEventsFindPatient();
   								 fixHeader();
   								 $j('#results').css('display','');		
   								} 				
   				}
   				
   	function fixHeader(){
   		var thead = document.getElementById('resTableHeader');
		var trs = thead.getElementsByTagName("tr");
		addClass(trs[0],"infoRow");
		addClass(trs[1],"oddRow");
		if (trs[0]){
			var toTmp = to;
			if (toTmp > savedRet.length)
			toTmp = savedRet.length -1;
			toTmp++;
			
			td = trs[0].getElementsByTagName('td')[0];
			var fromTmp = from + 1;
			var nextN = "&nbsp;&nbsp;&nbsp;" + fromTmp + " <spring:message code="mdrtb.to"/> " + toTmp +" <spring:message code="mdrtb.of"/> " + savedRet.length;
			
									var lastP = jumps;
									if (savedRet.length > toTmp){
										if (savedRet.length - toTmp < lastP)
										lastP = savedRet.length - toTmp;
									}
									var pipeTest = 0;
									if (savedRet.length > jumps && toTmp != savedRet.length){
									nextN = "<a href='javascript:next()'><spring:message code="mdrtb.next"/> " + lastP + "</a>&nbsp;&nbsp;"+nextN;
									pipeTest = 1;
									}
									if (fromTmp > 1){
									if (pipeTest == 1)
									nextN = "<a href='javascript:previous()'><spring:message code="mdrtb.previous"/> "+jumps+"</a>&nbsp;|&nbsp;" + nextN;
									else
									nextN = "<a href='javascript:previous()'><spring:message code="mdrtb.previous"/> "+jumps+"</a>&nbsp;&nbsp;" + nextN;
									}
			td.innerHTML = nextN;
		}
   	}			
	function next(){
		from = from + jumps;
		if (to + jumps > savedRet.length - 1)
			to = savedRet.length - 1;
		else	
		to = to + jumps;
		drawTable(savedRet);
	}
	
	function previous(){
		to = from - 1;
		from = from - jumps;
		if (to - from != jumps)
			from = to - jumps+1;
		if (to < jumps)
			to = jumps - 1;
		if (from < 0)
			from = 0;			
		drawTable(savedRet);
	}
	function getPartOfSavedRet(from, to, ret){
		var retTmp = new Array();
		if (ret[from]){
			toTmp = to;
			if (toTmp > ret.length)
				toTmp = ret.length -1;
			var c = 0;
			for (i = from; i <= toTmp; i++){
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
 	showLoading = 1;
 	setTimeout('function sitStill(){return "";}', 5000);
    var disabledZone = $('disabledZone');
    if (showLoading == 1){
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
    showLoading = 0;
  });
}
	</script>

<c:if test="${model.authenticatedUser != null}">
	<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/index.htm" />

	<c:choose>
		<c:when test="${model.size=='mini'}">
			<span id="findPatient">
				<c:choose>
					<c:when test="${!empty model.labelCode}"><span style="font-weight:bold"><spring:message code="${model.labelCode}"/></span></c:when>
					<c:otherwise><span style="font-weight:bold"><spring:message code="Patient.find"/></span></c:otherwise>
				</c:choose>
				<input type="text" value="" id="searchBox" name="searchBox">
				<div id="results" style="position:absolute; z-index:1000; border:2px solid black; background-color:#CCCCCC; ${model.resultStyle}">
					<table id="resTable" class="resTable" cellpadding="2" cellspacing="0" style="border-collapse: collapse">
						<thead id="resTableHeader" class="resTableHeader"/>	
						<tbody class="resTableBody" id="resTableBody" style="vertical-align: center"/>
						<tfoot id="resTableFooter" class="resTableFooter"/>	
					</table>
				</div>
			</span>
		</c:when>
		<c:otherwise>
			<div id="findPatient">
				
				<b class="boxHeader"><spring:message code="Patient.find" /></b>
				<div class="box" style="padding: 15px 15px 15px 15px;">
					
					<c:choose>
						<c:when test="${!empty model.labelCode}"><spring:message code="${model.labelCode}"/></c:when>
						<c:otherwise><spring:message code="Patient.find"/></c:otherwise>
					</c:choose>
					<input type="text" value="" id="searchBox" name="searchBox" style="width:50%;">   &nbsp;&nbsp;<br>

					<div id="results">
						<table id="resTable" class="resTable" cellpadding="2" cellspacing="0" style="border-collapse: collapse">
							<thead id="resTableHeader" class="resTableHeader"/>	
							<tbody class="resTableBody" id="resTableBody" style="vertical-align: center"/>
							<tfoot id="resTableFooter" class="resTableFooter"/>	
						</table>
					</div>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

</c:if>
<script>
 function init() {
 useMdrtbLoadingMessage('<spring:message code="mdrtb.loadingmessage"/>');
 }
 window.onload=init;
</script>
