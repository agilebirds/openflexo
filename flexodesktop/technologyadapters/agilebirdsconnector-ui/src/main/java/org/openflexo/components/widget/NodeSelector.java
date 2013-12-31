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
package org.openflexo.components.widget;

import java.awt.Dimension;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.node.AbstractNode;

/**
 * Widget allowing to select an AbstractNode while browsing the workflow
 * 
 * @author sguerin
 * 
 */
public class NodeSelector<N extends AbstractNode> extends AbstractBrowserSelector<N> {

	protected static final String EMPTY_STRING = "";

	public NodeSelector(FlexoProject project, N process, Class<N> nodeClass) {
		super(project, process, nodeClass);
	}

	public NodeSelector(FlexoProject project, N process, int cols, Class<N> nodeClass) {
		super(project, process, nodeClass, cols);
	}

	FlexoWorkflow getWorkflow() {
		if (getProject() != null) {
			return getProject().getFlexoWorkflow();
		}
		return null;
	}

	@Override
	protected NodeSelectorPanel makeCustomPanel(AbstractNode editedObject) {
		return new NodeSelectorPanel();
	}

	@Override
	public String renderedString(AbstractNode editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class NodeSelectorPanel extends AbstractSelectorPanel<N> {
		protected NodeSelectorPanel() {
			super(NodeSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new WorkflowBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class WorkflowBrowser extends ProjectBrowser {

		protected WorkflowBrowser() {
			super(getWorkflow().getProject(), false);
			init();
		}

		@Override
		public void configure() {
			setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.MESSAGE_DEFINITION, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.HIDE, true);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getWorkflow();
		}
	}

}
