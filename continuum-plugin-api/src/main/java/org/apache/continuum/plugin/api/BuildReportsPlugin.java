package org.apache.continuum.plugin.api;

import org.apache.continuum.model.BuildResult;
import org.apache.continuum.model.ScmResult;
import org.apache.continuum.plugin.api.context.ProjectInformation;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface BuildReportsPlugin
    extends Plugin
{
    void execute( ProjectInformation projectInformation, ScmResult scmResult, BuildResult buildResult );
}
