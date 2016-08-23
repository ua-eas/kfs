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


<script type="text/javascript">
    $(document).ready(function() {
        // Set initial button state
        var buttonsLocation = $('td.infoline').offset().top + $('td.infoline').outerHeight()
        var buttonContainerWidth = $('td.infoline').outerWidth()
        keepButtonsFixed()

        // Modify button state as we scroll
        $(window).scroll(function() {
            keepButtonsFixed()
        })

        $(window).resize(function() {
            keepButtonsFixed()
        })

        function keepButtonsFixed() {
            var multilineButtons = $('td.infoline').hasClass('multiline')
            var buttonsAreFixed = $('td.infoline').hasClass('fixed')
            var windowLocation = $(window).scrollTop() + $(window).height()
            if (windowLocation < buttonsLocation && !buttonsAreFixed) {
                $('td.infoline').addClass('fixed')
                $('td.infoline').css('width', buttonContainerWidth)
                $('#lookup').addClass('fixedButtons')
                if (multilineButtons) {
                    $('#lookup').addClass('multiline')
                }
            } else if (windowLocation >= buttonsLocation && buttonsAreFixed) {
                $('td.infoline').removeClass('fixed')
                $('#lookup').removeClass('fixedButtons')
            }
        }
    })
</script>
