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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.logging.FlexoLogger;

/**
 * This action is called to load a {@link FlexoResource}
 * 
 * @author sylvain
 * 
 */
public class LoadResourceAction extends FlexoAction<LoadResourceAction, FlexoObject, FlexoObject> {

	private static final Logger logger = FlexoLogger.getLogger(LoadResourceAction.class.getPackage().getName());

	public static FlexoActionType<LoadResourceAction, FlexoObject, FlexoObject> actionType = new FlexoActionType<LoadResourceAction, FlexoObject, FlexoObject>(
			"load_resource", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public LoadResourceAction makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new LoadResourceAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object instanceof FlexoResource && !((FlexoResource) object).isLoaded();
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object instanceof FlexoResource && ((FlexoResource) object).isLoadable() && !((FlexoResource) object).isLoaded();
		}

	};

	static {
		FlexoObject.addActionForClass(LoadResourceAction.actionType, FlexoObject.class);
	}

	LoadResourceAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject() instanceof FlexoResource) {
			if (!((FlexoResource<?>) getFocusedObject()).isLoaded()) {
				FlexoProgress progress = getEditor().getFlexoProgressFactory().makeFlexoProgress("loading_resource", 3);
				try {
					((FlexoResource<?>) getFocusedObject()).getResourceData(progress);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FlexoException(e);
				}
				progress.hideWindow();
			}
		}
	}
}
