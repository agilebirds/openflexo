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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.WKFObject;

public class UngroupActivities extends FlexoAction<UngroupActivities, ActivityGroup, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(UngroupActivities.class.getPackage().getName());

	public static FlexoActionType<UngroupActivities, ActivityGroup, WKFObject> actionType = new FlexoActionType<UngroupActivities, ActivityGroup, WKFObject>(
			"ungroup_activities", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public UngroupActivities makeNewAction(ActivityGroup focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new UngroupActivities(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ActivityGroup group, Vector<WKFObject> globalSelection) {
			return (group != null);
		}

		@Override
		public boolean isEnabledForSelection(ActivityGroup object, Vector<WKFObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(UngroupActivities.actionType, ActivityGroup.class);
	}

	UngroupActivities(ActivityGroup focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		// TODO Auto-generated method stub
		System.out.println("Ungroup activities");
		System.out.println("focused = " + getFocusedObject());
		System.out.println("global selection = " + getGlobalSelection());

		getFocusedObject().ungroup();
	}

}
