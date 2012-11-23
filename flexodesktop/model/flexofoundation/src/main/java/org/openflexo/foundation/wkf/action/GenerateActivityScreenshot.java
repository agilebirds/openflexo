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
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public class GenerateActivityScreenshot extends FlexoAction<GenerateActivityScreenshot, AbstractActivityNode, AbstractActivityNode> {

	private static final Logger logger = Logger.getLogger(GenerateActivityScreenshot.class.getPackage().getName());

	public static FlexoActionType<GenerateActivityScreenshot, AbstractActivityNode, AbstractActivityNode> actionType = new FlexoActionType<GenerateActivityScreenshot, AbstractActivityNode, AbstractActivityNode>(
			"generate_screenshot", FlexoActionType.docGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateActivityScreenshot makeNewAction(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection,
				FlexoEditor editor) {
			return new GenerateActivityScreenshot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return object != null;
		}

	};

	private boolean _hasBeenRegenerated;

	GenerateActivityScreenshot(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public AbstractActivityNode getActivity() {
		if (getFocusedObject() != null) {
			return getFocusedObject();
		}
		return null;
	}

	public ScreenshotResource getScreenshotResource() {
		return getActivity().getProject().getScreenshotResource(getActivity());
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getActivity() != null) {
			ScreenshotResource screenshotResource = getScreenshotResource();
			if (screenshotResource == null) {
				logger.info("Create resource for screenshot of activity " + getActivity().getName());
				screenshotResource = ScreenshotResource.createNewScreenshotForObject(getActivity());
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
