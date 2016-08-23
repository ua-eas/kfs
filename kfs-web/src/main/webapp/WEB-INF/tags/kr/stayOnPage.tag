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
<!--
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
-->
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="active" required="true" description="The selector used to find the body element." %>

<c:if test="${active && (empty KualiForm.documentActions || !KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT])}">
    <c:set var="active" value="false"/>
</c:if>

<script type="text/javascript">
    let active = '${active}' === 'true';

    function goToPage(url) {
        window.location = url;
    }

    function stayOnPage(event) {
        if (active) {
            let anchor = $(event.target);
            if (!anchor.attr('href')) {
                anchor = $(event.target).closest('a');
            }

            if (!anchor.attr('target')) {
                event.preventDefault();

                let href = anchor.attr('href');

                let myModal = $('#remodal');
                let modalBody = myModal.find('.remodal-content');
                let html = '<div class="confirm-dialog">';
                html += '<div class="message">You have a document open for editing. If you proceed you will lose any changes you have made.</div>';
                html += '<button class="btn btn-default" data-remodal-action="close">Stay on this Page</button>';
                html += '<button class="btn btn-default" onclick="goToPage(\'' + href + '\')">Leave this Page</button>';
                html += '</div>';
                modalBody.html(html);
                myModal.remodal();
                $('.remodal-wrapper').show();
            }
        }
    }

    if (active) {
        $(document).ready(function () {
            $(document).on('closed', '.remodal', function () {
                $('#remodal .remodal-content').html('');
                $('.remodal-wrapper').hide();
            });
        });
    }
</script>
