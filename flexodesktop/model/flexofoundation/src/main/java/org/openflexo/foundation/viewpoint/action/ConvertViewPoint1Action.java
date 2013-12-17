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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.foundation.viewpoint.rm.ViewPointResourceImpl;
import org.openflexo.logging.FlexoLogger;

/**
 * This action is called to convert a ViewPoint from 1.5 architecture to 1.6 architecture
 * 
 * @author sylvain
 * 
 */
public class ConvertViewPoint1Action extends FlexoAction<ConvertViewPoint1Action, FlexoObject, FlexoObject> {

	private static final Logger logger = FlexoLogger.getLogger(ConvertViewPoint1Action.class.getPackage().getName());

	public static FlexoActionType<ConvertViewPoint1Action, FlexoObject, FlexoObject> actionType = new FlexoActionType<ConvertViewPoint1Action, FlexoObject, FlexoObject>(
			"convert_viewpoint", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ConvertViewPoint1Action makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new ConvertViewPoint1Action(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object instanceof ViewPointResource && !((ViewPointResource) object).isLoaded()
					&& ((ViewPointResource) object).isDeprecatedVersion();
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ConvertViewPoint1Action.actionType, FlexoObject.class);
	}

	ConvertViewPoint1Action(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject() instanceof ViewPointResourceImpl) {
			if (((ViewPointResource) getFocusedObject()).isDeprecatedVersion()) {
				ViewPointResource res = (ViewPointResource) getFocusedObject();
				FlexoProgress progress = getEditor().getFlexoProgressFactory().makeFlexoProgress("converting_resource", 3);
				progress.setProgress("converting_files");
				ViewPointResourceImpl.convertViewPoint(res);
				res.setModelVersion(res.latestVersion());
				progress.setProgress("loading_view_point");
				try {
					res.loadResourceData(progress);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FlexoException(e);
				}
				progress.setProgress("saving_view_point");
				res.save(progress);
				progress.hideWindow();
			}
		}
	}
}
