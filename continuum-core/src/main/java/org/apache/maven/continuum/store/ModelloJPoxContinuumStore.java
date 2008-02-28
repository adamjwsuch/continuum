package org.apache.maven.continuum.store;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.apache.maven.continuum.project.ContinuumBuild;
import org.apache.maven.continuum.project.ContinuumBuildResult;
import org.apache.maven.continuum.project.ContinuumJPoxStore;
import org.apache.maven.continuum.project.ContinuumProject;
import org.apache.maven.continuum.project.ContinuumProjectState;
import org.apache.maven.continuum.project.state.ContinuumProjectStateGuard;
import org.apache.maven.continuum.scm.CheckOutScmResult;
import org.apache.maven.continuum.scm.ScmFile;
import org.apache.maven.continuum.scm.UpdateScmResult;

import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ModelloJPoxContinuumStore
    extends AbstractContinuumStore
    implements ContinuumStore, Initializable
{
    /** @requirement */
    private JdoFactory jdoFactory;

    /** @requirement */
    private ContinuumProjectStateGuard projectStateGuard;

    private ContinuumJPoxStore store;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
        store = new ContinuumJPoxStore( jdoFactory.getPersistenceManagerFactory() );
    }

    // ----------------------------------------------------------------------
    // ContinuumStore Implementation
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // ContinuumProject
    // ----------------------------------------------------------------------

    public String addProject( String name,
                              String scmUrl,
                              String nagEmailAddress,
                              String version,
                              String commandLineArguments,
                              String executorId,
                              String workingDirectory,
                              Properties configuration )
        throws ContinuumStoreException
    {
        ContinuumProject project = new ContinuumProject();

        project.setName( name );
        project.setScmUrl( scmUrl );
        project.setNagEmailAddress( nagEmailAddress );
        project.setVersion( version );
        project.setCommandLineArguments( commandLineArguments );
        project.setExecutorId( executorId );
        project.setWorkingDirectory( workingDirectory );
        project.setState( ContinuumProjectState.CHECKING_OUT );
        project.setConfiguration( configuration );

        try
        {
            Object id = store.addContinuumProject( project );

            project = store.getContinuumProjectByJdoId( id, true );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while adding a project.", e );
        }

        return project.getId();
    }

    public void removeProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

//            System.err.println( "**********************************" );
//            System.err.println( "**********************************" );
//            System.err.println( "**********************************" );

//            System.err.println( "getProject()" );
            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertDeletable( project );

            // TODO: This whole section is dumb.
            PersistenceManager pm = store.getThreadState().getPersistenceManager();

//            System.err.println( "project.getBuilds()" );
            List builds = project.getBuilds();

            for ( Iterator it = builds.iterator(); it.hasNext(); )
            {
                ContinuumBuild build = (ContinuumBuild) it.next();

//                System.err.println( "getBuildResult()" );
                ContinuumBuildResult result = build.getBuildResult();

                if ( result == null )
                {
                    continue;
                }

//                System.err.println( "result.getChangedFiles()" );
//                List changedFiles = result.getChangedFiles();

//                System.err.println( "changedFiles.clear()" );
//                changedFiles.clear();

//                System.err.println( "pm.deletePersistentAll( changedFiles )" );
//                pm.deletePersistentAll( changedFiles );

//                System.err.println( "result.setBuild( null )" );
                result.setBuild( null );

//                System.err.println( "pm.deletePersistent( result )" );
                pm.deletePersistent( result );

//                System.err.println( "build.setProject( null )" );
                build.setProject( null );
            }

            for ( Iterator it = builds.iterator(); it.hasNext(); )
            {
                ContinuumBuild build = (ContinuumBuild) it.next();

//                System.err.println( "build.setProject( null )" );
                build.setProject( null );

                pm.deletePersistent( build );
            }

//            System.err.println( "pm.deletePersistentAll( builds )" );
            pm.deletePersistentAll( builds );

//            System.err.println( "store.deleteContinuumProject( projectId )" );
            store.deleteContinuumProject( projectId );

            store.commit();

            getLogger().info( "Removed project with id '" + projectId + "'." );
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while removing project with id '" + projectId + "'.", e );
        }
    }

    public void setWorkingDirectory( String projectId, String workingDirectory )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertCanChangeWorkingDirectory( project );

            project.setWorkingDirectory( workingDirectory );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting the working directory.", e );
        }
    }

    public void updateProject( String projectId,
                               String name,
                               String scmUrl,
                               String nagEmailAddress,
                               String version,
                               String commandLineArguments )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertUpdatable( project );

            project.setName( name );
            project.setScmUrl( scmUrl );
            project.setNagEmailAddress( nagEmailAddress );
            project.setVersion( version );
            project.setCommandLineArguments( commandLineArguments );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while updating project.", e );
        }
    }

    public void updateProjectConfiguration( String projectId, Properties configuration )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertUpdatable( project );

            project.setConfiguration( configuration );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while updating project configuration.", e );
        }
    }

    public Collection getAllProjects()
        throws ContinuumStoreException
    {
        try
        {
            return store.getContinuumProjectCollection( true, null, "name ascending" );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading project set", e );
        }
    }

    public ContinuumProject getProjectByName( String name )
        throws ContinuumStoreException
    {
        try
        {
            String filter = "this.name == \"" + name + "\"";

            String ordering = "";

            Collection projects = store.getContinuumProjectCollection( true, filter, ordering );

            if ( projects.size() == 0 )
            {
                return null;
            }

            return (ContinuumProject) projects.iterator().next();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading project set", e );
        }
    }

    public ContinuumProject getProjectByScmUrl( String scmUrl )
        throws ContinuumStoreException
    {
        try
        {
            String filter = "this.scmUrl == \"" + scmUrl + "\"";

            String ordering = "";

            Collection projects = store.getContinuumProjectCollection( true, filter, ordering );

            if ( projects.size() == 0 )
            {
                return null;
            }

            return (ContinuumProject) projects.iterator().next();
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while finding projects.", e );
        }
    }

    public ContinuumProject getProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            ContinuumProject project = store.getContinuumProject( projectId, true );

            return project;
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading project.", e );
        }
    }

    public ContinuumProject getProjectByBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            Object id = JDOHelper.getObjectId( build.getProject() );

            store.commit();

            return store.getContinuumProjectByJdoId( id, true );
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while loading project.", e );
        }
    }

    public CheckOutScmResult getCheckOutScmResultForProject( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            if ( project.getCheckOutScmResult() == null )
            {
                store.commit();

                return null;
            }

            PersistenceManager pm = JDOHelper.getPersistenceManager( project );

            CheckOutScmResult result = project.getCheckOutScmResult();

            pm.retrieve( result );

            pm.makeTransient( result );

            pm.retrieveAll( result.getCheckedOutFiles(), false );

            pm.makeTransientAll( result.getCheckedOutFiles() );

            store.commit();

            return result;
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while getting build result.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Build
    // ----------------------------------------------------------------------

    public String createBuild( String projectId, boolean forced )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertInState( project, ContinuumProjectState.BUILDING );

            ContinuumBuild build = new ContinuumBuild();

            build.setStartTime( System.currentTimeMillis() );

            build.setState( ContinuumProjectState.BUILDING );

            build.setProject( project );

            build.setForced( forced );

            Object id = store.addContinuumBuild( build );

            store.commit();

            build = store.getContinuumBuildByJdoId( id, true );

            return build.getId();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while creating continuum build for project: '" + projectId + "'.", e );
        }
    }

    public void setBuildResult( String buildId, int state, ContinuumBuildResult result, UpdateScmResult scmResult, Throwable error )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            ContinuumProject project = build.getProject();

            projectStateGuard.assertTransition( project, state );

            project.setState( state );

            build.setState( state );

            build.setEndTime( new Date().getTime() );

            build.setError( throwableToString( error ) );

            build.setUpdateScmResult( scmResult );

//            store.commit();
//
//            // ----------------------------------------------------------------------
//            // This double commit seems to be needed for some reason. Not having it
//            // seems to result in some foreign key constraint violation.
//            // ----------------------------------------------------------------------
//
//            store.begin();
//
//            build = store.getContinuumBuild( buildId, false );

            build.setBuildResult( result );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting build result for build: '" + buildId + "'.", e );
        }
    }

    public ContinuumBuild getBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            return store.getContinuumBuild( buildId, true );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading build id: '" + buildId + "'.", e );
        }
    }

    public ContinuumBuild getLatestBuildForProject( String projectId )
        throws ContinuumStoreException
    {
        // TODO: Find a better way to query for this object.

        try
        {
            List builds = store.getContinuumProject( projectId, true ).getBuilds();

            if ( builds.size() == 0 )
            {
                return null;
            }

            return (ContinuumBuild) builds.get( builds.size() - 1 );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while loading last build for project id: '" + projectId + "'.", e );
        }
    }

    public Collection getBuildsForProject( String projectId, int start, int end )
        throws ContinuumStoreException
    {
        try
        {
            String filter = "this.project.id == \"" + projectId + "\"";

            String ordering = "startTime descending";

            return store.getContinuumBuildCollection( true, filter, ordering );
        }
        catch ( Exception e )
        {
            throw new ContinuumStoreException( "Error while getting builds for project id: '" + projectId + "'.", e );
        }
    }

    public ContinuumBuildResult getBuildResultForBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            if ( build.getBuildResult() == null )
            {
                store.commit();

                return null;
            }

            Object id = JDOHelper.getObjectId( build.getBuildResult() );

            store.commit();

            ContinuumBuildResult result = store.getContinuumBuildResultByJdoId( id, true );

            return result;
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while getting build result.", e );
        }
    }

    public List getChangedFilesForBuild( String buildId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumBuild build = store.getContinuumBuild( buildId, false );

            if ( build.getBuildResult() == null )
            {
                store.commit();

                return null;
            }

            // TODO: Having to copy the objects feels a /bit/ strange.

            List changedFiles = new ArrayList();

            for ( Iterator it = build.getUpdateScmResult().getUpdatedFiles().iterator(); it.hasNext(); )
            {
                ScmFile scmFile = (ScmFile) it.next();

                ScmFile file = new ScmFile();

                file.setPath( scmFile.getPath() );

                changedFiles.add( file );
            }

            store.commit();

            return changedFiles;
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while getting build result.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setCheckoutDone( String projectId,
                                 CheckOutScmResult scmResult,
                                 String errorMessage,
                                 Throwable exception )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            int state;

            if ( scmResult != null &&
                 scmResult.isSuccess() &&
                 StringUtils.isEmpty( errorMessage ) &&
                 exception == null )
            {
                state = ContinuumProjectState.NEW;
            }
            else
            {
                state = ContinuumProjectState.ERROR;
            }

            projectStateGuard.assertTransition( project, state );

            project.setState( state );

            project.setCheckOutScmResult( scmResult );

            project.setCheckOutErrorMessage( errorMessage );

            project.setCheckOutErrorException( throwableToString( exception ) );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting check out scm result.", e );
        }
    }

    public void setIsUpdating( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertTransition( project, ContinuumProjectState.UPDATING );

            project.setState( ContinuumProjectState.UPDATING );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting build state.", e );
        }
    }

    public void setUpdateDone( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            projectStateGuard.assertTransition( project, ContinuumProjectState.BUILDING );

            project.setState( ContinuumProjectState.BUILDING );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting update scm result.", e );
        }
    }

    public void setBuildNotExecuted( String projectId )
        throws ContinuumStoreException
    {
        try
        {
            store.begin();

            ContinuumProject project = store.getContinuumProject( projectId, false );

            int state;

            ContinuumBuild latestBuild = getLatestBuildForProject( projectId );

            if ( latestBuild == null )
            {
                state = ContinuumProjectState.NEW;
            }
            else
            {
                state = latestBuild.getState();
            }

            projectStateGuard.assertTransition( project, state );

            project.setState( state );

            store.commit();
        }
        catch ( Exception e )
        {
            rollback( store );

            throw new ContinuumStoreException( "Error while setting update scm result.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public ContinuumJPoxStore getStore()
    {
        return store;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void rollback( ContinuumJPoxStore store )
    {
        try
        {
            getLogger().warn( "Rolling back transaction." );

            store.rollback();
        }
        catch ( Exception e )
        {
            getLogger().error( "Error while rolling back tx.", e );
        }
    }
}