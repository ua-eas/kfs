import Immutable from 'immutable';


let moveLinkElement = function(list, fromIndex, toIndex, fromType, toType) {
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

let buildLinkSortableDropHandler = function(elementId, connectWithClass, component, sortableElementsPropertyName, updatingFunctionPropertyName) {
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
                        component.context[updatingFunctionPropertyName](updatedLinkGroups);
                    }
                }
            }
        });
        ele.disableSelection();
    }
};

let moveGroupElement = function(list, fromIndex, toIndex) {
    let movingElement = list.get(fromIndex);
    let updatedList = list.delete(fromIndex).splice(toIndex, 0, movingElement);

    return updatedList;
};

let buildGroupSortableDropHandler = function(elementId, component, sortableElementsPropertyName, updatingFunctionPropertyName) {
    let ele = $("#"+elementId);
    if (ele) {
        ele.sortable({
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
                    component.context[updatingFunctionPropertyName](updatedLinkGroups);
                }
            }
        });
        ele.disableSelection();
    }
};

let isScrolledIntoView = function (elem) {
    var $elem = $(elem);
    var $window = $(window);

    var docViewTop = $window.scrollTop();
    var docViewBottom = docViewTop + $window.height();

    var elemTop = $elem.offset().top;
    var elemBottom = elemTop + $elem.height();

    return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
};

module.exports = {
    buildLinkSortableDropHandler: buildLinkSortableDropHandler,
    buildGroupSortableDropHandler: buildGroupSortableDropHandler,
    isScrolledIntoView: isScrolledIntoView};