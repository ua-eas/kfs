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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:page showDocumentInfo="false"
          headerTitle="Modify Batch Job" docTitle="Modify Batch Job"
          transactionalDocument="false" htmlFormAction="batchModify"
          errorKey="*">
    <div style="text-align: right; margin: -75px 10px 50px 0;">
        <a class="btn btn-primary" href="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&conversionFields=name:name,group:group">Return to Lookup</a>
    </div>
    <html:hidden property="refreshCaller"/>
    <input type="hidden" name="name" value="${job.name}"/>
    <input type="hidden" name="group" value="${job.group}"/>
    <kul:tabTop tabTitle="Job Info" defaultOpen="true">
        <div class="tab-container" align="center">
            <table width="100%" cellpadding=0 cellspacing=0 class="datatable standard">
                <tr class="header">
                    <th>
                        <kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.name" noColon="${true}"/></td>
                    <th><kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.group" noColon="${true}"/></th>
                    <th><kul:htmlAttributeLabel attributeEntryName="DataDictionary.BatchJobStatus.attributes.status" noColon="${true}"/></th>
                    <th>More Info</th>
                    <c:if test="${canRunJob}">
                        <th>Running</th>
                    </c:if>
                    <c:if test="${canSchedule || canUnschedule || canStopJob}">
                        <th>Other Commands
                        </td>
                    </c:if>
                </tr>
                <tr class="top">
                    <td>${job.name}</td>
                    <td>${job.group}</td>
                    <td>${job.status}</td>
                    <td><sys:backdoorAwareLink displayTitle="true" title="Batch File lookup (to retrieve logs and reports)" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchFile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></td>
                    <c:if test="${canRunJob}">
                        <td>
                            <c:if test="${job.group == 'unscheduled' && !job.running}">
                                <table>
                                    <tr>
                                        <th class="right"><label for="startStep">Start Step</label>:</th>
                                        <td><input type="text" id="startStep" name="startStep" value="1" size="3"/></td>
                                    </tr>
                                    <tr>
                                        <th class="right"><label for="endStep">End Step</label>:
                                        </td>
                                        <td><input type="text" id="endStep" name="endStep" value="${job.numSteps}" size="3"/></td>
                                    </tr>
                                    <tr>
                                        <th class="right"><label for="startTime">Start Date/Time</label>:
                                        </td>
                                        <td>
                                            <input type="text" id="startTime" name="startTime" id="startTime" value="" maxlength="20" size="20" onchange="" onblur="" style="" class="">
                                            <img src="${ConfigProperties.kr.externalizable.images.url}cal.png" width="24" id="startTime_datepicker" style="cursor: pointer;" title="Date selector" alt="Date selector"/>
                                            <script type="text/javascript">
                                                Calendar.setup(
                                                        {
                                                            inputField: "startTime", // ID of the input field
                                                            ifFormat: "%m/%d/%Y %I:%M %p", // the date format
                                                            button: "startTime_datepicker", // ID of the button
                                                            showsTime: true,
                                                            timeFormat: "12"
                                                        }
                                                );
                                            </script>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="right"><label for="emailAddress">Results Email Address</label>:
                                        </td>
                                        <td>
                                            <input type="text" id="emailAddress" name="emailAddress" id="emailAddress" value=""/>
                                            <button
                                                    onclick="document.getElementById('emailAddress').value = '${userEmailAddress}'; return false;"
                                                    class="btn btn-default"
                                                    title="Mail To Me"
                                                    alt="Mail To Me">

                                                Mail to Me
                                            </button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td>
                                            <html:submit
                                                    styleClass="btn btn-green"
                                                    property="methodToCall.runJob"
                                                    title="Run Job"
                                                    alt="Run Job"
                                                    value="Run"/>
                                        </td>
                                    </tr>
                                </table>
                            </c:if>
                            &nbsp;
                        </td>
                    </c:if>
                    <c:if test="${canSchedule || canUnschedule || canStopJob}">
                        <td>
                            <c:if test="${canSchedule && !job.scheduled}">
                                <html:submit
                                        styleClass="btn btn-green"
                                        property="methodToCall.schedule"
                                        title="Add to Standard Schedule"
                                        alt="Add to Standard Schedule"
                                        value="Schedule"/>
                            </c:if>
                            <c:if test="${canUnschedule && job.scheduled}">
                                <html:submit
                                        styleClass="btn btn-red"
                                        property="methodToCall.unschedule"
                                        title="Remove From Standard Schedule"
                                        alt="Remove From Standard Schedule"
                                        value="Unschedule"/>
                            </c:if>
                            <c:if test="${canStopJob && job.running}">
                                <html:submit
                                        styleClass="btn btn-red"
                                        property="methodToCall.stopJob"
                                        title="Stop Running Job"
                                        alt="Stop Running Job"
                                        value="Stop"/>
                                <br/>
                            </c:if>
                        </td>
                    </c:if>
                </tr>
            </table>
        </div>
    </kul:tabTop>
    <kul:tab tabTitle="Steps" defaultOpen="true">
        <div class="tab-container" align="center">
            <table class="datatable standard">
                <tr class="header">
                    <th>#</th>
                    <th>Name
                    </td>
                </tr>
                <c:forEach items="${job.steps}" var="step" varStatus="status">
                    <tr class="${status.index % 2 == 0 ? 'highlight' : ''}">
                        <th>${status.count}</th>
                        <td>${step.name}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </kul:tab>
    <kul:tab tabTitle="Dependencies" defaultOpen="true">
        <div class="tab-container" align="center">
            <table class="datatable standard">
                <c:forEach items="${job.dependencies}" var="dep" varStatus="status">
                    <tr class="${(status.index + 1) % 2 == 0 ? 'highlight' : ''}">
                        <td>${dep.key} (${dep.value})</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </kul:tab>
</kul:page>
