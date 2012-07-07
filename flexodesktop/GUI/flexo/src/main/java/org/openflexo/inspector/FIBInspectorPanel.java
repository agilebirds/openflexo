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
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.inspector.ModuleInspectorController.EmptySelectionActivated;
import org.openflexo.inspector.ModuleInspectorController.InspectedObjectChanged;
import org.openflexo.inspector.ModuleInspectorController.InspectorSwitching;
import org.openflexo.inspector.ModuleInspectorController.MultipleSelectionActivated;
import org.openflexo.inspector.ModuleInspectorController.NewInspectorsLoaded;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represent a JDialog showing inspector for the selection managed by an instance of ModuleInspectorController
 * 
 * @author sylvain
 * 
 */
public class FIBInspectorPanel extends JPanel implements Observer, ChangeListener {

	static final Logger logger = Logger.getLogger(FIBInspectorPanel.class.getPackage().getName());

	private final JPanel EMPTY_CONTENT;
	private final JPanel MULTIPLE_SELECTION_CONTENT;

	private final Map<FIBInspector, FIBView<?, ?>> inspectorViews;

	private final ModuleInspectorController inspectorController;

	private int lastInspectedTabIndex = -1;
	private FIBTabPanelView tabPanelView;

	public FIBInspectorPanel(ModuleInspectorController inspectorController) {

		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder());
		// setOpaque(false);

		this.inspectorController = inspectorController;

		inspectorController.addObserver(this);

		inspectorViews = new Hashtable<FIBInspector, FIBView<?, ?>>();

		resetViews();

		EMPTY_CONTENT = new JPanel(new BorderLayout());
		EMPTY_CONTENT.add(new JLabel("No selection", SwingConstants.CENTER), BorderLayout.CENTER);
		MULTIPLE_SELECTION_CONTENT = new JPanel(new BorderLayout());
		MULTIPLE_SELECTION_CONTENT.add(new JLabel("Multiple selection", SwingConstants.CENTER), BorderLayout.CENTER);

		switchToEmptyContent();

	}

	private void resetViews() {

		if (inspectorViews != null) {
			for (FIBView<?, ?> v : inspectorViews.values()) {
				FlexoLocalization.removeFromLocalizationListeners(v);
			}
			inspectorViews.clear();
		}

		/*for (Class<?> c : inspectorController.getInspectors().keySet()) {
			FIBInspector inspector = inspectorController.getInspectors().get(c);
			FIBView<?, ?> inspectorView = FIBController.makeView(inspector, FlexoLocalization.getMainLocalizer());
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Initialized view for inspector for " + inspector.getDataClass());
			}
		}*/

	}

	private FIBView<?, ?> buildViewFor(FIBInspector inspector) {

		FIBView<?, ?> inspectorView = FIBController.makeView(inspector, FlexoLocalization.getMainLocalizer());
		// TODO: See with Sylvain the purpose of the next line.
		// ((FIBInspectorController) inspectorView.getController()).setEditor(inspectorController.getFlexoController().getEditor());
		FlexoLocalization.addToLocalizationListeners(inspectorView);
		inspectorViews.put(inspector, inspectorView);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initialized view for inspector for " + inspector.getDataClass());
		}
		return inspectorView;
	}

	public void delete() {
		inspectorController.deleteObserver(this);
		for (FIBView<?, ?> v : inspectorViews.values()) {
			v.getController().delete();
			FlexoLocalization.removeFromLocalizationListeners(v);
		}
		if (tabPanelView != null) {
			tabPanelView.getController().delete();
		}
		inspectorViews.clear();
		tabPanelView = null;
	}

	private FIBView<?, ?> currentInspectorView = null;

	/**
	 * Returns boolean indicating if inspection change
	 * 
	 * @param object
	 * @return
	 */
	/*private boolean inspectObject(Object object, FIBInspector inspector, boolean updateEPTabs) {
		FIBInspector newInspector = 
		
		if (object == currentInspectedObject) {
			return false;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorController.inspectorForObject(object);

		if (newInspector == null) {
			logger.warning("No inspector for " + object);
			switchToEmptyContent();
		} else {
			if (newInspector != currentInspector) {
				switchToInspector(newInspector);
			}
			if (object instanceof FlexoModelObject) {
				updateEditionPatternReferences(newInspector, (FlexoModelObject) object);
			}
			currentInspectorView.getController().setDataObject(object);
		}

		return true;
	}*/

	private void updateEditionPatternReferences(FIBInspector inspector, FlexoModelObject object) {
		if (inspector.updateEditionPatternReferences(object)) {
			FIBView<?, ?> view = viewForInspector(inspector);
			FIBController controller = view.getController();
			FIBTabPanelView tabPanelView = (FIBTabPanelView) controller.viewForComponent(inspector.getTabPanel());
			tabPanelView.updateLayout();
		} else {
			// Nothing change: nice !!!
		}
	}

	private void switchToEmptyContent() {
		currentInspectorView = null;
		removeAll();
		add(EMPTY_CONTENT, BorderLayout.CENTER);
		validate();
		repaint();
	}

	private void switchToMultipleSelection() {
		currentInspectorView = null;
		removeAll();
		add(MULTIPLE_SELECTION_CONTENT, BorderLayout.CENTER);
		validate();
		repaint();
	}

	private void switchToInspector(FIBInspector newInspector, boolean updateEPTabs) {

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("switchToInspector " + newInspector + " for " + this);
		}

		if (updateEPTabs) {
			FIBView<?, ?> view = viewForInspector(newInspector);
			FIBController controller = view.getController();
			FIBTabPanelView tabPanelView = (FIBTabPanelView) controller.viewForComponent(newInspector.getTabPanel());
			tabPanelView.updateLayout();
		}

		if (tabPanelView != null) {
			tabPanelView.getJComponent().removeChangeListener(this);
			// System.out.println("removeChangeListener for "+tabPanelView.getJComponent());
		}

		// System.out.println("switchToInspector() "+newInspector);
		FIBView<?, ?> view = viewForInspector(newInspector);
		if (view != null) {
			currentInspectorView = view;
			removeAll();
			add(currentInspectorView.getResultingJComponent(), BorderLayout.CENTER);
			validate();
			repaint();
			// logger.info("reset title to "+newInspector.getParameter("title"));dsqqsd
			// inspectorDialog.setTitle(newInspector.getParameter("title"));
			tabPanelView = (FIBTabPanelView) currentInspectorView.getController().viewForComponent(newInspector.getTabPanel());
			if (lastInspectedTabIndex >= 0 && lastInspectedTabIndex < tabPanelView.getJComponent().getTabCount()) {
				tabPanelView.getJComponent().setSelectedIndex(lastInspectedTabIndex);
			}
			tabPanelView.getJComponent().addChangeListener(this);
			// System.out.println("addChangeListener for "+tabPanelView.getJComponent());
		} else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
		}
	}

	private void switchToObject(Object inspectedObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("switchToObject " + inspectedObject + " for " + this);
		}
		currentInspectorView.getController().setDataObject(inspectedObject);
	}

	private FIBView<?, ?> viewForInspector(FIBInspector inspector) {
		FIBView<?, ?> returned = inspectorViews.get(inspector);
		if (returned == null) {
			returned = buildViewFor(inspector);
		}
		return returned;
	}

	@Override
	public void update(Observable o, Object notification) {
		// logger.info("FIBInspectorController received: "+selection);
		if (notification instanceof NewInspectorsLoaded) {
			resetViews();
		}
		if (notification instanceof EmptySelectionActivated) {
			switchToEmptyContent();
		} else if (notification instanceof MultipleSelectionActivated) {
			switchToMultipleSelection();
		} else if (notification instanceof InspectorSwitching) {
			switchToInspector(((InspectorSwitching) notification).getNewInspector(), ((InspectorSwitching) notification).updateEPTabs());
		} else if (notification instanceof InspectedObjectChanged) {
			switchToObject(((InspectedObjectChanged) notification).getInspectedObject());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		lastInspectedTabIndex = tabPanelView.getJComponent().getSelectedIndex();
		// System.out.println("Change for index "+lastInspectedTabIndex);
	}

}
