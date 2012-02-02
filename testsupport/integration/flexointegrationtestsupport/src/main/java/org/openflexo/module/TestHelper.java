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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.ToolBox;

public class TestHelper {

    public static FlexoEditor createSampleEmptyProject() {
        ToolBox.setPlatform();
        FlexoLoggingManager.forceInitialize();
        File _projectDirectory = null;
        try {
            File tempFile = File.createTempFile("SampleProject", "");
            _projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Cannot create sample projet :"+e.getMessage());
        }
        return ProjectLoader.instance().newProject(_projectDirectory);
    }

    public static File setupResourceCenter(){
        File resourceCenterDirectory = null;

       try {
            File tempFile = File.createTempFile("TestResourceCenter", "");
            resourceCenterDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Cannot create resource center :"+e.getMessage());
        }

        FlexoResourceCenterService.instance().createAndSetFlexoResourceCenter(resourceCenterDirectory);
        return resourceCenterDirectory;
    }

    public static File setupApplicationDataDirectory(){
        File applicationDataDirectory = null;

       try {
            File tempFile = File.createTempFile("TestApplicationData", "");
            applicationDataDirectory = new File(tempFile.getParentFile(), tempFile.getName() + "-appdata");
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Cannot create resource center :"+e.getMessage());
        }

        applicationDataDirectory.mkdirs();
        return applicationDataDirectory;
    }
}
