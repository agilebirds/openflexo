/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.module;

import junit.framework.Assert;

public class ModuleLoaderTest extends AbstractGuiTest {

    public void testInitWithoutUserTypeFail(){
        try{
            Assert.assertFalse("currentUserType must be null when starting a AbstractGuiTest",UserType.isCurrentUserTypeDefined());
            ModuleLoader.instance();
            fail("ModuleLoader.instance() must fail when there is no user type.");
        }catch(IllegalStateException e){
            //that's expected
        }
    }

    public void testInitWithUserTypeSuccess(){
        doInitModuleLoader();
    }

    public void testNoModuleAvailable(){
        doInitModuleLoader();
        assertNoModuleAvailable();
    }

    public void testNoModuleLoaded(){
        doInitModuleLoader();
        assertNoModuleLoaded();
    }

    private void doInitModuleLoader(){
        try{
            Assert.assertFalse("currentUserType must be null when starting a AbstractGuiTest",UserType.isCurrentUserTypeDefined());
            UserType.setCurrentUserType(UserType.CUSTOMER);
            ModuleLoader.instance();
        }catch(IllegalStateException e){
            fail("ModuleLoader.instance() must not fail when there is a user type.");
        }
    }

    private void assertNoModuleAvailable(){
        assertTrue(ModuleLoader.instance().availableModules().size() == 0);
    }

    private void assertNoModuleLoaded(){
        assertFalse(ModuleLoader.instance().loadedModules().hasMoreElements());
    }
}
