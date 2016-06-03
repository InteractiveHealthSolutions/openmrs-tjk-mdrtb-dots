<%@ include file="/WEB-INF/view/module/mdrtb/include.jsp"%>

<table width="100%">
	<c:forEach items="${regimenHistoryGroups}" var="group">
		<c:if test="${empty type || type == group.key}">
			<c:set var="history" value="${group.value}"/>
			<tr>
				<td class="groupStyle" colspan="4" style="margin-top:5px;"><spring:message code="mdrtb.treatment.${group.key}"/></td>
			</tr>
			<c:choose>
				<c:when test="${empty history.allRegimens}"><tr><th colspan="4"><spring:message code="mdrtb.none"/></th></tr></c:when>
				<c:otherwise>
					<tr>
						<th class="headerStyle"><spring:message code="mdrtb.regimen" text="Regimen"/></th>
						<c:if test="${!empty history.type.reasonForStartingQuestion}">
							<th class="headerStyle" width="100%"><spring:message code="mdrtb.treatmentType" text="Type"/></th>
						</c:if>
					</tr>
					<c:forEach items="${mdrtb:reverse(history.allRegimens)}" var="regimen">
						<c:set var="isFuture" value="${mdrtb:isFutureDate(regimen.startDate)}"/>
						<c:set var="changeStyle" value="${isFuture ? 'cellStyle future' : 'cellStyle'}"/>
						<tr>
							<td class="${changeStyle}">
								<a href="editRegimen.form?patientId=${patient.patientId}&patientProgramId=${patientProgramId}&type=${group.key}&changeDate=${mdrtb:formatDateDefault(regimen.startDate)}">
									${mdrtb:formatDate(regimen.startDate, '${_dateFormatDisplay}')}
								</a>
							</td>
							<td class="${changeStyle}"><mdrtb:format obj="${regimen}" separator=" + "/></td>
							<c:if test="${!empty history.type.reasonForStartingQuestion}">
								<td class="${changeStyle}"><mdrtb:format obj="${regimen.reasonForStarting}"/></td>
							</c:if>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr><td colspan="4">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
</table>
