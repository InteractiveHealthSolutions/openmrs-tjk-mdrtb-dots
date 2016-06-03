<%@ include file="/WEB-INF/view/module/dotsreports/include.jsp"%>
<%@ include file="../dotsHeader.jsp"%>

<style type="text/css">
	table.resultsTable {
		border-collapse: collapse;
	}
	table.resultsTable td, table.resultsTable th {
		border-top: 1px black solid;
		border-bottom: 1px black solid;
	}
</style>

<button onClick="window.location = 'simpleUsage.list'">Another calculation</button>
<br/>

<table width="100%">
	<tr>
		<td bgcolor="#e0e0ff">
			Drug Usage Calculation for
			<b><openmrs:format concept="${drugSet}"/></b>
			for
			<b>${cohort.description}</b>
			<b>(${cohort.size} patients)</b>
		</td>
	</tr>
	<tr>
		<td bgcolor="#e0e0ff">
			from <b><openmrs:formatDate date="${fromDate}"/></b>
			to <b><openmrs:formatDate date="${toDate}"/></b>
			(<b>${duration} days</b>)
		</td>
	</tr>
</table>

<br/>

<table class="resultsTable" cellspacing="0" cellpadding="2">
	<tr>
		<th colspan="2">Medication</th>
		<th>Average<br/>Daily Usage</th>
		<th>Total Quantity<br/>Required</th>
		<th>Price<br/>per unit</th>
		<th>Cost for<br/>this drug</th>
	</tr>
	<c:forEach var="row" items="${usage}" varStatus="status">
		<tr>
			<td><b>${row.drug.name}</b></td>
			<td><small>${row.drug.concept.name} ${row.drug.doseStrength} ${row.drug.units} ${row.drug.dosageForm}</small></td>
			<td align="right">${row.dailyUsage}</td>
			<td align="right"><b>${row.totalUsage}</b></td>
			<td align="center">x $<input class="priceBox" id="price_of_${status.count}" type="text" size="5" onChange="calculateCosts()"/> =</td>
			<td align="right">$<span class="costBox" id="cost_of_${status.count}"/></td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="6" align="right">Total cost: $<span class="costBox" id="total_cost"/></td>
	</tr>
</table>

<script type="text/javascript">
	function calculateCosts() {
		var runningTotal = 0;
		<c:forEach var="row" items="${usage}" varStatus="status">
			tmp = document.getElementById('price_of_${status.count}').value;
			tmp *= ${row.totalUsage};
			document.getElementById('cost_of_${status.count}').innerHTML = Math.round(tmp);
			runningTotal += tmp;
		</c:forEach>
		document.getElementById('total_cost').innerHTML = Math.round(runningTotal);
	}
</script>

