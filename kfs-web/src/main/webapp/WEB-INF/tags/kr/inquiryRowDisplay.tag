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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="rows" required="true" type="java.util.List"
              description="The rows of fields that we'll iterate to display." %>
<%@ attribute name="numberOfColumns" required="false"
              description="The # of fields in this row." %>
<%@ attribute name="skipTheOldNewBar" required="false"
              description="boolean that indicates whether the old and new bar should be skipped" %>
<%@ attribute name="depth" required="false"
              description="the recursion depth number" %>
<%@ attribute name="rowsHidden" required="false"
              description="boolean that indicates whether the rows should be hidden or all fields are hidden" %>
<%@ attribute name="rowsReadOnly" required="false"
              description="boolean that indicates whether the rows should be rendered as read-only (note that rows will automatically be rendered as readonly if it is an inquiry or if it is a maintenance document in readOnly mode" %>
<%@ attribute name="sessionDocument" required="false"
              description="boolean that indicates whether the sessionDocument declared in DD" %>

<c:if test="${empty depth}">
    <c:set var="depth" value="0" />
</c:if>

<%-- Is the screen a lookup? --%>
<c:set var="isLookup" value="${false}" />

<%-- Is the form read-only? --%>
<c:set var="isFormReadOnly" value="${rowsReadOnly}" />

<%-- What's the user trying to do? --%>
<c:set var="requestedAction" value="${KualiForm.methodToCall}" />

<c:set var="isHeaderDisplayed" value="false" />
<c:set var="rowCount" value="${rows.size()}"/>
<c:forEach items="${rows}" var="row" varStatus="rowVarStatus">

    <c:set var="rowHidden" value="${rowsHidden || row.hidden}" />

        <c:choose>
            <c:when test="${rowHidden}"><tr style="display: none;"></c:when>
            <c:otherwise><tr></c:otherwise>
        </c:choose>

    <c:forEach items="${row.fields}" var="field" varStatus="fieldVarStatus">
        <c:set var="isFieldAContainer" value="${field.fieldType eq field.CONTAINER}" />
        <c:set var="isFieldAddingToACollection" value="${fn:contains(field.propertyName, 'add.')}" />

        <c:set var="headerColspan" value="${numberOfColumns * 2}" />
        <c:set var="dataCellWidth" value="${100 / (numberOfColumns * ((requestedAction eq 'addLine') ? 4 : 2))}" />

        <c:set var="tabIndex" value="0"/>
        <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabIndex)}" />

        <%--
            ###################################################################
            # GATHER SOME INFORMATION ABOUT THE FIELD AND STORE IT IN VARIABLES
            ################################################################### --%>
        <%-- isFieldSecure determines whether or not the encrypted value should be shown for
        non-collections and a similar function for collections --%>
        <c:set var="isFieldSecure" value="${field.secure}" />

        <%-- isFieldReadOnly determines whether or not a field is readOnly --%>
        <%-- NOTE: The part about "fieldVarStatus.count mod 2" will work for any even number
        of columns assuming that all columns alternate between read-only and not-read-only. --%>
        <c:set var="isFieldReadOnly" value="${isFieldSecure || field.readOnly || isFormReadOnly}" />

        <%-- textStyle is used to store the style of the field value. i.e. whether or not it
        should display as red text. --%>
        <c:set var="textStyle" value="" />

        <%-- fieldValue should be used to store the appropriate value for a field, i.e. handle
        showing the encrypted value if a field is secure, etc. --%>
        <c:set var="fieldValue" value="${field.propertyValue}" />

        <%--
            #######################################################
            # PojoForm saves request input that the Formatter framework
            # could not convert to the type of a given field,
            # so that it can be redisplayed here for correction.
            #
            # (jdb) KualiRequestProcessor.processPopulate() puts this
            # in the request too (UnconvertedHash and UnconvertedValues),
            # but is that necessary?  Also, the non-maintenance
            # docs get this automatically by using the Struts tags
            # (e.g., html:text), because Struts is using PojoPropertyUtilsBean.
            # Should the maintenance framework use the Struts html tags too?
            ####################################################### --%>
        <c:set var="unconvertedValue" value="${KualiForm.unconvertedValues[field.propertyName]}"/>
        <c:if test="${not empty unconvertedValue}">
            <c:set var="textStyle" value="border-color: red" />
            <c:set var="fieldValue" value="${unconvertedValue}" />
        </c:if>

        <%--
            #######################################################
            # If the field has errors, highlight its display in red.
            ####################################################### --%>
        <kul:checkErrors keyMatch="${field.propertyName}" />

        <%--
            #######################################################
            # Set the onBlur handlers for the field.
            ####################################################### --%>
        <c:set var="onblur" value="" />
        <c:set var="onblurcall" value="" />

        <c:if test="${!(empty field.webOnBlurHandler)}">
            <c:set var="onblurParameters" value="" />
            <c:choose>
                <c:when test="${!(empty field.webOnBlurHandlerCallback)}">
                    <c:set var="onblurParameters" value="this, ${field.webOnBlurHandlerCallback}" />
                </c:when>
                <c:otherwise>
                    <c:set var="onblurParameters" value="this" />
                </c:otherwise>
            </c:choose>
            <c:if test="${!(empty field.webUILeaveFieldFunctionParameters)}">
                <c:set var="onblurParameters" value="${onblurParameters},${field.webUILeaveFieldFunctionParametersString}" />
            </c:if>

            <c:set var="onblur" value="${field.webOnBlurHandler}( ${onblurParameters} );" />
            <c:set var="onblurcall" value='onblur="${onblur}"' />
        </c:if>

        <%-- Set onchange to submit form if field configured to trigger on change --%>
        <c:set var="onchange" value="" />
        <c:set var="onchangecall" value="" />

        <c:if test="${field.triggerOnChange}">
            <c:set var="onchange" value="setFieldToFocusAndSubmit(this);" />
            <c:set var="onchangecall" value='onchange="${onchange}"' />
        </c:if>

        <%--
            ###########################################################
            # SHOW FIELD
            ########################################################### --%>
        <c:choose>
            <c:when test="${fieldVarStatus.count eq 1 && isFieldAddingToACollection && not isFieldAContainer}">
                <%-- Don't show anything --%>
            </c:when>
            <c:when test="${not (requestedAction eq Constants.SAVE_METHOD and isFieldAddingToACollection) && (requestedAction ne Constants.ADD_LINE_METHOD && fieldVarStatus.count eq 1 && isFieldAddingToACollection && not isFieldAContainer)}">
                <%-- Don't show anything --%>
            </c:when>
            <c:when test="${empty field.fieldType}">
                <%-- Don't show anything --%>
            </c:when>
            <c:when test="${isFieldSecure}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <c:out value="${field.displayMaskValue}"/>
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${isFieldAddingToACollection}">
                <%-- Don't show anything. --%>
            </c:when>
            <c:when test="${isFieldAContainer}">
                <td colspan="${headerColspan * 2}" class="tab-subhead">
                        <%-- Set the width for the collection container. --%>
                    <c:set var="width" value="${depth eq 0 ? '100%' : '85%'}" />
                    <c:set var="subTabTitle">
                        <kul:containerElementSubTabTitle containerField="${field}" isFieldAddingToACollection="${isFieldAddingToACollection}"/>
                    </c:set>
                    <c:set var="subTabButtonAlt">
                        <kul:containerElementSubTabTitle containerField="${field}" isFieldAddingToACollection="${isFieldAddingToACollection}"/>
                    </c:set>

                        <%-- determine whether there are highlighted fields in the container. If so highlight subtab --%>
                    <kul:checkTabHighlight rows="${field.containerRows}" addHighlighting="${addHighlighting}" />

                        <%-- Only show the show/hide button on collection entries that
                        contain data (i.e. those that aren't adding --%>
                    <kul:subtab noShowHideButton="${isFieldAddingToACollection or empty field.containerRows}" subTabTitle="${kfunc:scrubWhitespace(subTabTitle)}" buttonAlt="${kfunc:scrubWhitespace(subTabButtonAlt)}" width="${width}" highlightTab="${tabHighlight}"
                                boClassName="${field.multipleValueLookupClassName}" lookedUpBODisplayName="${field.multipleValueLookupClassLabel}" lookedUpCollectionName="${field.multipleValueLookedUpCollectionName}" useCurrentTabIndexAsKey="true">
                        <table style="width: ${width};" class="standard">
                                <%-- cannot refer to recursive tag (containerRowDisplay) using kul alias or Jetty 7 will have jsp compilation errors on Linux --%>
                                <%-- this tag ends up being recursive b/c it calls rowDisplay--%>
                            <%@ taglib tagdir="/WEB-INF/tags/kr" prefix="kul2"%>
                            <kul2:containerRowDisplay rows="${field.containerRows}" numberOfColumns="${field.numberOfColumnsForCollection}" depth="${depth + 1}" rowsReadOnly="${rowsReadOnly}"/>
                        </table>
                    </kul:subtab>
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.SUB_SECTION_SEPARATOR}">
                <td colspan="${headerColspan}" class="tab-subhead">
                    <c:out value="${field.fieldLabel}" />
                </td>
            </c:when>
            <c:when test="${(field.fieldType eq field.BLANK_SPACE)}">
                    <th class="grid" style="width:${dataCellWidth}%;"></th>
                    <td class="grid" style="width:${dataCellWidth}%;">
                        <c:out value="${field.fieldLabel}" />
                    </td>
            </c:when>
            <c:when test="${field.fieldType eq field.CURRENCY}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.TEXT}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.TEXT_AREA}">
                <kul:fieldDefaultLabel isLookup="${isLookup}"
                                       isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}"
                                       cellWidth="${dataCellWidth}%"
                                       fieldName="${field.propertyName}"
                                       fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}"
                                       cellClass="top"/>

                <td class="grid top" style="width:${dataCellWidth}%; line-height: 20px;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.CHECKBOX}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%"
                                       fieldName="${field.propertyName}" fieldType="${field.fieldType}" fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.DROPDOWN}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%"
                                       fieldName="${field.propertyName}" fieldType="${field.fieldType}" fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.DROPDOWN_REFRESH}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.DROPDOWN_SCRIPT}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.MULTISELECT}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%"
                                       fieldName="${field.propertyName}" fieldType="${field.fieldType}" fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.RADIO}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.KUALIUSER}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <c:if test="${!hasErrors}">
                        <kul:checkErrors keyMatch="${field.universalIdAttributeName}" />
                    </c:if>
                    <c:if test="${numberOfColumns > 1}">
                        <c:set var="hideEmptyCell" value="true" />
                    </c:if>

                    <kul:user userIdFieldName="${field.propertyName}"
                              universalIdFieldName="${field.universalIdAttributeName}"
                              userNameFieldName="${field.personNameAttributeName}"
                              userId="${field.propertyValue}"
                              universalId="${field.universalIdValue}"
                              userName="${field.personNameValue}"
                              label="${field.fieldLabel}"
                              referencesToRefresh="${field.referencesToRefresh}"
                              fieldConversions="${field.fieldConversions}"
                              lookupParameters="${field.lookupParameters}"
                              hasErrors="${hasErrors}"
                              readOnly="${field.keyField || isFieldReadOnly}"
                              onblur="${onblur}"
                              onchange="${onchange}"
                              highlight="${addHighlighting && field.highlightField}"
                              tabIndex="${tabIndex}"
                              hideEmptyCell="${hideEmptyCell}"
                              newRow="false">
                            <jsp:attribute name="helpLink" trim="true">
                                <c:if test="${field.fieldLevelHelpEnabled || (!field.fieldLevelHelpDisabled && KualiForm.fieldLevelHelpEnabled)}">
                                    <kul:help
                                            businessObjectClassName="${field.businessObjectClassName}"
                                            attributeName="${field.fieldHelpName}"
                                            altText="${field.fieldHelpSummary}"
                                            alternativeHelp="${field.fieldLevelHelpUrl}"/>
                                </c:if>
                            </jsp:attribute>
                    </kul:user>
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.WORKFLOW_WORKGROUP}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.FILE}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}" isReadOnly="${isFieldReadOnly}"
                                       cellWidth="${dataCellWidth}%" fieldType="${field.fieldType}" fieldLabel="${field.fieldLabel}" fieldName="${field.propertyName}"/>
                <c:set var="lineNum" value="" />
                <c:if test="${fn:contains(field.propertyName, '[') and fn:contains(field.propertyName, '].')}" >
                    <c:set var="lineNum" value=".line${fn:substringBefore(fn:substringAfter(field.propertyName, '['), '].')}.anchor${tabIndex}"/>
                </c:if>
                <td class="grid" style="width:${dataCellWidth}%;">
                    <c:if test="${empty fieldValue}" >
                        <c:out value="<%=((String) request.getAttribute(\"fileName\"))%>" />
                    </c:if>
                    <c:if test="${not empty fieldValue}" >
                        <html:image property="methodToCall.downloadAttachment${lineNum}" src="${ConfigProperties.kr.externalizable.images.url}${field.imageSrc}" alt="download attachment" style="padding:5px" onclick="excludeSubmitRestriction=true"/>
                        <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />
                    </c:if>
                    </div>
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.LOOKUP_READONLY}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <kul:fieldShowReadOnly field="${field}" addHighlighting="${addHighlighting}" isLookup="${isLookup}" />

                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.LOOKUP_HIDDEN}">
                <kul:fieldDefaultLabel isLookup="${isLookup}" isRequired="${field.fieldRequired}"
                                       isReadOnly="${isFieldReadOnly}" cellWidth="${dataCellWidth}%" fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                       fieldLabel="${field.fieldLabel}" />

                <td class="grid" style="width:${dataCellWidth}%;">
                    <input type="hidden" name='${field.propertyName}' value='<c:out value="${fieldValue}"/>' />
                    <kul:fieldShowIcons isReadOnly="${isFieldReadOnly}" field="${field}" addHighlighting="${addHighlighting}" />
                </td>
            </c:when>
            <c:when test="${field.fieldType eq field.LINK}">
                <c:choose>
                    <c:when test="${not fn:contains(field.propertyName, Constants.MAINTENANCE_OLD_MAINTAINABLE)}">
                        <kul:fieldDefaultLabel isLookup="false"
                                               isRequired="false"
                                               isReadOnly="true" cellWidth="${dataCellWidth}%"
                                               fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                               fieldLabel="${field.fieldLabel}" />

                        <td class="grid" style="width:${dataCellWidth}%;">
                            <c:if test="${not empty field.propertyValue }" >
                                <html:link href="${field.propertyValue}" target="${field.target}" styleClass="${field.styleClass}">${field.hrefText}
                                </html:link>
                            </c:if>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <kul:fieldDefaultLabel isLookup="false"
                                               isRequired="false"
                                               isReadOnly="true" cellWidth="${dataCellWidth}%"
                                               fieldName="${field.propertyName}" fieldType="${field.fieldType}"
                                               fieldLabel="" />

                        <td class="grid" style="width:${dataCellWidth}%;"></td>
                    </c:otherwise>
                </c:choose>
            </c:when>
        </c:choose>
    </c:forEach>

    <c:if test="${numberOfColumns > 1 && rowVarStatus.index == rowCount - 1 && rowCount % 2 != 0}">
        <th></th><td></td>
    </c:if>


</c:forEach>
