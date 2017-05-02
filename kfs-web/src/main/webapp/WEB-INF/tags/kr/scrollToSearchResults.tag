<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
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
<script type="text/javascript">
    var headerSelector = '.search-results .headerarea-small';
    var tableSelector = '.search-results table';
    var tableHeaderSelector = '.search-results table>thead';

    $(document).ready(function() {
        // smooth scroll window to the search results after clicking search
        if (window.location.hash != '#search-results') {
            $('html,body').animate({
                scrollTop: $('#search-results').offset().top
            }, 1000);
        }

        // make search results header sticky
        var headerLocation = $(headerSelector).offset().top - $(headerSelector).outerHeight();
        makeHeaderSticky();

        // Modify header stickiness as we scroll
        $(window).scroll(function() {
            makeHeaderSticky();
        });

        $(window).resize(function() {
            makeHeaderSticky();
        });

        function makeHeaderSticky() {
            var headerIsSticky = $(headerSelector).hasClass('fixed');
            var windowLocation = $(window).scrollTop();
            $(headerSelector).css('width', $(tableSelector).outerWidth());
            if (windowLocation > headerLocation && !headerIsSticky) {
                $(headerSelector).addClass('fixed');
                $(tableSelector).addClass('fixedHeader');
            } else if (windowLocation <= headerLocation && headerIsSticky) {
                $(headerSelector).removeClass('fixed');
                $(tableSelector).removeClass('fixedHeader');
            }
        }
    })
</script>
