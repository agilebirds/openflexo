package org.openflexo.view.controller.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.module.ProjectLoader;

public class RootControllerModel extends ControllerModelObject implements PropertyChangeListener {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RootControllerModel.class
			.getPackage().getName());

	public static final String PERSPECTIVES = "perspectives";
	public static final String EDITORS = "editors";
	public static final String OBJECTS = "objects";
	public static final String HISTORY = "history";
	public static final String CURRENT_PERPSECTIVE = "currentPerspective";
	public static final String CURRENT_EDITOR = "currentEditor";
	public static final String CURRENT_OBJECT = "currentObject";
	public static final String CURRENT_LOCATION = "currentLocation";

	private FlexoEditor currentEditor;

	private FlexoPerspective currentPerspective;

	private List<FlexoPerspective> perspectives;

	private List<FlexoEditor> editors;

	private List<FlexoModelObject> objects;

	protected Stack<HistoryLocation> previousHistory;
	protected Stack<HistoryLocation> nextHistory;
	protected HistoryLocation currentLocation;

	protected boolean isGoingForward = false;

	protected boolean isGoingBackward = false;

	private final Map<FlexoPerspective, FlexoModelObject> lastEditedObjectsForPerspective;

	private ApplicationContext context;

	public RootControllerModel(ApplicationContext context) {
		this.context = context;
		context.getProjectLoader().getPropertyChangeSupport().addPropertyChangeListener(ProjectLoader.EDITOR_ADDED, this);
		context.getProjectLoader().getPropertyChangeSupport().addPropertyChangeListener(ProjectLoader.EDITOR_REMOVED, this);
		lastEditedObjectsForPerspective = new Hashtable<FlexoPerspective, FlexoModelObject>();
		objects = new ArrayList<FlexoModelObject>();
		perspectives = new Vector<FlexoPerspective>();
		editors = new ArrayList<FlexoEditor>();
		previousHistory = new Stack<HistoryLocation>();
		nextHistory = new Stack<HistoryLocation>();
	}

	private ProjectLoader getProjectLoader() {
		return context.getProjectLoader();
	}

	@Override
	public void delete() {
		perspectives.clear();
		lastEditedObjectsForPerspective.clear();
		editors.clear();
		currentPerspective = null;
		currentEditor = null;
		currentLocation = null;
		super.delete();
	}

	/***************
	 * PERSPECTIVE *
	 ***************/

	/**
	 * Returns the current perspective.
	 * 
	 * @return the current perspective
	 */
	public FlexoPerspective getCurrentPerspective() {
		return currentPerspective;
	}

	public void setCurrentPerspective(FlexoPerspective currentPerspective) {
		if (this.currentPerspective != currentPerspective) {
			FlexoModelObject newEditedObject = getCurrentObject();

			if (currentPerspective == null || !currentPerspective.hasModuleViewForObject(getCurrentObject())) {
				newEditedObject = null;
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("switchToPerspective " + currentPerspective + " with object " + newEditedObject
						+ (newEditedObject != null ? " of " + newEditedObject.getClass().getSimpleName() : ""));
			}

			FlexoPerspective oldValue = this.currentPerspective;
			this.currentPerspective = currentPerspective;
			getPropertyChangeSupport().firePropertyChange(CURRENT_PERPSECTIVE, oldValue, currentPerspective);
			setCurrentObject(newEditedObject);
		}
	}

	public List<FlexoPerspective> getPerspectives() {
		return perspectives;
	}

	public void addToPerspectives(FlexoPerspective perspective) {
		perspectives.add(perspective);
		getPropertyChangeSupport().firePropertyChange(PERSPECTIVES, null, perspective);
	}

	public void removeFromPerspectives(FlexoPerspective perspective) {
		perspectives.remove(perspective);
		getPropertyChangeSupport().firePropertyChange(PERSPECTIVES, perspective, null);
	}

	/***********
	 * EDITORS *
	 ***********/

	public FlexoEditor getCurrentEditor() {
		return currentEditor;
	}

	public void setCurrentEditor(FlexoEditor projectEditor) {
		if (projectEditor == null && editors.size() > 0) {
			projectEditor = editors.get(editors.size() - 1);
		}
		FlexoEditor old = currentEditor;
		currentEditor = projectEditor;
		if (currentEditor != null) {
			editors.add(currentEditor);
			HistoryLocation hl = getLastHistoryLocationForProject(currentEditor.getProject());
			if (hl != null) {
				setCurrentObjectAndPerspective(hl.getObject(), hl.getPerspective());
			} else {
				setCurrentEditor(null);
			}
		}
		getPropertyChangeSupport().firePropertyChange(CURRENT_EDITOR, old, projectEditor);
	}

	public List<FlexoEditor> getEditors() {
		return editors;
	}

	private void setCurrentObjectAndPerspective(FlexoModelObject currentObject, FlexoPerspective perspective) {
		setCurrentPerspective(perspective);
		setCurrentObject(currentObject);
	}

	/**********************
	 * NAVIGATION HISTORY *
	 **********************/

	public FlexoModelObject getCurrentObject() {
		return currentLocation != null ? currentLocation.getObject() : null;
	}

	public void setCurrentObject(FlexoModelObject currentObject) {
		if (currentObject != null) {
			if (currentPerspective != null) {
				if (!isGoingForward && !isGoingBackward) {
					// _history.add(moduleView.getRepresentedObject());
					if (currentLocation == null || currentLocation.getObject() != currentObject) {
						if (currentLocation != null) {
							previousHistory.push(currentLocation);
						}
						nextHistory.clear();
						// Little block to change the currentPerspective if the
						// current perspective can handle this object
						if (!getCurrentPerspective().hasModuleViewForObject(currentObject)) {
							for (FlexoPerspective p : getPerspectives()) {
								if (p.hasModuleViewForObject(currentObject)) {
									setCurrentPerspective(p);
									break;
								}
							}
						}
						if (!objects.remove(currentObject)) {
							currentObject.getPropertyChangeSupport().addPropertyChangeListener(FlexoModelObject.DELETED_PROPERTY, this);
						}
						objects.add(currentObject);
						HistoryLocation old = currentLocation;
						currentLocation = new HistoryLocation(currentObject, getCurrentPerspective());
						getPropertyChangeSupport().firePropertyChange(CURRENT_LOCATION, old, currentLocation);
						getPropertyChangeSupport().firePropertyChange(CURRENT_EDITOR, old != null ? old.getObject() : null, currentObject);
					}
				}
			}
		} else {
			currentLocation = null;
		}
	}

	public boolean canGoForward() {
		return nextHistory.size() > 0;
	}

	public boolean canGoBackward() {
		return previousHistory.size() > 0;
	}

	public void goBackward() {
		if (canGoBackward()) {
			isGoingBackward = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Back to " + previousHistory.peek());
			}
			HistoryLocation historyLocation = previousHistory.peek();
			setCurrentObjectAndPerspective(historyLocation.getObject(), historyLocation.getPerspective());
			nextHistory.push(currentLocation);
			currentLocation = previousHistory.pop();
			isGoingBackward = false;
		}
	}

	public void goForward() {
		if (canGoForward()) {
			isGoingForward = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Forward to " + nextHistory.peek());
			}
			HistoryLocation historyLocation = nextHistory.peek();
			setCurrentObjectAndPerspective(historyLocation.getObject(), historyLocation.getPerspective());
			previousHistory.push(currentLocation);
			currentLocation = nextHistory.pop();
			isGoingForward = false;
		}
	}

	private HistoryLocation getLastHistoryLocationForProject(FlexoProject project) {
		for (int i = previousHistory.size(); i > 0; i--) {
			HistoryLocation location = previousHistory.get(i);
			if (location.getObject().getProject() == project) {
				return location;
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getProjectLoader()) {
			if (evt.getPropertyName().equals(ProjectLoader.EDITOR_ADDED)) {
				editors.add((FlexoEditor) evt.getNewValue());
			} else if (evt.getPropertyName().equals(ProjectLoader.EDITOR_REMOVED)) {
				editors.remove(getCurrentEditor());
				if (getCurrentEditor() == evt.getOldValue()) {
					setCurrentEditor(null);
				}
			}
		}
	}

}
