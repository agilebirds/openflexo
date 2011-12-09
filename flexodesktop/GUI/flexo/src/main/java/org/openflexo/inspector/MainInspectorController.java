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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.WindowSynchronizer;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class MainInspectorController implements Observer, ChangeListener {

	static final Logger logger = Logger.getLogger(MainInspectorController.class
			.getPackage().getName());

	private final JDialog inspectorDialog;
	private final JPanel EMPTY_CONTENT;
	private final JPanel MULTIPLE_SELECTION_CONTENT;
	private final JPanel rootPane;

	private final InteractiveFlexoEditor editor;

	private final Map<Class<?>, FIBInspector> inspectors;
	private final Map<FIBInspector, FIBView> inspectorViews;

	private final FlexoController flexoController;

	private static final WindowSynchronizer inspectorSync = new WindowSynchronizer();

	public MainInspectorController(FlexoController flexoController) {
		this.flexoController = flexoController;
		this.editor = flexoController.getEditor();

		inspectors = new Hashtable<Class<?>, FIBInspector>();
		inspectorViews = new Hashtable<FIBInspector, FIBView>();

		File inspectorsDir = new FileResource("Inspectors/COMMON");
		loadDirectory(inspectorsDir);

		inspectorDialog = new JDialog(flexoController.getFlexoFrame(),
				"Inspector", false);
		inspectorSync.addToSynchronizedWindows(inspectorDialog);
		inspectorDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		EMPTY_CONTENT = new JPanel(new BorderLayout());
		EMPTY_CONTENT.setPreferredSize(new Dimension(400, 400));
		EMPTY_CONTENT.add(new JLabel("No selection", SwingConstants.CENTER),
				BorderLayout.CENTER);
		MULTIPLE_SELECTION_CONTENT = new JPanel(new BorderLayout());
		MULTIPLE_SELECTION_CONTENT.setPreferredSize(new Dimension(400, 400));
		MULTIPLE_SELECTION_CONTENT.add(new JLabel("Multiple selection",
				SwingConstants.CENTER), BorderLayout.CENTER);

		rootPane = new JPanel(new BorderLayout());
		inspectorDialog.getContentPane().setLayout(new BorderLayout());
		inspectorDialog.getContentPane().add(rootPane, BorderLayout.CENTER);

		switchToEmptyContent();

		inspectorDialog.setResizable(true);
		inspectorDialog.setLocation(1210, 360);
		inspectorDialog.pack();
		inspectorDialog.setVisible(true);
	}

	public void loadDirectory(File dir) {
		logger.info("Directory: " + dir);
		logger.info("Exists: " + dir.exists());
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
			// System.out.println("Read "+f.getAbsolutePath());
			FIBInspector inspector = (FIBInspector) FIBLibrary.instance()
					.retrieveFIBComponent(f);
			if (inspector != null) {
				if (inspector.getDataClass() != null) {
					// try {
					inspectors.put(inspector.getDataClass(), inspector);
					logger.info("Loaded inspector: " + f.getName() + " for "
							+ inspector.getDataClass());
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

		for (Class c : inspectors.keySet()) {
			FIBInspector inspector = inspectors.get(c);
			inspector.appendSuperInspectors(this);
		}

		for (Class c : inspectors.keySet()) {
			FIBInspector inspector = inspectors.get(c);
			inspector.recursivelyReorderComponents();

			/*
			 * if (inspector.getDataClass().equals(ProjectOntology.class)) {
			 * FIBTabPanel tabPanel = (FIBTabPanel)inspector.getChildAt(0);
			 * FIBTab basicTab = (FIBTab)tabPanel.getComponentNamed("BasicTab");
			 * for (FIBComponent cpt : basicTab.getSubComponents()) {
			 * System.out.
			 * println("Component "+cpt+" constraints="+cpt.getConstraints()); }
			 * basicTab.reorderComponents(); System.out.println("Ensuite"); for
			 * (FIBComponent cpt : basicTab.getSubComponents()) {
			 * System.out.println
			 * ("Component "+cpt+" constraints="+cpt.getConstraints()); }
			 * System.exit(-1); }
			 */

			FIBView inspectorView = FIBController.makeView(inspector);
			((FIBInspectorController) inspectorView.getController())
					.setEditor(editor);
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
			logger.info("Initialized inspector for " + inspector.getDataClass());
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
			if (object instanceof FlexoModelObject) {
				updateEditionPatternReferences(newInspector,
						(FlexoModelObject) object);
			}
			if (object instanceof FlexoModelObject
					&& (object instanceof ViewShape || object instanceof ViewConnector)
					&& ((FlexoModelObject) object)
							.getEditionPatternReferences().size() > 0) {
				String newTitle = ((FlexoModelObject) object)
						.getEditionPatternReferences().firstElement()
						.getEditionPattern().getInspector().getInspectorTitle();
				inspectorDialog.setTitle(newTitle);
			} else {
				inspectorDialog.setTitle(newInspector.getParameter("title"));
			}
			currentInspectorView.getController().setDataObject(object);
		}
	}

	private void updateEditionPatternReferences(FIBInspector inspector,
			FlexoModelObject object) {
		if (inspector.updateEditionPatternReferences(object)) {
			FIBView view = viewForInspector(inspector);
			FIBController controller = view.getController();
			FIBTabPanelView tabPanelView = (FIBTabPanelView) controller
					.viewForComponent(inspector.getTabPanel());
			tabPanelView.updateLayout();
		} else {
			// Nothing change: nice !!!
		}
	}

	private void switchToEmptyContent() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(EMPTY_CONTENT, BorderLayout.CENTER);
		rootPane.validate();
		rootPane.repaint();
	}

	private void switchToMultipleSelection() {
		// System.out.println("switchToEmptyContent()");
		currentInspectedObject = null;
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(MULTIPLE_SELECTION_CONTENT, BorderLayout.CENTER);
		rootPane.validate();
		rootPane.repaint();
	}

	private void switchToInspector(FIBInspector newInspector) {
		/*
		 * if (newInspector.getDataClass() == FIBPanel.class) {
		 * System.out.println("Hop: "+newInspector.getXMLRepresentation()); }
		 */

		if (tabPanelView != null) {
			tabPanelView.getJComponent().removeChangeListener(this);
			// System.out.println("removeChangeListener for "+tabPanelView.getJComponent());
		}

		// System.out.println("switchToInspector() "+newInspector);
		FIBView view = viewForInspector(newInspector);
		if (view != null) {
			currentInspectorView = view;
			rootPane.removeAll();
			rootPane.add(currentInspectorView.getResultingJComponent(),
					BorderLayout.CENTER);
			rootPane.validate();
			rootPane.repaint();
			currentInspector = newInspector;
			// logger.info("reset title to "+newInspector.getParameter("title"));dsqqsd
			// inspectorDialog.setTitle(newInspector.getParameter("title"));
			tabPanelView = (FIBTabPanelView) currentInspectorView
					.getController().viewForComponent(
							currentInspector.getTabPanel());
			if (lastInspectedTabIndex >= 0
					&& lastInspectedTabIndex < tabPanelView.getJComponent()
							.getTabCount()) {
				tabPanelView.getJComponent().setSelectedIndex(
						lastInspectedTabIndex);
			}
			tabPanelView.getJComponent().addChangeListener(this);
			// System.out.println("addChangeListener for "+tabPanelView.getJComponent());
		} else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
		}
	}

	private FIBView viewForInspector(FIBInspector inspector) {
		return inspectorViews.get(inspector);
	}

	protected FIBInspector inspectorForObject(Object object) {
		if (object == null) {
			return null;
		}
		return inspectorForClass(object.getClass());
	}

	protected FIBInspector inspectorForClass(Class aClass) {
		Class c = aClass;
		while (c != null) {
			FIBInspector returned = inspectors.get(c);
			if (returned != null) {
				return returned;
			} else {
				c = c.getSuperclass();
			}
		}
		return null;
	}

	protected Map<Class<?>, FIBInspector> getInspectors() {
		return inspectors;
	}

	@Override
	public void update(Observable o, Object selection) {
		// logger.info("FIBInspectorController received: "+selection);
		if (selection instanceof InspectorSelection) {
			InspectorSelection inspectorSelection = (InspectorSelection) selection;
			if (inspectorSelection instanceof EmptySelection) {
				switchToEmptyContent();
			} else if (inspectorSelection instanceof MultipleSelection) {
				switchToMultipleSelection();
			} else if (inspectorSelection instanceof UniqueSelection) {
				inspectObject(((UniqueSelection) inspectorSelection)
						.getInspectedObject());
			}
		}
	}

	/**
	 * Called to update inspector window when something has happened (for
	 * example if an inspection context has changed)
	 */
	public void updateInspectorWindow() {
		logger.info("OK, on remet a jour le bazar");
		if (currentInspectorView != null) {
			currentInspectorView.getController().update();
		}
	}

	private int lastInspectedTabIndex = -1;
	private FIBTabPanelView tabPanelView;

	@Override
	public void stateChanged(ChangeEvent e) {
		lastInspectedTabIndex = tabPanelView.getJComponent().getSelectedIndex();
		// System.out.println("Change for index "+lastInspectedTabIndex);
	}

	public void setVisible(boolean flag) {
		inspectorDialog.setVisible(flag);
	}
}
