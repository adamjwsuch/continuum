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

import org.apache.continuum.web.test.parent.AbstractAdminTest;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = { "appearance" })
public class AppearanceTest
    extends AbstractAdminTest
{

    public void testAppearancePage()
    {
        goToAppearancePage();
    }

    @Test(dependsOnMethods = { "testAppearancePage" })
    public void testSaveFooter()
    {
        goToAppearancePage();
        getSelenium().type( "saveFooter_footer",
                            "<div class=\"xright\">Continuum Test Footer</div> <div class=\"clear\"><hr/></div>" );
        clickButtonWithValue( "Save" );

        assertTextPresent( "Successfully saved footer content." );

        String SAVE_FOOTER_URL = getProperty( "APPEARANCE_SAVE_FOOTER_URL" );
        Assert.assertEquals( getSelenium().getLocation(), SAVE_FOOTER_URL );
    }
}
