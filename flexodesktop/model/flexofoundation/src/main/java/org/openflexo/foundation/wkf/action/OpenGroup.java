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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.localization.FlexoLocalization;

public class OpenGroup extends FlexoUndoableAction<OpenGroup, WKFGroup, WKFGroup> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenGroup.class.getPackage().getName());

	public static FlexoActionType<OpenGroup, WKFGroup, WKFGroup> actionType = new FlexoActionType<OpenGroup, WKFGroup, WKFGroup>(
			"open_group", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenGroup makeNewAction(WKFGroup focusedObject, Vector<WKFGroup> globalSelection, FlexoEditor editor) {
			return new OpenGroup(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(WKFGroup object, Vector<WKFGroup> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(WKFGroup object, Vector<WKFGroup> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, WKFGroup.class);
	}

	private Boolean visibility;

	OpenGroup(WKFGroup focusedObject, Vector<WKFGroup> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		getFocusedObject().setIsVisible(visibility());
	}

	@Override
	public String getLocalizedName() {
		if (getFocusedObject().isExpanded()) {
			return FlexoLocalization.localizedForKey("close_group");
		} else {
			return FlexoLocalization.localizedForKey("open_group");
		}

	}

	@Override
	protected void undoAction(Object context) {
		setVisibility(!visibility());
		doAction(context);
	}

	@Override
	protected void redoAction(Object context) {
		setVisibility(!visibility());
		doAction(context);
	}

	public boolean visibility() {
		if (visibility == null)
			visibility = !getFocusedObject().getIsVisible();
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
}
