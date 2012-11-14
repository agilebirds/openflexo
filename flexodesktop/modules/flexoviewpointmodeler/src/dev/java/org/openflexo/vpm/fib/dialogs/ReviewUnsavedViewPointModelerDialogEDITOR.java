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
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.VPMModule;
import org.openflexo.vpm.controller.VPMController;

public class ReviewUnsavedViewPointModelerDialogEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				VPMController controller = null;
				try {
					VPMModule module = new VPMModule();
					controller = module.getCEDController();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return makeArray(controller);
			}

			@Override
			public File getFIBFile() {
				return CEDCst.REVIEW_UNSAVED_VPM_DIALOG_FIB;
			}
		};
		editor.launch();
	}
}
