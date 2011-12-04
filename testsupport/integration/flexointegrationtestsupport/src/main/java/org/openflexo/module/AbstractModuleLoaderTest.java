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

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractModuleLoaderTest {
    abstract protected UserType definedUserType();

    private UserType userTypeBackUp = null;

    @Before
    public void setUp() throws Exception {
        userTypeBackUp = setUserTypeFieldByReflection(null);
    }

    @After
    public void tearDown() throws Exception {
        setUserTypeFieldByReflection(userTypeBackUp);
    }

    @Test
    public void testClasspath() {
        for (Module module : definedUserType().getModules()) {
            Class moduleClass = assertModuleClassIsOnTheClasspath(module);
            assertModuleClassIsModule(moduleClass);
        }
    }

    @Test
    public void testNoModuleLoaded() {
        doInitModuleLoader();
        assertNoModuleLoaded();
    }

    @Test
    public void testIsAvailable() {
        doInitModuleLoader();
        for (Module expectedModule : definedUserType().getModules()) {
            Assert.assertTrue(expectedModule.getName() + " must be available in "
                              + definedUserType().getBusinessName2(), ModuleLoader.instance().isAvailable(expectedModule));
        }
    }

    @Test
    public void testAtLeastOneModuleDefine(){
        Assert.assertTrue("There must be at least one module define in "+definedUserType().getBusinessName2(),
                definedUserType().getModules().size()>0);
    }

    @Test
    public void testModuleNotRequiringProjectCanBeLoadedWithNullProject(){
        doInitModuleLoader();
        ArrayList<Module> loadedModules = new ArrayList<Module>();
        for(Module module:ModuleLoader.instance().availableModules()){
            if(!module.requireProject()){
                loadedModules.add(module);
                try {
                    ModuleLoader.instance().getModuleInstance(module, null);
                } catch (ModuleLoadingException e) {
                    Assert.fail("Not expecting a ModuleLoading exception." + e.getMessage());
                }
                assertIsLoaded(module);
            }
        }
        //now that we have loaded all modules, check once again that they still all loaded.
        for(Module module:loadedModules){
            assertIsLoaded(module);
        }
        //check that none of the modules requiring a project are loaded
        for(Module module:ModuleLoader.instance().availableModules()){
            if(module.requireProject()){
                assertNotLoaded(module);
            }
        }

    }

    private void assertNotLoaded(Module module) {
       Assert.assertFalse("module "+module.getName()+" must NOT be loaded, but ModuleLoader pretend it is.",
                        ModuleLoader.instance().isLoaded(module));
    }
    private void assertIsLoaded(Module module){
          Assert.assertTrue("module "+module.getName()+" must be loaded, but ModuleLoader pretend it is not.",
                        ModuleLoader.instance().isLoaded(module));
    }

    private void assertNoModuleLoaded() {
        Assert.assertFalse("After moduleLoader initialisation : there shouldn't be any loaded module.",
                ModuleLoader.instance().loadedModules().hasMoreElements());
    }

    protected void doInitModuleLoader() {
        try {
            Assert.assertFalse("currentUserType must be null when starting a AbstractGuiTest", UserType.isCurrentUserTypeDefined());
            UserType.setCurrentUserType(definedUserType());
            ModuleLoader.instance();
        } catch (IllegalStateException e) {
            Assert.fail("ModuleLoader.instance() must not fail when there is a user type.");
        }
    }

    private Class assertModuleClassIsOnTheClasspath(Module module) {
        Class moduleClass = null;
        try {
            moduleClass = Class.forName(module.getClassName());
        } catch (ClassNotFoundException e) {
            Assert.fail(
                    "Release " + definedUserType().getBusinessName2() + " must contains module " + module.getName() + " defined by class "
                    + ""
                    + module.getClassName() + ". But this class is not on the classpath.\n" + e.getMessage());
        }
        return moduleClass;
    }

    private void assertModuleClassIsModule(Class moduleClass) {
        Assert.assertTrue(
                "Class " + moduleClass + " must extends org.openflexo.module.Module.", FlexoModule.class.isAssignableFrom(moduleClass));
    }

    private UserType setUserTypeFieldByReflection(UserType valueToSet){
         Field currentUserTypeField = null;
         try{
             currentUserTypeField = UserType.class.getDeclaredField("currentUserType");
         }catch(NoSuchFieldException e){
             junit.framework.Assert.fail("Class UserType must have a static field named 'currentUserType'.");
         }
         UserType reply = null;
         boolean isAccessible = currentUserTypeField.isAccessible();
         try{
             currentUserTypeField.setAccessible(true);
             reply = (UserType)currentUserTypeField.get(UserType.class);
             currentUserTypeField.set(UserType.class,valueToSet);
         } catch(IllegalAccessException e){
             junit.framework.Assert.fail("UserType.currentUserType should be accessible.");
         }finally {
             currentUserTypeField.setAccessible(isAccessible);
         }
         return reply;

     }
}
