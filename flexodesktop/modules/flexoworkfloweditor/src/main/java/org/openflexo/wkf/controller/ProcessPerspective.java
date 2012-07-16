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

import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessView;

public class ProcessPerspective extends FlexoPerspective {
	static final Logger logger = Logger.getLogger(ProcessPerspective.class.getPackage().getName());

	private final WKFController _controller;

	private JComponent topRightDummy;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ProcessPerspective(WKFController controller) {
		super("process_edition");
		_controller = controller;
		topRightDummy = new JPanel();
		setTopLeftView(_controller.getWkfBrowserView());
		setBottomLeftView(_controller.getProcessBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentProcessView() != null) {
			return getCurrentProcessView().getController().getPaletteView();
		} else {
			return topRightDummy;
		}
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_BPEP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_BPEP_SELECTED_ICON;
	}

	@Override
	public FlexoProcess getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		} else if (_controller.getProject() != null) {
			return _controller.getProject().getRootFlexoProcess();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof FlexoProcess && !((FlexoProcess) object).isImported();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject process, FlexoController controller) {
		if (process instanceof FlexoProcess) {
			return new ProcessEditorController(_controller, (FlexoProcess) process).getDrawingView();
		} else {
			return null;
		}
	}

	public ProcessView getCurrentProcessView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof ProcessView) {
			return (ProcessView) _controller.getCurrentModuleView();
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
		// currentProcessView = (ProcessView) moduleView;
		if (moduleView instanceof ProcessView) {
			FlexoProcess process = ((ProcessView) moduleView).getRepresentedObject();
			_controller.getProcessBrowser().setRootObject(process);
			_controller.getExternalProcessBrowser().setRootObject(process);
			_controller.getWorkflowBrowser().focusOn(process);
			_controller.getSelectionManager().setSelectedObject(process);
		}
	}

}