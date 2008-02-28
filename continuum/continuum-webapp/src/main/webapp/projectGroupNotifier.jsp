<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri="continuum" prefix="c1" %>
<html>
<ww:i18n name="localization.Continuum">
  <head>
    <title><ww:text name="projectView.page.title"/></title>
  </head>

  <body>
  <div id="h3">
    <div>
      <p style="border-top: 1px solid transparent; border-bottom: 1px solid #DFDEDE;">
        <ww:url id="projectGroupSummaryUrl" action="projectGroupSummary">
          <ww:param name="projectGroupId" value="projectGroupId"/>
        </ww:url>
        <ww:url id="projectGroupMembersUrl" action="projectGroupMembers">
          <ww:param name="projectGroupId" value="projectGroupId"/>
        </ww:url>
        <ww:url id="projectGroupBuildDefinitionUrl" action="projectGroupBuildDefinition">
          <ww:param name="projectGroupId" value="projectGroupId"/>
        </ww:url>
        <ww:url id="projectGroupNotifierUrl" action="projectGroupNotifier">
          <ww:param name="projectGroupId" value="projectGroupId"/>
        </ww:url>
        <ww:a cssStyle="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;"
              href="%{projectGroupSummaryUrl}">Summary</ww:a>
        <ww:a cssStyle="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;"
              href="%{projectGroupMembersUrl}">Members</ww:a>
        <ww:a cssStyle="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;"
              href="%{projectGroupBuildDefinitionUrl}">Build Definitions</ww:a>
        <b style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em;">Notifier</b>

      </p>
    </div>

    <h3>PROJECT GROUP Notifiers<ww:text name="projectView.section.title"/></h3>

    <div class="axial">
      <table border="1" cellspacing="2" cellpadding="3" width="100%">
        <c1:data label="%{getText('projectView.project.name')}" name="projectGroup.name"/>
      </table>
    </div>

    <div class="axial">
      UNDER CONSTRUCTION
    </div>
  </div>
  </body>
</ww:i18n>
</html>