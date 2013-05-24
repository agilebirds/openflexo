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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public class DeleteViewElements extends FlexoUndoableAction<DeleteViewElements, ViewElement, ViewElement> {

	private static final Logger logger = Logger.getLogger(DeleteViewElements.class.getPackage().getName());

	public static FlexoActionType<DeleteViewElements, ViewElement, ViewElement> actionType = new FlexoActionType<DeleteViewElements, ViewElement, ViewElement>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteViewElements makeNewAction(ViewElement focusedObject, Vector<ViewElement> globalSelection, FlexoEditor editor) {
			return new DeleteViewElements(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewElement object, Vector<ViewElement> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewElement focusedObject, Vector<ViewElement> globalSelection) {
			Vector<ViewElement> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return objectsToDelete.size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteViewElements.actionType, ViewShape.class);
		FlexoModelObject.addActionForClass(DeleteViewElements.actionType, ViewConnector.class);
	}

	/**
	 * Given a selection, compute a list of ViewElements ensuring deletion of right selection<br>
	 * (extra selected embedded objects are removed from selection)
	 * 
	 * @param focusedObject
	 * @param globalSelection
	 * @return
	 */
	protected static Vector<ViewElement> objectsToDelete(ViewElement focusedObject, Vector<ViewElement> globalSelection) {
		Vector<ViewElement> allSelection = new Vector<ViewElement>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (ViewElement o : globalSelection) {
				if (!allSelection.contains(o)) {
					allSelection.add(o);
				}
			}
		}

		Vector<ViewElement> returned = new Vector<ViewElement>();
		for (ViewElement o : allSelection) {
			boolean isContainedByAnOtherObject = false;
			for (ViewElement potentielContainer : allSelection) {
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

	private Vector<ViewElement> viewElementsToDelete;
	private Vector<EditionPatternInstance> epiThatWillBeDeleted;
	private Vector<FlexoModelObject> allObjectsThatWillBeDeleted;

	protected DeleteViewElements(ViewElement focusedObject, Vector<ViewElement> globalSelection, FlexoEditor editor) {
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
			epi.delete();
		}

		for (ViewElement o : getViewElementsToDelete()) {
			if (!o.isDeleted()) {
				logger.info("Delete undeleted ViewElement " + o);
				o.delete();
			} else {
				logger.info("ViewElement " + o + " has been successfully deleted");
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

	public List<ViewElement> getViewElementsToDelete() {
		if (viewElementsToDelete == null) {
			computeElementsToDelete();
		}
		return viewElementsToDelete;
	}

	private void computeElementsToDelete() {
		viewElementsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
		Vector<ViewElement> viewElementsThatWillBeDeleted = new Vector<ViewElement>();
		for (ViewElement o : viewElementsToDelete) {
			addAllEmbeddedObjects(o, viewElementsThatWillBeDeleted);
		}
		epiThatWillBeDeleted = new Vector<EditionPatternInstance>();
		for (ViewElement o : viewElementsThatWillBeDeleted) {
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

	private void addAllEmbeddedObjects(ViewElement o, Vector<ViewElement> v) {
		if (!v.contains(o) && o.playsPrimaryRole()) {
			v.add(o);
			if (o instanceof ViewShape) {
				ViewShape s = (ViewShape) o;
				for (ViewConnector connector : s.getIncomingConnectors()) {
					if (!v.contains(connector) && connector.playsPrimaryRole()) {
						v.add(connector);
					}
				}
				for (ViewConnector connector : s.getOutgoingConnectors()) {
					if (!v.contains(connector) && connector.playsPrimaryRole()) {
						v.add(connector);
					}
				}
			}
			for (ViewObject child : o.getChilds()) {
				if (child instanceof ViewElement) {
					addAllEmbeddedObjects((ViewElement) child, v);
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
			/*for (ViewElement e : getObjectsThatWillBeDeleted()) {
				if (e.getEditionPatternReferences() != null) {
					for (EditionPatternReference ref : e.getEditionPatternReferences()) {
						EditionPatternInstance epInstance = ref.getEditionPatternInstance();
						for (String s : epInstance.getActors().keySet()) {
							FlexoModelObject actor = epInstance.getActors().get(s);
							if (actor instanceof AbstractOntologyObject) {
								if (!_ontologicObjectsToBeDeleted.contains(actor)) {
									_ontologicObjectsToBeDeleted.add(new OntologicObjectEntry(true, (AbstractOntologyObject) actor));
								}
							}
						}
					}
				}
			}*/
		}
		return allObjectsThatWillBeDeleted;
	}

}
