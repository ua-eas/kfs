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
import Immutable from 'immutable';


function moveLinkElement(list, fromIndex, toIndex, fromType, toType) {
    let movingType = list.get(fromType);
    let movingElement = movingType.get(fromIndex);

    let updatedList;
    if (toType === fromType) {
        let updatedType = movingType.delete(fromIndex).splice(toIndex, 0, movingElement);
        updatedList = list.set(fromType, updatedType);
    } else {
        let updatedMoveFromType = movingType.delete(fromIndex);

        let moveToType = list.get(toType);
        if (!moveToType) {
            moveToType = Immutable.fromJS([]);
        }
        let updatedMoveToType = moveToType.splice(toIndex, 0, movingElement);

        let partialUpdatedList = list.set(fromType, updatedMoveFromType);
        updatedList = partialUpdatedList.set(toType, updatedMoveToType);
    }
    return updatedList;
};

export function buildLinkSortableDropHandler(elementId, connectWithClass, component, sortableElementsPropertyName, updatingFunctionPropertyName, groupLabel) {
    let ele = $("#"+elementId);
    if (ele) {
        ele.sortable({
            start: function (event, ui) {
                $(ui.item).data("startindex", ui.item.index());
            },
            connectWith: connectWithClass,
            update: function (event, ui) {
                let startIndex = ui.item.data("startindex");
                let newIndex = ui.item.index();
                if (!ui.sender) {
                    let startType = component.props.type;
                    let newType = $(ui.item).closest('ul').attr('data-type');
                    if (newIndex !== startIndex || startType !== newType) {
                        let updatedLinkGroups = moveLinkElement(component.props[sortableElementsPropertyName], startIndex, newIndex, startType, newType);
                        $("#" + elementId).sortable('cancel');
                        component.props.stateMaintenance[updatingFunctionPropertyName](updatedLinkGroups, groupLabel);
                    }
                }
            }
        });
        ele.disableSelection();
    }
};

function moveGroupElement(list, fromIndex, toIndex) {
    let movingElement = list.get(fromIndex);
    let updatedList = list.delete(fromIndex).splice(toIndex, 0, movingElement);

    return updatedList;
};

export function buildGroupSortableDropHandler(elementId, component, sortableElementsPropertyName, updatingFunctionPropertyName) {
    let ele = $("#"+elementId);
    if (ele) {
        ele.sortable({
            items: 'li:not(.new)',
            start: function (event, ui) {
                $(ui.item).data("startindex", ui.item.index());
            },
            update: function (event, ui) {
                event.stopPropagation();
                let startIndex = ui.item.data("startindex");
                let newIndex = ui.item.index();
                if (newIndex != startIndex) {
                    let updatedLinkGroups = moveGroupElement(component.props[sortableElementsPropertyName], startIndex, newIndex);
                    $("#"+elementId).sortable('cancel');
                    component.props.stateMaintenance[updatingFunctionPropertyName](updatedLinkGroups);
                }
            }
        });
        ele.disableSelection();
    }
};

export function isScrolledIntoView(elem) {
    var $elem = $(elem);
    var $window = $(window);

    var docViewTop = $window.scrollTop();
    var docViewBottom = docViewTop + $window.height();

    var elemTop = $elem.offset().top;
    var elemBottom = elemTop + $elem.height();

    return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
};

export function validateForm(label, link) {
    let errors = [];
    let errorMessages = [];
    if (!label.trim()) {
        errors.push('label');
        errorMessages.push('Link Name cannot be blank');
    }

    if (!link.trim()) {
        errors.push('link');
        errorMessages.push('URL cannot be blank');
    }

    if (link.indexOf('http://') != 0 && link.indexOf('https://') != 0) {
        errors.push('link');
        errorMessages.push('URL must be an absolute path (i.e. http:// or https://)');
    }

    return {errors: errors, errorMessages: errorMessages};
};

const InstitutionConfigUtils = {
    buildLinkSortableDropHandler,
    buildGroupSortableDropHandler,
    isScrolledIntoView,
    validateForm
};

export default InstitutionConfigUtils;
