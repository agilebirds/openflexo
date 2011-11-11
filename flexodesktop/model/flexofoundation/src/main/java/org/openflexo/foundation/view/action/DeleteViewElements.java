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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;

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
		protected boolean isVisibleForSelection(ViewElement object, Vector<ViewElement> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewElement focusedObject, Vector<ViewElement> globalSelection) {
			Vector<ViewElement> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return (objectsToDelete.size() > 0);
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteViewElements.actionType, ViewShape.class);
		FlexoModelObject.addActionForClass(DeleteViewElements.actionType, ViewConnector.class);
	}

	protected static Vector<ViewElement> objectsToDelete(ViewElement focusedObject, Vector<ViewElement> globalSelection) {
		Vector<ViewElement> allSelection = new Vector<ViewElement>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (ViewElement o : globalSelection) {
				allSelection.add(o);
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
			if (!isContainedByAnOtherObject)
				returned.add(o);
		}

		return returned;
	}

	private Vector<OntologicObjectEntry> _ontologicObjectsToBeDeleted;
	private Vector<ViewElement> _objectsToDelete;
	private Vector<ViewElement> _objectsThatWillBeDeleted;
	public boolean deleteOntologicObjects = true;

	protected DeleteViewElements(ViewElement focusedObject, Vector<ViewElement> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		logger.info("Created DeleteShemaElements action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO))
			logger.info("DeleteShemaElements");
		if (logger.isLoggable(Level.INFO))
			logger.info("selection is: " + getGlobalSelection());
		if (logger.isLoggable(Level.INFO))
			logger.info("selection to delete is: " + getObjectsToDelete());
		if (logger.isLoggable(Level.INFO) && deleteOntologicObjects)
			logger.info("ontologic objects to delete are: " + getObjectsToDelete());

		if (deleteOntologicObjects) {
			for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
				if (e.deleteIt && e.ontologicObject instanceof OntologyStatement)
					e.ontologicObject.delete();
			}
			for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
				if (e.deleteIt && !(e.ontologicObject instanceof OntologyStatement))
					e.ontologicObject.delete();
			}
		}

		for (ViewElement o : getObjectsToDelete()) {
			o.delete();
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

	public Vector<ViewElement> getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
			_objectsThatWillBeDeleted = new Vector<ViewElement>();
			for (ViewElement o : _objectsToDelete) {
				addAllEmbeddedObjects(o, _objectsThatWillBeDeleted);
			}
		}
		return _objectsToDelete;
	}

	public Vector<ViewElement> getObjectsThatWillBeDeleted() {
		if (_objectsThatWillBeDeleted == null) {
			_objectsThatWillBeDeleted = new Vector<ViewElement>();
			for (ViewElement o : getObjectsToDelete()) {
				addAllEmbeddedObjects(o, _objectsThatWillBeDeleted);
			}
		}
		return _objectsThatWillBeDeleted;
	}

	private void addAllEmbeddedObjects(ViewElement o, Vector<ViewElement> v) {
		if (!v.contains(o))
			v.add(o);
		if (o instanceof ViewShape) {
			ViewShape s = (ViewShape) o;
			for (ViewConnector connector : s.getIncomingConnectors())
				if (!v.contains(connector))
					v.add(connector);
			for (ViewConnector connector : s.getOutgoingConnectors())
				if (!v.contains(connector))
					v.add(connector);
		}
		for (ViewObject child : o.getChilds()) {
			if (child instanceof ViewElement)
				addAllEmbeddedObjects((ViewElement) child, v);
		}
	}

	public class OntologicObjectEntry {

		public OntologicObjectEntry(boolean deleteIt, AbstractOntologyObject ontologicObject) {
			super();
			this.deleteIt = deleteIt;
			this.ontologicObject = ontologicObject;
		}

		public boolean deleteIt;
		public AbstractOntologyObject ontologicObject;
	}

	public Vector<OntologicObjectEntry> getOntologicObjectsToBeDeleted() {
		if (_ontologicObjectsToBeDeleted == null) {
			_ontologicObjectsToBeDeleted = new Vector<OntologicObjectEntry>();
			for (ViewElement e : getObjectsThatWillBeDeleted()) {
				if (e.getEditionPatternReferences() != null) {
					for (EditionPatternReference ref : e.getEditionPatternReferences()) {
						EditionPatternInstance epInstance = ref.getEditionPatternInstance();
						for (String s : epInstance.getActors().keySet()) {
							FlexoModelObject actor = epInstance.getActors().get(s);
							if (actor instanceof AbstractOntologyObject) {
								if (!_ontologicObjectsToBeDeleted.contains(actor))
									_ontologicObjectsToBeDeleted.add(new OntologicObjectEntry(true, (AbstractOntologyObject) actor));
							}
						}
					}
				}
			}
		}
		return _ontologicObjectsToBeDeleted;
	}

	public void selectAll() {
		for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
			e.deleteIt = true;
		}
	}

	public void selectNone() {
		for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
			e.deleteIt = false;
		}
	}

	public boolean getDeleteOntologicObjects() {
		return deleteOntologicObjects;
	}

	public void setDeleteOntologicObjects(boolean deleteOntologicObjects) {
		if (deleteOntologicObjects)
			selectAll();
		else
			selectNone();
		this.deleteOntologicObjects = deleteOntologicObjects;
	}
}
