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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

public class DeleteDiagramElements extends FlexoUndoableAction<DeleteDiagramElements, DiagramElement, DiagramElement> {

	private static final Logger logger = Logger.getLogger(DeleteDiagramElements.class.getPackage().getName());

	public static FlexoActionType<DeleteDiagramElements, DiagramElement, DiagramElement> actionType = new FlexoActionType<DeleteDiagramElements, DiagramElement, DiagramElement>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteDiagramElements makeNewAction(DiagramElement focusedObject, Vector<DiagramElement> globalSelection, FlexoEditor editor) {
			return new DeleteDiagramElements(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramElement object, Vector<DiagramElement> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramElement focusedObject, Vector<DiagramElement> globalSelection) {
			Vector<DiagramElement> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return objectsToDelete.size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteDiagramElements.actionType, DiagramShape.class);
		FlexoModelObject.addActionForClass(DeleteDiagramElements.actionType, DiagramConnector.class);
	}

	/**
	 * Given a selection, compute a list of ViewElements ensuring deletion of right selection<br>
	 * (extra selected embedded objects are removed from selection)
	 * 
	 * @param focusedObject
	 * @param globalSelection
	 * @return
	 */
	protected static Vector<DiagramElement> objectsToDelete(DiagramElement focusedObject, Vector<DiagramElement> globalSelection) {
		Vector<DiagramElement> allSelection = new Vector<DiagramElement>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (DiagramElement o : globalSelection) {
				if (!allSelection.contains(o)) {
					allSelection.add(o);
				}
			}
		}

		Vector<DiagramElement> returned = new Vector<DiagramElement>();
		for (DiagramElement o : allSelection) {
			boolean isContainedByAnOtherObject = false;
			for (DiagramElement potentielContainer : allSelection) {
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

	private Vector<DiagramElement> viewElementsToDelete;
	private Vector<EditionPatternInstance> epiThatWillBeDeleted;
	private Vector<FlexoModelObject> allObjectsThatWillBeDeleted;
	private HashMap<EditionPatternInstance,DeletionScheme> selectedEditionPatternInstanceDeletionSchemes;
	private DeletionScheme selectedDeletionScheme;
	private EditionPatternInstance selectedEditionPatternInstance;

	protected DeleteDiagramElements(DiagramElement focusedObject, Vector<DiagramElement> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		logger.info("Created DeleteShemaElements action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("DeleteShemaElements");
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection is: " + getGlobalSelection());
			logger.fine("selection to delete is: " + getViewElementsToDelete());
			logger.fine("all objects to delete are: " + getAllObjectsThatWillBeDeleted());
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

		for (EditionPatternInstance epi : getEPIThatWillBeDeleted()) {
			if(selectedEditionPatternInstanceDeletionSchemes!=null && selectedEditionPatternInstanceDeletionSchemes.get(epi)!=null){
				epi.delete(selectedEditionPatternInstanceDeletionSchemes.get(epi));
			}
			else{
				epi.delete();	
			}
		}

		for (DiagramElement o : getViewElementsToDelete()) {
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

	public List<DiagramElement> getViewElementsToDelete() {
		if (viewElementsToDelete == null) {
			computeElementsToDelete();
		}
		return viewElementsToDelete;
	}

	private void computeElementsToDelete() {
		viewElementsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
		Vector<DiagramElement<?>> viewElementsThatWillBeDeleted = new Vector<DiagramElement<?>>();
		for (DiagramElement<?> o : viewElementsToDelete) {
			addAllEmbeddedObjects(o, viewElementsThatWillBeDeleted);
		}
		epiThatWillBeDeleted = new Vector<EditionPatternInstance>();
		for (DiagramElement<?> o : viewElementsThatWillBeDeleted) {
			if (o.getEditionPatternInstance() != null && !epiThatWillBeDeleted.contains(o.getEditionPatternInstance())) {
				epiThatWillBeDeleted.add(o.getEditionPatternInstance());
			}
		}

		// Sorts EPI in order to privilegiate deletion of connectors first
		Collections.sort(epiThatWillBeDeleted, new Comparator<EditionPatternInstance>() {
			@Override
			public int compare(EditionPatternInstance o1, EditionPatternInstance o2) {
				if (o1.getEditionPattern().getPrimaryRepresentationRole() instanceof ShapePatternRole
						&& o2.getEditionPattern().getPrimaryRepresentationRole() instanceof ConnectorPatternRole) {
					return 1;
				} else if (o1.getEditionPattern().getPrimaryRepresentationRole() instanceof ConnectorPatternRole
						&& o2.getEditionPattern().getPrimaryRepresentationRole() instanceof ShapePatternRole) {
					return -1;
				}
				return 0;
			}
		});
		allObjectsThatWillBeDeleted = new Vector<FlexoModelObject>();
		for (EditionPatternInstance epi : epiThatWillBeDeleted) {
			if(selectedEditionPatternInstanceDeletionSchemes!=null && selectedEditionPatternInstanceDeletionSchemes.get(epi)!=null){
				List<FlexoModelObject> l = epi.objectsThatWillBeDeleted(selectedEditionPatternInstanceDeletionSchemes.get(epi));
				if (l != null) {
					for (FlexoModelObject o : l) {
						if (!allObjectsThatWillBeDeleted.contains(o)) {
							allObjectsThatWillBeDeleted.add(o);
						}
					}
				}
			}
			else{
				List<FlexoModelObject> l = epi.objectsThatWillBeDeleted();
				if (l != null) {
					for (FlexoModelObject o : l) {
						if (!allObjectsThatWillBeDeleted.contains(o)) {
							allObjectsThatWillBeDeleted.add(o);
						}
					}
				}
			}
		}
	}

	private void addAllEmbeddedObjects(DiagramElement<?> o, Vector<DiagramElement<?>> v) {
		if (!v.contains(o) && o.playsPrimaryRole()) {
			v.add(o);
			if (o instanceof DiagramShape) {
				DiagramShape s = (DiagramShape) o;
				for (DiagramConnector connector : s.getIncomingConnectors()) {
					if (!v.contains(connector) && connector.playsPrimaryRole()) {
						v.add(connector);
					}
				}
				for (DiagramConnector connector : s.getOutgoingConnectors()) {
					if (!v.contains(connector) && connector.playsPrimaryRole()) {
						v.add(connector);
					}
				}
			}
			for (DiagramElement<?> child : o.getChilds()) {
				if (child instanceof DiagramElement) {
					addAllEmbeddedObjects(child, v);
				}
			}
		}
	}

	public List<EditionPatternInstance> getEPIThatWillBeDeleted() {
		if (epiThatWillBeDeleted == null) {
			computeElementsToDelete();
		}
		return epiThatWillBeDeleted;
	}

	public List<FlexoModelObject> getAllObjectsThatWillBeDeleted() {
		if (allObjectsThatWillBeDeleted == null) {
			computeElementsToDelete();
			/*for (DiagramElement e : getObjectsThatWillBeDeleted()) {
				if (e.getEditionPatternReferences() != null) {
					for (EditionPatternReference ref : e.getEditionPatternReferences()) {
						EditionPatternInstance epInstance = ref.getEditionPatternInstance();
						for (String s : epInstance.getActors().keySet()) {
							FlexoModelObject actor = epInstance.getActors().get(s);
							if (actor instanceof FlexoOntologyObjectImpl) {
								if (!_ontologicObjectsToBeDeleted.contains(actor)) {
									_ontologicObjectsToBeDeleted.add(new OntologicObjectEntry(true, (FlexoOntologyObjectImpl) actor));
								}
							}
						}
					}
				}
			}*/
		}
		return allObjectsThatWillBeDeleted;
	}

	public DeletionScheme getSelectedDeletionScheme() {
		return selectedDeletionScheme;
	}

	public void setSelectedDeletionScheme(DeletionScheme selectedDeletionScheme) {
		if(getSelectedEditionPatternInstance()!=null){
			if(selectedEditionPatternInstanceDeletionSchemes==null){
				selectedEditionPatternInstanceDeletionSchemes = new HashMap<EditionPatternInstance,DeletionScheme>();
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
