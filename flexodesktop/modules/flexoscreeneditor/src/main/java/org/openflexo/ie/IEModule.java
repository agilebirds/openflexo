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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.external.ExternalIEController;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.view.controller.FlexoController;

/**
 * Interface Editor module
 * 
 * @author sguerin
 */
public class IEModule extends FlexoModule implements ExternalIEModule {

	private static final Logger logger = Logger.getLogger(IEModule.class.getPackage().getName());
	private static final InspectorGroup[] inspectorGroups = new InspectorGroup[] { Inspectors.IE };
	private IEWOComponentView componentView;

	public IEModule(ApplicationContext applicationContext) throws Exception {
		super(applicationContext);
		IEPreferences.init();
		getIEController().initWithEmptyPanel();
		getIEController().loadRelativeWindows();
	}

	@Override
	public Module getModule() {
		return Module.IE_MODULE;
	}

	@Override
	public InspectorGroup[] getInspectorGroups() {
		return inspectorGroups;
	}

	public IEController getIEController() {
		return (IEController) getFlexoController();
	}

	@Override
	protected FlexoController createControllerForModule() {
		return new IEController(this);
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
		answer.dropZone.revalidate();
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
			componentView.delete();
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
			compView.setPreferredSize(new Dimension(compView.getMaxWidth(), compView.dropZone.getPreferredSize().height));
		}
		pane.add(compView.dropZone, BorderLayout.CENTER);
		pane.validate();
		pane.doLayout();
		pane.repaint();
		return pane;
	}

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect(FlexoProject)
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return null;// getProject().getFlexoComponentLibrary();
	}

}
