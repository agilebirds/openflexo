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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.localization.FlexoLocalization;

public class ShowHidePortmapRegistery extends FlexoUndoableAction<ShowHidePortmapRegistery, WKFObject, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowHidePortmapRegistery.class.getPackage().getName());

	public static FlexoActionType<ShowHidePortmapRegistery, WKFObject, WKFObject> actionType = new FlexoActionType<ShowHidePortmapRegistery, WKFObject, WKFObject>(
			"show_portmap_registery", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ShowHidePortmapRegistery makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new ShowHidePortmapRegistery(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return (object instanceof SubProcessNode && ((SubProcessNode) object).getPortMapRegistery() != null)
					|| object instanceof PortMapRegistery;
		}

		@Override
		public boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	ShowHidePortmapRegistery(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getPortMapRegistery() != null) {
			getPortMapRegistery().setIsVisible(!getPortMapRegistery().getIsVisible());
		}
	}

	@Override
	public String getLocalizedName() {
		if (getPortMapRegistery() != null) {
			if (getPortMapRegistery().getIsVisible()) {
				return FlexoLocalization.localizedForKey("hide_portmap_registery");
			} else {
				return FlexoLocalization.localizedForKey("show_portmap_registery");
			}
		}
		return super.getLocalizedName();

	}

	public PortMapRegistery getPortMapRegistery() {
		if (getFocusedObject() instanceof PortMapRegistery) {
			return (PortMapRegistery) getFocusedObject();
		} else if (getFocusedObject() instanceof SubProcessNode) {
			return ((SubProcessNode) getFocusedObject()).getPortMapRegistery();
		}
		return null;
	}

	@Override
	protected void undoAction(Object context) {
		doAction(context);
	}

	@Override
	protected void redoAction(Object context) {
		doAction(context);
	}

}
