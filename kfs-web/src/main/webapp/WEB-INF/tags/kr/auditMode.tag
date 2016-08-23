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
<%@ attribute name="documentTypeName" required="true" description="The name of the document type displayed by the page rendering this tag; used to lookup the document type level help icon rendered on this page." %>
<%@ attribute name="categories" required="true" description="The categories of audit error clusters this tag should render errors for." %>

<div align="right">
	<kul:help documentTypeName="${documentTypeName}" pageName="${RiceConstants.AUDIT_MODE_HEADER_TAB}" altText="page help"/>
</div>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
	<tbody id="">
		<tr>
			<td><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="12" class="tl3" id=""></td>
			<td align="right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="12" class="tr3" id=""></td>
		</tr>
	</tbody>
</table>

<div id="workarea">
	<div class="tab-container"  align="center">
			<h3>Audit Mode</h3>
		<table cellpadding=0 cellspacing="0"  summary="">
			<tr>
				<td>
					<div class="floaters">
						<p>You can activate an audit check to determine any errors or incomplete information. </p>
						<p align="center">
							<c:choose>
								<c:when test="${KualiForm.auditActivated}"><html:submit property="methodToCall.deactivate" value="Deactivate Audit Mode" title="Deactivate audit mode" alt="Deactivate audit mode" styleClass="btn btn-default small" /></c:when>
								<c:otherwise><html:submit property="methodToCall.activate" value="Activate Audit Mode" title="Activate audit mode" alt="Activate audit mode" styleClass="btn btn-default small" /></c:otherwise>
							</c:choose>
						</p>
					</div>
				</td>
			</tr>
		</table>
		<c:if test="${KualiForm.auditActivated}">
			<table cellpadding="0" cellspacing="0" summary="">
			<c:forEach items="${fn:split(categories,',')}" var="category">
				<kul:auditSet category="${category}" />
			</c:forEach>
			</table>
		</c:if>
	</div>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
		<tr>
			<td align="left" class="footer"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="bl3" id=""></td>
			<td align="right" class="footer-right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="br3" id=""></td>
		</tr>
	</table>
</div>
<div class="globalbuttons"> </div>
