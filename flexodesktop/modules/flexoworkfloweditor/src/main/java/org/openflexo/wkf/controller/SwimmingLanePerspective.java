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

import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneView;

public class SwimmingLanePerspective extends FlexoPerspective {

	private final WKFController _controller;

	private final Hashtable<FlexoProcess, SwimmingLaneEditorController> _controllerForProcess;

	private final Hashtable<SwimmingLaneEditorController, JSplitPane> _splitPaneForProcess;

	private JPanel topRightDummy;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public SwimmingLanePerspective(WKFController controller) {
		super("swimming_lanes");
		_controller = controller;
		_controllerForProcess = new Hashtable<FlexoProcess, SwimmingLaneEditorController>();
		_splitPaneForProcess = new Hashtable<SwimmingLaneEditorController, JSplitPane>();
		topRightDummy = new JPanel();
		setTopLeftView(_controller.getWkfBrowserView());
		setBottomLeftView(_controller.getProcessBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentProcessView() != null) {
			return getCurrentProcessView().getController().getPaletteView();
		}
		return topRightDummy;
	}

	public SwimmingLaneEditorController getControllerForProcess(FlexoProcess process) {
		SwimmingLaneEditorController returned = _controllerForProcess.get(process);
		if (returned == null) {
			returned = new SwimmingLaneEditorController(_controller, process);
			_controllerForProcess.put(process, returned);
		}
		return returned;
	}

	public void removeProcessController(SwimmingLaneEditorController controller) {
		_controllerForProcess.remove(controller.getDrawing().getFlexoProcess());
		_splitPaneForProcess.remove(controller);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_SWLP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_SWLP_SELECTED_ICON;
	}

	@Override
	public FlexoProcess getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		} else if (proposedObject != null) {
			return proposedObject.getProject().getRootFlexoProcess();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof FlexoProcess && !((FlexoProcess) object).isImported();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller, boolean editable) {
		if (object instanceof FlexoProcess) {
			SwimmingLaneView drawingView = getControllerForProcess((FlexoProcess) object).getDrawingView();
			drawingView.getDrawing().setEditable(editable);
			return drawingView;
		} else {
			return null;
		}
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject process, FlexoController controller) {
		return createModuleViewForObject(process, controller, true);
	}

	public SwimmingLaneView getCurrentProcessView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof SwimmingLaneView) {
			return (SwimmingLaneView) _controller.getCurrentModuleView();
		}
		return null;
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentProcessView() != null) {
			return getCurrentProcessView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		// currentProcessView = (SwimmingLaneView) moduleView;
		if (moduleView instanceof SwimmingLaneView) {
			FlexoProcess process = ((SwimmingLaneView) moduleView).getRepresentedObject();
			_controller.getProcessBrowser().setRootObject(process);
			_controller.getExternalProcessBrowser().setRootObject(process);
			_controller.getWorkflowBrowser().focusOn(process);
			_controller.getSelectionManager().setSelectedObject(process);
		}
	}

}