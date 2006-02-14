package org.apache.maven.continuum.it;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.apache.maven.continuum.Continuum;
import org.apache.maven.continuum.execution.maven.m2.MavenTwoBuildExecutor;
import org.apache.maven.continuum.model.project.BuildResult;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.project.ProjectNotifier;
import org.apache.maven.continuum.project.ContinuumProjectState;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MavenTwoIntegrationTest
    extends AbstractIntegrationTest
{
    public void testBasic()
        throws Exception
    {
        Continuum continuum = getContinuum();

        initializeCvsRoot();

        progress( "Initializing Maven 2 CVS project" );

        File root = getItFile( "maven-two" );

        initMaven2Project( root, "maven-two" );

        progress( "Adding Maven 2 project" );

        int projectId = getProjectId( continuum.addMavenTwoProject( "file:" + root.getAbsolutePath() + "/pom.xml" ) );

        waitForSuccessfulCheckout( projectId );

        Project project = continuum.getProjectWithAllDetails( projectId );

        assertProject( projectId, "Maven 2 Project", "2.0-SNAPSHOT", "-N -B", MavenTwoBuildExecutor.ID, project );

        assertEquals( "project.notifiers.size", 2, project.getNotifiers().size() );

        //TODO: Activate this test when CONTINUUM-252 will be fixed
        //removeNotifier( projectId, ( (ProjectNotifier) project.getNotifiers().get( 1 ) ).getId() );

        //assertEquals( "project.notifiers.size", 1, project.getNotifiers().size() );

        Map configuration = ( (ProjectNotifier) project.getNotifiers().get( 0 ) ).getConfiguration();

        assertEquals( "project.notifiers[1].configuration.size", 1, configuration.size() );

        assertEquals( "project.notifiers[1].configuration['address']", getEmail(), configuration.get( "address" ) );

        progress( "Building Maven 2 project" );

        project = continuum.getProjectWithBuilds( projectId );
        int originalSize = project.getBuildResults().size();

        int buildId = buildProject( projectId, ContinuumProjectState.TRIGGER_SCHEDULED ).getId();

        assertSuccessfulMaven2Build( buildId, projectId );

        progress( "Test that a build without any files changed won't execute the executor" );

        project = continuum.getProjectWithBuilds( projectId );
        int expectedSize = project.getBuildResults().size();

        assertEquals( "build list was not updated", originalSize + 1, expectedSize );

        continuum.buildProject( projectId, ContinuumProjectState.TRIGGER_SCHEDULED );

        Thread.sleep( 3000 );

        project = continuum.getProjectWithBuilds( projectId );
        int actualSize = project.getBuildResults().size();

        assertEquals( "A build has unexpectedly been executed.", expectedSize, actualSize );

        progress( "Test that a forced build without any files changed executes the executor" );

        buildId = buildProject( projectId, ContinuumProjectState.TRIGGER_FORCED ).getId();

        BuildResult build = assertSuccessfulMaven2Build( buildId, projectId );

        assertEquals( "The 'build forced' flag wasn't true", ContinuumProjectState.TRIGGER_FORCED, build.getTrigger() );

        progress( "Test that a forced build with a pom deleted executes the executor" );

        File pom = new File(getContinuum().getWorkingDirectory( projectId), "pom.xml");

        assertTrue( pom.delete() );

        buildId = buildProject( projectId, ContinuumProjectState.TRIGGER_FORCED ).getId();

        build = assertSuccessfulMaven2Build( buildId, projectId );

        assertEquals( "The 'build forced' flag wasn't true", ContinuumProjectState.TRIGGER_FORCED, build.getTrigger() );

        removeProject( projectId );
    }

    private void initMaven2Project( File basedir, String artifactId )
        throws IOException, CommandLineException
    {
        File cvsRoot = getCvsRoot();

        deleteAndCreateDirectory( basedir );

        FileUtils.fileWrite( new File( basedir, "pom.xml" ).getAbsolutePath(), "<project>\n" +
            "  <modelVersion>4.0.0</modelVersion>\n" + "  <groupId>continuum</groupId>\n" + "  <artifactId>" +
            artifactId + "</artifactId>\n" + "  <version>2.0-SNAPSHOT</version>\n" +
            "  <name>Maven 2 Project</name>\n" + "  <ciManagement>\n" + "    <notifiers>\n" + "      <notifier>\n" +
            "        <type>mail</type>\n" + "        <configuration>\n" + "          <address>" + getEmail() +
            "</address>\n" + "        </configuration>\n" + "      </notifier>\n" + "      <notifier>\n" +
            "        <type>irc</type>\n" + "        <configuration>\n" + "          <host>irc.codehaus.org</host>\n" +
            "          <port>6667</port>\n" + "          <channel>#test</channel>\n" + "        </configuration>\n" +
            "      </notifier>\n" + "    </notifiers>\n" + "  </ciManagement>\n" + "  <scm>\n" + "    <connection>" +
            makeScmUrl( "cvs", cvsRoot, artifactId ) + "</connection>\n" + "  </scm>\n" + "</project>" );

        assertTrue( new File( basedir + "/src/main/java" ).mkdirs() );

        FileUtils.fileWrite( new File( basedir + "/src/main/java/Foo.java" ).getAbsolutePath(), "class Foo { }" );

        cvsImport( basedir, artifactId, getCvsRoot() );
    }

    private void removeNotifier( int projectId, int notifierId )
    {
        try
        {
            getContinuum().removeNotifier( projectId, notifierId );
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            fail( "Unexpected exception after removing notifier '" + notifierId + "' for project '" + projectId );
        }
    }
}
