
//******************************
// Adds a new drug entry row immediately before the sectionId element of a table
//******************************
function addDrug(sectionId, drug, genericOptions, /*drugOptions,*/ unitOptions, deleteButtonText, perDayText, perWeekText, suffix) {
	$hiddenOrderIndex = $j('<input type="hidden" name="newOrderKey"/>').val(suffix);
	$hiddenOrderId = $j('<input type="hidden" name="orderId:' + suffix + '"/>').val(drug.orderId);
	$genericSelector = $j('<select class="genericDrugInput" name="generic:' + suffix + '" onchange="limitDrug(this, \'drug'+suffix + '\');">' + genericOptions + '</select>').val(drug.generic);
	//$drugSelector = $j('<select id="drug' + suffix + '" name="drug:' + suffix + '">' + drugOptions + '</select>').val(drug.drugId);
	$doseSelector = $j('<input class="doseInput" type="text" size="6" name="dose:' + suffix + '"/>').val(drug.dose);
	$unitSelector = $j('<select name="unit:' + suffix + '">' + unitOptions + '</select>').val(drug.units);
	$daySelector = $j('<input type="text" size="5" name="perDay:' + suffix + '"/>');
	$dayText = $j('<span>').html(perDayText);
	$weekSelector = $j('<input type="text" size="5" name="perWeek:' + suffix + '"/>');
	$weekText = $j('<span>').html(perWeekText);
	$frequencySelector = $j('<input type="text" name="frequency:' + suffix + '"/>').val(drug.frequency);
	$endDateSelector = $j('<input type="text" size="10" tabIndex="-1" name="autoExpireDate:' + suffix + '" onFocus=\"showCalendar(this)\"/>').val(drug.autoExpireDate);
	$instructionsSelector = $j('<input type="text" name="instructions:' + suffix + '"/>').val(drug.instructions);
	$removeButton = $j('<input type="button" value=" ' + deleteButtonText + ' " onclick="removeElement(\'addDrugRow' + drug.drugId + '-' + suffix + '\');" />');
	
	$newRow = $j('<tr id="addDrugRow' + drug.drugId + '-' + suffix + '">');
	$newRow.append($hiddenOrderIndex);
	$newRow.append($hiddenOrderId);
	$newRow.append($j('<td>').html($genericSelector));
	//$newRow.append($j('<td>').html($drugSelector));
	$newRow.append($j('<td>').html($doseSelector).append($unitSelector));
	if (drug.frequency != null && drug.frequency != '') {
		$newRow.append($j('<td>').html($frequencySelector));
	}
	else {
		$dayWeekCell = $j('<td>');
		$dayWeekCell.append($daySelector);
		$dayWeekCell.append($dayText);
		$dayWeekCell.append($weekSelector);
		$dayWeekCell.append($weekText);
		$newRow.append($dayWeekCell);
	}
	$newRow.append($j('<td>').html(drug.startDate));
	$newRow.append($j('<td>').html($endDateSelector));
	$newRow.append($j('<td>').html($instructionsSelector));
	$newRow.append($j('<td>').html($removeButton));

	$j('#'+sectionId).before($newRow);
	
	// limit the drug selector for the valid drugs for that generic, and then set the default value
	limitDrug($genericSelector, 'drug'+suffix);
	//$drugSelector.val(drug.drugId);
}

// Removes the element with the passed id from the DOM
function removeElement(id) {
	$j('#'+id).remove();
}

function removeClass(elementId, className) {
	$j('#'+elementId).removeClass(className);
}

function addClass(elementId, className) {
	$j('#'+elementId).addClass(className);
}

// Limits the drug selector
function limitDrug(selectorElement, idOfDrugSelector) {
	$j('#'+idOfDrugSelector).hide();
	var genericId = $j(selectorElement).val();
	if (genericId != null && genericId != '') {
		if ($j('#'+idOfDrugSelector).find('.drugConcept'+genericId).size() > 0) {
			$j('#'+idOfDrugSelector).show();
			$j('#'+idOfDrugSelector + ' > option').hide();
			$j('#'+idOfDrugSelector + ' > .drugConcept'+genericId).show();
			$j('#'+idOfDrugSelector + ' > .unspecified').show();
			$j('#'+idOfDrugSelector).val('');
		}
	}
}

// Empty Function
function change() {}