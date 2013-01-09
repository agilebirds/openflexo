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
package org.openflexo.inspector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BooleanStaticBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;

/**
 * Represents the controller for all inspectors managed in the context of a module<br>
 * It is connected with one or many FIBInspectorPanels sharing the same selection. In particular, manage the inspector dialog of the module.
 * 
 * @author sylvain
 * 
 */
public class ModuleInspectorController extends Observable implements Observer {

	static final Logger logger = Logger.getLogger(ModuleInspectorController.class.getPackage().getName());

	private final FIBInspectorDialog inspectorDialog;

	private final FlexoController flexoController;

	private final Map<Class<?>, FIBInspector> inspectors;

	private FIBInspector currentInspector = null;
	private Object currentInspectedObject = null;

	public ModuleInspectorController(FlexoController flexoController) {
		this.flexoController = flexoController;
		inspectors = new Hashtable<Class<?>, FIBInspector>();
		inspectorDialog = new FIBInspectorDialog(this);
		File inspectorsDir = new FileResource("Inspectors/COMMON");
		loadDirectory(inspectorsDir);
	}

	public FlexoController getFlexoController() {
		return flexoController;
	}

	public void loadDirectory(File dir) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Loading directory: " + dir);
		}
		if (!dir.exists()) {
			logger.warning("Directory does NOT exist: " + dir.getAbsolutePath());
			// (new Exception("???")).printStackTrace();
			// System.exit(-1);
			return;
		}
		for (File f : dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// logger.info("name="+name);
				return name.endsWith(".inspector");
			}
		})) {
			FIBInspector inspector = (FIBInspector) FIBLibrary.instance().retrieveFIBComponent(f);
			if (inspector != null) {
				appendVisibleFor(inspector);
				if (inspector.getDataClass() != null) {
					// try {
					inspectors.put(inspector.getDataClass(), inspector);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Loaded inspector: " + f.getName() + " for " + inspector.getDataClass());
					}
					/*
					 * } catch (ClassNotFoundException e) {
					 * logger.warning("Not found: " +
					 * inspector.getDataClassName()); }
					 */
				}
			} else {
				logger.warning("Not found: " + f.getAbsolutePath());
			}
		}

		for (FIBInspector inspector : inspectors.values()) {
			inspector.appendSuperInspectors(this);
		}

		for (FIBInspector inspector : inspectors.values()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Initialized inspector for " + inspector.getDataClass());
			}
		}

		setChanged();
		notifyObservers(new NewInspectorsLoaded());
	}

	private void appendVisibleFor(FIBComponent component) {
		String visibleForParam = component.getParameter("visibleFor");
		if (visibleForParam != null) {
			String[] s = visibleForParam.split("[;,\"]");
			if (s.length > 0) {
				UserType userType = UserType.getCurrentUserType();
				boolean ok = false;
				for (String string : s) {
					ok |= userType.getName().equalsIgnoreCase(string);
					ok |= userType.getIdentifier().equalsIgnoreCase(string);
					if (ok) {
						break;
					}
				}
				if (!ok) {
					component.getVisible().setBinding(new BooleanStaticBinding(false));
				}
			}
		}
		if (component instanceof FIBContainer) {
			for (FIBComponent child : ((FIBContainer) component).getSubComponents()) {
				appendVisibleFor(child);
			}
		}
	}

	protected FIBInspector inspectorForObject(Object object) {
		if (object == null) {
			return null;
		}
		return inspectorForClass(object.getClass());
	}

	protected FIBInspector inspectorForClass(Class<?> aClass) {
		if (aClass == null) {
			return null;
		}
		FIBInspector returned = inspectors.get(aClass);
		if (returned != null) {
			return returned;
		} else {
			Class<?> superclass = aClass.getSuperclass();
			if (superclass != null) {
				returned = inspectors.get(aClass);
				if (returned != null) {
					return returned;
				} else {
					for (Class<?> superInterface : aClass.getInterfaces()) {
						returned = inspectors.get(superInterface);
						if (returned != null) {
							return returned;
						}
					}
					return inspectorForClass(superclass);
				}
			}
		}
		List<Class<?>> matchingClasses = new ArrayList<Class<?>>();
		for (Class<?> cl : inspectors.keySet()) {
			if (cl.isAssignableFrom(aClass)) {
				matchingClasses.add(cl);
			}
		}
		if (matchingClasses.size() > 0) {
			return inspectors.get(TypeUtils.getMostSpecializedClass(matchingClasses));
		}
		return null;
	}

	protected Map<Class<?>, FIBInspector> getInspectors() {
		return inspectors;
	}

	public FIBInspectorDialog getInspectorDialog() {
		return inspectorDialog;
	}

	protected void switchToEmptyContent() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		setChanged();
		notifyObservers(new EmptySelectionActivated());
	}

	private void switchToMultipleSelection() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		setChanged();
		notifyObservers(new MultipleSelectionActivated());
	}

	private void switchToInspector(FIBInspector newInspector, boolean updateEPTabs) {
		currentInspector = newInspector;
		setChanged();
		notifyObservers(new InspectorSwitching(newInspector, updateEPTabs));
	}

	private void displayObject(Object object) {
		setChanged();
		notifyObservers(new InspectedObjectChanged(object));
	}

	/**
	 * Returns boolean indicating if inspection change
	 * 
	 * @param object
	 * @return
	 */
	public boolean inspectObject(Object object) {
		if (object == currentInspectedObject) {
			return false;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorForObject(object);

		if (newInspector == null) {
			logger.warning("No inspector for " + object);
			switchToEmptyContent();
		} else {
			boolean updateEPTabs = false;
			if (object instanceof FlexoModelObject) {
				updateEPTabs = newInspector.updateEditionPatternReferences((FlexoModelObject) object);
			}
			if (newInspector != currentInspector || updateEPTabs) {
				switchToInspector(newInspector, updateEPTabs);
			}
			displayObject(object);
		}

		return true;
	}

	public void resetInspector() {
		switchToEmptyContent();
	}

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof InspectorSelection) {
			InspectorSelection inspectorSelection = (InspectorSelection) notification;
			if (inspectorSelection instanceof EmptySelection) {
				switchToEmptyContent();
			} else if (inspectorSelection instanceof MultipleSelection) {
				switchToMultipleSelection();
			} else if (inspectorSelection instanceof UniqueSelection) {
				inspectObject(((UniqueSelection) inspectorSelection).getInspectedObject());
			}
		}

		// Reforward notification to all in inspector panels
		setChanged();
		notifyObservers(notification);
	}

	public static class NewInspectorsLoaded {

	}

	public static class EmptySelectionActivated {

	}

	public static class MultipleSelectionActivated {

	}

	public static class InspectorSwitching {
		private boolean updateEPTabs;
		private FIBInspector newInspector;

		public InspectorSwitching(FIBInspector newInspector, boolean updateEPTabs) {
			this.newInspector = newInspector;
			this.updateEPTabs = updateEPTabs;
		}

		public boolean updateEPTabs() {
			return updateEPTabs;
		}

		public FIBInspector getNewInspector() {
			return newInspector;
		}
	}

	public static class InspectedObjectChanged {
		private Object inspectedObject;

		public InspectedObjectChanged(Object inspectedObject) {
			this.inspectedObject = inspectedObject;
		}

		public Object getInspectedObject() {
			return inspectedObject;
		}
	}

	public void delete() {
		inspectorDialog.delete();
		currentInspectedObject = null;
		currentInspector = null;
	}

}
