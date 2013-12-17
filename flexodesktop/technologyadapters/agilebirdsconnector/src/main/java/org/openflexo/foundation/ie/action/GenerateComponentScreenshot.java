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
package org.openflexo.foundation.ie.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.ScreenshotResource;

public class GenerateComponentScreenshot extends FlexoAction<GenerateComponentScreenshot, IEObject, IEObject> {

	private static final Logger logger = Logger.getLogger(GenerateComponentScreenshot.class.getPackage().getName());

	public static FlexoActionType<GenerateComponentScreenshot, IEObject, IEObject> actionType = new FlexoActionType<GenerateComponentScreenshot, IEObject, IEObject>(
			"generate_screenshot", FlexoActionType.docGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GenerateComponentScreenshot makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new GenerateComponentScreenshot(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return object != null;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, ComponentDefinition.class);
		AgileBirdsObject.addActionForClass(actionType, IEWOComponent.class);
	}

	private boolean _hasBeenRegenerated;

	GenerateComponentScreenshot(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public ComponentDefinition getComponent() {
		if (getFocusedObject() instanceof ComponentDefinition) {
			return (ComponentDefinition) getFocusedObject();
		} else if (getFocusedObject() instanceof IEWOComponent) {
			return ((IEWOComponent) getFocusedObject()).getComponentDefinition();
		} else {
			return null;
		}
	}

	public ScreenshotResource getScreenshotResource() {
		return getComponent().getProject().getScreenshotResource(getComponent());
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getComponent() != null) {
			ScreenshotResource screenshotResource = getScreenshotResource();
			if (screenshotResource == null) {
				logger.info("Create resource for screenshot");
				screenshotResource = ScreenshotResource.createNewScreenshotForObject(getComponent());
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
