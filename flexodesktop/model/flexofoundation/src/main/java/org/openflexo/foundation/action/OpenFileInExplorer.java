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
package org.openflexo.foundation.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public class OpenFileInExplorer extends FlexoAction<OpenFileInExplorer, FlexoModelObject, FlexoModelObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OpenFileInExplorer.class.getPackage().getName());

	public static FlexoActionType<OpenFileInExplorer, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<OpenFileInExplorer, FlexoModelObject, FlexoModelObject>(
			ToolBox.getPLATFORM() == ToolBox.MACOS ? "open_in_finder" : "open_in_explorer", FlexoActionType.defaultGroup,
			FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public OpenFileInExplorer makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new OpenFileInExplorer(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

	};

	private File fileToOpen;

	public OpenFileInExplorer(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {

		if (fileToOpen != null && fileToOpen.exists()) {
			try {
				ToolBox.showFileInExplorer(fileToOpen);
			} catch (IOException e) {
				throw new IOFlexoException(e);
			}
		}
	}

	public File getFileToOpen() {
		return fileToOpen;
	}

	public void setFileToOpen(File fileToOpen) {
		this.fileToOpen = fileToOpen;
	}

}
