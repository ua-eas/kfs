<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2016 The Kuali Foundation
   -
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   -
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   -
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="bodySelector" required="false" description="The selector used to find the body element." %>
<c:if test="${empty bodySelector}">
    <c:set var="bodySelector" value="div.inquiry"/>
</c:if>

<script type="text/javascript">
    var buttonSelector = "#globalbuttons";
    var bodySelector = "${bodySelector}";
    $(document).ready(function() {
        // Set initial button state
        var buttonsLocation = $(buttonSelector).offset().top + $(buttonSelector).outerHeight();
        keepButtonsFixed();

        // Modify button state as we scroll
        $(window).scroll(function() {
            keepButtonsFixed();
        });

        $(window).resize(function() {
            keepButtonsFixed();
        });

        $('#expandAll, #collapseAll, .toggle-show-tab').click(function() {
            tabsChanged();
        });

        function keepButtonsFixed() {
            var buttonContainerWidth = $(bodySelector).outerWidth();
            $(buttonSelector).css('width', buttonContainerWidth + 40);

            var buttonsAreFixed = $(buttonSelector).hasClass('fixed');
            var windowLocation = $(window).scrollTop() + $(window).height();
            if (windowLocation < buttonsLocation && !buttonsAreFixed) {
                $(buttonSelector).addClass('fixed');
                $(bodySelector).addClass('fixedButtons');
            } else if (windowLocation >= buttonsLocation && buttonsAreFixed) {
                $(buttonSelector).removeClass('fixed');
                $(bodySelector).removeClass('fixedButtons');
            }
        }

        function tabsChanged() {
            $(buttonSelector).removeClass('fixed');
            $(bodySelector).removeClass('fixedButtons');

            buttonsLocation = $(buttonSelector).offset().top + $(buttonSelector).outerHeight();
            keepButtonsFixed();
        }
    })
</script>
