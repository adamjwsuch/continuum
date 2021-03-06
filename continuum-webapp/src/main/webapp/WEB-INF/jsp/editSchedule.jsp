<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="continuum" prefix="c1" %>

<html>
  <s:i18n name="localization.Continuum">
<head>
<title><s:text name="editSchedule.page.title"/></title>
</head>
<body>
<div class="app">
  <div id="axial" class="h3">
    <h3><s:text name="editSchedule.section.title"/></h3>

    <div class="axial">
      <s:form action="saveSchedule" method="post" validate="false" name="scheduleForm">
        <s:hidden name="id"/>

        <s:if test="hasActionErrors()">
          <div class="errormessage">
            <s:actionerror/>
          </div>
        </s:if>
        <s:if test="hasActionMessages()">
          <div class="warningmessage">
            <s:actionmessage/>
          </div>
        </s:if>

          <table>
            <s:textfield label="%{getText('schedule.name.label')}" name="name" requiredLabel="true" size="100">
                <s:param name="after"><p><s:text name="schedule.name.message"/></p></s:param>
            </s:textfield>
            <s:textfield label="%{getText('schedule.description.label')}" name="description" requiredLabel="true" size="100">
                <s:param name="after"><p><s:text name="schedule.description.message"/></p></s:param>
            </s:textfield>

            <tr>
              <td class="tdLabel"><s:label theme="simple" class="label" value="%{getText('schedule.cronExpression.label')}:"/></td>
              <td>
                <table>
                  <s:textfield label="%{getText('schedule.second.label')}" name="second" size="10"/>
                  <s:textfield label="%{getText('schedule.minute.label')}" name="minute" size="10"/>
                  <s:textfield label="%{getText('schedule.hour.label')}" name="hour" size="10"/>
                  <s:textfield label="%{getText('schedule.dayOfMonth.label')}" name="dayOfMonth" size="10"/>
                  <s:textfield label="%{getText('schedule.month.label')}" name="month" size="10"/>
                  <s:textfield label="%{getText('schedule.dayOfWeek.label')}" name="dayOfWeek" size="10"/>
                  <s:textfield label="%{getText('schedule.year.label')}" name="year"  size="4">
                    <s:param name="after"><p><s:text name="schedule.cronExpression.message"/></p></s:param>
                  </s:textfield>
                </table>
              </td>
            </tr>

            <s:textfield label="%{getText('schedule.maxJobExecutionTime.label')}" name="maxJobExecutionTime" requiredLabel="true" size="100">
                <s:param name="after"><p><s:text name="schedule.maxJobExecutionTime.message"/></p></s:param>
            </s:textfield>
            <s:textfield label="%{getText('schedule.quietPeriod.label')}" name="delay" size="100">
                <s:param name="after"><p><s:text name="schedule.quietPeriod.message"/></p></s:param>
            </s:textfield>

            <c1:ifBuildTypeEnabled buildType="parallel">          
              <s:optiontransferselect 
                label="%{getText('schedule.buildqueues.label')}"
                name="availableBuildQueuesIds"
                list="availableBuildQueues"
                listKey="id"
                listValue="name"
                headerKey="hk-1"
                headerValue="%{getText('schedule.available.buildqueues')}"
                multiple="true"
                size="8"
                emptyOption="false"
                doubleName="selectedBuildQueuesIds"
                doubleList="selectedBuildQueues"
                doubleListKey="id"
                doubleListValue="name"
                doubleHeaderKey="hk-1"
                doubleSize="8"
                doubleMultiple="true"
                doubleEmptyOption="false"
                doubleHeaderValue="%{getText('schedule.available.buildqueues.used')}"
                formName="scheduleForm"
                addAllToRightOnclick="selectAllOptionsExceptSome(document.getElementById('saveSchedule_selectedBuildQueuesIds'), 'key', 'hk-1');"
                addToRightOnclick="selectAllOptionsExceptSome(document.getElementById('saveSchedule_availableBuildQueuesIds'), 'key', 'hk-1');selectAllOptionsExceptSome(document.getElementById('saveSchedule_selectedBuildQueuesIds'), 'key', 'hk-1');"
                addAllToLeftOnclick="selectAllOptionsExceptSome(document.getElementById('saveSchedule_availableBuildQueuesIds'), 'key', 'hk-1');"
                addToLeftOnclick="selectAllOptionsExceptSome(document.getElementById('saveSchedule_availableBuildQueuesIds'), 'key', 'hk-1');selectAllOptionsExceptSome(document.getElementById('saveSchedule_selectedBuildQueuesIds'), 'key', 'hk-1');"
                />
             </c1:ifBuildTypeEnabled>   
                                       
            <s:checkbox label="%{getText('schedule.enabled.label')}" name="active" value="active" fieldValue="true">
                <s:param name="after"><p><s:text name="schedule.enabled.message"/></p></s:param>
            </s:checkbox>

          </table>
          <div class="functnbar3">
            <s:submit value="%{getText('save')}" theme="simple"/>
            <input type="button" name="Cancel" value="<s:text name='cancel'/>" onclick="history.back();"/>
          </div>
      </s:form>
      
    </div>
  </div>
</div>

</body>
</s:i18n>
</html>

