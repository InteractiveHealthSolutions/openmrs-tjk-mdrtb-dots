<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<table width="100%">
	<c:forEach items="${regimenHistoryGroups}" var="group">
		<c:if test="${empty type || type == group.key}">
			<tr>
				<td class="groupStyle" colspan="7" style="margin-top:5px;"><spring:message code="mdrtb.treatment.${group.key}"/></td>
			</tr>
			<c:choose>
				<c:when test="${empty group.value.pastDrugOrders}">
					<tr><td class="cellStyle" style="padding-left:10px;" colspan="7"><spring:message code="mdrtb.none"/></td></tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.drug" text="Drug"/></th>
						<!-- COMMENTED FOR TAJIKISTAN -->
						<!-- <th class="headerStyle"><spring:message code="mdrtb.formulation" text="Formulation"/></th> -->
						<th class="headerStyle"><spring:message code="mdrtb.dose" text="Dose"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.startdate" text="Start Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.autoExpireDate" text="Scheduled Stop Date"/></th>
						<th class="headerStyle"><spring:message code="mdrtb.actualStopDate" text="Actual Stop Date"/></th>
						<th class="headerStyle" width="100%"><spring:message code="mdrtb.reasonDiscontinued" text="Reason Discontinued"/></th>
					</tr>
					<c:forEach items="${openmrs:sort(group.value.pastDrugOrders, 'startDate', false)}" var="drugOrder">
						<tr>
							<td class="indentedCellStyle">
								<a href="editDrugOrder.form?patientId=${patient.patientId}&patientProgramId=${patientProgramId}&type=${group.key}&orderId=${drugOrder.orderId}">
									${drugOrder.concept.name.name}
								</a>
							</td>
							<!-- COMMENTED FOR TAJIKISTAN -->
							<!-- <td class="cellStyle">${drugOrder.drug.name}</td>  -->
							<td class="cellStyle">
								<c:if test="${!empty drugOrder.dose}">
									${drugOrder.dose} ${drugOrder.units}
								</c:if>
							</td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.startDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle"><openmrs:formatDate date="${drugOrder.autoExpireDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellStyle"><openmrs:formatDate date="${empty drugOrder.discontinuedDate ? drugOrder.autoExpireDate : drugOrder.discontinuedDate}" format="${_dateFormatDisplay}"/></td>
							<td class="cellWrappable"><mdrtb:format obj="${drugOrder.discontinuedReason}"/></td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr><td colspan="7">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
</table>
