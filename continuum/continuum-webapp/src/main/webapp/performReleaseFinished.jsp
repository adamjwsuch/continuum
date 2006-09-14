<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="continuum" prefix="c1" %>
<html>
  <ww:i18n name="localization.Continuum">
    <head>
        <title><ww:text name="releaseProject.page.title"/></title>
    </head>
    <body>
      <h2>Continuum Release</h2>
      <h3><ww:property value="name"/></h3>
      <ww:form action="groupSummary" method="post">
        <div class="axial">
          <table width="100%">
            <tr>
              <th>Status</th>
              <th>Phase</th>
            </tr>
            <ww:iterator value="listener.phases">
              <tr>
                <ww:if test="listener.completedPhases.contains( top )">
                  <td>done</td>
                </ww:if>
                <ww:elseif test="listener.inProgress.equals( top )">
                  <ww:if test="listener.error == null">
                    <td>in progress</td>
                  </ww:if>
                  <ww:else>
                    <td>error</td>
                  </ww:else>
                </ww:elseif>
                <ww:else>
                  <td>not done</td>
                </ww:else>
                <td><ww:property/></td>
              </tr>
            </ww:iterator>
          </table>
        </div>

        <p>
          <ww:url id="viewReleaseResultUrl" action="viewReleaseResult" namespace="/">
            <ww:param name="releaseId" value="releaseId"/>
          </ww:url>
          <ww:a href="%{viewReleaseResultUrl}">View Release Output</ww:a>
        </p>

        <ww:submit value="Done"/>
      </ww:form>
    </body>
  </ww:i18n>
</html>
