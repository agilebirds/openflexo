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
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;

public class OpenActionLevel extends FlexoUndoableAction<OpenActionLevel, OperationNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(OpenActionLevel.class.getPackage().getName());

	public static FlexoActionType<OpenActionLevel, OperationNode, WKFObject> actionType = new FlexoActionType<OpenActionLevel, OperationNode, WKFObject>(
			"open_action_level", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenActionLevel makeNewAction(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenActionLevel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return ((object != null) && object.mightHaveActionPetriGraph() && ((object).getNodeType() == NodeType.NORMAL));
		}

		@Override
		public boolean isEnabledForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return (object.getNodeType() == NodeType.NORMAL);
		}

	};

	OpenActionLevel(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getFocusedObject().mightHaveActionPetriGraph()) {
			if (getFocusedObject().getActionPetriGraph() == null) {
				// We use here a null editor because this action is embedded
				CreatePetriGraph.actionType.makeNewEmbeddedAction(getFocusedObject(), null, this).doAction();
			}
			logger.info("OpenActionLevel");
			if (getFocusedObject().getActionPetriGraph() != null) {
				if (getFocusedObject().getActionPetriGraph().getIsVisible()) {
					getFocusedObject().getActionPetriGraph().setIsVisible(false);
					getFocusedObject().setChanged();
					getFocusedObject().notifyObservers(new PetriGraphHasBeenClosed(getFocusedObject().getActionPetriGraph()));
				} else {
					getFocusedObject().getActionPetriGraph().setIsVisible(true);
					getFocusedObject().setChanged();
					getFocusedObject().notifyObservers(new PetriGraphHasBeenOpened(getFocusedObject().getActionPetriGraph()));
					getFocusedObject().getActionPetriGraph().setChanged();
					getFocusedObject().getActionPetriGraph().notifyObservers(
							new PetriGraphHasBeenOpened(getFocusedObject().getActionPetriGraph()));
				}
			}
		}
	}

	@Override
	public String getLocalizedName() {
		if ((getFocusedObject()).getActionPetriGraph() != null) {
			if ((getFocusedObject()).getActionPetriGraph().getIsVisible()) {
				return FlexoLocalization.localizedForKey("close_action_level");
			} else {
				return FlexoLocalization.localizedForKey("open_action_level");
			}
		}
		return super.getLocalizedName();

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
