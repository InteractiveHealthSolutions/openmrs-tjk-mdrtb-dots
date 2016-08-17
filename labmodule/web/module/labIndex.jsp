<%@ include file="/WEB-INF/view/module/labmodule/include.jsp"%>
<%@ include file="dotsHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/labmodule/dotsreports.css"/>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<h2><table width="90%">
<tr>
<td rowspan="3" align="left" rowspan="3" align="right" width="50%"><spring:message code="labmodule.title.homepage" /></td>
<td rowspan="3" align="right" width="40%" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/TJK_logo.jpg" alt="logo Tajikistan" style="height:78px; width:87px;" border="0"/>
<td>&nbsp;</td>
<td align="right" rowspan="3" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/WHO_Euro_logo.jpg" alt="logo WHO Euro" style="height:78px; width:60px;" border="0"/></td>
<td align="right" rowspan="3" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/gfatm_square.jpg" alt="logo GFATM" style="height:78px; width:83px;" border="0"/></td>
</tr>
<tr>
<td align="right" valign="center"><img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_en.jpg" alt="logo USAID" style="height:45px; width:150px;" border="0"/>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
</table></h2>

<br/>
<table>
	<tr>
		<td valign='top'>

			
			<table class="indexInner" >
			
				<col width="160">
				<tr><td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
					<b class="boxHeaderTwo" nowrap style="padding:0px 0px 0px 0px;">
						<spring:message code="labmodule.dashboardTitle"/>&nbsp;&nbsp;
					</b>
				</td></tr>
				
				<tr><td>
					<openmrs:hasPrivilege privilege="View Lab Entry">
						<a href='javascript:;' onclick='showLabEntry()'><spring:message code="labmodule.labDataEntry" /></a><br/>
					</openmrs:hasPrivilege>
					
					<openmrs:hasPrivilege privilege="View Lab Search">	
						<a href='javascript:;' onclick='showLabSearch()'><spring:message code="labmodule.labSearch" /></a><br/>
					</openmrs:hasPrivilege>
				</td></tr>
			
			</table>
		</td>
		
		<td id="mainFrame" valign='top'>
			
			<div hidden id="labEntry">
		
					<openmrs:portlet id="labFindPatient" url="labFindPatient" parameters="size=full|postURL=${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form|showIncludeVoided=false|viewType=shortEdit" moduleId="labmodule"/>
				
					<br>
				
					<openmrs:portlet id="addSuspectForm" url="addSuspectForm" parameters="personType=patient|postURL=${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form|viewType=shortEdit" moduleId="labmodule"/>			
			</div>
			
			<span id="seperator" style="width:100%"><br><hr><br></span>
			
			<div hidden id="labSearch">	
	 				<openmrs:portlet id="labSearch" url="labSearch" parameters="size=full|postURL=${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form|showIncludeVoided=false|viewType=shortEdit" moduleId="labmodule"/> 
			</div>
			
		</td>
		
		<td valign='top' style="padding-left:30px;">
					
			<div hidden id="lastEncounters">
			
					<openmrs:portlet id="lastThreeLabEncounters" url="lastThreeLabEncounters" parameters="size=full|postURL=${pageContext.request.contextPath}/module/labmodule/lab/labEntry.form|showIncludeVoided=false|viewType=shortEdit" moduleId="labmodule"/> 
				
			</div>
			
		</td>
		
	</tr>
	
	<tr>
		
	</tr>
</table>


<br>&nbsp;<br>

<%@ include file="dotsFooter.jsp"%>

<script type='text/javascript'>

	function showLabEntry()
	{
		document.getElementById('seperator').style.display = "none";
		document.getElementById('labSearch').style.display = "none"; 
		document.getElementById('labEntry').style.display = "block";
		document.getElementById('lastEncounters').style.display = "block";
		document.getElementById("mainFrame").style.width = "50%";
	}

	function showLabSearch()
	{
		document.getElementById('seperator').style.display = "none";
		document.getElementById('labEntry').style.display = "none"; 
		document.getElementById('labSearch').style.display = "block";
		document.getElementById('lastEncounters').style.display = "none";
		document.getElementById("mainFrame").style.width = "80%";
	}
	
</script>
