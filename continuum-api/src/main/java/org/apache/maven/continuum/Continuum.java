package org.apache.maven.continuum;

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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.continuum.configuration.ConfigurationService;
import org.apache.maven.continuum.execution.ContinuumBuildExecutor;
import org.apache.maven.continuum.model.project.BuildDefinition;
import org.apache.maven.continuum.model.project.BuildResult;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.project.ProjectGroup;
import org.apache.maven.continuum.model.project.ProjectNotifier;
import org.apache.maven.continuum.model.project.Schedule;
import org.apache.maven.continuum.model.system.ContinuumUser;
import org.apache.maven.continuum.model.system.UserGroup;
import org.apache.maven.continuum.project.builder.ContinuumProjectBuildingResult;
import org.apache.maven.continuum.security.ContinuumSecurity;
import org.apache.maven.continuum.release.ContinuumReleaseManager;
import org.codehaus.plexus.util.dag.CycleDetectedException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface Continuum
{
    String ROLE = Continuum.class.getName();

    // ----------------------------------------------------------------------
    // Project Groups
    // ----------------------------------------------------------------------

    public static final String DEFAULT_PROJECT_GROUP_GROUP_ID = "default";

    public ProjectGroup getProjectGroup( int projectGroupId )
        throws ContinuumException;

    /**
     * Get all {@link ProjectGroup}s and their {@link Project}s
     * 
     * @return {@link Collection} &lt;{@link ProjectGroup}>
     */
    public Collection getAllProjectGroupsWithProjects();

    public ProjectGroup getProjectGroupByProjectId( int projectId )
        throws ContinuumException;

    public Collection getProjectsInGroup( int projectGroupId )
        throws ContinuumException;

    public void removeProjectGroup( int projectGroupId )
        throws ContinuumException;
    
    // ----------------------------------------------------------------------
    // Project
    // ----------------------------------------------------------------------

    void removeProject( int projectId )
        throws ContinuumException;

    void checkoutProject( int projectId )
        throws ContinuumException;

    Project getProject( int projectId )
        throws ContinuumException;

    List getAllProjectsWithAllDetails( int start, int end );

    Collection getAllProjects( int start, int end )
        throws ContinuumException;

    Collection getProjects()
        throws ContinuumException;

    Collection getProjectsWithDependencies()
        throws ContinuumException;

    BuildResult getLatestBuildResultForProject( int projectId );

    Map getLatestBuildResults();

    Map getBuildResultsInSuccess();

    // ----------------------------------------------------------------------
    // Queues
    // ----------------------------------------------------------------------

    boolean isInBuildingQueue( int projectId )
        throws ContinuumException;

    boolean isInBuildingQueue( int projectId, int buildDefinitionId )
        throws ContinuumException;

    boolean isInCheckoutQueue( int projectId )
        throws ContinuumException;
    // ----------------------------------------------------------------------
    // Building
    // ----------------------------------------------------------------------

    List getProjectsInBuildOrder()
        throws CycleDetectedException, ContinuumException;

    void buildProjects()
        throws ContinuumException;

    void buildProjects( int trigger )
        throws ContinuumException;

    void buildProjects( Schedule schedule )
        throws ContinuumException;

    void buildProject( int projectId )
        throws ContinuumException;

    void buildProject( int projectId, int trigger )
        throws ContinuumException;

    void buildProject( int projectId, int buildDefinitionId, int trigger )
        throws ContinuumException;

    public void buildProjectGroup( int projectGroupId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Build information
    // ----------------------------------------------------------------------

    BuildResult getBuildResult( int buildId )
        throws ContinuumException;

    BuildResult getBuildResultByBuildNumber( int projectId, int buildNumber )
        throws ContinuumException;

    String getBuildOutput( int projectId, int buildId )
        throws ContinuumException;

    Collection getBuildResultsForProject( int projectId )
        throws ContinuumException;

    List getChangesSinceLastSuccess( int projectId, int buildResultId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Projects
    // ----------------------------------------------------------------------

    /**
     * Add a project to the list of building projects (ant, shell,...)
     * 
     * @param project the project to add
     * @param executorId the id of an {@link ContinuumBuildExecutor}, eg. <code>ant</code> or <code>shell</code> 
     * @return id of the project
     * @throws ContinuumException
     */
    int addProject( Project project, String executorId )
        throws ContinuumException;

    /**
     * Add a Maven 2 project to the list of projects. 
     * 
     * @param metadataUrl url of the pom.xml
     * @return a holder with the projects, project groups and errors occurred during the project adding
     * @throws ContinuumException
     */
    ContinuumProjectBuildingResult addMavenTwoProject( String metadataUrl )
        throws ContinuumException;

    /**
     * Add a Maven 1 project to the list of projects. 
     * 
     * @param metadataUrl url of the project.xml
     * @return a holder with the projects, project groups and errors occurred during the project adding
     * @throws ContinuumException
     */
   ContinuumProjectBuildingResult addMavenOneProject( String metadataUrl )
        throws ContinuumException;

    void updateProject( Project project )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Notification
    // ----------------------------------------------------------------------

    ProjectNotifier getNotifier( int projectId, int notifierId )
        throws ContinuumException;

    void updateNotifier( int projectId, int notifierId, Map configuration )
        throws ContinuumException;

    void updateNotifier( int projectId, ProjectNotifier notifier )
        throws ContinuumException;

    void addNotifier( int projectId, ProjectNotifier notifier )
        throws ContinuumException;

    void addNotifier( int projectId, String notifierType, Map configuration )
        throws ContinuumException;

    void removeNotifier( int projectId, int notifierId )
        throws ContinuumException;

    Project getProjectWithCheckoutResult( int projectId )
        throws ContinuumException;

    Project getProjectWithAllDetails( int projectId )
        throws ContinuumException;

    Project getProjectWithBuilds( int projectId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Build Definition
    // ----------------------------------------------------------------------

    /**
     * @deprecated
     */
    List getBuildDefinitions( int projectId )
        throws ContinuumException;

    /**
     * @deprecated
     */
    BuildDefinition getBuildDefinition( int projectId, int buildDefinitionId )
        throws ContinuumException;

    /**
     * @deprecated
     */
    void removeBuildDefinition( int projectId, int buildDefinitionId )
        throws ContinuumException;

    /**
     * returns the build definition from either the project or the project group it is a part of
     *
     * @param buildDefinitionId
     * @return
     */
    BuildDefinition getBuildDefinition( int buildDefinitionId )
        throws ContinuumException;

    /**
     * returns the default build definition for the project
     *
     * 1) if project has default build definition, return that
     * 2) otherwise return default build definition for parent project group
     *
     * @param projectId
     * @return
     * @throws ContinuumException
     */
    BuildDefinition getDefaultBuildDefinition( int projectId )
        throws ContinuumException;

    void addBuildDefinitionToProject( int projectId, BuildDefinition buildDefinition )
        throws ContinuumException;

    void addBuildDefinitionToProjectGroup( int projectGroupId, BuildDefinition buildDefinition )
        throws ContinuumException;    

    List getBuildDefinitionsForProject( int projectId )
        throws ContinuumException;

    List getBuildDefinitionsForProjectGroup( int projectGroupId )
        throws ContinuumException;

    void removeBuildDefinitionFromProject( int projectId, int buildDefinitionId )
        throws ContinuumException;

    void removeBuildDefinitionFromProjectGroup( int projectGroupId, int buildDefinitionId )
        throws ContinuumException;

    void updateBuildDefinitionForProject( int projectId, BuildDefinition buildDefinition )
        throws ContinuumException;

    void updateBuildDefinitionForProjectGroup( int projectGroupId, BuildDefinition buildDefinition )
        throws ContinuumException;


    // ----------------------------------------------------------------------
    // Schedule
    // ----------------------------------------------------------------------

    Schedule getSchedule( int id )
        throws ContinuumException;

    Collection getSchedules()
        throws ContinuumException;

    void addSchedule( Schedule schedule )
        throws ContinuumException;

    void updateSchedule( Schedule schedule )
        throws ContinuumException;

    void updateSchedule( int scheduleId, Map configuration )
        throws ContinuumException;

    void removeSchedule( int scheduleId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Working copy
    // ----------------------------------------------------------------------

    File getWorkingDirectory( int projectId )
        throws ContinuumException;

    String getFileContent( int projectId, String directory, String filename )
        throws ContinuumException;

    List getFiles( int projectId, String currentDirectory )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    ConfigurationService getConfiguration();

    void updateConfiguration( Map parameters )
        throws ContinuumException;

    void reloadConfiguration()
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Security
    // ----------------------------------------------------------------------

    ContinuumSecurity getSecurity();

    // ----------------------------------------------------------------------
    // User
    // ----------------------------------------------------------------------

    void addUser( ContinuumUser user )
        throws ContinuumException;

    void addUser( Map configuration )
        throws ContinuumException;

    void updateUser( ContinuumUser user )
        throws ContinuumException;

    void updateUser( int userId, Map configuration )
        throws ContinuumException;

    List getUsers()
        throws ContinuumException;

    ContinuumUser getUser( int userId )
        throws ContinuumException;

    void removeUser( int userId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // User Group
    // ----------------------------------------------------------------------

    void addUserGroup( UserGroup userGroup );

    void addUserGroup( Map configuration )
        throws ContinuumException;

    void updateUserGroup( UserGroup userGroup )
        throws ContinuumException;

    void updateUserGroup( int userGroupId, Map configuration )
        throws ContinuumException;

    List getUserGroups()
        throws ContinuumException;

    UserGroup getUserGroup( int userGroupId )
        throws ContinuumException;

    void removeUserGroup( int userGroupId )
        throws ContinuumException;

    // ----------------------------------------------------------------------
    // Continuum Release
    // ----------------------------------------------------------------------
    ContinuumReleaseManager getReleaseManager();
}
