package org.apache.continuum.distributed.transport.slave;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;
import java.util.Map;

import org.apache.continuum.buildagent.ContinuumBuildAgentException;
import org.apache.continuum.buildagent.ContinuumBuildAgentService;
import org.apache.continuum.buildagent.model.Installation;
import org.apache.continuum.distributed.transport.SlaveBuildAgentTransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProxyMasterBuildAgentTransportService
 */
public class DefaultSlaveBuildAgentTransportService
    implements SlaveBuildAgentTransportService
{
    private Logger log = LoggerFactory.getLogger( this.getClass() );
    
    private ContinuumBuildAgentService continuumBuildAgentService;
    
    public DefaultSlaveBuildAgentTransportService( ContinuumBuildAgentService continuumBuildAgentService )
    {
        this.continuumBuildAgentService = continuumBuildAgentService;
    }
    
    public Boolean buildProjects( List<Map> projectsBuildContext )
        throws Exception
    {
        Boolean result = Boolean.FALSE;
        
        try
        {
            continuumBuildAgentService.buildProjects( projectsBuildContext );
            result = Boolean.TRUE;
            
            log.info( "Building projects." );
        }
        catch ( ContinuumBuildAgentException e )
        {
            log.error( "Failed to build projects.", e );
        }
        
        return result;
    }

    // TODO: fix this
    public List<Object> getAvailableInstallations()
        throws Exception
    {
        List<Object> installationsObj = null;

        List<Installation> installations = null;
        
        try
        {
            installations = continuumBuildAgentService.getAvailableInstallations();
            log.info( "Available installations: " + installations.size() );
        }
        catch ( ContinuumBuildAgentException e )
        {
            log.error( "Failed to get available installations.", e );
        }

        return installationsObj;
    }

    public Map getBuildResult( int projectId )
        throws Exception
    {
        Map buildResult = null;
        
        try
        {
            continuumBuildAgentService.getBuildResult( projectId );
            log.info( "Build result for project " + projectId + " acquired." );
        }
        catch ( ContinuumBuildAgentException e )
        {
            log.error( "Failed to get build result for project " + projectId, e );
        }
        
        return buildResult;
    }

    public Integer getProjectCurrentlyBuilding()
        throws Exception
    {
        Integer projectId = new Integer( continuumBuildAgentService.getProjectCurrentlyBuilding() );
        
        log.info( "Currently building project " + projectId.intValue() );
        
        return projectId;
    }

    public Boolean isBusy()
        throws Exception
    {
        Boolean busy = null;
        try
        {
            busy = new Boolean( continuumBuildAgentService.isBusy() );
            log.info( "Build agent is " + ( busy ? "" : "not" ) + " busy." );
        }
        catch ( ContinuumBuildAgentException e )
        {
            log.error( "Failed to determine if master is busy.", e );
        }
        
        return busy;
    }

    public Boolean ping()
        throws Exception
    {
        log.info( "Ping ok" );
        
        return Boolean.TRUE;
    }

    public Boolean cancelBuild()
        throws Exception
    {
        Boolean result = Boolean.FALSE;

        try
        {
            continuumBuildAgentService.cancelBuild();
            result = Boolean.TRUE;
            log.info( "Cancelled build" );
        }
        catch ( ContinuumBuildAgentException e )
        {
            log.error( "Failed to cancel build", e );
        }

        return result;
    }
}
