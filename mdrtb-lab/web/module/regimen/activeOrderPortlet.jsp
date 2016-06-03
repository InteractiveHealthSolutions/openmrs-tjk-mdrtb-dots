<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<table width="100%">
	<c:forEach items="${regimenHistoryGroups}" var="group">
		<c:if test="${empty type || type == group.key}">
			<c:set var="regimen" value="${group.value.activeRegimen}"/>
			<tr>
				<td class="groupStyle" colspan="8" style="margin-top:5px;"><spring:message code="mdrtb.treatment.${group.key}"/></td>
			</tr>
			<c:choose>
				<c:when test="${empty regimen.drugOrders && empty group.value.futureDrugOrders}">
					<tr><td class="cellStyle" style="padding-left:10px;" colspan="8"><spring:message code="mdrtb.none"/></td></tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
						<!-- COMMENTED FOR TAJIKISTAN -->
						<!-- <th class="headerStyle"><spring:message code="mdrtb.formulation" text="Formulation"/></th> -->
						<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.frequency" text="Frequency"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.startdate" text="Start Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.scheduledStopDate" text="Scheduled Stop Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.instructions" text="Instructions"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.alerts" text="Alerts"/></th>
					</tr>
					<c:forEach items="${openmrs:sort(group.value.futureDrugOrders, 'startDate', true)}" var="drugOrder">
						<tr>
							<td class="indentedCellStyle future">
								<a href="editDrugOrder.form?patientId=${patient.patientId}&patientProgramId=${patientProgramId}&type=${group.key}&orderId=${drugOrder.orderId}">
									${drugOrder.concept.name.name}
								</a>
							</td>
							<!--  COMMENTED FOR TAJIKISTAN -->
							<!-- <td class="cellStyle future">${drugOrder.drug.name}</td> -->
							<td class="cellStyle future">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle future">${drugOrder.frequency}</td>
							<td class="cellStyle future"><openmrs:formatDate date="${drugOrder.startDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle future"><openmrs:formatDate date="${drugOrder.autoExpireDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle future">${drugOrder.instructions}</td>
							<td class="cellStyle future drugAlert" style="width:100%">${drugAlerts[drugOrder]}</td>
						</tr>
					</c:forEach>
					<c:forEach items="${openmrs:sort(regimen.drugOrders, 'startDate', true)}" var="drugOrder">
						<tr>
							<td class="indentedCellStyle">
								<a href="editDrugOrder.form?patientId=${patient.patientId}&patientProgramId=${patientProgramId}&type=${group.key}&orderId=${drugOrder.orderId}">
									${drugOrder.concept.name.name}
								</a>
							</td>
							<!--  COMMENTED FOR TAJIKISTAN -->
							<!-- <td class="cellStyle">${drugOrder.drug.name}</td> -->
							<td class="cellStyle">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle">${drugOrder.frequency}</td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.startDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.autoExpireDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle">${drugOrder.instructions}</td>
							<td class="cellStyle future drugAlert" style="width:100%">${drugAlerts[drugOrder]}</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr><td colspan="8">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
</table>