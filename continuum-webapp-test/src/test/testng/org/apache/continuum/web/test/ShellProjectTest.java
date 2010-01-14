package org.apache.continuum.web.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.continuum.web.test.parent.AbstractContinuumTest;
import org.testng.annotations.Test;

/**
 * Based on AddShellProjectTestCase of Emmanuel Venisse.
 *
 * @author José Morales Martínez
 * @version $Id$
 */
@Test( groups = { "shellProject" }, dependsOnMethods = { "testWithCorrectUsernamePassword" } )
public class ShellProjectTest
    extends AbstractContinuumTest
{
    // TODO: seems like a bug, currently giving "Project Group is required and all projects must be in a project group", and returning to the Ant add page?
    @Test( enabled = false )
    public void testAddShellProject()
        throws Exception
    {
        String SHELL_NAME = getProperty( "SHELL_NAME" );
        String SHELL_DESCRIPTION = getProperty( "SHELL_DESCRIPTION" );
        String SHELL_VERSION = getProperty( "SHELL_VERSION" );
        String SHELL_TAG = getProperty( "SHELL_TAG" );
        String SHELL_SCM_URL = getProperty( "SHELL_SCM_URL" );
        String SHELL_SCM_USERNAME = getProperty( "SHELL_SCM_USERNAME" );
        String SHELL_SCM_PASSWORD = getProperty( "SHELL_SCM_PASSWORD" );
        String DEFAULT_PROJ_GRP_NAME = getProperty( "DEFAULT_PROJ_GRP_NAME" );
        String DEFAULT_PROJ_GRP_ID = getProperty( "DEFAULT_PROJ_GRP_ID" );
        String DEFAULT_PROJ_GRP_DESCRIPTION = getProperty( "DEFAULT_PROJ_GRP_DESCRIPTION" );
        goToAddShellProjectPage();
        addProject( SHELL_NAME, SHELL_DESCRIPTION, SHELL_VERSION, SHELL_SCM_URL, SHELL_SCM_USERNAME,
                    SHELL_SCM_PASSWORD, SHELL_TAG, false, DEFAULT_PROJ_GRP_NAME, null, true );
        assertProjectGroupSummaryPage( DEFAULT_PROJ_GRP_NAME, DEFAULT_PROJ_GRP_ID, DEFAULT_PROJ_GRP_DESCRIPTION );
    }

    public void testSubmitEmptyForm()
    {
        goToAddShellProjectPage();
        submit();
        assertAddProjectPage( "shell" );
        assertTextPresent( "Name is required and cannot contain null or spaces only" );
        assertTextPresent( "Version is required and cannot contain null or spaces only" );
        assertTextPresent( "SCM Url is required and cannot contain null or spaces only" );
    }

    @Test( dependsOnMethods = { "testAddShellProject" } )
    public void testAddDupliedShellProject()
        throws Exception
    {
        String SHELL_NAME = getProperty( "SHELL_NAME" );
        String SHELL_DESCRIPTION = getProperty( "SHELL_DESCRIPTION" );
        String SHELL_VERSION = getProperty( "SHELL_VERSION" );
        String SHELL_TAG = getProperty( "SHELL_TAG" );
        String SHELL_SCM_URL = getProperty( "SHELL_SCM_URL" );
        String SHELL_SCM_USERNAME = getProperty( "SHELL_SCM_USERNAME" );
        String SHELL_SCM_PASSWORD = getProperty( "SHELL_SCM_PASSWORD" );
        goToAddShellProjectPage();
        addProject( SHELL_NAME, SHELL_DESCRIPTION, SHELL_VERSION, SHELL_SCM_URL, SHELL_SCM_USERNAME,
                    SHELL_SCM_PASSWORD, SHELL_TAG, false, null, null, false );
        assertTextPresent( "Project name already exist" );
    }
}
