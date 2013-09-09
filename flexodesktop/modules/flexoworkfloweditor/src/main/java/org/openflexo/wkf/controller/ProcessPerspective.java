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
import javax.swing.SwingUtilities;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.module.UserType;
import org.openflexo.swing.GlassPaneWrapper;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessView;
import org.openflexo.wkf.view.ImportedWorkflowView;
import org.openflexo.wkf.view.WKFProjectModuleView;

public class ProcessPerspective extends FlexoPerspective {
	static final Logger logger = Logger.getLogger(ProcessPerspective.class.getPackage().getName());

	private final WKFController _controller;

	private JComponent topRightDummy;

	private ImportedWorkflowView importedWorkflowView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ProcessPerspective(WKFController controller) {
		super("process_edition");
		_controller = controller;
		topRightDummy = new JPanel();
		importedWorkflowView = new ImportedWorkflowView(controller);
		setTopLeftView(_controller.getWkfBrowserView());
		if (!UserType.isLite()) {
			setBottomLeftView(_controller.getProcessBrowserView());
		}

	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentProcessView() != null) {
			if (getCurrentProcessView().getDrawing().isEditable()) {
				return getCurrentProcessView().getController().getPaletteView();
			} else {
				return new GlassPaneWrapper(getCurrentProcessView().getController().getPaletteView());
			}
		} else {
			return topRightDummy;
		}
	}

	@Override
	public JComponent getBottomRightView() {
		if (getCurrentProcessView() != null) {
			if (getCurrentProcessView().getDrawing().isEditable()) {
				return _controller.getDisconnectedDocInspectorPanel();
			} else {
				return new GlassPaneWrapper(_controller.getDisconnectedDocInspectorPanel());
			}
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

	@Override
	public FlexoObject getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		} else if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		} else if (proposedObject instanceof FlexoProjectObject && ((FlexoProjectObject) proposedObject).getProject() != null) {
			if (((FlexoProjectObject) proposedObject).getProject().getRootFlexoProcess() != null) {
				return ((FlexoProjectObject) proposedObject).getProject().getRootFlexoProcess();
			} else {
				return ((FlexoProjectObject) proposedObject).getProject().getWorkflow();
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof FlexoProcess || object instanceof FlexoWorkflow && !((FlexoWorkflow) object).isCache();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller, boolean editable) {
		if (object instanceof FlexoProcess) {
			ProcessView drawingView = new ProcessEditorController(_controller, (FlexoProcess) object).getDrawingView();
			drawingView.getDrawing().setEditable(editable);
			return drawingView;
		} else if (object instanceof FlexoWorkflow) {
			return new WKFProjectModuleView((FlexoWorkflow) object, _controller, this);
		} else {
			return null;
		}

	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		return createModuleViewForObject(object, controller, true);
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
	public void notifyModuleViewDisplayed(final ModuleView<?> moduleView) {
		// currentProcessView = (ProcessView) moduleView;
		if (moduleView instanceof ProcessView) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					FlexoProcess process = ((ProcessView) moduleView).getRepresentedObject();
					_controller.getProcessBrowser().setRootObject(process);
					_controller.getExternalProcessBrowser().setRootObject(process);
					_controller.getWorkflowBrowser().focusOn(process);
					_controller.getSelectionManager().setSelectedObject(process);
					importedWorkflowView.setSelected(process.getProcessNode());
				}
			});
		}
	}

	protected void updateMiddleLeftView() {
		if (_controller.getProject() != null && _controller.getProject().hasImportedProjects()) {
			setMiddleLeftView(importedWorkflowView);
		} else {
			setMiddleLeftView(null);
		}
	}

	public void setProject(FlexoProject project) {
		updateMiddleLeftView();
	}

}
