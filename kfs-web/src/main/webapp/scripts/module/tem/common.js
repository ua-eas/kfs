/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function checkPersonal(line) {
    var baseElement = "document.perDiemExpenses[" + line + "]";
    //get the value of the checkbox first (are we checking or unchecking?)
    var personal = document.getElementById(baseElement + ".personal");

    // fields
    var perDiem = document.getElementById(baseElement + '.mealsAndIncidentals');
    var lodging = document.getElementById(baseElement + '.lodging');
    var miles = document.getElementById(baseElement + ".miles");
    var mileageRate = document.getElementById(baseElement + ".mileageRateId");
    var dailyTotal = document.getElementById(baseElement + ".total");
    var mileageTotal = document.getElementById(baseElement + ".mileageTotal");
    var breakfast = document.getElementById(baseElement + '.breakfast');
    var lunch = document.getElementById(baseElement + '.lunch');
    var dinner = document.getElementById(baseElement + '.dinner');
    var breakfastValue = document.getElementById(baseElement + '.breakfastValue');
    var lunchValue = document.getElementById(baseElement + '.lunchValue');
    var dinnerValue = document.getElementById(baseElement + '.dinnerValue');
    var incidentalsValue = document.getElementById(baseElement + '.incidentalsValue');

    //original values
    var perDiemOrig = document.getElementById(baseElement + '.mealsAndIncidentals.holder');
    var lodgingOrig = document.getElementById(baseElement + '.lodging.holder');
    var milesOrig = document.getElementById(baseElement + ".miles.holder");
    var mileageRateOrig = document.getElementById(baseElement + ".mileageRateId.holder");
    var dailyTotalOrig = document.getElementById(baseElement + '.total.holder');
    var mileageTotalOrig = document.getElementById(baseElement + '.mileageTotal.holder');
    var breakfastValueOrig = document.getElementById(baseElement + '.breakfastValue.holder');
    var lunchValueOrig = document.getElementById(baseElement + '.lunchValue.holder');
    var dinnerValueOrig = document.getElementById(baseElement + '.dinnerValue.holder');
    var incidentalsValueOrig = document.getElementById(baseElement + '.incidentalsValue.holder');

    try {
        if (personal.checked) {
            dailyTotal.innerHTML = "0.00";
            mileageTotal.innerHTML = "0.00";

            if (perDiem != null) {
                perDiem.innerHTML = "0.00";
            }

            if (lodging != null) {
                lodging.value = "0.00";
                lodging.disabled = true;
            }

            if (miles != null) {
                miles.value = "0";
                miles.disabled = true;
                mileageRate.disabled = true;
            }

            if (breakfast && lunch && dinner) {
                breakfast.disabled = true;
                lunch.disabled = true;
                dinner.disabled = true;
            }

            if (breakfastValue != null) {
                breakfastValue.value = "0.00";
                breakfastValue.disabled = true;
            }

            if (lunchValue != null) {
                lunchValue.value = "0.00";
                lunchValue.disabled = true;
            }

            if (dinnerValue != null) {
                dinnerValue.value = "0.00";
                dinnerValue.disabled = true;
            }

            if (incidentalsValue != null) {
                incidentalsValue.value = "0.00";
                incidentalsValue.disabled = true;
            }

        } else {
            dailyTotal.innerHTML = dailyTotalOrig.value;
            mileageTotal.innerHTML = mileageTotalOrig.value;

            if (perDiem != null) {
                perDiem.innerHTML = perDiemOrig.value.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
            }

            if (lodging != null) {
                lodging.value = lodgingOrig.value;
                lodging.disabled = false;
            }

            if (miles != null) {
                miles.value = milesOrig.value;
                if (mileageRateOrig.value != '') {
                    mileageRate.value = mileageRateOrig.value;
                } else {
                    try {
                        mileageRate.selectedIndex = 0;
                    } catch (err) {
                        console.log(err)
                    }
                    ;
                }
                miles.disabled = false;
                mileageRate.disabled = false;
            }

            if (breakfast && lunch && dinner) {
                breakfast.disabled = false;
                lunch.disabled = false;
                dinner.disabled = false;
            }

            if (breakfastValue != null) {
                breakfastValue.value = breakfastValueOrig.value;
                breakfastValue.disabled = false;
            }

            if (lunchValue != null) {
                lunchValue.value = lunchValueOrig.value;
                lunchValue.disabled = false;
            }

            if (dinnerValue != null) {
                dinnerValue.value = dinnerValueOrig.value;
                dinnerValue.disabled = false;
            }

            if (incidentalsValue != null) {
                incidentalsValue.value = incidentalsValueOrig.value;
                incidentalsValue.disabled = false;
            }
        }
    } catch (err) {
        console.log(err)
    }
    ;
}

function checkDirectBilled(expenseType) {
    var expenseObject = document.getElementById(expenseType + ".travelCompanyCodeCode");
    var directBilled = document.getElementById(expenseType + ".nonReimbursable");

    if ((directBilled != null || directBilled != undefined) && (expenseObject != null || expenseObject != undefined)) {
        if (expenseObject.value.toLowerCase().charAt(0) == 'p') {
            directBilled.checked = true;
        } else {
            directBilled.checked = false;
        }
    }
}

function getAllStates() {
    if (document.getElementById("document.traveler.countryCode") != null) {
        var countryCode = document.getElementById("document.traveler.countryCode").value;
        var dwrReply = {
            callback: function (data) {
                var stateCodes = data.split(";");
                var stateObj = document.getElementById("document.traveler.stateCode");
                var selected = stateObj.options[stateObj.selectedIndex].value;
                stateObj.length = 0;
                for (i = 0; i < stateCodes.length - 1; i++) {
                    stateObj.options[i] = new Option(stateCodes[i], stateCodes[i]);
                }
                stateObj.value = selected;
            },
            errorHandler: function (errorMessage) {
                window.status = errorMessage;
            }
        };
        TravelDocumentService.getAllStates(countryCode, dwrReply);
    }

}

function update_travelerTypeCode() {
    if (document.getElementById("newGroupTravelerLine.travelerTypeCode") != null) {
        var travelerType = document.getElementById("newGroupTravelerLine.travelerTypeCode").value;
        var dwrReply = {
            callback: function (data) {
                var person = document.getElementById("personLookupButton");
                var customer = document.getElementById("customerLookupButton");
                var personLabel = document.getElementById("personLabel");
                var customerLabel = document.getElementById("customerLabel");
                var name = document.getElementById("newGroupTravelerLine.name");
                if (data) {
                    customer.style.display = "inline";
                    person.style.display = "none";
                    customerLabel.style.display = "block";
                    personLabel.style.display = "none";
                    name.disabled = false;
                } else {
                    customer.style.display = "none";
                    person.style.display = "inline";
                    customerLabel.style.display = "none";
                    personLabel.style.display = "block";
                    name.disabled = true;
                }
            },
            errorHandler: function (errorMessage) {
                window.status = errorMessage;
            }
        };
        TravelDocumentService.checkNonEmployeeTravelerTypeCode(travelerType, dwrReply);
    }
}

function copy_payeeToHostName(payeeFirstName, payeeLastName) {
    var hostAsPayee = document.getElementById("document.hostAsPayee").checked;
    if (hostAsPayee == true) {
        alert("Clicking OK, will update the Event Host with the Payee's Name.");
        document.getElementById("document.hostName").value = payeeFirstName + " " + payeeLastName;
    }
    else {
        alert("Clicking OK, will clear the Event Host Name.");
        document.getElementById("document.hostName").value = "";
    }
}

function check_nonReimbursable() {
    var expenseType = document.getElementById("newActualExpenseLine.travelExpenseTypeCodeId").value;
    var dwrReply = {
        callback: function (data) {
            document.getElementById("newActualExpenseLine.nonReimbursable").checked = data;
        },
        errorHandler: function (errorMessage) {
            window.status = errorMessage;
        }
    };
    TravelAuthorizationService.checkNonReimbursable(expenseType, dwrReply);
}

function disableExpenseAmount(expense) {
    var objName = expense.id.replace("travelCompanyCodeCode", "") + "expenseAmount";
    if (expense.value == "MM") {
        document.getElementById(objName).disabled = true;
        document.getElementById(objName).value = "0.00";
    }
    else {
        document.getElementById(objName).disabled = false;
    }
}

function updateMileage(mileageObjectID) {
    var id = mileageObjectID.replace(".miles", "");
    var lineNumber = id.split("[")[1];
    lineNumber = lineNumber.split("]")[0];
    var amount = document.getElementById("div_document.actualExpenses[" + lineNumber + "].expenseAmount").innerHTML;
    amount = parseFloat(amount);
    var convertedAmount = document.getElementById("div_document.actualExpenses[" + lineNumber + "].convertedAmount").innerHTML;
    convertedAmount = parseFloat(convertedAmount);
    var rate = document.getElementById("div_document.actualExpenses[" + lineNumber + "].currencyRate").innerHTML;
    rate = parseFloat(rate);
    //alert("amount:" + amount + "\n" + "convertedAmount:" + convertedAmount + "\n" + "rate:" + rate + "\n");
    if (!isNaN(document.getElementById(mileageObjectID).value)) {
        var label = document.getElementById(id + ".mileageRateId")[document.getElementById(id + ".mileageRateId").selectedIndex].innerHTML;
        var mileageRate = label.split("$")[1];
        if (isNaN(mileageRate)) {
            mileageRate = 0.00;
        }
        else {
            mileageRate = parseFloat(mileageRate);
        }
        var miles = document.getElementById(mileageObjectID).value;
        if (isNaN(miles)) {
            miles = 0;
        }
        else {
            miles = parseInt(miles);
        }
        //alert("miles:" + miles + "\n" + "mileageRate:" + mileageRate + "\n" + "rate:" + rate + "\n");

        total = miles * mileageRate
        document.getElementById("div_" + id + ".expenseAmount").innerHTML = padZeros(roundNumber(total, 2));
        document.getElementById("div_" + id + ".convertedAmount").innerHTML = padZeros(roundNumber(total, 2));

        var counter = 0;
        var detailTotal = 0;
        var obj = document.getElementById("div_document.actualExpenses[" + lineNumber + "].expenseDetails[" + counter + "].expenseAmount");
        while (obj != null || obj != undefined) {
            var tempAmount = parseFloat(obj.innerHTML);
            detailTotal += tempAmount;
            counter++;
            obj = document.getElementById("div_document.actualExpenses[" + lineNumber + "].expenseDetails[" + counter + "].expenseAmount");
        }

        if (id.indexOf("document") == -1) {
            document.getElementById("div_document.actualExpenses[" + lineNumber + "].expenseAmount").innerHTML = padZeros(roundNumber(total + detailTotal, 2));
            document.getElementById("div_document.actualExpenses[" + lineNumber + "].convertedAmount").innerHTML = padZeros(roundNumber((total + detailTotal), 2));
        }
        else {
            document.getElementById("div_document.actualExpenses[" + lineNumber + "].expenseAmount").innerHTML = padZeros(roundNumber(detailTotal, 2));
            document.getElementById("div_document.actualExpenses[" + lineNumber + "].convertedAmount").innerHTML = padZeros(roundNumber(detailTotal, 2));
        }
    }
}

function roundNumber(num, dec) {
    var result = Math.round(num * Math.pow(10, dec)) / Math.pow(10, dec);
    return result;
}

function padZeros(amount) {
    var str = amount + "";
    var temp = str.split(".");
    if (str.indexOf(".") == -1) {
        return amount + ".00";
    }
    else {
        if (temp[1].length == 1) {
            return amount + "0";
        }
        else {
            return amount;
        }
    }
}

var recalculateDistributionRemainingAmount = function () {
    var numOfDistributions = document.getElementById("accountDistributionSize").value;
    var total = 0;
    var distributedTotal = 0;
    for (var i = 0; i < numOfDistributions; i++) {
        var distributionName = "distribution[" + i + "]";
        var distributionSelected = document.getElementById(distributionName + ".selected").checked;
        if (distributionSelected) {
            var subTotal = parseFloat(document.getElementById(distributionName + ".subTotal").value);
            var remainingAmount = parseFloat(document.getElementById(distributionName + ".remainingAmount").value);
            total += remainingAmount;
            distributedTotal += (subTotal - remainingAmount);
        }
    }

    var selectedDistributionAmount = total;
    var expenseLimit = parseFloat(document.getElementById("document.expenseLimit").value);
    if (expenseLimit && expenseLimit > 0) {
        if (distributedTotal > expenseLimit) {
            selectedDistributionAmount = 0;
        } else if (expenseLimit < total) {
            selectedDistributionAmount = expenseLimit - distributedTotal;
        }
    }

    var selectedDistributionAmountControl = document.getElementById("selectedDistributionAmount");
    selectedDistributionAmountControl.value = selectedDistributionAmount;

    // now loop through the assign account accounting lines and update their amounts
    var newDistributionLineAmount = kualiElements["accountDistributionnewSourceLine.amount"];
    if (newDistributionLineAmount) {
        newDistributionLineAmount.value = selectedDistributionAmount;
        updatePercent("accountDistributionnewSourceLine.amount");
    }
    var count = 0;
    var distributionLineAmount = kualiElements["accountDistributionsourceAccountingLines[" + count + "].amount"];
    while (distributionLineAmount) {
        updatePercent("accountDistributionsourceAccountingLines[" + count + "].amount");
        count += 1;
        distributionLineAmount = kualiElements["accountDistributionsourceAccountingLines[" + count + "].amount"];
    }
}

function updatePercent(amountField) {
    var currentAmount = parseFloat(document.getElementById("selectedDistributionAmount").value);
    if (currentAmount != 0) {
        var thisFieldAmount = parseFloat(document.getElementById(amountField).value);
        if (isNaN(thisFieldAmount)) {
            thisFieldAmount = 0.00;
            document.getElementById(amountField).value = "0.00";
        }
        var strName = amountField.split(".amount");
        var percentField = document.getElementById(strName[0] + ".accountLinePercent");
        var percent = thisFieldAmount * 100 / currentAmount;
        percent = roundNumber(percent, 5);
        percentField.value = percent;
        //if this is called from the new line, do not update itself again base on the collections
        if (amountField.indexOf("[") != -1) {
            var success = updateNewAssignAccountsPercentAmount(currentAmount);
            if (!success) {
                var newAmount = parseFloat(document.getElementById("accountDistributionnewSourceLine.amount").value);
                var newPercent = parseFloat(document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value);
                document.getElementById(amountField).value = thisFieldAmount + newAmount;
                percentField.value = percent + newPercent;
                document.getElementById("accountDistributionnewSourceLine.amount").value = "0.00";
                document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = "0.00";
            }
        }
    }
}
function updateAmount(percentField) {
    var currentAmount = parseFloat(document.getElementById("selectedDistributionAmount").value);
    var thisFieldPercent = parseFloat(document.getElementById(percentField).value);
    thisFieldPercent = roundNumber(thisFieldPercent, 5);
    if (isNaN(thisFieldPercent)) {
        thisFieldAmount = 0.00;
        document.getElementById(amountField).value = "0.00";
    }
    var strName = percentField.split(".accountLinePercent");
    var amountField = document.getElementById(strName[0] + ".amount");
    var amount = thisFieldPercent * currentAmount / 100;
    amount = roundNumber(amount, 2);
    amountField.value = amount;
    //if this is called from the new line, do not update itself again base on the collections
    if (percentField.indexOf("[") != -1) {
        var success = updateNewAssignAccountsPercentAmount(currentAmount);
        if (!success) {
            var newAmount = parseFloat(document.getElementById("accountDistributionnewSourceLine.amount").value);
            var newPercent = parseFloat(document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value);
            document.getElementById(percentField).value = thisFieldPercent + newPercent;
            amountField.value = amount + newAmount;
            document.getElementById("accountDistributionnewSourceLine.amount").value = "0.00";
            document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = "0.00";
        }
    }
}
function updateNewAssignAccountsPercentAmount(currentAmount) {
    var totalAmount = 0.00;
    var totalPercent = 0.00;
    var counter = 0;
    var fieldPercent = document.getElementById("accountDistributionsourceAccountingLines[" + counter + "].accountLinePercent");
    var fieldAmount = document.getElementById("accountDistributionsourceAccountingLines[" + counter + "].amount");

    while (fieldAmount != null) {
        if (isNaN(parseFloat(fieldAmount.value))) {
            fieldAmount.value = "0.00";
        } else {
            totalAmount += parseFloat(fieldAmount.value);
        }
        if (isNaN(parseFloat(fieldPercent.value))) {
            fieldAmount.value = "0.00";
        } else {
            totalPercent += parseFloat(fieldPercent.value);
        }
        counter++;
        fieldPercent = document.getElementById("accountDistributionsourceAccountingLines[" + counter + "].accountLinePercent");
        fieldAmount = document.getElementById("accountDistributionsourceAccountingLines[" + counter + "].amount");
    }
    //if there is any change from the existing assign accounting lines
    if (totalAmount != "0.00") {
        document.getElementById("accountDistributionnewSourceLine.amount").value = roundNumber(currentAmount - totalAmount, 2);
        document.getElementById("accountDistributionnewSourceLine.accountLinePercent").value = roundNumber(100 - totalPercent, 5);
    }
    return currentAmount - totalAmount >= 0;
}
