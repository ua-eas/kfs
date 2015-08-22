<!--
- The Kuali Financial System, a comprehensive financial management system for higher education.
-
- Copyright 2005-2014 The Kuali Foundation
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
-->
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%--<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">--%>
    <%--<div class="modal-dialog">--%>
        <%--<div class="modal-content">--%>
            <%--<!-- remote modal content goes here -->--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>
<div class="remodal-overlay"></div>
<div class="remodal-wrapper">
    <div id="remodal" class="remodal" data-remodal-id="modal" data-remodal-options="hashTracking: false">
        <div class="content"></div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $(document).on('click', 'a[data-remodal-target="modal"]', function(event) {
            event.preventDefault();
            var myModal = $('#remodal');

            var modalBody = myModal.find('.content');
            var href = $(event.target).attr('href');
            var title = $.trim($(event.target).text());
            modalBody.load(href, function() {
                myModal.remodal();

                // if we just clicked one of the crumbs then pop everything off the stack on top of it
                var stackIndex = $(event.target).attr("data-stack-index");
                console.log(stackIndex)
                if (stackIndex > -1) {
                    breadcrumbs = breadcrumbs.slice(0, stackIndex);
                }

                var crumbs = '';
                for (var i = 0; i < breadcrumbs.length; i++) {
                    crumbs += '<a href="' + breadcrumbs[i].href + '" title="' + breadcrumbs[i].title + '" data-remodal-target="modal" data-stack-index="' + i + '">';
                    crumbs +=       breadcrumbs[i].title;
                    crumbs += '</a>';
                    if (i < breadcrumbs.length - 1) {
                        crumbs += '<span class="glyphicon glyphicon-chevron-right"></span>';
                    }
                }
                $('#breadcrumbs').html(crumbs)

                breadcrumbs.push({title: title, href: href})
            });
        });

        $(document).on('closed', '.remodal', function () {
            breadcrumbs = [];
        });
    });
</script>