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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.wkf.node.OperationNode;

public class GenerateOperationScreenshot extends FlexoAction<GenerateOperationScreenshot, OperationNode, OperationNode> {

	private static final Logger logger = Logger.getLogger(GenerateOperationScreenshot.class.getPackage().getName());

	public static FlexoActionType<GenerateOperationScreenshot, OperationNode, OperationNode> actionType = new FlexoActionType<GenerateOperationScreenshot, OperationNode, OperationNode>(
			"generate_screenshot", FlexoActionType.docGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateOperationScreenshot makeNewAction(OperationNode focusedObject, Vector<OperationNode> globalSelection,
				FlexoEditor editor) {
			return new GenerateOperationScreenshot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OperationNode object, Vector<OperationNode> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(OperationNode object, Vector<OperationNode> globalSelection) {
			return object != null;
		}

	};

	private boolean _hasBeenRegenerated;

	GenerateOperationScreenshot(OperationNode focusedObject, Vector<OperationNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public OperationNode getOperation() {
		if (getFocusedObject() != null) {
			return getFocusedObject();
		}
		return null;
	}

	public ScreenshotResource getScreenshotResource() {
		return getOperation().getProject().getScreenshotResource(getOperation());
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getOperation() != null) {
			ScreenshotResource screenshotResource = getScreenshotResource();
			if (screenshotResource == null) {
				logger.info("Create resource for screenshot of operation " + getOperation().getName());
				screenshotResource = ScreenshotResource.createNewScreenshotForObject(getOperation());
			} else {
				logger.info("Resource for screenshot has been found");
			}
			_hasBeenRegenerated = screenshotResource.ensureGenerationIsUpToDate();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Screenshot generated");
			}
		} else {
			logger.warning("Focused object is null !");
		}
	}

	public boolean hasBeenRegenerated() {
		return _hasBeenRegenerated;
	}

}
