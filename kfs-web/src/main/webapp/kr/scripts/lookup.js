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
/**
 * Bind to click events on toggle buttons
 */
jQuery(function () {
    /**
     * Toggle the named input value between YES and NO, and then call customLookupChanged
     * to submit form to refresh.
     */
    function toggleSearchType(input_name) {
        var input = jQuery('input[name=' + input_name + ']');
        input.val(input.val() == "YES" ? "NO" : "YES");
        customLookupChanged();
    }

    jQuery("#toggleAdvancedSearch").click(function () {
        toggleSearchType("isAdvancedSearch");
    });
    jQuery("#toggleSuperUserSearch").click(function () {
        toggleSearchType("superUserSearch");
    });
    jQuery("#resetSavedSearch").click(function () {
        toggleSearchType("resetSavedSearch");
    });
});

/**
 * Called on an action that requires the lookup to be refreshed
 * Invokes performCustomAction.
 */
function customLookupChanged() {
    var methodToCallElement = document.createElement("input");
    methodToCallElement.setAttribute("type", "hidden");
    methodToCallElement.setAttribute("name", "methodToCall");
    methodToCallElement.setAttribute("value", "refresh");
    document.forms[0].appendChild(methodToCallElement);

    var refreshCallerElement = document.createElement("input");
    refreshCallerElement.setAttribute("type", "hidden");
    refreshCallerElement.setAttribute("name", "refreshCaller");
    refreshCallerElement.setAttribute("value", "customLookupAction");
    document.forms[0].appendChild(refreshCallerElement);

    document.forms[0].submit();
}
