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

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.action.CreateViewPoint;
import org.openflexo.vpm.CEDCst;

public class CreateViewPointDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		FlexoResourceCenter resourceCenter = DefaultResourceCenterService.getNewInstance().getOpenFlexoResourceCenter();
		ViewPointLibrary calcLibrary = resourceCenter.retrieveViewPointLibrary();
		CreateViewPoint action = CreateViewPoint.actionType.makeNewAction(calcLibrary, null, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return CEDCst.CREATE_VIEW_POINT_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(CreateViewPointDialogEDITOR.class);
	}

}
