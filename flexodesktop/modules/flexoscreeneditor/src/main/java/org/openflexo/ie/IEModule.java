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
package org.openflexo.ie;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.application.FlexoApplication;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalIEController;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * Interface Editor module
 * 
 * @author sguerin
 */
public class IEModule extends FlexoModule implements ExternalIEModule {

	private static final Logger logger = Logger.getLogger(IEModule.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.IE };
	private IEWOComponentView componentView;

	/**
	 * The 'main' method of module allow to launch this module as a single-module application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Start Interface Editor stand-alone.");
		}
		FlexoLoggingManager.initialize();
		FlexoApplication.initialize();
		ModuleLoader.initializeSingleModule(Module.IE_MODULE);
	}

	public IEModule(InteractiveFlexoEditor projectEditor) throws Exception {
		super(projectEditor);
		setFlexoController(new IEController(projectEditor, this));
		IEPreferences.init(getIEController());
		getProject().getFlexoWorkflow();
		// We retains all the resources of the workflow, since GUI operations
		// could
		// modify processes (eg NextPageEdges creating)
		/*
		 * retain(project.getFlexoWorkflow()); for (Enumeration
		 * e=project.getFlexoWorkflow().getAllFlexoProcesses().elements();
		 * e.hasMoreElements();) { retain((FlexoProcess)e.nextElement()); }
		 */
		retain(getProject().getFlexoComponentLibrary());
		retain(getProject().getFlexoNavigationMenu());

		getIEController().initWithEmptyPanel();

		getIEController().loadRelativeWindows();

	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public File getSmallButtonsDirectory() {
		return new FileResource(IECst.SMALL_BUTTONS_DIR);
	}

	public File getBigButtonsDirectory() {
		return new FileResource(IECst.BIG_BUTTONS_DIR);
	}

	public IEController getIEController() {
		return (IEController) getFlexoController();
	}

	// ==========================================================================
	// ========================== ExternalIEModule
	// ==============================
	// ==========================================================================

	@Override
	public void showScreenInterface(ComponentInstance component) {
		getIEController().setSelectedComponent(component);
	}

	@Override
	public void saveAll(boolean showConfirm) {
		save(showConfirm, true);
	}

	@Override
	public JComponent createViewForOperation(OperationNode operation) {
		if (!operation.hasWOComponent()) {
			return null;
		}
		if (componentView != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Previous component view was not properly disposed, please invoke finalizeScreenshot() before requesting a new component.");
			}
			finalizeScreenshot();
		}
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.setSize(1024, 768);
		IEWOComponentView answer = new IEWOComponentView(getIEController(), operation.getComponentInstance());
		if (operation.getTabComponent() != null) {
			answer.setSelectedTab(operation.getTabComponent());
		}
		pane.add(answer.dropZone, BorderLayout.CENTER);
		pane.validate();
		pane.doLayout();
		pane.repaint();
		answer.dropZone.propagateResize();
		componentView = answer;
		return pane;
	}

	/**
	 *
	 */
	@Override
	public void finalizeScreenshot() {
		if (componentView != null) {
			if (componentView.getParent() != null) {
				componentView.getParent().remove(componentView);
			}
			componentView.deleteModuleView();
		}
		componentView = null;
	}

	/**
	 * Overrides getIEExternalController
	 * 
	 * @see org.openflexo.module.external.ExternalIEModule#getIEExternalController()
	 */
	@Override
	public ExternalIEController getIEExternalController() {
		return getIEController();
	}

	/**
	 * Overrides getWOComponentView
	 * 
	 * @see org.openflexo.module.external.ExternalIEModule#getWOComponentView(org.openflexo.module.external.ExternalIEController,
	 *      org.openflexo.foundation.ie.IEWOComponent)
	 */
	@Override
	public JComponent getWOComponentView(ExternalIEController controller, IEWOComponent component) {
		if (componentView != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Previous component view was not properly disposed, please invoke finalizeScreenshot() before requesting a new component.");
			}
			finalizeScreenshot();
		}
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.setSize(1024, 768);
		IEWOComponentView compView = null;
		if (component instanceof IEReusableComponent) {
			compView = new IEReusableWidgetComponentView((IEController) controller, ((IEReusableComponent) component)
					.getComponentDefinition().getDummyComponentInstance());
		} else {
			compView = new IEWOComponentView((IEController) controller, component.getComponentDefinition().getDummyComponentInstance());
			compView.setPreferredSize(new Dimension(compView.getMaxWidth(), (compView).dropZone.getPreferredSize().height));
		}
		pane.add((compView).dropZone, BorderLayout.CENTER);
		pane.validate();
		pane.doLayout();
		pane.repaint();
		return pane;
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect()
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect() {
		return null;// getProject().getFlexoComponentLibrary();
	}

	/**
	 * Overrides moduleWillClose
	 * 
	 * @see org.openflexo.module.FlexoModule#moduleWillClose()
	 */
	@Override
	public void moduleWillClose() {
		super.moduleWillClose();
		IEPreferences.reset();
	}
}
