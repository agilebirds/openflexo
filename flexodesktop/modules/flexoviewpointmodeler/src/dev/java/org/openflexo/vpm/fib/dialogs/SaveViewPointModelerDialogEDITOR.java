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
package org.openflexo.vpm.fib.dialogs;

import java.io.File;

import org.openflexo.ApplicationContext;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.VPMModule;
import org.openflexo.vpm.controller.VPMController;

public class SaveViewPointModelerDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		VPMController controller = null;
		try {
			VPMModule module = new VPMModule(new ApplicationContext() {

				@Override
				public FlexoEditor makeFlexoEditor(FlexoProject project) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public FlexoEditor createApplicationEditor() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				protected FlexoProjectReferenceLoader createProjectReferenceLoader() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				protected FlexoResourceCenterService createResourceCenterService() {
					return DefaultResourceCenterService.getNewInstance();
				}
			});
			controller = module.getCEDController();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return makeArray(controller);
	}

	@Override
	public File getFIBFile() {
		return CEDCst.SAVE_VPM_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(SaveViewPointModelerDialogEDITOR.class);
	}
}
