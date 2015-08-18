<%--
 Copyright 2005-2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<div id="globalbuttons" class="globalbuttons">
        <c:if test="${KualiForm.canExport}">
		  <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_export.gif" styleClass="globalbuttons" property="methodToCall.export" title="Perform Export" alt="Perform Export" />
	    </c:if>
		<button class="globalbuttons btn btn-default" onclick="window.close()" title="close this window" alt="close this window">Close</button>


    <script type="text/javascript">
        var buttonSelector = "#globalbuttons"
        var bodySelector = "div.inquiry"
        $(document).ready(function() {
            // Set initial button state
            var buttonsLocation = $(buttonSelector).offset().top + $(buttonSelector).outerHeight()
            keepButtonsFixed()

            // Modify button state as we scroll
            $(window).scroll(function() {
                keepButtonsFixed()
            })

            $(window).resize(function() {
                keepButtonsFixed()
            })

            $('#expandAll, #collapseAll, .toggle-show-tab').click(function() {
                tabsChanged()
            })

            function keepButtonsFixed() {
                var buttonContainerWidth = $(bodySelector).outerWidth()
                $(buttonSelector).css('width', buttonContainerWidth + 40)

                var buttonsAreFixed = $(buttonSelector).hasClass('fixed')
                var windowLocation = $(window).scrollTop() + $(window).height()
                if (windowLocation < buttonsLocation && !buttonsAreFixed) {
                    $(buttonSelector).addClass('fixed')
                    $(bodySelector).addClass('fixedButtons')
                } else if (windowLocation >= buttonsLocation && buttonsAreFixed) {
                    $(buttonSelector).removeClass('fixed')
                    $(bodySelector).removeClass('fixedButtons')
                }
            }

            function tabsChanged() {
                $(buttonSelector).removeClass('fixed')
                $(bodySelector).removeClass('fixedButtons')

                buttonsLocation = $(buttonSelector).offset().top + $(buttonSelector).outerHeight()
                keepButtonsFixed()
            }
        })
    </script>
</div>
