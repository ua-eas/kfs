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

<%@ attribute name="lookup" required="false" description="indicates whether the lookup specific footer should be shown" %>
<%@ attribute name="feedbackKey" required="false" description="application resources key that contains feedback contact address only used when lookup attribute is false"%>

<c:choose>
	<c:when test="${lookup}" >
        <!-- lookup/inquiry footer -->
    </c:when>
	<c:otherwise>
		<!-- footer -->
		<c:choose>
			<c:when test="${empty feedbackKey}">
				<c:set var="feedbackKey" value="app.feedback.link"/>
			</c:when>
		</c:choose>

					<p>&nbsp;</p>
				</td>
				<!-- KULRICE-8093: Horizontal scrolling for maintenance documents with errors listed  -->
				<td width="21"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20"/></td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>

