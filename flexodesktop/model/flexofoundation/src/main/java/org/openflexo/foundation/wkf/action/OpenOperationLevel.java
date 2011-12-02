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
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.localization.FlexoLocalization;

public class OpenOperationLevel extends FlexoUndoableAction<OpenOperationLevel, AbstractActivityNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(OpenOperationLevel.class.getPackage().getName());

	public static FlexoActionType<OpenOperationLevel, AbstractActivityNode, WKFObject> actionType = new FlexoActionType<OpenOperationLevel, AbstractActivityNode, WKFObject>(
			"open_operation_level", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenOperationLevel makeNewAction(AbstractActivityNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenOperationLevel(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(AbstractActivityNode object, Vector<WKFObject> globalSelection) {
			return object.mightHaveOperationPetriGraph();
		}

		@Override
		protected boolean isEnabledForSelection(AbstractActivityNode object, Vector<WKFObject> globalSelection) {
			return object.mightHaveOperationPetriGraph();
		}

	};

	private Boolean visibility;

	OpenOperationLevel(AbstractActivityNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getFocusedObject().mightHaveOperationPetriGraph()) {
			if (getFocusedObject().getOperationPetriGraph() == null) {
				// We use here a null editor because this action is embedded
				CreatePetriGraph.actionType.makeNewEmbeddedAction(getFocusedObject(), null, this).doAction();
			}
			logger.info("OpenOperationLevel");
			if (getFocusedObject().getOperationPetriGraph() != null) {

				if (!visibility()) {
					getFocusedObject().getOperationPetriGraph().setIsVisible(false);
					getFocusedObject().setChanged();
					getFocusedObject().notifyObservers(new PetriGraphHasBeenClosed(getFocusedObject().getOperationPetriGraph()));
				} else {
					getFocusedObject().getOperationPetriGraph().setIsVisible(true);
					getFocusedObject().setChanged();
					getFocusedObject().notifyObservers(new PetriGraphHasBeenOpened(getFocusedObject().getOperationPetriGraph()));
					getFocusedObject().getOperationPetriGraph().setChanged();
					getFocusedObject().getOperationPetriGraph().notifyObservers(
							new PetriGraphHasBeenOpened(getFocusedObject().getOperationPetriGraph()));
				}
			}
		}
	}

	@Override
	public String getLocalizedName() {
		if ((getFocusedObject()).getOperationPetriGraph() != null) {
			if ((getFocusedObject()).getOperationPetriGraph().getIsVisible()) {
				return FlexoLocalization.localizedForKey("close_operation_level");
			} else {
				return FlexoLocalization.localizedForKey("open_operation_level");
			}
		}
		return super.getLocalizedName();

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

	private CreatePetriGraph _createPetriGraph = null;

	public boolean visibility() {
		if (visibility == null) {
			visibility = !getFocusedObject().getOperationPetriGraph().getIsVisible();
		}
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
}
