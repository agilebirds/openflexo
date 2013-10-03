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
package org.openflexo.ve.fib.dialogs;

import java.io.File;

import org.openflexo.fib.ProjectDialogEDITOR;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance.CreateConcreteVirtualModelInstance;
import org.openflexo.toolbox.FileResource;
import org.openflexo.ve.VECst;

public class ConfigureModelInstanceDialogEDITOR extends ProjectDialogEDITOR {

	@Override
	public Object[] getData() {
		FlexoEditor editor = loadProject(new FileResource("TestProjects/1.6/Test1.6.prj"));
		FlexoProject project = editor.getProject();
		View v = project.getViewLibrary().getRootFolder().getResources().get(0).getView();
		CreateConcreteVirtualModelInstance action = CreateVirtualModelInstance.actionType.makeNewAction(v, null, editor);
		action.setVirtualModel(v.getViewPoint().getVirtualModels().get(0));
		return makeArray(action.getModelSlotInstanceConfiguration(action.getVirtualModel().getModelSlots().get(0)));
	}

	@Override
	public File getFIBFile() {
		return VECst.CONFIGURE_FREE_MODEL_SLOT_INSTANCE_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(ConfigureModelInstanceDialogEDITOR.class);
	}

}
