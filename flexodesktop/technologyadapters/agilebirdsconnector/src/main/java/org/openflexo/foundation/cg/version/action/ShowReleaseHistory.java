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
package org.openflexo.foundation.cg.version.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;

public class ShowReleaseHistory extends FlexoGUIAction<ShowReleaseHistory, GenerationRepository, CGObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowReleaseHistory.class.getPackage().getName());

	public static FlexoActionType<ShowReleaseHistory, GenerationRepository, CGObject> actionType = new FlexoActionType<ShowReleaseHistory, GenerationRepository, CGObject>(
			"show_release_history", AbstractGCAction.versionningMenu, AbstractGCAction.versionningShowGroup,
			FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ShowReleaseHistory makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ShowReleaseHistory(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object != null && object.getManageHistory();
		}

	};

	static {
		AgileBirdsObject.addActionForClass(ShowReleaseHistory.actionType, GenerationRepository.class);
	}

	ShowReleaseHistory(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
