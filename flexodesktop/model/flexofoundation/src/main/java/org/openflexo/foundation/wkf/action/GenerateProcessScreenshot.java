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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.wkf.FlexoProcess;

public class GenerateProcessScreenshot extends FlexoAction<GenerateProcessScreenshot, FlexoProcess, FlexoProcess> {

	private static final Logger logger = Logger.getLogger(GenerateProcessScreenshot.class.getPackage().getName());

	public static FlexoActionType<GenerateProcessScreenshot, FlexoProcess, FlexoProcess> actionType = new FlexoActionType<GenerateProcessScreenshot, FlexoProcess, FlexoProcess>(
			"generate_screenshot", FlexoActionType.docGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateProcessScreenshot makeNewAction(FlexoProcess focusedObject, Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
			return new GenerateProcessScreenshot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return object != null;
		}

	};

	private FlexoProject _project;
	private boolean _hasBeenRegenerated;

	protected GenerateProcessScreenshot(FlexoProcess focusedObject, Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcess() {
		return getFocusedObject();
	}

	public ScreenshotResource getScreenshotResource() {
		return getProcess().getProject().getScreenshotResource(getProcess());
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getProcess() != null) {
			ScreenshotResource screenshotResource = getScreenshotResource();
			if (screenshotResource == null) {
				logger.info("Create resource for screenshot of process " + getProcess());
				screenshotResource = ScreenshotResource.createNewScreenshotForObject(getProcess());
			} else {
				logger.info("Resource for screenshot has been found");
			}
			_hasBeenRegenerated = screenshotResource.ensureGenerationIsUpToDate();
		} else {
			logger.warning("Focused object is null !");
		}
	}

	public boolean hasBeenRegenerated() {
		return _hasBeenRegenerated;
	}

}
