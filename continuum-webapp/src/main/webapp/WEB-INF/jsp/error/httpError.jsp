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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<s:i18n name="localization.Continuum">
<html>
<head>
  <title>
      <s:if test="#parameters.errorCode == 403">
        <s:text name="error.403.title"/>
      </s:if>
      <s:elseif test="#parameters.errorCode == 404">
        <s:text name="error.404.title"/>
      </s:elseif>
      <s:elseif test="#parameters.errorCode == 500">
        <s:text name="error.500.title"/>
      </s:elseif>
      <s:else>
        <s:text name="error.page.title"/>
      </s:else>
  </title>
</head>

<body>
  <div id="h3">
    <h3>
        <s:if test="#parameters.errorCode == 403">
          <s:text name="error.403.section.title"/>
        </s:if>
        <s:elseif test="#parameters.errorCode == 404">
          <s:text name="error.404.section.title"/>
        </s:elseif>
        <s:elseif test="#parameters.errorCode == 500">
          <s:text name="error.500.section.title"/>
        </s:elseif>
        <s:else>
          The URL requested results to an unknown error (Error <s:property value="errorCode"/>).
        </s:else>
    </h3>

    <div class="errormessage">
        <s:if test="#parameters.errorCode == 403">
          <s:text name="error.403.message"/>
        </s:if>
        <s:elseif test="#parameters.errorCode == 404">
          <s:text name="error.404.message"/>
        </s:elseif>
        <s:elseif test="#parameters.errorCode == 500">
          <s:text name="error.500.message"/>
        </s:elseif>
        <s:else>
          The URL requested results to an unknown error (Error <s:property value="errorCode"/>).
        </s:else>
    </div>
  </div>
</body>
</html>
</s:i18n>