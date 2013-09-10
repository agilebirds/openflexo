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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;

public class MoveView extends FlexoAction<MoveView, ViewDefinition, ViewDefinition> {

	private static final Logger logger = Logger.getLogger(MoveView.class.getPackage().getName());

	public static final FlexoActionType<MoveView, ViewDefinition, ViewDefinition> actionType = new FlexoActionType<MoveView, ViewDefinition, ViewDefinition>(
			"move_view") {

		@Override
		public boolean isEnabledForSelection(ViewDefinition object, Vector<ViewDefinition> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(ViewDefinition object, Vector<ViewDefinition> globalSelection) {
			return false;
		}

		@Override
		public MoveView makeNewAction(ViewDefinition focusedObject, Vector<ViewDefinition> globalSelection, FlexoEditor editor) {
			return new MoveView(focusedObject, globalSelection, editor);
		}

	};

	private ViewFolder folder;

	static {
		FlexoModelObject.addActionForClass(actionType, ViewDefinition.class);
	}

	protected MoveView(ViewDefinition focusedObject, Vector<ViewDefinition> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFolder() == null) {
			logger.warning("Cannot move: null folder");
			return;
		}
		for (ViewDefinition v : getGlobalSelection()) {
			moveToFolder(v, folder);
		}
	}

	private void moveToFolder(ViewDefinition v, ViewFolder folder) {
		ViewFolder oldFolder = v.getFolder();
		// Hack: we have first to load the view, to prevent a null value returned by FlexoOEShemaResource.getSchemaDefinition()
		// After the view is removed from initial folder
		v.getShema();
		oldFolder.removeFromShemas(v);
		folder.addToShemas(v);
	}

	public ViewFolder getFolder() {
		return folder;
	};

	public void setFolder(ViewFolder folder) {
		this.folder = folder;
	}
}
