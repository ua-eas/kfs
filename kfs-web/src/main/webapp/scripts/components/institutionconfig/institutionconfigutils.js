let moveElement = function(list, fromIndex, toIndex) {
    let movingElement = list.get(fromIndex);
    let updatedList = list.delete(fromIndex).splice(toIndex, 0, movingElement);
    return updatedList;
};

let buildSortableDropHandler = function(elementId, component, sortableElementsPropertyName, updatingFunctionPropertyName) {
    let ele = $("#"+elementId);
    if (ele) {
        ele.sortable({
            start: function (event, ui) {
                $(ui.item).data("startindex", ui.item.index());
            },
            update: function (event, ui) {
                let startIndex = ui.item.data("startindex");
                let newIndex = ui.item.index();
                if (newIndex != startIndex) {
                    let updatedLinkGroups = moveElement(component.props[sortableElementsPropertyName], startIndex, newIndex);
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

module.exports = {buildSortableDropHandler: buildSortableDropHandler, isScrolledIntoView: isScrolledIntoView};