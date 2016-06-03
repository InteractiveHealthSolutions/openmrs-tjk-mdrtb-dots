<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>
<%@ include file="mdrtbHeader.jsp"%>
<style><%@ include file="resources/mdrtb.css"%></style>
<style><%@ include file="resources/date_input.css"%></style>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery-1.2.3.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/jquery.dimensions.pack.js'></script>
<script src='<%= request.getContextPath() %>/moduleResources/mdrtb/date_input.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbContactsDWRService.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbFindPatient.js'></script>
<script src='<%= request.getContextPath() %>/dwr/interface/MdrtbNextVisit.js'></script>
<script type="text/javascript">
	var dateFormat = '${dateFormat}';
	var DAY_NAMES=new Array(${daysOfWeek});
    var MONTH_NAMES=new Array(${monthsOfYear});
    var $j = jQuery.noConflict(); 
    var relationshipTypeMap = {<c:forEach items="${relationshipTypes}" var="type" varStatus="varStatus">"${type.relationshipTypeId}A":"${type.aIsToB}","${type.relationshipTypeId}B":"${type.bIsToA}"<c:if test="${!varStatus.last}">,</c:if></c:forEach>};
    
  
    function editTest(personId){
    
			var hiddenElement = document.getElementById("testResultAction_"+personId);
			var testResult = document.getElementById("testResult_"+personId);
			var testResultDate = document.getElementById("testResultDate_"+personId);
			var testType = document.getElementById("testResultType_"+personId);
			var hiddentestResult = document.getElementById("hiddentestResult_"+personId);
			var hiddentestResultDate = document.getElementById("hiddentestResultDate_"+personId);
			var hiddentestType = document.getElementById("hiddentestResultType_"+personId);
			$j(testResult).val(hiddentestResult.value);
			$j(testResultDate).val(hiddentestResultDate.value);
			$j(testType).val(hiddentestType.value);
			
		
			$j(hiddenElement).val(1);
			var direction = false;
			$j(testResultDate).removeClass("disabledTwo");
			$j(testResult).removeClass("disabledTwo");
			$j(testType).removeClass("disabledTwo");	
			$j(testResult).attr("disabled", direction);
			$j(testResultDate).attr("disabled", direction);
			$j(testType).attr("disabled", direction);
			$j(testResultDate).attr("onMouseDown", "$j(this).date_input()");

    }
    
        function addNewTest(personId){
    
			var hiddenElement = document.getElementById("testResultAction_"+personId);
			var testResult = document.getElementById("testResult_"+personId);
			var testResultDate = document.getElementById("testResultDate_"+personId);
			var testType = document.getElementById("testResultType_"+personId);
			
			var direction = false;
			$j(testResultDate).removeClass("disabledTwo");
			$j(testResult).removeClass("disabledTwo");
			$j(testType).removeClass("disabledTwo");	
			$j(testResult).attr("disabled", direction);
			$j(testResultDate).attr("disabled", direction);
			$j(testType).attr("disabled", direction);
			
			$j(hiddenElement).val(2);
			$j(testResult).val("");
			$j(testResultDate).val("");
			$j(testType).val("");
			
			$j(testResultDate).mousedown(function(){
				$j(this).date_input();
			});
    }
    
    function deleteThisTest(personId){
    
    		var hiddenElement = document.getElementById("testResultAction_"+personId);
			var testResult = document.getElementById("testResult_"+personId);
			var testResultDate = document.getElementById("testResultDate_"+personId);
			var testType = document.getElementById("testResultType_"+personId);
    		
    		var direction = true;
    		$j(testResultDate).addClass("disabledTwo");
			$j(testResult).addClass("disabledTwo");
			$j(testType).addClass("disabledTwo");
			$j(testResult).attr("disabled", direction);
			$j(testResultDate).attr("disabled", direction);
			$j(testType).attr("disabled", direction);
				
    		$j(hiddenElement).val(3);
    		$j(testResult).val("");
			$j(testResultDate).val("");
			$j(testType).val("");
    		
    }
    
    ////simple popup:
    var showSubmit = true;
	$j(document).ready(function(){ 				 
  		activateNewSimplePopups();
 	});	
 	
 	var doStuffAction = 0;
 	var contactIdInQuestion = 0;
 	var addressIdInQuestion = 0;
 	var relationshipIdInQuestion = 0;

 	
 	//this is the onsubmit function always called by the simple popup window
    function doStuff(input){
    	
	    if (doStuffAction == 1){
	    	//get values
	    	var popupRegion = document.getElementById("popupRegion_" + contactIdInQuestion);
	    	var popupRegionVal = $j(popupRegion).val();
	    	var popupDistrict = document.getElementById("popupDistrict_" + contactIdInQuestion);
	    	var popupDistrictVal = $j(popupDistrict).val();
	    	var popupCity = document.getElementById("popupCity_" + contactIdInQuestion);
	    	var popupCityVal = $j(popupCity).val();
	    	var popupTownship = document.getElementById("popupTownship_" + contactIdInQuestion);
	    	var popupTownshipVal = $j(popupTownship).val();
	    	var popupAddressTwo = document.getElementById("popupAddressTwo_" + contactIdInQuestion);
	    	var popupAddressTwoVal = $j(popupAddressTwo).val();
	    	var popupAddressOne = document.getElementById("popupAddressOne_" + contactIdInQuestion);
	    	var popupAddressOneVal = $j(popupAddressOne).val();
	    	var popupPhone  = document.getElementById("popupPhone_" + contactIdInQuestion);
	    	var popupPhoneVal = $j(popupPhone).val();
	    	//set values

	    	var spAddressOne = document.getElementById("spAddress1_" + contactIdInQuestion);
	    	$j(spAddressOne).text(popupAddressOneVal);
	    	var spAddressTwo = document.getElementById("spAddress2_" + contactIdInQuestion);
	    	$j(spAddressTwo).text(popupAddressTwoVal);
	    	var spPhone = document.getElementById("spPhone_" + contactIdInQuestion);
	    	$j(spPhone).text(popupPhoneVal);
	    	var spRegion = document.getElementById("spRegion_" + contactIdInQuestion);
	    	$j(spRegion).text(popupRegionVal);
	    	var spDistrict = document.getElementById("spDistrict_" + contactIdInQuestion);
	    	$j(spDistrict).text(popupDistrictVal);
	    	var spTownship = document.getElementById("spTownship_" + contactIdInQuestion);
	    	$j(spTownship).text(popupTownshipVal);
	    	var spCity = document.getElementById("spCity_" + contactIdInQuestion);
	    	$j(spCity).text(popupCityVal);
	    	//DWR
	 		
				 if (addressIdInQuestion > 0){   	
						MdrtbContactsDWRService.updateAddress(contactIdInQuestion, addressIdInQuestion, popupAddressOneVal, popupAddressTwoVal, popupTownshipVal, popupCityVal, popupDistrictVal, popupRegionVal, popupPhoneVal, function(ret){
										if (!ret) 
											alert("<spring:message code="mdrtb.DWRfailedtoupdateaddress" />");
										else {
										
											//TODO:  this is crap:
											var editAddressLink  =  document.getElementById("editAddress_" + contactIdInQuestion );	
											$j(editAddressLink).addClass("displayOff");
										
										}
										
									});
				 }  else  {
				 	//create new address
				 	MdrtbContactsDWRService.createNewAddress(contactIdInQuestion, popupAddressOneVal, popupAddressTwoVal, popupTownshipVal, popupCityVal, popupDistrictVal, popupRegionVal, popupPhoneVal, function(ret){
										if (ret == null) {
										alert("<spring:message code="mdrtb.DWRfailedtoupdateaddress" />");
										} else {
											
											//TODO: this is crap
											var editAddressLink  =  document.getElementById("editAddress_" + contactIdInQuestion );	
											$j(editAddressLink).addClass("displayOff");
											
						//					$j(editAddressLink).click(function(){	
						//						doStuffAction = 1;
						//						addressIdInQuestion=ret.personAddressId;
						//						contactIdInQuestion=ret.person;
						//						return false;
						//					});
									   
									   		
										}
									});
				 
				 }   	
		}
		
		if (doStuffAction == 2){
			//get values
			var popupGivenName = document.getElementById("popupGivenName_" + contactIdInQuestion);
	    	var popupGivenNameVal = $j(popupGivenName).val();
	    	var popupFamilyName = document.getElementById("popupFamilyName_" + contactIdInQuestion);
	    	var popupFamilyNameVal = $j(popupFamilyName).val();
	    	var popupRelationship = document.getElementById("popupRelationship_" + contactIdInQuestion);
	    	var popupRelationshipVal = $j(popupRelationship).val();
	    	var popupContactId = document.getElementById("popupContactId_" + contactIdInQuestion);
	    	var popupContactIdVal = $j(popupContactId).val();
	    	var popupKnownMDR = document.getElementById("popupKnownMDR_" + contactIdInQuestion);
	    	var popupKnownMDRVal = $j(popupKnownMDR).val();
	    	//set values
	    	var spRelationshipType = document.getElementById("spRelationshipType_" + contactIdInQuestion);
	    	$j(spRelationshipType).html(relationshipTypeMap[popupRelationshipVal]);
	    	var spGivenName = document.getElementById("spGivenName_" + contactIdInQuestion);
	    	$j(spGivenName).text(popupGivenNameVal);
	    	var spFamilyName = document.getElementById("spFamilyName_" + contactIdInQuestion);
	    	$j(spFamilyName).text(popupFamilyNameVal);
	    	
	    	
	    	var spContactId = document.getElementById("contactId_" + contactIdInQuestion);
	   		if (spContactId != null)
	    		$j(spContactId).text(popupContactIdVal);
	    	
	    	var spKnownMDR = document.getElementById("isKnownMdr_" + contactIdInQuestion);
	    	if (popupKnownMDRVal == 0)
	    		$j(spKnownMDR).html('<spring:message code="mdrtb.no" />');
	    	else if (popupKnownMDRVal == 1)	
	    		$j(spKnownMDR).html('<spring:message code="mdrtb.yes" />');
	    		
	    	//DWR
	    	MdrtbContactsDWRService.updateContact(${obj.patient.patientId}, contactIdInQuestion, relationshipIdInQuestion, popupRelationshipVal, popupGivenNameVal, popupFamilyNameVal, popupContactIdVal , popupKnownMDRVal, function(ret){
							if (!ret)
								alert("<spring:message code="mdrtb.DWRfailedtoupdatecontact" />");
							else {
							
								//TODO: this is crap
								var editContactLink  =  document.getElementById("editContact_" + contactIdInQuestion );	
							    $j(editContactLink).addClass("displayOff");
							   
							}
						});
	    	
		}	
		
		//new contacts:
	
		doStuffAction = 0;

    }
    
    
    //hack called by onblur in popup fields:
    function setValue(input){
    	var val = input.value;
    	var id = input.id;
    	var target = document.getElementById(id);
    	$j(target).val(val);
    }
    
    //////person lookup:
    
    
     //simple popup cont... The following is for patient lookups:
	var savedRet;
    function loadPatients(input){
    	//TODO:  Next N instead...
    	if (input.value.length > 4){
	    	MdrtbFindPatient.findAllPeople(input.value, dateFormat, false, function(ret){
   						savedRet = ret;
   						drawPatientTable(savedRet);
   			});
    	} else {
    		$j(".resTableBodyLookup").html("<Br>");
    	}
    }
    
    function drawPatientTable(savedRet){
    $j(".resTableBodyLookup").html("<Br>");
    	var retString = "<br>"; 
    	if (savedRet.length > 0)
    		retString += "<table class='findpatientpopup'><thead><Th><spring:message code="mdrtb.name" /></th><Th><spring:message code="mdrtb.surname" /></th><Th><spring:message code="mdrtb.birthdate" /></th><Th><spring:message code="mdrtb.healthcentervillage" /></th><Th><spring:message code="mdrtb.gender" /></th></thead><tbody id='findpatientpopupbody'>";
    	for (var i = 0; i < savedRet.length; i++){
    		var thisPatient = savedRet[i];
    		retString += "<tr><td><span class='displayOff'>" + i + "</span>" + thisPatient.givenName + "</td><td>" + thisPatient.familyName + "</Td><td>" + thisPatient.birthdateString + "</td><Td>" + thisPatient.village + "</Td><td>" + thisPatient.gender + "</td></tr>";
    	}
    	if (savedRet.lenght > 0) 
    		retString += "</tbody></table>"
    	$j(".resTableBodyLookup").html(retString);
    	//add click and onmouseover events and striping to tr's
    	addRowEvents();
    	
    }
    
    	var classTmp = "";
    function addRowEvents(){
		var tbody = document.getElementById('findpatientpopupbody');
		var trs = tbody.getElementsByTagName("tr");
		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
   		
   		$j('table.findpatientpopup tbody tr').mouseover(function(){
				mouseOver(this);refresh(this);
			});
		$j('table.findpatientpopup tbody tr').mouseout(function(){
			mouseOut(this); refresh(this);
		});
		$j('table.findpatientpopup tbody tr').click(function(){
			choosePatient(this);
		});
			
	}
	
	function mouseOver(input){
		classTmp = this.className;
		input.className="rowMouseOver";
		refresh(this);	
	}
	function mouseOut(input){
		input.className = classTmp;
		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
  		$j('table.findpatientpopup tbody tr:odd').addClass('oddRow');
  		$j('table.findpatientpopup tbody tr:even').addClass('evenRow');
		refresh(this);
    }
    	function refresh(input){
		if (input.className) input.className = input.className;
	}
	
	
	//sets the patient fileds	
	function choosePatient(input){
			//set whatever you need to set:

			
		if (input.firstChild.firstChild.innerHTML != null && input.firstChild.firstChild.innerHTML != 'undefined' && input.firstChild.firstChild.innerHTML !=''){
			var index = input.firstChild.firstChild.innerHTML;
			
			var newGivenName = document.getElementById("newGivenName_" + newRowsToBeAdded);
			var newFamilyName = document.getElementById("newFamilyName_" + newRowsToBeAdded);
			var newName = document.getElementById("newRelationshipId_" + newRowsToBeAdded);
			var newGender = document.getElementById("newGender_" + newRowsToBeAdded);
			var newRelationshipType = document.getElementById("newRelationshipType_" + newRowsToBeAdded);
			
			
			var relationshipTypeParents = $j(newRelationshipType).parent();
			var parent = relationshipTypeParents[0];
			var newSpan = document.createElement("span");
			$j(newSpan).html("<bR>* <spring:message code='mdrtb.choosearelationshiptype' />");
			$j(newSpan).attr("style", "color:red");
			if (newRelationshipType.value != null && (newRelationshipType.value == "" || newRelationshipType.value == null))
				parent.appendChild(newSpan);
			
			$j(newGivenName).val(savedRet[index].givenName);
			$j(newFamilyName).val(savedRet[index].familyName);
			$j(newName).val(savedRet[index].personId);	
			$j(newGender).val(savedRet[index].gender);
				
			var tbodys = $j(input).parent();
			var tables = $j(tbodys[0]).parent();
			$j(tables).empty();				
			$j(newGivenName).addClass("disabled");	
			$j(newFamilyName).addClass("disabled");
			$j(newGender).addClass("disabled");	
			$j(".simple_close").click();
			
		}
	}
	
	
	function showNewContactFields(input){
		var addnewpersonHTML = $j("#hiddenaddnewperson").html();
		var popupContainer = $j(".popup_container");
		$j(popupContainer).html(addnewpersonHTML);
		
	}
	
	function enableFindPerson(input){
		var	hiddenfindapersonHTML = $j("#hiddenfindaperson").html();
		var popupContainer = $j(".popup_container");
		$j(popupContainer).html(hiddenfindapersonHTML);
	}
    
    
    ///////  Add a new contact
    
    var newRowsToBeAdded = 0;
    function addNewContact(){
    	newRowsToBeAdded++;
    	$j("#newRowsToBeAdded").val(newRowsToBeAdded);
    	var length = $j("#newPeopleBody").children().length;
		if (length == 0){
			$j("#newPeopleBody").empty();
			var clonedHeaderRow = $j("#newHeaderTemplate").clone();
			$j("#newPeopleBody").append(clonedHeaderRow);
		}
		var clonedRow = $j("#newRowTemplate").clone();
		$j(clonedRow).attr("id","newRowTemplate_" + newRowsToBeAdded);
		var tds = $j(clonedRow).children();
		for (i = 0; i < tds.length-1; i++){
			var td = tds[i];
			var tdInsides = $j(td).children();
			for (j = 0; j < tdInsides.length; j++){
				var inputItem = tdInsides[j];
				inputItem.name = inputItem.name + newRowsToBeAdded;
				inputItem.id = inputItem.id + newRowsToBeAdded;
			}
		}
		$j("#newPeopleBody").append(clonedRow);
		activateNewSimplePopups();
		
    }
    
    
    function remove(obj){
		 $j(obj).remove();
	 	var length = $j("#newPeopleBody").children().length; 
	 	if (length == 1){
	 		$j("#newPeopleBody").empty();
	 	}
	}
    
    
    
   /////////////////// //here's the main popup function:
    
    function activateNewSimplePopups() {
     var viewport = {
        o: function() {
            if (self.innerHeight) {
    			this.pageYOffset = self.pageYOffset;
    			this.pageXOffset = self.pageXOffset;
    			this.innerHeight = self.innerHeight;
    			this.innerWidth = self.innerWidth;
    		} else if (document.documentElement && document.documentElement.clientHeight) {
    			this.pageYOffset = document.documentElement.scrollTop;
    			this.pageXOffset = document.documentElement.scrollLeft;
    			this.innerHeight = document.documentElement.clientHeight;
    			this.innerWidth = document.documentElement.clientWidth;
    		} else if (document.body) {
    			this.pageYOffset = document.body.scrollTop;
    			this.pageXOffset = document.body.scrollLeft;
    			this.innerHeight = document.body.clientHeight;
    			this.innerWidth = document.body.clientWidth;
    		}
    		return this;
        },
        init: function(el) {
            $j(el).css("left",Math.round(viewport.o().innerWidth/2) + viewport.o().pageXOffset - Math.round($j(el).width()/2));
            $j(el).css("top",Math.round(viewport.o().innerHeight/2) + viewport.o().pageYOffset - Math.round($j(el).height()/2));
        }
    };
    $j(".simple_popup_info").each(function(){
        $j(this).css("display","none").siblings(".simple_popup").click(function(){
            $j(".simple_popup_div").remove();
            var strSimple = "<div class='simple_popup_div'><div class='simple_popup_inner'>";
            strSimple += $j(this).siblings(".simple_popup_info").html();
            strSimple += "<Br><Br><table class='popupTable'><tr>";
            if (showSubmit == true)
            strSimple += "<td><p class='simple_close'>[ x ] <a href='#' onmouseup='javascript:doStuff(this)'><spring:message code="mdrtb.submit" /></a>&nbsp;&nbsp;&nbsp;</p></td>";
            strSimple += "<td><p class='simple_close'>[ x ] <a href='#'><spring:message code="mdrtb.close" /></a><Br><br>&nbsp;</p></td></tr></table>";
            strSimple += "<Br></div></div>";
            $j("body").append(strSimple);
            viewport.init(".simple_popup_div");
            $j(".simple_close").click(function(){
                $j(".simple_popup_div").remove();
                $j(".resTableBodyLookup").empty();
                reasonTmp = "";
 				orderIdTmp = "";
 				closeDateTmp = "";
 				action = "";
 				showSubmit = true;
                return false;
           	 });
            	return false;
        	});
    	});
    	
     $j(".simple_popup_info").each(function(){
        $j(this).css("display","none").siblings(".simple_popup").click(function(){
            $j(".simple_popup_div").remove();
            var strSimple = "<div class='simple_popup_div'><div class='simple_popup_inner'>";
            strSimple += $j(this).siblings(".simple_popup_info").html();
            strSimple += "<Br><Br><table class='popupTable'><tr>";
            if (showSubmit == true)
            strSimple += "<td><p class='simple_close'>[ x ] <a href='#' onmouseup='javascript:doStuff(this)'><spring:message code="mdrtb.submit" /></a>&nbsp;&nbsp;&nbsp;</p></td>";
            strSimple += "<td><p class='simple_close'>[ x ] <a href='#'><spring:message code="mdrtb.close" /></a><Br><br>&nbsp;</p></td></tr></table>";
            strSimple += "<Br></div></div>";
            $j("body").append(strSimple);
            viewport.init(".simple_popup_div");
            $j(".simple_close").click(function(){
                $j(".simple_popup_div").remove();
                $j(".resTableBodyLookup").empty();
                reasonTmp = "";
 				orderIdTmp = "";
 				closeDateTmp = "";
 				action = "";
 				showSubmit = true;
                return false;
           	 });
            	return false;
        	});
    	});
    }
    
    
    //////////////////////Make person a patient:
    
    
   function makePatient(contactId, relationshipId ,divId, patientIdInput, patientIdInputType){
    	var myDiv = document.getElementById(divId);
		var patientIdBox = document.getElementById(patientIdInput);
		var typeBox = document.getElementById(patientIdInputType);
		var newId = patientIdBox.value;
		var newIdType = typeBox.value;
		if (newId != "" && newIdType != ""){
				//DWR:
			MdrtbContactsDWRService.makePatient(contactId, relationshipId, newId, newIdType, function(ret){
   						if (!ret){
   							alert("Error converting person to patient.  Check logs for DWR error");
   						} else {
   							var isPatientText = document.getElementById("isPatient_" + contactId);
   							$j(isPatientText).html("<spring:message code="mdrtb.yes" />");
   							
   							var contactNameTD = document.getElementById("contactName_" + contactId);
   							var currentTDContents = contactNameTD.innerHTML;
   							//TODO: 
   							
   							var givenNameSpan = document.getElementById("spGivenName_" + contactId);
   							var familyNameSpan = document.getElementById("spFamilyName_" + contactId);
   							
   							var givenName = $j(givenNameSpan).html();
   							var familyName = $j(familyNameSpan).html();
   							
   							$j(givenNameSpan).empty();
   							$j(familyNameSpan).empty();
   							
   							$j(givenNameSpan).html("<a href='${pageContext.request.contextPath}/patientDashboard.form?patientId=" + contactId + "'>" +givenName+ "</a>");
   							$j(familyNameSpan).html("<a href='${pageContext.request.contextPath}/patientDashboard.form?patientId=" + contactId + "'>" +familyName+ "</a>");
   							
   						}
   			});
    	$j(myDiv).addClass("displayOff");
		
		} else {
			alert("<spring:message code="mdrtb.youmustgiveapatientidentifier" />");
    	}
    }
    
    function showMakePatientDiv(divId) {
    	var myDiv = document.getElementById(divId);
    	$j(myDiv).removeClass("displayOff");
    }
    function cancelMakePatient(divId, input){
    	var myDiv = document.getElementById(divId);
    	var myInput = document.getElementById(input);
    	$j(myInput).val("");
    	$j(myDiv).addClass("displayOff");
    }
    
        function showNextVisitDiv(input){
    	var divToShow = document.getElementById("nextVisitDivnextscheduledvisit");
    	$j(divToShow).removeClass("displayOff");
    	$j(divToShow).addClass("displayOn");
    	var nextVisitMessage = document.getElementById("nextVisitMessage");
    	$j(nextVisitMessage).text("");
    }
    
    function saveNewNextVisit(input){
    	var newValTextbox = document.getElementById("nextscheduledvisit");
    	if (newValTextbox.value != ""){
    		var divToShow = document.getElementById("nextVisitDivnextscheduledvisit");
    		MdrtbNextVisit.setNextVisitDate(newValTextbox.value,'${obj.patient.patientId}','${obj.location.locationId}', function(ret){
    			if (!ret){
    				alert("<spring:message code='DWRfailedtosetnextvisit' />");
    			} else {
    				$j(divToShow).removeClass("displayOn");
					$j(divToShow).addClass("displayOff");
					var nextVisitMessage = document.getElementById("nextVisitMessage");
					$j(nextVisitMessage).text("<spring:message code='mdrtb.successfullyupdated' />");
    			}
			});
    		
    	}
    }
    
</script>



<openmrs:portlet url="mdrtbmdrtbPatientHeader" id="mdrtbmdrtbPatientHeader" moduleId="mdrtb" patientId="${obj.patient.patientId}"/>
<!-- <openmrs:portlet url="mdrtbHeaderBox" id="mdrtbHeaderBox" moduleId="mdrtb"  patientId="${obj.patient.patientId}"/>-->
<br><br>
<div style="font-size:80%">

<form method="post"> 
	<br>
			<Br>	
			
			<c:if test="${obj.contacts != null}">
			<table class="contactsTable">
			<tr style="font-size:90%">
				<th><spring:message code="mdrtb.delete" /></th>
				<th><spring:message code="mdrtb.name" /></th>
				<th><spring:message code="mdrtb.relationship" /></th>
				<th><spring:message code="mdrtb.latesttestresult" /></th>
				<th><spring:message code="mdrtb.address" /></th>
				<th><spring:message code="mdrtb.patienttype" /></th>	
				<th></th>
			</tr>
			<c:if test="${empty obj.contacts}">
				<tr><td colspan="7" style="text-align:center"><i><spring:message code="mdrtb.none" /></i></td></tr>
			</c:if>
			<c:forEach items="${obj.contacts}" var="contact" varStatus="varStatus">
				<c:set var="rowClass" scope="page">
					<c:choose>
					<c:when test="${varStatus.index % 2 == 0}">contactsTableOdd</c:when><c:otherwise>contactsTableEven</c:otherwise></c:choose>
				</c:set>
				
				<Tr class="${rowClass}">
					<Td>
						<span class="displayOff">${contact.person.personId}</span>
						<input type="checkbox" name="deleteContact_${contact.person.personId}" id="deleteContact_${contact.person.personId}" value="${contact.relationship.relationshipId}"/>
					</Td>
					<Td id="contactName_${contact.person.personId}">
						<c:if test="${contact.isTBPatient}">
							<a href="${pageContext.request.contextPath}/module/mdrtb/dashboard/dashboard.form?patientId=${contact.person.personId}"><span id="spGivenName_${contact.person.personId}">${contact.person.givenName}</span> <span id="spFamilyName_${contact.person.personId}">${contact.person.familyName}</span></a>
						</c:if>
						<c:if test="${!contact.isTBPatient}">
							<c:if test="${contact.isPatient}">
								<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${contact.person.personId}"><span id="spGivenName_${contact.person.personId}">${contact.person.givenName}</span> <span id="spFamilyName_${contact.person.personId}">${contact.person.familyName}</span></a>
							</c:if>
						</c:if>
						<c:if test="${!contact.isTBPatient}">
							<c:if test="${!contact.isPatient}">
								<span id="spGivenName_${contact.person.personId}">${contact.person.givenName}</span> <span id="spFamilyName_${contact.person.personId}">${contact.person.familyName}</span>
							</c:if>
						</c:if>	
						
						<c:if test="${!empty contact.mdrtbContactId}">
							<br>${contact.mdrtbContactId.value}	
						</c:if>
					</td>
					
					<td id="spRelationshipType_${contact.person.personId}">
						<c:if test="${contact.relationship.personA.personId == obj.patient.patientId}">
							${contact.relationship.relationshipType.bIsToA}
						</c:if>
						<c:if test="${contact.relationship.personB.personId == obj.patient.patientId}">
							${contact.relationship.relationshipType.aIsToB}
						</c:if>
					</td>
					<td>
							
							<input type="hidden" name="testResultAction_${contact.person.personId}" id="testResultAction_${contact.person.personId}" value="0" />
							<input type="hidden" name="hiddentestResult_${contact.person.personId}" id="hiddentestResult_${contact.person.personId}" value="${contact.testResult.valueCoded.conceptId}">
						<table>
						<tr><td><spring:message code="mdrtb.result" /></td><td>
							<select class="disabledTwo" disabled="true" name="testResult_${contact.person.personId}" id="testResult_${contact.person.personId}" >
								<option value=""></option>
								<c:forEach items="${testResultResponses}" var="result" varStatus="varStatus">
									<option value="${result.answerConcept.conceptId}"
									<c:if test="${contact.testResult.valueCoded.conceptId == result.answerConcept.conceptId}">SELECTED</c:if>
									>${result.answerConcept.name.name}</option>	
								</c:forEach>
							</select>
							
						</td></tr>
						<Tr><td><spring:message code="mdrtb.datelowercase" /></td><td>
							<input type="hidden" name="hiddentestResultDate_${contact.person.personId}" id="hiddentestResultDate_${contact.person.personId}" value="<openmrs:formatDate date="${contact.testResult.valueDatetime}" format="${dateFormat}" />" />
							
							<input  class="disabledTwo" disabled="true" type="text" style="width:90px"  name="testResultDate_${contact.person.personId}" id="testResultDate_${contact.person.personId}" value="<openmrs:formatDate date="${contact.testResult.valueDatetime}" format="${dateFormat}" />"/>
						</td></tr>	
						<Tr><td><spring:message code="mdrtb.typelowercase" /></td><td>
							<input type="hidden" name="hiddentestResultType_${contact.person.personId}" id="hiddentestResultType_${contact.person.personId}" value="${contact.testType.valueCoded.conceptId}" />
							
							<select  class="disabledTwo" disabled="true" name="testResultType_${contact.person.personId}" id="testResultType_${contact.person.personId}">
								<option value=""></option>
								<c:forEach items="${testTypeResponses}" var="type" varStatus="varStatus">
									<option value="${type.answerConcept.conceptId}"
									<c:if test="${contact.testType.valueCoded.conceptId == type.answerConcept.conceptId}">SELECTED</c:if>
									>${type.answerConcept.name.shortName}</option>	
								</c:forEach>
							</select>
					
						</td></tr>
						<tr><td></td><td>
							<c:if test="${!empty contact.testResult}">
							<a href="#" onClick="javascript:editTest(${contact.person.personId});return false;"><spring:message code="mdrtb.editthistest" /></a><Br>
							</c:if>
							<a href="#" onClick="javascript:addNewTest(${contact.person.personId});return false;"><spring:message code="mdrtb.addnewtest" /></a><Br>
							<c:if test="${!empty contact.testResult}">
								<a href="#" onClick="javascript:deleteThisTest(${contact.person.personId});return false;"><spring:message code="mdrtb.deletethistest" /></a><Br>
							</c:if>
						</td></tr></table>
					
						
					</td>
					<td>
						<table><tr><td>
						<span id="spAddress1_${contact.person.personId}">${contact.address.address1}</span> &nbsp;
						<span id="spAddress2_${contact.person.personId}">${contact.address.address2}</span><Br>
						<span id="spTownship_${contact.person.personId}">${contact.address.townshipDivision}</span><c:if test="${!empty contact.address.townshipDivision}">&nbsp;&nbsp;</c:if> <span id="spCity_${contact.person.personId}">${contact.address.cityVillage} &nbsp;</span>
						<Br><span id="spDistrict_${contact.person.personId}">${contact.address.countyDistrict}</span><c:if test="${!empty contact.address.countyDistrict}">&nbsp;&nbsp;</c:if> <span id="spRegion_${contact.person.personId}">${contact.address.region} &nbsp;</span>
						<Br><span id="spPhone_${contact.person.personId}">${contact.phone.valueText} &nbsp;</span>
						</td></tr>
						<tr><td style="vertical-align:bottom">
						<a href="#" id="editAddress_${contact.person.personId}" class="simple_popup" onClick="javascript:doStuffAction = 1;contactIdInQuestion=${contact.person.personId}<c:if test="${!empty contact.address.personAddressId}">;addressIdInQuestion=${contact.address.personAddressId}</c:if>;return false;"><spring:message code="mdrtb.editaddress" /></a>
													<div class="simple_popup_info">
														<input type="hidden" id="popupContactId" value="${contact.person.personId}">
														<table>
															<tr>
																<Td>
																	<spring:message code="mdrtb.address1" />
																</Td>
																<Td>
																	<input type="text" name="popupAddressOne_${contact.person.personId}" id="popupAddressOne_${contact.person.personId}" value="${contact.address.address1}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.address2" />
																</Td>
																<Td>
																	<input type="text" name="popupAddressTwo_${contact.person.personId}" id="popupAddressTwo_${contact.person.personId}" value="${contact.address.address2}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.townshipdivision" />
																</Td>
																<Td>
																	<input type="text" name="popupTownship_${contact.person.personId}" id="popupTownship_${contact.person.personId}" value="${contact.address.townshipDivision}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.cityvillage" />
																</Td>
																<Td>
																	<input type="text" name="popupCity_${contact.person.personId}" id="popupCity_${contact.person.personId}" value="${contact.address.cityVillage}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.district" />
																</Td>
																<Td>
																	<input type="text" name="popupDistrict_${contact.person.personId}" id="popupDistrict_${contact.person.personId}" value="${contact.address.countyDistrict}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.region" />
																</Td>
																<Td>
																	<input type="text" name="popupRegion_${contact.person.personId}" id="popupRegion_${contact.person.personId}" value="${contact.address.region}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.phone" />
																	
																</Td>
																<Td>
																	<input type="text" name="popupPhone_${contact.person.personId}" id="popupPhone_${contact.person.personId}" value="${contact.phone.valueText}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
														</table>
													</div>
						</td></tr></table>
					</td>
					<td>
						<table>
							<c:if test="${!empty contact.isTBPatient}">
								<c:if test="${contact.isTBPatient}">
									<Tr><Td><spring:message code="mdrtb.istbpatient" /></Td><td><spring:message code="mdrtb.yes" /></td></Tr>
								</c:if>
								<c:if test="${contact.isTBPatient == false}">
									<tr><Td><spring:message code="mdrtb.istbpatient" /></Td><td><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
							</c:if>
							<c:if test="${!empty contact.isPatient}">
								<c:if test="${contact.isPatient}">
									<tr><td><spring:message code="mdrtb.ispatient" /></td><td><spring:message code="mdrtb.yes" /></td></tr>
								</c:if>
								<c:if test="${contact.isPatient == false}">
									<tr><td><spring:message code="mdrtb.ispatient" /></td><td id="isPatient_${contact.person.personId}"><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
							</c:if>
							<c:if test="${!empty contact.knownMdrtbContact}">
								<c:if test="${contact.knownMdrtbContact.valueNumeric == 0}">
									<tr><td><spring:message code="mdrtb.knownmdr" />?</td><td id="isKnownMdr_${contact.person.personId}"><spring:message code="mdrtb.no" /></td></tr>
								</c:if>
								<c:if test="${contact.knownMdrtbContact.valueNumeric == 1}">
									<tr><td><spring:message code="mdrtb.knownmdr" />?</td><td id="isKnownMdr_${contact.person.personId}"><spring:message code="mdrtb.yes" /></td></tr>
								</c:if>
							</c:if>
						</table>
					</td>
					<td>
						<a href="#" class="simple_popup" id="editContact_${contact.person.personId}" onClick="javascript:doStuffAction = 2; contactIdInQuestion = ${contact.person.personId};relationshipIdInQuestion = ${contact.relationship.relationshipId}; return false;"><spring:message code="mdrtb.editthiscontact" /></a>
													<div class="simple_popup_info">
														<input type="hidden" id="popupContactId" value="${contact.person.personId}">
														<table>
															<tr>
																<Td>
																	<spring:message code="mdrtb.givenname" />
																</Td>
																<Td>
																	<input type="text" name="popupGivenName_${contact.person.personId}" id="popupGivenName_${contact.person.personId}" value="${contact.person.givenName}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td>
																	<spring:message code="mdrtb.familyname" />
																</Td>
																<Td>
																	<input type="text" name="popupFamilyName_${contact.person.personId}" id="popupFamilyName_${contact.person.personId}" value="${contact.person.familyName}" onblur="javascript:setValue(this)">
																</Td>
															</tr>
															<tr>
																<Td colspan="2">
																	<spring:message code="mdrtb.relationship" />
																</Td>
																
															</tr>
															<Tr>
																<Td colspan="2">
																	<select  name="popupRelationship_${contact.person.personId}" id="popupRelationship_${contact.person.personId}" onblur="javascript:setValue(this)">
																		<option value=""></option>
																		<c:forEach items="${relationshipTypes}" var="type" varStatus="varStatus">
																		
																			<option value="${type.relationshipTypeId}A" style="width:200px;"
																			<c:if test="${contact.relationship.relationshipType.relationshipTypeId == type.relationshipTypeId}">
																				<c:if test="${contact.relationship.personB.personId == obj.patient.patientId}">SELECTED</c:if>
																			</c:if>
																			>${type.aIsToB}</option>	
																			
																			<option value="${type.relationshipTypeId}B" style="width:200px;"
																			<c:if test="${contact.relationship.relationshipType.relationshipTypeId == type.relationshipTypeId}">
																				<c:if test="${contact.relationship.personA.personId == obj.patient.patientId}">SELECTED</c:if>
																			</c:if>
																			>${type.bIsToA}</option>	
																			
																		</c:forEach>
																	</select>
																</Td>
															</Tr>
															<Tr>
																<td><spring:message code="mdrtb.contactidentifier" /></td>
																<td><input type="text" value="${contact.mdrtbContactId.value}" id="popupContactId_${contact.person.personId}" name="popupContactId_${contact.person.personId}" onblur="javascript:setValue(this)"></td>
															</Tr>
															<tr>
																<td><spring:message code="mdrtb.knownmdr" /></td>
																<td>
																	<select  name="popupKnownMDR_${contact.person.personId}" id="popupKnownMDR_${contact.person.personId}" onblur="javascript:setValue(this)">
																		<option value=""></option>
																		<option value="0"
																			<c:if test="${!empty contact.knownMdrtbContact}">
																			<c:if test="${contact.knownMdrtbContact.valueNumeric == 0}">
																				SELECTED
																			</c:if>
																			</c:if>
																		><spring:message code="mdrtb.no" /></option>
																		<option value="1"
																			<c:if test="${!empty contact.knownMdrtbContact}">
																			<c:if test="${contact.knownMdrtbContact.valueNumeric == 1}">
																				SELECTED
																			</c:if>
																			</c:if>
																		><spring:message code="mdrtb.yes" /></option>
																	</select>
																
																</td>
															</tr>
														</table>	
													</div>
						
						<c:if test="${!empty contact.isPatient}">
							<c:if test="${!contact.isPatient}">	
							<div id="addNewPatientLink_${contact.person.personId}">
							<Br><a href="#" onClick="javascript:showMakePatientDiv('makePatient_${contact.person.personId}');return false;"><spring:message code="mdrtb.makethiscontactapatient" /></a><bR>
								<div id="makePatient_${contact.person.personId}" class="displayOff">
									<br>
									<spring:message code="mdrtb.patientidentifier" />:<br>
									<input type="text" id="makePatientId_${contact.person.personId}" value="">&nbsp;&nbsp;
									<Br><spring:message code="mdrtb.patientidentifiertype" />:<br>
									<select  id="newPatientIdentiferType_${contact.person.personId}">
											<option value=""></option>
											<c:forEach items="${patientIdentifierTypes}" var="type" varStatus="varStatus">
											<option value="${type.patientIdentifierTypeId}" style="width:200px;"
												<c:if test="${varStatus.count == 1}">SELECTED</c:if>
											>${type.name}</option>	
											</c:forEach>
									</select>
									<input type="button" value="<spring:message code="mdrtb.save" />" onClick="makePatient(${contact.person.personId},${contact.relationship.relationshipId},'addNewPatientLink_${contact.person.personId}', 'makePatientId_${contact.person.personId}', 'newPatientIdentiferType_${contact.person.personId}')">
									<input type="button" value="<spring:message code="mdrtb.cancel" />" onClick="cancelMakePatient('makePatient_${contact.person.personId}', 'makePatientId_${contact.person.personId}')">
								</div>
							</div>	
							</c:if>
						</c:if>	
					</td>
				</Tr>
			</c:forEach>
			</table>
			</c:if>
			<bR><a style="font-size:120%;" href="#" onClick="addNewContact();return false;"><spring:message code="mdrtb.addnewcontact" /></a><Br><Br>
			<br>
			<table id="newPeople" class="regTable">
				<tbody id="newPeopleBody">
				</tbody>
			</table>
			<br>




<input type="hidden" name="newRowsToBeAdded" id="newRowsToBeAdded" value="0">
<div id="newRowTemplateDisplayDiv" style="display:none">
		<table id="newRowTemplateTable">
			<tr id="newHeaderTemplate">
				<th><spring:message code="mdrtb.givenname" /></th>
				<Th><spring:message code="mdrtb.familyname" /></Th>
				<th><spring:message code="mdrtb.gender" /></th>
				<th><spring:message code="mdrtb.relationship" /></th>
				<Th><spring:message code="mdrtb.contactidentifier" /></Th>
				<Th><spring:message code="mdrtb.knownmdr" /></Th>
				<th></th>
			</tr>
			<Tr id="newRowTemplate">

				<Td>
					<input type="hidden" name="newRelationshipId_" id="newRelationshipId_" value="0">
					<input type="text" id="newGivenName_" name="newGivenName_" value=""> 
				</Td>
				<td>
					<input type="text" name="newFamilyName_" id="newFamilyName_" value="">
				</td>
				<td>
					<select id="newGender_" name="newGender_" value="">
						<option value=""></option>
						<option value="M"><spring:message code="mdrtb.male" /></option>
						<option value="F"><spring:message code="mdrtb.female" /></option>
					</select>
				</td>
				<td>
					<select  name="newRelationshipType_" id="newRelationshipType_">
						<option value=""></option>
						<c:forEach items="${relationshipTypes}" var="type" varStatus="varStatus">
							<option value="${type.relationshipTypeId}A" style="width:200px;">${type.aIsToB}</option>	
							<option value="${type.relationshipTypeId}B" style="width:200px;">${type.bIsToA}</option>	
						</c:forEach>
					</select>
				</td>
				<td>
					<input type="text" name="newContactIdString_" id="newContactIdString_" value="">
				</td>
				<td>
					<select  name="newKnownMDR_" id="newKnownMDR_">
						<option value=""></option>
						<option value="0"><spring:message code="mdrtb.no" /></option>
						<option value="1"><spring:message code="mdrtb.yes" /></option>
					</select>
				</td>
				<td style="vertical-align:center">
					<a href="#" class="simple_popup" onClick="javascript:showSubmit=false;javascript:return false;"><spring:message code="mdrtb.findapersoninopenmrs" /></a>
							<div class="simple_popup_info">
								<br>
									<spring:message code="mdrtb.findapersoninopenmrs" /> : <Br><Br><input type="text" id="patientLookupPopup" value="" onkeyup="javascript:loadPatients(this);"/><br>
									<div class="resTableBodyLookup">
										<Br>
									</div>
									
								<br>	
							</div>
					<br><a href="#" onClick="javascript:remove(this.parentNode.parentNode)"><spring:message code="mdrtb.cancellowercase" /></a>
				</td>
			</Tr>
		</table>
	</div>
		
	<input type="submit" name="submit" value="<spring:message code="mdrtb.save" />" >
	<input type='hidden' id='patientId' name='patientId' value='${obj.patient.patientId}'>
	<input type="submit" name="submit" value="<spring:message code="mdrtb.done" />">
	
</form>
<br>
</div>

<%@ include file="mdrtbFooter.jsp"%>
