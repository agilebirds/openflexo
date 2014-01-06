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
package org.openflexo.technologyadapter.diagram.model.action;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.model.factory.EmbeddingType;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

public class DeleteDiagramElements extends FlexoUndoableAction<DeleteDiagramElements, DiagramElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(DeleteDiagramElements.class.getPackage().getName());

	public static FlexoActionType<DeleteDiagramElements, DiagramElement<?>, DiagramElement<?>> actionType = new FlexoActionType<DeleteDiagramElements, DiagramElement<?>, DiagramElement<?>>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteDiagramElements makeNewAction(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection,
				FlexoEditor editor) {
			return new DeleteDiagramElements(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection) {
			Vector<DiagramElement<?>> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return objectsToDelete.size() > 0;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(DeleteDiagramElements.actionType, DiagramShape.class);
		FlexoObjectImpl.addActionForClass(DeleteDiagramElements.actionType, DiagramConnector.class);
	}

	/**
	 * Given a selection, compute a list of ViewElements ensuring deletion of right selection<br>
	 * (extra selected embedded objects are removed from selection)
	 * 
	 * @param focusedObject
	 * @param globalSelection
	 * @return
	 */
	protected static Vector<DiagramElement<?>> objectsToDelete(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection) {
		Vector<DiagramElement<?>> allSelection = new Vector<DiagramElement<?>>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (DiagramElement<?> o : globalSelection) {
				if (!allSelection.contains(o)) {
					allSelection.add(o);
				}
			}
		}

		Vector<DiagramElement<?>> returned = new Vector<DiagramElement<?>>();
		for (DiagramElement<?> o : allSelection) {
			boolean isContainedByAnOtherObject = false;
			for (DiagramElement<?> potentielContainer : allSelection) {
				if (potentielContainer != o && o.isContainedIn(potentielContainer)) {
					isContainedByAnOtherObject = true;
					break;
				}
			}
			if (!isContainedByAnOtherObject && !returned.contains(o)) {
				returned.add(o);
			}
		}

		return returned;
	}

	private List<DiagramElement<?>> diagramElementsToDelete;
	private HashMap<EditionPatternInstance, DeletionScheme> selectedEditionPatternInstanceDeletionSchemes;
	private DeletionScheme selectedDeletionScheme;
	private EditionPatternInstance selectedEditionPatternInstance;

	protected DeleteDiagramElements(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		logger.info("Created DeleteShemaElements action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("DeleteDiagramElements");
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection is: " + getGlobalSelection());
			logger.fine("selection to delete is: " + getDiagramElementsToDelete());
			// logger.fine("all objects to delete are: " + getAllObjectsThatWillBeDeleted());
		}

		/*if (deleteOntologicObjects) {
			for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
				if (e.deleteIt && e.ontologicObject instanceof OntologyStatement) {
					e.ontologicObject.delete();
				}
			}
			for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
				if (e.deleteIt && !(e.ontologicObject instanceof OntologyStatement)) {
					e.ontologicObject.delete();
				}
			}
		}*/

		/*for (EditionPatternInstance epi : getEPIThatWillBeDeleted()) {
			if (selectedEditionPatternInstanceDeletionSchemes != null && selectedEditionPatternInstanceDeletionSchemes.get(epi) != null) {
				epi.delete(selectedEditionPatternInstanceDeletionSchemes.get(epi));
			} else {
				epi.delete();
			}
		}*/

		for (DiagramElement<?> o : getDiagramElementsToDelete()) {
			if (!o.isDeleted()) {
				logger.info("Delete undeleted DiagramElement " + o);
				o.delete();
			} else {
				logger.info("DiagramElement " + o + " has been successfully deleted");
			}
		}
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO DELETE not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO DELETE not implemented yet !");
	}

	public List<DiagramElement<?>> getDiagramElementsToDelete() {
		if (diagramElementsToDelete == null) {
			computeElementsToDelete();
		}
		return diagramElementsToDelete;
	}

	private void computeElementsToDelete() {
		diagramElementsToDelete = (List) getFocusedObject()
				.getDiagram()
				.getDiagramFactory()
				.getEmbeddedObjects(getFocusedObject(), EmbeddingType.DELETION,
						getGlobalSelection().toArray(new DiagramElement<?>[getGlobalSelection().size()]));

	}

	public DeletionScheme getSelectedDeletionScheme() {
		return selectedDeletionScheme;
	}

	public void setSelectedDeletionScheme(DeletionScheme selectedDeletionScheme) {
		if (getSelectedEditionPatternInstance() != null) {
			if (selectedEditionPatternInstanceDeletionSchemes == null) {
				selectedEditionPatternInstanceDeletionSchemes = new HashMap<EditionPatternInstance, DeletionScheme>();
			}
			selectedEditionPatternInstanceDeletionSchemes.put(getSelectedEditionPatternInstance(), selectedDeletionScheme);
			this.selectedDeletionScheme = selectedDeletionScheme;
			computeElementsToDelete();
		}
	}

	public EditionPatternInstance getSelectedEditionPatternInstance() {
		return selectedEditionPatternInstance;
	}

	public void setSelectedEditionPatternInstance(EditionPatternInstance selectedEditionPatternInstance) {
		this.selectedEditionPatternInstance = selectedEditionPatternInstance;
	}
}
