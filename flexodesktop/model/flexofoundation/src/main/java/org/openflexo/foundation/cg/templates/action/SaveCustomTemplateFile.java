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
package org.openflexo.foundation.cg.templates.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.toolbox.ToolBox;

public class SaveCustomTemplateFile extends FlexoAction<SaveCustomTemplateFile, CGTemplateFile, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(SaveCustomTemplateFile.class.getPackage().getName());

	public static FlexoActionType<SaveCustomTemplateFile, CGTemplateFile, CGTemplateObject> actionType = new FlexoActionType<SaveCustomTemplateFile, CGTemplateFile, CGTemplateObject>(
			"save_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public SaveCustomTemplateFile makeNewAction(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new SaveCustomTemplateFile(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection) {
			return ((object != null) && (object.isCustomTemplate()));
		}

		@Override
		protected boolean isEnabledForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection) {
			return ((object != null) && (object.isCustomTemplate()) && (object.isEdited()));
		}

	};

	static {
		FlexoModelObject.addActionForClass(SaveCustomTemplateFile.actionType, CGTemplateFile.class);
	}

	SaveCustomTemplateFile(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException {
		logger.info("Save CustomTemplateFile " + getFocusedObject());
		if (getFocusedObject() != null) {
			File file = getFocusedObject().getTemplateFile();
			long previousLastModified = file.lastModified();
			getFocusedObject().save();
			if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
				long startChecking = System.currentTimeMillis();
				logger.info("Checking that file " + file.getAbsolutePath() + " has been successfully written");
				boolean fileHasBeenWritten = false;
				while (System.currentTimeMillis() <= startChecking + FlexoFileResource.ACCEPTABLE_FS_DELAY && !fileHasBeenWritten) {
					if (previousLastModified == 0) {
						fileHasBeenWritten = file.exists();
					} else {
						fileHasBeenWritten = file.lastModified() > previousLastModified;
					}
					if (!fileHasBeenWritten) {
						logger.info("Waiting file " + file.getAbsolutePath() + " to be written");
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (logger.isLoggable(Level.INFO))
						logger.info("Writing of template: " + getFocusedObject().getName() + " took "
								+ (file.lastModified() - previousLastModified) + "ms");
				}
				if (!fileHasBeenWritten)
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Waited for " + FlexoFileResource.ACCEPTABLE_FS_DELAY + "ms but file was still not written on disk!");
			}
		}
	}

}
