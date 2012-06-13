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
package org.openflexo.wkf.controller;

import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessView;

public class ProcessPerspective extends FlexoPerspective {
	static final Logger logger = Logger.getLogger(ProcessPerspective.class.getPackage().getName());

	private final WKFController _controller;

	private final Hashtable<FlexoProcess, ProcessEditorController> _controllerForProcess;
	private final Hashtable<ProcessEditorController, JSplitPane> _splitPaneForProcess;

	// private JSplitPane splitPaneWithWKFPalettesAndDocInspectorPanel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ProcessPerspective(WKFController controller) {
		super("process_edition");
		_controller = controller;
		_controllerForProcess = new Hashtable<FlexoProcess, ProcessEditorController>();
		_splitPaneForProcess = new Hashtable<ProcessEditorController, JSplitPane>();
	}

	public ProcessEditorController getControllerForProcess(FlexoProcess process) {
		ProcessEditorController returned = _controllerForProcess.get(process);
		if (returned == null) {
			returned = new ProcessEditorController(_controller, process);
			_controllerForProcess.put(process, returned);
		}
		return returned;
	}

	public void removeProcessController(ProcessEditorController controller) {
		_controllerForProcess.remove(controller.getDrawing().getFlexoProcess());
		_splitPaneForProcess.remove(controller);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_BPEP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_BPEP_SELECTED_ICON;
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public FlexoProcess getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		}
		return _controller.getProject().getRootFlexoProcess();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return (object instanceof FlexoProcess) && !((FlexoProcess) object).isImported();
	}

	@Override
	public ProcessView createModuleViewForObject(FlexoProcess process, FlexoController controller) {
		return getControllerForProcess(process).getDrawingView();
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return _controller.getWorkflowProcessBrowserViews();
	}

	@Override
	public boolean doesPerspectiveControlRightView() {
		return true;
	}

	@Override
	public JComponent getRightView() {
		if (getCurrentProcessView() == null) {
			return new JPanel();
		}
		return getSplitPaneWithWKFPalettesAndDocInspectorPanel();
	}

	public ProcessView getCurrentProcessView() {
		if ((_controller != null) && (_controller.getCurrentModuleView() instanceof ProcessView)) {
			return (ProcessView) _controller.getCurrentModuleView();
		}
		return null;
	}

	/**
	 * Return Split pane with Role palette and doc inspector panel Disconnect doc inspector panel from its actual parent
	 * 
	 * @return
	 */
	protected JSplitPane getSplitPaneWithWKFPalettesAndDocInspectorPanel() {
		JSplitPane splitPaneWithWKFPalettesAndDocInspectorPanel = _splitPaneForProcess.get(getCurrentProcessView().getController());
		if (splitPaneWithWKFPalettesAndDocInspectorPanel == null) {
			splitPaneWithWKFPalettesAndDocInspectorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getCurrentProcessView()
					.getController().getPaletteView(), _controller.getDisconnectedDocInspectorPanel());
			splitPaneWithWKFPalettesAndDocInspectorPanel.setResizeWeight(0);
			splitPaneWithWKFPalettesAndDocInspectorPanel.setDividerLocation(WKFCst.PALETTE_DOC_SPLIT_LOCATION);
			splitPaneWithWKFPalettesAndDocInspectorPanel.setBorder(BorderFactory.createEmptyBorder());
			_splitPaneForProcess.put(getCurrentProcessView().getController(), splitPaneWithWKFPalettesAndDocInspectorPanel);
		}
		if (splitPaneWithWKFPalettesAndDocInspectorPanel.getBottomComponent() == null) {
			splitPaneWithWKFPalettesAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
		}
		PropertyChangeListener[] listeners = splitPaneWithWKFPalettesAndDocInspectorPanel.getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners) {
			if (listener instanceof FlexoSplitPaneLocationSaver) {
				splitPaneWithWKFPalettesAndDocInspectorPanel.removePropertyChangeListener(listener);
			}
		}
		new FlexoSplitPaneLocationSaver(splitPaneWithWKFPalettesAndDocInspectorPanel, "WKFPaletteAndDocInspectorPanel");
		return splitPaneWithWKFPalettesAndDocInspectorPanel;
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentProcessView() != null) {
			return getCurrentProcessView().getController().getScalePanel();
		}
		return null;
	}

	// SGU: dynamic handling now
	// private ProcessView currentProcessView = null;

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		// currentProcessView = (ProcessView) moduleView;
		if (moduleView instanceof ProcessView) {
			FlexoProcess process = ((ProcessView) moduleView).getRepresentedObject();
			process.addObserver(_controller.getMainFrame());
			_controller.getProcessBrowser().setCurrentProcess(process);
			_controller.getExternalProcessBrowser().setCurrentProcess(process);
			_controller.getWorkflowBrowser().focusOn(process);
			_controller.getSelectionManager().setSelectedObject(process);
		}
	}

}