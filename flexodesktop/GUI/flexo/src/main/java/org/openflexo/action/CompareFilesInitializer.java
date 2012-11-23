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
package org.openflexo.action;

import java.awt.event.ActionEvent;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CompareFiles;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.popups.FileDiffEditorPopup;

/**
 * @author gpolet
 * 
 */
public class CompareFilesInitializer extends ActionInitializer<CompareFiles, CGFile, CGFile> {

	/**
	 * @param actionType
	 * @param controllerActionInitializer
	 */
	public CompareFilesInitializer(ControllerActionInitializer controllerActionInitializer) {
		super(CompareFiles.actionType, controllerActionInitializer);
	}

	protected static FlexoFrame getActiveModuleFrame() {
		if (FlexoModule.getActiveModule() != null) {
			return FlexoModule.getActiveModule().getFlexoFrame();
		} else {
			return null;
		}
	}

	/**
	 * Overrides getDefaultInitializer
	 * 
	 * @see org.openflexo.view.controller.ActionInitializer#getDefaultInitializer()
	 */
	@Override
	protected FlexoActionInitializer<CompareFiles> getDefaultInitializer() {

		return new FlexoActionInitializer<CompareFiles>() {

			@Override
			public boolean run(ActionEvent event, CompareFiles action) {
				return true;
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<? super CompareFiles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CompareFiles>() {

			@Override
			public boolean run(ActionEvent event, CompareFiles action) {

				CGFile file1 = action.getFiles().get(0);
				CGFile file2 = action.getFiles().get(1);
				String content1 = null, content2 = null;
				if (file1.getGeneratedResourceData() instanceof ASCIIFile
						&& ((ASCIIFile) file1.getGeneratedResourceData()).getContentToWriteOnDisk() != null
						&& ((ASCIIFile) file1.getGeneratedResourceData()).getCurrentGeneration() != null) {
					content1 = ((ASCIIFile) file1.getGeneratedResourceData()).getContentToWriteOnDisk();
				}
				if (file2.getGeneratedResourceData() instanceof ASCIIFile
						&& ((ASCIIFile) file2.getGeneratedResourceData()).getContentToWriteOnDisk() != null
						&& ((ASCIIFile) file2.getGeneratedResourceData()).getCurrentGeneration() != null) {
					content2 = ((ASCIIFile) file2.getGeneratedResourceData()).getContentToWriteOnDisk();
				}
				if (content1 != null && content2 != null) {
					FileDiffEditorPopup popup = new FileDiffEditorPopup(file1.getDisplayableName(), file2.getDisplayableName(), content1,
							content2, getController());
					popup.show();
					return true;
				}
				return false;
			}

		};
	}

}
