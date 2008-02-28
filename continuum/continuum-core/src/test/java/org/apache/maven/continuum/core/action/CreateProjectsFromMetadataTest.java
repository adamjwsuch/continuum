package org.apache.maven.continuum.core.action;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.continuum.project.builder.ContinuumProjectBuilder;
import org.apache.maven.continuum.project.builder.ContinuumProjectBuildingResult;
import org.apache.maven.continuum.project.builder.manager.ContinuumProjectBuilderManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class CreateProjectsFromMetadataTest
    extends MockObjectTestCase
{

    private CreateProjectsFromMetadata action;

    private Mock projectBuilderManagerMock, projectBuilder;

    protected void setUp()
        throws Exception
    {
        action = new CreateProjectsFromMetadata();
        action.enableLogging( new ConsoleLogger( Logger.LEVEL_DEBUG, "" ) );
        projectBuilderManagerMock = mock( ContinuumProjectBuilderManager.class );
        action.setProjectBuilderManager( (ContinuumProjectBuilderManager) projectBuilderManagerMock.proxy() );

        projectBuilder = mock( ContinuumProjectBuilder.class );

        projectBuilderManagerMock.expects( once() ).method( "getProjectBuilder" )
            .will(returnValue( projectBuilder.proxy() ) );
        projectBuilder.expects( once() ).method( "buildProjectsFromMetadata" )
            .will( returnValue( new ContinuumProjectBuildingResult() ) );
    }

    public void testExecute()
        throws Exception
    {
        Map context = new HashMap();
        context.put( CreateProjectsFromMetadata.KEY_URL, "http://svn.apache.org/repos/asf/maven/continuum/trunk/pom.xml" );
        context.put( CreateProjectsFromMetadata.KEY_PROJECT_BUILDER_ID, "id" );

        action.execute( context );

        ContinuumProjectBuildingResult result = (ContinuumProjectBuildingResult) context
            .get( CreateProjectsFromMetadata.KEY_PROJECT_BUILDING_RESULT );

        assertFalse( "Should not have errors", result.hasErrors() );
    }

}