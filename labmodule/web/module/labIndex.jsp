<%@ include file="/WEB-INF/view/module/labmodule/include.jsp"%>
<%@ include file="dotsHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/labmodule/dotsreports.css"/>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/findPatient.htm" />

<h2><table width="100%">
<tr>

<td align="left">
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/TJK_logo.jpg" alt="logo Tajikistan" style="height:78px; width:87px;" border="0"/>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/WHO_Euro_logo.jpg" alt="logo WHO Euro" style="height:78px; width:60px;" border="0"/>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/gfatm_square.jpg" alt="logo GFATM" style="height:78px; width:83px;" border="0"/>
<img src="${pageContext.request.contextPath}/moduleResources/mdrtb/USAID_logo_en.jpg" alt="logo USAID" style="height:78px; width:259px;" border="0"/>
</td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td rowspan="3" align="left" rowspan="3" align="right" width="50%"><spring:message code="labmodule.title.homepage" /></td>
</tr>
</table></h2>


<br/>

<div id="index">
<table class="indexInner" >	
		<tr>
			<td style="background-color:#8FABC7;padding:2px 2px 2px 2px;">
				<b class="boxHeader" nowrap style="padding:0px 0px 0px 0px;">
					<spring:message code="labmodule.dashboardTitle"/>&nbsp;&nbsp;
				</b>
			</td>
		</tr>
		
		<tr>
			<td>
				<openmrs:hasPrivilege privilege="View Lab Entry">
					<a href='javascript:;' onclick='showLabEntry()'><spring:message code="labmodule.labDataEntry" /></a>
				</openmrs:hasPrivilege>
			</td>
			
			<td>	
				<openmrs:hasPrivilege privilege="View Lab Search">	
					<a href='javascript:;' onclick='showLabSearch()'><spring:message code="labmodule.labSearch" /></a>
				</openmrs:hasPrivilege>
			</td>
			
			<td>
				<a href="../labmodule/reporting/reports.form?type=org.openmrs.module.labmodule.reporting.data.LabReport"><spring:message code="labmodule.labReports" /></a>
			</td>
		</tr>
				
</table>
<br>
<br>
</div>
				
<table>		
	<tr>	
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
		document.getElementById("mainFrame").style.width = "60%";
	}

	function showLabSearch()
	{
		document.getElementById('seperator').style.display = "none";
		document.getElementById('labEntry').style.display = "none"; 
		document.getElementById('labSearch').style.display = "block";
		document.getElementById('lastEncounters').style.display = "none";
		document.getElementById("mainFrame").style.width = "80%";
	}

	function showLabReports()
	{

	}

	function hideAll()
	{
		document.getElementById('seperator').style.display = "none";
		document.getElementById('labEntry').style.display = "none"; 
		document.getElementById('labSearch').style.display = "none";
		document.getElementById('lastEncounters').style.display = "none";
	}
	
</script>

<openmrs:hasPrivilege privilege="View Lab Entry">
	<script  type="text/javascript">	
		showLabEntry();
		document.getElementById("index").style.display="none";	
	</script>
</openmrs:hasPrivilege>
<openmrs:hasPrivilege privilege="View Lab Search">
	<script  type="text/javascript">
		showLabSearch();
		document.getElementById("index").style.display="none";
	</script>
</openmrs:hasPrivilege>
<openmrs:hasPrivilege privilege="Manage Lab">
	<script  type="text/javascript">
		hideAll();
		document.getElementById("index").style.display="table";
	</script>
</openmrs:hasPrivilege>
