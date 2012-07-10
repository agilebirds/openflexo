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

import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.layout.MultiSplitLayoutTypeAdapterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RootControllerModel extends ControllerModelObject implements PropertyChangeListener {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(RootControllerModel.class
			.getPackage().getName());

	public static final String RIGHT_VIEW_VISIBLE = "rightViewVisible";

	public static final String LEFT_VIEW_VISIBLE = "leftViewVisible";

	public static final String PERSPECTIVES = "perspectives";
	public static final String EDITORS = "editors";
	public static final String OBJECTS = "objects";
	public static final String HISTORY = "history";
	public static final String CURRENT_PERPSECTIVE = "currentPerspective";
	public static final String CURRENT_EDITOR = "currentEditor";
	public static final String CURRENT_OBJECT = "currentObject";
	public static final String CURRENT_LOCATION = "currentLocation";

	private Gson gsonLayout;

	private boolean leftViewVisible = true;

	private boolean rightViewVisible = true;

	private FlexoEditor currentEditor;

	private FlexoPerspective currentPerspective;

	private List<FlexoPerspective> perspectives;

	private List<FlexoEditor> editors;

	private List<FlexoModelObject> objects;

	private Stack<HistoryLocation> previousHistory;
	private Stack<HistoryLocation> nextHistory;
	private HistoryLocation currentLocation;

	private boolean isGoingForward = false;

	private boolean isGoingBackward = false;

	private Map<FlexoPerspective, FlexoModelObject> lastEditedObjectsForPerspective;

	private ApplicationContext context;

	private FlexoModule module;

	public RootControllerModel(ApplicationContext context, FlexoModule module) {
		this.context = context;
		this.module = module;
		leftViewVisible = GeneralPreferences.getShowLeftView(module.getShortName());
		rightViewVisible = GeneralPreferences.getShowRightView(module.getShortName());
		context.getProjectLoader().getPropertyChangeSupport().addPropertyChangeListener(ProjectLoader.EDITOR_ADDED, this);
		context.getProjectLoader().getPropertyChangeSupport().addPropertyChangeListener(ProjectLoader.EDITOR_REMOVED, this);
		lastEditedObjectsForPerspective = new Hashtable<FlexoPerspective, FlexoModelObject>();
		objects = new ArrayList<FlexoModelObject>();
		perspectives = new Vector<FlexoPerspective>();
		editors = new ArrayList<FlexoEditor>();
		previousHistory = new Stack<HistoryLocation>();
		nextHistory = new Stack<HistoryLocation>();
	}

	public ModuleLoader getModuleLoader() {
		return context.getModuleLoader();
	}

	private ProjectLoader getProjectLoader() {
		return context.getProjectLoader();
	}

	public FlexoModule getModule() {
		return module;
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

			if (currentPerspective == null || getCurrentObject() != null && !currentPerspective.hasModuleViewForObject(getCurrentObject())) {
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
		if (currentPerspective == null) {
			setCurrentPerspective(perspective);
		}
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
				setCurrentObject(null);
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
						getPropertyChangeSupport().firePropertyChange(CURRENT_OBJECT, old != null ? old.getObject() : null, currentObject);
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

	public boolean canGoUp() {
		return getCurrentObject() != null && getParent(getCurrentObject()) != null;
	}

	private FlexoModelObject getParent(FlexoModelObject object) {
		if (object instanceof DMObject) {
			return ((DMObject) object).getParent();
		} else if (object instanceof FlexoProcess) {
			return ((FlexoProcess) object).getParentProcess();
		} else if (object instanceof CGFile) {
			return ((CGFile) object).getRepository();
		} else if (object instanceof TOCEntry) {
			if (((TOCEntry) object).getParent() != null) {
				return ((TOCEntry) object).getParent();
			} else {
				((TOCEntry) object).getRepository();
			}
		} else if (object instanceof WSObject) {
			return ((WSObject) object).getParent();
		} else if (object instanceof ServiceInterface) {
			WSPortType proc = ((ServiceInterface) object).getProject().getFlexoWSLibrary()
					.getWSPortTypeNamed(((ServiceInterface) object).getName());
			if (proc != null) {
				return proc.getWSService().getWSPortTypeFolder();
			}
		} else if (object instanceof ServiceOperation) {
			return ((ServiceOperation) object).getServiceInterface();
		} else if (object instanceof ServiceMessageDefinition) {
			return ((ServiceMessageDefinition) object).getOperation();
		} else if (object instanceof MessageEntry) {
			return ((MessageEntry) object).getMessage();
		}
		return null;
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

	public void goUp() {
		if (canGoUp()) {
			setCurrentObject(getParent(getCurrentObject()));
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

	/**********
	 * LAYOUT *
	 **********/

	public Node getLayoutForPerspective(FlexoPerspective perspective) {
		String layout = GeneralPreferences.getLayoutFor(getModule().getShortName() + perspective.getName());
		if (layout != null) {
			return getLayoutFromString(layout);
		} else {
			return null;
		}
	}

	private Node getLayoutFromString(String layout) {
		return getGsonLayout().fromJson(layout, Node.class);
	}

	public void setLayoutForPerspective(FlexoPerspective perspective, Node layout) {
		GeneralPreferences.setLayoutFor(getGsonLayout().toJson(layout), getModule().getShortName() + perspective.getName());
		GeneralPreferences.save();
	}

	private Gson getGsonLayout() {
		if (gsonLayout == null) {
			GsonBuilder builder = new GsonBuilder().registerTypeAdapterFactory(new MultiSplitLayoutTypeAdapterFactory());
			gsonLayout = builder.create();

		}
		return gsonLayout;
	}

	/**************
	 * VISIBILITY *
	 **************/

	public boolean isLeftViewVisible() {
		return leftViewVisible;
	}

	public void setLeftViewVisible(boolean leftViewVisible) {
		this.leftViewVisible = leftViewVisible;
		if (GeneralPreferences.getShowLeftView(getModule().getShortName())) {
			GeneralPreferences.setShowLeftView(getModule().getShortName(), true);
			FlexoPreferences.savePreferences(true);
		}
		getPropertyChangeSupport().firePropertyChange(LEFT_VIEW_VISIBLE, !leftViewVisible, leftViewVisible);
	}

	public boolean isRightViewVisible() {
		return rightViewVisible;
	}

	public void setRightViewVisible(boolean rightViewVisible) {
		this.rightViewVisible = rightViewVisible;
		if (GeneralPreferences.getShowRightView(getModule().getShortName())) {
			GeneralPreferences.setShowRightView(getModule().getShortName(), true);
			FlexoPreferences.savePreferences(true);
		}
		getPropertyChangeSupport().firePropertyChange(RIGHT_VIEW_VISIBLE, !rightViewVisible, rightViewVisible);
	}
}
