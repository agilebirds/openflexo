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
package org.openflexo.rest.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;

public class UploadProjectAction extends FlexoGUIAction<UploadProjectAction, FlexoProject, FlexoProject> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(UploadProjectAction.class.getPackage().getName());

	public static final FlexoActionType<UploadProjectAction, FlexoProject, FlexoProject> actionType = new FlexoActionType<UploadProjectAction, FlexoProject, FlexoProject>(
			"upload_prj", FlexoActionType.defaultGroup) {

		@Override
		public UploadProjectAction makeNewAction(FlexoProject focusedObject, Vector<FlexoProject> globalSelection, FlexoEditor editor) {
			return new UploadProjectAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProject object, Vector<FlexoProject> globalSelection) {
			return true;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	protected UploadProjectAction(FlexoProject focusedObject, Vector<FlexoProject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
