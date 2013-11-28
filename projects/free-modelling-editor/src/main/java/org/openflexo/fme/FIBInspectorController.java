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
package org.openflexo.fme;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;

public class FIBInspectorController implements Observer/*, ChangeListener*/{

	static final Logger logger = Logger.getLogger(FIBInspectorController.class.getPackage().getName());

	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FMEEditorLocalized"),
			new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

	// private JDialog inspectorDialog;
	private JPanel EMPTY_CONTENT;
	private JPanel MULTIPLE_CONTENT;
	private JPanel rootPane;

	private Hashtable<Class, FIBInspector> inspectors;
	private Hashtable<FIBInspector, FIBView> inspectorViews;

	private FreeModellingEditorApplication application;

	public FIBInspectorController(FreeModellingEditorApplication application) {
		inspectors = new Hashtable<Class, FIBInspector>();
		inspectorViews = new Hashtable<FIBInspector, FIBView>();

		File dir = new FileResource("FreeModellingEditorInspectors");

		for (File f : dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".inspector");
			}
		})) {
			logger.info("Loading " + f.getAbsolutePath());
			FIBInspector inspector = (FIBInspector) FIBLibrary.instance().retrieveFIBComponent(f);
			if (inspector != null) {
				if (inspector.getDataClass() != null) {
					inspectors.put(inspector.getDataClass(), inspector);
					logger.info("Loaded inspector: " + f.getName() + " for " + inspector.getDataClass());
				}
			} else {
				logger.warning("Not found: " + f.getAbsolutePath());
			}
		}

		for (FIBInspector inspector : inspectors.values()) {
			inspector.appendSuperInspectors(this);
		}

		for (FIBInspector inspector : inspectors.values()) {

			FIBView inspectorView = FIBController.makeView(inspector, LOCALIZATION);
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
			logger.info("Initialized inspector for " + inspector.getDataClass());
		}

		EMPTY_CONTENT = new JPanel(new BorderLayout());
		EMPTY_CONTENT.add(new JLabel("No selection", SwingConstants.CENTER), BorderLayout.CENTER);

		MULTIPLE_CONTENT = new JPanel(new BorderLayout());
		MULTIPLE_CONTENT.add(new JLabel("Multiple selection", SwingConstants.CENTER), BorderLayout.CENTER);

		rootPane = new JPanel(new BorderLayout());

		switchToEmptyContent();
		rootPane.revalidate();
	}

	public void setDiagramEditor(DiagramEditor diagramEditor) {
		for (FIBView inspectorView : inspectorViews.values()) {
			((FMEFIBController) inspectorView.getController()).setDiagramEditor(diagramEditor);
		}
	}

	private FIBInspector currentInspector = null;
	private FIBView currentInspectorView = null;

	private Object currentInspectedObject = null;

	public void inspectObject(Object object) {
		if (object == currentInspectedObject) {
			return;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorForObject(object);

		if (newInspector == null) {
			logger.warning("No inspector for " + object);
			switchToEmptyContent();
		} else {
			if (newInspector != currentInspector) {
				switchToInspector(newInspector);
			}
			currentInspectorView.getController().setDataObject(object);
		}
	}

	public void switchToEmptyContent() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(EMPTY_CONTENT, BorderLayout.CENTER);
		rootPane.revalidate();
		rootPane.repaint();
	}

	public void switchToMultipleContent() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(MULTIPLE_CONTENT, BorderLayout.CENTER);
		rootPane.revalidate();
		rootPane.repaint();
	}

	private void switchToInspector(FIBInspector newInspector) {
		FIBView view = inspectorViews.get(newInspector);
		if (view != null) {
			currentInspectorView = view;
			rootPane.removeAll();
			rootPane.add(currentInspectorView.getResultingJComponent(), BorderLayout.CENTER);
			rootPane.revalidate();
			rootPane.repaint();
			currentInspector = newInspector;
		} else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
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

	protected Hashtable<Class, FIBInspector> getInspectors() {
		return inspectors;
	}

	@Override
	public void update(Observable o, Object notification) {
		/*if (notification instanceof FIBEditorNotification) {
			if (notification instanceof SelectedObjectChange) {
				SelectedObjectChange selectionChange = (SelectedObjectChange) notification;
				if (selectionChange.newValue() != null) {
					inspectObject(selectionChange.newValue());
				}
			}
		}*/
	}

	public JPanel getRootPane() {
		return rootPane;
	}

	public FreeModellingEditorApplication getApplication() {
		return application;
	}
}
